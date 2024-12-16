package com.example.uniconnect;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;

public class ChatActivity extends AppCompatActivity {
private TextView name,emaill,contact,dep;
    private RecyclerView chatRecyclerView;
    private EditText messageInput;
    private Button sendButton;
    private ImageView pimage;
    private ChatAdapter chatAdapter;
    private ArrayList<Message> messageList;
    private DatabaseReference chatRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
name=findViewById(R.id.receiverName);

        chatRecyclerView = findViewById(R.id.chatRecyclerView);
        messageInput = findViewById(R.id.messageInput);
        sendButton = findViewById(R.id.sendButton);
pimage=findViewById(R.id.receiverImage);

        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String role = sharedPreferences.getString("role", null);




        messageList = new ArrayList<>();
        chatAdapter = new ChatAdapter(this, messageList);
        chatRecyclerView.setAdapter(chatAdapter);
        chatRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        ImageView gifImageView = findViewById(R.id.gifImageView);
        Glide.with(this) .asGif() .load(R.raw.chat)
                .into(gifImageView);

        // Get sender and receiver emails (pass these values when starting the activity)
        String senderEmail = getIntent().getStringExtra("senderEmail");
        String receiverEmail = getIntent().getStringExtra("receiverEmail");
        if(role.equals("student"))
        {
            if(senderEmail.equals(receiverEmail))
            {
                pimage.setImageResource(R.drawable.profile);
            }
            else
            {pimage.setImageResource(R.drawable.t);}
        }
        else
        {
            if(senderEmail.equals(receiverEmail))
            {
                pimage.setImageResource(R.drawable.t);
            }
            pimage.setImageResource(R.drawable.profile);
        }
        String nam=getIntent().getStringExtra("receiverName");
name.setText(nam);

        if (senderEmail == null || receiverEmail == null) {
            Toast.makeText(this, "Missing sender or receiver email", Toast.LENGTH_SHORT).show();
            finish(); // Close activity if required data is missing
            return;
        }

        // Initialize Firebase chat reference using emails
        chatRef = getChatReference(senderEmail, receiverEmail);

        // Fetch chat history
        loadChatHistory();

        // Send button click listener
        sendButton.setOnClickListener(v -> {
            String messageText = messageInput.getText().toString().trim();
            if (!messageText.isEmpty()) {
                sendMessage(messageText);
            } else {
                Toast.makeText(ChatActivity.this, "Message cannot be empty!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadChatHistory() {
        chatRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                messageList.clear();
                for (DataSnapshot data : snapshot.getChildren()) {
                    Message message = data.getValue(Message.class);
                    if (message != null) {
                        messageList.add(message);
                    }
                }
                chatAdapter.notifyDataSetChanged();
                chatRecyclerView.scrollToPosition(messageList.size() - 1);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(ChatActivity.this, "Error loading chat: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private DatabaseReference getChatReference(String senderEmail, String receiverEmail) {
        // Sanitize emails
        String sanitizedSender = senderEmail.replace(".", "_").replace("@", "_");
        String sanitizedReceiver = receiverEmail.replace(".", "_").replace("@", "_");

        // Ensure consistent key ordering for both directions
        if (sanitizedSender.compareTo(sanitizedReceiver) > 0) {
            String temp = sanitizedSender;
            sanitizedSender = sanitizedReceiver;
            sanitizedReceiver = temp;
        }

        // Combine emails to create a unique chat reference
        String chatKey = sanitizedSender + "_chat_" + sanitizedReceiver;

        // Return the Firebase chat reference
        return FirebaseDatabase.getInstance().getReference("chat").child(chatKey);
    }

    private void sendMessage(String messageText) {
        // Retrieve sender's name (assuming it's stored in SharedPreferences)
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String senderName = sharedPreferences.getString("name", "User"); // Default to "User" if name not found

        // Check if the message is a link
        if (isValidLink(messageText)) {
            // It's a link, treat it as a URL
            uploadLink(senderName, messageText);
        } else {
            // It's a regular message, store as text
            String messageId = chatRef.push().getKey();
            if (messageId != null) {
                // Create a new message with sender's name and message text
                Message message = new Message(messageId, senderName, messageText);
                chatRef.child(messageId).setValue(message).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        messageInput.setText(""); // Clear the input box
                    } else {
                        Toast.makeText(ChatActivity.this, "Failed to send message.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }

    private void uploadLink(String senderName, String link) {
        String messageId = chatRef.push().getKey();
        if (messageId != null) {
            // Create a new message object with the link
            Message linkMessage = new Message(messageId, senderName, link);
            chatRef.child(messageId).setValue(linkMessage).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    messageInput.setText(""); // Clear the input box
                } else {
                    Toast.makeText(ChatActivity.this, "Failed to send link.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private boolean isValidLink(String messageText) {
        // Simple regex to detect if the message contains a URL
        return messageText.startsWith("http://") || messageText.startsWith("https://");
    }
}
