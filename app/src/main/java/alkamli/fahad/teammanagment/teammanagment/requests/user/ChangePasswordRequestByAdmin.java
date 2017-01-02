package alkamli.fahad.teammanagment.teammanagment.requests.user;

import com.fasterxml.jackson.databind.ObjectMapper;

import alkamli.fahad.teammanagment.teammanagment.CommonFunctions;



public class ChangePasswordRequestByAdmin {

    private String AdminSession,userId,newPassword;




    public ChangePasswordRequestByAdmin(String adminSession, String userId, String newPassword) {
        super();
        AdminSession = adminSession;
        this.userId = userId;
        this.newPassword = newPassword;
    }


    public String getNewPassword() {
        return newPassword;
    }


    public void setNewPassword(String newPassword) {

        if(CommonFunctions.clean(newPassword)==null || CommonFunctions.clean(newPassword).length()<1)
        {
            return;
        }
        this.newPassword = newPassword;
    }


    public ChangePasswordRequestByAdmin() {
        super();
        // TODO Auto-generated constructor stub
    }


    public String getAdminSession() {
        return AdminSession;
    }

    public void setAdminSession(String adminSession) {
        if(CommonFunctions.clean(adminSession)==null || CommonFunctions.clean(adminSession).length()<1)
        {
            return;
        }
        AdminSession = adminSession;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        if(CommonFunctions.clean(userId)==null || CommonFunctions.clean(userId).length()<1)
        {
            return;
        }
        this.userId = userId;
    }

    public String getJson(ChangePasswordRequestByAdmin request)
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
