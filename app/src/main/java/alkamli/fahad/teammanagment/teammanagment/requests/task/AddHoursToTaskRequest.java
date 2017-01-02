package alkamli.fahad.teammanagment.teammanagment.requests.task;

import com.fasterxml.jackson.databind.ObjectMapper;

import alkamli.fahad.teammanagment.teammanagment.CommonFunctions;

/**
 * Created by d0l1 on 12-12-2016.
 */

public class AddHoursToTaskRequest {

    private String session;
    private int taskId;
    private double hours;




    public AddHoursToTaskRequest(String session, int taskId, double hours) {
        super();
        this.session = session;
        this.taskId = taskId;
        this.hours = hours;
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





    public double getHours() {
        return hours;
    }
    public void setHours(double hours) {
        this.hours = hours;
    }
    public AddHoursToTaskRequest() {
    }




    public String getJson(AddHoursToTaskRequest request)
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

