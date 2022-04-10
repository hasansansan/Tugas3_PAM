package com.example.pam_gmaps;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {


    Context context;
    ArrayList<DataModel> dataArraylist;

    public MyAdapter(Context context, ArrayList<DataModel> dataArraylist) {
        this.context = context;
        this.dataArraylist = dataArraylist;
    }

    @NonNull
    @Override
    public MyAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_data,parent,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyAdapter.ViewHolder holder, int position) {

        DataModel data = dataArraylist.get(position);

        holder.name.setText(data.name);
//        holder.createdDate.setText(String.valueOf(data.createdDate));

    }

    @Override
    public int getItemCount() {
        return dataArraylist.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        TextView name;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.item_order);

        }
    }
}
