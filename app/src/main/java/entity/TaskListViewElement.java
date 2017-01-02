package entity;


import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;

public class TaskListViewElement {

    private Project project;
    private ArrayList<Task> taskArrayList;

    public TaskListViewElement(Project project, ArrayList<Task> taskArrayList)
    {
        this.project = project;
        this.taskArrayList = taskArrayList;
    }
    public TaskListViewElement(Project project, Task task)
    {
        this.project = project;

        this.taskArrayList=new ArrayList<Task>();
        this.taskArrayList.add(task);
    }
    public TaskListViewElement()
    {
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public ArrayList<Task> getTaskArrayList() {
        return taskArrayList;
    }

    public void setTaskArrayList(ArrayList<Task> taskArrayList) {
        this.taskArrayList = taskArrayList;
    }

    public String getJson(TaskListViewElement request)
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
        if (obj instanceof  TaskListViewElement)
        {
            TaskListViewElement temp=(TaskListViewElement) obj;
            if(temp.getProject().equals(getProject()) && temp.getTaskArrayList().equals(getTaskArrayList()))
            {
                return true;
            }
        }
        return false;
    }
}
