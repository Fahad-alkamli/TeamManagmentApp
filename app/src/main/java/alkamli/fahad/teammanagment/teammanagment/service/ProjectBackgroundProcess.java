package alkamli.fahad.teammanagment.teammanagment.service;

import android.content.Context;
import android.util.Log;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import javax.net.ssl.HttpsURLConnection;

import alkamli.fahad.teammanagment.teammanagment.CommonFunctions;
import alkamli.fahad.teammanagment.teammanagment.R;
import alkamli.fahad.teammanagment.teammanagment.http_client.HttpRequestClient;
import alkamli.fahad.teammanagment.teammanagment.http_client.HttpRequestClientResponse;
import alkamli.fahad.teammanagment.teammanagment.requests.project.GetAllProjects;
import alkamli.fahad.teammanagment.teammanagment.views.HomeActivity;
import entity.Project;

import static alkamli.fahad.teammanagment.teammanagment.CommonFunctions.TAG;
import static alkamli.fahad.teammanagment.teammanagment.CommonFunctions.waitingTime;
import static android.util.Log.e;


public class ProjectBackgroundProcess extends Observable {

    ArrayList<Project> projectList=new ArrayList<Project>();
    ArrayList<Observer> projectWaitingList=null;
    boolean updatedList=false;

    private Thread backgroundThread;
    Context context;
    public ProjectBackgroundProcess(Context context,ArrayList<Observer> projectWaitingList)
    {
        this.context=context;
        //backgroundProcess=this;
        this.projectWaitingList=projectWaitingList;
        Runnable run=new Runnable()
        {
            @Override
            public void run() {
                backgroundProcess();
            }
        };
        backgroundThread= new Thread(run);
        backgroundThread.start();
    }

    private void backgroundProcess()
    {
        try {
            Log.d(TAG,"Project backgroundProcess Started");
            do {
                synchronized (this)
                {
                    try {

                        wait(1000);
                        if(context != null)
                        {
                            refreshProjectList();
                        }
                        //Send the request every 1 minute wait(60000);
                        wait(waitingTime);
                        Log.d(TAG, "Finished stopping");

                    } catch(InterruptedException e)
                    {
                        Thread.currentThread().interrupt();
                        // handle the interrupt

                        Log.e(TAG,"Project Background service has been Interrupted");
                        /// Log.e(CommonFunctions.TAG, ("MethodName: " + Local.class.getEnclosingMethod().getName() + " || ErrorMessage: " + e.getMessage()));
                        return;
                    }catch (Exception e) {
                        class Local {};
                        Log.e(CommonFunctions.TAG, ("MethodName: " + Local.class.getEnclosingMethod().getName() + " || ErrorMessage: " + e.getMessage()));

                    }
                }

            } while (true);

        } catch(Exception e)
        {
            class Local {};
            Log.e(CommonFunctions.TAG, ("MethodName: " + Local.class.getEnclosingMethod().getName() + " || ErrorMessage: " + e.getMessage()));
            return;
        }

    }

    public synchronized void addProjectToList(Project project)
    {
        for(Project temp:projectList)
        {
            if(temp.getProjectID()==project.getProjectID())
            {
                if(temp.equals(project)==false)
                {
                    //replace the old one with the new one
                    checkForWaitingObservers();
                    projectList.remove(temp);
                    projectList.add(project);
                    Log.d(TAG,"Project object got updated");
                    updatedList=true;
                    setChanged();
                    notifyObservers();
                    return;
                }else{
                    return;
                }
            }
        }
        Log.d(TAG,"Project object has been added");
        updatedList=true;
        projectList.add(project);
    }

    public  synchronized ArrayList<Project> getProjectList()
    {
        return projectList;
    }

    public  synchronized  ArrayList<String> getProjectListNames(){
        //First let's sort this by colors the green will be first then the red
        sort();
        ArrayList<String> temp=new ArrayList<String>();
        if(getProjectList() ==null)
        {
            return null;
        }
        for(Project project:getProjectList())
        {
            temp.add(project.getProjectName());
        }
        return temp;
    }

    public  synchronized void  notify_Observers2()
    {
        //if(backgroundProcess != null)
        {
            Log.d("Alkamli","notify_Observers2");
            checkForWaitingObservers();
            setChanged();
            notifyObservers();
        }
        /*else{
            Log.d("Alkamli","projectbackgroundProcess != null couldn't notify ");
        }
*/

    }

    public synchronized void stopBackgroundProcess()
    {

        backgroundThread.interrupt();
    }

    private synchronized void sort()
    {
        if(projectList == null)
        {
            return;
        }
        ArrayList<Project> red=new ArrayList<Project>();
        ArrayList<Project> green=new ArrayList<Project>();
        for(Project temp:projectList)
        {
            if(temp.isEnabledState())
            {
                green.add(temp);
                //  Log.e(TAG,"Green : "+temp.getProjectName());
            }else{
                //it's red
                red.add(temp);
                //Log.e(TAG,"Red : "+temp.getProjectName());
            }
        }

        projectList.clear();
        projectList.addAll(green);
        projectList.addAll(red);

    }


    private synchronized void checkForWaitingObservers()
    {
        try{
            if(projectWaitingList != null)
            {
                for(Observer observer:projectWaitingList)
                {
                    addObserver(observer);
                    //Log.e(TAG,"add the Project waiting observers");
                    projectWaitingList.remove(observer);
                }
            }
        }catch(Exception e)
        {
            class Local {};
            Log.e(CommonFunctions.TAG, ("MethodName: " + Local.class.getEnclosingMethod().getName() + " || ErrorMessage: " + e.getMessage()));

        }

    }


    public synchronized void refreshProjectList()
    {
        try {
            synchronized (this) {
                try {
                    if (!CommonFunctions.userLoggedOut(context) && CommonFunctions.isNetworkAvailable(context))
                    {
                        Log.d(TAG, "refreshProjectList Started");
                        //let's fetch the projects first
                        String session = CommonFunctions.getSharedPreferences(context).getString("session", null);
                        if (session == null) {
                            Log.d(TAG, "session==null");
                            return;
                        }
                        GetAllProjects request = new GetAllProjects(session);
                        try {
                            HttpRequestClient client = new HttpRequestClient(context.getString(R.string.get_all_projects_url), request.getJson(request));
                            HttpRequestClientResponse response = client.post();

                            // Log.e(TAG, response.getjson(response));
                            if (response.getHttpStatus() == HttpsURLConnection.HTTP_OK)
                            {
                                //First we make sure the response is a not0
                                if (CommonFunctions.clean(response.getResponseString()).length() > 0)
                                {

                                    // Log.d(TAG, response);
                                    //Let's print the response first
                                    //We convert the response to an array of projects
                                    ObjectMapper objectMapper = new ObjectMapper();
                                    objectMapper.configure(JsonParser.Feature.AUTO_CLOSE_SOURCE, true);
                                    TypeReference<List<Project>> mapType = new TypeReference<List<Project>>() {
                                    };
                                    List<Project> jsonToProjectList = objectMapper.readValue(response.getResponseString(), mapType);
                                    //This will covert the case where a user or all users got deleted after the first fetch
                                    if (jsonToProjectList.isEmpty()==false && jsonToProjectList.equals(getProjectList()) == false)
                                    {
                                        //The size changed so update the whole thing
                                        projectList.clear();
                                       // Log.e(TAG, "The size changed so update the whole thing");
                                    }
                                    for (Project temp : jsonToProjectList)
                                    {
                                        addProjectToList(temp);
                                    }
                                    if (getProjectList() != null && updatedList)
                                    {
                                        checkForWaitingObservers();
                                        setChanged();
                                        notifyObservers();
                                        updatedList = false;
                                    }
                                } else {
                                    //This will covert the case where a user or all users got deleted after the first fetch
                                    projectList.clear();
                                    setChanged();
                                    notifyObservers();
                                   // Log.e(TAG, "The size changed so update the whole thing 1");
                                }

                            } else if (response.getHttpStatus() == HttpsURLConnection.HTTP_UNAUTHORIZED) {
                                //User doesn't have permission to make this request
                                //CommonFunctions.sendToast(context,"Not Authorized");
                                CommonFunctions.sessionExpiredHandler(context, null);
                                return;
                            }
                        } catch (Exception e) {

                            class Local {
                            }
                            ;
                            e(CommonFunctions.TAG, ("MethodName: " + Local.class.getEnclosingMethod().getName() + " || ErrorMessage: " + e.getMessage()));
                        }
                    } else if (CommonFunctions.isNetworkAvailable(context) == false) {
                        //lack internet connection don't send a request
                        Log.d(TAG, "lack internet connection don't send a request");
                        if (HomeActivity.getActivity() != null) {
                            // CommonFunctions.sendToast(HomeActivity.getActivity(), "Error: check internet connection.");
                        }
                    } else {
                        //Stop this Service
                        Log.e(TAG, "Stop this Service");
                        return;
                    }

                } catch (Exception e) {
                    class Local {
                    }
                    ;
                    Log.e(CommonFunctions.TAG, ("MethodName: " + Local.class.getEnclosingMethod().getName() + " || ErrorMessage: " + e.getMessage()));

                }
            }
        }catch(Exception e)
        {
            class Local {
            }
            ;
            Log.e(CommonFunctions.TAG, ("MethodName: " + Local.class.getEnclosingMethod().getName() + " || ErrorMessage: " + e.getMessage()));
        }

    }



    public synchronized void deleteProjectFromList(String projectId)
    {
        try{
            for(Project temp:getProjectList())
            {
                if(temp.getProjectID()==Integer.parseInt(projectId))
                {
                    projectList.remove(temp);
                    return;
                }
            }}catch(Exception e)
        {
            class Local {};
            Log.e(CommonFunctions.TAG, ("MethodName: " + Local.class.getEnclosingMethod().getName() + " || ErrorMessage: " + e.getMessage()));
        }
    }

}
