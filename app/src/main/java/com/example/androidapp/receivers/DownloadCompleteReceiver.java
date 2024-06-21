package com.example.androidapp.receivers;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.example.androidapp.activities.InfDocumentActivity;

public class DownloadCompleteReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        long downloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
        // Assume InfDocumentActivity has a static method to get download ID
        if (InfDocumentActivity.getDownloadId() == downloadId) {
            Toast.makeText(context, "Tệp tin đã được tải xuống thành công", Toast.LENGTH_SHORT).show();
        }
    }
}