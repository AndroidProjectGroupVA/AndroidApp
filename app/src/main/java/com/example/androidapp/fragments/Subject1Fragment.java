package com.example.androidapp.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.androidapp.R;
import com.example.androidapp.activities.InfSubjectActivity;
import com.example.androidapp.adapters.SubjectAdapter;
import com.example.androidapp.models.Subject;
import com.example.androidapp.utilities.PreferenceManager;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Collections;

public class Subject1Fragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public Subject1Fragment() {
        // Required empty public constructor
    }

    private EditText edt_sub_search;
    private ListView lv_Subject_list;
    private SubjectAdapter subjectAdapter;
    private ArrayList<Subject> subjects = new ArrayList<>();
    private FirebaseStorage storage;
    private StorageReference storageRef;
    private FirebaseFirestore db;
    private Context context;
    private PreferenceManager pref;

    public static Subject1Fragment newInstance(String param1, String param2) {
        Subject1Fragment fragment = new Subject1Fragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_subject1, container, false);
        context = requireContext();

        Spinner dropdown = view.findViewById(R.id.spinnerSubject);
        String[] items = new String[]{"Theo tên A-Z", "Theo tên Z-A"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_dropdown_item, items);
        dropdown.setAdapter(adapter);
        dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                sortSubjects(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if (activity != null && activity.getSupportActionBar() != null) {
            activity.getSupportActionBar().setTitle("Môn học");
        }

        pref = new PreferenceManager(context);
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        db = FirebaseFirestore.getInstance();

        lv_Subject_list = view.findViewById(R.id.lv_Subject);
        subjectAdapter = new SubjectAdapter(context, subjects);
        lv_Subject_list.setAdapter(subjectAdapter);

        edt_sub_search = view.findViewById(R.id.edt_Subject_search);
        edt_sub_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                sub_search_subject();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        lv_Subject_list.setOnItemClickListener((parent, view1, position, id) -> {
            Subject subject = subjects.get(position);
            Intent intent = new Intent(Subject1Fragment.this.getContext(), InfSubjectActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("fileID", subject.getId());
            bundle.putString("fileNameDisplay", subject.getName());
            bundle.putString("fileIcon", subject.getLogo());
            bundle.putString("fileDescription", subject.getDescription());
            intent.putExtra("subject", bundle);
            startActivity(intent);
        });

        loadSubject();
        return view;
    }

    private void loadSubject() {
        db.collection("subjects").get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                subjects.clear();
                for (DocumentSnapshot subjectSnapshot : task.getResult()) {
                    String fileID = subjectSnapshot.getId();
                    String fileNameDisplay = subjectSnapshot.getString("fileNameDisplay");
                    String fileDescription = subjectSnapshot.getString("fileDescription");
                    String fileIcon = subjectSnapshot.getString("fileIcon");
                    Subject subject = new Subject(fileID, fileNameDisplay, fileDescription, fileIcon);
                    subjects.add(subject);
                }
                sortSubjects(0);  // Default sort order
            } else {
                Toast.makeText(Subject1Fragment.this.getContext(), "Lỗi truy vấn dữ liệu", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sub_search_subject() {
        String searchText = edt_sub_search.getText().toString().trim();
        db.collection("subjects").get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                subjects.clear();
                for (DocumentSnapshot subjectSnapshot : task.getResult()) {
                    String fileNameDisplay = subjectSnapshot.getString("fileNameDisplay");
                    if (fileNameDisplay.contains(searchText)) {
                        String fileID = subjectSnapshot.getId();
                        String fileDescription = subjectSnapshot.getString("fileDescription");
                        String fileIcon = subjectSnapshot.getString("fileIcon");
                        Subject subject = new Subject(fileID, fileNameDisplay, fileDescription, fileIcon);
                        subjects.add(subject);
                    }
                }
                sortSubjects(0);  // Default sort order
            } else {
                Toast.makeText(Subject1Fragment.this.getContext(), "Lỗi truy vấn dữ liệu", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sortSubjects(int position) {
        switch (position) {
            case 0:
                Collections.sort(subjects, (p1, p2) -> p1.getName().compareToIgnoreCase(p2.getName()));
                break;
            case 1:
                Collections.sort(subjects, (p1, p2) -> p2.getName().compareToIgnoreCase(p1.getName()));
                break;
//            case 2:
//                Collections.sort(subjects, (p1, p2) -> p1.getUpLoadTimeStamp().compareTo(p2.getUpLoadTimeStamp()));
//                break;
//            case 3:
//                Collections.sort(subjects, (p1, p2) -> p2.getUpLoadTimeStamp().compareTo(p1.getUpLoadTimeStamp()));
//                break;
        }
        subjectAdapter.notifyDataSetChanged();
    }
}
