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
import alkamli.fahad.teammanagment.teammanagment.http_client.HttpRequestClient;
import alkamli.fahad.teammanagment.teammanagment.http_client.HttpRequestClientResponse;
import alkamli.fahad.teammanagment.teammanagment.requests.user.ChangePasswordRequestByToken;
import alkamli.fahad.teammanagment.teammanagment.requests.user.ResetPasswordRequest;

import static alkamli.fahad.teammanagment.teammanagment.CommonFunctions.TAG;
import static android.util.Log.e;
import static alkamli.fahad.teammanagment.teammanagment.CommonFunctions.clean;

public class ForgotPasswordActivity extends AppCompatActivity {

    EditText emailEditText;
    LinearLayout content1;
    LinearLayout content2;
    ProgressBar progressBar;
    EditText token;
    EditText password;
    EditText password2;
    Activity activity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        activity=this;
        content1=(LinearLayout)  findViewById(R.id.content1);
        content2=(LinearLayout)  findViewById(R.id.content2);
        emailEditText=(EditText) findViewById(R.id.emailEditText);
        token=(EditText) findViewById(R.id.token);
        password=(EditText) findViewById(R.id.password);
        password2=(EditText) findViewById(R.id.password2);
        progressBar=(ProgressBar) findViewById(R.id.progressBar);
       if( getIntent().getStringExtra("email")!=null)
       {
           emailEditText.setText(CommonFunctions.clean(getIntent().getStringExtra("email")));
       }
    }



    public void submitNewPassword(View view)
    {

        //validate fields

        //Not empty
        if(CommonFunctions.clean(token.getText().toString())==null || CommonFunctions.clean(token.getText().toString()).length()<1)
        {
            token.setError(getString(R.string.error_field_required));
            return;
        }
        //need to be six digits
        if(CommonFunctions.clean(token.getText().toString()).length()!=6)
        {
            token.setError(getString(R.string.token_needs_to_be_exactly_6_digits));
            return;
        }
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
        new Thread(new Runnable(){
            @Override
            public void run() {
                submitChangePasswordRequest(getApplicationContext());
            }
        }).start();

    }

    public void askForToken(View view)
    {
        //validate fields
        if(CommonFunctions.clean(emailEditText.getText().toString()).length()<1 || !emailEditText.getText().toString().contains("@"))
        {
            if(!emailEditText.getText().toString().contains("@"))
            {
                emailEditText.setError(getString(R.string.not_a_valid_email));
            }else{
                emailEditText.setError(getString(R.string.error_field_required));
            }

            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        new Thread(new Runnable()
        {
            @Override
            public void run() {
                submitForgotPasswordRequest(getApplicationContext());
            }
        }).start();

    }


    public void alreadyHasToken(View view)
    {
        content1.setVisibility(View.GONE);
        content2.setVisibility(View.VISIBLE);
    }

    private void submitForgotPasswordRequest(Context context)
    {
        ResetPasswordRequest request=new ResetPasswordRequest(emailEditText.getText().toString());

        final String jsonString=request.getJson(request);
        Log.d("Alkamli",jsonString);
        URL url;
        String response = "";
        try {
            url = new URL(context.getString(R.string.forgot_password_url));
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
                CommonFunctions.sendToast(activity,getString(R.string.token_has_been_sent_to_your_email));
                Log.d("Alkamli","Token has been sent to your email");
                activity.runOnUiThread(new Runnable(){
                    @Override
                    public void run() {
                        progressBar.setVisibility(View.GONE);
                        content1.setVisibility(View.GONE);
                        content2.setVisibility(View.VISIBLE);
                    }
                });
            }else if(responseCode == HttpsURLConnection.HTTP_UNAUTHORIZED)
            {
                //User doesn't have permission to make this request
                activity.runOnUiThread(new Runnable(){
                    @Override
                    public void run() {
                        progressBar.setVisibility(View.GONE);
                        content1.setVisibility(View.VISIBLE);
                        content2.setVisibility(View.GONE);
                    }
                });
                CommonFunctions.sendToast(activity,getString(R.string.email_does_not_exists));
                emailEditText.setError("Email doesn't exists");

            }
            else {
                response="";
                Log.d("Alkamli",Integer.toString(responseCode));
                activity.runOnUiThread(new Runnable(){
                    @Override
                    public void run() {
                        progressBar.setVisibility(View.GONE);
                        content1.setVisibility(View.VISIBLE);
                        content2.setVisibility(View.GONE);
                    }
                });
                //login is not successful
                CommonFunctions.sendToast(activity,getString(R.string.request_failed));


            }
        } catch (Exception e) {

            class Local {
            }
            ;
            e(TAG, ("MethodName: " + Local.class.getEnclosingMethod().getName() + " || ErrorMessage: " + e.getMessage()));
        }
    }


    private void submitChangePasswordRequest(Context context)
    {

        ChangePasswordRequestByToken request=new ChangePasswordRequestByToken(clean(token.getText().toString()),password.getText().toString(),clean(emailEditText.getText().toString()));


        Log.d("Alkamli",request.getJson(request));
        try {
            HttpRequestClient client=new HttpRequestClient(context.getString(R.string.change_password_url),request.getJson(request));

          HttpRequestClientResponse response= client.post();

            int responseCode=response.getHttpStatus();

            if (responseCode == HttpsURLConnection.HTTP_OK)
            {
                //Login is successful
                CommonFunctions.sendToast(activity,getString(R.string.password_has_been_changed_successfully));
                Log.d("Alkamli","Password has Been Changed");
                Intent i=new Intent(context,LoginActivity.class);
                startActivity(i);
            }else if(responseCode == HttpsURLConnection.HTTP_UNAUTHORIZED)
            {
                Log.d("Alkamli",response.getjson(response));
                activity.runOnUiThread(new Runnable()
                {
                    @Override
                    public void run() {
                        progressBar.setVisibility(View.GONE);
                        content2.setVisibility(View.VISIBLE);
                        token.setError(getString(R.string.wrong_token));

                    }
                });
                CommonFunctions.sendToast(activity,getString(R.string.wrong_token));
            }
            else {
               Log.d("Alkamli",response.getjson(response));
                activity.runOnUiThread(new Runnable(){
                    @Override
                    public void run() {
                        progressBar.setVisibility(View.GONE);
                        content1.setVisibility(View.VISIBLE);
                        content2.setVisibility(View.GONE);
                    }
                });
                //login is not successful
                CommonFunctions.sendToast(activity,getString(R.string.not_authorized));
            }
        } catch (Exception e) {

            class Local {
            }
            ;
            e(TAG, ("MethodName: " + Local.class.getEnclosingMethod().getName() + " || ErrorMessage: " + e.getMessage()));
        }
    }

}
