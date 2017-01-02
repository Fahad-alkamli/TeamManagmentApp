package alkamli.fahad.teammanagment.teammanagment.requests;

import com.fasterxml.jackson.databind.ObjectMapper;

public class Response {

    private boolean state=false;
    private String message="";
    public Response(boolean state, String message) {
        super();
        this.state = state;
        this.message = message;
    }
    public boolean getState() {
        return state;
    }
    public void setState(boolean state) {
        this.state = state;
    }
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }


    public Response() {
    }

    public String getJson(Response request)
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
