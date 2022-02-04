package com.example.ToListApp.ui;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ToListApp.R;
import com.example.ToListApp.model.Items;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

public class ItemUpdateAdapter extends RecyclerView.Adapter<ItemUpdateAdapter.ViewHolder> {
    private OnItemClickListener listener;

    private Context context;
    private List<Items> allItems;
    private FirebaseFirestore firestore;
    private int countchecked = 0;
    private String list_docid;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference collectionReferenceL = db.collection("List");

    interface OnItemCheckListener {
        void onItemCheck(Items item);

        void onItemUnCheck(Items item);
    }


    public ItemUpdateAdapter(Context context, List<Items> allItems) {
        this.context = context;
        this.allItems = allItems;
    }

    @NonNull
    @Override
    public ItemUpdateAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.update_item_row, viewGroup, false);
        firestore = FirebaseFirestore.getInstance();
        return new ViewHolder(view, context);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Items item = allItems.get(position);
        holder.itemName.setText(item.getItemName());
        holder.itemQty.setText("-  " + item.getItemQty());

        //count item yg status = 1
        if (item.getStatus() == 1)  {
            countchecked++;
        }

        //get document id
        String ItemID = allItems.get(position).getItemid();
        final String[] itemdocId = new String[1];

        //find document id for item
        firestore.collection("Items").whereEqualTo("itemid", ItemID)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException e) {

                        if (e != null) {
                            Log.d("", "Error : " + e.getMessage());
                        }
                        for (DocumentChange docI : value.getDocumentChanges()) {
                            itemdocId[0] = docI.getDocument().getId();
                        }
                    }
                });
        //find document id for list
        collectionReferenceL.whereEqualTo("listid", item.getListid())
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        for (DocumentChange docL : value.getDocumentChanges()) {
                            list_docid = docL.getDocument().getId();
                        }
                    }
                });


        //checkbox
        holder.mCheckBox.setChecked(toBoolean(item.getStatus()));
        holder.mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    firestore.collection("Items").document(itemdocId[0]).update("status", 1);

                    countchecked++;
                    if (countchecked == allItems.size())    {
                        firestore.collection("List").document(list_docid).update("statusList", "Completed");

                    }

                } else if (!isChecked) {
                    firestore.collection("Items").document(itemdocId[0]).update("status", 0);

                    countchecked--;
                    if (countchecked != allItems.size()) {
                        firestore.collection("List").document(list_docid).update("statusList", "In Progress");
                    }
                }


            }
        });

        //remove
        holder.buttonminus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                firestore.collection("Items").document(itemdocId[0]).delete();
                allItems.remove(position);
                notifyItemRemoved(position);
            }
        });


    }

    private boolean toBoolean(int status) {
        return status != 0;
    }

    @Override
    public int getItemCount() {
        return allItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView itemName, itemQty;
        public Button buttonminus;
        CheckBox mCheckBox;
        boolean checked;


        public ViewHolder(View itemView, Context ctx) {
            super(itemView);
            context = ctx;

            itemName = itemView.findViewById(R.id.item_name);
            itemQty = itemView.findViewById(R.id.item_qty);
            buttonminus = itemView.findViewById(R.id.buttonminus);
            mCheckBox = (CheckBox) itemView.findViewById(R.id.checkedbox);


        }

    }

    public interface OnItemClickListener {
        void onItemClick(DocumentSnapshot documentSnapshot, int position);

    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
}