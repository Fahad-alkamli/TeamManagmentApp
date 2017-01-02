package alkamli.fahad.teammanagment.teammanagment.views;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import alkamli.fahad.teammanagment.teammanagment.CommonFunctions;
import alkamli.fahad.teammanagment.teammanagment.R;
import alkamli.fahad.teammanagment.teammanagment.http_client.HttpRequestClient;
import alkamli.fahad.teammanagment.teammanagment.http_client.HttpRequestClientResponse;
import alkamli.fahad.teammanagment.teammanagment.requests.task.GetTaskMembersRequest;
import alkamli.fahad.teammanagment.teammanagment.requests.task.RemoveUserFromTaskRequest;
import alkamli.fahad.teammanagment.teammanagment.views.activites_adapters.DisplayProjectMembersActivityAdapter;
import entity.User;

import static alkamli.fahad.teammanagment.teammanagment.CommonFunctions.TAG;

public class DisplayTaskMembersActivity extends AppCompatActivity {

    Activity activity;
    int id;
    DisplayProjectMembersActivityAdapter adapter=null;
    ArrayList<String> removeUsersList=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(!CommonFunctions.checkForValidLoginSession(this))
        {
            CommonFunctions.sendToast(this,"Please login again.");
            Intent i=new Intent(this,LoginActivity.class);
            startActivity(i);
            finish();
            return;
        }
        if(getIntent().getIntExtra("task_id",-1)==-1)
        {
        finish();
            return;
        }
        id=getIntent().getIntExtra("task_id",-1);
        setContentView(R.layout.activity_display_task_members);
        activity=this;

        new Thread(new Runnable()
        {
            @Override
            public void run() {
                taskMembers(id);
            }
        }).start();

    }


    public void removeUsers(View view)
    {
        Log.d(TAG,"Remove user");
        if(adapter != null)
        {
            removeUsersList= adapter.getUserIdList();
            if(removeUsersList != null && removeUsersList.size()>0)
            {
                //Construct the request and send it

                Runnable run=new Runnable()
                {
                    @Override
                    public void run()
                    {
                        RemoveUserRequest();

                    }
                };
                new Thread(run).start();
            }
        }
    }

    private void taskMembers(int taskId)
    {
        try {
            String session = CommonFunctions.getSharedPreferences(activity).getString("session", null);
            if (session == null) {
                return;
            }
            GetTaskMembersRequest request = new GetTaskMembersRequest(Integer.toString(taskId), session);
            HttpRequestClient client = new HttpRequestClient(activity.getString(R.string.get_task_members_url), request.getJson(request));
            HttpRequestClientResponse response = client.post();
            switch (response.getHttpStatus()) {
                case HttpsURLConnection.HTTP_OK: {
                    if (activity != null) {

                        ObjectMapper objectMapper = new ObjectMapper();
                        // TypeReference<List<User>> mapType = new TypeReference<List<User>>() {};
                        TypeReference<List<User>> mapType = new TypeReference<List<User>>() {
                        };
                        final List<User> jsonToUserList = objectMapper.readValue(response.getResponseString(), mapType);

                        final ArrayList<User> userList = new ArrayList<User>();
                        userList.addAll(jsonToUserList);

                        if (jsonToUserList == null || jsonToUserList.size() < 1) {
                            return;
                        }
                        //Now i need to set the adapter

                        Runnable run = new Runnable() {
                            @Override
                            public void run() {
                                ListView userView = (ListView) activity.findViewById(R.id.usersListView);
                                adapter = new DisplayProjectMembersActivityAdapter(activity, getNames(jsonToUserList), userList);
                                userView.setAdapter(adapter);
                            }
                        };
                        activity.runOnUiThread(run);

                    }
                    Log.d(CommonFunctions.TAG, "Task has been deleted");
                    break;
                }
                case HttpURLConnection.HTTP_UNAUTHORIZED: {
                    CommonFunctions.sessionExpiredHandler(activity, activity);
                    break;
                }

            }
        }catch(Exception e)
        {
            class Local {};
            Log.e(TAG, ("MethodName: " + Local.class.getEnclosingMethod().getName() + " || ErrorMessage: " + e.getMessage()));
        }

    }

    private ArrayList<String> getNames(List<User> users)
    {
        ArrayList<String> returnValue=new ArrayList<String>();
        for(User temp:users)
        {
            returnValue.add(temp.getName());
        }
        return returnValue;
    }

   private ArrayList<Integer> convertToInts(ArrayList<String> values)
   {
       try {
           ArrayList<Integer> temp = new ArrayList<>();
           for (String temp2 : values)
           {
               temp.add(Integer.parseInt(temp2));
           }
           return temp;
       }catch(Exception e)
       {
           class Local {};
           Log.e(TAG, ("MethodName: " + Local.class.getEnclosingMethod().getName() + " || ErrorMessage: " + e.getMessage()));
       }
       return null;
   }


    private void RemoveUserRequest()
    {
        try {
            String session = CommonFunctions.getSharedPreferences(activity).getString("session", null);
            if (session == null) {
                return;
            }

            RemoveUserFromTaskRequest request = new RemoveUserFromTaskRequest(session,id,convertToInts(removeUsersList));
            HttpRequestClient client = new HttpRequestClient(activity.getString(R.string.remove_task_from_user_url), request.getJson(request));
            HttpRequestClientResponse response = client.post();
            switch (response.getHttpStatus())
            {
                case HttpsURLConnection.HTTP_OK: {
                    if (activity != null)
                    {

                        CommonFunctions.sendToast(activity,getString(R.string.Users_has_been_removed_from_a_task));
                    }
                    break;
                }
                case HttpURLConnection.HTTP_UNAUTHORIZED: {
                    CommonFunctions.sessionExpiredHandler(activity, activity);
                    break;
                }
                default:
                {
                    CommonFunctions.sendToast(activity,getString(R.string.users_could_not_been_removed_from_a_task));
                }

            }
        }catch(Exception e)
        {
            class Local {};
            Log.e(TAG, ("MethodName: " + Local.class.getEnclosingMethod().getName() + " || ErrorMessage: " + e.getMessage()));
        }
        activity.finish();
    }

}
