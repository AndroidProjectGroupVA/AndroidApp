package com.example.androidapp.activities;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import com.example.androidapp.utilities.Constants;
import com.example.androidapp.utilities.PreferenceManager;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import javax.annotation.Nullable;

public class BaseActivity extends AppCompatActivity {
    private DocumentReference documentReference;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
        PreferenceManager preferenceManager = new PreferenceManager(getApplicationContext());
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        documentReference = database.collection(Constants.KEY_COLLECTION_USERS)
                .document(preferenceManager.getString(Constants.KEY_USER_ID));
    }
    @Override
    protected void onPause(){
        super.onPause();
        documentReference.update(Constants.KEY_AVAILABILITY,0);
    }
    @Override
    protected  void onResume(){
        super.onResume();
        documentReference.update(Constants.KEY_AVAILABILITY,1);
    }
}