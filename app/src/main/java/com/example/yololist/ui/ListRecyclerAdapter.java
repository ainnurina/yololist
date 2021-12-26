package com.example.yololist.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.yololist.MainActivity;
import com.example.yololist.R;

import com.example.yololist.data.model.List;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;


public class ListRecyclerAdapter extends RecyclerView.Adapter<ListRecyclerAdapter.ViewHolder> implements Filterable {
    private final RecyclerViewInterface recyclerViewInterface;


    private static Context context;
    private java.util.List<List> allList; //examplelist
    private java.util.List<List> allListFull;

    public ListRecyclerAdapter(Context context, java.util.List<List> allList, RecyclerViewInterface recyclerViewInterface) {

        this.context = context;
        this.allList = allList;
        this.recyclerViewInterface = recyclerViewInterface;
        allListFull = new ArrayList<>(allList);
    }


    @NonNull
    @Override
    public ListRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {

        View view = LayoutInflater.from(context)
                .inflate(R.layout.list_row, viewGroup, false);

        return new ViewHolder(view, context, recyclerViewInterface);
    }


    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ListRecyclerAdapter.ViewHolder viewHolder, int position) {

        List list = allList.get(position);
        viewHolder.title.setText(list.getTitle());
        viewHolder.itemqty.setText(""+list.getTotitem()+" items");

        //String timeAgo = (String) DateUtils.getRelativeTimeSpanString(list.
        //       getTimeAdded().getSeconds()*1000);
        //viewHolder.dateAdded.setText(list.getTimeAdded());
        String timeAgo = (String) DateUtils.getRelativeTimeSpanString(list.getTimeAdded().getSeconds()*1000);

        viewHolder.dateAdded.setText(timeAgo);


    }

    @Override
    public int getItemCount() {
        return allList.size();
    }

    @Override
    public Filter getFilter() {
        return exampleFilter;
    }

    private Filter exampleFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            java.util.List<List> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(allListFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (List L : allListFull)  {
                    if (L.getTitle().toLowerCase().contains(filterPattern)) {
                        filteredList.add(L);
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredList;

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            allList.clear();
            allList.addAll((java.util.List)results.values);
            notifyDataSetChanged();
        }
    };

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public TextView title, itemqty, dateAdded;

        String userId;
        String username;

        public ViewHolder(@NonNull View itemView, Context ctx, RecyclerViewInterface recyclerViewInterface) {
            super(itemView);
            context = ctx;

            title = itemView.findViewById(R.id.list_title);
            itemqty = itemView.findViewById(R.id.items_qty);
            dateAdded = itemView.findViewById(R.id.dateadded);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (recyclerViewInterface != null)  {
                        int pos = getAdapterPosition();

                        if (pos != RecyclerView.NO_POSITION)    {
                            recyclerViewInterface.onItemClick(pos);
                        }
                    }
                }
            });


        }

    }

}