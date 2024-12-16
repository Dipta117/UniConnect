package com.example.uniconnect;

import android.content.Context;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {

    private Context context;
    private ArrayList<Message> messageList;

    public ChatAdapter(Context context, ArrayList<Message> messageList) {
        this.context = context;
        this.messageList = messageList;
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.chat_item, parent, false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        Message message = messageList.get(position);
        holder.senderTextView.setText(message.getSender());

        String messageContent = message.getContent();

        // Check if the message is a link
        if (isValidLink(messageContent)) {
            // If it's a link, format it as a clickable link
            holder.messageTextView.setText(Html.fromHtml("<a href=\"" + messageContent + "\">" + messageContent + "</a>"));
            holder.messageTextView.setMovementMethod(LinkMovementMethod.getInstance()); // Make it clickable
        } else {
            // Otherwise, just set the message as plain text
            holder.messageTextView.setText(messageContent);
        }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    public static class ChatViewHolder extends RecyclerView.ViewHolder {
        TextView senderTextView, messageTextView;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            senderTextView = itemView.findViewById(R.id.senderTextView);
            messageTextView = itemView.findViewById(R.id.messageTextView);
        }
    }

    private boolean isValidLink(String messageText) {
        // Simple regex to detect if the message contains a URL
        return messageText.startsWith("http://") || messageText.startsWith("https://");
    }
}
