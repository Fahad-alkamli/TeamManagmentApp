package alkamli.fahad.teammanagment.teammanagment.views.fragments.adapters.user;


import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.TextView;

import java.io.BufferedWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import alkamli.fahad.teammanagment.teammanagment.service.Service;
import alkamli.fahad.teammanagment.teammanagment.views.AssignTaskToUserActivity;
import alkamli.fahad.teammanagment.teammanagment.views.ChangePasswordActivity;
import alkamli.fahad.teammanagment.teammanagment.CommonFunctions;
import alkamli.fahad.teammanagment.teammanagment.R;
import alkamli.fahad.teammanagment.teammanagment.requests.user.DeleteUserRequest;
import alkamli.fahad.teammanagment.teammanagment.views.AssignProjectToUserActivity;
import alkamli.fahad.teammanagment.teammanagment.views.fragments.adapters.project.ProjectAdapter2;
import entity.Project;
import entity.User;

import static alkamli.fahad.teammanagment.teammanagment.CommonFunctions.TAG;
import static android.util.Log.e;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.MyViewHolder> {


    private List<User> userList;
    private Fragment userFragment;
    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView nickname;
        public View view;
        public MyViewHolder(View view) {
            super(view);
            nickname = (TextView) view.findViewById(R.id.nickname);
            this.view = view;
        }
    }

    public UsersAdapter(Fragment userFragment, List<User> userList)
    {
        this.userList = userList;
        this.userFragment=userFragment;
    }

    @Override
    public UsersAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View CustomView = inflater.inflate(R.layout.user_list_element, parent, false);


        return new UsersAdapter.MyViewHolder(CustomView);
    }


    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        if (userList == null) {
            return;
        }
        User user = userList.get(position);
        holder.nickname.setText(user.getName());
        //set the project id
        holder.view.setTag(Integer.toString(user.getId()));
        if (CommonFunctions.getSharedPreferences(userFragment.getContext()).getBoolean("admin", false)) {
            holder.view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final String id = (String) view.getTag();
                    Log.d("Alkamli", "The user id is " + id);
                    PopupMenu popupMenu = new PopupMenu(userFragment.getContext(), view);
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem menuItem) {
                            switch (menuItem.getItemId()) {
                                case R.id.addToProject: {
                                    Log.d("Alkamli", "Add to project");
                                    Intent i = new Intent(userFragment.getContext(), AssignProjectToUserActivity.class);
                                    i.putExtra("id", id);
                                    userFragment.startActivityForResult(i, 1);
                                    //getContext().startActivity(i);
                                    break;
                                }
                                case R.id.addToTask: {
                                    Intent i = new Intent(userFragment.getContext(), AssignTaskToUserActivity.class);
                                    i.putExtra("id", id);
                                    userFragment.startActivityForResult(i, 2);
                                    break;
                                }
                                case R.id.changePassword: {

                                    Intent i = new Intent(userFragment.getContext(), ChangePasswordActivity.class);
                                    i.putExtra("id", id);
                                    userFragment.getContext().startActivity(i);
                                    break;
                                }
                                case R.id.deleteUser: {
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            sendDeleteUserRequest(userFragment.getActivity(), id);
                                        }
                                    }).start();
                                    break;
                                }

                            }

                            return true;
                        }
                    });
                    popupMenu.inflate(R.menu.popup_menu_users_admin_options);
                    popupMenu.show();
                }
            });
        }
    }
    @Override
    public int getItemCount() {
        if(userList==null)
        {
            return 0;
        }
        return userList.size();
    }

    private void sendDeleteUserRequest(Activity context,String userId) {
        //session + user id
        String session = CommonFunctions.getSharedPreferences(context).getString("session", null);

        if (session == null) {
            return;
        }
        DeleteUserRequest request = new DeleteUserRequest(CommonFunctions.clean(session), CommonFunctions.clean(userId));

        final String jsonString = request.getJson(request);
        Log.d("Alkamli", jsonString);
        URL url;
        String response = "";
        try {
            url = new URL(context.getString(R.string.delete_user_url));
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(15000);
            conn.setConnectTimeout(15000);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);

            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));
            writer.write(jsonString);

            writer.flush();
            writer.close();
            os.close();
            int responseCode = conn.getResponseCode();

            if (responseCode == HttpsURLConnection.HTTP_OK) {
                //Login is successful
                Service.deleteUser(Integer.parseInt(userId.trim()));
                CommonFunctions.sendToast(context, context.getString(R.string.User_has_been_deleted));

                Log.d("Alkamli", "User has been deleted");
            } else if (responseCode == HttpsURLConnection.HTTP_UNAUTHORIZED) {
                // CommonFunctions.sendToast(activity,"Wrong Token");
            } else {
                response = "";
                Log.d("Alkamli", Integer.toString(responseCode));
                //login is not successful
                CommonFunctions.sendToast(context, context.getString(R.string.User_could_not_been_deleted));
            }
        } catch (Exception e) {

            class Local {
            }
            ;
            Log.e(TAG, ("MethodName: " + Local.class.getEnclosingMethod().getName() + " || ErrorMessage: " + e.getMessage()));
        }
    }


}