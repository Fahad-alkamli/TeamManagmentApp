package alkamli.fahad.teammanagment.teammanagment.requests.user;

import com.fasterxml.jackson.databind.ObjectMapper;
public class UserLoginResponse
{

    private String session,nickname;
    private boolean admin=false;
    public String getSession() {
        return session;
    }
    public void setSession(String session) {
        this.session = session;
    }
    public boolean isAdmin() {
        return admin;
    }
    public void setAdmin(boolean admin) {
        this.admin = admin;
    }
    public UserLoginResponse(String session, boolean admin,String nickname) {
        this.session = session;
        this.admin = admin;
        this.nickname=nickname;
    }
    public UserLoginResponse() {

    }

    public String getJson(UserLoginResponse request)
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
    public String getNickname() {
        return nickname;
    }
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }



}
