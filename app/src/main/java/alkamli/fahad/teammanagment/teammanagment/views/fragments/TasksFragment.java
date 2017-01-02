package alkamli.fahad.teammanagment.teammanagment.views.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import alkamli.fahad.teammanagment.teammanagment.CommonFunctions;
import alkamli.fahad.teammanagment.teammanagment.R;
import alkamli.fahad.teammanagment.teammanagment.views.fragments.adapters.task.TaskAdapter;
import alkamli.fahad.teammanagment.teammanagment.service.Service;
import alkamli.fahad.teammanagment.teammanagment.views.CreateNewTaskActivity;
import entity.TaskListViewElement;

public class TasksFragment extends Fragment implements Observer {


    View view;
    final String TAG=CommonFunctions.TAG;

    RecyclerView tasksListView;

    SwipeRefreshLayout taskRefresh;
    Fragment thisFragment;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        Service.addTaskObserver(this);
        thisFragment=this;

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_tasks, container, false);
        this.view=view;
         tasksListView =(RecyclerView)view.findViewById(R.id.tasksListView);
        taskRefresh=(SwipeRefreshLayout)  view.findViewById(R.id.taskRefresh);
        ArrayList<TaskListViewElement> tasksTemp=new ArrayList<>();

        taskRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Thread(new Runnable()
                {
                    @Override
                    public void run() {
                        Service.refreshTaskList(getActivity());

                        getActivity().runOnUiThread(new Runnable()
                        {
                            @Override
                            public void run() {
                                if(taskRefresh != null )
                                {
                                    taskRefresh.setRefreshing(false);
                                }
                            }
                        });
                    }
                }).start();


            }
        });
        //getItems();
        //These two lines are important for some reason they make sure the list is ready to receive items when ready
        tasksListView.setAdapter(new TaskAdapter(getContext(), Service.getTasksList()));
        tasksListView.setLayoutManager(new LinearLayoutManager(getActivity()));
        return view;
    }


    @Override
    public void update(Observable observable, Object o)
    {

        //Log.e(TAG,"TaskFragment update has been called");
        if(thisFragment==null || thisFragment.getActivity()==null)
        {
            return;
        }

        new Thread(new Runnable(){
            @Override
            public void run() {
                getItems();
            }
        }).start();
    }

    private void getItems()
    {
        ArrayList<TaskListViewElement> temp= Service.getTasksList();

        if(temp==null || tasksListView==null)
        {
            Log.d("Alkamli", "getUsernames()==null");
            return ;
        }

        if(getActivity() !=null)
        {
           final TaskAdapter adapter=new TaskAdapter(getContext(), temp);
            getActivity().runOnUiThread(new Runnable(){
                @Override
                public void run() {
                    tasksListView.setAdapter(adapter);
                }
            });

        }
       // tasksListView.setLayoutManager(new LinearLayoutManager(getActivity()));

    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //Log.e(TAG,"tasks Fragment");

        try {
            switch (item.getItemId())
            {
                case R.id.add:
                    //Show the menu
                {
                    Log.d(TAG, "addTask clicked");
                    Intent i=new Intent(getContext(),CreateNewTaskActivity.class);
                    startActivity(i);
                    return true;
                }
            }
        }catch(Exception e) {
            class Local {};
            Log.e(CommonFunctions.TAG, ("MethodName: " + Local.class.getEnclosingMethod().getName() + " || ErrorMessage: " + e.getMessage()));
        }
        return super.onOptionsItemSelected(item);
    }

}

