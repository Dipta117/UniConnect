package com.example.uniconnect;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.text.style.TypefaceSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class CommentBottomSheetFragment extends BottomSheetDialogFragment {

    private EditText commentEditText;
    private Button postCommentButton;
    private TextView existingCommentsTextView;
private ImageView profileImageView;
    private String postId;
    private String commenterName;

    // Set the post ID and commenter name
    public void setPostIdAndName(String postId, String commenterName) {
        this.postId = postId;
        this.commenterName = commenterName;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_comment_bottom_sheet, container, false);

        commentEditText = rootView.findViewById(R.id.commentEditText);
        postCommentButton = rootView.findViewById(R.id.commentButton);
        existingCommentsTextView = rootView.findViewById(R.id.existingCommentsTextView);




        // Load existing comments from Firebase
        loadCommentsFromFirebase();

        // Handle the post comment button click
        postCommentButton.setOnClickListener(v -> {
            String commentText = commentEditText.getText().toString().trim();
            if (!TextUtils.isEmpty(commentText)) {
                addCommentToFirebase(commentText);
            } else {
                Toast.makeText(getContext(), "Comment cannot be empty", Toast.LENGTH_SHORT).show();
            }
        });

        return rootView;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        BottomSheetDialog dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
        dialog.setOnShowListener(dialogInterface -> {
            BottomSheetDialog bottomSheetDialog = (BottomSheetDialog) dialogInterface;
            setupFullHeight(bottomSheetDialog);
        });
        return dialog;
    }

    private void setupFullHeight(BottomSheetDialog bottomSheetDialog) {
        View bottomSheet = bottomSheetDialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
        if (bottomSheet != null) {
            BottomSheetBehavior<View> behavior = BottomSheetBehavior.from(bottomSheet);
            behavior.setPeekHeight(BottomSheetBehavior.PEEK_HEIGHT_AUTO);
            bottomSheet.setLayoutParams(new CoordinatorLayout.LayoutParams(CoordinatorLayout.LayoutParams.MATCH_PARENT, CoordinatorLayout.LayoutParams.MATCH_PARENT));
        }
    }

    private void addCommentToFirebase(String commentText) {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        String institution = sharedPreferences.getString("institution", null);
        String cname = sharedPreferences.getString("name", null);
        DatabaseReference postRef = FirebaseDatabase.getInstance().getReference("ProfileInfo")
                .child(institution)
                .child(postId)
                .child("comments");

        String commentId = postRef.push().getKey();

        if (commentId != null) {
            Comment comment = new Comment(cname, commentText, 0);
            postRef.child(commentId).setValue(comment).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    commentEditText.setText(""); // Clear the input field
                } else {
                    Toast.makeText(getContext(), "Failed to add comment. Please try again.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void loadCommentsFromFirebase() {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        String institution = sharedPreferences.getString("institution", null);

        DatabaseReference commentsRef = FirebaseDatabase.getInstance().getReference("ProfileInfo")
                .child(institution)
                .child(postId)
                .child("comments");

        commentsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                existingCommentsTextView.setText(""); // Clear the current comments
                for (DataSnapshot commentSnapshot : snapshot.getChildren()) {
                    Comment comment = commentSnapshot.getValue(Comment.class);
                    if (comment != null) {
                        appendCommentToTextView(comment.getCommenterName(), comment.getCommentText());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Failed to load comments: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void appendCommentToTextView(String commenterName, String commentText) {
        SpannableString spannableComment = new SpannableString(commenterName + "\n" + commentText + "\n\n");

        // Style the commenter's name
        spannableComment.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, commenterName.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableComment.setSpan(new TypefaceSpan("sans-serif-medium"), 0, commenterName.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableComment.setSpan(new RelativeSizeSpan(1.2f), 0, commenterName.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE); // Increase font size

        // Style the comment text
        spannableComment.setSpan(new TypefaceSpan("sans-serif"), commenterName.length() + 1, spannableComment.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableComment.setSpan(new RelativeSizeSpan(0.9f), commenterName.length() + 1, spannableComment.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE); // Decrease font size

        existingCommentsTextView.append(spannableComment);
    }
}
