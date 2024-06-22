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
import com.example.androidapp.fragments.LibraryFragment;
import com.example.androidapp.models.Document;
import com.example.androidapp.utilities.Constants;
import com.example.androidapp.utilities.PreferenceManager;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ManageDocment extends AppCompatActivity {

    Spinner sp_manage;
    EditText edt_manage_search;
    ListView lv_manage_doc;
    ArrayList<Document> list;
    DocumentAdapter adapter;
    ArrayAdapter sp_adapter;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    PreferenceManager pref;
    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_manage_docment);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        pref = new PreferenceManager(getApplicationContext());
        sp_manage = findViewById(R.id.sp_manage);
        String[] items = new String[]{"Theo tên A-Z", "Theo tên Z-A", "Theo thời gian - tăng dần", "Theo thời gian - giảm dần"};
        sp_adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, items);
        sp_manage.setAdapter(sp_adapter);
        edt_manage_search = findViewById(R.id.edt_manage_search);
        lv_manage_doc = findViewById(R.id.lv_manage_doc);
        list = new ArrayList<>();
        adapter = new DocumentAdapter(this, list);
        lv_manage_doc.setAdapter(adapter);
        LoadData();
        //Toast.makeText(this, "Manage Document: " + pref.getString(Constants.KEY_NAME), Toast.LENGTH_SHORT).show();
        sp_manage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0:{
                        Collections.sort(list, (p1, p2) -> p1.getName().compareToIgnoreCase(p2.getName()));
                        break;
                    }
                    case 1:{
                        Collections.sort(list, (p1, p2) -> p2.getName().compareToIgnoreCase(p1.getName()));
                        break;
                    }
                    case 2:{
                        Collections.sort(list, (p1, p2) -> p1.getUpLoadTimeStamp().compareToIgnoreCase(p2.getUpLoadTimeStamp()));
                        break;
                    }
                    case 3:{
                        Collections.sort(list, (p1, p2) -> p2.getUpLoadTimeStamp().compareToIgnoreCase(p1.getUpLoadTimeStamp()));
                        break;
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Collections.sort(list, (p1, p2) -> p1.getName().compareToIgnoreCase(p2.getName()));
                adapter.notifyDataSetChanged();
            }
        });
        lv_manage_doc.setOnItemClickListener((parent, view, position, id) -> {
            Document document = list.get(position);
            Intent intent = new Intent(ManageDocment.this, InfDocumentActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("fileID", document.getId());
            bundle.putString("fileNameDisplay", document.getName());
            bundle.putString("fileSubject", document.getSubject());
            bundle.putString("fileType", document.getType());
            bundle.putString("fileUrl", document.getUrl());
            bundle.putString("fileDate", document.getUpLoadTimeStamp());
            bundle.putString("fileOwner", document.getOwner());
            bundle.putString("fileDescription", document.getDescription());
            bundle.putString("fileOwner", document.getOwner());
            bundle.putString("fileIcon", document.getLogo());
            intent.putExtra("document", bundle);

            startActivity(intent);
        });

        edt_manage_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                search();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }



    private void LoadData() {
        db.collection("documents").get().addOnCompleteListener(task -> {
            if(task.isSuccessful() && task.getResult() != null){
                for (DocumentSnapshot documentSnapshot : task.getResult()) {
                    if(documentSnapshot.getString("fileUsernameOwner") != null && documentSnapshot.getString("fileUsernameOwner").equals(pref.getString(Constants.KEY_NAME))){
                        String fileID = documentSnapshot.getId();
                        String fileNameDisplay = documentSnapshot.getString("fileNameDisplay");
                        String fileSubject = documentSnapshot.getString("fileSubject");
                        String fileType = documentSnapshot.getString("fileType");
                        String fileUrl = documentSnapshot.getString("fileUrl");
                        String fileDate = documentSnapshot.getString("fileDate");
                        String fileOwner = documentSnapshot.getString("fileOwner");
                        String fileDescription = documentSnapshot.getString("fileDescription");
                        String fileIcon = documentSnapshot.getString("fileIcon");
                        Document document = new Document(fileID, fileNameDisplay,fileSubject, fileType, fileUrl, fileDate, fileOwner, fileDescription, fileIcon);
                        list.add(document);
                    }
                }
                Collections.sort(list, (p1, p2) -> p1.getName().compareToIgnoreCase(p2.getName()));
                adapter.notifyDataSetChanged();
            }
        });
    }

    private void search(){
        String key = edt_manage_search.getText().toString();
        ArrayList<Document> search_list = new ArrayList<>();
        if(key.isEmpty()){
            LoadData();
        }
        else{
            for(Document document : list){
                if(document.getName().toLowerCase().contains(key.toLowerCase())){
                    search_list.add(document);
                }
            }
            adapter = new DocumentAdapter(this, search_list);
            lv_manage_doc.setAdapter(adapter);
        }
    }
}