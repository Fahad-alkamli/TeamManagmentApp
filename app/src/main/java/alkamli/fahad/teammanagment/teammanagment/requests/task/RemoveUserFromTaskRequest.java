package alkamli.fahad.teammanagment.teammanagment.requests.task;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;

import alkamli.fahad.teammanagment.teammanagment.CommonFunctions;


public class RemoveUserFromTaskRequest {

    private String adminSession;
    private int taskId;
    private ArrayList<Integer>  userId;
    public String getAdminSession() {
        return adminSession;
    }
    public void setAdminSession(String adminSession) {
        if(CommonFunctions.clean(adminSession)==null || CommonFunctions.clean(adminSession).length()<1)
        {
            return;
        }
        this.adminSession = adminSession;
    }
    public int getTaskId() {
        return taskId;
    }
    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }
    public ArrayList<Integer> getUserId() {
        return userId;
    }
    public void setUserId(ArrayList<Integer> userId) {
        if(userId==null || userId.isEmpty())
        {
            return;
        }
        this.userId = userId;
    }

    public String getJson(RemoveUserFromTaskRequest request)
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

    public RemoveUserFromTaskRequest(String adminSession, int taskId, ArrayList<Integer> userId) {
        this.adminSession = adminSession;
        this.taskId = taskId;
        this.userId = userId;
    }
    public RemoveUserFromTaskRequest() {
    }

}

