package com.example.androidapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.androidapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class SignOutActivity extends AppCompatActivity {
    ImageButton ibtnSignOut;
    FirebaseAuth mAuth;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_out);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        ibtnSignOut = findViewById(R.id.ibtnSignOut);
        ibtnSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(SignOutActivity.this);
                View dialogView = getLayoutInflater().inflate(R.layout.sign_out_dialog, null);
                builder.setView(dialogView);
                AlertDialog dialog = builder.create();
                dialog.show();

                Button abortButton = dialogView.findViewById(R.id.abortButton);
                Button acceptButton = dialogView.findViewById(R.id.acceptButton);

                abortButton.setOnClickListener(vbtn -> dialog.dismiss());
                acceptButton.setOnClickListener(vbtn -> {
                    mAuth.signOut();
                    Intent intent = new Intent(SignOutActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                    dialog.dismiss();
                });


            }
        });
    }

}