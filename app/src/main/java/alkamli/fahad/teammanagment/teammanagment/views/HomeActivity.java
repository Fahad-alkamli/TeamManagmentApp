package alkamli.fahad.teammanagment.teammanagment.views;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import java.util.ArrayList;

import alkamli.fahad.teammanagment.teammanagment.*;
import alkamli.fahad.teammanagment.teammanagment.views.fragments.ProjectsFragment;
import alkamli.fahad.teammanagment.teammanagment.views.fragments.TasksFragment;
import alkamli.fahad.teammanagment.teammanagment.views.fragments.UsersFragment;
import alkamli.fahad.teammanagment.teammanagment.service.Service;
import alkamli.fahad.teammanagment.teammanagment.views.activites_adapters.ViewPagerAdapter;

import static android.util.Log.e;

public class HomeActivity extends AppCompatActivity {

    public static ArrayList<String> UsersList= new ArrayList<String>();
    final String TAG="Alkamli";
    TabLayout tabLayout;
    ViewPager viewPager;
    ViewPagerAdapter viewPagerAdapter;
    static AppCompatActivity activity=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_home);
            activity=this;
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolBar);
            setSupportActionBar(toolbar);
            tabLayout=(TabLayout) findViewById(R.id.tabLayout);
            viewPager=(ViewPager) findViewById(R.id.viewPager);
            //http://stackoverflow.com/questions/8348707/prevent-viewpager-from-destroying-off-screen-views
            viewPager.setOffscreenPageLimit(2);
            viewPagerAdapter=new ViewPagerAdapter(getSupportFragmentManager());
            UsersFragment UsersFragment=new UsersFragment();
            ProjectsFragment projectsFragment=new ProjectsFragment();
            TasksFragment Task=new TasksFragment();
            viewPagerAdapter.addFragments(UsersFragment,getString(R.string.fragmentUsersTitle));
            viewPagerAdapter.addFragments(projectsFragment,getString(R.string.fragmentProjectsTitle));
            viewPagerAdapter.addFragments(Task,getString(R.string.fragmentTasksTitle));
            viewPager.setAdapter(viewPagerAdapter);
            tabLayout.setupWithViewPager(viewPager);
            //only start the service after all the components have been initiated
            Intent i=new Intent(this,Service.class);
            startService(i);

        }catch(Exception e)
        {
            Log.d(TAG,"onCreate: "+e.getMessage());

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        //Log.e(TAG,"onCreateOptionsMenu");
        getSupportActionBar().setTitle(getString(R.string.home_title)+" "+CommonFunctions.getSharedPreferences(getApplicationContext()).getString("nickname",""));
        boolean admin= CommonFunctions.getSharedPreferences(getApplicationContext()).getBoolean("admin",false);
        MenuInflater inflater = getMenuInflater();
        if(admin)
        {

            inflater.inflate(R.menu.admin_menu, menu);
        }else{
            inflater.inflate(R.menu.normal_user_menu, menu);
        }



        return true;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        try {
            switch (item.getItemId()) {

                case R.id.settings:
                {
                    Intent i = new Intent(getApplicationContext(), UserSettingsActivity.class);
                    startActivity(i);
                    //overridePendingTransition(R.anim.fadeout, R.anim.fadein);
                    return true;
                }
                case R.id.logout:
                {
                    Log.d(TAG, "logout clicked");
                  final String tempSession= CommonFunctions.getSharedPreferences(getApplicationContext()).getString("session",null);
                    if(tempSession !=null)
                    {
                        CommonFunctions.getEditor(getApplicationContext()).clear().commit();
                        Runnable run = new Runnable() {
                            @Override
                            public void run() {
                                CommonFunctions.logout(tempSession, activity);
                            }
                        };
                        new Thread(run).start();
                    }
                    Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(i);
                    finish();
                    return true;
                }
            }
        }catch(Exception e)
        {
            class Local {};
            Log.e(CommonFunctions.TAG, ("MethodName: " + Local.class.getEnclosingMethod().getName() + " || ErrorMessage: " + e.getMessage()));
        }


        return false;
    }


    @Override
    protected void onResume() {
        super.onResume();
        if(!CommonFunctions.checkForValidLoginSession(this))
        {
            CommonFunctions.sendToast(this,"Please login again.");
            Intent i=new Intent(this,LoginActivity.class);
            startActivity(i);
            finish();

        }
    }

    public static AppCompatActivity getActivity()
    {
        return activity;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //Log.e(TAG,"This is the controller");
        super.onActivityResult(requestCode, resultCode, data);
    }

}
