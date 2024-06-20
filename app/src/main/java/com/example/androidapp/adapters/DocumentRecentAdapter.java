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
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.example.androidapp.R;
import com.example.androidapp.models.Document;
import com.example.androidapp.models.DocumentRecent;

import java.util.List;

public class DocumentRecentAdapter extends ArrayAdapter<Document> {

    private Context context;
    private List<Document> itemList;
    public DocumentRecentAdapter(@NonNull Context context, List<Document> dOcumentRecentList) {
        super(context, R.layout.activity_view_recent_doc, dOcumentRecentList);
        this.context = context;
        this.itemList = dOcumentRecentList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        DocumentAdapter.ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.activity_document_recent, parent, false);
            viewHolder = new DocumentAdapter.ViewHolder();
            viewHolder.tv_name = convertView.findViewById(R.id.tv_RecentDocument_Name);
            viewHolder.tv_date = convertView.findViewById(R.id.tv_RecentDocument_date);
            viewHolder.tv_subject = convertView.findViewById(R.id.tv_RecentDocument_subject);
            viewHolder.img_logo = convertView.findViewById(R.id.iv_RecentDocument_img);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (DocumentAdapter.ViewHolder) convertView.getTag();
        }

        Document currentItem = itemList.get(position);
        viewHolder.tv_name.setText(currentItem.getName());
        viewHolder.tv_subject.setText(currentItem.getSubject());
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
        TextView tv_subject;
        ImageView img_logo;
    }
}
