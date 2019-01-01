package com.comingoo.driver.fousa.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.comingoo.driver.fousa.model.Course;
import com.comingoo.driver.fousa.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ramotion.foldingcell.FoldingCell;

import java.text.DecimalFormat;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {
    private List<Course> mDataset;
    private Context mContext;

    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case

        public View h;
        public TextView driverName, dateText, startText, startText2, endText2, endText, priceText, priceText3, km, kmVal, waitTime, waitTime2, km2, waitTimeVal, lateVal, basePriceVal, date2;
        public Button priceText2;
        public FoldingCell fc;

        public ViewHolder(View v) {
            super(v);
            h = v;
            dateText = v.findViewById(R.id.date);
            startText = v.findViewById(R.id.depart);
            endText = v.findViewById(R.id.destination);
            priceText = v.findViewById(R.id.price);

            date2 = v.findViewById(R.id.dateText);
            startText2 = v.findViewById(R.id.depart_text);
            endText2 = v.findViewById(R.id.end_text);
            priceText2 = v.findViewById(R.id.price2);
            priceText3 = v.findViewById(R.id.price3);
            km = v.findViewById(R.id.km);
            kmVal = v.findViewById(R.id.kmValue);
            waitTime = v.findViewById(R.id.waitTime);
            waitTimeVal = v.findViewById(R.id.waitTimeValue);
            lateVal = v.findViewById(R.id.preWaitV);
            basePriceVal = v.findViewById(R.id.basePrice);

            waitTime2 = v.findViewById(R.id.waitTime2);
            km2 = v.findViewById(R.id.km2);

            driverName = v.findViewById(R.id.textView27);

            fc = v.findViewById(R.id.folding_cell);
        }
    }

    public HistoryAdapter(Context context, List<Course> myDataset) {
        this.mDataset = myDataset;
        this.mContext = context;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent,
                                         int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.rows_history, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        final Course newCourse = mDataset.get(position);

        holder.fc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.fc.toggle(false);
            }
        });

        holder.dateText.setText(newCourse.getDate());
        holder.date2.setText(newCourse.getDate());
        holder.startText.setText(newCourse.getStartAddress());
        holder.endText.setText(newCourse.getEndAddress());
        holder.startText2.setText(newCourse.getStartAddress());
        holder.endText2.setText(newCourse.getEndAddress());
        holder.priceText.setText(newCourse.getPrice());
        holder.priceText2.setText(newCourse.getPrice());
        holder.priceText3.setText(newCourse.getPrice());

        holder.km.setText(newCourse.getDistance() + "km");
        holder.waitTime.setText(newCourse.getWaitTime() + "min");

        holder.km2.setText(newCourse.getDistance() + "km");
        holder.waitTime2.setText(newCourse.getWaitTime() + "min");

        final SharedPreferences prefs = mContext.getSharedPreferences("COMINGOODRIVERDATA", MODE_PRIVATE);
        final String number = prefs.getString("userId", null);
        FirebaseDatabase.getInstance().getReference("DRIVERUSERS").child(number).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                holder.driverName.setText(dataSnapshot.child("fullName").getValue(String.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        if (Integer.parseInt(newCourse.getPreWaitTime()) > 180) {
            holder.lateVal.setText("3 MAD");
        } else {
            holder.lateVal.setText("0 MAD");
        }

        FirebaseDatabase.getInstance().getReference("PRICES").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                holder.kmVal.setText((new DecimalFormat("##.##").format(Double.parseDouble(newCourse.getDistance()) * Double.parseDouble(dataSnapshot.child("km").getValue(String.class)))) + " MAD");
                holder.waitTimeVal.setText((new DecimalFormat("##.##").format(Double.parseDouble(newCourse.getWaitTime()) * Double.parseDouble(dataSnapshot.child("att").getValue(String.class)))) + " MAD");
                holder.basePriceVal.setText(dataSnapshot.child("base").getValue(String.class) + " MAD");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}