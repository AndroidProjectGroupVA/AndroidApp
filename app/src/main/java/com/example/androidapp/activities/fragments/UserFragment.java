package com.example.androidapp.activities.fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.androidapp.R;
import com.example.androidapp.activities.FirstMainActivity;
import com.example.androidapp.activities.SignInActivity;
import com.example.androidapp.activities.utilities.Constants;
import com.example.androidapp.activities.utilities.PreferenceManager;
import com.example.androidapp.databinding.FragmentUserBinding;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UserFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private FragmentUserBinding binding;
    private PreferenceManager preferenceManager;

    public UserFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment UserTokenFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static UserFragment newInstance(String param1, String param2) {
        UserFragment fragment = new UserFragment();
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
        // Inflate the layout for this fragment
        binding = FragmentUserBinding.inflate(inflater, container, false);
//        Glide.with(this)
//                .load(R.drawable.user_solid_240) // Đường dẫn đến hình ảnh của bạn
//                .into(new CustomTarget<Drawable>() {
//                    @Override
//                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
//                        binding.imgUserAvatar.setImageDrawable(resource);
//                    }
//
//                    @Override
//                    public void onLoadCleared(@Nullable Drawable placeholder) {
//                        // Xử lý khi hình ảnh bị xóa khỏi view
//                    }
//                });
        Glide.with(this)
                .load(R.drawable.user_solid_240) // Đường dẫn đến hình ảnh của bạn
                .into(new CustomTarget<Drawable>() {
                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                        binding.ibtnUserInfo.setImageDrawable(resource);
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                        // Xử lý khi hình ảnh bị xóa khỏi view
                    }
                });
        Glide.with(this)
                .load(R.drawable.two_people) // Đường dẫn đến hình ảnh của bạn
                .into(new CustomTarget<Drawable>() {
                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                        binding.ibtnFriends.setImageDrawable(resource);
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                        // Xử lý khi hình ảnh bị xóa khỏi view
                    }
                });
        Glide.with(this)
                .load(R.drawable.group_icon) // Đường dẫn đến hình ảnh của bạn
                .into(new CustomTarget<Drawable>() {
                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                        binding.ibtnGroup.setImageDrawable(resource);
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                        // Xử lý khi hình ảnh bị xóa khỏi view
                    }
                });
        Glide.with(this)
                .load(R.drawable.red_warning) // Đường dẫn đến hình ảnh của bạn
                .into(new CustomTarget<Drawable>() {
                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                        binding.ibtnReport.setImageDrawable(resource);
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                        // Xử lý khi hình ảnh bị xóa khỏi view
                    }
                });
        Glide.with(this)

                .load(R.drawable.log_out) // Đường dẫn đến hình ảnh của bạn

                .into(new CustomTarget<Drawable>() {
                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                        binding.ibtnSignOut.setImageDrawable(resource);
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                        // Xử lý khi hình ảnh bị xóa khỏi view
                    }
                });
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        preferenceManager = new PreferenceManager(getContext());
        loadUserDetail();
        setListeners();
    }

    private void setListeners() {

        binding.ibtnSignOut.setOnClickListener(v -> signOut());
        binding.txtSignOut.setOnClickListener(v -> signOut());
    }

    private void loadUserDetail() {
        binding.txtUserName.setText(preferenceManager.getString(Constants.KEY_NAME_DISPLAY));
        String base64Image = preferenceManager.getString(Constants.KEY_IMAGE);
        if (base64Image != null && !base64Image.isEmpty()) {
            // ... (decoding and setting image code)
            byte[] bytes = android.util.Base64.decode(base64Image, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            binding.imgUserAvatar.setImageBitmap(bitmap);
        } else {
            // Handle the case where there's no image data (e.g., show a default image)
            binding.imgUserAvatar.setImageResource(R.drawable.user_solid_240);
        }
    }

    private void showToast(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void signOut() {
        showToast("Signing out...");
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection(Constants.KEY_COLLECTION_USERS).document(
                preferenceManager.getString(Constants.KEY_USER_ID));
        HashMap<String, Object> updates = new HashMap<>();
        updates.put(Constants.KEY_FCM_TOKEN, FieldValue.delete());
        docRef.update(updates)
                .addOnSuccessListener(unused -> {
                    showToast("Signed out successfully");
                    preferenceManager.clear();
                    startActivity(new Intent(requireContext(), FirstMainActivity.class)); // Use requireContext()
                    getActivity().finish();
                })
                .addOnFailureListener(e -> showToast("Failed to sign out"));
    }
}