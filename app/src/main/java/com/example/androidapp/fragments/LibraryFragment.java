package com.example.androidapp.fragments;

import static android.app.Activity.RESULT_OK;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.androidapp.R;
import com.example.androidapp.activities.InfDocumentActivity;
import com.example.androidapp.adapters.DocumentAdapter;
import com.example.androidapp.models.Document;
import com.example.androidapp.utilities.Constants;
import com.example.androidapp.utilities.PreferenceManager;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LibraryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LibraryFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    ImageButton img_btn_viewadd;
    Button add_Document_clear_select;
    TextView tv_Document_name_select, tv_Format_fileformat, tv_Format_date;
    EditText edt_Document_name, edt_Format_description;
    AutoCompleteTextView acv_subject;
    ImageView add_Document_ilogo_file, add_Document_img;
    CardView add_Document_card, add_Document_card_img;
    ListView lv_Document_list;
    DocumentAdapter documentAdapter;
    ArrayAdapter adapterSubject;
    ArrayList<Document> documents = new ArrayList<>();
    ArrayList<String> listSubjects = new ArrayList<>(Arrays.asList(
            "Nhập môn Công nghệ Thông tin",
            "Toán cao cấp",
            "Đại số tuyến tính",
            "Cấu trúc dữ liệu",
            "Thuật toán",
            "Hệ điều hành",
            "Hệ quản trị cơ sở dữ liệu",
            "Công nghệ phần mềm",
            "Trí tuệ nhân tạo",
            "Máy học",
            "Mạng máy tính",
            "Toán rời rạc",
            "Phát triển web",
            "Phát triển ứng dụng di động",
            "Tương tác người-máy",
            "Thiết kế logic số",
            "Thiết kế trình biên dịch",
            "Đồ họa máy tính",
            "An ninh mạng",
            "Điện toán đám mây",
            "Phân tích dữ liệu lớn",
            "Internet vạn vật (IoT)",
            "Phát triển trò chơi",
            "Đạo đức trong công nghệ"
    ));
    FirebaseStorage storage;
    StorageReference storageRef;
    FirebaseFirestore db;
    Uri fileUri;
    Context context;
    PreferenceManager pref;

    private String encodedImage;
    private static final int PICK_FILE_REQUEST = 1;

    public LibraryFragment() {
        // Required empty public constructor
    }



    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LibraryFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LibraryFragment newInstance(String param1, String param2) {
        LibraryFragment fragment = new LibraryFragment();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        context = requireActivity(); // hoặc getActivity()
        View view = inflater.inflate(R.layout.fragment_library, container, false);
//        LinearLayout linearTailieu = view.findViewById(R.id.linearTailieu);
//        linearTailieu.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // Xử lý khi LinearLayout được nhấn vào
//                // Ví dụ: Chuyển hướng sang một trang mới
//                Fragment fragment = new Library2Fragment();
//                FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
//                transaction.replace(R.id.frame_layout, fragment); // R.id.fragment_container is the ID of the container where you want to replace the fragment
//                transaction.addToBackStack(null); // Add transaction to back stack to allow back navigation
//                transaction.commit();
//            }
//        });
        pref = new PreferenceManager(requireContext());
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        db = FirebaseFirestore.getInstance();
        lv_Document_list = view.findViewById(R.id.lv_Document);
        documentAdapter = new DocumentAdapter(context, documents);
        lv_Document_list.setAdapter(documentAdapter);

        img_btn_viewadd = view.findViewById(R.id.imgbtn_viewadd);


        // Set up the spinner
        Spinner dropdown = view.findViewById(R.id.spinner);
        String[] items = new String[]{"Theo tên", "Theo ngày"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_dropdown_item, items);
        dropdown.setAdapter(adapter);

        // Set the title of the action bar if available
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if (activity != null && activity.getSupportActionBar() != null) {
            activity.getSupportActionBar().setTitle("Tài liệu");
        }

//        LayoutInflater inflater = getLayoutInflater();
        View customLayout = inflater.inflate(R.layout.diaglog_add_document, null);
        add_Document_ilogo_file = customLayout.findViewById(R.id.add_Document_ilogo_file);
        add_Document_img = customLayout.findViewById(R.id.add_Document_img);
        tv_Document_name_select = customLayout.findViewById(R.id.tv_Document_name_select);
        tv_Format_fileformat = customLayout.findViewById(R.id.tv_Format_fileformat);
        tv_Format_date = customLayout.findViewById(R.id.tv_Format_date);
        add_Document_card = customLayout.findViewById(R.id.add_Document_card);
        add_Document_card_img = customLayout.findViewById(R.id.add_Document_card_img);
        add_Document_clear_select = customLayout.findViewById(R.id.add_Document_clear_select);
        edt_Document_name = customLayout.findViewById(R.id.edt_Document_name);
        edt_Format_description = customLayout.findViewById(R.id.edt_Format_description);
        acv_subject = customLayout.findViewById(R.id.acv_subject);

        Button btn_select_file = customLayout.findViewById(R.id.add_Document_btn_select_file);
        Button btn_cancel = customLayout.findViewById(R.id.add_Document_btn_cancel);
        Button btn_upload = customLayout.findViewById(R.id.add_Document_btn_add);
        AlertDialog.Builder builder = new AlertDialog.Builder(LibraryFragment.this.getContext());
        builder.setView(customLayout);
        AlertDialog dialog = builder.create();

        //show alertdialog add document
        img_btn_viewadd.setOnClickListener(v -> dialog.show());

        //select img
        add_Document_card_img.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            pickImage.launch(intent);
        });

        edt_Document_name.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus && edt_Document_name.getText().toString().trim().isEmpty()) {
                Toast.makeText(LibraryFragment.this.getContext(), "Vui lòng nhập tên file", Toast.LENGTH_SHORT).show();
            }
        });

        adapterSubject = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, listSubjects);
        acv_subject.setAdapter(adapterSubject);
        acv_subject.setThreshold(1);
        acv_subject.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                acv_subject.showDropDown();
            }
        });

        // chon file tu thiet bi
        btn_select_file.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("*/*");
            startActivityForResult(intent, PICK_FILE_REQUEST);
        });

        //clear file select
        add_Document_clear_select.setOnClickListener(v -> {
            add_Document_card.setVisibility(View.GONE);
            add_Document_ilogo_file.setVisibility(View.GONE);
            tv_Document_name_select.setVisibility(View.GONE);
            tv_Format_fileformat.setText("");
            tv_Format_date.setText("");
        });

        //cancel add document
        btn_cancel.setOnClickListener(v -> dialog.dismiss());

        //upload file
        btn_upload.setOnClickListener(v -> {
            String fileName = getFileName(fileUri);
            String nameFileDisplay = edt_Document_name.getText().toString().trim();
            String subject = acv_subject.getText().toString().trim();
            StorageReference fileRef = storageRef.child("files/" + fileName);
            UploadTask uploadTask = fileRef.putFile(fileUri);

            uploadTask.addOnSuccessListener(taskSnapshot -> fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                String downloadUrl = uri.toString();
                String fileDescription = edt_Format_description.getText().toString().trim();
                String fileType = context.getContentResolver().getType(fileUri);
                String date = tv_Format_date.getText().toString().trim();
                String fileIcon = "";
                if(encodedImage != null){
                    fileIcon = encodedImage;
                }
                else{
                    Bitmap bitmap = getImageBitmapFromPath(context, R.drawable.file_default_ic);
                    fileIcon = encodeImage(bitmap);
                }

                Map<String, Object> contentValues = new HashMap<>();
                contentValues.put("fileNameDisplay", nameFileDisplay);
                contentValues.put("fileSubject", subject);
                contentValues.put("fileType", fileType);
                contentValues.put("fileUrl", downloadUrl);
                contentValues.put("fileDate", date);
                contentValues.put("fileOwner", pref.getString(Constants.KEY_NAME_DISPLAY));
                contentValues.put("fileDescription", fileDescription);
                contentValues.put("fileIcon", fileIcon);

                db.collection("documents").add(contentValues)
                        .addOnSuccessListener(documentReference -> {
                            Toast.makeText(LibraryFragment.this.getContext(), "Upload successful", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                            loadDocuments();
                        })
                        .addOnFailureListener(e -> Toast.makeText(LibraryFragment.this.getContext(), "Upload failed", Toast.LENGTH_SHORT).show());
            })).addOnFailureListener(exception -> Toast.makeText(LibraryFragment.this.getContext(), "Upload failed", Toast.LENGTH_SHORT).show());
        });

        lv_Document_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Document document = documents.get(position);
                Intent intent = new Intent(LibraryFragment.this.getContext(), InfDocumentActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("fileNameDisplay", document.getName());
                bundle.putString("fileSubject", document.getSubject());
                bundle.putString("fileType", document.getType());
                bundle.putString("fileUrl", document.getUrl());
                bundle.putString("fileDate", document.getUpLoadTimeStamp());
                bundle.putString("fileOwner", document.getOwner());
                bundle.putString("fileDescription", document.getDescription());
                bundle.putString("fileOwner", document.getOwner());
                bundle.putString("fileIcon", document.getLogo());
                intent.putExtra("document", bundle);
                startActivity(intent);
                //Toast.makeText(LibraryFragment.this.getContext(), "Click item " + document.getName(), Toast.LENGTH_SHORT).show();
            }
        });

        loadDocuments();

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_FILE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            fileUri = data.getData();
            String fileName = getFileName(fileUri);
            String fileType = context.getContentResolver().getType(fileUri);
            Date now = new Date();
            SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            String formattedDateTime = formatter.format(now);
            tv_Format_date.setText(formattedDateTime);
            add_Document_card.setVisibility(View.VISIBLE);
            add_Document_ilogo_file.setVisibility(View.VISIBLE);
            tv_Document_name_select.setVisibility(View.VISIBLE);
            getFileIconName(fileType);
            tv_Document_name_select.setText(fileName);
            tv_Format_fileformat.setText(fileType);
        }
    }

    private void loadDocuments() {
        db.collection("documents").get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                documents.clear();
                for (DocumentSnapshot documentSnapshot : task.getResult()) {
                    String fileNameDisplay = documentSnapshot.getString("fileNameDisplay");
                    String fileSubject = documentSnapshot.getString("fileSubject");
                    String fileType = documentSnapshot.getString("fileType");
                    String fileUrl = documentSnapshot.getString("fileUrl");
                    String fileDate = documentSnapshot.getString("fileDate");
                    String fileOwner = documentSnapshot.getString("fileOwner");
                    String fileDescription = documentSnapshot.getString("fileDescription");
                    String fileIcon = documentSnapshot.getString("fileIcon");
                    Document document = new Document(fileNameDisplay,fileSubject, fileType, fileUrl, fileDate, fileOwner, fileDescription, fileIcon);
                    documents.add(document);
                }
                documentAdapter.notifyDataSetChanged();
                Toast.makeText(LibraryFragment.this.getContext(), "Đã lấy dữ liệu " + documents.size(), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(LibraryFragment.this.getContext(), "Lỗi truy vấn dữ liệu", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            try (Cursor cursor = context.getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    if (nameIndex >= 0) {
                        result = cursor.getString(nameIndex);
                    }
                }
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    private void getFileIconName(String fileType) {
        if (fileType == null) {
            add_Document_ilogo_file.setImageResource(R.drawable.file_default_ic);
        } else if (fileType.equals("application/pdf")) {
            add_Document_ilogo_file.setImageResource(R.drawable.pdf_ic);
        } else if (fileType.equals("application/msword") || fileType.equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document")) {
            add_Document_ilogo_file.setImageResource(R.drawable.word_ic);
        } else if (fileType.startsWith("image/")) {
            add_Document_ilogo_file.setImageResource(R.drawable.image_ic);
        } else if (fileType.startsWith("audio/")) {
            add_Document_ilogo_file.setImageResource(R.drawable.video_ic);
        } else {
            add_Document_ilogo_file.setImageResource(R.drawable.file_default_ic);
        }
    }

    private String encodeImage(Bitmap bitmap) {
        int previewWidth = 150;
        int previewHeight = bitmap.getHeight() * previewWidth / bitmap.getWidth();
        Bitmap previewBitmap = Bitmap.createScaledBitmap(bitmap, previewWidth, previewHeight, false);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        previewBitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    public static Bitmap getImageBitmapFromPath(Context context, int imageResId) {
        Bitmap bitmap = null;
        try {
            // Đọc ảnh từ resource và chuyển đổi thành Bitmap
            bitmap = BitmapFactory.decodeResource(context.getResources(), imageResId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    private final ActivityResultLauncher<Intent> pickImage = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    if (result.getData() != null) {
                        Uri imageUri = result.getData().getData();
                        try {
                            InputStream inputStream = context.getContentResolver().openInputStream(imageUri);
                            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                            add_Document_img.setImageBitmap(bitmap);
                            encodedImage = encodeImage(bitmap);
                        } catch (FileNotFoundException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }
    );

}

