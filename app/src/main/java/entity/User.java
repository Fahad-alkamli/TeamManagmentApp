package entity;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import alkamli.fahad.teammanagment.teammanagment.CommonFunctions;

public class User {
    private String Name,Email,Password,Session;
    private boolean Admin=false;
    private int id;

    //The primary key in this sense will be the email address
    public User( int id,String name, String email, String password, boolean isAdmin)
    {
        this.id=id;
        this.Name = name.trim();
        this.Email = CommonFunctions.clean(email);
        this.Password = password;
        this.Admin = isAdmin;
    }

    public User( int id,String name, String email, boolean isAdmin)
    {
        this.id=id;
        this.Name = name.trim();
        this.Email = CommonFunctions.clean(email);
        this.Admin = isAdmin;
    }

    public User() {
    }


    public void setAdmin(boolean Admin)
    {
        this.Admin = Admin;
    }



    public boolean getAdmin() {
        return Admin;
    }



    public int getId() {
        return id;
    }



    public void setId(int id) {
        this.id = id;
    }


    public String getSession() {
        return Session;
    }



    public void setSession(String session) {
        Session = session;
    }



    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getJson(User user)
    {
        String jsonInString="";
        ObjectMapper mapper = new ObjectMapper();
        try {
            jsonInString	 = mapper.writeValueAsString(user);
        } catch (JsonProcessingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return jsonInString;
    }


    @Override
    public boolean equals(Object obj) {

        try{

            if(obj instanceof User)
            {
                User user=(User) obj;
                if(user.getJson(user).equals(getJson(this)))
                {
                    return true;
                }
            }
        }catch(Exception e)
        {

        }

        return super.equals(obj);
    }
}