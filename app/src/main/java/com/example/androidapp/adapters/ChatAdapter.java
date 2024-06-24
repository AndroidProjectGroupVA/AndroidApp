package com.example.androidapp.adapters;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidapp.databinding.ItemContainerReceivedMessageBinding;
import com.example.androidapp.databinding.ItemContainerSentMessageBinding;
import com.example.androidapp.models.ChatMessage;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final List<ChatMessage> chatMessages;
    private final Bitmap receiverProfileImage;
    private final String senderId;

    public static final int VIEW_TYPE_SENT = 1;
    public static final int VIEW_TYPE_RECEIVED = 2;

    public ChatAdapter(List<ChatMessage> chatMessages, Bitmap receiverProfileImage, String senderId) {
        this.chatMessages = chatMessages;
        this.receiverProfileImage = receiverProfileImage;
        this.senderId = senderId;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_SENT) {
            return new SentMessageViewHolder(
                    ItemContainerSentMessageBinding.inflate(
                            LayoutInflater.from(parent.getContext()),
                            parent,
                            false
                    )
            );
        } else {
            return new ReceivedMessageViewHolder(
                    ItemContainerReceivedMessageBinding.inflate(
                            LayoutInflater.from(parent.getContext()),
                            parent,
                            false
                    )
            );
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ChatMessage chatMessage = chatMessages.get(position);
        if (getItemViewType(position) == VIEW_TYPE_SENT) {
            ((SentMessageViewHolder) holder).setData(chatMessage);
        } else {
            ((ReceivedMessageViewHolder) holder).setData(chatMessage, receiverProfileImage);
        }
    }

    @Override
    public int getItemCount() {
        return chatMessages.size();
    }

    @Override
    public int getItemViewType(int position) {
        return chatMessages.get(position).senderId.equals(senderId) ? VIEW_TYPE_SENT : VIEW_TYPE_RECEIVED;
    }

    static class SentMessageViewHolder extends RecyclerView.ViewHolder {
        private final ItemContainerSentMessageBinding binding;

        SentMessageViewHolder(ItemContainerSentMessageBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void setData(ChatMessage chatMessage) {
            binding.textDateTime.setText(chatMessage.dateTime);
            binding.textDateTimeImg.setText(chatMessage.dateTime);

            if ("image".equals(chatMessage.messageType)) {
                showImageMessage(chatMessage.messageIMG);
            } else {
                showTextMessage(chatMessage.message);
            }
        }

        private void showImageMessage(String encodedImage) {
            Bitmap imageBitmap = decodeImageFromBase64(encodedImage);
            if (imageBitmap != null) {
                binding.imageMessage.setImageBitmap(imageBitmap);
                binding.textMessage.setVisibility(View.GONE);
                binding.imageMessage.setVisibility(View.VISIBLE);
                binding.textDateTime.setVisibility(View.GONE);
                binding.textDateTimeImg.setVisibility(View.VISIBLE);
            }
        }

        private void showTextMessage(String message) {
            binding.textMessage.setText(message);
            binding.textMessage.setVisibility(View.VISIBLE);
            binding.imageMessage.setVisibility(View.GONE);
            binding.textDateTime.setVisibility(View.VISIBLE);
            binding.textDateTimeImg.setVisibility(View.GONE);
        }

        private Bitmap decodeImageFromBase64(String encodedImage) {
            if (!(encodedImage == null)) {
                try {
                    byte[] bytes = Base64.decode(encodedImage, Base64.DEFAULT);
                    return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }
    }

    static class ReceivedMessageViewHolder extends RecyclerView.ViewHolder {
        private final ItemContainerReceivedMessageBinding binding;

        ReceivedMessageViewHolder(ItemContainerReceivedMessageBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void setData(ChatMessage chatMessage, Bitmap receiverProfileImage) {
            binding.textDateTime.setText(chatMessage.dateTime);
            binding.imageAvatar.setImageBitmap(receiverProfileImage);
            binding.textDateTimeImg.setText(chatMessage.dateTime);
            binding.imageAvatarImg.setImageBitmap(receiverProfileImage);

            if ("image".equals(chatMessage.messageType)) {
                showImageMessage(chatMessage.messageIMG);
            } else {
                showTextMessage(chatMessage.message);
            }
        }

        private void showImageMessage(String encodedImage) {
            Bitmap imageBitmap = decodeImageFromBase64(encodedImage);
            if (imageBitmap != null) {
                binding.imageMessage.setImageBitmap(imageBitmap);
                binding.textMessage.setVisibility(View.GONE);
                binding.imageMessage.setVisibility(View.VISIBLE);
                binding.cardView2.setVisibility(View.GONE);
                binding.cardViewImg.setVisibility(View.VISIBLE);
                binding.textDateTime.setVisibility(View.GONE);
                binding.textDateTimeImg.setVisibility(View.VISIBLE);
            }
        }

        private void showTextMessage(String message) {
            binding.textMessage.setText(message);
            binding.textMessage.setVisibility(View.VISIBLE);
            binding.imageMessage.setVisibility(View.GONE);
            binding.cardView2.setVisibility(View.VISIBLE);
            binding.cardViewImg.setVisibility(View.GONE);
            binding.textDateTime.setVisibility(View.VISIBLE);
            binding.textDateTimeImg.setVisibility(View.GONE);
        }

        private Bitmap decodeImageFromBase64(String encodedImage) {
            if (!(encodedImage == null)) {
                try {
                    byte[] bytes = Base64.decode(encodedImage, Base64.DEFAULT);
                    return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }
    }
}

