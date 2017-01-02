package alkamli.fahad.teammanagment.teammanagment.views.activites_adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;

import java.util.ArrayList;

import alkamli.fahad.teammanagment.teammanagment.CommonFunctions;
import alkamli.fahad.teammanagment.teammanagment.R;
import entity.Project;

import static alkamli.fahad.teammanagment.teammanagment.CommonFunctions.TAG;
import static android.util.Log.e;

/**
 * Created by d0l1 on 11-16-2016.
 */

public class ChooseProjectToAssignAdapter extends ArrayAdapter<String> {



    ArrayList<Project> ids;
    ArrayList<String> projectIdList=new ArrayList<String>();

    public ChooseProjectToAssignAdapter(Context context, ArrayList<String> names, ArrayList<Project> ids)
    {
        super(context, R.layout.project_list_element,names);
        this.ids=ids;
    }

    static int count=0;
    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View CustomView = inflater.inflate(R.layout.checkbox_element, parent, false);
        try {
            CheckBox nickname = (CheckBox) CustomView.findViewById(R.id.nickname);
          //
            nickname.setText(getItem(position));
            nickname.setTag(Integer.toString(ids.get(count).getProjectID()));
            // Log.d("Alkamli",Integer.toString(ids.get(count).getProjectID()));


            nickname.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    CheckBox item=(CheckBox) view;
                    String id=(String) view.getTag();
                    if(item.isChecked())
                    {
                      //  Log.d("Alkamli","Testing on click");
                        Log.d(CommonFunctions.TAG,"Project id has been added ID: "+id);
                        projectIdList.add(id);


                    }else{
                       // Log.d(CommonFunctions.TAG,"Testing on click");
                        Log.d(CommonFunctions.TAG,"Project id has been removed ID: "+id);
                        projectIdList.remove(id);


                    }
                }
            });
            count += 1;

            if (count >= ids.size()) {
                count = 0;
                Log.d("Alkamli", "Has been rest");
            }
        }catch(Exception e) {
            class Local {
            }
            ;
            e(TAG, ("MethodName: " + Local.class.getEnclosingMethod().getName() + " || ErrorMessage: " + e.getMessage()));
        }

        return CustomView;
    }

    public ArrayList<String> getProjectIdList()
    {
        return projectIdList;
    }
}
