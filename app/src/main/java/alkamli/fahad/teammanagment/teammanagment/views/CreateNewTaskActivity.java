package alkamli.fahad.teammanagment.teammanagment.views;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.mobsandgeeks.saripaar.annotation.Pattern;

import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import alkamli.fahad.teammanagment.teammanagment.CommonFunctions;
import alkamli.fahad.teammanagment.teammanagment.http_client.HttpRequestClient;
import alkamli.fahad.teammanagment.teammanagment.R;
import alkamli.fahad.teammanagment.teammanagment.http_client.HttpRequestClientResponse;
import alkamli.fahad.teammanagment.teammanagment.requests.task.CreateTaskRequest;
import alkamli.fahad.teammanagment.teammanagment.service.Service;
import entity.Project;
import entity.Task;

import static alkamli.fahad.teammanagment.teammanagment.CommonFunctions.TAG;
import static alkamli.fahad.teammanagment.teammanagment.CommonFunctions.clean;
import static alkamli.fahad.teammanagment.teammanagment.CommonFunctions.sessionExpiredHandler;
import static android.util.Log.e;

public class CreateNewTaskActivity extends AppCompatActivity implements Validator.ValidationListener {


    int pickProjectRequest = 1;
    Validator validator;
    @NotEmpty
    EditText taskSummary;
    @NotEmpty(message = "Pick a project First")
    TextView projectId;
    Activity activity;
    EditText doneTotalHours;
    @Pattern(regex = CommonFunctions.datePattern, message = "Only dd/mm/yyyy")
    EditText startDate, endDate;
    CheckBox completedCheckBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_task);
        taskSummary = (EditText) findViewById(R.id.projectSummary);
        startDate = (EditText) findViewById(R.id.startDate);
        endDate = (EditText) findViewById(R.id.endDate);
        completedCheckBox = (CheckBox) findViewById(R.id.completedCheckBox);
        doneTotalHours = (EditText) findViewById(R.id.doneTotalHours);
        projectId = (TextView) findViewById(R.id.projectId);
        validator = new Validator(this);
        validator.setValidationListener(this);
        activity = this;

    }

    public void launchPickProject(View view) {
        Intent i = new Intent(this, AssignProjectToUserActivity.class);
        startActivityForResult(i, pickProjectRequest);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK) {
            if (requestCode == pickProjectRequest) {
                //This is a response containing the picked projects
                final ArrayList<String> projectIds = data.getStringArrayListExtra("projectIds");
                Project project = Service.getProjectById(projectIds.get(0));
                if (project != null) {
                    //projectName.setText(project.getProjectName());
                    projectId.setText(Integer.toString(project.getProjectID()));
                }
                //HMMMM can i add a task to more than one project?!

            }

        }

    }


    public void createTask(View view) {
        validator.validate();
    }

    @Override
    public void onValidationSucceeded() {
        //make sure the end date comes after the start date
        if(!CommonFunctions.compareDates(clean(startDate.getText().toString()),clean(endDate.getText().toString())))
        {
            endDate.setError(getString(R.string.end_date_should_be_after_the_start_date));
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                createTaskRequest(getApplicationContext());
            }
        }).start();
        Log.d(CommonFunctions.TAG, "Create the Task");
    }

    @Override
    public void onValidationFailed(List<ValidationError> errors) {
        try {
            for (ValidationError temp : errors) {
                if (temp.getView() instanceof EditText)
                {
                    ((EditText) temp.getView()).setError(temp.getCollatedErrorMessage(getApplicationContext()));
                } else if (temp.getView() instanceof TextView)
                {
                    ((TextView) temp.getView()).setError(temp.getCollatedErrorMessage(getApplicationContext()));
                }
            }
        } catch (Exception e) {
            class Local {
            }
            ;
            Log.e(TAG, ("MethodName: " + Local.class.getEnclosingMethod().getName() + " || ErrorMessage: " + e.getMessage()));

        }

    }


    private void createTaskRequest(Context context) {
        try {
            String session = CommonFunctions.getSharedPreferences(context).getString("session", null);
            if (session == null)
            {
                sessionExpiredHandler(context,activity);
                return;
            }
            CreateTaskRequest request = new CreateTaskRequest(session, Integer.parseInt(projectId.getText().toString()), doneTotalHours.getText().toString(), taskSummary.getText().toString(), startDate.getText().toString(), endDate.getText().toString(), completedCheckBox.isChecked());
            //Login process
            HttpRequestClient client = new HttpRequestClient(context.getString(R.string.create_task_url), request.getJson(request));

            HttpRequestClientResponse response = client.post();

            if (response == null) {
                return;
            }
            switch (response.getHttpStatus())
            {
                case HttpsURLConnection.HTTP_CREATED:
                {
                    ObjectMapper objectMapper = new ObjectMapper();
                    Task task = objectMapper.readValue(response.getResponseString(), Task.class);
                    Log.d(TAG, task.getJson(task));
                    Service.addTaskToList(task);
                    Intent i = new Intent(this, HomeActivity.class);
                    startActivity(i);
                    CommonFunctions.sendToast(activity, getString(R.string.task_has_been_created));
                    finish();
                    break;
                }
                case HttpsURLConnection.HTTP_UNAUTHORIZED:
                {
                    CommonFunctions.sendToast(activity, getString(R.string.not_authorized));
                    sessionExpiredHandler(activity,activity);
                    break;
                }
                default: {
                    CommonFunctions.sendToast(activity, getString(R.string.task_creation_was_not_Successful));
                }
            }
        } catch (Exception e) {
            class Local {
            }
            ;
            Log.e(TAG, ("MethodName: " + Local.class.getEnclosingMethod().getName() + " || ErrorMessage: " + e.getMessage()));

        }
    }


}

