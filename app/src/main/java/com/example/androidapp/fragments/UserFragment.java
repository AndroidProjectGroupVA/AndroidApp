package com.example.androidapp.fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.androidapp.R;
import com.example.androidapp.activities.FirstMainActivity;

import com.example.androidapp.activities.MainActivity;
import com.example.androidapp.utilities.Constants;
import com.example.androidapp.utilities.PreferenceManager;
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


    TextView txtUserInfo;

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
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if(activity !=null){
            activity.getSupportActionBar().setTitle("Người dùng");
        }

        Glide.with(this)
                .load(R.drawable.user_solid_240) // Đường dẫn đến hình ảnh của bạn
                .into(new CustomTarget<Drawable>() {
                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                        binding.imgUserAvatar.setImageDrawable(resource);
                        loadUserDetail();
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                        // Xử lý khi hình ảnh bị xóa khỏi view
                    }
                });
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
                .load(R.drawable.tutor_icon) // Đường dẫn đến hình ảnh của bạn
                .into(new CustomTarget<Drawable>() {
                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                        binding.ibtnTutorReq.setImageDrawable(resource);
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                        // Xử lý khi hình ảnh bị xóa khỏi view
                    }
                });
        Glide.with(this)
                .load(R.drawable.info_app) // Đường dẫn đến hình ảnh của bạn
                .into(new CustomTarget<Drawable>() {
                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                        binding.ibtnAppInfo.setImageDrawable(resource);
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
        binding.txtUserInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getActivity() instanceof MainActivity) {
                    ((MainActivity) getActivity()).replaceFragment(new InfUserFragment());
                }
            }
        });

        binding.txtTutorReq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "https://docs.google.com/forms/d/e/1FAIpQLSeTpmshQYpXWM8XuSDrOFMqerZi2giqyxeX30fwiXzjnmvxDw/viewform?usp=sf_link";
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                startActivity(intent);
            }
        });

        binding.txtAppInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlertDialog();
            }
        });

        binding.txtReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "https://docs.google.com/forms/d/e/1FAIpQLSeZ8cDoELZ2F8_oZu7VhzGu8zkg4WawYCWAjwDfnhKrgiW8wg/viewform?usp=sf_link";
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                startActivity(intent);
            }
        });
        return binding.getRoot();
    }

    private void showAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Thông tin ứng dụng");
        builder.setMessage("Ứng dụng hỗ trợ học tập - Gia sư TLU là nơi chia sẻ, trao đổi kiến thức giữa các sinh " +
                "viên học tốt và các bạn sinh viên cần hỗ trợ trong học tập. Đây là công việc phi lợi nhuận, hoàn toàn vì mục đích chia " +
                "sẻ kiến thức, hỗ trợ cộng đồng, đây cũng là cơ hội để các bạn trau dồi kiến thức, rèn luyện kỹ năng mềm hay kết được nhiều bạn mới. ");
        builder.setCancelable(true);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Xử lý khi người dùng nhấn nút OK
                dialog.dismiss();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        preferenceManager = new PreferenceManager(requireContext());
        loadUserDetail();
        setListeners();
    }

    private void setListeners() {

        binding.txtSignOut.setOnClickListener(v -> signOutDialog());
        binding.ibtnSignOut.setOnClickListener(v -> signOutDialog());
    }
    private void signOutDialog () {
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.sign_out_dialog, null);

        // Access the buttons from the dialog layout
        Button abortButton = dialogView.findViewById(R.id.abortButton);
        Button acceptButton = dialogView.findViewById(R.id.acceptButton);

        // Create a AlertDialog with the custom layout
        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setView(dialogView)
                .create();

        // Set click listener for Abort button
        abortButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Dismiss the dialog when Abort button is clicked
                dialog.dismiss();
            }
        });

        // Set click listener for Accept button
        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Perform sign out action upon clicking "Accept" button
                signOut();

                // Dismiss the dialog
                dialog.dismiss();
            }
        });

        // Show the dialog
        dialog.show();
    }

    private void loadUserDetail() {

        String base64Image = preferenceManager.getString(Constants.KEY_IMAGE);
        //Toast.makeText(getContext(), base64Image, Toast.LENGTH_SHORT).show();
        if(base64Image != null){
            Bitmap bitmap = getUserImage(base64Image);
            binding.imgUserAvatar.setImageBitmap(bitmap);
        }
        else{
            binding.imgUserAvatar.setImageResource(R.drawable.user_solid_240);
        }
        String userName = preferenceManager.getString(Constants.KEY_NAME_DISPLAY);

        // Hiển thị tên người dùng nếu có
        if (userName != null) {
            binding.txtUserName.setText(userName);
        } else {
            // Xử lý trường hợp tên người dùng chưa được đặt
            binding.txtUserName.setText("Tên người dùng");
        }
    }

    private Bitmap getUserImage(String encodeImage){
        if (encodeImage == null || encodeImage.isEmpty()) {
            // Trả về một hình ảnh mặc định hoặc null nếu encodeImage là null hoặc trống
            return null;
        }
        byte[] bytes = Base64.decode(encodeImage, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    private void showToast(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void signOut() {
        showToast("Đăng xuất...");
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection(Constants.KEY_COLLECTION_USERS).document(
                preferenceManager.getString(Constants.KEY_USER_ID));
        HashMap<String, Object> updates = new HashMap<>();
        updates.put(Constants.KEY_FCM_TOKEN, FieldValue.delete());
        docRef.update(updates)
                .addOnSuccessListener(unused -> {
//                    showToast("Signed out successfully");
                    preferenceManager.clear();
                    startActivity(new Intent(requireContext(), FirstMainActivity.class)); // Use requireContext()
                    getActivity().finish();
                })
                .addOnFailureListener(e -> showToast("Đăng xuất thất bại"));
    }
    @Override
    public void onResume() {
        super.onResume();
        // Lấy tên người dùng từ SharedPreferences
        String userName = preferenceManager.getString(Constants.KEY_NAME_DISPLAY);

        // Hiển thị tên người dùng nếu có
        if (userName != null) {
            binding.txtUserName.setText(userName);
        } else {
            // Xử lý trường hợp tên người dùng chưa được đặt
            binding.txtUserName.setText("Tên người dùng");
        }
    }
}