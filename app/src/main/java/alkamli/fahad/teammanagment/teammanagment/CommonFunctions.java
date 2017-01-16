package alkamli.fahad.teammanagment.teammanagment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import alkamli.fahad.teammanagment.teammanagment.requests.user.LogoutUserRequest;
import alkamli.fahad.teammanagment.teammanagment.service.Service;
import alkamli.fahad.teammanagment.teammanagment.views.HomeActivity;
import alkamli.fahad.teammanagment.teammanagment.views.LoginActivity;

import static android.content.Context.MODE_PRIVATE;
import static android.util.Log.e;

public class CommonFunctions {

    public final static String TAG="Alkamli";
    public static final String datePattern="(^(((0[1-9]|1[0-9]|2[0-8])[\\/](0[1-9]|1[012]))|((29|30|31)[\\/](0[13578]|1[02]))|((29|30)[\\/](0[4,6,9]|11)))[\\/](19|[2-9][0-9])\\d\\d$)|(^29[\\/]02[\\/](19|[2-9][0-9])(00|04|08|12|16|20|24|28|32|36|40|44|48|52|56|60|64|68|72|76|80|84|88|92|96)$)";

    public static final String doublePattern="^\\d{1,}\\.?\\d{1,}?$";

    public static final int waitingTime=60000;
    /**
     This method will clean the string from any spaces.
     @param temp The string to clean
     @return The string without any spaces
     */
    public static String clean(String temp)
    {
        try {
            return temp.trim().replace(" ", "");
        }catch(Exception e) {
            class Local {}; Log.e(CommonFunctions.TAG,("MethodName: "+Local.class.getEnclosingMethod().getName()+" || ErrorMessage: "+e.getMessage()));

        }
        return null;
    }

    public static void sendToast(final Activity activity, final String message)
    {
        if(activity==null)
        {
            return;
        }
        activity.runOnUiThread(new Runnable()
        {
            @Override
            public void run() {
                Toast.makeText(activity,message,Toast.LENGTH_LONG).show();
            }
        });

    }

    public static SharedPreferences.Editor getEditor(Context context)
    {

        SharedPreferences.Editor editor=context.getApplicationContext().getSharedPreferences(context.getString(R.string.app_name),MODE_PRIVATE).edit();

        return editor;
    }

    public static SharedPreferences getSharedPreferences(Context context)
    {

        return context.getSharedPreferences(context.getString(R.string.app_name),MODE_PRIVATE);
    }


    public static void sessionExpiredHandler(final Context context,Activity activity)
    {
        try {
            //Do a logout and send a toast telling the user that he/she needs to login because his session expired
            Log.e(TAG, "sessionExpiredHandler");
            final String tempSession = getSharedPreferences(context).getString("session", null);
            if (tempSession != null) {
                getEditor(context).clear().commit();
                logout(tempSession, context);
            }

            if(activity != null)
            {
                Intent i=new Intent(context,LoginActivity.class);
                activity.startActivity(i);
                activity.finish();
            }
            else if(HomeActivity.getActivity() != null)
            {
                Intent i=new Intent(context,LoginActivity.class);
                HomeActivity.getActivity().startActivity(i);
                HomeActivity.getActivity().finish();
            }

            //      Toast.makeText(context,"Error in session, please try to logout and login again",Toast.LENGTH_LONG).show();
        }catch(Exception e)
        {
            class Local {}; Log.e(CommonFunctions.TAG,("MethodName: "+Local.class.getEnclosingMethod().getName()+" || ErrorMessage: "+e.getMessage()));
        }


    }


    public static void logout(String tempSession,Context context)
    {

        //Send the logout request to the server to clean the session
        //remove the session

        if(tempSession == null)
        {
            //we don't have a session to begin with
            return;
        }
        LogoutUserRequest logoutRequest=new LogoutUserRequest(tempSession);

        URL url;
        String response = "";
        try {
            url = new URL(context.getString(R.string.user_logout_url));
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
            writer.write(logoutRequest.getJson(logoutRequest));
            writer.flush();
            writer.close();
            os.close();
            int responseCode=conn.getResponseCode();

            if (responseCode == HttpsURLConnection.HTTP_OK)
            {

                Log.d("Alkamli","Logout was successful");
            }
        } catch (Exception e) {

            class Local {}; Log.e(CommonFunctions.TAG,("MethodName: "+Local.class.getEnclosingMethod().getName()+" || ErrorMessage: "+e.getMessage()));
        }



        //Stop the service
        Log.i("Alkamli","logout trying to Stop the service");
        Service.stopService();





    }


    public static boolean userLoggedOut(Context context)
    {
        try {
            if (getSharedPreferences(context).getString("session", null) != null)
            {
                return false;
            }
        }catch(Exception e)
        {
            class Local {}; Log.e(CommonFunctions.TAG,("MethodName: "+Local.class.getEnclosingMethod().getName()+" || ErrorMessage: "+e.getMessage()));

        }
        return true;
    }


    public static boolean isNetworkAvailable(Context context)
    {
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            if (activeNetworkInfo != null) {
                return activeNetworkInfo.isConnected();
            }
        }catch(Exception e)
        {
            class Local {}; Log.e(CommonFunctions.TAG,("MethodName: "+Local.class.getEnclosingMethod().getName()+" || ErrorMessage: "+e.getMessage()));
        }
        return false;
    }

    public static boolean checkForValidLoginSession(Context context)
    {

        if(CommonFunctions.getSharedPreferences(context).getString("session",null) != null)
        {
            return true;
        }
        return false;
    }



    //Compare two dates and make sure that the startdate comes after the enddate
    public static boolean compareDates(String startDate,String endDate)
    {
        try {
            int day = Integer.parseInt(startDate.split("/")[0]);
            int month = Integer.parseInt(startDate.split("/")[1]);
            int year = Integer.parseInt(startDate.split("/")[2]);

            int day2 = Integer.parseInt(endDate.split("/")[0]);
            int month2 = Integer.parseInt(endDate.split("/")[1]);
            int year2 = Integer.parseInt(endDate.split("/")[2]);

            if(year<year2)
            {
                return true;
            }else if(year==year2)
            {
                //Here we have a date in the same year we need to compare the month and the day
                if(month<month2)
                {
                    return true;
                }else if(month==month2)
                {
                    //Here we have a date in the same month we need to check the day
                    if(day<day2)
                    {
                        return true;
                    }
                }
            }
        }catch(Exception e)
        {
            Log.e(TAG,e.getMessage());
        }

        return false;
    }


}
