package alkamli.fahad.teammanagment.teammanagment.requests.task;

import com.fasterxml.jackson.databind.ObjectMapper;

import alkamli.fahad.teammanagment.teammanagment.CommonFunctions;



public class CreateTaskRequest {

    private int project_id;
    private double done_total_hours=0;
    private String adminSession,task_summary,task_start_date,task_end_date;
    private boolean completed=false;

    public CreateTaskRequest() {
        super();
    }


    public CreateTaskRequest(String adminSession,int project_id, String done_total_hours, String task_summary,
                             String task_start_date, String task_end_date,boolean completed) {
        super();
        this.adminSession=adminSession;
        this.project_id = project_id;
        if(done_total_hours ==null || CommonFunctions.clean(done_total_hours).length()<1)
        {
            done_total_hours="0";
        }
        this.done_total_hours = Double.parseDouble(done_total_hours);
        this.task_summary = task_summary;
        this.task_start_date = task_start_date;
        this.task_end_date = task_end_date;
        this.completed=completed;
    }

    public int getProject_id() {
        return project_id;
    }
    public void setProject_id(int project_id) {
        this.project_id = project_id;
    }

    public double getDone_total_hours() {
        return done_total_hours;
    }
    public void setDone_total_hours(double done_total_hours) {
        this.done_total_hours = done_total_hours;
    }
    public String getTask_summary() {
        return task_summary;
    }
    public void setTask_summary(String task_summary) {
        if(CommonFunctions.clean(task_summary).length()<1)
        {
            //	System.out.println("memberId  size1: "+CommonFunctions.clean(memberId).length());
            return;
        }
        this.task_summary = task_summary;
    }
    public String getTask_start_date() {
        return task_start_date;
    }
    public void setTask_start_date(String task_start_date) {
        if(CommonFunctions.clean(task_start_date).length()<1)
        {
            //	System.out.println("memberId  size1: "+CommonFunctions.clean(memberId).length());
            return;
        }
        this.task_start_date = task_start_date;
    }
    public String getTask_end_date() {
        return task_end_date;
    }
    public void setTask_end_date(String task_end_date) {
        if(CommonFunctions.clean(task_end_date).length()<1)
        {
            //	System.out.println("memberId  size1: "+CommonFunctions.clean(memberId).length());
            return;
        }
        this.task_end_date = task_end_date;
    }




    public String getAdminSession() {
        return adminSession;
    }


    public void setAdminSession(String adminSession) {
        if(CommonFunctions.clean(adminSession).length()<1)
        {
            //	System.out.println("memberId  size1: "+CommonFunctions.clean(memberId).length());
            return;
        }
        this.adminSession = adminSession;
    }


    public String getJson(CreateTaskRequest request)
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


    public boolean isCompleted() {
        return completed;
    }


    public void setCompleted(boolean completed) {
        this.completed = completed;
    }


}
