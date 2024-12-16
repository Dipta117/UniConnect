package com.example.uniconnect;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {

    private ArrayList<Post> postList;
    private Context context;
    private DatabaseReference databaseReference;

    public PostAdapter(ArrayList<Post> postList, Context context) {
        this.postList = postList;
        this.context = context;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_post, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {


        Post post = postList.get(position);


        holder.userNameTextView.setText(post.getUserName());
        holder.postTextView.setText(Html.fromHtml(post.getText(), Html.FROM_HTML_MODE_LEGACY));
        holder.postTextView.setMovementMethod(LinkMovementMethod.getInstance());
        Linkify.addLinks(holder.postTextView, Linkify.WEB_URLS);
        holder.commentButton.setOnClickListener(v -> {
            if (context instanceof AppCompatActivity) {
                AppCompatActivity activity = (AppCompatActivity) context;
                CommentBottomSheetFragment commentBottomSheetFragment = new CommentBottomSheetFragment();
                commentBottomSheetFragment.setPostIdAndName(post.getPostId(), post.getUserName());
                commentBottomSheetFragment.show(activity.getSupportFragmentManager(), "CommentBottomSheet");
            }
        });

        holder.menuImageView.setOnClickListener(view -> showPopupMenu(view, post, holder));
    }

    private void showPopupMenu(View view, Post post, PostViewHolder holder) {
        PopupMenu popupMenu = new PopupMenu(context, view);
        popupMenu.inflate(R.menu.menu_post);
        popupMenu.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.action_edit:
                    showEditPostView(holder, post);
                    return true;
                case R.id.action_delete:
                    deletePost(post);
                    return true;
                default:
                    return false;
            }
        });
        popupMenu.show();
    }

    private void showEditPostView(PostViewHolder holder, Post post) {
        holder.postTextView.setVisibility(View.GONE);
        holder.editPostEditText.setVisibility(View.VISIBLE);
        holder.editPostEditText.setText(post.getText());
        holder.savePostButton.setVisibility(View.VISIBLE);
        holder.savePostButton.setOnClickListener(v -> {
            String updatedText = holder.editPostEditText.getText().toString().trim();
            if (!updatedText.isEmpty()) {
                updatePost(post, updatedText);
                holder.postTextView.setVisibility(View.VISIBLE);
                holder.editPostEditText.setVisibility(View.GONE);
                holder.savePostButton.setVisibility(View.GONE);
            } else {
                Toast.makeText(context, "Post content cannot be empty", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updatePost(Post post, String updatedText) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        String institution = sharedPreferences.getString("institution", null);
        databaseReference = FirebaseDatabase.getInstance().getReference("ProfileInfo").child(institution).child("Posts");

        post.setText(updatedText);
        databaseReference.child(post.getPostId()).setValue(post).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(context, "Post updated successfully", Toast.LENGTH_SHORT).show();
                notifyDataSetChanged();
            } else {
                Toast.makeText(context, "Failed to update post", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deletePost(Post post) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        String institution = sharedPreferences.getString("institution", null);
        databaseReference = FirebaseDatabase.getInstance().getReference("ProfileInfo").child(institution).child("Posts");

        databaseReference.child(post.getPostId()).removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(context, "Post deleted successfully", Toast.LENGTH_SHORT).show();
                postList.remove(post);
                notifyDataSetChanged();
            } else {
                Toast.makeText(context, "Failed to delete post", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    public static class PostViewHolder extends RecyclerView.ViewHolder {
        TextView userNameTextView, postTextView;
        EditText editPostEditText;
        Button commentButton, savePostButton;
        ImageView menuImageView;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            userNameTextView = itemView.findViewById(R.id.userNameTextView);
            postTextView = itemView.findViewById(R.id.postTextView);
            editPostEditText = itemView.findViewById(R.id.editPostEditText);
            commentButton = itemView.findViewById(R.id.commentButton);
            savePostButton = itemView.findViewById(R.id.savePostButton);
            menuImageView = itemView.findViewById(R.id.menuImageView);

        }
    }
}
