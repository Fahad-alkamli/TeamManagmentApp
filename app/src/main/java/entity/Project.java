package entity;

import com.fasterxml.jackson.databind.ObjectMapper;

public class Project {
    private String projectName,startDate,endDate;
    private boolean enabledState;
    private int projectID;//project ID should be auto generated

    public String getProjectName() {
        return projectName;
    }
    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }
    public String getStartDate() {
        return startDate;
    }
    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }
    public String getEndDate() {
        return endDate;
    }
    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }
    public boolean isEnabledState() {
        return enabledState;
    }
    public void setEnabledState(boolean enabledState) {
        this.enabledState = enabledState;
    }
    public int getProjectID() {
        return projectID;
    }
    public Project(String projectName, String startDate, String endDate, boolean enabledState)
    {
        super();
        this.projectName = projectName;
        this.startDate = startDate;
        this.endDate = endDate;
        this.enabledState = enabledState;
    }

    public Project(int projectID,String projectName, String startDate, String endDate, boolean enabledState)
    {
        super();
        this.projectName = projectName;
        this.startDate = startDate;
        this.endDate = endDate;
        this.enabledState = enabledState;
        this.projectID = projectID;
    }
    public Project() {

    }


    public String getJson(Project request)
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


    @Override
    public boolean equals(Object obj) {
        if(obj instanceof  Project)
        {
            Project temp=(Project) obj;
            if(temp.getProjectID()== this.getProjectID() &&
                    temp.getProjectName().equals(getProjectName())
                    && temp.isEnabledState()==isEnabledState() &&
                    temp.getEndDate().equals(getEndDate()) &&
                    temp.getStartDate().equals(getStartDate()))
            {
                return true;
            }else{
            return false;
        }
        }
        return super.equals(obj);
    }
}
