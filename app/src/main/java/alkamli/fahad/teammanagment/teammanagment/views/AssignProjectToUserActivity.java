package alkamli.fahad.teammanagment.teammanagment.views;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import java.util.ArrayList;

import alkamli.fahad.teammanagment.teammanagment.CommonFunctions;
import alkamli.fahad.teammanagment.teammanagment.R;
import alkamli.fahad.teammanagment.teammanagment.service.Service;
import alkamli.fahad.teammanagment.teammanagment.views.activites_adapters.ChooseProjectToAssignAdapter;

import static android.util.Log.e;

public class AssignProjectToUserActivity extends AppCompatActivity {

    final String TAG="Alkamli";
    private String userId;
    private Activity activity;
    ListView listView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(!CommonFunctions.checkForValidLoginSession(this))
        {
            CommonFunctions.sendToast(this,"Please login again.");
            Intent i=new Intent(this,LoginActivity.class);
            startActivity(i);
            finish();
        }
        setContentView(R.layout.activity_assign_project_to_user);
        activity=this;
        try {
        Log.d(TAG, "The ID is: " + getIntent().getStringExtra("id"));
            userId=getIntent().getStringExtra("id");
    if (Service.getProjectListNames() != null || Service.getProjectListNames().size() > 0)
    {
        ChooseProjectToAssignAdapter adapter = new ChooseProjectToAssignAdapter(this, Service.getProjectListNames(), Service.getProjectList());

         listView = (ListView) findViewById(R.id.project_list_view);
        listView.setAdapter(adapter);
    }

    }catch(Exception e)
    {
        Log.e(TAG,e.getMessage());
        finish();
    }

    }


    public void addUserToProject(View view)
    {
        try{

           final ArrayList<String> projectIds=((ChooseProjectToAssignAdapter) listView.getAdapter()).getProjectIdList();
            if(projectIds != null  && projectIds.size()>0)
            {
                Intent i=new Intent();
                i.putExtra("projectIds",projectIds);
                i.putExtra("userId",userId);
                setResult(RESULT_OK,i);
               // Log.d(TAG,"Check this: "+projectIds.size());
                finish();

            }else{
                setResult(RESULT_CANCELED,null);
                finish();
            }
        }catch(Exception e)
        {
            Log.e(TAG,e.getMessage());
        }

    }



}
