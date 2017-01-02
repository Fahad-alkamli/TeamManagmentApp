package alkamli.fahad.teammanagment.teammanagment.views;


import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mobsandgeeks.saripaar.annotation.Email;
import com.mobsandgeeks.saripaar.annotation.Password;

import javax.net.ssl.HttpsURLConnection;

import alkamli.fahad.teammanagment.teammanagment.CommonFunctions;
import alkamli.fahad.teammanagment.teammanagment.R;
import alkamli.fahad.teammanagment.teammanagment.http_client.HttpRequestClient;
import alkamli.fahad.teammanagment.teammanagment.http_client.HttpRequestClientResponse;
import alkamli.fahad.teammanagment.teammanagment.requests.Response;
import alkamli.fahad.teammanagment.teammanagment.requests.user.UserLoginRequest;
import alkamli.fahad.teammanagment.teammanagment.requests.user.UserLoginResponse;

import static android.util.Log.e;


public class LoginActivity extends AppCompatActivity{

    final String TAG="Alkamli";
    View login_form;
    View login_progress;
    EditText email;
    EditText password;
    boolean hidePassword=true;
    static Activity activity;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        if(CommonFunctions.checkForValidLoginSession(this))
        {
            //The user has a valid session therefor will go to home directly without the need to sign in again
            Intent i=new Intent(this,HomeActivity.class);
            startActivity(i);
            finish();
        }
        login_form=findViewById(R.id.login_form);
        login_progress=findViewById(R.id.login_progress);
        email=((EditText)findViewById(R.id.email));
        password=((EditText)findViewById(R.id.password));

        activity=this;

        findViewById(R.id.showPasswordButton).setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {
                switch ( event.getAction() ) {
                    case MotionEvent.ACTION_DOWN:
                        if(!hidePassword)
                        {
                            password.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD | InputType.TYPE_CLASS_TEXT);
                            Log.d("Alkamli","Hide password");
                            hidePassword=true;
                        }else{
                            password.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                            Log.d("Alkamli","Show password");
                            hidePassword=false;
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        break;
                }
                return true;
            }
        });
    }

    public void login(View view)
    {

        boolean go=true;
        Log.d(TAG,"Login");
       if(CommonFunctions.clean(email.getText().toString()).length()<1  )
       {
           email.setError(getString(R.string.error_field_required));
            go=false;
       }

        if(!CommonFunctions.clean( email.getText().toString()).contains("@") )
        {
            email.setError(getString(R.string.not_a_valid_email));
            go=false;
        }


        if(CommonFunctions.clean( password.getText().toString()).length()<1)
        {
            password.setError(getString(R.string.error_field_required));
            go=false;
        }
        if(!go)
        {
            return;
        }


        //Hide the form
        hide(true);

        Runnable run=new Runnable()
        {
            @Override
            public void run() {
                checkCredentials();
            }
        };
        new Thread(run).start();

    }



    private void checkCredentials()
    {
        UserLoginRequest loginRequest=new UserLoginRequest(email.getText().toString().trim(),password.getText().toString());

        String jsonString=loginRequest.getJson(loginRequest);
        Log.d("Alkamli",loginRequest.getJson(loginRequest));

       //Login process
        try {
            HttpRequestClient client=new HttpRequestClient(getString(R.string.user_login_url),jsonString);

            HttpRequestClientResponse response=client.post();
            int responseCode=response.getHttpStatus();

           // Log.e(TAG,response.getjson(response));
            if (responseCode == HttpsURLConnection.HTTP_OK)
            {
                //First we make sure the response is a session
                if(CommonFunctions.clean(response.getResponseString()).length()<0)
                {
                    CommonFunctions.sendToast(activity,getString(R.string.login_was_not_successful));
                    hide(false);
                    return;
                }
                //Login is successful
                CommonFunctions.sendToast(activity,getString(R.string.login_is_successful));

                Log.d("Alkamli","Login is Successful");
                Intent i=new Intent(this,HomeActivity.class);
                startActivity(i);
                ObjectMapper mapper = new ObjectMapper();
                UserLoginResponse responseObject=mapper.readValue(response.getResponseString(), UserLoginResponse.class);
                SharedPreferences.Editor editor= CommonFunctions.getEditor(getApplicationContext());
                editor.putString("session",CommonFunctions.clean(responseObject.getSession()));
                editor.putBoolean("admin",responseObject.isAdmin());
                editor.putString("nickname",responseObject.getNickname());
                editor.putString("email",CommonFunctions.clean(email.getText().toString()));
                editor.commit();
                Log.d("Alkamli","Session: "+response);
                finish();
            }
            else {
                Log.d("Alkamli",Integer.toString(responseCode));
                try{

                    if(CommonFunctions.clean(response.getResponseString())!= null && response.getResponseString().length()>0)
                    {
                        CommonFunctions.sendToast(activity,response.getResponseString());
                        hide(false);
                        return;
                    }
                }catch(Exception e)
                {

                    Log.e(TAG,e.getMessage());
                }
                //login is not successful
                CommonFunctions.sendToast(activity,getString(R.string.login_was_not_successful));

                hide(false);

            }
        } catch (Exception e) {

            class Local {
            }
            ;
            e(CommonFunctions.TAG, ("MethodName: " + Local.class.getEnclosingMethod().getName() + " || ErrorMessage: " + e.getMessage()));
        }

    }

    private void hide(final boolean yes)
    {
        activity.runOnUiThread(new Runnable()
        {
            @Override
            public void run() {
                if(yes)
                {
                    //Hide the view
                    login_form.setVisibility(View.INVISIBLE);
                    login_progress.setVisibility(View.VISIBLE);
                }else{
                    //show
                    //Hide the view
                    login_form.setVisibility(View.VISIBLE);
                    login_progress.setVisibility(View.GONE);
                }

            }
        });
    }


    public void forgotPassword(View view)
    {
        Intent i=new Intent(this,ForgotPasswordActivity.class);
        if(email.getText()!= null && CommonFunctions.clean(email.getText().toString()).length()>0)
        {
            i.putExtra("email",CommonFunctions.clean(email.getText().toString()));
        }
        startActivity(i);
        Log.d(TAG,"Test");
    }


}

