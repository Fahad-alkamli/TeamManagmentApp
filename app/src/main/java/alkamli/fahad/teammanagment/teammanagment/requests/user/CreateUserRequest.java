package alkamli.fahad.teammanagment.teammanagment.requests.user;


import com.fasterxml.jackson.databind.ObjectMapper;

import alkamli.fahad.teammanagment.teammanagment.CommonFunctions;

public class CreateUserRequest{

	 private String Email;

	 private String Password;
	 private String AdminSession;
	 private boolean IsAdmin=false;
    private String Name="";

    public boolean isAdmin() {
        return IsAdmin;
    }

    public void setAdmin(boolean admin) {
        IsAdmin = admin;
    }

    //The primary key in this sense will be the email address
	    public CreateUserRequest( String name, String email, String password,String adminSession,boolean isAdmin)
	    {
	        this.Name = name.trim();
	        this.Email = CommonFunctions.clean(email);
	        this.Password = password;
	        this.AdminSession=adminSession;
            this.IsAdmin=isAdmin;
	    }


	    public String getAdminSession() {
			return AdminSession;
		}

		public void setAdminSession(String adminSession) {
			AdminSession = adminSession;
		}

		public CreateUserRequest() {}

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


        public String getJson(CreateUserRequest request)
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
