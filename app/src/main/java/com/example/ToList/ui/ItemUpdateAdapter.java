package com.example.ToList.ui;

        import android.content.Context;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.TextView;

        import androidx.annotation.NonNull;
        import androidx.recyclerview.widget.RecyclerView;

        import com.example.ToList.R;
        import com.example.ToList.model.Items;
        import com.google.firebase.firestore.DocumentSnapshot;

        import java.util.List;

public class ItemUpdateAdapter extends RecyclerView.Adapter<ItemUpdateAdapter.ViewHolder> {
    private OnItemClickListener listener;

    private Context context;
    private List<Items> allItems;

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
    }

    @Override
    public int getItemCount() {
        return allItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView itemName;


        public ViewHolder (View itemView, Context ctx)  {
            super(itemView);
            context = ctx;

            itemName = itemView.findViewById(R.id.item_name);
        }

    }

    public interface OnItemClickListener {
        void onItemClick(DocumentSnapshot documentSnapshot, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener)    {
        this.listener = listener;
    }
}