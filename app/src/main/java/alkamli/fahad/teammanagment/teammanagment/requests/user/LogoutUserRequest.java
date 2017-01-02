package alkamli.fahad.teammanagment.teammanagment.requests.user;

import com.fasterxml.jackson.databind.ObjectMapper;

public class LogoutUserRequest {


	 private String session;

	 
	public LogoutUserRequest(String session) {
		this.session = session;
	}

	public String getSession() {
		return session;
	}

	public void setSession(String session) {
		this.session = session;
	}

	public LogoutUserRequest() {
	}

	public String getJson(LogoutUserRequest request)
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
