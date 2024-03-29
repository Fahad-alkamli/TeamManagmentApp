package alkamli.fahad.teammanagment.teammanagment.views.activites_adapters;



import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;


public class ViewPagerAdapter extends FragmentPagerAdapter{

    ArrayList<Fragment> fragments=new ArrayList<Fragment>();
    ArrayList<String> tabTitles=new ArrayList<String>();
    public ViewPagerAdapter(FragmentManager fm)
    {
        super(fm);

    }

    public void addFragments(Fragment fragment,String tabTitle)
    {
        fragments.add(fragment);
        tabTitles.add(tabTitle);
    }
    @Override
    public Fragment getItem(int position) {

        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles.get(position);
    }






}
