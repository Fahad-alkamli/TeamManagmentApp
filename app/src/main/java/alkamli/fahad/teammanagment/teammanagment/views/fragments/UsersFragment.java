package alkamli.fahad.teammanagment.teammanagment.views.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import javax.net.ssl.HttpsURLConnection;

import alkamli.fahad.teammanagment.teammanagment.CommonFunctions;
import alkamli.fahad.teammanagment.teammanagment.R;
import alkamli.fahad.teammanagment.teammanagment.views.fragments.adapters.project.ProjectAdapter2;
import alkamli.fahad.teammanagment.teammanagment.views.fragments.adapters.user.UsersAdapter;
import alkamli.fahad.teammanagment.teammanagment.http_client.HttpRequestClient;
import alkamli.fahad.teammanagment.teammanagment.http_client.HttpRequestClientResponse;
import alkamli.fahad.teammanagment.teammanagment.requests.project.AddMemberToProjectRequest;
import alkamli.fahad.teammanagment.teammanagment.requests.task.AssignTaskToUserRequest;
import alkamli.fahad.teammanagment.teammanagment.service.Service;
import alkamli.fahad.teammanagment.teammanagment.views.CreateUserActivity;

import static android.app.Activity.RESULT_OK;
import static android.util.Log.e;

public class UsersFragment extends Fragment implements Observer {

    final String TAG="Alkamli";
    static RecyclerView usersListView;
    static Fragment thisFrgment;
    SwipeRefreshLayout userRefresh;
    final int assignUserToProjectsRequest=1;
    final int assignUserToTaskRequest=2;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        thisFrgment=this;
        setHasOptionsMenu(true);
        Service.addUserObserver(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
       // Log.e(TAG,"User onCreateView");
        View view=inflater.inflate(R.layout.fragment_users, container, false);
        usersListView =(RecyclerView)view.findViewById(R.id.usersListView);
        userRefresh=(SwipeRefreshLayout)  view.findViewById(R.id.userRefresh);
        userRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Runnable run=new Runnable()
                {
                    @Override
                    public void run() {
                        Service.refreshUserList(getActivity());

                        if( getActivity() != null)
                        {
                            getActivity().runOnUiThread(new Runnable(){
                                @Override
                                public void run() {
                                    if(userRefresh != null )
                                    {
                                        userRefresh.setRefreshing(false);
                                    }
                                }
                            });
                        }
                    }
                };
                new Thread(run).start();

            }
        });
        // getItems();
        update(null,null);
        usersListView.setClickable(false);
        usersListView.setAdapter(new UsersAdapter(thisFrgment,Service.getUsernamesList()));
        usersListView.setLayoutManager(new LinearLayoutManager(getActivity()));
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
       // Log.e(TAG,"onDestroyView");
    }

    @Override
    public void update(Observable observable, Object o) {

        try {
            Log.i("Alkamli", "UserFragment Update has been called");

            Runnable run = new Runnable() {
                @Override
                public void run() {
                    getItems();
                }
            };
            if(getActivity()!= null)
            {
                new Thread(run).start();
            }

        } catch (Exception e) {
            Log.d("Alkamli", "UserFragment update: " + e.getMessage());
        }
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //Log.e(TAG,"User Fragment");
             try {
            switch (item.getItemId())
            {
                case R.id.add:
                    //Show the menu
                {
                    Log.d(TAG, "add user clicked");
                    Intent i = new Intent(this.getContext(), CreateUserActivity.class);
                    startActivity(i);
                    return true;
                }
            }
        }catch(Exception e) {
            class Local {};
            Log.e(CommonFunctions.TAG, ("MethodName: " + Local.class.getEnclosingMethod().getName() + " || ErrorMessage: " + e.getMessage()));
        }
        return super.onOptionsItemSelected(item);
    }

    private void getItems()
    {
        ArrayList<String> temp= Service.getUsernames();

        if(temp==null || usersListView==null)
        {
            Log.d("Alkamli", "getUsernames()==null");
            return ;
        }
        final UsersAdapter adapter = new UsersAdapter(thisFrgment,Service.getUsernamesList());
        if(getActivity() != null)
        {
            getActivity().runOnUiThread(new Runnable(){
                @Override
                public void run() {
                    usersListView.setAdapter(adapter);
                }
            });

        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
       // Log.e(TAG,"This is the UserFragment");
        if(resultCode==RESULT_OK)
        {
            switch(requestCode)
            {
                case assignUserToProjectsRequest:
                {
                    final ArrayList<String> projectIds= data.getStringArrayListExtra("projectIds");
                    final String userId=data.getStringExtra("userId");
                    if(projectIds == null || userId==null )
                    {
                        return;
                    }
                    Runnable run=new Runnable()
                    {
                        @Override
                        public void run() {
                            addProjectToUserRequest(projectIds,userId);
                        }
                    };

                    new Thread(run).start();
                    break;
                }
                case assignUserToTaskRequest:
                {
                    final ArrayList<Integer> temp=data.getIntegerArrayListExtra("ids");
                    final String user_id=data.getStringExtra("user_id");
                    if(temp ==null || user_id==null)
                    {
                        return;
                    }

                    new Thread(new Runnable()
                    {
                        @Override
                        public void run() {
                            assignUserToTaskRequest(temp,user_id);
                        }
                    }).start();
                    
                    break;
                }
            }

        }
    }





    private void addProjectToUserRequest(ArrayList<String> projectIds,String userId)
    {
        try{
            String session=  CommonFunctions.getSharedPreferences(getContext()).getString("session",null);
            if(session==null || userId==null || projectIds==null || projectIds.size()<1)
            {
                return;
            }
            AddMemberToProjectRequest request=new AddMemberToProjectRequest(session,userId,projectIds);


            final String jsonString=request.getJson(request);
            Log.d("Alkamli",jsonString);
            URL url;
            String response = "";

            url = new URL(getContext().getString(R.string.add_member_to_project_url));
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(15000);
            conn.setConnectTimeout(15000);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);

            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));
            writer.write(jsonString);

            writer.flush();
            writer.close();
            os.close();
            int responseCode=conn.getResponseCode();

            if (responseCode == HttpsURLConnection.HTTP_OK)
            {
                String line;
                BufferedReader br=new BufferedReader(new InputStreamReader(conn.getInputStream()));
                while ((line=br.readLine()) != null)
                {
                    response+=line;

                }


                Log.d("Alkamli","User has been added to the project/s Successful");
                CommonFunctions.sendToast(thisFrgment.getActivity(),"User has been added to the project/s Successful");
            }else if(responseCode == HttpsURLConnection.HTTP_UNAUTHORIZED)
            {
                //User doesn't have permission to make this request
                CommonFunctions.sendToast(thisFrgment.getActivity(),"Not Authorized");
                CommonFunctions.sessionExpiredHandler(thisFrgment.getActivity(),thisFrgment.getActivity());
            }
            else
            {
                String line;
                BufferedReader br=new BufferedReader(new InputStreamReader(conn.getInputStream()));
                while ((line=br.readLine()) != null)
                {
                    response+=line;
                }
                Log.d(TAG,response+" || "+Integer.toString(responseCode));
                //login is not successful
                if(response.toLowerCase().contains("enrolled"))
                {
                    CommonFunctions.sendToast(thisFrgment.getActivity(),response.trim());
                }else{
                    CommonFunctions.sendToast(thisFrgment.getActivity(),"User couldn't be added to a project/s ");
                }

            }
        }catch(Exception e)
        {
            class Local {};Log.e(TAG, ("MethodName: " + Local.class.getEnclosingMethod().getName() + " || ErrorMessage: " + e.getMessage()));
           // Log.e(TAG,"This one");
            e.printStackTrace();
            CommonFunctions.sendToast(thisFrgment.getActivity(),"User couldn't be added to a project/s ");
        }

    }


    private void assignUserToTaskRequest(ArrayList<Integer> taskIds, String userId)
    {
        String session=CommonFunctions.getSharedPreferences(getContext()).getString("session",null);
        if(session==null)
        {
            return;
        }
        AssignTaskToUserRequest request=new AssignTaskToUserRequest(session,Integer.parseInt(userId),taskIds);
        Log.d(TAG,request.getJson(request));
        HttpRequestClientResponse response=new HttpRequestClient(getContext().getString(R.string.assign_task_to_user_url),request.getJson(request)).post();
        switch(response.getHttpStatus())
        {

            case HttpURLConnection.HTTP_OK:
            {
                CommonFunctions.sendToast(getActivity(),"User has been assigned to the task/s");
                break;
            }
            case HttpURLConnection.HTTP_UNAUTHORIZED:
            {
                CommonFunctions.sessionExpiredHandler(getContext(),getActivity());
                break;
            }
            default:
            {
                  if(CommonFunctions.clean(response.getResponseString()).length()>0)
                {
                    Log.e(TAG,response.getResponseString());
                    CommonFunctions.sendToast(getActivity(),response.getResponseString());
                }else{
                      CommonFunctions.sendToast(getActivity(),"We couldn't add the user to  task/s");
                  }
            }
        }

    }

}
