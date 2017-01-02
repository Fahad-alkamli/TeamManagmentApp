package alkamli.fahad.teammanagment.teammanagment.views.fragments.adapters.task;


import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import java.util.ArrayList;
import alkamli.fahad.teammanagment.teammanagment.R;
import entity.TaskListViewElement;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.MyViewHolder>{


    private ArrayList<TaskListViewElement> tasks;
    private Context context;

    public class MyViewHolder extends RecyclerView.ViewHolder
    {
        public TextView projectName, startDate, endDate;
        public RecyclerView tasksListView ;

        public MyViewHolder(View view) {
            super(view);
            projectName = (TextView) view.findViewById(R.id.projectName);
            startDate = (TextView) view.findViewById(R.id.startDate);
            endDate = (TextView) view.findViewById(R.id.endDate);
            tasksListView=(RecyclerView) view.findViewById(R.id.tasksListView);
        }
    }


    public TaskAdapter(Context context,ArrayList<TaskListViewElement> tasks)
    {
        this.tasks = tasks;
        this.context=context;
    }





    @Override
    public TaskAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View CustomView = inflater.inflate(R.layout.task_list_view_element, parent, false);
        this.context=parent.getContext();

        return new TaskAdapter.MyViewHolder(CustomView);
    }

    @Override
    public void onBindViewHolder(TaskAdapter.MyViewHolder holder, int position) {
        if(tasks==null)
        {
            return;
        }
        TaskListViewElement task = tasks.get(position);
        holder.projectName.setText(task.getProject().getProjectName());
        holder.startDate.setText(task.getProject().getStartDate());
        holder.endDate.setText(task.getProject().getEndDate());
        holder.tasksListView.setAdapter(new TaskElementAdapter(holder.tasksListView.getContext(), task.getTaskArrayList()));
        holder.tasksListView.setLayoutManager(new LinearLayoutManager(holder.tasksListView.getContext()));

    }

    @Override
    public int getItemCount() {
        if(tasks==null)
        {
            return 0;
        }
        return tasks.size();
    }




    //http://stackoverflow.com/questions/1778485/android-listview-display-all-available-items-without-scroll-with-static-header
    public static boolean setListViewHeightBasedOnItems(ListView listView) {

        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter != null) {

            int numberOfItems = listAdapter.getCount();

            // Get total height of all items.
            int totalItemsHeight = 0;
            for (int itemPos = 0; itemPos < numberOfItems; itemPos++) {
                View item = listAdapter.getView(itemPos, null, listView);
                item.measure(0, 0);
                totalItemsHeight += item.getMeasuredHeight();
            }

            // Get total height of all item dividers.
            int totalDividersHeight = listView.getDividerHeight() *
                    (numberOfItems - 1);

            // Set list height.
            ViewGroup.LayoutParams params = listView.getLayoutParams();
            params.height = totalItemsHeight + totalDividersHeight;
            listView.setLayoutParams(params);
            listView.requestLayout();

            return true;

        } else {
            return false;
        }

    }


}
