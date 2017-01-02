package alkamli.fahad.teammanagment.teammanagment.views;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;

import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import alkamli.fahad.teammanagment.teammanagment.CommonFunctions;
import alkamli.fahad.teammanagment.teammanagment.R;
import alkamli.fahad.teammanagment.teammanagment.http_client.HttpRequestClient;
import alkamli.fahad.teammanagment.teammanagment.http_client.HttpRequestClientResponse;
import alkamli.fahad.teammanagment.teammanagment.requests.task.UpdateTaskRequest;
import alkamli.fahad.teammanagment.teammanagment.service.Service;
import entity.Task;

import static alkamli.fahad.teammanagment.teammanagment.CommonFunctions.TAG;
import static alkamli.fahad.teammanagment.teammanagment.CommonFunctions.sessionExpiredHandler;

public class EditTaskActivity extends AppCompatActivity implements  Validator.ValidationListener{

    private Task task;
     Button taskCompletedState;
    @NotEmpty
    EditText taskSummary;
    @NotEmpty
    EditText taskStartDate;
    @NotEmpty
    EditText taskEndDate;
    @NotEmpty
    EditText doneTotalHours;
    Validator validator;
    Activity activity;
    LinearLayout adminContent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_task);

        if(getIntent().getIntExtra("taskId",-1) ==-1)
        {
                finish();
        }else if (Service.getTaskById(getIntent().getIntExtra("taskId",-1))==null)
        {
            Toast.makeText(this, getString(R.string.task_does_not_exists), Toast.LENGTH_SHORT).show();
            finish();
        }
        this.task=Service.getTaskById(getIntent().getIntExtra("taskId",-1));
        this.taskCompletedState=(Button) findViewById(R.id.taskCompletedState);
        adminContent=(LinearLayout) findViewById(R.id.adminContent);
        doneTotalHours=(EditText) findViewById(R.id.doneTotalHours);
        taskSummary=(EditText) findViewById(R.id.taskSummary);
        taskStartDate=(EditText) findViewById(R.id.taskStartDate);
        taskEndDate=(EditText) findViewById(R.id.taskEndDate);

        if(CommonFunctions.getSharedPreferences(this).getBoolean("admin",false))
        {
            adminContent.setVisibility(View.VISIBLE);
        }
        taskSummary.setText(task.getTask_summary());
        taskStartDate.setText(task.getTask_start_date());
        taskEndDate.setText(task.getTask_end_date());
        doneTotalHours.setText(Double.toString(task.getDone_total_hours()));
        doneTotalHours.setSelection(doneTotalHours.getText().length());

        if(task.isCompleted())
        {
            taskCompletedState.setBackground(ContextCompat.getDrawable(getBaseContext(), R.drawable.element_is_enabled));
        }
        validator=new Validator(this);
        validator.setValidationListener(this);
        activity=this;

    }


    public void changeTaskState(View view)
    {
        if(task.isCompleted())
        {
            task.setCompleted(false);
            taskCompletedState.setBackground(ContextCompat.getDrawable(getBaseContext(), R.drawable.element_not_enabled));
        }else{
            task.setCompleted(true);
            taskCompletedState.setBackground(ContextCompat.getDrawable(getBaseContext(), R.drawable.element_is_enabled));
        }

    }

    public void save(View view)
    {
        validator.validate();
    }

    @Override
    public void onValidationSucceeded() {
        if(CommonFunctions.getSharedPreferences(this).getBoolean("admin",false))
        {
            task.setTask_summary(taskSummary.getText().toString());
            task.setTask_start_date(taskStartDate.getText().toString());
            task.setTask_end_date(taskEndDate.getText().toString());
        }
       task.setDone_total_hours(Double.parseDouble(doneTotalHours.getText().toString()));
        new Thread(new Runnable()
        {
            @Override
            public void run() {
                sendUpdateTaskRequest(activity);

            }
        }).start();

    }

    @Override
    public void onValidationFailed(List<ValidationError> errors) {
        try {
            for (ValidationError temp : errors) {
                if (temp.getView() instanceof EditText) {
                    ((EditText) temp.getView()).setError(temp.getCollatedErrorMessage(getApplicationContext()));
                } else if (temp.getView() instanceof TextView) {
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

    private void sendUpdateTaskRequest(Context context)
    {
        //UpdateTaskRequest
        try {
            String session = CommonFunctions.getSharedPreferences(context).getString("session", null);
            if (session == null)
            {
                sessionExpiredHandler(context,activity);
                return;
            }
            UpdateTaskRequest request = new UpdateTaskRequest(session,task);
            //Login process
            HttpRequestClient client = new HttpRequestClient(context.getString(R.string.update_task_url), request.getJson(request));

            HttpRequestClientResponse response = client.post();

            if (response == null) {
                return;
            }
            switch (response.getHttpStatus())
            {
                case HttpsURLConnection.HTTP_OK:
                {
                    ObjectMapper objectMapper = new ObjectMapper();
                    objectMapper.configure(JsonParser.Feature.AUTO_CLOSE_SOURCE, true);
                   // Log.e(TAG,response.getResponseString());
                   Task task = objectMapper.readValue(response.getResponseString(), Task.class);
                    Log.d(TAG, task.getJson(task));
                    Service.addTaskToList(task);
                    Intent i = new Intent(this, HomeActivity.class);
                    startActivity(i);
                    CommonFunctions.sendToast(activity, getString(R.string.task_has_been_updated));
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
                    CommonFunctions.sendToast(activity, getString(R.string.task_could_not_be_updated));
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
