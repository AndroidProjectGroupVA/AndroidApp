package com.example.androidapp.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.example.androidapp.R;
import com.example.androidapp.models.Document;

import java.util.List;

public class DocumentAdapter extends ArrayAdapter<Document> {
    private Context context;
    private List<Document> itemList;
    public DocumentAdapter(@NonNull Context context, List<Document> listDocument) {
        super(context, R.layout.fragment_library, listDocument);
        this.context = context;
        this.itemList = listDocument;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.fragment_library_item, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.tv_name = convertView.findViewById(R.id.tv_Document_Name);
            viewHolder.tv_date = convertView.findViewById(R.id.tv_Document_date);
            viewHolder.img_logo = convertView.findViewById(R.id.iv_Document_img);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Document currentItem = itemList.get(position);
        viewHolder.tv_name.setText(currentItem.getName());
        viewHolder.tv_date.setText(currentItem.getUpLoadTimeStamp());
        Bitmap imageBitmap = getImageView(currentItem.getLogo());
        if (imageBitmap != null) {
            Glide.with(context)
                    .load(imageBitmap)
                    .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                    .into(viewHolder.img_logo);
        } else {
            // Load a placeholder or default image if imageBitmap is null
            Glide.with(context)
                    .load(R.drawable.file_default_ic) // Your placeholder image resource
                    .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                    .into(viewHolder.img_logo);
        }
        return convertView;
    }

    private Bitmap getImageView(String encodeImage) {
        if (encodeImage == null || encodeImage.isEmpty()) {
            // Trả về một hình ảnh mặc định hoặc null nếu encodeImage là null hoặc trống
            Log.e("DocumentAdapter", "encodeImage is null or empty");
            return null;
        }
        try {
            Log.d("DocumentAdapter", "Base64 string: " + encodeImage);
            byte[] bytes = Base64.decode(encodeImage, Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        } catch (IllegalArgumentException e) {
            Log.e("DocumentAdapter", "Invalid Base64 string", e);
            return null;
        }
    }

    static class ViewHolder{
        TextView tv_name;
        TextView tv_date;
        ImageView img_logo;
    }
}
