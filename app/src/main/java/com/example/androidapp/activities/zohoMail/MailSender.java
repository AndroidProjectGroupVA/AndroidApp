package com.example.androidapp.activities.zohoMail;

import android.util.Log;

import java.util.Properties;

import javax.mail.AuthenticationFailedException;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class MailSender {

    private final String username;
    private final String password;
    private final String smtpHost;
    private final int smtpPort;

    public MailSender(String username, String password, String smtpHost, int smtpPort) {
        this.username = username;
        this.password = password;
        this.smtpHost = smtpHost;
        this.smtpPort = smtpPort;
    }

    public boolean sendEmail(String toEmail, String subject, String body) {
        Properties props = new Properties();
        props.put("mail.smtp.host", smtpHost);
        props.put("mail.smtp.port", smtpPort);
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true"); // for TLS

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject(subject);
            message.setText(body);

            Transport.send(message);
            return true;
        } catch (AuthenticationFailedException e) {
            Log.e("MailSender", "Authentication failed. Make sure your username and password are correct.", e);
            return false;
        } catch (MessagingException e) {
            Log.e("MailSender", "Messaging error. Make sure your SMTP host and port are correct.", e);
            return false;
        } catch (Exception e) {
            Log.e("MailSender", "Unknown error occurred while sending email.", e);
            return false;
        }
    }

}
