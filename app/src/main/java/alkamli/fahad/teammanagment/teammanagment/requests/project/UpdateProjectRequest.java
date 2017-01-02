package alkamli.fahad.teammanagment.teammanagment.requests.project;

import com.fasterxml.jackson.databind.ObjectMapper;

import alkamli.fahad.teammanagment.teammanagment.CommonFunctions;

/**
 * Created by d0l1 on 11-25-2016.
 */

public class UpdateProjectRequest {

    private String AdminSession,projectName,start_date,end_date,projectId;
    private boolean enabled=false;


    public UpdateProjectRequest(String adminSession, String projectName, String start_date, String end_date,boolean enabled,String projectId)
    {
        AdminSession = adminSession;
        this.projectName = projectName;
        this.start_date = start_date;
        this.end_date = end_date;
        this.enabled = enabled;
        this.projectId=projectId;
    }
    public UpdateProjectRequest(String adminSession, String projectName, String start_date, String end_date,String projectId)
    {
        AdminSession = adminSession;
        this.projectName = projectName;
        this.start_date = start_date;
        this.end_date = end_date;
        this.projectId=projectId;
    }
    public UpdateProjectRequest()
    {

    }
    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getStart_date() {
        return start_date;
    }

    public void setStart_date(String start_date) {
        if(CommonFunctions.clean(start_date).length()<1)
        {
            //	System.out.println("memberId  size1: "+CommonFunctions.clean(memberId).length());
            return;
        }
        this.start_date = start_date;
    }

    public String getEnd_date() {
        return end_date;
    }

    public void setEnd_date(String end_date) {
        if(CommonFunctions.clean(end_date).length()<1)
        {
            //	System.out.println("memberId  size1: "+CommonFunctions.clean(memberId).length());
            return;
        }
        this.end_date = end_date;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

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

    public String getJson(UpdateProjectRequest request)
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


}
