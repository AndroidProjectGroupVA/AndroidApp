package com.example.androidapp.adapters;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidapp.R;
import com.example.androidapp.databinding.FragmentGiasuItemBinding;
import com.example.androidapp.listener.UserListener;
import com.example.androidapp.models.User;
import com.example.androidapp.databinding.ItemContainerUserBinding;

import java.util.List;

public class GiasuAdapter extends RecyclerView.Adapter<GiasuAdapter.UserViewHolder>{

    private final List<User> users;
    private final UserListener userListener;

    public GiasuAdapter(List<User> users, UserListener userListener) {
        this.users = users;
        this.userListener = userListener;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        FragmentGiasuItemBinding fragmentGiasuItemBinding = FragmentGiasuItemBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false);
        return new UserViewHolder(fragmentGiasuItemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        holder.setUserData(users.get(position));
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    //view holder
    class UserViewHolder extends RecyclerView.ViewHolder {
        FragmentGiasuItemBinding binding;

        //constructor
        UserViewHolder(FragmentGiasuItemBinding fragmentGiasuItemBinding){
            super(fragmentGiasuItemBinding.getRoot());
            binding = fragmentGiasuItemBinding;

            //click listener
            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    userListener.onUserClicked(users.get(position));
                }
            });
        }

        //set user data
        void setUserData(User user){
            if (user.nameDisplay == null){
                binding.txtUserName.setText("Nguoi dung");
            }
            else{
                binding.txtUserName.setText(user.nameDisplay);
            }
            binding.txtUserEmail.setText("Email: "+ user.email);
            if (getUserImage(user.image) != null){
                binding.imgUserAvt.setImageBitmap(getUserImage(user.image));
            }
            else{
                binding.imgUserAvt.setImageResource(R.drawable.null_user_avata);
            }
            //binding.imgVAvata.setImageBitmap(getUserImage(user.image));
            binding.getRoot().setOnClickListener(v -> userListener.onUserClicked(user));
        }
    }

    //get image from string
    private Bitmap getUserImage(String encodeImage){
        if (encodeImage == null || encodeImage.isEmpty()) {
            // Trả về một hình ảnh mặc định hoặc null nếu encodeImage là null hoặc trống
            return null;
        }
        byte[] bytes = Base64.decode(encodeImage, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }
}
