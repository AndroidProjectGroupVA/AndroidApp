package com.example.androidapp.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;

import android.os.StrictMode;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.androidapp.R;
import com.example.androidapp.fragments.ChatFragment;
import com.example.androidapp.fragments.HomeFragment;
import com.example.androidapp.fragments.LibraryFragment;
import com.example.androidapp.fragments.NotifyFragment;
import com.example.androidapp.fragments.Subject1Fragment;
import com.example.androidapp.fragments.UserFragment;
import com.example.androidapp.utilities.Constants;
import com.example.androidapp.utilities.PreferenceManager;
import com.example.androidapp.databinding.ActivityMainBinding;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener{

    private ActivityMainBinding binding;
    private DrawerLayout drawerLayout;
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManager = new PreferenceManager(getApplicationContext());
        getToken();

        setSupportActionBar(binding.toolbar);
        drawerLayout = binding.drawerLayout;
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, binding.toolbar, R.string.open_nav, R.string.close_nav);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, new HomeFragment()).commit();
            binding.bottomNavigationView.setSelectedItemId(R.id.bottom_menu_home);
        }
        binding.navView.setNavigationItemSelectedListener(this);

        binding.bottomNavigationView.setBackground(null);
        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.bottom_menu_user) {
                replaceFragment(new UserFragment());
            } else if (itemId == R.id.bottom_menu_chat) {
                replaceFragment(new ChatFragment());
            } else if (itemId == R.id.bottom_menu_notify) {
                replaceFragment(new NotifyFragment());
            } else if (itemId == R.id.bottom_menu_library) {
                replaceFragment(new LibraryFragment());
            } else if (itemId == R.id.bottom_menu_home) {
                replaceFragment(new HomeFragment());
            }
            return true;
        });

        if (getIntent().getBooleanExtra("openChatFragment", false)) {
            replaceFragment(new ChatFragment());
        }
    }

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void getToken() {
        FirebaseMessaging.getInstance().getToken()
                .addOnSuccessListener(this::updateToken);
    }

    private void updateToken(String token) {
        preferenceManager.putString(Constants.KEY_FCM_TOKEN, token);
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        DocumentReference docRef = database.collection(Constants.KEY_COLLECTION_USERS).document(
                preferenceManager.getString(Constants.KEY_USER_ID));
        docRef.update(Constants.KEY_FCM_TOKEN, token)
                .addOnFailureListener(e ->
                    showToast("Không thể cập nhật mã token")
                );
    }

    public void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
        drawerLayout.closeDrawer(GravityCompat.START);
    }

    // Tat ban phim ao khi bam ra ngoai
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                    v.clearFocus();
                    hideKeyboard(v);
                }
            }
        }
        return super.dispatchTouchEvent(event);
    }

    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (inputMethodManager != null) {
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_menu_chat) {
            replaceFragment(new ChatFragment());
        } else if (id == R.id.nav_menu_giasu) {
            Intent intent = new Intent(this, UsersActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_menu_tailieu) {
            replaceFragment(new LibraryFragment());
        } else if (id == R.id.nav_menu_hotro) {
            String url = "https://docs.google.com/forms/d/e/1FAIpQLSd_NTTEoxH8v9mzKuMpzwydDOjmBwUg_2st7-waiXW7Ww-rrA/viewform?usp=sf_link";
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            startActivity(intent);
        } else if (id == R.id.nav_menu_subject) {
            replaceFragment(new Subject1Fragment());
        } else {
            return false;
        }

        binding.drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START);

        } else {
            super.onBackPressed();
        }

    }

}


