package com.example.utsav.schooldemo.Utils;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.utsav.schooldemo.DataClasses.ContactsData;
import com.example.utsav.schooldemo.DataClasses.NoticeData;
import com.example.utsav.schooldemo.R;

import org.sufficientlysecure.htmltextview.HtmlTextView;

import java.util.List;

/**
 * Created by utsav on 1/28/2016.
 */
public class RVAdapterContacts extends RecyclerView.Adapter<RVAdapterContacts.DataViewHolder> {
    List<ContactsData> data;


    public RVAdapterContacts(List<ContactsData> data) {
        this.data = data;
    }

    public static class DataViewHolder extends RecyclerView.ViewHolder {
        CardView cv;
        TextView name;
        TextView designation;
        TextView email;
        TextView phone;

        DataViewHolder(View itemView) {
            super(itemView);
            cv = (CardView) itemView.findViewById(R.id.cardView_contacts);
            name = (TextView)itemView.findViewById(R.id.name_contacts);
            designation = (TextView)itemView.findViewById(R.id.designation_contacts);
            email = (TextView)itemView.findViewById(R.id.email_contacts);
            phone = (TextView )itemView.findViewById(R.id.number_contacts);
        }
    }

    @Override
    public RVAdapterContacts.DataViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.contacts_card, parent, false);
        DataViewHolder pvh = new DataViewHolder(v);
        return pvh;
    }

    @Override
    public void onBindViewHolder(RVAdapterContacts.DataViewHolder holder, int position) {
        holder.name.setText("Name: " + data.get(position).getNameContacts());
        holder.designation.setText("Designation: " + data.get(position).getDesignation());
        holder.email.setText("Email: " + data.get(position).getEmail());
        holder.phone.setText("Phone no.: " + data.get(position).getPhone());
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