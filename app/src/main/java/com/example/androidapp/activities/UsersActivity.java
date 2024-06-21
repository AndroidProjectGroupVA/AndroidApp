package com.example.androidapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.androidapp.R;
import com.example.androidapp.adapters.UsersAdapter;
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

public class UsersActivity extends AppCompatActivity implements UserListener {

    private ActivityUsersBinding binding;
    private PreferenceManager preferenceManager;
    Spinner spinner;
    ArrayList<String> universityCourses = new ArrayList<>(Arrays.asList(
            "Giải tích",
            "Đại số tuyến tính",
            "Cơ sở dữ liệu",
            "Lập trình hướng đối tượng",
            "Mạng máy tính",
            "Kỹ thuật lập trình",
            "Hệ điều hành",
            "Công nghệ phần mềm",
            "Phân tích thiết kế hệ thống",
            "Trí tuệ nhân tạo",
            "Machine Learning",
            "An toàn thông tin",
            "Thị giác máy tính",
            "Xử lý ngôn ngữ tự nhiên"
    ));
    ArrayAdapter adapter;

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
        adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, universityCourses);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        getUsers();
    }

    private void getUsers(){
        loading(true);
        //call api
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection(Constants.KEY_COLLECTION_USERS)
                .get()
                .addOnCompleteListener(task -> {
                    loading(false);
                    String currentUserId = preferenceManager.getString(Constants.KEY_USER_ID);
                    if(task.isSuccessful() && task.getResult() != null){
                        List<User> users = new ArrayList<>();
                        for(QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()){
                            Map<String, Object> userMap = queryDocumentSnapshot.getData();
                            //gia sư là đối tương có thuộc tính subjectID còn người dùng thì không
                            if (currentUserId.equals(queryDocumentSnapshot.getId()) || queryDocumentSnapshot.get("subjectID") == null){
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

    //show error
    private void showErrorMessage(String message){
        binding.textErrorMessage.setText(String.format("%s", message));
        binding.textErrorMessage.setVisibility(View.VISIBLE);
    }


    //visible progress bar
    private void loading(Boolean isLoading){
        if(isLoading){
            binding.progressBar.setVisibility(View.VISIBLE);
        }else{
            binding.progressBar.setVisibility(View.GONE);
        }
    }

    @Override
    public void onUserClicked(User user) {
        Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
        intent.putExtra(Constants.KEY_USER, user);
        startActivity(intent);
        finish();
    }
}