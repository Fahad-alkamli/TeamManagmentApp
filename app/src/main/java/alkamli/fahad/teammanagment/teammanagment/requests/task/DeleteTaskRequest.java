package alkamli.fahad.teammanagment.teammanagment.requests.task;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Created by d0l1 on 12-05-2016.
 */

public class DeleteTaskRequest {


    private String adminSession;
    private int taskId;


    public DeleteTaskRequest() {

    }


    public DeleteTaskRequest(String adminSession, int taskId) {
        super();
        this.adminSession = adminSession;
        this.taskId = taskId;
    }


    public String getAdminSession() {
        return adminSession;
    }


    public void setAdminSession(String adminSession) {
        this.adminSession = adminSession;
    }


    public int getTaskId() {
        return taskId;
    }


    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }


    public String getJson(DeleteTaskRequest request)
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
