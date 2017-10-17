package com.example.elena.queue;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Elena on 9/12/2017.
 */

class ShoppingListAdapter extends RecyclerView.Adapter<ShoppingListAdapter.ShoppingListViewHolder>{
    private Queue data;
    public void setData(Queue newData){
        data = newData;
        notifyDataSetChanged();
    }

    @Override
    public ShoppingListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutId = R.layout.list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(layoutId, parent, false);
        return new ShoppingListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ShoppingListViewHolder holder, int position) {
        List<String> aux = data.getmItems();
        String item = aux.get(position);
        holder.nameTextView.setText(item);
        holder.countTextView.setText((position+1)+"");
    }

    @Override
    public int getItemCount() {
        if(data == null)
            return 0;
        return data.getCount();
    }

    class ShoppingListViewHolder extends RecyclerView.ViewHolder{

        TextView nameTextView, countTextView;
        ShoppingListViewHolder(View itemView) {
            super(itemView);
            nameTextView = (TextView) itemView.findViewById(R.id.text_item_name);
            countTextView = (TextView) itemView.findViewById(R.id.text_item_count);
        }
    }
}
