package com.example.androidapp.fragments;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.example.androidapp.R;
import com.example.androidapp.activities.MainActivity;
import com.example.androidapp.activities.SignInActivity;
import com.example.androidapp.databinding.FragmentInfUserBinding;
import com.example.androidapp.utilities.Constants;
import com.example.androidapp.utilities.PreferenceManager;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import kotlin.jvm.internal.PackageReference;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link InfUserFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class InfUserFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public InfUserFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment InfUserFragment.
     */
    // TODO: Rename and change types and number of parameters
    FragmentInfUserBinding binding;
    PreferenceManager pref;
    FirebaseFirestore db;
    private FrameLayout drawerLayout;
    public static InfUserFragment newInstance(String param1, String param2) {
        InfUserFragment fragment = new InfUserFragment();
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
        binding = FragmentInfUserBinding.inflate(inflater, container, false);
        pref = new PreferenceManager(getContext());
        db = FirebaseFirestore.getInstance();
        drawerLayout = binding.fragmentInf;
        binding.btnViewChangePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getActivity() instanceof MainActivity) {
                    ((MainActivity) getActivity()).replaceFragment(new ChangePassFragment());
                }
            }
        });

        binding.btnInfuserDelete.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
            builder.setTitle("Xác nhận xóa");
            builder.setMessage("Bạn có chắc chắn muốn xóa tài khoản?");
            builder.setPositiveButton("Có", (dialog, which) -> {
                String userName = pref.getString(Constants.KEY_NAME);
                deleteAccount(userName);
            });
            builder.setNegativeButton("Không", (dialog, which) -> {
                // Do nothing
            });
            AlertDialog dialog = builder.create();
            dialog.show();

            // Ensure buttons are displayed correctly
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.colorToolbar));
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.gray));
        });




        loadInf();
        return binding.getRoot(); // Return the root of the binding
    }

//    private void replaceFragment(Fragment fragment) {
//        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
//        transaction.replace(R.id.fragment_inf, fragment);
//        transaction.addToBackStack(null);
//        transaction.commit();
//    }
//private void replaceFragment(Fragment fragment) {
//    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
//    Fragment currentFragment = fragmentManager.findFragmentById(R.id.fragment_inf);
//    if (currentFragment != null) {
//        fragmentManager.beginTransaction().remove(currentFragment).commit();
//    }
//    fragmentManager.beginTransaction()
//            .replace(R.id.fragment_inf, fragment)
//            .addToBackStack(null)
//            .commit();
//}
    private void deleteAccount(String userName) {
        db.collection("users")
                .whereEqualTo("name", userName)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            db.collection("users").document(document.getId()).delete();
                            pref.clear();
                            break;
                        }
                        Intent intent = new Intent(requireContext(), SignInActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        Toast.makeText(requireContext(), "Xóa tài khoản thành công", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(requireContext(), "Xóa tài khoản thất bại", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void loadInf(){
        String name = pref.getString(Constants.KEY_NAME);
        String pass = pref.getString(Constants.KEY_PASSWORD);
        String visibalePass = "";
        for (int i = 0; i < pass.length(); i++) {
            visibalePass += "*";
        }
        //Toast.makeText(getContext(), name, Toast.LENGTH_SHORT).show();
        binding.tvInfNamedisplay.setText(pref.getString(Constants.KEY_NAME_DISPLAY));
        binding.tvInfUsername.setText(pref.getString(Constants.KEY_NAME));
        binding.tvInfEmail.setText(pref.getString(Constants.KEY_EMAIL));
        binding.tvInfPhone.setText(pref.getString(Constants.KEY_PHONE));
        binding.tvInfPass.setText(visibalePass);
        String base64Image = pref.getString(Constants.KEY_IMAGE);
        if(base64Image != null){
            Bitmap bitmap = getUserImage(base64Image);
            binding.ivInfAvatar.setImageBitmap(bitmap);
        }
        else{
            binding.ivInfAvatar.setImageResource(R.drawable.user_solid_240);
        }
        binding.tvInfTitle.setText(pref.getString(Constants.KEY_NAME_DISPLAY));
    }
    private Bitmap getUserImage(String encodeImage){
        if (encodeImage == null || encodeImage.isEmpty()) {
            // Trả về một hình ảnh mặc định hoặc null nếu encodeImage là null hoặc trống
            return null;
        }
        byte[] bytes = Base64.decode(encodeImage, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }
}