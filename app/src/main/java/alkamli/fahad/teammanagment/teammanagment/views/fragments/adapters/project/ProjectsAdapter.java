package alkamli.fahad.teammanagment.teammanagment.views.fragments.adapters.project;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

import alkamli.fahad.teammanagment.teammanagment.CommonFunctions;
import alkamli.fahad.teammanagment.teammanagment.R;
import alkamli.fahad.teammanagment.teammanagment.requests.project.SessionAndProjectIdRequest;
import alkamli.fahad.teammanagment.teammanagment.service.Service;
import alkamli.fahad.teammanagment.teammanagment.views.DisplayProjectMembersActivity;
import entity.Project;

import static alkamli.fahad.teammanagment.teammanagment.CommonFunctions.TAG;
import static android.util.Log.e;


public class ProjectsAdapter extends ArrayAdapter<String> {


    ArrayList<Project> ids;
    int count=0;

    public ProjectsAdapter(Context context, ArrayList<String> names,ArrayList<Project> ids)
    {
        super(context, R.layout.project_list_element,names);
        this.ids=ids;
    }


    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            View CustomView = inflater.inflate(R.layout.project_list_element, parent, false);
           try {
               TextView nickname = (TextView) CustomView.findViewById(R.id.nickname);
               TextView startDate=(TextView) CustomView.findViewById(R.id.startDate);
               TextView endDate=(TextView) CustomView.findViewById(R.id.endDate);
               Button projectEnabledState = (Button) CustomView.findViewById(R.id.projectEnabledState);

               startDate.setText(ids.get(count).getStartDate());
               endDate.setText(ids.get(count).getEndDate());
               if (ids.get(count).isEnabledState()) {
                   projectEnabledState.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.element_is_enabled));
               }
               nickname.setText(getItem(position));
              //  Log.e("Alkamli",Integer.toString(ids.get(count).getProjectID()));
               nickname.setTag(Integer.toString(ids.get(count).getProjectID()));


               if (CommonFunctions.getSharedPreferences(getContext()).getBoolean("admin", false)) {
                   nickname.setOnClickListener(new View.OnClickListener() {
                       @Override
                       public void onClick(View view) {
                           final String projectId = (String) view.getTag();
                           Log.d("Alkamli", "The project id is " + projectId);
                           PopupMenu popupMenu = new PopupMenu(getContext(), view);
                           popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                               @Override
                               public boolean onMenuItemClick(MenuItem menuItem) {
                                   switch (menuItem.getItemId()) {
                                       case R.id.enableProject:
                                           //First we check if this project is enabled already or not
                                       {
                                           Project currentProject = findProjectById(projectId);
                                           if (currentProject != null && currentProject.isEnabledState()) {
                                               //The project is already enalbed
                                               Toast.makeText(getContext(), "This project is already Enabled", Toast.LENGTH_LONG).show();

                                           } else {
                                               //We send a request to process the operation enable a project
                                               Runnable run = new Runnable() {
                                                   @Override
                                                   public void run() {
                                                       sendEnableProjectRequest(getContext(), projectId);
                                                   }
                                               };
                                               new Thread(run).start();
                                           }
                                       }
                                       break;
                                       case R.id.disableProject: {
                                           Project currentProject = findProjectById(projectId);
                                           if (currentProject != null && currentProject.isEnabledState() == false) {
                                               //The project is already disabled
                                               Toast.makeText(getContext(), "This project is already Disabled", Toast.LENGTH_LONG).show();
                                           } else {
                                               //We send a request to process the operation
                                               //We send a request to process the operation diable a project
                                               Runnable run = new Runnable() {
                                                   @Override
                                                   public void run() {
                                                       sendDisableProjectRequest(getContext(), projectId);
                                                   }
                                               };
                                               new Thread(run).start();
                                           }
                                       }
                                       break;
                                       case R.id.showMembers: {
                                           Intent i = new Intent(getContext(), DisplayProjectMembersActivity.class);
                                           i.putExtra("projectId", projectId);
                                           getContext().startActivity(i);
                                       }
                                       break;
                                   }

                                   return true;
                               }
                           });
                           popupMenu.inflate(R.menu.popup_menu_projects_admin_options);
                           popupMenu.show();
                       }
                   });
               }
               count += 1;

               if (count >= ids.size()) {
                   count = 0;
                   //Log.d("Alkamli", "Has been rest");

               }

           }catch(Exception e)
           {
               class Local {};
               Log.e(TAG, ("MethodName: " + Local.class.getEnclosingMethod().getName() + " || ErrorMessage: " + e.getMessage()));

           }
        return CustomView;
    }



    private Project findProjectById(String id)
    {
        if(ids ==null)
        {
           return null;
        }
            for(Project temp:ids)
            {
                if(temp.getProjectID()==Integer.parseInt(id))
                {
                    return temp;
                }
            }

        return null;
    }



    private void sendEnableProjectRequest(Context context,String projectId)
    {
        String session=CommonFunctions.getSharedPreferences(context).getString("session",null);
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
            url = new URL(context.getString(R.string.enable_project_url));
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
                //Create project is successful
                //Add the project to the list from the response
                ObjectMapper objectMapper = new ObjectMapper();
                Project project = objectMapper.readValue(response, Project.class);
                Service.addProjectToList(project);
                Log.d("Alkamli","project has been enabled Successfully");
            }else if(responseCode == HttpsURLConnection.HTTP_UNAUTHORIZED)
            {

            }
            else {
                response="";
                Log.d("Alkamli",Integer.toString(responseCode));
                //login is not successful



            }
        } catch (Exception e) {

            class Local {};
            Log.e(TAG, ("MethodName: " + Local.class.getEnclosingMethod().getName() + " || ErrorMessage: " + e.getMessage()));
        }
    }


    private void sendDisableProjectRequest(Context context,String projectId)
    {
        String session=CommonFunctions.getSharedPreferences(context).getString("session",null);
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
            url = new URL(context.getString(R.string.disable_project_url));
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
                //Create project is successful
                //Add the project to the list from the response
                ObjectMapper objectMapper = new ObjectMapper();
                Project project = objectMapper.readValue(response, Project.class);
                Service.addProjectToList(project);
                Log.d("Alkamli","project has been enabled Successfully");
            }else if(responseCode == HttpsURLConnection.HTTP_UNAUTHORIZED)
            {

            }
            else {
                response="";
                Log.d("Alkamli",Integer.toString(responseCode));
                //login is not successful



            }
        } catch (Exception e) {

            class Local {};
            Log.e(TAG, ("MethodName: " + Local.class.getEnclosingMethod().getName() + " || ErrorMessage: " + e.getMessage()));
        }
    }

}
