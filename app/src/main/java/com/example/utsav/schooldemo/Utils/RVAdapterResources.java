package com.example.utsav.schooldemo.Utils;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.utsav.schooldemo.DataClasses.ResourcesData;
import com.example.utsav.schooldemo.R;

import java.util.List;

/**
 * Created by utsav on 1/28/2016.
 */
public class RVAdapterResources extends RecyclerView.Adapter<RVAdapterResources.DataViewHolder> {
    List<ResourcesData> data;
    String months[] = {"","Jan", "Feb", "Mar", "Apr",
            "May", "Jun", "Jul", "Aug", "Sep",
            "Oct", "Nov", "Dec"};

    public RVAdapterResources(List<ResourcesData> data) {
        // this.data.clear();
        this.data = data;
    }

    public static class DataViewHolder extends RecyclerView.ViewHolder {
        CardView cv;
        TextView day;
        TextView monthYear;
        TextView title;

        DataViewHolder(View itemView) {
            super(itemView);
            cv = (CardView) itemView.findViewById(R.id.cardView_resources);
            day = (TextView)itemView.findViewById(R.id.day_resources);
            monthYear = (TextView)itemView.findViewById(R.id.month_year_resources);
            title = (TextView)itemView.findViewById(R.id.title_resources);

        }
    }

    @Override
    public RVAdapterResources.DataViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_resources, parent, false);

        DataViewHolder pvh = new DataViewHolder(v);
        return pvh;
    }

    @Override
    public void onBindViewHolder(RVAdapterResources.DataViewHolder holder, int position) {
        // holder.day.setText("21321");
        holder.day.setText(data.get(position).getDay());
        //subtracting 1 to fix the array starting from 0 problem
        // int month = Integer.parseInt();
        String month = data.get(position).getMonth();
        int value = Integer.parseInt(month);
        Log.d("RVAdapter", value + "");
        String yr = data.get(position).getYear();
        String year;
        if (yr != null && yr.length() >= 2) {
            year = yr.substring(yr.length() - 2);
        }else
            year = yr;
        String mY =  months[value] +", "+ year;
        //String mY =  data.get(position).getMonth()+ ", "+ data.get(position).getYear();

        holder.monthYear.setText(mY);
        holder.title.setText(data.get(position).getTitle());

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }
}