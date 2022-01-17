package com.example.ToList.ui;

        import android.app.Activity;
        import android.content.Context;
        import android.content.Intent;
        import android.os.Build;
        import android.util.Log;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.Button;
        import android.widget.CheckBox;
        import android.widget.CompoundButton;
        import android.widget.TextView;
        import android.widget.Toast;

        import androidx.annotation.NonNull;
        import androidx.annotation.Nullable;
        import androidx.recyclerview.widget.RecyclerView;

        import com.example.ToList.MainActivity;
        import com.example.ToList.R;
        import com.example.ToList.model.Items;
        import com.google.android.gms.tasks.OnCompleteListener;
        import com.google.android.gms.tasks.OnFailureListener;
        import com.google.android.gms.tasks.Task;
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
        return new ViewHolder(view, context);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Items item = allItems.get(position);
        holder.itemName.setText(item.getItemName());
        holder.buttonminus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

                String ItemID = allItems.get(position).getItemid();

                firebaseFirestore.collection("Items").whereEqualTo("itemid", ItemID)
                        .addSnapshotListener(new EventListener<QuerySnapshot>() {
                            @Override
                            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException e) {
                                if (e != null) {
                                    Log.d("", "Error : " + e.getMessage());
                                }
                                for (DocumentChange docL : value.getDocumentChanges()) {
                                    docL.getDocument().getId();

                                    firebaseFirestore.collection("Items").document(docL.getDocument().getId())
                                            .delete()
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful())    {
                                                        int total = allItems.size() - 1 ; //kena htr value ni utk update dkt list quantity.
                                                        Toast.makeText(context, "Succesful remove "+item.getItemName(), Toast.LENGTH_SHORT).show();

                                                        ((Activity)context).finish();
                                                        ((Activity)context).overridePendingTransition( 0, 0);
                                                        ((Activity)context).startActivity(((Activity)context).getIntent());
                                                        ((Activity)context).overridePendingTransition( 0, 0);

                                                    } else  {
                                                        Toast.makeText(context, "Fail to remove item "+item.getItemName(), Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(context, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }

                            }
                        });

            }
        });

        //bila dia tick, auto refresh.. the item on top!



        /*
        holder.donecheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                holder.checked = b;
            }
        });
        holder.checked = holder.donecheck.isChecked();

        if (holder.checked) {
            item.setItemStatus("done purchased");
            //reference.Child(String.valueOf(i+1).setValue(item));
        }

         */

    }

    @Override
    public int getItemCount() {
        return allItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView itemName;
        public Button buttonminus;
        CheckBox donecheck;
        boolean checked;


        public ViewHolder (View itemView, Context ctx)  {
            super(itemView);
            context = ctx;

            itemName = itemView.findViewById(R.id.item_name);
            buttonminus = itemView.findViewById(R.id.buttonminus);
            donecheck = (CheckBox) itemView.findViewById(R.id.checkedbox);

        }

    }

    public interface OnItemClickListener {
        void onItemClick(DocumentSnapshot documentSnapshot, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener)    {
        this.listener = listener;
    }
}