package com.example.utsav.schooldemo.Utils;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.utsav.schooldemo.DownloadData;
import com.example.utsav.schooldemo.NoticeData;
import com.example.utsav.schooldemo.R;

import java.util.List;

/**
 * Created by utsav on 1/19/2016.
 */
public class RVAdapterDownloads extends RecyclerView.Adapter<RVAdapterDownloads.DataViewHolder> {
    List<DownloadData> data;
    String months[] = {"Jan", "Feb", "Mar", "Apr",
            "May", "Jun", "Jul", "Aug", "Sep",
            "Oct", "Novr", "Dec"};

    public RVAdapterDownloads(List<DownloadData> data) {
        this.data = data;
    }

    public static class DataViewHolder extends RecyclerView.ViewHolder {
        CardView cv;
        TextView day;
        TextView monthYear;
        TextView title;
        TextView message;

        DataViewHolder(View itemView) {
            super(itemView);
            cv = (CardView) itemView.findViewById(R.id.cardView);
            day = (TextView)itemView.findViewById(R.id.day_notice);
            monthYear = (TextView)itemView.findViewById(R.id.month_year_notice);
            title = (TextView)itemView.findViewById(R.id.title_notice);
            message = (TextView)itemView.findViewById(R.id.message_notice);
        }
    }

    @Override
    public RVAdapterDownloads.DataViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_layout, parent, false);
        DataViewHolder pvh = new DataViewHolder(v);
        return pvh;
    }

    @Override
    public void onBindViewHolder(RVAdapterDownloads.DataViewHolder holder, int position) {
        holder.day.setText(data.get(position).getDay());
        //subtracting 1 to fix the array starting from 0 problem
        int month = Integer.parseInt(data.get(position).getMonth())-1;
        String mY =  months[month] +", "+ data.get(position).getYear();
        //String mY =  data.get(position).getMonth()+ ", "+ data.get(position).getYear();

        holder.monthYear.setText(mY);
        holder.title.setText(data.get(position).getTitle());
        holder.message.setText(data.get(position).getMessage());
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