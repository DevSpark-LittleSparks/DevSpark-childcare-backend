package com.devspark.childcare.comms;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    @org.springframework.beans.factory.annotation.Value("${app.mail.admin:admin@devspark.com}")
    private String adminEmail;

    @org.springframework.beans.factory.annotation.Value("${spring.mail.username}")
    private String senderEmail;

    public void notifyAdminNewRequest(String name, String email, String role) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            String htmlContent = "<!DOCTYPE html><html><body style='font-family: sans-serif;'>" +
                "<h2>🔔 New " + role + " Signup Request</h2>" +
                "<p>A new signup request has been submitted and requires your approval.</p>" +
                "<ul>" +
                "<li><b>Name:</b> " + name + "</li>" +
                "<li><b>Email:</b> " + email + "</li>" +
                "<li><b>Role:</b> " + role + "</li>" +
                "</ul>" +
                "<p>Please log in to the Admin Dashboard to review and approve this request.</p>" +
                "</body></html>";

            helper.setFrom("LittleSparks <" + senderEmail + ">");
            helper.setTo(adminEmail);
            helper.setSubject("🔔 New " + role + " Signup Request — Action Required");
            helper.setText(htmlContent, true);

            mailSender.send(message);
            log.info("Admin notification sent for {} ({})", name, role);
        } catch (MessagingException e) {
            log.error("Failed to send admin notification", e);
        }
    }

    public void sendOtpEmail(String to, String otp) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            String verificationUrl = "http://localhost:5173/verify-otp?email=" + to;

            String htmlContent = "<!DOCTYPE html>" +
                "<html>" +
                "<head>" +
                "    <style>" +
                "        body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; margin: 0; padding: 0; background-color: #f8fafc; }" +
                "        .container { max-width: 600px; margin: 40px auto; background: #ffffff; border-radius: 24px; overflow: hidden; box-shadow: 0 10px 40px rgba(0,0,0,0.05); }" +
                "        .header { background: linear-gradient(135deg, #0891b2 0%, #1d4ed8 100%); padding: 40px 20px; text-align: center; color: white; }" +
                "        .content { padding: 40px; text-align: center; }" +
                "        .otp-box { background: #f1f5f9; padding: 20px; border-radius: 16px; font-size: 32px; font-weight: 900; letter-spacing: 12px; color: #0f172a; margin: 24px 0; display: inline-block; }" +
                "        .button { display: inline-block; padding: 16px 40px; background: #0891b2; color: #ffffff !important; text-decoration: none; border-radius: 16px; font-weight: 800; font-size: 14px; text-transform: uppercase; letter-spacing: 1px; margin-top: 20px; box-shadow: 0 10px 20px rgba(8,145,178,0.2); }" +
                "        .footer { padding: 24px; text-align: center; font-size: 12px; color: #94a3b8; background: #f8fafc; }" +
                "        .brand { font-size: 24px; font-weight: 900; margin-bottom: 8px; }" +
                "    </style>" +
                "</head>" +
                "<body>" +
                "    <div class='container'>" +
                "        <div class='header'>" +
                "            <div class='brand'>LittleSparks</div>" +
                "            <div>ChildCare Management</div>" +
                "        </div>" +
                "        <div class='content'>" +
                "            <h2 style='color: #1e293b; margin-top: 0;'>Your Signup is Approved!</h2>" +
                "            <p style='color: #64748b; line-height: 1.6;'>Hello, we are excited to have you join our community! Use the code below to complete your account activation.</p>" +
                "            <div class='otp-box'>" + otp + "</div>" +
                "            <p style='color: #94a3b8; font-size: 13px;'>This code is valid for 24 hours only.</p>" +
                "            <a href='" + verificationUrl + "' class='button'>Verify Now</a>" +
                "        </div>" +
                "        <div class='footer'>" +
                "            &copy; 2026 LittleSparks ChildCare. All rights reserved.<br>" +
                "            Empowering the next generation with love and care." +
                "        </div>" +
                "    </div>" +
                "</body>" +
                "</html>";

            helper.setFrom("LittleSparks <noreply@littlesparks.com>");
            helper.setTo(to);
            helper.setSubject("Welcome to LittleSparks - Activate Your Account");
            helper.setText("Your OTP is: " + otp + ". Visit " + verificationUrl + " to verify.", true);

            mailSender.send(message);
            log.info("OTP Email sent successfully to {}", to);
        } catch (MessagingException e) {
            log.error("Failed to send HTML OTP email", e);
            throw new RuntimeException("Email sending failed");
        }
    }
}
