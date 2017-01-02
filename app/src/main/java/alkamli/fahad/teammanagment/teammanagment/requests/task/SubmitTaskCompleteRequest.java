package alkamli.fahad.teammanagment.teammanagment.requests.task;

import com.fasterxml.jackson.databind.ObjectMapper;

import alkamli.fahad.teammanagment.teammanagment.CommonFunctions;

/**
 * Created by d0l1 on 12-12-2016.
 */

public class SubmitTaskCompleteRequest {

    private String session;
    private int taskId;


    public SubmitTaskCompleteRequest() {
    }
    public SubmitTaskCompleteRequest(String session, int taskId) {
        super();
        this.session = session;
        this.taskId = taskId;
    }
    public String getSession() {
        return session;
    }
    public void setSession(String session) {
        if(CommonFunctions.clean(session)==null || CommonFunctions.clean(session).length()<1)
        {
            return;
        }
        this.session = session;
    }
    public int getTaskId() {
        return taskId;
    }
    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }


    public String getJson(SubmitTaskCompleteRequest request)
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
