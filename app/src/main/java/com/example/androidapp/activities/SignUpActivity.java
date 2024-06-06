package com.example.androidapp.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.util.Base64;
import android.util.Patterns;
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
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.androidapp.R;
import com.example.androidapp.utilities.Constants;
import com.example.androidapp.utilities.PreferenceManager;
import com.example.androidapp.databinding.ActivitySignUpBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;

public class SignUpActivity extends AppCompatActivity {
    private ActivitySignUpBinding binding;
    private PreferenceManager preferenceManager;
    private String endcodeedImage;
    boolean passwordVisible = false;

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

        // Hien thi mat khau
        binding.signUpBtnImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (passwordVisible) {
                    // Ẩn mật khẩu
                    binding.signUpEdtPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    binding.signUpBtnImg.setImageResource(R.drawable.eyesolid); // Đặt hình ảnh "ẩn mật khẩu"
                } else {
                    // Hiển thị mật khẩu
                    binding.signUpEdtPassword.setInputType(InputType.TYPE_CLASS_TEXT);
                    binding.signUpBtnImg.setImageResource(R.drawable.eyeslashsolid); // Đặt hình ảnh "hiển thị mật khẩu"
                }
                passwordVisible = !passwordVisible;
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

//        //khai bao doi tuong firebase
//        FirebaseFirestore database = FirebaseFirestore.getInstance();
//        //Tao hash map luu tru thong tin tu textview
//        HashMap<String, Object> user = new HashMap<>();
//        //put value tu textview vao hashmap
//        user.put(Constants.KEY_NAME, binding.signUpEdtUsername.getText().toString());
//        user.put(Constants.KEY_PASSWORD, binding.signUpEdtPassword.getText().toString());
//        user.put(Constants.KEY_EMAIL, binding.signUpEdtEmail.getText().toString());
//        user.put(Constants.KEY_PHONE, binding.signUpEdtPhone.getText().toString());
//
//        //tham chieu toi collection users trong firebase de luu tru thong tin
//        database.collection(Constants.KEY_COLLECTION_USERS)
//                .add(user)
//                .addOnSuccessListener(documentReference -> {
//                    loading(false);
//                    //luu thong tin vao preference(Sharepreference_
//                    //preference luu thong tin dang ki vao 1 tep tin trong ung dung
//                    preferenceManager.putBoolean(Constants.KEY_IS_SIGNED_IN, true);
//                    preferenceManager.putString(Constants.KEY_USER_ID, documentReference.getId());
//                    preferenceManager.putString(Constants.KEY_NAME, binding.signUpEdtUsername.getText().toString());
//                    preferenceManager.putString(Constants.KEY_EMAIL, binding.signUpEdtEmail.getText().toString());
//                    preferenceManager.putString(Constants.KEY_PHONE, binding.signUpEdtPhone.getText().toString());
//                    //tao intent
//                    Intent intent = new Intent(getApplicationContext(), SignInActivity.class);
//                    //tao 1 task moi de chuyen den MainActivity va xoa cac activity truoc do
//                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                    startActivity(intent);
//                    Toast.makeText(this, "Đăng ký thành công", Toast.LENGTH_SHORT).show();
//                    finish();
//                })
//                .addOnFailureListener(e -> {
//                    loading(false);
//                    showToast(e.toString());
//                });
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
//        firestore.collection(Constants.KEY_COLLECTION_USERS)
//                .whereEqualTo(Constants.KEY_NAME, binding.signUpEdtUsername.getText().toString())
//                .get()
//                .addOnCompleteListener(task -> {
//                    if(task.isSuccessful() && task.getResult() != null && task.getResult().getDocuments().size() > 0){
//                        Toast.makeText(this, "Tên đăng nhập đã tồn tại", Toast.LENGTH_SHORT).show();
//                        loading(false);
//                    }
//                    else{
//                        nextActivity();
//                    }
//                });
        String username = binding.signUpEdtUsername.getText().toString();
        String email = binding.signUpEdtEmail.getText().toString();

        //tao truy van username trong collection users trong firestore
        Task<QuerySnapshot> queryUsernameTask = firestore.collection(Constants.KEY_COLLECTION_USERS)
                .whereEqualTo(Constants.KEY_NAME, username)
                .get();
        //tao truy van email trong collection users trong firestore
        Task<QuerySnapshot> queryEmailTask = firestore.collection(Constants.KEY_COLLECTION_USERS)
                .whereEqualTo(Constants.KEY_EMAIL, email)
                .get();

        //chay 2 truy van trong 1 lan
        Tasks.whenAllComplete(queryUsernameTask, queryEmailTask).addOnCompleteListener(task->{
            boolean usernameExits = false;
            boolean emailExits = false;
            if (queryUsernameTask.isSuccessful() && queryUsernameTask.getResult() != null) {
                //kiem tra ket qua truy van username neu chua co trong collection tra ve faslse
                usernameExits = !queryUsernameTask.getResult().isEmpty();
            }
            if (queryEmailTask.isSuccessful() && queryEmailTask.getResult() != null) {
                //kiem tra ket qua truy van email neu chua co trong collection tra ve faslse
                emailExits = !queryEmailTask.getResult().isEmpty();
            }

            if(usernameExits){
                Toast.makeText(this, "Tên đăng nhập đã tồn tại", Toast.LENGTH_SHORT).show();
                loading(false);
            }
            else if(emailExits){
                Toast.makeText(this, "Email đã tồn tại", Toast.LENGTH_SHORT).show();
                loading(false);
            }
            else{
                nextActivity();
            }
        });
    }

    private void addDataToFirestore() {
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
                    Intent intent = new Intent(getApplicationContext(), SignInActivity.class);
                    //tao 1 task moi de chuyen den MainActivity va xoa cac activity truoc do
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    Toast.makeText(this, "Đăng ký thành công", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    loading(false);
                    showToast(e.toString());
                });
    }

    private void nextActivity(){
        String username = binding.signUpEdtUsername.getText().toString();
        String password = binding.signUpEdtPassword.getText().toString();
        String email = binding.signUpEdtEmail.getText().toString();
        String phone = binding.signUpEdtPhone.getText().toString();
        Intent setimg = new Intent(getApplicationContext(), SignUpImgActivity.class);
        Bundle user = new Bundle();
        user.putString("username", username);
        user.putString("password", password);
        user.putString("email", email);
        user.putString("phone", phone);
        setimg.putExtra("newUser", user);
        startActivity(setimg);
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
    public boolean isNumeric(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }
        return str.matches("\\d+");
    }
    private Boolean isValidSignupDetail(){
        String pass = binding.signUpEdtPassword.getText().toString();
        String phone = binding.signUpEdtPhone.getText().toString();
        int lengthPass = pass.length();
        if(binding.signUpEdtUsername.getText().toString().isEmpty()){
            showToast("Bạn chưa nhập tên đăng nhập");
            return false;
        }
        else if(binding.signUpEdtPassword.getText().toString().isEmpty()){
            showToast("Bạn chưa nhập mật khẩu");
            return false;
        }
        else if (lengthPass < 8){
            showToast("Mật khẩu tối thiểu 8 kí tự");
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
        else if (phone.length() < 10 || !isNumeric(phone)){
            showToast("Số điện thoại không hợp lệ");
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