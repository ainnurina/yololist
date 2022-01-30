package com.example.ToListApp.ui;

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

import com.example.ToListApp.MainActivity;
import com.example.ToListApp.R;
import com.example.ToListApp.model.Items;
import com.example.ToListApp.model.List;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ListRecyclerAdapter extends RecyclerView.Adapter<ListRecyclerAdapter.ViewHolder> implements Filterable {
    private final RecyclerViewInterface recyclerViewInterface;

    private static Context context;
    private java.util.List<List> allList; //examplelist
    private java.util.List<List> allListFull;
    private java.util.List<Items> allItems = new ArrayList<Items>();

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference collectionReferenceI = db.collection("Items");


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

        long t = list.getDatePlan().getSeconds()*1000;
        SimpleDateFormat sfd = new SimpleDateFormat("dd/MM/yyyy");

        viewHolder.title.setText(list.getTitle());
        viewHolder.budgetshop.setText("Estimate budget RM"+list.getTotalbudget());
        viewHolder.placeshop.setText("Shop at "+list.getShopName()+" on "+sfd.format(new Date(t)));
        String timeAgo = (String) DateUtils.getRelativeTimeSpanString(list.getTimeAdded().getSeconds()*1000);
        viewHolder.dateAdded.setText("Created at "+timeAgo);

        viewHolder.txt_option.setOnClickListener(view -> {
            PopupMenu popupMenu = new PopupMenu(context, viewHolder.txt_option);
            popupMenu.inflate(R.menu.option_menu);
            popupMenu.setOnMenuItemClickListener(menuItem -> {
                switch (menuItem.getItemId()) {
                    //share List
                    case R.id.menu_share:
                        String title = allList.get(position).getTitle();
                        String ListID = allList.get(position).getListid();
                        String dateP = ""+allList.get(position).getDatePlan();

                        //to send data
                        Intent shareIntent = new Intent();
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                            shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        }

                        //get data of the items
                        final String[] allItemName = new String[1];
                        StringBuffer sb = new StringBuffer();
                        collectionReferenceI.whereEqualTo("listid", ListID).get()
                                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                    @Override
                                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                        for (QueryDocumentSnapshot items : queryDocumentSnapshots) {
                                            Items item = items.toObject(Items.class);

                                            allItems.add(item);
                                        }

                                        for (int i = 0; i < allItems.size(); i++)   {
                                            sb.append("" + (i+1) + ". ").append(allItems.get(i).getItemName() + " - " + allItems.get(i).getItemQty()).append('\n');
                                            //Toast.makeText(context, "" + allItems.get(i).getItemName(), Toast.LENGTH_SHORT).show();
                                        }
                                        Toast.makeText(context, "" + sb.toString(), Toast.LENGTH_SHORT).show();
                                        allItemName[0] = sb.toString();

                                        long t = allList.get(position).getDatePlan().getSeconds()*1000;
                                        SimpleDateFormat sfd = new SimpleDateFormat("dd/MM/yyyy");

                                        shareIntent.setAction(Intent.ACTION_SEND);
                                        //shareIntent.putExtra(Intent.EXTRA_TITLE, "List.txt");
                                        shareIntent.setType("text/plain");
                                        shareIntent.putExtra(Intent.EXTRA_TEXT, "Title : " + allList.get(position).getTitle()+
                                                "\nDate : " + sfd.format(new Date(t)) +
                                                "\nShop : " + allList.get(position).getShopName() +
                                                "\n\nList of item :\n" + sb.toString());

                                        context.startActivity(shareIntent);

                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                            }
                        });
                        break;

                        //remove List
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
                                                                Toast.makeText(context, "List has been deleted", Toast.LENGTH_SHORT).show();
                                                                Intent intent = new Intent(context, MainActivity.class);
                                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                                                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                                }
                                                                context.startActivity(intent);
                                                            } else  {
                                                                Toast.makeText(context, "Fail to delete the list", Toast.LENGTH_SHORT).show();
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
            dateAdded = itemView.findViewById(R.id.dateadded);
            txt_option = itemView.findViewById(R.id.txt_option);
            placeshop = itemView.findViewById(R.id.placetoshop);
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