package alkamli.fahad.teammanagment.teammanagment.views;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import java.io.BufferedWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import alkamli.fahad.teammanagment.teammanagment.CommonFunctions;
import alkamli.fahad.teammanagment.teammanagment.R;
import alkamli.fahad.teammanagment.teammanagment.requests.user.ChangePasswordRequestByAdmin;

import static alkamli.fahad.teammanagment.teammanagment.CommonFunctions.TAG;
import static android.util.Log.e;

public class ChangePasswordActivity extends AppCompatActivity {

    String userId;
    EditText password;
    EditText password2;
    ProgressBar progressBar;
    LinearLayout content;
    Activity activity;
    //if the id is valid then this came from an admin account
    //if not then this came from the user to change his/her own account password
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        activity=this;
        password=(EditText) findViewById(R.id.password);
        password2=(EditText) findViewById(R.id.password2);
        progressBar=(ProgressBar) findViewById(R.id.progressBar);
        content=(LinearLayout) findViewById(R.id.content);
        if(getIntent().getStringExtra("id") != null && CommonFunctions.clean(getIntent().getStringExtra("id")).length()>0)
        {
            userId=CommonFunctions.clean(getIntent().getStringExtra("id"));
            Log.d(CommonFunctions.TAG,"User id to change is : "+userId);
        }
    }


    public void submitNewPassword(View view)
    {
        //password not empty
        if(CommonFunctions.clean(password.getText().toString())==null || CommonFunctions.clean(password.getText().toString()).length()<1)
        {
            password.setError(getString(R.string.error_field_required));
            return;
        }
        //matching passwords
        if(!password.getText().toString().equals(password2.getText().toString()))
        {
            password.setError(getString(R.string.passwords_do_not_match));
            password2.setError(getString(R.string.passwords_do_not_match));
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        content.setVisibility(View.GONE);
        new Thread(new Runnable()
        {
            @Override
            public void run() {
                submitChangePasswordRequest(getApplicationContext());
            }
        }).start();
    }


    private void submitChangePasswordRequest(Context context)
    {
        String session=CommonFunctions.getSharedPreferences(context).getString("session",null);

        if(session==null)
        {
            return;
        }

        ChangePasswordRequestByAdmin request=new ChangePasswordRequestByAdmin(CommonFunctions.clean(session),CommonFunctions.clean(userId),password.getText().toString());

        final String jsonString=request.getJson(request);
        Log.d("Alkamli",jsonString);
        URL url;
        String response = "";
        try {
            url = new URL(context.getString(R.string.change_password_by_admin_url));
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
                //Login is successful
                CommonFunctions.sendToast(activity,getString(R.string.password_has_been_changed_successfully));
                Log.d("Alkamli","Password has Been Changed");
                Intent i=new Intent(context,HomeActivity.class);
                startActivity(i);
            }else if(responseCode == HttpsURLConnection.HTTP_UNAUTHORIZED)
            {
                CommonFunctions.sendToast(activity,"Only admin personal are authorized to make this request");
            }
            else {
                response="";
                Log.d("Alkamli",Integer.toString(responseCode));
                //login is not successful
                CommonFunctions.sendToast(activity,getString(R.string.request_failed));


            }
            activity.runOnUiThread(new Runnable()
            {
                @Override
                public void run() {
                    progressBar.setVisibility(View.GONE);
                    content.setVisibility(View.VISIBLE);

                }
            });
        } catch (Exception e) {

            class Local {
            }
            ;
            e(TAG, ("MethodName: " + Local.class.getEnclosingMethod().getName() + " || ErrorMessage: " + e.getMessage()));
        }
    }

}

