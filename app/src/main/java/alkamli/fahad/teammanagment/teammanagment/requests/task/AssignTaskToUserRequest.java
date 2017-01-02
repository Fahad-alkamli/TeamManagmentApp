package alkamli.fahad.teammanagment.teammanagment.requests.task;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;

import alkamli.fahad.teammanagment.teammanagment.CommonFunctions;


public class AssignTaskToUserRequest {
    private String adminSession;

    private int userId;
    private ArrayList<Integer>  taskIds;

    public String getAdminSession() {
        return adminSession;
    }
    public void setAdminSession(String adminSession)
    {
        if(CommonFunctions.clean(adminSession) ==null || CommonFunctions.clean(adminSession).length()<1)
        {
            return;
        }
        this.adminSession = adminSession;
    }
    public int getUserId() {
        return userId;
    }
    public void setUserId(int userId) {
        this.userId = userId;
    }

    public AssignTaskToUserRequest() {
        super();
        // TODO Auto-generated constructor stub
    }
    public ArrayList<Integer> getTaskIds() {
        return taskIds;
    }
    public void setTaskIds(ArrayList<Integer> taskIds) {
        if(taskIds == null || taskIds.size()<1)
        {
            return;
        }
        this.taskIds = taskIds;
    }
    public AssignTaskToUserRequest(String adminSession, int userId, ArrayList<Integer> taskIds) {
        super();
        this.adminSession = adminSession;
        this.userId = userId;
        this.taskIds = taskIds;
    }



    public String getJson(AssignTaskToUserRequest request)
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