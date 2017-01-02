package alkamli.fahad.teammanagment.teammanagment.requests.project;

import com.fasterxml.jackson.databind.ObjectMapper;

public class GetAllProjects {

    private String session;

    public String getSession() {
        return session;
    }

    public void setSession(String session) {
        this.session = session;
    }

    public GetAllProjects(String session) {
        this.session = session;
    }

    public GetAllProjects() {
    }



    public String getJson(GetAllProjects request)
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
