package com.backing.backingapp.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.backing.backingapp.R;
import com.backing.backingapp.data.dto.DetailDto;
import com.backing.backingapp.fragments.DetailsFragment;
import com.backing.backingapp.fragments.DetailsFragment.OnClickListener;


import java.util.List;

public class DetailsAdapter extends RecyclerView.Adapter<DetailsAdapter.ViewHolder> {
    private OnClickListener listener;
    private List<DetailDto> items;

    public DetailsAdapter() {
    }

    public void swapAdapter(List<DetailDto> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    public void setListener(OnClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_detail, parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        holder.itemView.setOnClickListener(view -> listener.onSelected(
                items.get(holder.getAdapterPosition()).getType(),
                items.get(holder.getAdapterPosition()).getId(),
                items.get(holder.getAdapterPosition()).isVideo()));

        holder.textViewDetails.setText(items.get(position).getText());
        if (items.get(position).isVideo()){
            holder.imageViewVideoDetails.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        if (items == null) {
            return 0;
        }
        return items.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewDetails;
        private ImageView imageViewVideoDetails;

        ViewHolder(View itemView) {
            super(itemView);
            textViewDetails = (TextView)itemView.findViewById(R.id.textView_details);
            imageViewVideoDetails = (ImageView) itemView.findViewById(R.id.imageView_video_details);
        }
    }
}
