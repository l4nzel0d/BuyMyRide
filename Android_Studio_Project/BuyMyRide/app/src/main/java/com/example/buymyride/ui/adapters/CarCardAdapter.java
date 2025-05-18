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
import com.example.buymyride.data.models.CarCardInfo;

import java.util.List;

public class CarCardAdapter extends RecyclerView.Adapter<CarCardAdapter.CarCardViewHolder> {

    private final List<CarCardInfo> carList;
    private final Context context;
    private final OnFavoriteClickListener favoriteClickListener;

    public interface OnFavoriteClickListener {
        void onFavoriteClick(CarCardInfo car, int position);
    }

    public CarCardAdapter(Context context, List<CarCardInfo> carList, OnFavoriteClickListener favoriteClickListener) {
        this.context = context;
        this.carList = carList;
        this.favoriteClickListener = favoriteClickListener;
    }

    @NonNull
    @Override
    public CarCardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.car_card, parent, false);
        return new CarCardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CarCardViewHolder holder, int position) {
        CarCardInfo car = carList.get(position);
        holder.nameText.setText(car.getName());
        holder.detailsText.setText(car.getDetails());
        holder.priceText.setText(car.getPrice());

        Glide.with(context)
                .load(car.getImageUrl())
                .placeholder(R.drawable.placeholder)
                .into(holder.carImage);

        holder.favoriteIcon.setImageResource(
                car.isFavorite() ? R.drawable.ic_favorites_filled : R.drawable.ic_favorites);

        holder.favoriteIcon.setOnClickListener(v -> {
            if (favoriteClickListener != null) {
                favoriteClickListener.onFavoriteClick(car, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return carList.size();
    }

    public static class CarCardViewHolder extends RecyclerView.ViewHolder {

        ImageView carImage;
        TextView nameText;
        TextView detailsText;
        TextView priceText;
        ImageView favoriteIcon;

        public CarCardViewHolder(@NonNull View itemView) {
            super(itemView);
            carImage = itemView.findViewById(R.id.car_image);
            nameText = itemView.findViewById(R.id.car_name);
            detailsText = itemView.findViewById(R.id.car_details);
            priceText = itemView.findViewById(R.id.car_price);
            favoriteIcon = itemView.findViewById(R.id.image_favorites);
        }
    }
}
