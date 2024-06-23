package com.example.androidapp.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidapp.R;
import com.example.androidapp.adapters.UsersAdapter;
import com.example.androidapp.listener.UserListener;
import com.example.androidapp.models.User;
import com.example.androidapp.utilities.Constants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class InfGiasuActivity extends AppCompatActivity implements UserListener {

    ImageView img_avt_giasu;
    ImageButton btn_viewchat;
    TextView tv_name_giasu, txtGiasuInfo, txtEmailGiasu, txtPhoneGiasu, txtGPAGiasu, tv_giasu_subject;
    RecyclerView lv_giasu_subject;

    List<User> list_giasu_subject = new ArrayList<>();
    User user;

    String subjectID, subjectName = "";

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_inf_giasu);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        img_avt_giasu = findViewById(R.id.img_avt_giasu);
        btn_viewchat = findViewById(R.id.btn_viewchat);
        tv_name_giasu = findViewById(R.id.tv_name_giasu);
        txtGiasuInfo = findViewById(R.id.txtGiasuInfo);
        txtEmailGiasu = findViewById(R.id.txtEmailGiasu);
        txtPhoneGiasu = findViewById(R.id.txtPhoneGiasu);
        txtGPAGiasu = findViewById(R.id.txtGPAGiasu);
        //lv_giasu_subject = findViewById(R.id.lv_giasu_subject);
        tv_giasu_subject = findViewById(R.id.tv_giasu_subject);

        //UsersAdapter userAdapter = new UsersAdapter(list_giasu_subject, InfGiasuActivity.this);
        //lv_giasu_subject.setAdapter(userAdapter);
        Intent intent = getIntent();
        user = (User) intent.getSerializableExtra(Constants.KEY_USER);
        String username = user.name;
        db.collection("users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful() && task.getResult() != null){
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        if(username != null && document.getString("name").equals(username)){
                            tv_name_giasu.setText(document.getString("name"));
                            txtGiasuInfo.setText(document.getString("description"));
                            txtEmailGiasu.setText(document.getString("email"));
                            txtPhoneGiasu.setText(document.getString("phone"));
                            Bitmap userImage = getUserImage(document.getString("image"));
                            if (userImage != null) {
                                img_avt_giasu.setImageBitmap(userImage);
                            }
                            subjectID = document.getString("subjectID");
                            loadDataSubject(subjectID);
                        }
                    }
                }
            }
        });

        btn_viewchat.setOnClickListener(v -> {
            Intent intent1 = new Intent(InfGiasuActivity.this, ChatActivity.class);
            intent1.putExtra(Constants.KEY_USER, user);
            startActivity(intent1);
        });
    }

    private Bitmap getUserImage(String encodeImage){
        if (encodeImage == null || encodeImage.isEmpty()) {
            // Trả về một hình ảnh mặc định hoặc null nếu encodeImage là null hoặc trống
            return null;
        }
        byte[] bytes = Base64.decode(encodeImage, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    private void loadDataSubject(String subject){
        db.collection("subjects").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful() && task.getResult() != null){
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        if(subject != null && document.getId().equals(subject)){
                            subjectName += document.getString("fileNameDisplay") + " ";
                        }
                    }
                    tv_giasu_subject.setText(subjectName);
                }
            }
        });
    }



    @Override
    public void onUserClicked(User user) {

    }
}