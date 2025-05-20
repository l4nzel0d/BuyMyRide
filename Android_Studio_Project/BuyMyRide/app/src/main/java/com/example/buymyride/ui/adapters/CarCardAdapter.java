package com.example.buymyride.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.buymyride.R;
import com.example.buymyride.data.models.CarCardModel;

import java.util.ArrayList;
import java.util.List;

public class CarCardAdapter extends RecyclerView.Adapter<CarCardAdapter.CarViewHolder> {

    private Context context;
    private List<CarCardModel> carCardModelList;
    private OnItemClickListener itemClickListener;

    public interface OnItemClickListener {
        void onItemClick(int position); // Only pass position
        void onFavoriteClick(int position); // Only pass position
    }

    public CarCardAdapter(Context context, OnItemClickListener itemClickListener) {
        this.context = context;
        this.itemClickListener = itemClickListener;
        this.carCardModelList = new ArrayList<>();
    }

    @NonNull
    @Override
    public CarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.car_card_item, parent, false);
        return new CarViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CarViewHolder holder, int position) {
        CarCardModel carCardModel = carCardModelList.get(position);

        holder.textMake.setText(carCardModel.getMake());
        holder.textModel.setText(carCardModel.getModel());
        holder.textYear.setText(String.valueOf(carCardModel.getYear()));
        holder.textPrice.setText(String.format("%,d", carCardModel.getPrice()).replace(',', ' ') + " ₽");

        Glide.with(context)
                .load(carCardModel.getImageUrl())
                .into(holder.carImage);

        holder.imageFavorite.setSelected(carCardModel.isFavorite());

        holder.itemView.setOnClickListener(v -> itemClickListener.onItemClick(position)); // Pass position
        holder.imageFavorite.setOnClickListener(v -> itemClickListener.onFavoriteClick(position)); // Pass position
    }

    @Override
    public int getItemCount() {
        return carCardModelList != null ? carCardModelList.size() : 0;
    }

    public void setCarCardModelList(List<CarCardModel> carCardModelList) {
        this.carCardModelList = carCardModelList;
        notifyDataSetChanged();
    }

    public CarCardModel getCarCardModelAt(int position) {
        if (position >= 0 && position < carCardModelList.size()) {
            return carCardModelList.get(position);
        }
        return null; // Or throw an exception if you prefer
    }

    public static class CarViewHolder extends RecyclerView.ViewHolder {
        ImageView carImage;
        TextView textMake;
        TextView textModel;
        TextView textYear;
        TextView textPrice;
        ImageView imageFavorite;

        public CarViewHolder(@NonNull View itemView) {
            super(itemView);
            carImage = itemView.findViewById(R.id.car_image);
            textMake = itemView.findViewById(R.id.text_make);
            textModel = itemView.findViewById(R.id.text_model);
            textYear = itemView.findViewById(R.id.text_year);
            textPrice = itemView.findViewById(R.id.text_price);
            imageFavorite = itemView.findViewById(R.id.image_favorites);
        }
    }
}