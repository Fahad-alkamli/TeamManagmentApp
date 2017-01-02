package alkamli.fahad.teammanagment.teammanagment.requests.project;


import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;

public class AddMemberToProjectRequest {
    private String adminSession,memberId;

    private ArrayList<String>  projectId;




    public AddMemberToProjectRequest() {
        super();
        // TODO Auto-generated constructor stub
    }


    public AddMemberToProjectRequest(String adminSession, String memberId, ArrayList<String> projectId) {
        super();
        this.adminSession = adminSession;
        this.memberId = memberId;
        this.projectId = projectId;
    }


    public String getAdminSession() {
        return adminSession;
    }

    public void setAdminSession(String adminSession) {
        this.adminSession = adminSession;
    }

    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    public ArrayList<String> getProjectId() {
        return projectId;
    }

    public void setProjectId(ArrayList<String> projectId) {
        this.projectId = projectId;
    }

    public String getJson(AddMemberToProjectRequest request)
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

