package alkamli.fahad.teammanagment.teammanagment.views;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import alkamli.fahad.teammanagment.teammanagment.R;
import alkamli.fahad.teammanagment.teammanagment.service.Service;
import alkamli.fahad.teammanagment.teammanagment.views.activites_adapters.ChooseTaskToAssignToUserAdapter;

public class AssignTaskToUserActivity extends AppCompatActivity {

    private String userId;
    private Activity activity;
    private ListView taskListView;
    ChooseTaskToAssignToUserAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assign_task_to_user);
        activity=this;
        if( getIntent().getStringExtra("id")==null)
        {
            finish();
            return;
        }

        taskListView=(ListView)  findViewById(R.id.taskListView);
        userId=getIntent().getStringExtra("id");
        if(Service.getAllTasksNames()==null || Service.getAllTasks()==null)
        {
            finish();
            return;
        }
        adapter=new ChooseTaskToAssignToUserAdapter(this,Service.getAllTasksNames(),Service.getAllTasks());
        taskListView.setAdapter(adapter);
    }



    public void addTasksToUser(View view)
    {
        Intent i=new Intent();
        i.putExtra("ids",adapter.getPickedTasksIds());
        i.putExtra("user_id",userId);
        setResult(RESULT_OK,i);
        finish();
    }
}
