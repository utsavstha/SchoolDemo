package com.example.utsav.schooldemo.Utils;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.example.utsav.schooldemo.DataClasses.ClientsData;
import com.example.utsav.schooldemo.R;

import java.util.List;

/**
 * Created by utsav on 2/6/2016.
 */
public class RVClient extends RecyclerView.Adapter<RVClient.DataViewHolder> {
    List<ClientsData> data;
    ImageLoader mImageLoader;
    Context context;
    public RVClient(List<ClientsData> data, Context context) {
        this.data = data;
        this.context = context;
    }

    public static class DataViewHolder extends RecyclerView.ViewHolder {

        TextView name;
        NetworkImageView icon;
       // CircularNetworkImageView icon;

        DataViewHolder(View itemView) {
            super(itemView);

            name = (TextView)itemView.findViewById(R.id.client_tv);
            icon = (NetworkImageView)itemView.findViewById(R.id.client_image);
        }
    }

    @Override
    public RVClient.DataViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.client_rv_list_adapter, parent, false);
        DataViewHolder pvh = new DataViewHolder(v);
        return pvh;
    }

    @Override
    public void onBindViewHolder(RVClient.DataViewHolder holder, int position) {
        holder.name.setText(data.get(position).getName());

        mImageLoader = CustomVolleyRequestQueue.getInstance(context)
                .getImageLoader();
        //Image URL - This can point to any image file supported by Android
        mImageLoader.get(data.get(position).getUrl(), ImageLoader.getImageListener(holder.icon,
                android.R.drawable.arrow_down_float, android.R.drawable
                        .ic_dialog_alert));
        holder.icon.setImageUrl(data.get(position).getUrl(), mImageLoader);

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