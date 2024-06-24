package com.example.androidapp.activities;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidapp.R;
import com.example.androidapp.adapters.GiasuAdapter;
import com.example.androidapp.adapters.UsersAdapter;
import com.example.androidapp.listener.UserListener;
import com.example.androidapp.models.User;
import com.example.androidapp.utilities.Constants;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class InfSubjectActivity extends AppCompatActivity implements UserListener {
    private ImageView ivInfSubjectAvt;
    private TextView tvSubjectName, tvSubjectDescription;
    private BroadcastReceiver onComplete;

    private static final int WRITE_EXTERNAL_STORAGE_PERMISSION_CODE = 1;
    private static final int PERMISSION_REQUEST_CODE = 100;
    private String fileUrl, fileName;
    RecyclerView rcvGiasuDay;
    GiasuAdapter giasuAdapter;
    List<User> users = new ArrayList<>();
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_inf_subject);

        // Set window insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.inf_subject_layout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize views
        ivInfSubjectAvt = findViewById(R.id.iv_inf_subject_avt);
        tvSubjectName = findViewById(R.id.tv_subjectt_name); // Corrected ID
        tvSubjectDescription = findViewById(R.id.txt_descriptionSubject);
        rcvGiasuDay = findViewById(R.id.giasuRecycleView);
        rcvGiasuDay.setLayoutManager(new LinearLayoutManager(this));

        // Get data from intent
        Intent getIntent = getIntent();
        Bundle bundle = getIntent.getBundleExtra("subject");

        if (bundle != null) {
            // Set subject name and date
            String nameDisplay = tvSubjectName.getText().toString() + " " + bundle.getString("fileNameDisplay");
            String fileDescription = tvSubjectDescription.getText().toString() + " " + bundle.getString("fileDescription");
            tvSubjectName.setText(nameDisplay);
            tvSubjectDescription.setText(fileDescription);

            // Decode and set image
            String fileIcon = bundle.getString("fileIcon");
            Bitmap bitmap = getImageView(fileIcon);
            if (bitmap != null) {
                ivInfSubjectAvt.setImageBitmap(bitmap);
            } else {
                ivInfSubjectAvt.setImageResource(R.drawable.file_default_ic);
            }
            String SubjectID = bundle.getString("fileID");
            db.collection("users").get().addOnCompleteListener(task -> {
                if(task.isSuccessful() && task.getResult() != null){
                    for(QueryDocumentSnapshot document : task.getResult()){
                        if(document.getString("subjectID") != null && document.getString("subjectID").equals(SubjectID)){
                            User user = new User();
                            //get data
                            user.name  = document.getString(Constants.KEY_NAME);
                            user.email = document.getString(Constants.KEY_EMAIL);
                            user.image = document.getString(Constants.KEY_IMAGE);
                            user.token = document.getString(Constants.KEY_FCM_TOKEN);
                            user.nameDisplay = document.getString(Constants.KEY_NAME_DISPLAY);
                            user.subjectID = document.getString(Constants.KEY_SUBJECT_ID);
                            user.id = document.getId();
                            //add to list
                            users.add(user);
                        }
                    }
//                    Toast.makeText(InfSubjectActivity.this, "Size: " + users.size(), Toast.LENGTH_SHORT).show();
                    giasuAdapter = new GiasuAdapter(users, this);
                    rcvGiasuDay.setAdapter(giasuAdapter);
                }
            });

        } else {
            Log.e("InfSubjectActivity", "Bundle is null");
        }
    }

    private Bitmap getImageView(String encodeImage) {
        if (encodeImage == null || encodeImage.isEmpty()) {
            Log.e("InfSubjectActivity", "encodeImage is null or empty");
            return null;
        }
        try {
            Log.d("InfSubjectActivity", "Base64 string: " + encodeImage);
            byte[] bytes = Base64.decode(encodeImage, Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        } catch (IllegalArgumentException e) {
            Log.e("InfSubjectActivity", "Invalid Base64 string", e);
            return null;
        }
    }

    @Override
    public void onUserClicked(User user) {

    }
}
