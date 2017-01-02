package alkamli.fahad.teammanagment.teammanagment.http_client;


import com.fasterxml.jackson.databind.ObjectMapper;

public class HttpRequestClientResponse {

    private int httpStatus;
    private String responseString;
    public HttpRequestClientResponse(int httpStatus,String responseString)
    {
        this.httpStatus=httpStatus;
        this.responseString=responseString;

    }

    public int getHttpStatus() {
        return httpStatus;
    }

    public void setHttpStatus(int httpStatus) {
        this.httpStatus = httpStatus;
    }

    public String getResponseString() {
        return responseString;
    }

    public void setResponseString(String responseString) {
        this.responseString = responseString;
    }


    public String getjson(HttpRequestClientResponse request)
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
