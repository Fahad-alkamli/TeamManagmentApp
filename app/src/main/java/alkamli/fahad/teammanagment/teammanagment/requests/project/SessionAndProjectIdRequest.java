package alkamli.fahad.teammanagment.teammanagment.requests.project;


import com.fasterxml.jackson.databind.ObjectMapper;

public class SessionAndProjectIdRequest {

	private String adminSession,projectId;

	public String getAdminSession() {
		return adminSession;
	}

	public void setAdminSession(String adminSession) {
		this.adminSession = adminSession;
	}

	public String getProjectId() {
		return projectId;
	}

	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}

	public SessionAndProjectIdRequest(String adminSession, String projectId)
	{
		this.adminSession = adminSession;
		this.projectId = projectId;
	}
	public SessionAndProjectIdRequest() {

	}

	public String getJson(SessionAndProjectIdRequest request)
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
