package com.example.ToList.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ToList.MainActivity;
import com.example.ToList.R;

import com.example.ToList.UpdateYololistActivity;
import com.example.ToList.model.List;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
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
        //allList.clear();
        List list = allList.get(position);
        viewHolder.title.setText(list.getTitle());
        viewHolder.itemqty.setText(""+list.getTotitem()+" items");

        viewHolder.budgetshop.setText("Estimate budget RM"+list.getTotalbudget());

        viewHolder.dateshop.setText("Shop on "+list.getDatePlan());
        viewHolder.placeshop.setText("Shop at "+list.getShopName());
        viewHolder.txt_option.setOnClickListener(view -> {
            PopupMenu popupMenu = new PopupMenu(context, viewHolder.txt_option);
            popupMenu.inflate(R.menu.option_menu);
            popupMenu.setOnMenuItemClickListener(menuItem -> {
                switch (menuItem.getItemId()) {
                    case R.id.menu_share:
                        Intent intent = new Intent(context, UpdateYololistActivity.class);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        }
                        intent.putExtra("Title", allList.get(position).getTitle());
                        intent.putExtra("ListID", allList.get(position).getListid());
                        intent.putExtra("ItemQty", allList.get(position).getTotitem());
                        intent.putExtra("DateAdded", allList.get(position).getTimeAdded());
                        context.startActivity(intent);
                        break;

                    case R.id.menu_remove:
                        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

                        String LID = allList.get(position).getListid();

                        firebaseFirestore.collection("List").whereEqualTo("listid", LID)
                                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                    @Override
                                    public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                                        if (e != null) {
                                            Log.d("", "Error : " + e.getMessage());
                                        }
                                        for (DocumentChange docL : documentSnapshots.getDocumentChanges()) {
                                            docL.getDocument().getId();

                                            firebaseFirestore.collection("List").document(docL.getDocument().getId())
                                                    .delete()
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {

                                                            firebaseFirestore.collection("Items").whereEqualTo("listid", allList.get(position).getListid())
                                                                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                                                        @Override
                                                                        public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                                                                            if (e != null) {
                                                                                Log.d("", "Error : " + e.getMessage());
                                                                            }
                                                                            for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {
                                                                                doc.getDocument().getId();

                                                                                firebaseFirestore.collection("Items").document(doc.getDocument().getId())
                                                                                        .delete()
                                                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                            @Override
                                                                                            public void onComplete(@NonNull Task<Void> task) {


                                                                                            }
                                                                                        })
                                                                                        .addOnFailureListener(new OnFailureListener() {
                                                                                            @Override
                                                                                            public void onFailure(@NonNull Exception e) {
                                                                                                //pd.dismiss();
                                                                                                Toast.makeText(context, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                                                            }
                                                                                        });
                                                                            }

                                                                        }
                                                                    });

                                                            if (task.isSuccessful())    {
                                                                Toast.makeText(context, "List has been deleted from Database.", Toast.LENGTH_SHORT).show();
                                                                Intent intent = new Intent(context, MainActivity.class);
                                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                                                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                                }
                                                                context.startActivity(intent);
                                                            } else  {
                                                                Toast.makeText(context, "Fail to delete the course. ", Toast.LENGTH_SHORT).show();
                                                            }
                                                        }
                                                    }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    //pd.dismiss();
                                                    Toast.makeText(context, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        }
                                    }
                                });





                        break;
                }
                return false;
            });
            popupMenu.show();
        });

        //String timeAgo = (String) DateUtils.getRelativeTimeSpanString(list.
        //       getTimeAdded().getSeconds()*1000);
        //viewHolder.dateAdded.setText(list.getTimeAdded());
        String timeAgo = (String) DateUtils.getRelativeTimeSpanString(list.getTimeAdded().getSeconds()*1000);

        viewHolder.dateAdded.setText("Created at "+timeAgo);

    }

    @Override
    public int getItemCount() {
        return allList.size();
    }

    /* search */

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
        public TextView title, itemqty, dateAdded, txt_option, placeshop, dateshop, budgetshop;

        String userId;
        String username;

        public ViewHolder(@NonNull View itemView, Context ctx, RecyclerViewInterface recyclerViewInterface) {
            super(itemView);
            context = ctx;

            title = itemView.findViewById(R.id.list_title);
            itemqty = itemView.findViewById(R.id.items_qty);
            dateAdded = itemView.findViewById(R.id.dateadded);
            txt_option = itemView.findViewById(R.id.txt_option);
            placeshop = itemView.findViewById(R.id.placetoshop);
            dateshop = itemView.findViewById(R.id.datetoshop);
            budgetshop = itemView.findViewById(R.id.budgettoshop);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (recyclerViewInterface != null)  {
                        int pos = getBindingAdapterPosition();

                        if (pos != RecyclerView.NO_POSITION)    {
                            recyclerViewInterface.onItemClick(pos);
                        }
                    }
                }
            });


        }

    }

}