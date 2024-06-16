package com.example.androidapp.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.androidapp.R;
import com.example.androidapp.databinding.ActivityChatBinding;
import com.example.androidapp.models.User;
import com.example.androidapp.utilities.Constants;

public class ChatActivity extends AppCompatActivity {
    private ActivityChatBinding binding;
    private User receiverUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.chat_main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        setListeners();
        loadReceiverDetails();
    }

    private void loadReceiverDetails() {
        receiverUser = (User) getIntent().getSerializableExtra(Constants.KEY_USER);
        if (receiverUser.name == null){
            binding.chatName.setText("Nguoi dung");
        }
        else{
            binding.chatName.setText(receiverUser.name);
        }
    }

//    private void setListeners() {
//        binding.chatBtnBack.setOnClickListener(v -> onBackPressed());
//    }

    private void setListeners() {
        binding.chatBtnBack.setOnClickListener(v -> navigateToChatFragment());
    }

    private void navigateToChatFragment() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("openChatFragment", true);
        startActivity(intent);
        finish();
    }
}