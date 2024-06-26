package com.example.androidapp.fragments;

import static android.content.ContentValues.TAG;

import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.androidapp.R;
import com.example.androidapp.activities.MainActivity;
import com.example.androidapp.databinding.FragmentChangePassBinding;
import com.example.androidapp.utilities.Constants;
import com.example.androidapp.utilities.PreferenceManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ChangePassFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChangePassFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ChangePassFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ChangePassFragment.
     */
    // TODO: Rename and change types and number of parameters
    PreferenceManager preferenceManager;
    FragmentChangePassBinding binding;
    FirebaseFirestore firestore;

    public static ChangePassFragment newInstance(String param1, String param2) {
        ChangePassFragment fragment = new ChangePassFragment();
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
        binding = FragmentChangePassBinding.inflate(inflater, container, false);
        preferenceManager = new PreferenceManager(getContext());

        binding.edtChangepassOld.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus && binding.edtChangepassOld.getText().toString().length() < 8){
                    showError("Mật khẩu tối thiểu 8 kí tự");
                }
            }
        });

        binding.edtChangepassNew.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus && binding.edtChangepassNew.getText().toString().isEmpty()){
                    showError("Mật khẩu mới tối thiểu 8 kí tự");
                }
            }
        });

        binding.edtChangepassConfirm.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus && (binding.edtChangepassConfirm.getText().toString().isEmpty() || !binding.edtChangepassConfirm.getText().toString().equals(binding.edtChangepassNew.getText().toString()))){
                    showError("Mật khẩu xác nhận không trùng khớp");
                }
            }
        });

        binding.edtChangepassOld.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                binding.edtChangepassOld.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        binding.edtChangepassNew.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                binding.edtChangepassNew.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        binding.edtChangepassConfirm.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                binding.edtChangepassConfirm.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        binding.btnSaveChangpass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getActivity() instanceof MainActivity) {
                    //get email and password
                    String email = preferenceManager.getString(Constants.KEY_EMAIL);
                    String pass_current = preferenceManager.getString(Constants.KEY_PASSWORD);
                    String pass_old = binding.edtChangepassOld.getText().toString();
                    String pass_new = binding.edtChangepassNew.getText().toString();
                    String pass_confirm = binding.edtChangepassConfirm.getText().toString();
                    //check pass old new
                    if (pass_current.equals(pass_old)) {
                        //check pass new
                        if (pass_new.equals(pass_confirm)) {
                            //save new pass preference
                            preferenceManager.putString(Constants.KEY_PASSWORD, pass_new);
                            //update new pass
                            firestore = FirebaseFirestore.getInstance();
                            HashMap<String, Object> data = new HashMap<>();
                            data.put(Constants.KEY_PASSWORD, pass_new);
                            //update data
                            firestore.collection(Constants.KEY_COLLECTION_USERS)
                                    //select data with email
                                    .whereEqualTo(Constants.KEY_EMAIL, email)
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if (task.isSuccessful()) {
                                                // Lặp qua từng tài khoản tìm thấy
                                                for (QueryDocumentSnapshot document : task.getResult()) {
                                                    // Lấy document ID và cập nhật dữ liệu
                                                    String documentId = document.getId();
                                                    Map<String, Object> newData = new HashMap<>();
                                                    newData.put(Constants.KEY_PASSWORD, pass_new);

                                                    // Thực hiện cập nhật
                                                    firestore.collection(Constants.KEY_COLLECTION_USERS).document(documentId)
                                                            .update(newData)
                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void aVoid) {
                                                                    // Thành công
                                                                    ((MainActivity) getActivity()).replaceFragment(new InfUserFragment());
                                                                    Toast.makeText(getContext(), "Đổi mật khẩu thành công", Toast.LENGTH_SHORT).show();
                                                                }
                                                            })
                                                            .addOnFailureListener(new OnFailureListener() {
                                                                @Override
                                                                public void onFailure(@NonNull Exception e) {
                                                                    // Xảy ra lỗi
                                                                    showError("Có lỗi xảy ra");
                                                                }
                                                            });
                                                }
                                            } else {
                                                // Xảy ra lỗi khi tìm kiếm
                                                Log.d(TAG, "Có lỗi xảy ra: ", task.getException());
                                            }
                                        }
                                    });
                        }
                    }
                }
            }
        });
        loadUserDetail();
        return binding.getRoot();
    }

    public void showError(String error){
        Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
    }

    public void loadUserDetail(){
        binding.tvChangepassTitle.setText(preferenceManager.getString(Constants.KEY_NAME_DISPLAY));
        String base64Image = preferenceManager.getString(Constants.KEY_IMAGE);
        if(base64Image != null){
            Bitmap bitmap = getUserImage(base64Image);
            binding.ivChangpassAvatar.setImageBitmap(bitmap);
        }
        else{
            binding.ivChangpassAvatar.setImageResource(R.drawable.user_solid_240);
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
}