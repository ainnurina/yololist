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
    private FirebaseFirestore firestore;

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

        //get document id
        String ItemID = allItems.get(position).getItemid();
        final String[] itemdocId = new String[1];

        firestore.collection("Items").whereEqualTo("itemid", ItemID)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException e) {

                        if (e != null) {
                            Log.d("", "Error : " + e.getMessage());
                        }
                        for (DocumentChange docL : value.getDocumentChanges()) {
                            itemdocId[0] = docL.getDocument().getId();
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
                } else {
                    firestore.collection("Items").document(itemdocId[0]).update("status", 0);
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

    private boolean toBoolean(int status){
        return status != 0;
    }

    @Override
    public int getItemCount() {
        return allItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView itemName;
        public Button buttonminus;
        CheckBox mCheckBox;
        boolean checked;


        public ViewHolder (View itemView, Context ctx)  {
            super(itemView);
            context = ctx;

            itemName = itemView.findViewById(R.id.item_name);
            buttonminus = itemView.findViewById(R.id.buttonminus);
            mCheckBox = (CheckBox) itemView.findViewById(R.id.checkedbox);

        }

    }

    public interface OnItemClickListener {
        void onItemClick(DocumentSnapshot documentSnapshot, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener)    {
        this.listener = listener;
    }
}