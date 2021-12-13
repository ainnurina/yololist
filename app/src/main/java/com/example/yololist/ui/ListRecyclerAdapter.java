package com.example.yololist.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.yololist.R;

import com.example.yololist.data.model.List;


public class ListRecyclerAdapter extends RecyclerView.Adapter<ListRecyclerAdapter.ViewHolder> {

    private Context context;
    private java.util.List<List> allList;

    public ListRecyclerAdapter(Context context, java.util.List<List> allList) {
        this.context = context;
        this.allList = allList;
    }

    @NonNull
    @Override
    public ListRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {

        View view = LayoutInflater.from(context)
                .inflate(R.layout.list_row, viewGroup, false);

        return new ViewHolder(view, context);
    }


    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ListRecyclerAdapter.ViewHolder viewHolder, int position) {

        List list = allList.get(position);
        viewHolder.title.setText(list.getTitle());
        viewHolder.itemqty.setText(""+list.getTotitem()+" items");

        String timeAgo = (String) DateUtils.getRelativeTimeSpanString(list.getTimeAdded().getSeconds()*1000);

        viewHolder.dateAdded.setText(timeAgo);


    }

    @Override
    public int getItemCount() {
        return allList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView title, itemqty, dateAdded;

        String userId;
        String username;

        public ViewHolder(@NonNull View itemView, Context ctx) {
            super(itemView);
            context = ctx;

            title = itemView.findViewById(R.id.list_title);
            itemqty = itemView.findViewById(R.id.items_qty);
            dateAdded = itemView.findViewById(R.id.dateadded);

        }

    }


}
