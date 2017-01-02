package alkamli.fahad.teammanagment.teammanagment.views;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.*;
import java.net.*;
import java.util.*;
import javax.net.ssl.HttpsURLConnection;

import alkamli.fahad.teammanagment.teammanagment.CommonFunctions;
import alkamli.fahad.teammanagment.teammanagment.R;
import alkamli.fahad.teammanagment.teammanagment.requests.project.RemoveUserFromProject;
import alkamli.fahad.teammanagment.teammanagment.requests.project.SessionAndProjectIdRequest;
import alkamli.fahad.teammanagment.teammanagment.views.activites_adapters.DisplayProjectMembersActivityAdapter;
import entity.User;

import static alkamli.fahad.teammanagment.teammanagment.CommonFunctions.TAG;

public class DisplayProjectMembersActivity extends AppCompatActivity {

    String projectId;
    Activity activity;
    ArrayList<String> removeUsersList=null;
    DisplayProjectMembersActivityAdapter adapter=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(!CommonFunctions.checkForValidLoginSession(this))
        {
            CommonFunctions.sendToast(this,"Please login again.");
            Intent i=new Intent(this,LoginActivity.class);
            startActivity(i);
            finish();

        }
        setContentView(R.layout.activity_display_project_members);

        projectId=getIntent().getStringExtra("projectId");
        if(projectId==null)
        {
            //finish this one and go to home
            Intent i=new Intent(this,HomeActivity.class);
            startActivity(i);
            finish();
            return;
        }
        activity=this;

        Runnable run=new Runnable()
        {
            @Override
            public void run() {
                getUsersThatBelongsToThisProjectRequest(activity,projectId);
            }
        };
       new Thread(run).start();

      //  Log.e(CommonFunctions.TAG,"DisplayProjectMembersActivity");
    }


    private void getUsersThatBelongsToThisProjectRequest(final Context context, String projectId)
    {
        String session= CommonFunctions.getSharedPreferences(context).getString("session",null);
        if(session == null)
        {
            return;
        }
        SessionAndProjectIdRequest request=new SessionAndProjectIdRequest(session,projectId);


        final String jsonString=request.getJson(request);
        Log.d("Alkamli",jsonString);
        URL url;
        String response = "";
        try {
            url = new URL(context.getString(R.string.get_project_users_url));
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(15000);
            conn.setConnectTimeout(15000);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);
            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
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
                //First we make sure the response is a session
                if(CommonFunctions.clean(response).length()<0)
                {
                    return;
                }
                //Log.d(TAG,response);
                ObjectMapper objectMapper = new ObjectMapper();
               // TypeReference<List<User>> mapType = new TypeReference<List<User>>() {};
                TypeReference<List<User>> mapType = new TypeReference<List<User>>() {};
               final List<User> jsonToUserList = objectMapper.readValue(response, mapType);

                final ArrayList<User> userList=new ArrayList<User>();
                userList.addAll(jsonToUserList);

                if(jsonToUserList == null || jsonToUserList.size()<1)
                {
                    return;
                }
                //Now i need to set the adapter

                Runnable run=new Runnable()
                {
                    @Override
                    public void run()
                    {
                        ListView userView=(ListView)activity.findViewById(R.id.usersListView);
                         adapter=new DisplayProjectMembersActivityAdapter(context,getNames(jsonToUserList),userList);
                        userView.setAdapter(adapter);
                    }
                };
                activity.runOnUiThread(run);



                Log.d("Alkamli","project has been enabled Successfully");
            }else if(responseCode == HttpsURLConnection.HTTP_UNAUTHORIZED)
            {

            }
            else {
                response="";
                Log.d("Alkamli",Integer.toString(responseCode));
                //login is not successful
                Intent i=new Intent(context,HomeActivity.class);
                context.startActivity(i);
                finish();
            }
        } catch (Exception e) {

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

    private void RemoveUserRequest()
    {  try{
        String session=  CommonFunctions.getSharedPreferences(getApplicationContext()).getString("session",null);
        if(session==null)
        {
            return;
        }
        RemoveUserFromProject request=new RemoveUserFromProject(session,projectId,removeUsersList);


        final String jsonString=request.getJson(request);
        Log.d("Alkamli",jsonString);
        URL url;
        String response = "";

        url = new URL(getApplicationContext().getString(R.string.remove_users_from_project_url));
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


            Log.d("Alkamli","User/s has been removed from the project Successful");
            CommonFunctions.sendToast(activity,getString(R.string.user_has_been_removed_from_the_project_successful));
            Intent i=new Intent(this,HomeActivity.class);
            startActivity(i);
            finish();
        }else if(responseCode == HttpsURLConnection.HTTP_UNAUTHORIZED)
        {
            //User doesn't have permission to make this request
            CommonFunctions.sendToast(activity,getString(R.string.not_authorized));
            CommonFunctions.sessionExpiredHandler(activity,activity);
            finish();
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
            if(response.length()>0)
            {
                CommonFunctions.sendToast(activity,response.trim());
            }else{
                CommonFunctions.sendToast(activity,getString(R.string.Users_could_not_be_removed_from_the_project));
            }

        }
    }catch(Exception e)
    {
        class Local {};Log.e(TAG, ("MethodName: " + Local.class.getEnclosingMethod().getName() + " || ErrorMessage: " + e.getMessage()));
        //Log.e(TAG,"This one");
        //e.printStackTrace();
        CommonFunctions.sendToast(activity,getString(R.string.Users_could_not_be_removed_from_the_project));
    }

        //stop the current activity and launch the home activity
        Intent i=new Intent(getApplicationContext(),HomeActivity.class);
        activity.startActivity(i);
        activity.finish();


    }


}
