package com.example.ToList.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ToList.R;

import java.util.List;

public class budgetAdapter extends RecyclerView.Adapter<budgetAdapter.ViewHolder>   {

    private static Context context;
    private List<com.example.ToList.model.List> allList;
    int i = 1;

    public budgetAdapter(Context context, List<com.example.ToList.model.List> allList) {
        this.context = context;
        this.allList = allList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.tablelist_row, viewGroup, false);

        return new ViewHolder(view, context);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            com.example.ToList.model.List list = allList.get(position);
            holder.table_no.setText(""+i);
            holder.table_listname.setText(list.getTitle());
            holder.table_listbudget.setText(""+list.getTotalbudget());
            holder.table_listexpenses.setText(""+list.getTotalexpenses());
            holder.table_liststatus.setText("belom kira");
        i++;
    }

    @Override
    public int getItemCount() {
        return allList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView table_no, table_listname, table_listbudget, table_listexpenses, table_liststatus;

        public ViewHolder(@NonNull View itemView, Context context) {
            super(itemView);
            context = context;

            table_no = itemView.findViewById(R.id.table_no);
            table_listname = itemView.findViewById(R.id.table_listname);
            table_listbudget = itemView.findViewById(R.id.table_listbudget);
            table_listexpenses = itemView.findViewById(R.id.table_listexpenses);
            table_liststatus = itemView.findViewById(R.id.table_liststatus);

        }
    }
}