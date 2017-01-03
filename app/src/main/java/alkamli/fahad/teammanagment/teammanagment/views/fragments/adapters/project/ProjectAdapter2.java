package alkamli.fahad.teammanagment.teammanagment.views.fragments.adapters.project;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.*;
import android.view.*;
import android.widget.*;
import java.io.*;
import java.net.*;
import java.util.List;
import alkamli.fahad.teammanagment.teammanagment.CommonFunctions;
import alkamli.fahad.teammanagment.teammanagment.R;
import alkamli.fahad.teammanagment.teammanagment.http_client.HttpRequestClient;
import alkamli.fahad.teammanagment.teammanagment.http_client.HttpRequestClientResponse;
import alkamli.fahad.teammanagment.teammanagment.requests.project.*;
import alkamli.fahad.teammanagment.teammanagment.service.Service;
import alkamli.fahad.teammanagment.teammanagment.views.*;
import entity.Project;
import com.fasterxml.jackson.databind.ObjectMapper;
import javax.net.ssl.HttpsURLConnection;
import static alkamli.fahad.teammanagment.teammanagment.CommonFunctions.TAG;

public class ProjectAdapter2 extends RecyclerView.Adapter<ProjectAdapter2.MyViewHolder>
{

    /*
    http://www.androidhive.info/2016/01/android-working-with-recycler-view/
    http://stackoverflow.com/questions/37507937/margin-between-items-in-recycler-view-android
     */
    private List<Project> projectList;
    private Context context;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView nickname, startDate, endDate;
        public Button   projectEnabledState ;

        public View view;

        public MyViewHolder(View view) {
            super(view);
            nickname = (TextView) view.findViewById(R.id.nickname);
            startDate = (TextView) view.findViewById(R.id.startDate);
            endDate = (TextView) view.findViewById(R.id.endDate);
            projectEnabledState = (Button) view.findViewById(R.id.projectEnabledState);
            this.view=view;

        }
    }


    public ProjectAdapter2(Context context,List<Project> projectList)
    {
        this.projectList = projectList;
        this.context=context;

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View CustomView = inflater.inflate(R.layout.project_list_element, parent, false);

        this.context=parent.getContext();

        return new MyViewHolder(CustomView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        if(projectList==null)
        {
            return;
        }
        Project project = projectList.get(position);
        holder.nickname.setText(project.getProjectName());
        holder.startDate.setText(project.getStartDate());
        holder.endDate.setText(project.getEndDate());
        if(project.isEnabledState() && context!=null)
        {
            holder.projectEnabledState.setBackground(ContextCompat.getDrawable(context, R.drawable.element_is_enabled));
        }else if( context!=null)
        {
            holder.projectEnabledState.setBackground(ContextCompat.getDrawable(context, R.drawable.element_not_enabled));
        }
        //set the project id
        holder.view.setTag(project.getProjectID());


        if (CommonFunctions.getSharedPreferences(context).getBoolean("admin", false)) {
            holder.view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final String projectId = Integer.toString(((int) view.getTag()));
                    Log.d("Alkamli", "The project id is " + projectId);
                    PopupMenu popupMenu = new PopupMenu(context, holder.endDate);
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem menuItem) {
                            switch (menuItem.getItemId()) {
                                case R.id.enableProject:
                                    //First we check if this project is enabled already or not
                                {
                                    Project currentProject = findProjectById(projectId);
                                    if (currentProject != null && currentProject.isEnabledState())
                                    {
                                        //The project is already enalbed
                                        Toast.makeText(context, R.string.This_project_is_already_Enabled, Toast.LENGTH_LONG).show();

                                    } else {
                                        //We send a request to process the operation enable a project
                                        Runnable run = new Runnable() {
                                            @Override
                                            public void run() {
                                                sendEnableProjectRequest(context, projectId);
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
                                        Toast.makeText(context, R.string.This_project_is_already_Disabled, Toast.LENGTH_LONG).show();
                                    } else {
                                        //We send a request to process the operation
                                        //We send a request to process the operation diable a project
                                        Runnable run = new Runnable() {
                                            @Override
                                            public void run() {
                                                sendDisableProjectRequest(context, projectId);
                                            }
                                        };
                                        new Thread(run).start();
                                    }
                                }
                                break;
                                case R.id.showMembers:
                                {
                                    Intent i = new Intent(context, DisplayProjectMembersActivity.class);
                                    i.putExtra("projectId", projectId);
                                    context.startActivity(i);
                                }
                                break;
                                case R.id.updateDetails:
                                {
                                    Intent i = new Intent(context, CreateProjectActivity.class);
                                    i.putExtra("projectId", projectId);
                                    context.startActivity(i);
                                }break;
                                case R.id.deleteProject:
                                {
                                    new Thread(new Runnable(){
                                        @Override
                                        public void run()
                                        {
                                            deleteProjectRequest(context,projectId);
                                        }
                                    }).start();

                                }break;
                            }

                            return true;
                        }
                    });
                    popupMenu.inflate(R.menu.popup_menu_projects_admin_options);
                    popupMenu.show();
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        if(projectList==null)
        {
            return 0;
        }
        return projectList.size();
    }


    private Project findProjectById(String id)
    {
        if(projectList ==null)
        {
            return null;
        }
        for(Project temp:projectList)
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



        HttpRequestClient client=new HttpRequestClient(context.getString(R.string.enable_project_url),request.getJson(request));
        Log.d("Alkamli",request.getJson(request));

        HttpRequestClientResponse response =client.post();
        try {


            if (response.getHttpStatus() == HttpsURLConnection.HTTP_OK)
            {
                //First we make sure the response is a session
                if(CommonFunctions.clean(response.getResponseString()).length()<0)
                {
                    return;
                }
                //Create project is successful
                //Add the project to the list from the response
                ObjectMapper objectMapper = new ObjectMapper();
                Project project = objectMapper.readValue(response.getResponseString(), Project.class);
                Service.addProjectToList(project);
                Log.d("Alkamli","project has been enabled Successfully");
            }else if(response.getHttpStatus() == HttpsURLConnection.HTTP_UNAUTHORIZED)
            {

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
        Log.d("Alkamli",request.getJson(request));
        HttpRequestClient client=new HttpRequestClient(context.getString(R.string.disable_project_url),request.getJson(request));
            HttpRequestClientResponse response =client.post();
            try {


                if (response.getHttpStatus() == HttpsURLConnection.HTTP_OK)
                {
                    //First we make sure the response is a session
                    if(CommonFunctions.clean(response.getResponseString()).length()<0)
                    {
                        return;
                    }
                    //Create project is successful
                    //Add the project to the list from the response
                    ObjectMapper objectMapper = new ObjectMapper();
                    Project project = objectMapper.readValue(response.getResponseString(), Project.class);
                    Service.addProjectToList(project);
                    Log.d("Alkamli","project has been enabled Successfully");
                }else if(response.getHttpStatus() == HttpsURLConnection.HTTP_UNAUTHORIZED)
                {
                }

            } catch (Exception e) {

                class Local {};
                Log.e(TAG, ("MethodName: " + Local.class.getEnclosingMethod().getName() + " || ErrorMessage: " + e.getMessage()));
            }
    }



    private void deleteProjectRequest(Context context, String projectId)
    {
        String session=CommonFunctions.getSharedPreferences(context).getString("session",null);
        if(session == null)
        {
            return;
        }
        DeleteProjectRequest request=new DeleteProjectRequest(session,projectId);
        final String jsonString=request.getJson(request);
        Log.d("Alkamli",jsonString);
        URL url;
        String response = "";
        try {
            url = new URL(context.getString(R.string.delete_project_url));
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
                Activity activity= HomeActivity.getActivity();
                if(activity!=null)
                {
                    Service.deletProjectFromList(projectId);
                    CommonFunctions.sendToast(activity,activity.getString(R.string.Project_has_been_deleted));
                }
                Log.d("Alkamli","project has been deleted Successfully");
            }else if(responseCode == HttpsURLConnection.HTTP_UNAUTHORIZED)
            {
                Activity activity= HomeActivity.getActivity();
                if(activity!=null)
                {
                    CommonFunctions.sendToast(activity,activity.getString(R.string.Could_not_delete_a_project));
                }
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