package com.example.uniconnect;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CustomRecyclerAdapter2 extends RecyclerView.Adapter<CustomRecyclerAdapter2.ViewHolder> {

    private Context context;
    private List<ScholarModel> modelList;

    public CustomRecyclerAdapter2(Context context, List<ScholarModel> modelList) {
        this.context = context;
        this.modelList = modelList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.scholar_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ScholarModel model = modelList.get(position);

        holder.titleTextView.setText(model.getTitle());
        holder.snippetTextView.setText(model.getSnippet());
        holder.displayedLinkTextView.setText(model.getDisplayedLink());


        holder.readMoreButton.setOnClickListener(view -> {
            Intent intent = new Intent(context, WebViewer.class);
            intent.putExtra("URL", model.getTitleLink());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return modelList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView titleTextView;
        public TextView snippetTextView;
        public TextView displayedLinkTextView;
        public Button readMoreButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.title);
            snippetTextView = itemView.findViewById(R.id.snippet);
            displayedLinkTextView = itemView.findViewById(R.id.displayedLink);
            readMoreButton = itemView.findViewById(R.id.readMoreButton);
        }
    }
}
