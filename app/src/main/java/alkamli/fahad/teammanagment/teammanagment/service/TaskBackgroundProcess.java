package alkamli.fahad.teammanagment.teammanagment.service;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import javax.net.ssl.HttpsURLConnection;

import alkamli.fahad.teammanagment.teammanagment.CommonFunctions;
import alkamli.fahad.teammanagment.teammanagment.R;
import alkamli.fahad.teammanagment.teammanagment.http_client.HttpRequestClient;
import alkamli.fahad.teammanagment.teammanagment.http_client.HttpRequestClientResponse;
import alkamli.fahad.teammanagment.teammanagment.requests.task.GetAllTasksRequest;
import alkamli.fahad.teammanagment.teammanagment.views.HomeActivity;
import entity.Task;
import entity.TaskListViewElement;

import static alkamli.fahad.teammanagment.teammanagment.CommonFunctions.waitingTime;
import static android.util.Log.e;

public class TaskBackgroundProcess extends Observable {


    ArrayList<TaskListViewElement> tasksList = new ArrayList<TaskListViewElement>();
    final String TAG = "Alkamli";
    TaskBackgroundProcess backgroundProcess;

    ArrayList<Observer> tasksWaitingList = null;
    boolean updatedList = false;

    private Thread backgroundThread;
    Context context;

    public TaskBackgroundProcess(Context context, ArrayList<Observer> tasksWaitingList) {
        this.context = context;
        backgroundProcess = this;
        this.tasksWaitingList = tasksWaitingList;
        Runnable run = new Runnable() {
            @Override
            public void run() {
                backgroundProcess();
            }
        };
        backgroundThread = new Thread(run);
        backgroundThread.start();
    }

    private void backgroundProcess() {
        try {
            Log.d(TAG, "Task backgroundProcess Started");
            do {
                synchronized (this) {
                    try {
                        wait(1000);
                        if(context != null)
                        {
                            refreshTaskList(null);
                        }
                        //Send the request every 1 minute wait(60000);
                        wait(waitingTime);
                        Log.d(TAG, "Finished stopping");

                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        // handle the interrupt

                        Log.e(TAG, "Task Background service has been Interrupted");
                        /// Log.e(CommonFunctions.TAG, ("MethodName: " + Local.class.getEnclosingMethod().getName() + " || ErrorMessage: " + e.getMessage()));
                        return;
                    } catch (Exception e) {
                        class Local {
                        }
                        ;
                        Log.e(CommonFunctions.TAG, ("MethodName: " + Local.class.getEnclosingMethod().getName() + " || ErrorMessage: " + e.getMessage()));

                    }
                }

            } while (true);

        } catch (Exception e) {
            class Local {
            }
            ;
            Log.e(CommonFunctions.TAG, ("MethodName: " + Local.class.getEnclosingMethod().getName() + " || ErrorMessage: " + e.getMessage()));
            return;
        }

    }

    //Fix this one
    //if one of the tasks get changed we will never know !because this is checking for an existing project only and not the tasks inside
    public synchronized void addTaskToList(TaskListViewElement task) {
        for (TaskListViewElement temp : tasksList) {
            if (temp.getProject().getProjectID() == task.getProject().getProjectID())
            {
                return;
            }
        }
        Log.d(TAG, "Task object has been added");
        updatedList = true;
        tasksList.add(task);
    }


    public synchronized void addTaskToList(Task task) {

        for (TaskListViewElement temp : getTasksList()) {
            if (temp.getProject().getProjectID() == task.getProject_id())
            {
                //make sure an older task will be removed before adding  the updated task
                for(Task temp2:temp.getTaskArrayList())
                {
                    if(temp2.getTask_id()==task.getTask_id())
                    {
                        temp.getTaskArrayList().remove(temp2);
                        break;
                    }
                }
                temp.getTaskArrayList().add(task);
                setChanged();
                notifyObservers();
                return;
            }
        }
        //Create new TaskListViewElement that contains the project + the newly created task
        if (Service.getProjectById(Integer.toString(task.getProject_id())) != null) {
            TaskListViewElement newTask = new TaskListViewElement(Service.getProjectById(Integer.toString(task.getProject_id())), task);
            tasksList.add(newTask);
            setChanged();
            notifyObservers();
        }

    }

    public synchronized ArrayList<TaskListViewElement> getTasksList() {
        return tasksList;
    }

    public synchronized void stopBackgroundProcess() {

        backgroundThread.interrupt();
    }


    private synchronized void checkForWaitingObservers() {
        try {
            if (tasksWaitingList != null) {
                for (Observer observer : tasksWaitingList) {
                    addObserver(observer);
                    // Log.e(TAG, "add the Project waiting observers");
                    tasksWaitingList.remove(observer);
                }
            }
        } catch (Exception e) {
            class Local {
            }
            ;
            Log.e(CommonFunctions.TAG, ("MethodName: " + Local.class.getEnclosingMethod().getName() + " || ErrorMessage: " + e.getMessage()));

        }

    }


    public synchronized void deleteTask(int taskId)
    {

        for (TaskListViewElement temp : getTasksList())
        {
            //if (temp.getProject().getProjectID() == task.getProject_id())
            {
                //make sure an older task will be removed before adding  the updated task
                for(Task temp2:temp.getTaskArrayList())
                {
                    if(temp2.getTask_id()==taskId)
                    {
                        temp.getTaskArrayList().remove(temp2);
                        //if this project doesn't have any more tasks delete the project from the list
                        if(temp.getTaskArrayList().size()<1)
                        {
                            getTasksList().remove(temp);

                        }
                        setChanged();
                        notifyObservers();
                        return;
                    }
                }

            }
        }
    }
    public synchronized void refreshTaskList(Activity activity)
    {
        if (!CommonFunctions.userLoggedOut(context) && CommonFunctions.isNetworkAvailable(context))
        {
            try {
                String session = CommonFunctions.getSharedPreferences(context).getString("session", null);
                if (session == null) {
                    Log.d(TAG, "session==null");
                    return;
                }
                GetAllTasksRequest request = new GetAllTasksRequest(session);
                HttpRequestClient client = new HttpRequestClient(context.getString(R.string.get_all_tasks_url), request.getJson(request));
                HttpRequestClientResponse response = client.post();

                switch (response.getHttpStatus())
                {
                    case HttpsURLConnection.HTTP_OK:
                    {
                        if (CommonFunctions.clean(response.getResponseString()).length() > 0)
                        {
                            Log.i(TAG,response.getResponseString());
                            ObjectMapper objectMapper = new ObjectMapper();
                            objectMapper.configure(JsonParser.Feature.AUTO_CLOSE_SOURCE, true);
                            TypeReference<List<TaskListViewElement>> mapType = new TypeReference<List<TaskListViewElement>>() {
                            };
                            List<TaskListViewElement> jsonToProjectList = objectMapper.readValue(response.getResponseString(), mapType);
                            if (getTasksList().equals(jsonToProjectList) == false)
                            {
                                //The size changed so update the whole thing
                                Log.d(TAG, "Task list: The task array changed so update the whole thing");
                                tasksList.clear();
                            }
                            for (TaskListViewElement temp : jsonToProjectList) {
                                addTaskToList(temp);
                            }
                            if (getTasksList() != null && updatedList)
                            {
                                checkForWaitingObservers();
                                setChanged();
                                notifyObservers();
                                updatedList = false;
                                // Log.e(TAG,"project notifyObservers has been called");
                            } else if (getTasksList() == null) {
                                Log.e(TAG, "getTasksList() == null");
                            }
                        } else {
                            //This user doesn't have any more tasks we need to clear the tasks and notify the bla bla
                            tasksList.clear();
                            setChanged();
                            notifyObservers();
                        }
                        break;
                    }
                    case HttpURLConnection.HTTP_UNAUTHORIZED: {
                        CommonFunctions.sessionExpiredHandler(context, activity);
                        break;
                    }


                }


            }catch(Exception e)
            {
                class Local {
                }
                ;
                Log.e(CommonFunctions.TAG, ("MethodName: " + Local.class.getEnclosingMethod().getName() + " || ErrorMessage: " + e.getMessage()));

                e.printStackTrace();
            }

        }else{
            //no internet connection
            if(HomeActivity.getActivity()!= null)
            {
               // CommonFunctions.sendToast(HomeActivity.getActivity(),"Error: Check internet connection");
                return;
            }
        }
    }
}
