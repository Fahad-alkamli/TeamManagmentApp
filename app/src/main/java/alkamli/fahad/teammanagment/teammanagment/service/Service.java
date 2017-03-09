package alkamli.fahad.teammanagment.teammanagment.service;


import android.app.Activity;
import android.app.IntentService;
import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;

import java.util.ArrayList;
import java.util.Observer;

import alkamli.fahad.teammanagment.teammanagment.CommonFunctions;
import entity.Project;
import entity.Task;
import entity.TaskListViewElement;
import entity.User;


public class Service extends IntentService {
    final String TAG="Alkamli";

   private static boolean Running=false;


    static ProjectBackgroundProcess projectbackgroundProcess;
    static UserBackgroundProcess userbackgroundProcess;
    static TaskBackgroundProcess taskBackgroundProcess;
    static ArrayList<Observer> projectWaitingList=new ArrayList<Observer>();
    static ArrayList<Observer> userWaitingList=new ArrayList<Observer>();
    static ArrayList<Observer> taskWaitingList=new ArrayList<Observer>();
    public Service() {
        super("Service");
        //Log.d(TAG,"Service ");
    }


    public static void stopService()
    {
        Running=false;
        //Stop the background processes if they exists
        if(projectbackgroundProcess != null)
        {
            projectbackgroundProcess.stopBackgroundProcess();
        }
        if(userbackgroundProcess != null)
        {
            userbackgroundProcess.stopBackgroundProcess();
        }
        if(taskBackgroundProcess != null)
        {
            taskBackgroundProcess.stopBackgroundProcess();
        }
        projectbackgroundProcess=null;
        userbackgroundProcess=null;
        taskBackgroundProcess=null;
        Log.i("Alkamli","Stop the service");
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        if(!Running)
        {
            Running=true;
        }
        else{
            //Already running
            return Service.START_STICKY;
        }
         projectbackgroundProcess =  new ProjectBackgroundProcess(getBaseContext(),projectWaitingList);
         userbackgroundProcess=new UserBackgroundProcess(getBaseContext(),userWaitingList);
         taskBackgroundProcess=new TaskBackgroundProcess(getBaseContext(),taskWaitingList);
        Log.i(TAG,"Service onStartCommand");

        return Service.START_STICKY;
    }


    public static void addProjectObserver(Observer observer)
    {
        if(projectbackgroundProcess != null)
        {
            projectbackgroundProcess.addObserver(observer);
            Log.i("Alkamli","addProjectObserver");
        }else{
            Log.i("Alkamli","We didn't add ProjectObserver yet");
            projectWaitingList.add(observer);
        }
    }


    public static void addTaskObserver(Observer observer)
    {
        if(taskBackgroundProcess != null)
        {
            taskBackgroundProcess.addObserver(observer);
            Log.i("Alkamli","add Task Observer");
        }else{
            Log.i("Alkamli","We didn't add Task Observer yet, adding it now");
            taskWaitingList.add(observer);
        }
    }




    public static void refreshProjectList(Activity activity)
    {
        if(projectbackgroundProcess!= null)
        {
            projectbackgroundProcess.refreshProjectList();
        }
    }

    public static void refreshTaskList(Activity activity)
    {
        if(taskBackgroundProcess!= null)
        {
            taskBackgroundProcess.refreshTaskList(activity);
        }
    }




    public static ArrayList<String> getProjectListNames()
    {
        Log.d("Alkamli","getProjectListNames");
        if(projectbackgroundProcess != null)
        {
            return projectbackgroundProcess.getProjectListNames();
        }else{

            Log.d("Alkamli","The background hasn't been started yet returning null");
        }
        return null;
    }


    public static void deletProjectFromList(String projectId)
    {
        if(projectbackgroundProcess == null)
        {
            return;
        }
        projectbackgroundProcess.deleteProjectFromList(projectId);
        projectbackgroundProcess.notify_Observers2();
    }

    public static void addProjectToList(Project project)
    {
        if(projectbackgroundProcess == null)
        {
            return;
        }
        projectbackgroundProcess.addProjectToList(project);
        projectbackgroundProcess.notify_Observers2();
    }

    public static void addUserToList(User user)
    {
        if(userbackgroundProcess == null)
        {
            return;
        }

        userbackgroundProcess.addUserToList(user);
        userbackgroundProcess.notify_Observers2();
    }


    public static void addTaskToList(Task task)
    {
        if(taskBackgroundProcess==null)
        {
            return;
        }
        taskBackgroundProcess.addTaskToList(task);
    }

    public static ArrayList<Project> getProjectList()
    {

        if(projectbackgroundProcess ==null)
        {
            ArrayList<Project> temp=new ArrayList<Project>();
            Log.e(CommonFunctions.TAG,"getProjectList ==null");
            return temp;
        }
        return projectbackgroundProcess.getProjectList();
    }


    public static ArrayList<String> getUsernames()
    {
        Log.d("Alkamli","getUsernames");
        if(userbackgroundProcess != null)
        {
            return userbackgroundProcess.getUsersListNames();
        }else{

            Log.d("Alkamli","The background hasn't been started yet returning null");
        }
        return new ArrayList<String>();
    }
    public static ArrayList<User> getUsernamesList()
    {
        if(userbackgroundProcess==null)
        {
            return new ArrayList<User>();
        }
       return  userbackgroundProcess.getUserList();
    }

    public static ArrayList<TaskListViewElement> getTasksList()
    {
        if(taskBackgroundProcess==null)
        {

            //Return an empty list
            return new ArrayList<TaskListViewElement>();
        }
        return  taskBackgroundProcess.getTasksList();
    }

    public static void addUserObserver(Observer observer)
    {
        if(userbackgroundProcess != null)
        {
            userbackgroundProcess.addObserver(observer);
            Log.i("Alkamli","add the addUserObserver observer");
        }else{
            Log.i("Alkamli","We didn't add the addUserObserver observer yet");
            userWaitingList.add(observer);
        }
    }


    @Override
    protected void onHandleIntent(Intent intent) {
    }


    public static Project getProjectById(String projectId)
    {
        if(getProjectList() ==null)
        {
            return null;
        }
        for(Project temp:getProjectList())
        {
            if(temp.getProjectID()==Integer.parseInt(projectId))
            {
                return temp;
            }
        }
        return null;
    }

    public static Task getTaskById(int taskId)
    {
        if(taskBackgroundProcess==null || taskBackgroundProcess.getTasksList()==null || taskBackgroundProcess.getTasksList().size()<1)
        {
            return null;
        }
        for(TaskListViewElement temp:taskBackgroundProcess.getTasksList())
        {
            for(Task temp2:temp.getTaskArrayList())
            {
                if(temp2.getTask_id()==taskId)
                {
                    return temp2;
                }
            }

        }

        return null;
    }


    public static void deleteTask(int taskId)
    {
        if(taskBackgroundProcess != null)
        {
            taskBackgroundProcess.deleteTask(taskId);
        }
    }

    public static void deleteUser(int userId)
    {
        if(userbackgroundProcess!= null)
        {
            userbackgroundProcess.deleteUser(userId);
        }
    }

    public static void refreshUserList(Activity activity)
    {

        if(userbackgroundProcess != null)
        {
            userbackgroundProcess.refreshUserList();
        }
    }

    public static ArrayList<Task> getAllTasks()
    {
        ArrayList<Task> temp=new ArrayList<>();
        if(taskBackgroundProcess ==null ||getTasksList()==null)
        {
            return null;
        }
        for(TaskListViewElement tempTask:getTasksList())
        {
            temp.addAll(tempTask.getTaskArrayList());
        }
        return temp;
    }
    public static ArrayList<String> getAllTasksNames()
    {
        ArrayList<String> temp=new ArrayList<>();
        if(taskBackgroundProcess ==null || getTasksList()==null)
        {
            return null;
        }
        for(TaskListViewElement temp2:getTasksList())
        {
            for(Task temp3:temp2.getTaskArrayList())
            {
                temp.add(temp3.getTask_summary());
            }
        }
        return temp;
    }
}

