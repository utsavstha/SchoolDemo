package com.example.utsav.schooldemo.Utils;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.utsav.schooldemo.DBClasses.PathsDB;
import com.example.utsav.schooldemo.DataClasses.DownloadData;
import com.example.utsav.schooldemo.R;

import java.util.List;

/**
 * Created by utsav on 1/19/2016.
 */
public class RVAdapterDownloads extends RecyclerView.Adapter<RVAdapterDownloads.DataViewHolder> {
    List<DownloadData> data;
    PathsDB pathsDB;
    /*String months[] = {"","Jan", "Feb", "Mar", "Apr",
            "May", "Jun", "Jul", "Aug", "Sep",
            "Oct", "Nov", "Dec"};*/

    public RVAdapterDownloads(List<DownloadData> data) {
       // this.data.clear();
        this.data = data;
    }

    public static class DataViewHolder extends RecyclerView.ViewHolder {
        CardView cv;
        TextView day;
        TextView monthYear;
        TextView title;
        TextView size;
        ImageView imageView;
        DataViewHolder(View itemView) {
            super(itemView);
            cv = (CardView) itemView.findViewById(R.id.cardView_downloads);
            day = (TextView)itemView.findViewById(R.id.day_downloads);
            monthYear = (TextView)itemView.findViewById(R.id.month_year_downloads);
            title = (TextView)itemView.findViewById(R.id.title_downloads);
            size = (TextView)itemView.findViewById(R.id.size_downloads);
            imageView = (ImageView)itemView.findViewById(R.id.icon_download);

        }
    }

    @Override
    public RVAdapterDownloads.DataViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.downloads_recyclerview, parent, false);
        pathsDB = new PathsDB(parent.getContext());

        DataViewHolder pvh = new DataViewHolder(v);
        return pvh;
    }

    @Override
    public void onBindViewHolder(RVAdapterDownloads.DataViewHolder holder, int position) {
       // holder.day.setText("21321");
        holder.day.setText(data.get(position).getDay());
        //subtracting 1 to fix the array starting from 0 problem
       // int month = Integer.parseInt();
        String month = data.get(position).getMonth();
        //int value = Integer.parseInt(month);
        //Log.d("RVAdapter", value+"");
        String yr = data.get(position).getYear();
        String year;
        if (yr != null && yr.length() >= 2) {
            year = yr.substring(yr.length() - 2);
        }else
            year = yr;
        //String mY =  months[month] +", "+ year;
        String mY = data.get(position).getMonth() + ", " + year;
        //String mY =  data.get(position).getMonth()+ ", "+ data.get(position).getYear();

        // holder.day.setText(data.get(position).getDay());
        holder.monthYear.setText(mY);
        holder.title.setText(data.get(position).getTitle());
        holder.size.setText(data.get(position).getSize());
        if(pathsDB.getPath(data.get(position).getId()).equalsIgnoreCase("xxx")){
            holder.imageView.setImageResource(R.drawable.savefile);
        }else{
            holder.imageView.setImageResource(R.drawable.eye106);
        }
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