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

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.androidapp.R;

public class InfSubjectActivity extends AppCompatActivity {
    private ImageView ivInfSubjectAvt;
    private TextView tvSubjectName, tvSubjectDescription;
    private BroadcastReceiver onComplete;

    private static final int WRITE_EXTERNAL_STORAGE_PERMISSION_CODE = 1;
    private static final int PERMISSION_REQUEST_CODE = 100;
    private String fileUrl, fileName;

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
}
