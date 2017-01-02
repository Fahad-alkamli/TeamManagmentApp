package alkamli.fahad.teammanagment.teammanagment.requests.user;


import com.fasterxml.jackson.databind.ObjectMapper;

import alkamli.fahad.teammanagment.teammanagment.CommonFunctions;

public class ChangeEmail {

    private String session,password;
    private String email;

    public ChangeEmail() {
        super();
    }

    public ChangeEmail(String email, String session,String password)
    {
        super();
        this.email = email;
        this.session = session;
        this.password=password;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = CommonFunctions.clean(email);
    }

    public String getSession() {
        return session;
    }

    public void setSession(String session) {
        this.session =  CommonFunctions.clean(session);
    }

    public String getJson(ChangeEmail request)
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
