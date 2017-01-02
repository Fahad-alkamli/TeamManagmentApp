package alkamli.fahad.teammanagment.teammanagment.service;

import android.content.Context;
import android.util.Log;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URL;
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
import entity.User;

import static alkamli.fahad.teammanagment.teammanagment.CommonFunctions.waitingTime;


public class UserBackgroundProcess extends Observable {
    ArrayList<User> userList =new ArrayList<User>();
    final String TAG="Alkamli";


    private Thread backgroundThread;
    //private  UserBackgroundProcess backgroundProcess;
    ArrayList<Observer> userWaitingList=null;
    private boolean dataChanged=false;

    Context context;
    public UserBackgroundProcess(Context context,ArrayList<Observer> userWaitingList) {
        this.context=context;
        this.userWaitingList=userWaitingList;
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
        Log.d(TAG,"User backgroundProcess Started");
        do{
            synchronized (this)
            {
                try {
                    wait(1000);
                    if(context != null)
                    {
                        refreshUserList();
                    }
                    //Send the request every 1 minute wait(60000);
                    wait(waitingTime);
                    Log.d(TAG, "Finished stopping");

                }catch(InterruptedException e)
                {
                    Log.e(TAG,"User Background service has been Interrupted");
                    Thread.currentThread().interrupt();
                    // handle the interrupt
                    return;
                } catch (Exception e)
                {

                    class Local {};
                    Log.e(CommonFunctions.TAG, ("MethodName: " + Local.class.getEnclosingMethod().getName() + " || ErrorMessage: " + e.getMessage()));

                }
            }
        }while(true);


    }

    public synchronized void addUserToList(User user)
    {
        boolean add=true;
        for(User temp: userList)
        {
            if(temp.getId()==user.getId())
            {
                //check if this is equal or not
                if(temp.equals(user)==false)
                {
                    //update the user
                    checkForWaitingObservers();
                    userList.remove(temp);
                    userList.add(user);
                    setChanged();
                    notifyObservers();
                    return ;
                }
                add=false;
                break;
            }
        }
        if(add)
        {
            Log.d(TAG,"user has been added to the list");
            //I will notify the observers with each change
            userList.add(user);
            Log.d(TAG,user.getJson(user));
            dataChanged=true;
        }
    }

    public  synchronized ArrayList<User> getUserList()
    {
        return userList;
    }

    public  synchronized  ArrayList<String> getUsersListNames(){
        ArrayList<String> temp=new ArrayList<String>();


        if(getUserList() ==null)
        {
            return null;
        }
        for(User project: getUserList())
        {
            temp.add(project.getName());
        }
        Log.d("Alkamli","getUsersListNames got invoked");
        return temp;
    }

    public  synchronized void  notify_Observers2()
    {

            Log.d("Alkamli","notify_Observers2");
            checkForWaitingObservers();
            setChanged();
           notifyObservers();

    }

    public synchronized void stopBackgroundProcess()
    {
        backgroundThread.interrupt();
    }



    private synchronized void checkForWaitingObservers()
    {
        try{

            for(Observer observer:userWaitingList)
            {
                addObserver(observer);
                // Log.e(TAG,"add the User waiting observers");
                userWaitingList.remove(observer);
            }

        }catch(Exception e)
        {
            class Local {};
            Log.e(CommonFunctions.TAG, ("MethodName: " + Local.class.getEnclosingMethod().getName() + " || ErrorMessage: " + e.getMessage()));

        }

    }


    public synchronized void deleteUser(int userId)
    {

        if(userList != null && userList.isEmpty()==false)
        {

            for(User user:userList)
            {
                if(user.getId()==userId)
                {
                    userList.remove(user);
                    setChanged();
                    notifyObservers();
                }
            }
        }

    }


    public synchronized void refreshUserList()
    {
        try {
            if(!CommonFunctions.userLoggedOut(context) && CommonFunctions.isNetworkAvailable(context) )
            {
                //let's fetch the projects first
                String session = CommonFunctions.getSharedPreferences(context).getString("session", null);
                if (session == null) {
                    Log.d(TAG, "session==null");
                    return;
                }
                GetAllProjects request = new GetAllProjects(session);
                HttpRequestClient client=new HttpRequestClient(context.getString(R.string.get_all_users_url),request.getJson(request));
                URL url;
                HttpRequestClientResponse response = client.post();
                try {
                    if (response.getHttpStatus() == HttpsURLConnection.HTTP_OK)
                    {
                        //First we make sure the response is not zero
                        if (CommonFunctions.clean(response.getResponseString()).length() > 0)
                        {
                            ObjectMapper objectMapper = new ObjectMapper();
                            TypeReference<List<User>> mapType = new TypeReference<List<User>>() {
                            };
                            List<User> jsonToProjectList = objectMapper.readValue(response.getResponseString(), mapType);
                            if (jsonToProjectList.size() > 0)
                            {
                                //Check if this list is an equal to the previous list
                                if(jsonToProjectList.equals(getUserList())==false)
                                {
                                    userList.clear();
                                }
                                for (User temp : jsonToProjectList)
                                {
                                    addUserToList(temp);
                                }

                                if(dataChanged)
                                {
                                    checkForWaitingObservers();
                                    dataChanged=false;
                                    setChanged();
                                    notifyObservers();
                                }

                            }
                        }else{
                            userList.clear();
                            setChanged();
                            notifyObservers();
                        }
                    } else if (response.getHttpStatus() == HttpsURLConnection.HTTP_UNAUTHORIZED)
                    {
                        CommonFunctions.sessionExpiredHandler(context,null);
                        return;
                    }
                } catch (Exception e) {

                    class Local {
                    }
                    ;
                    Log.e(CommonFunctions.TAG, ("MethodName: " + Local.class.getEnclosingMethod().getName() + " || ErrorMessage: " + e.getMessage()));
                }
            }else if (!CommonFunctions.isNetworkAvailable(context))
            {
                //no internet connection
                if(HomeActivity.getActivity()!= null)
                {
                   // CommonFunctions.sendToast(HomeActivity.getActivity(),"Error: Check internet connection");
                    return;
                }
            }else{
                //Stop the service
                return ;
            }
        }catch (Exception e)
        {
            class Local {};
            Log.e(CommonFunctions.TAG, ("MethodName: " + Local.class.getEnclosingMethod().getName() + " || ErrorMessage: " + e.getMessage()));

        }
    }
}

