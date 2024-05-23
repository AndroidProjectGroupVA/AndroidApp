package com.example.androidapp.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.util.Patterns;
import android.view.View;
import android.widget.ScrollView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.androidapp.R;
import com.example.androidapp.activities.utilities.Constants;
import com.example.androidapp.activities.utilities.PreferenceManager;
import com.example.androidapp.databinding.ActivitySignUpBinding;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;

public class SignUpActivity extends AppCompatActivity {
    private ActivitySignUpBinding binding;
    private PreferenceManager preferenceManager;
    private String endcodeedImage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManager = new PreferenceManager(getApplicationContext());
        //setContentView(R.layout.activity_sign_up);
        setListener();
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.signUp_main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        ScrollView scrollView = findViewById(R.id.signUp_main);

        //glide xu li do phan giai anh
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

    //set event btn signup
    private void setListener(){
        //binding tham chieu toi btn signup
        binding.signUpBtnSignUp.setOnClickListener(v -> {
            if (isValidSignupDetail()){
                signup();
            }
        });
//        binding.signUpImgAvatar.setOnClickListener(v -> {
//            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//            pickImage.launch(intent);
//        });
    }

    //thong bao
    private void showToast(String Message){
        Toast.makeText(this, Message, Toast.LENGTH_SHORT).show();
    }
    private void signup(){
        //animation btn signup -- se thiet ke sau
        loading(true);

        //khai bao doi tuong firebase
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        //Tao hash map luu tru thong tin tu textview
        HashMap<String, Object> user = new HashMap<>();
        //put value tu textview vao hashmap
        user.put(Constants.KEY_NAME, binding.signUpEdtUsername.getText().toString());
        user.put(Constants.KEY_PASSWORD, binding.signUpEdtPassword.getText().toString());
        user.put(Constants.KEY_EMAIL, binding.signUpEdtEmail.getText().toString());
        user.put(Constants.KEY_PHONE, binding.signUpEdtPhone.getText().toString());

        //tham chieu toi collection users trong firebase de luu tru thong tin
        database.collection(Constants.KEY_COLLECTION_USERS)
                .add(user)
                .addOnSuccessListener(documentReference -> {
                    loading(false);
                    //luu thong tin vao preference(Sharepreference_
                    //preference luu thong tin dang ki vao 1 tep tin trong ung dung
                    preferenceManager.putBoolean(Constants.KEY_IS_SIGNED_IN, true);
                    preferenceManager.putString(Constants.KEY_USER_ID, documentReference.getId());
                    preferenceManager.putString(Constants.KEY_NAME, binding.signUpEdtUsername.getText().toString());
                    preferenceManager.putString(Constants.KEY_EMAIL, binding.signUpEdtEmail.getText().toString());
                    preferenceManager.putString(Constants.KEY_PHONE, binding.signUpEdtPhone.getText().toString());
                    //tao intent
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    //tao 1 task moi de chuyen den MainActivity va xoa cac activity truoc do
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e -> {
                    loading(false);
                    showToast(e.toString());
                });
    }


    //set kich thuoc image
    private String enCodeImage(Bitmap bitmap){
        //set with
        int previewWith = 150;
        //set height
        int previewHeight = bitmap.getHeight() * previewWith / bitmap.getWidth();
        //scale image
        Bitmap previewBitmap = Bitmap.createScaledBitmap(bitmap, previewWith, previewHeight, false);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        previewBitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    //tao 1 intent chon anh tu thu vien
    private final ActivityResultLauncher<Intent> pickImage = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    if (result.getData() != null) {
                        Uri imageuri = result.getData().getData();
                        try {
                            InputStream inputStream = getContentResolver().openInputStream(imageuri);
                            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
//                            binding.signUpImgAvatar.setImageBitmap(bitmap);
//                            endcodeedImage = enCodeImage(bitmap);
                        }
                        catch (FileNotFoundException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }
    );
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
            return false;
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