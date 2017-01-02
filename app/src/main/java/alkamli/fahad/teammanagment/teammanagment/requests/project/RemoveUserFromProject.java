package alkamli.fahad.teammanagment.teammanagment.requests.project;


import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;

import alkamli.fahad.teammanagment.teammanagment.CommonFunctions;

public class RemoveUserFromProject {

    private String AdminSession,projectId;

    private ArrayList<String>  UsersId;

    public String getAdminSession() {
        return AdminSession;
    }

    public void setAdminSession(String adminSession) {
        if(CommonFunctions.clean(adminSession).length()<1)
        {
            //	System.out.println("memberId  size1: "+CommonFunctions.clean(memberId).length());
            return;
        }
        AdminSession = adminSession;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        if(CommonFunctions.clean(projectId).length()<1)
        {
            //	System.out.println("memberId  size1: "+CommonFunctions.clean(memberId).length());
            return;
        }
        this.projectId = projectId;
    }

    public RemoveUserFromProject(String adminSession, String projectId,ArrayList<String>  UsersId) {
        super();
        this.AdminSession = adminSession;
        this.projectId = projectId;
        this.UsersId = UsersId;
    }

    public RemoveUserFromProject() {
        super();

    }

    public ArrayList<String> getUsersId() {
        return UsersId;
    }

    public void setUsersId(ArrayList<String> usersId) {
        if(usersId == null || usersId.size()<1)
        {
            return;
        }
        UsersId = usersId;
    }

    public String getJson(RemoveUserFromProject request)
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
