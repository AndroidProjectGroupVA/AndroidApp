package com.example.androidapp.activities;

import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.androidapp.R;
import com.example.androidapp.activities.fragments.ChatFragment;
import com.example.androidapp.activities.fragments.ForumFragment;
import com.example.androidapp.activities.fragments.GiaSuFragment;
import com.example.androidapp.activities.fragments.HomeFragment;
import com.example.androidapp.activities.fragments.LibraryFragment;
import com.example.androidapp.activities.fragments.NotifyFragment;
import com.example.androidapp.activities.fragments.SupportFragment;
import com.example.androidapp.activities.fragments.UserFragment;
import com.example.androidapp.activities.utilities.Constants;
import com.example.androidapp.activities.utilities.PreferenceManager;
import com.example.androidapp.databinding.ActivityMainBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

public class MainActivity extends AppCompatActivity {

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

        binding.navView.setNavigationItemSelectedListener(menuItem -> {
            int itemId = menuItem.getItemId();
            if (itemId == R.id.nav_menu_chat) {
                replaceFragment(new HomeFragment());
            } else if (itemId == R.id.nav_menu_diendan) {
                replaceFragment(new ForumFragment());
            } else if (itemId == R.id.nav_menu_giasu) {
                replaceFragment(new GiaSuFragment());
            } else if (itemId == R.id.nav_menu_hotro) {
                replaceFragment(new SupportFragment());
            }
            drawerLayout.closeDrawer(GravityCompat.START); // Đóng DrawerLayout ở đây
            return true;
        });
    }

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void getToken() {
        FirebaseMessaging.getInstance().getToken()
                .addOnSuccessListener(this::updateToken);
    }

    private void updateToken(String token) {
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        DocumentReference docRef = database.collection(Constants.KEY_COLLECTION_USERS).document(
                preferenceManager.getString(Constants.KEY_USER_ID));
        docRef.update(Constants.KEY_FCM_TOKEN, token)
                .addOnFailureListener(e -> {
                    showToast("Unable to update token");
                });
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }
}


