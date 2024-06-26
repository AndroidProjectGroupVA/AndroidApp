package com.example.androidapp.activities;

import static android.content.ContentValues.TAG;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.androidapp.R;
import com.example.androidapp.databinding.ActivitySignInBinding;
import com.example.androidapp.firebase.SessionManager;
import com.example.androidapp.utilities.Constants;
import com.example.androidapp.utilities.PreferenceManager;
import com.example.androidapp.databinding.ActivitySignInBinding;
//import com.facebook.CallbackManager;
//import com.facebook.FacebookCallback;
//import com.facebook.FacebookException;
//import com.facebook.login.LoginManager;
//import com.facebook.login.LoginResult;
import com.example.androidapp.zohoMail.SendEmailTask;
import com.example.androidapp.zohoMail.getPassword;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.concurrent.atomic.AtomicReference;


public class SignInActivity extends AppCompatActivity {
    private GoogleSignInClient gsc;
    private ActivitySignInBinding binding;
    private PreferenceManager preferenceManager;
    private SessionManager sessionManager;
    boolean passwordVisible = false;
    private FirebaseAuth auth;
    private static final int REQ_ONE_TAP = 2;  // Can be any integer unique to the Activity.
    private boolean showOneTapUI = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferenceManager = new PreferenceManager(getApplicationContext());
//        sessionManager = new SessionManager(getApplicationContext());
//
//        if (sessionManager.isLoggedIn()) {
//            startActivity(new Intent(getApplicationContext(), MainActivity.class));
//            finish(); // Đóng SignInActivity
//        }
        EdgeToEdge.enable(this);
        binding = ActivitySignInBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setListeners();
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.signIn_main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });
        ScrollView scrollView = findViewById(R.id.signIn_main);
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
        binding.signInBtnImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (passwordVisible) {
                    // Ẩn mật khẩu
                    binding.signInEdtPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    binding.signInBtnImg.setImageResource(R.drawable.eyesolid); // Đặt hình ảnh "ẩn mật khẩu"
                } else {
                    // Hiển thị mật khẩu
                    binding.signInEdtPassword.setInputType(InputType.TYPE_CLASS_TEXT);
                    binding.signInBtnImg.setImageResource(R.drawable.eyeslashsolid); // Đặt hình ảnh "hiển thị mật khẩu"
                }
                passwordVisible = !passwordVisible;
            }
        });

        binding.signInTxtForgotPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(SignInActivity.this);
                View dialogView = getLayoutInflater().inflate(R.layout.dialog_forgot, null);
                //EditText nameBox = dialogView.findViewById(R.id.dialogForgot_edtUsername);
                EditText emailBox = dialogView.findViewById(R.id.dialogForgot_edtEmail);

                builder.setView(dialogView);
                AlertDialog dialog = builder.create();

                dialogView.findViewById(R.id.dialogForgot_btnForgot).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AtomicReference<String> name = new AtomicReference<>();
                        String toMail = emailBox.getText().toString().trim();
                        getPassword passwordRetrieval = new getPassword();


                        if (TextUtils.isEmpty(toMail) || !Patterns.EMAIL_ADDRESS.matcher(toMail).matches()) {
                            Toast.makeText(SignInActivity.this, "Thông tin không hợp lệ", Toast.LENGTH_SHORT).show();
                            return;
                        } else {
                            FirebaseFirestore firestore = FirebaseFirestore.getInstance();
                            firestore.collection(Constants.KEY_COLLECTION_USERS)
                                            .whereEqualTo(Constants.KEY_EMAIL, toMail)
                                            .get()
                                            .addOnSuccessListener(task -> {
                                                if (!task.isEmpty()) {
                                                    DocumentSnapshot documentSnapshot = task.getDocuments().get(0);
                                                    name.set(documentSnapshot.getString(Constants.KEY_NAME_DISPLAY));
                                                }
                                            });
                            passwordRetrieval.getPass(toMail, new getPassword.FirestoreCallback() {
                                @Override
                                public void onCallback(String password) {
                                    if (password != null) {
                                        String username = "cloudcomputing@zohomail.com";
                                        String passwordMail = "nhom3cloud@";
                                        String smtpHost = "smtp.zoho.com";
                                        int smtpPort = 587; // Or "587" if using TLS

                                        //String toMail = "hdquy2003@gmail.com";
                                        String subject = "Yêu cầu đặt lại mật khẩu";
                                        String body = "Kính gửi " + name + ",\n\n"
                                                + "Chúng tôi đã nhận được yêu cầu đặt lại mật khẩu cho ứng dụng Gia sư TLU của bạn.\n"
                                                + "Mật khẩu của bạn là: " + password + "\n\n"
                                                + "Nếu bạn không yêu cầu thay đổi này, vui lòng liên hệ với chúng tôi ngay lập tức.\n\n"
                                                + "Trân trọng,\n"
                                                + "Đội ngũ Gia sư TLU";
                                        new SendEmailTask(SignInActivity.this, toMail, subject, body, username, passwordMail, smtpHost, smtpPort).execute();
                                        dialog.dismiss();
                                        Toast.makeText(getApplicationContext(), "Mật khẩu của bạn đã được gửi đến email của bạn", Toast.LENGTH_SHORT).show();
                                    }
                                    else{
                                        Toast.makeText(getApplicationContext(), "Email không tồn tại hoặc có lỗi", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                        }
                    }
                });

                dialogView.findViewById(R.id.dialogForgot_btnCancel).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                if (dialog.getWindow() != null) {
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
                }
                dialog.show();
            }
        });
        setAdjustScreen();

    }
    protected void setAdjustScreen() {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
    }

    private void setListeners() {
        binding.signInBtnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isValidSigInDetails()){
                    signIn();
                }
            }
        });
    }
    private void signIn(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(Constants.KEY_COLLECTION_USERS)
                .whereEqualTo(Constants.KEY_NAME, binding.signInEdtUsername.getText().toString())
                .whereEqualTo(Constants.KEY_PASSWORD, binding.signInEdtPassword.getText().toString())
                .get()
                .addOnCompleteListener(task ->{
                    if (task.isSuccessful() && task.getResult() != null
                            && task.getResult().getDocuments().size() > 0
                    ) {
                        DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
                        preferenceManager.putBoolean(Constants.KEY_IS_SIGNED_IN, true);
                        preferenceManager.putString(Constants.KEY_USER_ID, documentSnapshot.getId());
                        preferenceManager.putString(Constants.KEY_NAME, documentSnapshot.getString(Constants.KEY_NAME));
                        preferenceManager.putString(Constants.KEY_IMAGE, documentSnapshot.getString(Constants.KEY_IMAGE));
                        preferenceManager.putString(Constants.KEY_PASSWORD, documentSnapshot.getString(Constants.KEY_PASSWORD));
                        preferenceManager.putString(Constants.KEY_EMAIL, documentSnapshot.getString(Constants.KEY_EMAIL));
                        preferenceManager.putString(Constants.KEY_PHONE, documentSnapshot.getString(Constants.KEY_PHONE));
                        preferenceManager.putString(Constants.KEY_NAME_DISPLAY, documentSnapshot.getString(Constants.KEY_NAME_DISPLAY));
//                        sessionManager.setLoggedIn(true);
//                        sessionManager.setUserId(documentSnapshot.getId());
                        showToast("Đăng nhập thành công");
                        Intent i1 = new Intent(getApplicationContext(), MainActivity.class);
                        i1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(i1);
                    }else {
                        showToast("Đăng nhập thất bại");
                    }
                });

    }
    public void showToast(String message){
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT ).show();
    }
    private Boolean isValidSigInDetails(){
        if (binding.signInEdtUsername.getText().toString().trim().isEmpty()) {
            showToast("Nhập tên đăng nhập");
            return false;
        } else if (binding.signInEdtPassword.getText().toString().trim().isEmpty()) {
            showToast("Nhập mật khẩu");
            return false;
        } else {
            return true;
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