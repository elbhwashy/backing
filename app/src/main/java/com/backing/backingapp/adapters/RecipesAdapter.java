package com.backing.backingapp.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.backing.backingapp.R;
import com.backing.backingapp.data.dto.RecipeDto;
import com.backing.backingapp.fragments.MasterRecipesFragment.OnRecipeClickListener;

import java.util.List;

public class RecipesAdapter extends RecyclerView.Adapter<RecipesAdapter.ViewHolder> {

    private List<RecipeDto> recipes;
    private OnRecipeClickListener listener;

    public RecipesAdapter() {
    }

    public void swapAdapter(List<RecipeDto> recipes) {
        this.recipes = recipes;
        notifyDataSetChanged();
    }

    public void setListener(OnRecipeClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recipe, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        holder.textViewRecipe.setText(recipes.get(position).getName());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onRecipeSelected(recipes.get(holder.getAdapterPosition()).getId());
            }
        });
    }

    @Override
    public int getItemCount() {
        if (recipes == null) {
            return 0;
        }
        return recipes.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewRecipe;

        ViewHolder(View itemView) {
            super(itemView);
            textViewRecipe = (TextView)itemView.findViewById(R.id.textView_recipe);
        }
    }
}
