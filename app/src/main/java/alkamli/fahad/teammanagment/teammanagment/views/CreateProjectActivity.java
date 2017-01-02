package alkamli.fahad.teammanagment.teammanagment.views;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HttpsURLConnection;

import alkamli.fahad.teammanagment.teammanagment.CommonFunctions;
import alkamli.fahad.teammanagment.teammanagment.R;
import alkamli.fahad.teammanagment.teammanagment.requests.project.CreateProjectRequest;
import alkamli.fahad.teammanagment.teammanagment.requests.project.UpdateProjectRequest;
import alkamli.fahad.teammanagment.teammanagment.service.Service;
import entity.Project;

import static alkamli.fahad.teammanagment.teammanagment.CommonFunctions.TAG;
import static android.util.Log.e;

public class CreateProjectActivity extends AppCompatActivity {

    EditText projectName;
    EditText startDate;
    EditText endDate;
    CheckBox enableCheckBox;
    String session;
    View content;
    View progressBar;
    Activity activity;
    String projectId=null;
    Project project=null;
    Button createProjectButton=null;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if(!CommonFunctions.checkForValidLoginSession(this))
        {
            CommonFunctions.sendToast(this,"Please login again.");
            Intent i=new Intent(this,LoginActivity.class);
            startActivity(i);
            finish();

        }
        setContentView(R.layout.activity_create_project);
        activity=this;
        projectName=(EditText) findViewById(R.id.projectName);
        startDate=(EditText) findViewById(R.id.startDate);
        endDate=(EditText) findViewById(R.id.endDate);
        enableCheckBox=(CheckBox) findViewById(R.id.enableCheckBox);
        content= findViewById(R.id.contentLayout);
        progressBar=findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);
        createProjectButton=(Button) findViewById(R.id.createProject);
        //
        if( getIntent().getExtras() != null && getIntent().getExtras().getString("projectId") != null)
        {

            projectId=getIntent().getExtras().getString("projectId");
            createProjectButton.setText(getString(R.string.update_project));
            fillProjectInfo();
        }

    }

    public void createProject(View view)
    {
        boolean go=true;
        //validation
        if(CommonFunctions.clean(projectName.getText().toString()).equals("") ||CommonFunctions.clean(projectName.getText().toString()).length()<=0 )
        {
            //empty
            projectName.setError(getString(R.string.error_field_required));
            go=false;
        }
        if(CommonFunctions.clean(startDate.getText().toString()).equals("")  ||CommonFunctions.clean(startDate.getText().toString()).length()<=0 )
        {
            //empty
            startDate.setError(getString(R.string.error_field_required));
            go=false;
        }
        if(CommonFunctions.clean(endDate.getText().toString()).equals("")  ||CommonFunctions.clean(endDate.getText().toString()).length()<=0 )
        {
            //empty
            endDate.setError(getString(R.string.error_field_required));
            go=false;
        }
        //Valid is done
        if(go==false)
        {
            Log.d("Alkamli","Should of stopped");
            return;
        }

        Pattern r = Pattern.compile(CommonFunctions.datePattern);
        Matcher m = r.matcher(startDate.getText().toString());
        //not a valid start date format
        if (!m.find())
        {
            startDate.setError(getString(R.string.not_valid_date_message));
            go=false;
        }
        m = r.matcher(endDate.getText().toString());
        //not a valid end date format
        if (!m.find())
        {
            endDate.setError(getString(R.string.not_valid_date_message));
            go=false;
        }
        //Valid is done
        if( !go)
        {
            return;
        }

        //Let's start sending the request

         session=CommonFunctions.getSharedPreferences(getApplicationContext()).getString("session",null);

        if(session==null)
        {
            return;
        }



        Runnable run=new Runnable(){
            @Override
            public void run() {
                hide(true);
                if(projectId==null)
                {
                    createProjectRequest(activity);
                }else{
                    updateProject(activity);
                }

            }
        };

        new Thread(run).start();

    }

    private void hide(final boolean yes)
    {
        activity.runOnUiThread(new Runnable()
        {
            @Override
            public void run() {
                if(yes)
                {
                    //Hide the view
                    content.setVisibility(View.INVISIBLE);
                    progressBar.setVisibility(View.VISIBLE);
                }else{
                    //show
                    //Hide the view
                    content.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                }

            }
        });
    }


    private void createProjectRequest(Context context)
    {
        CreateProjectRequest request=new CreateProjectRequest(session,projectName.getText().toString(),startDate.getText().toString(),endDate.getText().toString(),enableCheckBox.isChecked());
        //Login process

        final String jsonString=request.getJson(request);
        Log.d("Alkamli",jsonString);
        URL url;
        String response = "";
        try {
            url = new URL(context.getString(R.string.create_project_url));
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

            if (responseCode == HttpsURLConnection.HTTP_CREATED)
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
                    CommonFunctions.sendToast(activity,getString(R.string.project_creation_was_not_successful));

                    hide(false);

                    return;
                }
                //Create project is successful
                CommonFunctions.sendToast(activity,getString(R.string.project_creation_is_Successful));
                //Add the project to the list from the response
                ObjectMapper objectMapper = new ObjectMapper();
                Project project = objectMapper.readValue(response, Project.class);
                Service.addProjectToList(project);
                Log.d("Alkamli","project creation  is Successful");
                Intent i=new Intent(this,HomeActivity.class);
                startActivity(i);
                finish();
            }else if(responseCode == HttpsURLConnection.HTTP_UNAUTHORIZED)
            {
                //User doesn't have permission to make this request
                CommonFunctions.sendToast(activity,getString(R.string.not_authorized));
                CommonFunctions.sessionExpiredHandler(activity,activity);

            }
            else {
                response="";
                Log.d("Alkamli",Integer.toString(responseCode));
                //login is not successful
                CommonFunctions.sendToast(activity,getString(R.string.project_creation_was_not_successful));

                hide(false);

            }
        } catch (Exception e) {

            class Local {
            }
            ;
            e(TAG, ("MethodName: " + Local.class.getEnclosingMethod().getName() + " || ErrorMessage: " + e.getMessage()));
        }
    }


    private void updateProject(Context context)
    {
        UpdateProjectRequest request=new UpdateProjectRequest(session,projectName.getText().toString(),startDate.getText().toString(),endDate.getText().toString(),enableCheckBox.isChecked(),projectId);
        //Login process

        final String jsonString=request.getJson(request);
        Log.d("Alkamli",jsonString);
        URL url;
        String response = "";
        try {
            url = new URL(context.getString(R.string.update_project_url));
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
                //First we make sure the response is a session
                if(CommonFunctions.clean(response).length()<0)
                {
                    CommonFunctions.sendToast(activity,getString(R.string.project_update_was_not_successful));

                    hide(false);

                    return;
                }
                //Create project is successful
                CommonFunctions.sendToast(activity,getString(R.string.project_update_is_Successful));
                //Add the project to the list from the response
                ObjectMapper objectMapper = new ObjectMapper();
                Project project = objectMapper.readValue(response, Project.class);
                Service.addProjectToList(project);
                Log.d("Alkamli","project update  is Successful");
                Intent i=new Intent(this,HomeActivity.class);
                startActivity(i);
                finish();
            }else if(responseCode == HttpsURLConnection.HTTP_UNAUTHORIZED)
            {
                //User doesn't have permission to make this request
                CommonFunctions.sendToast(activity,getString(R.string.not_authorized));
                CommonFunctions.sessionExpiredHandler(activity,activity);

            }
            else {
                response="";
                Log.d("Alkamli",Integer.toString(responseCode));
                //login is not successful
                CommonFunctions.sendToast(activity,getString(R.string.project_update_was_not_successful));
                hide(false);

            }
        } catch (Exception e) {

            class Local {
            }
            ;
            e(TAG, ("MethodName: " + Local.class.getEnclosingMethod().getName() + " || ErrorMessage: " + e.getMessage()));
        }

    }



    private void fillProjectInfo()
    {
        ArrayList<Project> projectArrayList=Service.getProjectList();
        if(projectArrayList==null || projectId==null)
        {
            return;
        }
        for(Project temp:projectArrayList)
        {
            if(temp.getProjectID()==Integer.parseInt(projectId))
            {
                project=temp;
                break;
            }
        }
        if(project==null)
        {
            return;
        }

        projectName.setText(project.getProjectName());
        startDate.setText(project.getStartDate());
        endDate.setText(project.getEndDate());
        if(project.isEnabledState())
        {
           // Log.e(TAG,"This project should be enabled");
            enableCheckBox.setChecked(true);
        }
    }
}
