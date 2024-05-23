package com.example.androidapp.activities;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.ScrollView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.ReturnThis;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.androidapp.R;
import com.example.androidapp.databinding.ActivitySignInBinding;
import com.example.androidapp.databinding.ActivitySignUpBinding;

public class SignUpActivity extends AppCompatActivity {
    private ActivitySignUpBinding binding;
    private String endcodeedImage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        //setContentView(R.layout.activity_sign_up);
        setListener();
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.signUp_main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        ScrollView scrollView = findViewById(R.id.signUp_main);
        Glide.with(this)
                .load(R.drawable.background1) // Đường dẫn đến hình ảnh của bạn
                .into(new CustomTarget<Drawable>() {
                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                        scrollView.setBackground(resource);
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                        // Xử lý khi hình ảnh bị xóa khỏi view
                    }
                });
    }
    private void setListener(){
        binding.signUpBtnSignUp.setOnClickListener(v -> {
            if (isValidSignupDetail()){
                signup();
            }
        });
    }
    private void showToast(String Message){
        Toast.makeText(this, Message, Toast.LENGTH_SHORT).show();
    }
    private void signup(){

    }
    private Boolean isValidSignupDetail(){
        if(binding.signUpEdtUsername.getText().toString().isEmpty()){
            showToast("Bạn chưa nhập tên đăng nhập");
            return false;
        }
        else if(binding.signUpEdtPassword.getText().toString().isEmpty()){
            showToast("Bạn chưa nhập mật khẩu");
            return false;
        }
        else if (!Patterns.EMAIL_ADDRESS.matcher(binding.signUpEdtEmail.getText().toString()).matches()) {
            showToast("Email không hợp lệ");
        }
        else if(binding.signUpEdtPhone.getText().toString().isEmpty()){
            showToast("Bạn chưa nhập số điện thoại");
            return false;
        }
        return true;
    }
    private void loading(Boolean isLoading){
        if (isLoading){
            binding.signUpBtnSignUp.setVisibility(View.INVISIBLE);
        }
        else{
            binding.signUpBtnSignUp.setVisibility(View.VISIBLE);
        }
    }
}