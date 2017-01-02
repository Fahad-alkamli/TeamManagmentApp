package alkamli.fahad.teammanagment.teammanagment.requests.user;

import com.fasterxml.jackson.databind.ObjectMapper;

import alkamli.fahad.teammanagment.teammanagment.CommonFunctions;



public class ChangePasswordRequestByToken {


    private	String token,password,email;

    public ChangePasswordRequestByToken() {
    }

    public ChangePasswordRequestByToken(String token, String password,String email) {
        super();
        this.token = token;
        this.password = password;
        this.email=email;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        if(CommonFunctions.clean(email) ==null || CommonFunctions.clean(email).length()<1 )
        {
            return;
        }
        this.email = email;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        if(CommonFunctions.clean(token) ==null || CommonFunctions.clean(token).length()<1 )
        {
            return;
        }
        this.token = CommonFunctions.clean(token);
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        if(CommonFunctions.clean(password) ==null || CommonFunctions.clean(password).length()<1 )
        {
            return;
        }
        this.password = password;
    }



    public String getJson(ChangePasswordRequestByToken request)
    {
        try{
            ObjectMapper mapper = new ObjectMapper();

            String jsonInString = mapper.writeValueAsString(request);
            return jsonInString;

        }catch(Exception e)
        {
            System.out.println(e.getMessage());

        }
        return null;
    }

}
