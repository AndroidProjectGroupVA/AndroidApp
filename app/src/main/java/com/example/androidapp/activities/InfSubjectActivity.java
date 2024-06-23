package com.example.androidapp.activities;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.androidapp.R;

public class InfSubjectActivity extends AppCompatActivity {
    ImageView iv_inf_subject_avt;
    TextView tv_subject_name,  tv_subject_date ;
    BroadcastReceiver onComplete;

    private static final int WRITE_EXTERNAL_STORAGE_PERMISSION_CODE = 1;
    private static final int PERMISSION_REQUEST_CODE = 100;
    private String fileUrl, fileName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_inf_subject);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.inf_subject_layout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        iv_inf_subject_avt = (ImageView) findViewById(R.id.iv_inf_subject_avt);
        tv_subject_name = (TextView) findViewById(R.id.tv_subjectt_name);
        tv_subject_date = (TextView) findViewById(R.id.tv_subject_date);
        Intent getintent = getIntent();
        Bundle bundle = getintent.getBundleExtra("document");
        String nameDisplay = tv_subject_name.getText().toString() + bundle.getString("fileNameDisplay");
        String fileIcon = bundle.getString("fileIcon");
        String fileDate = tv_subject_date.getText().toString() + bundle.getString("fileDate");
        tv_subject_name.setText(nameDisplay);
        tv_subject_date.setText(fileDate);

        Bitmap bitmap = getImageView(fileIcon);
        if (bitmap != null) {
            iv_inf_subject_avt.setImageBitmap(bitmap);
        }
        else{
            iv_inf_subject_avt.setImageResource(R.drawable.file_default_ic);
        }

    }

    private Bitmap getImageView(String encodeImage) {
        if (encodeImage == null || encodeImage.isEmpty()) {
            // Trả về một hình ảnh mặc định hoặc null nếu encodeImage là null hoặc trống
            Log.e("SubjectAdapter", "encodeImage is null or empty");
            return null;
        }
        try {
            Log.d("SubjectAdapter", "Base64 string: " + encodeImage);
            byte[] bytes = Base64.decode(encodeImage, Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        } catch (IllegalArgumentException e) {
            Log.e("SubjectAdapter", "Invalid Base64 string", e);
            return null;
        }
    }
}