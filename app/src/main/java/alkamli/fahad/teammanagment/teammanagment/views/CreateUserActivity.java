package alkamli.fahad.teammanagment.teammanagment.views;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.Email;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import alkamli.fahad.teammanagment.teammanagment.R;
import alkamli.fahad.teammanagment.teammanagment.CommonFunctions;
import alkamli.fahad.teammanagment.teammanagment.requests.user.CreateUserRequest;
import alkamli.fahad.teammanagment.teammanagment.service.Service;
import entity.User;

import static alkamli.fahad.teammanagment.teammanagment.CommonFunctions.TAG;
import static android.util.Log.e;

public class CreateUserActivity extends AppCompatActivity implements Validator.ValidationListener{

    Activity activity;
    View login_progress;
    @Email
    EditText email;
    @NotEmpty
    EditText password;
    View registerContent;
    EditText nickname;
    CheckBox adminCheckBox;
    Validator validator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(!CommonFunctions.checkForValidLoginSession(this))
        {
            CommonFunctions.sendToast(this,"Please login again.");
            Intent i=new Intent(this,LoginActivity.class);
            startActivity(i);
            finish();

        }
        setContentView(R.layout.activity_create_user);
        validator=new Validator(this);
        validator.setValidationListener(this);
        login_progress=findViewById(R.id.login_progress);
        email=((EditText)findViewById(R.id.email));
        password=((EditText)findViewById(R.id.password));
        nickname=((EditText)findViewById(R.id.nickname));
        registerContent=findViewById(R.id.registerContent);
        registerContent.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View view)
            {

            }
        });
        activity=this;
        adminCheckBox=(CheckBox) findViewById(R.id.adminCheckBox);
    }


    public void createUser(View view)
    {
        boolean go=true;
        validator.validate(true);
        return;
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
                    registerContent.setVisibility(View.INVISIBLE);
                    login_progress.setVisibility(View.VISIBLE);
                }else{
                    //show
                    //Hide the view
                    registerContent.setVisibility(View.VISIBLE);
                    login_progress.setVisibility(View.GONE);
                }

            }
        });
    }

    private void createUserRequest(Context context)
    {
        String session=CommonFunctions.getSharedPreferences(context).getString("session",null);

        CreateUserRequest request=new CreateUserRequest(nickname.getText().toString(),email.getText().toString(),password.getText().toString(),session,adminCheckBox.isChecked());

        final String jsonString=request.getJson(request);
        Log.d("Alkamli",jsonString);
        URL url;
        String response = "";
        try {
            url = new URL(context.getString(R.string.create_user_url));
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

            if (responseCode == HttpsURLConnection.HTTP_CREATED)
            {
                String line;
                BufferedReader br=new BufferedReader(new InputStreamReader(conn.getInputStream()));
                while ((line=br.readLine()) != null)
                {
                    response+=line;

                }
                //First we make sure the response is a session
                if(CommonFunctions.clean(response).length()<0)
                {
                    CommonFunctions.sendToast(activity,getString(R.string.user_creation_was_not_successful));
                    hide(false);

                    return;
                }
                //get the response and add the user to the list
                ObjectMapper objectMapper = new ObjectMapper();
                User user = objectMapper.readValue(response, User.class);
                Service.addUserToList(user);
                CommonFunctions.sendToast(activity,getString(R.string.user_creation_is_successful));
                Log.d("Alkamli","user creation  is Successful");
                Intent i=new Intent(this,HomeActivity.class);
                startActivity(i);
                finish();
            }else if(responseCode == HttpsURLConnection.HTTP_UNAUTHORIZED)
            {
                //User doesn't have permission to make this request
                CommonFunctions.sendToast(activity,getString(R.string.not_authorized));
                CommonFunctions.sessionExpiredHandler(activity,activity);
            }
            else {
                response="";
                Log.d("Alkamli",Integer.toString(responseCode));
                //login is not successful
                CommonFunctions.sendToast(activity,getString(R.string.user_creation_was_not_successful));

                hide(false);

            }
        } catch (Exception e) {

            class Local {
            }
            ;
            Log.e(TAG, ("MethodName: " + Local.class.getEnclosingMethod().getName() + " || ErrorMessage: " + e.getMessage()));
        }

    }


    @Override
    public void onValidationSucceeded() {

        activity.runOnUiThread(new Runnable()
        {
            @Override
            public void run() {
                hide(true);
            }
        });

        Runnable run=new Runnable()
        {
            @Override
            public void run() {
                createUserRequest(activity);
            }
        };

        new Thread(run).start();
    }

    @Override
    public void onValidationFailed(List<ValidationError> errors) {
        try {
            for (ValidationError temp : errors) {
                if (temp.getView() instanceof EditText)
                {
                    ((EditText) temp.getView()).setError(temp.getCollatedErrorMessage(getApplicationContext()));
                } else if (temp.getView() instanceof TextView)
                {
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


}
