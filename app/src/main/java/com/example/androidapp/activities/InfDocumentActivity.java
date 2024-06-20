package com.example.androidapp.activities;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.androidapp.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

public class InfDocumentActivity extends AppCompatActivity {

    ImageView iv_inf_document_avt;
    TextView tv_documentt_name, tv_document_subject, tv_document_date, tv_document_owner, tv_inf_document_content;
    Button btn_inf_document_dowload, btn_inf_document_view;

    private static final int WRITE_EXTERNAL_STORAGE_PERMISSION_CODE = 1;
    private static final int PERMISSION_REQUEST_CODE = 1;
    private String fileUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_inf_document);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.inf_document_layout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        iv_inf_document_avt = (ImageView) findViewById(R.id.iv_inf_document_avt);
        tv_documentt_name = (TextView) findViewById(R.id.tv_documentt_name);
        tv_document_subject = (TextView) findViewById(R.id.tv_document_subject);
        tv_document_date = (TextView) findViewById(R.id.tv_document_date);
        tv_document_owner = (TextView) findViewById(R.id.tv_document_owner);
        tv_inf_document_content = (TextView) findViewById(R.id.tv_inf_document_content);
        btn_inf_document_dowload = (Button) findViewById(R.id.btn_inf_document_dowload);
        btn_inf_document_view = (Button) findViewById(R.id.btn_inf_document_view);

        Intent getintent = getIntent();
        Bundle bundle = getintent.getBundleExtra("document");
        String nameDisplay = tv_documentt_name.getText().toString() + bundle.getString("fileNameDisplay");
        String subject = tv_document_subject.getText().toString() + bundle.getString("fileSubject");
        String fileType = bundle.getString("fileType");
        String fileUrl = bundle.getString("fileUrl");
        String fileDate = tv_document_date.getText().toString() + bundle.getString("fileDate");
        String fileOwner = tv_document_owner.getText().toString() + bundle.getString("fileOwner");
        String fileDescription = bundle.getString("fileDescription");
        String fileIcon = bundle.getString("fileIcon");
        //Toast.makeText(this, fileDate, Toast.LENGTH_SHORT).show();
        if(fileType != null && !fileType.equals("application/pdf")){
            btn_inf_document_view.setEnabled(false);
        }
        else if(fileType == null){
            btn_inf_document_view.setEnabled(false);
            btn_inf_document_dowload.setEnabled(false);
        }

        tv_documentt_name.setText(nameDisplay);
        tv_document_subject.setText(subject);
        tv_document_date.setText(fileDate);
        tv_document_owner.setText(fileOwner);
        tv_inf_document_content.setText(fileDescription);

        Bitmap bitmap = getImageView(fileIcon);
        if (bitmap != null) {
            iv_inf_document_avt.setImageBitmap(bitmap);
        }
        else{
            iv_inf_document_avt.setImageResource(R.drawable.file_default_ic);
        }

        btn_inf_document_view.setOnClickListener(v -> {
            Intent intent = new Intent(InfDocumentActivity.this, ReadDocumentActivity.class);
            intent.putExtra("fileUrl", fileUrl);
            startActivity(intent);
        });

        btn_inf_document_dowload.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(InfDocumentActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(InfDocumentActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_EXTERNAL_STORAGE_PERMISSION_CODE);
            } else {
                downloadFile(fileUrl);
            }

        });
    }

    private void downloadFile(String fileUrl) {
        new DownloadFileTask().execute(fileUrl);
    }

    private class DownloadFileTask extends AsyncTask<String, Void, String> {

        private String filePath;

        @Override
        protected String doInBackground(String... params) {
            String fileUrl = params[0];
            try {
                URL url = new URL(fileUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    Log.e(InfDocumentActivity.class.getSimpleName(), "Server returned HTTP " + connection.getResponseCode() + " " + connection.getResponseMessage());
                    return "Server returned HTTP " + connection.getResponseCode() + " " + connection.getResponseMessage();
                }

                InputStream inputStream = connection.getInputStream();
                String fileName = getFileNameFromUrl(fileUrl);
                fileName = sanitizeFileName(fileName); // Sanitize the file name to remove special characters
                File directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                if (!directory.exists()) {
                    directory.mkdirs();
                }

                File file = new File(directory, fileName);
                filePath = file.getAbsolutePath(); // Store the file path
                FileOutputStream outputStream = new FileOutputStream(file);

                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }

                outputStream.close();
                inputStream.close();

                return "File downloaded";

            } catch (Exception e) {
                Log.e(InfDocumentActivity.class.getSimpleName(), "Error downloading file", e);
                return "Download failed: " + e.getMessage();
            }
        }


        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(InfDocumentActivity.this, result, Toast.LENGTH_SHORT).show();
            if (result.equals("File downloaded")) {
                Toast.makeText(InfDocumentActivity.this, "File saved at: " + filePath, Toast.LENGTH_LONG).show();
                Log.i(InfDocumentActivity.class.getSimpleName(), "File saved at: " + filePath);
            }
        }
    }

    private String getFileNameFromUrl(String fileUrl) {
        try {
            String decodedUrl = URLDecoder.decode(fileUrl, StandardCharsets.UTF_8.name());
            String fileName = decodedUrl.substring(decodedUrl.lastIndexOf('/') + 1, decodedUrl.indexOf("?"));
            return fileName;
        } catch (Exception e) {
            Log.e(InfDocumentActivity.class.getSimpleName(), "Error decoding URL", e);
            return "default_filename.pdf";
        }
    }

    private String sanitizeFileName(String fileName) {
        return fileName.replaceAll("[^a-zA-Z0-9\\.\\-]", "_");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                downloadFile("your_file_url_here");
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private Bitmap getImageView(String encodeImage) {
        if (encodeImage == null || encodeImage.isEmpty()) {
            // Trả về một hình ảnh mặc định hoặc null nếu encodeImage là null hoặc trống
            Log.e("DocumentAdapter", "encodeImage is null or empty");
            return null;
        }
        try {
            Log.d("DocumentAdapter", "Base64 string: " + encodeImage);
            byte[] bytes = Base64.decode(encodeImage, Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        } catch (IllegalArgumentException e) {
            Log.e("DocumentAdapter", "Invalid Base64 string", e);
            return null;
        }
    }
}