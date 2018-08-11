package com.backing.backingapp.widget;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.backing.backingapp.R;
import com.backing.backingapp.data.dto.WidgetDto;

import java.util.List;

public class WidgetAdapter extends RecyclerView.Adapter<WidgetAdapter.ViewHolder> {
    private List<WidgetDto> ingredients;
    private IngredientsConfigureActivity.Widgetable widgetable;

    WidgetAdapter(List<WidgetDto> ingredients, IngredientsConfigureActivity.Widgetable widgetable) {
        this.ingredients = ingredients;
        this.widgetable = widgetable;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_widget, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.textViewWidget.setText(ingredients.get(position).getName());
        holder.itemView.setOnClickListener
                (view -> widgetable.clickEvent(ingredients.get(holder.getAdapterPosition()).getId()));
    }

    @Override
    public int getItemCount() {
        if (ingredients != null) {
            return ingredients.size();
        } else {
            return 0;
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewWidget;

        public ViewHolder(View itemView) {
            super(itemView);
            textViewWidget = (TextView)itemView.findViewById(R.id.textView_widget);
        }
    }
}
