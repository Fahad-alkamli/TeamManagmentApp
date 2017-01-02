package alkamli.fahad.teammanagment.teammanagment.views.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
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
import alkamli.fahad.teammanagment.teammanagment.views.fragments.adapters.project.ProjectAdapter2;
import alkamli.fahad.teammanagment.teammanagment.service.Service;
import alkamli.fahad.teammanagment.teammanagment.views.CreateProjectActivity;

import static alkamli.fahad.teammanagment.teammanagment.CommonFunctions.TAG;
import static android.util.Log.e;


public class ProjectsFragment extends Fragment implements Observer{

   static RecyclerView listView;
    Context context;
    ProjectAdapter2 adapter ;
    SwipeRefreshLayout projectRefresh;

    FragmentActivity fragmentActivity;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        Service.addProjectObserver(this);
        context=getContext();
        fragmentActivity=getActivity();
    }

    @Override
    public void update(Observable observable, Object o)
    {
        try {
            //Log.e(CommonFunctions.TAG, "ProjectsFragment Update has been called");
            Runnable run=new Runnable()
            {
                @Override
                public void run() {
                    getItems();
                }
            };
            if(fragmentActivity != null)
            {
               // fragmentActivity.runOnUiThread(run);
                new Thread(run).start();
            }else{
                //Log.e(TAG,"ProjectFragment update function : activity is null");
            }


        }catch(Exception e) {
            class Local {};
            Log.e(TAG, ("MethodName: " + Local.class.getEnclosingMethod().getName() + " || ErrorMessage: " + e.getMessage()));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.d("Alkamli","onCreateView");
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_projects, container, false);
        listView= (RecyclerView) view.findViewById(R.id.projectsListView);
        projectRefresh=(SwipeRefreshLayout)  view.findViewById(R.id.projectRefresh);
        projectRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Thread(new Runnable()
                {
                    @Override
                    public void run() {
                        Service.refreshProjectList(getActivity());

                        getActivity().runOnUiThread(new Runnable()
                        {
                            @Override
                            public void run() {
                                if(projectRefresh != null )
                                {
                                    projectRefresh.setRefreshing(false);
                                }
                            }
                        });
                    }
                }).start();


            }
        });
        listView.setAdapter(new ProjectAdapter2(getContext(),Service.getProjectList()));
        listView.setLayoutManager(new LinearLayoutManager(getActivity()));
       // listView.setHasFixedSize(true);
      //  getItems();
        return view;
    }


    private void getItems()
    {
        try {
            //Log.e(CommonFunctions.TAG,"ProjectFragment GetItems");
           // Log.d("Alkamli", "Project fragment has been called");
            ArrayList<String> temp = Service.getProjectListNames();

            if (temp == null || listView == null) {
                Log.d("Alkamli", "getProjectListNames()==null");
                return;
            }

            adapter = new ProjectAdapter2(getContext(),Service.getProjectList());
            if(fragmentActivity!= null)
            {
                fragmentActivity.runOnUiThread(new Runnable(){
                    @Override
                    public void run() {
                        listView.setAdapter(adapter);
                    }
                });
            }

        }catch(Exception e) {
            class Local {};
            Log.e(TAG, ("MethodName: " + Local.class.getEnclosingMethod().getName() + " || ErrorMessage: " + e.getMessage()));
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        try {
            switch (item.getItemId())
            {
                case R.id.add:
                    //Show the menu
                {
                    Log.d(TAG, "addProject clicked");
                    Intent i=new Intent(getContext(),CreateProjectActivity.class);
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

