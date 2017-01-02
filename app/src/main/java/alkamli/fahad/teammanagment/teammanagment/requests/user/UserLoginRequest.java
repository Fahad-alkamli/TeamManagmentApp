package alkamli.fahad.teammanagment.teammanagment.requests.user;

import android.util.Log;

import com.fasterxml.jackson.databind.ObjectMapper;

import alkamli.fahad.teammanagment.teammanagment.CommonFunctions;

import static alkamli.fahad.teammanagment.teammanagment.CommonFunctions.TAG;
import static android.util.Log.e;

public class UserLoginRequest {

    private String Email,Password;


    public UserLoginRequest( String email, String password)
    {
        this.Email = CommonFunctions.clean(email);
        this.Password = password;

    }


    public UserLoginRequest() {}


    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }
    public String getJson(UserLoginRequest request)
    {
            try{
                ObjectMapper mapper = new ObjectMapper();

                String jsonInString = mapper.writeValueAsString(request);
                return jsonInString;

            }catch(Exception e) {

                class Local {
                }
                ;
                e(TAG, ("MethodName: " + Local.class.getEnclosingMethod().getName() + " || ErrorMessage: " + e.getMessage()));
            }
            return null;
        }

}
