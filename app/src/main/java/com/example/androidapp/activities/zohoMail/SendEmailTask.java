package com.example.androidapp.activities.zohoMail;

import android.util.Log;
import android.os.AsyncTask;


import android.util.Log;
import android.widget.Toast;

import com.example.androidapp.activities.SignInActivity;

import java.lang.ref.WeakReference;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class SendEmailTask {

    private WeakReference<SignInActivity> activityReference;
    private final String toEmail;
    private final String subject;
    private final String body;
    private final String username;
    private final String password;
    private final String smtpHost;
    private final int smtpPort;

    public SendEmailTask(SignInActivity context, String toEmail, String subject, String body, String username, String password, String smtpHost, int smtpPort) {
        this.activityReference = new WeakReference<>(context);
        this.toEmail = toEmail;
        this.subject = subject;
        this.body = body;
        this.username = username;
        this.password = password;
        this.smtpHost = smtpHost;
        this.smtpPort = smtpPort;
    }

    public void execute() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<Boolean> future = executor.submit(new Callable<Boolean>() {
            @Override
            public Boolean call() {
                try {
                    MailSender mailSender = new MailSender(username, password, smtpHost, smtpPort);
                    return mailSender.sendEmail(toEmail, subject, body);
                } catch (Exception e) {
                    Log.e("SendEmailTask", "Error sending email", e);
                    return false;
                }
            }
        });

        executor.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    boolean isEmailSent = future.get();
                    SignInActivity activity = activityReference.get();
                    if (activity == null || activity.isFinishing()) return;

                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (isEmailSent) {
                                Toast.makeText(activity, "Email sent successfully", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(activity, "Failed to send email", Toast.LENGTH_SHORT).show();
                                Log.e("SendEmailTask", "Failed to send email");
                            }
                        }
                    });
                } catch (Exception e) {
                    Log.e("SendEmailTask", "Error executing email send task", e);
                } finally {
                    executor.shutdown();
                }
            }
        });
    }
}


