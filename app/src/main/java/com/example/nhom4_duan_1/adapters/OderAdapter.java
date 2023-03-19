package com.example.nhom4_duan_1.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nhom4_duan_1.R;
import com.example.nhom4_duan_1.models.Bills;
import com.example.nhom4_duan_1.models.Products;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class OderAdapter extends RecyclerView.Adapter<OderAdapter.ViewHolder>{
    private Context context;
    private ArrayList<Bills> list;

    public OderAdapter(Context context, ArrayList<Bills> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = ((Activity)context).getLayoutInflater();
        View view = inflater.inflate(R.layout.item_oder, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.tvTime.setText(list.get(position).getTime());
        holder.tvPrice.setText(list.get(position).getPrice() + "đ");
        holder.tvAmount.setText(list.get(position).getAmount() + " item");
    }



    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView tvTime, tvPrice, tvAmount, tvRepurchase;
        public ViewHolder(@NonNull View itemView){
            super(itemView);

            tvTime = itemView.findViewById(R.id.tvTime);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvAmount = itemView.findViewById(R.id.tvAmount);
            tvRepurchase = itemView.findViewById(R.id.tvRepurchase);
        }
    }
}