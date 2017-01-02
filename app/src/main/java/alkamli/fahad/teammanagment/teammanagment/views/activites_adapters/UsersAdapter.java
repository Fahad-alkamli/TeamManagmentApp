package alkamli.fahad.teammanagment.teammanagment.views.activites_adapters;


import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.util.ArrayList;

import alkamli.fahad.teammanagment.teammanagment.R;
import entity.User;

public class UsersAdapter extends ArrayAdapter<String> {
    ArrayList<User> ids;

    public UsersAdapter(Context context, ArrayList<String> names, ArrayList<User> ids)
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
        View CustomView = inflater.inflate(R.layout.user_list_element, parent, false);
        TextView nickname = (TextView) CustomView.findViewById(R.id.nickname);
        nickname.setText(getItem(position));
        // Log.d("Alkamli",Integer.toString(ids.get(count).getProjectID()));
        CustomView.setTag(Integer.toString(ids.get(count).getId()));
        count += 1;

        if (count >= ids.size()) {
            count = 0;
            Log.d("Alkamli","Users Has been rest");
        }

        return CustomView;
    }

}
