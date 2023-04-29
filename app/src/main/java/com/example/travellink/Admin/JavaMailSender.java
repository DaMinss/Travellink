package com.example.travellink.Admin;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class JavaMailSender extends javax.mail.Authenticator {
    private static final String TAG = "JavaMailSender";
    private String email;
    private String password;
    private Session session;

    public JavaMailSender(String email, String password) {
        this.email = email;
        this.password = password;

        Properties props = new Properties();
        props.setProperty("mail.transport.protocol", "smtp");
        props.setProperty("mail.smtp.host", "smtp.gmail.com");
        props.setProperty("mail.smtp.port", "587");
        props.setProperty("mail.smtp.auth", "true");
        props.setProperty("mail.smtp.starttls.enable", "true");

        session = Session.getInstance(props, this);
    }

    public JavaMailSender() {

    }

    @Override
    protected PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(email, password);
    }

    public void sendMail(String subject, String body, String recipient) throws MessagingException {
        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(email));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipient));
        message.setSubject(subject);
        message.setText(body);
        message.setSubject(subject);
        Transport.send(message);
    }
}
