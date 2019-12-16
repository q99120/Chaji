package com.mei.chaji.ui.main.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mei.chaji.R;
import com.mei.chaji.core.bean.main.GoodsInfo;

import java.util.ArrayList;
import java.util.List;

public class ReclyGridAdapter extends RecyclerView.Adapter<ReclyGridAdapter.MyViewHolder> {

    private List<GoodsInfo> list;
    Context context;
    private List<Boolean> isClicks;
    private RecyleOnItemClickListener mClickListener;


    public ReclyGridAdapter(Context context, List<GoodsInfo> list) {
        this.context = context;
        this.list = list;
        isClicks = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            if (i == 0) {
                isClicks.add(true);
            } else {
                isClicks.add(false);
            }
        }
    }

    public void setOnItemClickListener(RecyleOnItemClickListener onItemClickListener) {
        this.mClickListener = onItemClickListener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_goods_item, parent, false);
        return new MyViewHolder(view, mClickListener);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Log.e("+++++++++++++", "onBindViewHolder: "+list.get(position).getGoodsName() );
        holder.name.setText(list.get(position).getGoodsName());
        holder.price.setText("价格:"+list.get(position).getGoodsPrice()+"元");
        holder.itemView.setTag(position);
        if (isClicks.get(position)) {
            holder.name.setBackgroundResource(R.drawable.shape_dotted_line_check);
        } else {
            holder.name.setBackgroundResource(R.drawable.shape_dotted_line);
        }
        holder.name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < isClicks.size(); i++) {
                    isClicks.set(i, false);
                }
                isClicks.set(position, true);
                notifyDataSetChanged();
                mClickListener.onItemClick(holder.itemView, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView name;
        TextView price;

        public MyViewHolder(View itemView, RecyleOnItemClickListener mClickListener) {
            super(itemView);
            name = itemView.findViewById(R.id.tea_name);
            price = itemView.findViewById(R.id.tea_price);
            itemView.setOnClickListener(this);

        }


        @Override
        public void onClick(View view) {
            int p = (int) view.getTag();
            if (mClickListener != null) {
                mClickListener.onItemClick(view, p);
            }
        }
    }

}


