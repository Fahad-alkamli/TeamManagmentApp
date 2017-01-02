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
import entity.Task;

import static alkamli.fahad.teammanagment.teammanagment.CommonFunctions.TAG;
import static android.util.Log.e;

public class ChooseTaskToAssignToUserAdapter extends ArrayAdapter<String> {

    ArrayList<Task> tasks=new ArrayList<Task>();
    ArrayList<String> pickedTasksIds=new ArrayList<>();
    public ChooseTaskToAssignToUserAdapter(Context context, ArrayList<String> names, ArrayList<Task> tasks)
    {
        super(context, R.layout.activity_assign_task_to_user,names);
        this.tasks=tasks;
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
            nickname.setTag((tasks.get(count).getTask_id()));
            // Log.d("Alkamli",Integer.toString(ids.get(count).getProjectID()));


            nickname.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        CheckBox item = (CheckBox) view;
                        int id = (Integer) view.getTag();
                        if (item.isChecked()) {
                            //  Log.d("Alkamli","Testing on click");
                            Log.d(CommonFunctions.TAG, "Task id has been added ID: " + id);
                            pickedTasksIds.add(Integer.toString(id));


                        } else {
                            // Log.d(CommonFunctions.TAG,"Testing on click");
                            Log.d(CommonFunctions.TAG, "Task id has been removed ID: " + id);
                            pickedTasksIds.remove(Integer.toString(id));


                        }
                    }catch(Exception e)
                    {
                        class Local {
                        }
                        ;
                        Log.e(TAG, ("MethodName: " + Local.class.getEnclosingMethod().getName() + " || ErrorMessage: " + e.getMessage()));
                    }
                }
            });
            count += 1;

            if (count >= tasks.size())
            {
                count = 0;
                Log.d("Alkamli", "Has been rest");
            }
        }catch(Exception e) {
            class Local {
            }
            ;
            Log.e(TAG, ("MethodName: " + Local.class.getEnclosingMethod().getName() + " || ErrorMessage: " + e.getMessage()));
        }

        return CustomView;
    }

    public ArrayList<Integer> getPickedTasksIds()
    {
        ArrayList<Integer> temp=new ArrayList<Integer>();

        for(String temp2:pickedTasksIds)
        {
            temp.add(Integer.parseInt(temp2));
        }
        return temp;
    }








}
