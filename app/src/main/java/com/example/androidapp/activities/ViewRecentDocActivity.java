package com.example.androidapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.androidapp.R;
import com.example.androidapp.adapters.DocumentAdapter;
import com.example.androidapp.adapters.DocumentRecentAdapter;
import com.example.androidapp.fragments.LibraryFragment;
import com.example.androidapp.models.Document;
import com.example.androidapp.models.DocumentRecent;
import com.example.androidapp.utilities.Constants;
import com.example.androidapp.utilities.PreferenceManager;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class ViewRecentDocActivity extends AppCompatActivity {

    FirebaseFirestore db;
    ListView lv_recent_document;
    Spinner sp_sort_recent;
    EditText edt_search_recentDoc;
    ArrayList<Document> documents = new ArrayList<>();
    ArrayAdapter adapter;
    DocumentRecentAdapter recentDocumentAdapter;
    PreferenceManager preferenceManager;
    String username;
    Map<String, Object> recentDocument = new HashMap<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_view_recent_doc);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        db = FirebaseFirestore.getInstance();
        sp_sort_recent = findViewById(R.id.sp_sort_recent);
        String[] sortOptions = new String[]{"Ngày gần nhất", "Ngày cũ nhất", "Tên A-Z", "Tên Z-A"};
        adapter = new ArrayAdapter(ViewRecentDocActivity.this, android.R.layout.simple_spinner_item, sortOptions);
        sp_sort_recent.setAdapter(adapter);

        edt_search_recentDoc = findViewById(R.id.edt_search_recentDoc);
        lv_recent_document = findViewById(R.id.lv_recent_document);

        recentDocumentAdapter = new DocumentRecentAdapter(this, documents);
        lv_recent_document.setAdapter(recentDocumentAdapter);
        preferenceManager = new PreferenceManager(this);
        username = preferenceManager.getString(Constants.KEY_NAME);

        sp_sort_recent.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0:{
                        Collections.sort(documents, (p1, p2) -> p2.getUpLoadTimeStamp().compareToIgnoreCase(p1.getUpLoadTimeStamp()));
                        break;
                    }
                    case 1:{
                        Collections.sort(documents, (p1, p2) -> p1.getUpLoadTimeStamp().compareToIgnoreCase(p2.getUpLoadTimeStamp()));
                        break;
                    }
                    case 2:{
                        Collections.sort(documents, (p1, p2) -> p1.getName().compareToIgnoreCase(p2.getName()));
                        break;
                    }
                    case 3:{
                        Collections.sort(documents, (p1, p2) -> p2.getName().compareToIgnoreCase(p1.getName()));
                        break;
                    }
                }
                recentDocumentAdapter.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Collections.sort(documents, (p1, p2) -> p2.getUpLoadTimeStamp().compareToIgnoreCase(p1.getUpLoadTimeStamp()));
                recentDocumentAdapter.notifyDataSetChanged();
            }
        });

        lv_recent_document.setOnItemClickListener((parent, view, position, id) -> {
            Document document = documents.get(position);
            Intent intent = new Intent(ViewRecentDocActivity.this, InfDocumentActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("fileID", document.getId());
            bundle.putString("fileNameDisplay", document.getName());
            bundle.putString("fileSubject", document.getSubject());
            bundle.putString("fileType", document.getType());
            bundle.putString("fileUrl", document.getUrl());
            bundle.putString("fileOwner", document.getOwner());
            bundle.putString("fileDescription", document.getDescription());
            bundle.putString("fileOwner", document.getOwner());
            bundle.putString("fileIcon", document.getLogo());
            db.collection("documents").document(document.getId()).get().addOnCompleteListener(task -> {
                if (task.isSuccessful() && task.getResult() != null) {
                    String upLoadTimeStamp = task.getResult().getString("fileDate");
                    bundle.putString("fileDate", upLoadTimeStamp);
                    intent.putExtra("document", bundle);
                    startActivity(intent);
                    //Toast.makeText(ViewRecentDocActivity.this, "Đã lấy dữ liệu " + upLoadTimeStamp, Toast.LENGTH_SHORT).show();
                } else {
                    intent.putExtra("document", bundle);
                    startActivity(intent);
                    //Toast.makeText(ViewRecentDocActivity.this, "Lỗi truy vấn dữ liệu", Toast.LENGTH_SHORT).show();
                }
            });

        });

        edt_search_recentDoc.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchRecentDoc();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        selectData();
    }

    private void selectData(){
        db.collection("recentDocument").get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                documents.clear();
                for (DocumentSnapshot documentSnapshot : task.getResult()) {
                    String fileID = documentSnapshot.getId();
                    Map<String, Object> data = documentSnapshot.getData();
                    if(data.containsKey(username)){
                        String recentDate = data.get(username).toString();
                        recentDocument.put(fileID, recentDate);
                    }
                }
                loadData(recentDocument);
                //Toast.makeText(LibraryFragment.this.getContext(), "Đã lấy dữ liệu " + documents.size(), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(ViewRecentDocActivity.this, "Lỗi truy vấn dữ liệu", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadData(Map<String, Object> data) {
        // lấy kich thước data
        int totalDocuments = data.size();
        // tạo biến đếm số lượng documents đã lấy
        AtomicInteger completedDocuments = new AtomicInteger(0);

        // Lấy dữ liệu từ Firebase Firestore
        for (Map.Entry<String, Object> entry : data.entrySet()) {
            String fileID = entry.getKey();
            String recentDate = entry.getValue().toString();
            // lấy doc có uid = fileId từ collection "documents"
            FirebaseFirestore ft = FirebaseFirestore.getInstance();
            ft.collection("documents").document(fileID)
                    .get().addOnCompleteListener(task -> {
                        if (task.isSuccessful() && task.getResult() != null) {
                            String id = task.getResult().getId();
                            String name = task.getResult().getString("fileNameDisplay");
                            String subject = task.getResult().getString("fileSubject");
                            String type = task.getResult().getString("fileType");
                            String url = task.getResult().getString("fileUrl");
                            String owner = task.getResult().getString("fileOwner");
                            String description = task.getResult().getString("fileDescription");
                            String icon = task.getResult().getString("fileIcon");
                            Document document = new Document(id, name, subject, type, url, recentDate, owner, description, icon);
                            documents.add(document);

                        } else {
                            Toast.makeText(ViewRecentDocActivity.this, "Lỗi truy vấn dữ liệu", Toast.LENGTH_SHORT).show();
                        }

                        // Increment the completed documents counter
                        if (completedDocuments.incrementAndGet() == totalDocuments) {
                            // All documents have been processed
                            Collections.sort(documents, (p1, p2) -> p2.getUpLoadTimeStamp().compareToIgnoreCase(p1.getUpLoadTimeStamp()));
                            recentDocumentAdapter.notifyDataSetChanged();
                        }
                    });
        }
    }

    private void searchRecentDoc(){
        String searchText = edt_search_recentDoc.getText().toString().trim();
        ArrayList<Document> searchResults = new ArrayList<>();
        for(Document document : documents){
            if(document.getName().toLowerCase().contains(searchText.toLowerCase())){
                searchResults.add(document);
            }
        }
        lv_recent_document.setAdapter(new DocumentRecentAdapter(this, searchResults));
    }
}