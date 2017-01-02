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

import entity.User;

import static alkamli.fahad.teammanagment.teammanagment.CommonFunctions.TAG;
import static android.util.Log.e;


public class DisplayProjectMembersActivityAdapter extends ArrayAdapter<String> {
    ArrayList<User> ids;
    ArrayList<String> userIdList=new ArrayList<String>();

    public DisplayProjectMembersActivityAdapter(Context context, ArrayList<String> names, ArrayList<User> ids)
    {
        super(context, R.layout.user_list_element,names);
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
            nickname.setTag(Integer.toString(ids.get(count).getId()));


            nickname.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    CheckBox item=(CheckBox) view;
                    String id=(String) view.getTag();
                    if(item.isChecked())
                    {
                        //  Log.d("Alkamli","Testing on click");
                        Log.d(CommonFunctions.TAG,"User id has been added ID: "+id);
                        userIdList.add(id);


                    }else{
                        // Log.d(CommonFunctions.TAG,"Testing on click");
                        Log.d(CommonFunctions.TAG,"User id has been removed ID: "+id);
                        userIdList.remove(id);


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

    public ArrayList<String> getUserIdList()
    {
        return userIdList;
    }
}
