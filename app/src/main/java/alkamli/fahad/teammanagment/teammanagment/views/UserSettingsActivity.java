package alkamli.fahad.teammanagment.teammanagment.views;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.ConfirmPassword;
import com.mobsandgeeks.saripaar.annotation.Email;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.mobsandgeeks.saripaar.annotation.Password;

import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import alkamli.fahad.teammanagment.teammanagment.CommonFunctions;
import alkamli.fahad.teammanagment.teammanagment.R;
import alkamli.fahad.teammanagment.teammanagment.http_client.HttpRequestClient;
import alkamli.fahad.teammanagment.teammanagment.http_client.HttpRequestClientResponse;
import alkamli.fahad.teammanagment.teammanagment.requests.user.ChangeEmail;
import alkamli.fahad.teammanagment.teammanagment.requests.user.ChangeNickName;
import alkamli.fahad.teammanagment.teammanagment.requests.user.ChangePasswordRequestByUserSession;
import alkamli.fahad.teammanagment.teammanagment.service.Service;
import entity.Task;

import static alkamli.fahad.teammanagment.teammanagment.CommonFunctions.TAG;
import static alkamli.fahad.teammanagment.teammanagment.CommonFunctions.clean;
import static alkamli.fahad.teammanagment.teammanagment.CommonFunctions.sessionExpiredHandler;
import static android.util.Log.e;

public class UserSettingsActivity extends AppCompatActivity{

    EditText nickname,password,newPassword,newPassword2,email,passwordForEmail;
    LinearLayout changePasswordLayout,changeEmailLayout,changeNicknameLayout,buttonsLayout;
    Activity activity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_settings);
        activity=this;
        nickname=(EditText) findViewById(R.id.nickname);
        password=(EditText) findViewById(R.id.password);
        newPassword=(EditText) findViewById(R.id.newPassword);
        newPassword2=(EditText) findViewById(R.id.newPassword2);
        passwordForEmail=(EditText) findViewById(R.id.passwordForEmail);
        changePasswordLayout=(LinearLayout)  findViewById(R.id.changePasswordLayout);
        changeEmailLayout=(LinearLayout)  findViewById(R.id.changeEmailLayout);
        changeNicknameLayout=(LinearLayout)  findViewById(R.id.changeNicknameLayout);

        buttonsLayout=(LinearLayout)  findViewById(R.id.buttonsLayout);
        email=(EditText) findViewById(R.id.email);

        try{
           nickname.setText(CommonFunctions.getSharedPreferences(getApplicationContext()).getString("nickname",""));
           email.setText(CommonFunctions.getSharedPreferences(getApplicationContext()).getString("email",""));
        }catch(Exception e)
        {
            class Local {
            };
            e(CommonFunctions.TAG, ("MethodName: " + Local.class.getEnclosingMethod().getName() + " || ErrorMessage: " + e.getMessage()));
        }

    }

    /*
    These are the buttons that will show the required content to the user. e.g. the change password UI.
     */
    public void showChangePassword(View view)
    {

        //Hide
        changeEmailLayout.setVisibility(View.GONE);
        changeNicknameLayout.setVisibility(View.GONE);
        buttonsLayout.setVisibility(View.GONE);
        //show
        changePasswordLayout.setVisibility(View.VISIBLE);

    }
    public void showChangeEmail(View view)
    {
        //Hide
        changePasswordLayout.setVisibility(View.GONE);
        changeNicknameLayout.setVisibility(View.GONE);
        buttonsLayout.setVisibility(View.GONE);
        //show
        changeEmailLayout.setVisibility(View.VISIBLE);
    }
    public void showChangeNickname(View view)
    {
        //Hide
        changeEmailLayout.setVisibility(View.GONE);
        changePasswordLayout.setVisibility(View.GONE);
        buttonsLayout.setVisibility(View.GONE);
        //show
        changeNicknameLayout.setVisibility(View.VISIBLE);

    }
    /*
    These are the buttons that will take the action for example; validate change password request +send it to the server
     */
    public void changePassword(View view)
    {
        class temp  implements Validator.ValidationListener
        {
            @NotEmpty
            EditText password;
            @Password(min = 1, scheme = Password.Scheme.ANY)
            EditText newPassword;
            @ConfirmPassword
            EditText newPassword2;
            Validator validator;
            public temp(EditText password,EditText newPassword,EditText newPassword2)
            {
                this.password=password;
                this.newPassword=newPassword;
                this.newPassword2=newPassword2;
                validator=new Validator(this);
                validator.setValidationListener(this);
                validator.validate();
            }
            @Override
            public void onValidationSucceeded()
            {
               Runnable run= new Runnable(){
                    @Override
                    public void run() {
                        String session=CommonFunctions.getSharedPreferences(getApplicationContext()).getString("session",null);
                        ChangePasswordRequestByUserSession request=new ChangePasswordRequestByUserSession(password.getText().toString(),newPassword.getText().toString(),newPassword2.getText().toString(),session);
                        HttpRequestClient client=new HttpRequestClient(getApplicationContext().getString(R.string.change_password_by_session_url),request.getJson(request));
                        HttpRequestClientResponse response=client.post();
                        switch (response.getHttpStatus())
                        {
                            case HttpsURLConnection.HTTP_OK:
                            {
                                CommonFunctions.sendToast(activity, getString(R.string.password_has_been_changed_successfully));
                                finish();
                                break;
                            }
                            case HttpsURLConnection.HTTP_UNAUTHORIZED:
                            {
                                CommonFunctions.sendToast(activity, getString(R.string.password_could_not_be_changed)+getString(R.string.old_password_is_not_correct));
                                //sessionExpiredHandler(activity,activity);
                               activity.runOnUiThread(new Runnable(){
                                   @Override
                                   public void run() {
                                       password.setError(getString(R.string.old_password_is_not_correct));
                                   }
                               });
                                break;
                            }
                            default:
                            {
                                CommonFunctions.sendToast(activity, getString(R.string.password_could_not_be_changed));
                            }
                        }
                    }
                };
                new Thread(run).start();
            }

            @Override
            public void onValidationFailed(List<ValidationError> errors)
            {
                try {
                    for (ValidationError temp : errors)
                    {
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

        new temp(password,newPassword,newPassword2);
    }
    public void changeEmail(View view)
    {
        class temp  implements Validator.ValidationListener
        {
            @Email
            EditText email;
            @NotEmpty
            EditText passwordForEmail;
            Validator validator;
            public temp(EditText email,EditText passwordForEmail)
            {
                this.email=email;
                this.passwordForEmail=passwordForEmail;
                validator=new Validator(this);
                validator.setValidationListener(this);
                validator.validate();
            }
            @Override
            public void onValidationSucceeded()
            {
                Runnable run= new Runnable(){
                    @Override
                    public void run() {
                        String session=CommonFunctions.getSharedPreferences(getApplicationContext()).getString("session",null);
                        ChangeEmail request=new ChangeEmail(email.getText().toString(),session,passwordForEmail.getText().toString());
                        HttpRequestClient client=new HttpRequestClient(getApplicationContext().getString(R.string.change_email_url),request.getJson(request));
                        HttpRequestClientResponse response=client.post();
                        switch (response.getHttpStatus())
                        {
                            case HttpsURLConnection.HTTP_OK:
                            {
                                CommonFunctions.sendToast(activity, getString(R.string.email_has_been_changed_successfully));
                                //Don't forget to change the email in the preferences
                               SharedPreferences.Editor editor= CommonFunctions.getEditor(getApplicationContext());
                                editor.putString("email",clean(email.getText().toString()));
                                editor.commit();
                                finish();
                                break;
                            }
                            case HttpsURLConnection.HTTP_UNAUTHORIZED:
                            {
                                CommonFunctions.sendToast(activity, getString(R.string.email_could_not_be_changed)+getString(R.string.password_is_not_correct));
                                //sessionExpiredHandler(activity,activity);
                                activity.runOnUiThread(new Runnable(){
                                    @Override
                                    public void run() {
                                        passwordForEmail.setError(getString(R.string.password_is_not_correct));
                                    }
                                });
                                break;
                            }
                            default:
                            {
                                CommonFunctions.sendToast(activity, getString(R.string.email_could_not_be_changed));
                            }
                        }
                    }
                };
                new Thread(run).start();
            }
            @Override
            public void onValidationFailed(List<ValidationError> errors)
            {
                try {
                    for (ValidationError temp : errors)
                    {
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
        new temp(email,passwordForEmail);
    }
    public void changeNickName(View view)
    {
        class temp  implements Validator.ValidationListener
        {
            @NotEmpty
            EditText nickname;
            Validator validator;
            public temp(EditText nickname)
            {
                this.nickname=nickname;
                validator=new Validator(this);
                validator.setValidationListener(this);
                validator.validate();
            }
            @Override
            public void onValidationSucceeded()
            {
                Runnable run= new Runnable(){
                    @Override
                    public void run() {
                        String session=CommonFunctions.getSharedPreferences(getApplicationContext()).getString("session",null);
                        ChangeNickName request=new ChangeNickName(session,nickname.getText().toString());
                        HttpRequestClient client=new HttpRequestClient(getApplicationContext().getString(R.string.change_nickname_url),request.getJson(request));
                        HttpRequestClientResponse response=client.post();
                        switch (response.getHttpStatus())
                        {
                            case HttpsURLConnection.HTTP_OK:
                            {
                                CommonFunctions.sendToast(activity, getString(R.string.nickname_has_been_changed_successfully));
                                //Don't forget to change the nickname in the preferences
                                SharedPreferences.Editor editor= CommonFunctions.getEditor(getApplicationContext());
                                editor.putString("nickname",(nickname.getText().toString()));
                                if(HomeActivity.getActivity().getSupportActionBar()!= null)
                                {

                           HomeActivity .getActivity().runOnUiThread(new Runnable(){
                               @Override
                               public void run() {
                                   HomeActivity.getActivity().getSupportActionBar().setTitle(getString(R.string.home_title)+" "+nickname.getText().toString());
                               }
                           });
                                }
                                editor.commit();
                                finish();
                                break;
                            }
                            case HttpsURLConnection.HTTP_UNAUTHORIZED:
                            {
                                CommonFunctions.sendToast(activity,getString(R.string.nickname_could_not_be_changed));
                                //sessionExpiredHandler(activity,activity);
                                break;
                            }
                            default:
                            {
                                CommonFunctions.sendToast(activity, getString(R.string.nickname_could_not_be_changed));
                            }
                        }
                    }
                };
                new Thread(run).start();
            }
            @Override
            public void onValidationFailed(List<ValidationError> errors)
            {
                try {
                    for (ValidationError temp : errors)
                    {
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
        new temp(nickname);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch(keyCode){
            case KeyEvent.KEYCODE_BACK:
                // do something here
                if(buttonsLayout.getVisibility()==View.GONE)
                {
                    changeEmailLayout.setVisibility(View.GONE);
                    changeNicknameLayout.setVisibility(View.GONE);
                    changePasswordLayout.setVisibility(View.GONE);
                    buttonsLayout.setVisibility(View.VISIBLE);
                    return false;
                }
        }
        return super.onKeyDown(keyCode, event);
    }


}


