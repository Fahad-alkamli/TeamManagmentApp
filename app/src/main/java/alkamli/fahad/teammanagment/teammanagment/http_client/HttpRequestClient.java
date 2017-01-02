package alkamli.fahad.teammanagment.teammanagment.http_client;


import android.util.Log;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import javax.net.ssl.HttpsURLConnection;
import alkamli.fahad.teammanagment.teammanagment.requests.Response;
import static alkamli.fahad.teammanagment.teammanagment.CommonFunctions.TAG;
import static alkamli.fahad.teammanagment.teammanagment.CommonFunctions.clean;

public class HttpRequestClient{

    private URL url;
    private String postData;
    /**
     This method will create an Http client and it will take the url, the data to post.
     @param url The url to send the request too
     @param postData the post data in json format
     */
    public HttpRequestClient( String url, String postData)
    {
        try {
            this.url = new URL(url);
            this.postData=postData;
        }catch(Exception e)
        {
            class Local {
            }
            ;
            Log.e(TAG, ("MethodName: " + Local.class.getEnclosingMethod().getName() + " || ErrorMessage: " + e.getMessage()));
        }
    }
    public HttpRequestClientResponse post()
    {
        try{
            if(url==null|| postData==null)
            {
                return new HttpRequestClientResponse(HttpsURLConnection.HTTP_BAD_REQUEST,"");
            }

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(15000);
            conn.setConnectTimeout(5000);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);

            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));
            writer.write(postData);

            writer.flush();
            writer.close();
            os.close();
            int responseCode=conn.getResponseCode();
            String response="";
            if (responseCode == HttpsURLConnection.HTTP_CREATED || responseCode == HttpsURLConnection.HTTP_OK)
            {
                String line;
                BufferedReader br=new BufferedReader(new InputStreamReader(conn.getInputStream()));
                while ((line=br.readLine()) != null)
                {
                    response+=line;
                }
                br.close();
                conn.disconnect();
                return new HttpRequestClientResponse(responseCode,response);
            }else{
                String line;
                //http://stackoverflow.com/questions/4633048/httpurlconnection-reading-response-content-on-403-error
                BufferedReader br=new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                while ((line=br.readLine()) != null)
                {
                    response+=line;
                }
                br.close();
                conn.disconnect();
                try{
                    if(clean(response)!= null && clean(response).length()>0)
                    {
                        ObjectMapper objectMapper = new ObjectMapper();
                        objectMapper.configure(JsonParser.Feature.AUTO_CLOSE_SOURCE, true);
                        Response errorMessage=objectMapper.readValue(response, Response.class);

                        return new HttpRequestClientResponse(responseCode,errorMessage.getMessage());
                    }else{
                        return new HttpRequestClientResponse(responseCode,"");
                    }

                }catch(Exception e)
                {
                    class Local {
                    }
                    ;
                    Log.e(TAG, ("MethodName: " + Local.class.getEnclosingMethod().getName() + " || ErrorMessage: " + e.getMessage()));

                }
                return new HttpRequestClientResponse(responseCode,"");
            }

        }catch(Exception e)
        {
            class Local {
            }
            ;
            Log.e(TAG, ("MethodName: " + Local.class.getEnclosingMethod().getName() + " || ErrorMessage: " + e.getMessage()));

        }
        return new HttpRequestClientResponse(HttpsURLConnection.HTTP_BAD_REQUEST,"");
    }
}
