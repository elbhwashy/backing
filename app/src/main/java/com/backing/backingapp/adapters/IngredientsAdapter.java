package com.backing.backingapp.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.backing.backingapp.R;
import com.backing.backingapp.data.dto.IngredientDto;

import java.util.List;

public class IngredientsAdapter extends RecyclerView.Adapter<IngredientsAdapter.ViewHolder> {
    private List<IngredientDto> itemIngredients;

    public IngredientsAdapter() {
    }

    public void swapAdapter(List<IngredientDto> item) {
        this.itemIngredients = item;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ingredient, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        IngredientDto ingredient = itemIngredients.get(position);
        if (ingredient != null) {
            String text = ingredient.getIngredient() + " " + ingredient.getIngredientQuantity() + " " + ingredient.getIngredientMeasure();
            holder.textViewIngredients.setText(text);
        }
    }

    @Override
    public int getItemCount() {
        if (itemIngredients == null) {
            return 0;
        }
        return itemIngredients.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewIngredients;

        ViewHolder(View itemView) {
            super(itemView);
            textViewIngredients = (TextView)itemView.findViewById(R.id.textView_ingredient);
        }
    }
}
