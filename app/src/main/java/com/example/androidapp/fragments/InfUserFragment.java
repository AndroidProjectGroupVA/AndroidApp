package com.example.androidapp.fragments;

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
import com.example.androidapp.databinding.FragmentInfUserBinding;
import com.example.androidapp.utilities.Constants;
import com.example.androidapp.utilities.PreferenceManager;

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
        drawerLayout = binding.fragmentInf;
        binding.btnViewChangePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getActivity() instanceof MainActivity) {
                    ((MainActivity) getActivity()).replaceFragment(new ChangePassFragment());
                }
            }
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