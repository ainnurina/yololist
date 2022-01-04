package com.example.yololist.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.yololist.MainActivity;
import com.example.yololist.R;

import com.example.yololist.data.model.List;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;


public class ListRecyclerAdapter extends RecyclerView.Adapter<ListRecyclerAdapter.ViewHolder> implements Filterable {
    private final RecyclerViewInterface recyclerViewInterface;


    private static Context context;
    private java.util.List<List> allList; //examplelist
    private java.util.List<List> allListFull;

    FirebaseFirestore firestore;
    FirebaseAuth auth;

    public ListRecyclerAdapter(Context context, java.util.List<List> allList, RecyclerViewInterface recyclerViewInterface) {

        this.context = context;
        this.allList = allList;
        this.recyclerViewInterface = recyclerViewInterface;
        allListFull = new ArrayList<>(allList);
        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

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
        viewHolder.txt_option.setOnClickListener(view -> {
            PopupMenu popupMenu = new PopupMenu(context, viewHolder.txt_option);
            popupMenu.inflate(R.menu.option_menu);
            popupMenu.setOnMenuItemClickListener(menuItem -> {
                switch (menuItem.getItemId()) {
                    case R.id.menu_edit:
                        //Intent intent = new Intent(context, MainActivity.class);
                        //intent.putExtra("EDIT", list);
                        //context.startActivity(intent);
                        break;

                    case R.id.menu_remove:

                        FirebaseDatabase db =FirebaseDatabase.getInstance();
                        DatabaseReference databaseReference = db.getReference(List.class.getSimpleName());
                        databaseReference.child(list.getListid()).removeValue().addOnSuccessListener(suc -> {
                            Toast.makeText(context, "Record is removed", Toast.LENGTH_SHORT).show();
                            notifyItemRemoved(position);
                            databaseReference.child(list.getListid()).removeValue();
                        }).addOnFailureListener(er-> {
                            Toast.makeText(context, ""+er.getMessage(), Toast.LENGTH_SHORT).show();
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

        viewHolder.dateAdded.setText(timeAgo);

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
        public TextView title, itemqty, dateAdded, txt_option;

        String userId;
        String username;

        public ViewHolder(@NonNull View itemView, Context ctx, RecyclerViewInterface recyclerViewInterface) {
            super(itemView);
            context = ctx;

            title = itemView.findViewById(R.id.list_title);
            itemqty = itemView.findViewById(R.id.items_qty);
            dateAdded = itemView.findViewById(R.id.dateadded);
            txt_option = itemView.findViewById(R.id.txt_option);

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