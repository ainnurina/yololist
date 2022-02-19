package com.example.ToListApp.ui;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ToListApp.R;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class budgetAdapter extends RecyclerView.Adapter<budgetAdapter.ViewHolder> {

    private static Context context;
    private List<com.example.ToListApp.model.List> allList;
    int i = 1;


    public budgetAdapter(Context context, List<com.example.ToListApp.model.List> allList) {
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
        com.example.ToListApp.model.List list = allList.get(position);

        float budgetminusexpenses;
        long t = list.getDatePlan().getSeconds() * 1000;
        //change date format
        SimpleDateFormat sfd = new SimpleDateFormat("dd/MM/yyyy");
        //change float formal to two decimal point
        DecimalFormat df = new DecimalFormat("0.00");

        holder.table_no.setText("" + i);
        holder.table_listname.setText(list.getTitle());
        holder.table_listbudget.setText("RM" + df.format(list.getTotalbudget()));
        holder.table_listexpenses.setText("RM" + df.format(list.getTotalexpenses()));

        budgetminusexpenses = list.getTotalbudget() - list.getTotalexpenses();
        holder.table_listbudgetminusexpenses.setText("RM" + df.format(budgetminusexpenses));
        //set status & count qty status
        if (list.getTotalbudget() >= list.getTotalexpenses()) {
            holder.table_liststatus.setText("within budget");
        } else if (list.getTotalexpenses() >= list.getTotalbudget()) {
            holder.table_liststatus.setText("overbudget");
            holder.table_liststatus.setTextColor(Color.RED);
        }

        holder.table_listdate.setText("" + sfd.format(new Date(t)));

        i++;
    }

    @Override
    public int getItemCount() {
        return allList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView table_no, table_listname, table_listbudget, table_listexpenses, table_liststatus, table_listdate, table_listbudgetminusexpenses;
        TextView sumbudget, sumexpenses, qtylistwithinbudget, qtylistoverbudget, largestlistitle;

        public ViewHolder(@NonNull View itemView, Context context) {
            super(itemView);
            context = context;

            table_no = itemView.findViewById(R.id.table_no);
            table_listname = itemView.findViewById(R.id.table_listname);
            table_listbudget = itemView.findViewById(R.id.table_listbudget);
            table_listexpenses = itemView.findViewById(R.id.table_listexpenses);
            table_liststatus = itemView.findViewById(R.id.table_liststatus);
            table_listdate = itemView.findViewById(R.id.table_listdate);
            table_listbudgetminusexpenses = itemView.findViewById(R.id.table_listbudgetminusexpenses);

        }
    }
}