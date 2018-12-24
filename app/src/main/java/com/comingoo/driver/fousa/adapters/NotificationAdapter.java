package com.comingoo.driver.fousa.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.comingoo.driver.fousa.R;
import com.comingoo.driver.fousa.model.Notification;

import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {
    private List<Notification> mDataset;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case

        public View h;
        public TextView title, content;

        public ViewHolder(View v) {
            super(v);
            h = v;

            title = v.findViewById(R.id.title);
            content = v.findViewById(R.id.content);

        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public NotificationAdapter(List<Notification> myDataset) {
        this.mDataset = myDataset;

    }

    // Create new views (invoked by the layout manager)
    @Override
    public NotificationAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                                        int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.rows_notification, parent, false);
        NotificationAdapter.ViewHolder vh = new NotificationAdapter.ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final NotificationAdapter.ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        final Notification newCourse = mDataset.get(position);

        holder.title.setText(newCourse.getTitle());
        holder.content.setText(newCourse.getContent());

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}