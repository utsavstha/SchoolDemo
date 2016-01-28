package com.example.utsav.schooldemo.Utils;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.utsav.schooldemo.NoticeData;
import com.example.utsav.schooldemo.R;

import org.sufficientlysecure.htmltextview.HtmlTextView;

import java.util.List;

/**
 * Created by utsav on 1/2/2016.
 */
public class RVAdapter extends RecyclerView.Adapter<RVAdapter.DataViewHolder> {
    List<NoticeData> data;
    String months[] = {"Jan", "Feb", "Mar", "Apr",
            "May", "Jun", "Jul", "Aug", "Sep",
            "Oct", "Nov", "Dec"};

    public RVAdapter(List<NoticeData> data) {
        this.data = data;
    }

    public static class DataViewHolder extends RecyclerView.ViewHolder {
        CardView cv;
        TextView day;
        TextView monthYear;
        TextView title;
        HtmlTextView message;

        DataViewHolder(View itemView) {
            super(itemView);
            cv = (CardView) itemView.findViewById(R.id.cardView);
            day = (TextView)itemView.findViewById(R.id.day_notice);
            monthYear = (TextView)itemView.findViewById(R.id.month_year_notice);
            title = (TextView)itemView.findViewById(R.id.title_notice);
            message = (HtmlTextView )itemView.findViewById(R.id.message_notice);
        }
    }

    @Override
    public RVAdapter.DataViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_layout, parent, false);
        DataViewHolder pvh = new DataViewHolder(v);
        return pvh;
    }

    @Override
    public void onBindViewHolder(RVAdapter.DataViewHolder holder, int position) {
        holder.day.setText(data.get(position).getDay());
        //subtracting 1 to fix the array starting from 0 problem
        int month = Integer.parseInt(data.get(position).getMonth())-1;
        String mY =  months[month] +", "+ data.get(position).getYear();
        //String mY =  data.get(position).getMonth()+ ", "+ data.get(position).getYear();

        holder.monthYear.setText(mY);
        holder.title.setText(data.get(position).getTitle());
        holder.message.setText(data.get(position).getMessage());
        holder.message.setHtmlFromString(data.get(position).getMessage(), new HtmlTextView.LocalImageGetter());
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