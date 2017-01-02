package alkamli.fahad.teammanagment.teammanagment.requests.user;

import com.fasterxml.jackson.databind.ObjectMapper;

import alkamli.fahad.teammanagment.teammanagment.CommonFunctions;

/**
 * Created by d0l1 on 11-27-2016.
 */

public class ResetPasswordRequest {

    private String email;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        if(CommonFunctions.clean(email)==null || CommonFunctions.clean(email).length()<1)
        {
            return;

        }
        this.email = email;
    }

    public ResetPasswordRequest(String email) {
        super();
        this.email = email;
    }
    public ResetPasswordRequest() {
    }


    public String getJson(ResetPasswordRequest request)
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
