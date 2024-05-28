package com.example.androidapp.activities.adapters;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidapp.R;
import com.example.androidapp.activities.models.User;
import com.example.androidapp.databinding.ItemContainerUserBinding;

import java.util.List;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.UserViewHolder>{

    private final List<User> users;

    public UsersAdapter(List<User> users) {
        this.users = users;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemContainerUserBinding itemContainerUserBinding = ItemContainerUserBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false);
        return new UserViewHolder(itemContainerUserBinding);
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
        ItemContainerUserBinding binding;

        //constructor
        UserViewHolder(ItemContainerUserBinding itemContainerUserBinding){
            super(itemContainerUserBinding.getRoot());
            binding = itemContainerUserBinding;
        }

        //set user data
        void setUserData(User user){
            if (user.nameDisplay == null){
                binding.tvUserName.setText("Nguoi dung");
            }
            else{
                binding.tvUserName.setText(user.nameDisplay);
            }
            binding.tvUserEmail.setText(user.email);
            if (getUserImage(user.image) != null){
                binding.imgUserAvatar.setImageBitmap(getUserImage(user.image));
            }
            else{
                binding.imgUserAvatar.setImageResource(R.drawable.null_user_avata);
            }
            //binding.imgVAvata.setImageBitmap(getUserImage(user.image));
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
