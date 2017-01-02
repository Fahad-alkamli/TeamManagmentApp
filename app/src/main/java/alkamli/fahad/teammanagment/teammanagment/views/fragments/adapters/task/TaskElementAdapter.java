package alkamli.fahad.teammanagment.teammanagment.views.fragments.adapters.task;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.HttpURLConnection;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

import alkamli.fahad.teammanagment.teammanagment.CommonFunctions;
import alkamli.fahad.teammanagment.teammanagment.views.DisplayTaskMembersActivity;
import alkamli.fahad.teammanagment.teammanagment.R;
import alkamli.fahad.teammanagment.teammanagment.http_client.HttpRequestClient;
import alkamli.fahad.teammanagment.teammanagment.http_client.HttpRequestClientResponse;
import alkamli.fahad.teammanagment.teammanagment.requests.task.AddHoursToTaskRequest;
import alkamli.fahad.teammanagment.teammanagment.requests.task.DeleteTaskRequest;
import alkamli.fahad.teammanagment.teammanagment.requests.task.SubmitTaskCompleteRequest;
import alkamli.fahad.teammanagment.teammanagment.requests.task.SubmitTaskNotCompleteRequest;
import alkamli.fahad.teammanagment.teammanagment.service.Service;
import alkamli.fahad.teammanagment.teammanagment.views.HomeActivity;
import alkamli.fahad.teammanagment.teammanagment.views.EditTaskActivity;
import entity.Task;

import static alkamli.fahad.teammanagment.teammanagment.CommonFunctions.TAG;


public class TaskElementAdapter extends RecyclerView.Adapter<TaskElementAdapter.MyViewHolder>
{

    private ArrayList<Task> tasks;
    private Context context;

    public class MyViewHolder extends RecyclerView.ViewHolder
    {
        public TextView taskSummary, startDate, endDate,completedTaskHours;
        public Button taskCompletedState ;
        public View view;
        public LinearLayout line;

        public MyViewHolder(View view) {
            super(view);
            this.view=view;
            taskSummary = (TextView) view.findViewById(R.id.taskSummary);
            startDate = (TextView) view.findViewById(R.id.startDate);
            endDate = (TextView) view.findViewById(R.id.endDate);
            completedTaskHours=(TextView) view.findViewById(R.id.completedTaskHours);
            taskCompletedState=(Button) view.findViewById(R.id.taskCompletedState);
            line=(LinearLayout) view.findViewById(R.id.line);
        }
    }




    public TaskElementAdapter(Context context,ArrayList<Task> tasksList)
    {
        this.tasks = tasksList;
        this.context=context;
    }


    @Override
    public int getItemCount() {
        if(tasks==null)
        {
            return 0;
        }
        return tasks.size();
    }

    @Override
    public TaskElementAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View CustomView = inflater.inflate(R.layout.task_element, parent, false);

        this.context=parent.getContext();

        return new TaskElementAdapter.MyViewHolder(CustomView);
    }

    @Override
    public void onBindViewHolder(final TaskElementAdapter.MyViewHolder holder, int position) {
        if(tasks==null)
        {
            return;
        }
        Task task = tasks.get(position);
        if(position==0)
        {
            //Hide the line
            holder.line.setVisibility(View.GONE);
        }
        holder.taskSummary.setText(task.getTask_summary());
        holder.startDate.setText(task.getTask_start_date());
        holder.endDate.setText(task.getTask_end_date());
        holder.completedTaskHours.setText(Double.toString(task.getDone_total_hours()));
        if(task.isCompleted() && context!=null)
        {
            holder.taskCompletedState.setBackground(ContextCompat.getDrawable(context, R.drawable.element_is_enabled));
        }else{
            holder.taskCompletedState.setBackground(ContextCompat.getDrawable(context, R.drawable.element_not_enabled));
        }


        holder.view.setTag(task.getTask_id());
        if (CommonFunctions.getSharedPreferences(context).getBoolean("admin", false))
        {
            holder.view.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view) {
                    final int id=(int) view.getTag();
                    // Log.e(TAG,"The Task id: "+id);
                    PopupMenu popupMenu = new PopupMenu(context, holder.completedTaskHours);
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem menuItem)
                        {
                            switch(menuItem.getItemId())
                            {
                                case R.id.editTask:
                                {
                                    if(HomeActivity.getActivity() != null)
                                    {
                                        Intent i=new Intent(context, EditTaskActivity.class);
                                        i.putExtra("taskId",id);
                                        HomeActivity.getActivity().startActivity(i);
                                    }
                                    break;
                                }
                                case R.id.deleteTask:
                                {
                                    new Thread(new Runnable()
                                    {
                                        @Override
                                        public void run() {
                                            deleteTask(id);
                                        }
                                    }).start();
                                    break;
                                }
                                case R.id.taskMembers:
                                {

                                    if(HomeActivity.getActivity()!= null)
                                    {
                                        Intent i = new Intent(context, DisplayTaskMembersActivity.class);
                                        i.putExtra("task_id",id);
                                        HomeActivity.getActivity().startActivity(i);
                                    }
                                    break;
                                }
                                case R.id.taskCompleted:
                                {
                                    if(HomeActivity.getActivity() != null)
                                    {

                                        new Thread(new Runnable()
                                        {
                                            @Override
                                            public void run() {
                                                sendTaskCompleteRequest(id);
                                            }
                                        }).start();
                                    }
                                    break;
                                }
                                case R.id.taskNotComplete:
                                {
                                    if(HomeActivity.getActivity() != null)
                                    {

                                        new Thread(new Runnable()
                                        {
                                            @Override
                                            public void run() {
                                                sendTaskNotCompleteRequest(id);
                                            }
                                        }).start();
                                    }
                                    break;
                                }
                                case R.id.addHours:
                                {
                                    askForInput(id);
                                    break;
                                }
                            }
                            return true;
                        }
                    });

                    popupMenu.inflate(R.menu.popup_menu_tasks_admin_options);
                    popupMenu.show();
                }
            });
        }else{
            //Normal user menu
            holder.view.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view) {
                    final int id=(int) view.getTag();
                    // Log.e(TAG,"The Task id: "+id);
                    PopupMenu popupMenu = new PopupMenu(context, view);
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem menuItem)
                        {
                            switch(menuItem.getItemId())
                            {
                                case R.id.taskCompleted:
                                {
                                    if(HomeActivity.getActivity() != null)
                                    {

                                        new Thread(new Runnable()
                                        {
                                            @Override
                                            public void run() {
                                                sendTaskCompleteRequest(id);
                                            }
                                        }).start();
                                    }
                                    break;
                                }
                                case R.id.taskNotComplete:
                                {
                                    if(HomeActivity.getActivity() != null)
                                    {

                                        new Thread(new Runnable()
                                        {
                                            @Override
                                            public void run() {
                                                sendTaskNotCompleteRequest(id);
                                            }
                                        }).start();
                                    }
                                    break;
                                }
                                case R.id.addHours:
                                {
                                    askForInput(id);
                                    break;
                                }
                            }
                            return true;
                        }
                    });

                    popupMenu.inflate(R.menu.popup_menu_tasks_user_options);
                    popupMenu.show();
                }
            });
        }
    }



    private void deleteTask(int taskId)
    {
        String session= CommonFunctions.getSharedPreferences(context).getString("session",null);
        if(session==null) {return;}
        DeleteTaskRequest request=new DeleteTaskRequest(session,taskId);
        HttpRequestClient client=new HttpRequestClient(context.getString(R.string.delete_task_url),request.getJson(request));
        HttpRequestClientResponse response=client.post();
        switch(response.getHttpStatus())
        {
            case HttpsURLConnection.HTTP_OK:
            {
                if(HomeActivity.getActivity() != null)
                {
                    Service.deleteTask(taskId);
                    CommonFunctions.sendToast(HomeActivity.getActivity(),context.getString(R.string.Task_has_been_deleted));
                }
                Log.d(CommonFunctions.TAG,"Task has been deleted");
                break;
            }
            case HttpURLConnection.HTTP_UNAUTHORIZED:
            {
                CommonFunctions.sessionExpiredHandler(context, HomeActivity.getActivity());
                break;
            }

        }


    }


    private void sendTaskCompleteRequest(int taskId)
    {
        try {

            String session = CommonFunctions.getSharedPreferences(context).getString("session", null);
            if (session == null) {
                return;
            }
            SubmitTaskCompleteRequest request = new SubmitTaskCompleteRequest(session, taskId);

            HttpRequestClient client = new HttpRequestClient(context.getString(R.string.submit_task_complete_url), request.getJson(request));
            HttpRequestClientResponse response = client.post();

            if (response.getHttpStatus() == HttpURLConnection.HTTP_OK)
            {
                if(response.getResponseString().length()>0)
                {
                    ObjectMapper objectMapper = new ObjectMapper();
                    objectMapper.configure(JsonParser.Feature.AUTO_CLOSE_SOURCE, true);
                    // Log.e(TAG,response.getResponseString());
                    Task task = objectMapper.readValue(response.getResponseString(), Task.class);
                    Log.d(TAG, task.getJson(task));
                    Service.addTaskToList(task);
                }

            } else if (response.getHttpStatus() == HttpURLConnection.HTTP_UNAUTHORIZED) {
                CommonFunctions.sessionExpiredHandler(context, HomeActivity.getActivity());
            }


        }catch(Exception e)
        {

        }

    }

    private void sendTaskNotCompleteRequest(int taskId)
    {
            try {

                String session = CommonFunctions.getSharedPreferences(context).getString("session", null);
                if (session == null) {
                    return;
                }
                SubmitTaskNotCompleteRequest request = new SubmitTaskNotCompleteRequest(session, taskId);

                HttpRequestClient client = new HttpRequestClient(context.getString(R.string.submit_task_not_complete_url), request.getJson(request));
                HttpRequestClientResponse response = client.post();

                if (response.getHttpStatus() == HttpURLConnection.HTTP_OK)
                {
                    if(response.getResponseString().length()>0)
                    {
                        ObjectMapper objectMapper = new ObjectMapper();
                        objectMapper.configure(JsonParser.Feature.AUTO_CLOSE_SOURCE, true);
                        // Log.e(TAG,response.getResponseString());
                        Task task = objectMapper.readValue(response.getResponseString(), Task.class);
                        Log.d(TAG, task.getJson(task));
                        Service.addTaskToList(task);
                    }

                } else if (response.getHttpStatus() == HttpURLConnection.HTTP_UNAUTHORIZED) {
                    CommonFunctions.sessionExpiredHandler(context, HomeActivity.getActivity());
                }
            }catch(Exception e)
            {

            }

    }

    private void sendAddHoursToTaskRequest(int taskId, double hours)
    {
        try {
            String session = CommonFunctions.getSharedPreferences(context).getString("session", null);
            if (session == null) {
                return;
            }
            AddHoursToTaskRequest request = new AddHoursToTaskRequest(session, taskId,hours);
            HttpRequestClient client = new HttpRequestClient(context.getString(R.string.add_hours_to_task_url), request.getJson(request));
            HttpRequestClientResponse response = client.post();

         //   Log.e(TAG,response.getjson(response));
            if (response.getHttpStatus() == HttpURLConnection.HTTP_OK)
            {
                if(response.getResponseString().length()>0)
                {
                    ObjectMapper objectMapper = new ObjectMapper();
                    objectMapper.configure(JsonParser.Feature.AUTO_CLOSE_SOURCE, true);
                    // Log.e(TAG,response.getResponseString());
                    Task task = objectMapper.readValue(response.getResponseString(), Task.class);
                    Log.d(TAG, task.getJson(task));
                    Service.addTaskToList(task);
                }

            } else if (response.getHttpStatus() == HttpURLConnection.HTTP_UNAUTHORIZED) {
                CommonFunctions.sessionExpiredHandler(context, HomeActivity.getActivity());
            }


        }catch(Exception e)
        {

        }

    }

    private void askForInput(final int taskId)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(context.getString(R.string.please_enter_a_number));

// Set up the input
        final EditText input = new EditText(context);
        input.setText("0.0");
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
     //   input.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL );
        input.setRawInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        builder.setView(input);
// Set up the buttons
        builder.setPositiveButton(context.getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        builder.setNegativeButton(context.getString(R.string.cancel),null);
        //http://stackoverflow.com/questions/2620444/how-to-prevent-a-dialog-from-closing-when-a-button-is-clicked
        final AlertDialog dialog = builder.create();
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                try{
                    if(input.getText()!= null && CommonFunctions.clean(input.getText().toString())!= null)
                    {
                        final double  hours= Double.parseDouble(CommonFunctions.clean(input.getText().toString()));
                        if(hours>0)
                        {
                            new Thread(new Runnable()
                            {
                                @Override
                                public void run() {
                                    sendAddHoursToTaskRequest(taskId,hours);
                                }
                            }).start();
                            dialog.dismiss();
                        }else{

                            input.setError(context.getString(R.string.hours_need_to_be_more_than_zero));
                        }
                    }
                }catch(Exception e)
                {
                    Log.e(TAG,e.getMessage());
                    input.setError(context.getString(R.string.hours_need_to_be_more_than_zero));
                }

            }
        });

    }
}
