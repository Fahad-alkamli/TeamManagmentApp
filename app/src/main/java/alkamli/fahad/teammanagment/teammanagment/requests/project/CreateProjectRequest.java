package alkamli.fahad.teammanagment.teammanagment.requests.project;


import com.fasterxml.jackson.databind.ObjectMapper;

public class CreateProjectRequest{


	private String AdminSession,projectName,start_date,end_date;
	private boolean enabled=false;
	

	public CreateProjectRequest(String adminSession, String projectName, String start_date, String end_date,boolean enabled) 
	{
		AdminSession = adminSession;
		this.projectName = projectName;
		this.start_date = start_date;
		this.end_date = end_date;
		this.enabled = enabled;
	}
	public CreateProjectRequest(String adminSession, String projectName, String start_date, String end_date) 
	{
		AdminSession = adminSession;
		this.projectName = projectName;
		this.start_date = start_date;
		this.end_date = end_date;
	}
	public CreateProjectRequest() 
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
		this.start_date = start_date;
	}

	public String getEnd_date() {
		return end_date;
	}

	public void setEnd_date(String end_date) {
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
		AdminSession = adminSession;
	}

	public String getJson(CreateProjectRequest request)
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
