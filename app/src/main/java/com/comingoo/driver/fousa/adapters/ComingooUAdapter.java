package com.comingoo.driver.fousa.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.comingoo.driver.fousa.R;
import com.comingoo.driver.fousa.model.Car;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class ComingooUAdapter extends RecyclerView.Adapter<ComingooUAdapter.ViewHolder> {

    private List<Car> mDataset;
    private DatabaseReference mLocation;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case

        public View h;
        public TextView carName, carDesc;
        public ImageView selected;

        public ViewHolder(View v) {
            super(v);
            h = v;

            carName = v.findViewById(R.id.car_name);
            carDesc = v.findViewById(R.id.car_desc);
            selected = v.findViewById(R.id.imageView27);

        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public ComingooUAdapter(List<Car> myDataset,DatabaseReference mLocation) {
        this.mDataset = myDataset;
        this.mLocation = mLocation;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ComingooUAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                                      int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cars_rows, parent, false);
        ComingooUAdapter.ViewHolder vh = new ComingooUAdapter.ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final ComingooUAdapter.ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        final Car newCar = mDataset.get(position);

        holder.carName.setText(newCar.getName());
        holder.carDesc.setText(newCar.getDescription());
        if (newCar.getSelected().equals("1")) {
            holder.selected.setBackgroundResource(R.drawable.selected_icon);
        } else {
            holder.selected.setBackgroundResource(R.drawable.unselected_icon);
        }

        holder.selected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLocation.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot data : dataSnapshot.getChildren()) {
                            mLocation.child(data.getKey()).child("selected").setValue("0");
                        }
                        mLocation.child(newCar.getId()).child("selected").setValue("1");
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}