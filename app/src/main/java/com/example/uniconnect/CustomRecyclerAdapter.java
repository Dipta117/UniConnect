package com.example.uniconnect;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

public class CustomRecyclerAdapter extends RecyclerView.Adapter<CustomRecyclerAdapter.ViewHolder> {

    private Context context;
    private List<Model> newCards;

    public CustomRecyclerAdapter(Context context, List<Model> newCards) {
        this.context = context;
        this.newCards = newCards;
    }

    @NonNull
    @Override
    public CustomRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.news_card, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final CustomRecyclerAdapter.ViewHolder viewHolder, final int position) {
        Model model = newCards.get(position);

        // Set text fields
        viewHolder.txt_title.setText(model.getTitle());
        viewHolder.txt_desc.setText(model.getDescription());
viewHolder.txt_pub.setText(model.getAuthor());

        // Set button click listener
        viewHolder.btn_more.setOnClickListener(view -> {
            Intent intent = new Intent(context, WebViewer.class);
            intent.putExtra("URL", model.getUrl());
            context.startActivity(intent);
        });

        // Load image using Picasso
        String imageUrl = model.getUrlToImage() != null ? model.getUrlToImage() : "";
        Picasso.get()
                .load(imageUrl)
                .placeholder(R.drawable.a) // Placeholder image
                .error(R.drawable.b)       // Error image
                .into(viewHolder.imageView, new Callback() {
                    @Override
                    public void onSuccess() {
                        Log.d("Picasso", "Image loaded successfully");
                    }

                    @Override
                    public void onError(Exception e) {
                        Log.e("Picasso", "Error loading image", e);
                    }
                });
    }

    @Override
    public int getItemCount() {
        return newCards.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;
        public TextView txt_title;
        public TextView txt_desc;
        public TextView txt_time,txt_pub;

        public Button btn_more;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.img_url);
            txt_title = itemView.findViewById(R.id.txt_title);
            txt_desc = itemView.findViewById(R.id.txt_desc);
            btn_more = itemView.findViewById(R.id.btn_more);

            txt_pub=itemView.findViewById(R.id.txt_publisher);
        }
    }
}
