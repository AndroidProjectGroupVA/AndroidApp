package com.example.androidapp.activities;

import static android.content.ContentValues.TAG;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.androidapp.R;
import com.example.androidapp.activities.utilities.Constants;
import com.example.androidapp.activities.utilities.PreferenceManager;
import com.example.androidapp.databinding.ActivitySignInBinding;
import com.example.androidapp.databinding.ActivitySignUpImgBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;

public class SignUpImgActivity extends AppCompatActivity {
    private ActivitySignUpImgBinding binding;
    private String endcodeedImage;
    private PreferenceManager preferenceManager;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        binding = ActivitySignUpImgBinding.inflate(getLayoutInflater());
        preferenceManager = new PreferenceManager(this);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        setContentView(binding.getRoot());
        ScrollView scrollView = findViewById(R.id.signUp_img_main);
        Glide.with(this)
                .load(R.drawable.logo) // Đường dẫn đến hình ảnh của bạn
                .into(new CustomTarget<Drawable>() {
                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                        binding.signInImageView.setBackground(resource);
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                        // Xử lý khi hình ảnh bị xóa khỏi view
                    }
                });
        binding.signUpImgUserAvatar.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            pickImage.launch(intent);
        });
        setListener();
    }

    private Boolean isValidate(){
        if (endcodeedImage == null){
            showToast("Vui lòng chọn ảnh");
            return false;
        }
        else if (binding.signUpImgEdtName.getText().toString().isEmpty()){
            showToast("Vui lòng nhập tên");
            return false;
        }
        return true;
    }

    private void showToast(String Message){
        Toast.makeText(this, Message, Toast.LENGTH_SHORT).show();
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
                            binding.signUpImgUserAvatar.setImageBitmap(bitmap);
                            endcodeedImage = enCodeImage(bitmap);
                            binding.textAddImg.setVisibility(View.GONE);
                        }
                        catch (FileNotFoundException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }
    );

    private void setListener(){
        binding.signUpImgBtnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isValidate()){
                    signup();
                }
            }
        });
    }
    private void signup(){
        //khai bao doi tuong firebase
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        //Tao hash map luu tru thong tin tu textview
        HashMap<String, Object> user = new HashMap<>();
        //put value tu textview vao hashmap

        Intent getintent = getIntent();
        Bundle bundle = getintent.getBundleExtra("newUser");
        String username = bundle.getString("username");
        String password = bundle.getString("password");
        String email = bundle.getString("email");
        String phone = bundle.getString("phone");
        String nameDisplay = binding.signUpImgEdtName.getText().toString();
        user.put(Constants.KEY_NAME, username);
        user.put(Constants.KEY_PASSWORD, password);
        user.put(Constants.KEY_EMAIL, email);
        user.put(Constants.KEY_PHONE, phone);
        user.put(Constants.KEY_IMAGE, endcodeedImage);
        user.put(Constants.KEY_NAME_DISPLAY, nameDisplay);

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            //logcat fixbug
                            Log.d(TAG, "createUserWithEmail:success");
                            //FirebaseUser user = mAuth.getCurrentUser();
                        } else {
                            // If sign in fails, display a message to the user.
                            //logcat fixbug
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
//                            Toast.makeText(getApplicationContext(), "Authentication failed.",
//                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        //tham chieu toi collection users trong firebase de luu tru thong tin
        database.collection(Constants.KEY_COLLECTION_USERS)
                .add(user)
                .addOnSuccessListener(documentReference -> {
                    //luu thong tin vao preference(Sharepreference_
                    //preference luu thong tin dang ki vao 1 tep tin trong ung dung
                    preferenceManager.putBoolean(Constants.KEY_IS_SIGNED_IN, true);
                    preferenceManager.putString(Constants.KEY_USER_ID, documentReference.getId());
                    preferenceManager.putString(Constants.KEY_NAME, username);
                    preferenceManager.putString(Constants.KEY_EMAIL, email);
                    preferenceManager.putString(Constants.KEY_PHONE, phone);
                    preferenceManager.putString(Constants.KEY_IMAGE, endcodeedImage);
                    preferenceManager.putString(Constants.KEY_NAME_DISPLAY, nameDisplay);
                    //tao intent
                    Intent intent = new Intent(getApplicationContext(), SignInActivity.class);
                    //tao 1 task moi de chuyen den MainActivity va xoa cac activity truoc do
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    Toast.makeText(this, "Đăng ký thành công", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    showToast(e.toString());
                });
    }
    private Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }
    public void buttonRotate(View view) {
        Bitmap originalBitmap = ((BitmapDrawable) binding.signUpImgUserAvatar.getDrawable()).getBitmap();
        Bitmap rotatedBitmap = rotateImage(originalBitmap, 90);
        binding.signUpImgUserAvatar.setImageBitmap(rotatedBitmap);
        endcodeedImage = enCodeImage(rotatedBitmap);
    }

    // Tat ban phim ao khi bam ra ngoai
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                    v.clearFocus();
                    hideKeyboard(v);
                }
            }
        }
        return super.dispatchTouchEvent(event);
    }

    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (inputMethodManager != null) {
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

}
