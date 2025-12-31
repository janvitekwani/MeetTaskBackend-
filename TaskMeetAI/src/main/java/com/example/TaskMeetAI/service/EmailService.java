package com.example.TaskMeetAI.service;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class EmailService {

    @Value("${sendgrid.api.key}")
    private String sendGridApiKey;

    @Value("${sendgrid.from.email}")
    private String fromEmail;

    @Value("${sendgrid.from.name}")
    private String fromName;

    @Value("${app.frontend-url:http://localhost:5173}")
    private String frontendUrl;

    private SendGrid sendGrid;

    @PostConstruct
    public void initializeSendGrid() {
        if (sendGridApiKey == null || sendGridApiKey.isBlank()) {
            throw new IllegalStateException("SendGrid API key is missing");
        }
        this.sendGrid = new SendGrid(sendGridApiKey);
    }


    public void sendVerificationEmail(String toEmail, String token) {
        try {
            String verificationUrl = frontendUrl + "/verify-email?token=" + token;

            Email from = new Email(fromEmail, fromName);
            Email to = new Email(toEmail);
            String subject = "Verify your MeetTask account";
            Content content = new Content("text/plain",
                    "Welcome to MeetTask!\n\n" +
                            "Please verify your email address by clicking the link below:\n\n" +
                            verificationUrl + "\n\n" +
                            "This link will expire in 24 hours.\n\n" +
                            "If you didn't create this account, please ignore this email.");

            Mail mail = new Mail(from, subject, to, content);

            Request request = new Request();
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());

            Response response = sendGrid.api(request);

            if (response.getStatusCode() >= 200 && response.getStatusCode() < 300) {
                System.out.println("Email sent successfully to " + toEmail);
            } else {
                System.err.println("Failed to send email. Status: " + response.getStatusCode());
                System.err.println("Response body: " + response.getBody());
                throw new RuntimeException("Failed to send verification email");
            }
        } catch (IOException e) {
            System.err.println("Error sending email: " + e.getMessage());
            throw new RuntimeException("Failed to send verification email", e);
        }
    }

    public void sendPasswordResetEmail(String toEmail, String token) {
        try {
            String resetUrl = frontendUrl + "/reset-password?token=" + token;

            Email from = new Email(fromEmail, fromName);
            Email to = new Email(toEmail);
            String subject = "Reset your MeetTask password";
            Content content = new Content("text/plain",
                    "You requested to reset your password.\n\n" +
                            "Click the link below to set a new password:\n\n" +
                            resetUrl + "\n\n" +
                            "This link will expire in 1 hour.\n\n" +
                            "If you didn't request this, please ignore this email.");

            Mail mail = new Mail(from, subject, to, content);

            Request request = new Request();
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());

            System.out.println(" Sending password reset email to " + toEmail);
            System.out.println(" From: " + fromEmail + " (" + fromName + ")");
            System.out.println("Reset URL: " + resetUrl);

            Response response = sendGrid.api(request);

            System.out.println("SendGrid Response Status: " + response.getStatusCode());
            System.out.println("SendGrid Response Body: " + response.getBody());

            if (response.getStatusCode() >= 200 && response.getStatusCode() < 300) {
                System.out.println("Password reset email sent successfully to " + toEmail);
            } else {
                System.err.println("Failed to send email. Status: " + response.getStatusCode());
                System.err.println("Response body: " + response.getBody());
                throw new RuntimeException("Failed to send password reset email");
            }
        } catch (IOException e) {
            System.err.println("Error sending email: " + e.getMessage());
            throw new RuntimeException("Failed to send password reset email", e);
        }
    }
}