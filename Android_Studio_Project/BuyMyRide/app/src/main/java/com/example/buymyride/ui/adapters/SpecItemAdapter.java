package com.example.buymyride.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.buymyride.data.models.SpecItem;
import com.example.buymyride.R;

import java.util.List;

public class SpecItemAdapter extends RecyclerView.Adapter<SpecItemAdapter.SpecItemViewHolder> {
    private List<SpecItem> specItems;

    public SpecItemAdapter(List<SpecItem> specItems) {
        this.specItems = specItems;
    }

    @NonNull
    @Override
    public SpecItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.spec_row_item, parent, false);
        return new SpecItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SpecItemViewHolder holder, int position) {
        SpecItem item = specItems.get(position);
        holder.specValueTextView.setText(item.specName());
        holder.specValueTextView.setText(item.specValue());
    }

    @Override
    public int getItemCount() {
        return specItems.size();
    }

    public static class SpecItemViewHolder extends RecyclerView.ViewHolder {
        TextView specNameTextView;
        TextView specValueTextView;


        public SpecItemViewHolder(@NonNull View itemView) {
            super(itemView);
            specNameTextView = itemView.findViewById(R.id.text_spec_name);
            specValueTextView = itemView.findViewById(R.id.text_spec_value);
        }
    }
}
