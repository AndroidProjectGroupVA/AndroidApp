package com.example.androidapp.activities;

import static java.security.AccessController.getContext;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentTransaction;

import com.example.androidapp.R;
import com.example.androidapp.adapters.UsersAdapter;
import com.example.androidapp.fragments.GiaSuFragment;
import com.example.androidapp.listener.UserListener;
import com.example.androidapp.models.User;
import com.example.androidapp.utilities.Constants;
import com.example.androidapp.utilities.PreferenceManager;
import com.example.androidapp.databinding.ActivityUsersBinding;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class UsersActivity extends BaseActivity implements UserListener {

    private ActivityUsersBinding binding;
    private PreferenceManager preferenceManager;
    Spinner spinner;
//    ArrayList<String> universityCourses = new ArrayList<>(Arrays.asList(
//            "Toàn bộ",
//            "Giải tích",
//            "Đại số tuyến tính",
//            "Cơ sở dữ liệu",
//            "Lập trình hướng đối tượng",
//            "Mạng máy tính",
//            "Kỹ thuật lập trình",
//            "Hệ điều hành",
//            "Công nghệ phần mềm",
//            "Phân tích thiết kế hệ thống",
//            "Trí tuệ nhân tạo",
//            "Machine Learning",
//            "An toàn thông tin",
//            "Thị giác máy tính",
//            "Xử lý ngôn ngữ tự nhiên"
//    ));
    ArrayAdapter adapter;
    String currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //get layout activity_users
        binding = ActivityUsersBinding.inflate(getLayoutInflater());
        EdgeToEdge.enable(this);
        //set layout
        setContentView(binding.getRoot());
        preferenceManager = new PreferenceManager(this);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        spinner = findViewById(R.id.spinner2_sub);
        List<String> universityCourses = getUniqueSubjectNames(); // Gọi hàm để lấy danh sách môn học
        adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, universityCourses);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        getUsers();

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String selectedCourse = spinner.getSelectedItem().toString();
                String searchText = binding.edtSearch.getText().toString(); // Lấy text từ editText
                filterUsers(selectedCourse, searchText);
            }// ...

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        binding.edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String selectedCourse = spinner.getSelectedItem().toString();
                String searchText = charSequence.toString();
                filterUsers(selectedCourse, searchText);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }

            // ...
        });

    }
//    private List<String> getUniqueSubjectIDs() {
//        List<String> uniqueSubjectIDs = new ArrayList<>();
//        uniqueSubjectIDs.add("Toàn bộ"); // Thêm "Toàn bộ" vào đầu danh sách
//
//        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
//        firestore.collection(Constants.KEY_COLLECTION_USERS)
//                .get()
//                .addOnCompleteListener(task -> {
//                    if (task.isSuccessful() && task.getResult() != null) {
//                        for (QueryDocumentSnapshot document : task.getResult()) {
//                            if (document.contains("subjectID")) {
//                                String subjectID = document.getString("subjectID");
//                                if (!uniqueSubjectIDs.contains(subjectID)) { // Tránh trùng lặp
//                                    uniqueSubjectIDs.add(subjectID);
//                                }
//                            }
//                        }
//
//                        // Cập nhật ArrayAdapter sau khi lấy được danh sách môn học
//                        adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, uniqueSubjectIDs);
//                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//                        spinner.setAdapter(adapter);
//                    } else {
//                        showErrorMessage("Lỗi khi lấy danh sách môn học: " + task.getException().getMessage());
//                    }
//                });
//
//        return uniqueSubjectIDs;
//    }
private List<String> getUniqueSubjectNames() {
    List<String> uniqueSubjectNames = new ArrayList<>();
    uniqueSubjectNames.add("Toàn bộ");

    FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    // Lấy tất cả subject từ collection "subjects"
    firestore.collection("subjects")
            .get()
            .addOnCompleteListener(subjectTask -> {
                if (subjectTask.isSuccessful() && subjectTask.getResult() != null) {
                    for (QueryDocumentSnapshot subjectDoc : subjectTask.getResult()) {
                        String subjectName = subjectDoc.getString("fileNameDisplay");
                        uniqueSubjectNames.add(subjectName);
                    }

                    adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, uniqueSubjectNames);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner.setAdapter(adapter);
                } else {
                    showErrorMessage("Lỗi khi lấy danh sách môn học: " + subjectTask.getException().getMessage());
                }
            });

    return uniqueSubjectNames;
}


    private void filterUsers(String selectedSubjectName, String searchText) {
        loading(true);

        List<User> filteredUsers = new ArrayList<>();
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection(Constants.KEY_COLLECTION_USERS)
                .get()
                .addOnCompleteListener(userTask -> {
                    if (userTask.isSuccessful() && userTask.getResult() != null) {
                        for (QueryDocumentSnapshot userDoc : userTask.getResult()) {
                            if (userDoc.contains("subjectID") && !currentUserId.equals(userDoc.getString("name"))) {
                                String subjectID = userDoc.getString("subjectID");

                                firestore.collection("subjects").document(subjectID)
                                        .get()
                                        .addOnSuccessListener(subjectDoc -> {
                                            if (subjectDoc.exists()) { // Kiểm tra xem môn học có tồn tại không
                                                String subjectName = subjectDoc.getString("fileNameDisplay");
                                                String nameDisplay = userDoc.getString(Constants.KEY_NAME_DISPLAY);

                                                if (subjectName != null && nameDisplay != null) {

                                                    boolean courseMatch = selectedSubjectName.equals("Toàn bộ") ||
                                                            subjectName.toLowerCase().contains(selectedSubjectName.toLowerCase());
                                                    boolean textMatch = searchText.isEmpty() ||
                                                            nameDisplay.toLowerCase().contains(searchText.toLowerCase());

                                                    if (courseMatch && textMatch) {
                                                        User user = new User();
                                                        user.name = userDoc.getString(Constants.KEY_NAME);
                                                        user.email = userDoc.getString(Constants.KEY_EMAIL);
                                                        user.image = userDoc.getString(Constants.KEY_IMAGE);
                                                        user.token = userDoc.getString(Constants.KEY_FCM_TOKEN);
                                                        user.nameDisplay = nameDisplay;
                                                        user.id = userDoc.getId();
                                                        user.subjectID = subjectID;
                                                        user.subjectName = subjectName; // Lưu tên môn học

                                                        filteredUsers.add(user);
                                                    }
                                                }
                                            }

                                            // Cập nhật UsersAdapter sau khi xử lý xong tất cả user
                                            UsersAdapter userAdapter = new UsersAdapter(filteredUsers, this);
                                            binding.usersRecycleView.setAdapter(userAdapter);
                                            userAdapter.notifyDataSetChanged();

                                            loading(false);

                                            // Xử lý khi không có kết quả phù hợp
                                            if (filteredUsers.isEmpty()) {
                                                showErrorMessage("Không có người dùng nào phù hợp");
                                            } else {
                                                binding.textErrorMessage.setVisibility(View.GONE);
                                            }
                                        })
                                        .addOnFailureListener(e -> {
                                            loading(false);
                                            showErrorMessage("Lỗi khi lấy tên môn học: " + e.getMessage());
                                        });
                            }
                        }
                    } else {
                        loading(false);
                        showErrorMessage("Lỗi khi lấy danh sách người dùng: " + userTask.getException().getMessage());
                    }
                });
    }





    private void getUsers(){
        loading(true);
        //call api
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection(Constants.KEY_COLLECTION_USERS)
                .get()
                .addOnCompleteListener(task -> {
                    loading(false);
                    currentUserId = preferenceManager.getString(Constants.KEY_NAME);
                    if(task.isSuccessful() && task.getResult() != null){
                        List<User> users = new ArrayList<>();
                        for(QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()){
                            Map<String, Object> userMap = queryDocumentSnapshot.getData();
                            //gia sư là đối tương có thuộc tính subjectID còn người dùng thì không
                            if (currentUserId.equals(queryDocumentSnapshot.getString("name")) || queryDocumentSnapshot.get("subjectID") == null){
                                continue;
                            }
                            //create user object
                            User user = new User();
                            //get data
                            user.name  = queryDocumentSnapshot.getString(Constants.KEY_NAME);
                            user.email = queryDocumentSnapshot.getString(Constants.KEY_EMAIL);
                            user.image = queryDocumentSnapshot.getString(Constants.KEY_IMAGE);
                            user.token = queryDocumentSnapshot.getString(Constants.KEY_FCM_TOKEN);
                            user.nameDisplay = queryDocumentSnapshot.getString(Constants.KEY_NAME_DISPLAY);
                            user.subjectID = queryDocumentSnapshot.getString(Constants.KEY_SUBJECT_ID);
                            user.id = queryDocumentSnapshot.getId();
                            //add to list
                            users.add(user);
                        }
                        if (users.size() > 0){
                            UsersAdapter userAdapter = new UsersAdapter(users, this);
                            binding.usersRecycleView.setAdapter(userAdapter);
                            binding.usersRecycleView.setVisibility(View.VISIBLE);
                        }else{
                            showErrorMessage("Không có người dùng nào");
                        }
                    }
                    else{
                        showErrorMessage("Error: " + task.getException().getMessage());
                    }
                });
    }
    private List<User> getAllUsersWithSubjectID() {
        final List<User> usersWithSubjectID = new ArrayList<>();
        loading(true); // Hiển thị progressBar
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection(Constants.KEY_COLLECTION_USERS)
                .get()
                .addOnCompleteListener(task -> {
                    loading(false); // Ẩn progressBar
                    if (task.isSuccessful() && task.getResult() != null) {
                        for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                            // Kiểm tra sự tồn tại của trường subjectID
                            if (queryDocumentSnapshot.contains("subjectID")) {
                                // Tạo đối tượng User và lấy dữ liệu
                                User user = new User();
                                user.name = queryDocumentSnapshot.getString(Constants.KEY_NAME);
                                user.email = queryDocumentSnapshot.getString(Constants.KEY_EMAIL);
                                user.image = queryDocumentSnapshot.getString(Constants.KEY_IMAGE);
                                user.token = queryDocumentSnapshot.getString(Constants.KEY_FCM_TOKEN);
                                user.nameDisplay = queryDocumentSnapshot.getString(Constants.KEY_NAME_DISPLAY);
                                user.id = queryDocumentSnapshot.getId();
                                user.subjectID = queryDocumentSnapshot.getString(Constants.KEY_SUBJECT_ID);
                                // Thêm vào danh sách kết quả
                                usersWithSubjectID.add(user);
                            }
                        }
                    } else {
                        // Xử lý lỗi nếu có
                        showErrorMessage("Error: " + task.getException().getMessage());
                    }
                });

        return usersWithSubjectID;
    }
    //show error
    private void showErrorMessage(String message){
        binding.textErrorMessage.setText(String.format("%s", message));
        binding.textErrorMessage.setVisibility(View.VISIBLE);
    }

    public void onUserClicked(User user) {
        Intent intent = new Intent(UsersActivity.this, InfGiasuActivity.class);
        intent.putExtra(Constants.KEY_USER, user);
        startActivity(intent);
    }

    //visible progress bar
    private void loading(Boolean isLoading){
        if(isLoading){
            binding.progressBar.setVisibility(View.VISIBLE);
        }else{
            binding.progressBar.setVisibility(View.GONE);
        }
    }

//    @Override
//    public void onUserClicked(User user) {
//        Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
//        intent.putExtra(Constants.KEY_USER, user);
//        startActivity(intent);
//        finish();
//    }
}