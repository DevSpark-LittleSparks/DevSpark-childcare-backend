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
                "    <link href='https://fonts.googleapis.com/css2?family=Inter:wght@400;600;700;800&family=Nunito:wght@800&display=swap' rel='stylesheet'>" +
                "    <style>" +
                "        body { font-family: 'Inter', sans-serif; margin: 0; padding: 0; background-color: #f8fafc; color: #1e293b; }" +
                "        .wrapper { padding: 40px 20px; }" +
                "        .container { max-width: 560px; margin: 0 auto; background: #ffffff; border-radius: 32px; overflow: hidden; box-shadow: 0 20px 50px rgba(0,0,0,0.04); border: 1px solid #f1f5f9; }" +
                "        .header { padding: 40px 40px 0 40px; display: flex; align-items: center; }" +
                "        .logo { font-family: 'Nunito', sans-serif; font-size: 24px; font-weight: 800; letter-spacing: -1px; color: #1F2937; }" +
                "        .logo-sparks { color: #06C5D4; }" +
                "        .content { padding: 40px; }" +
                "        .title { font-size: 28px; font-weight: 800; color: #1F2937; margin-bottom: 24px; display: flex; align-items: center; gap: 8px; }" +
                "        .greeting { font-size: 16px; font-weight: 600; color: #475569; margin-bottom: 12px; }" +
                "        .text { font-size: 15px; color: #64748b; line-height: 1.6; margin-bottom: 32px; }" +
                "        .highlight { color: #0891B2; font-weight: 700; }" +
                "        .otp-container { background: #F0FDFF; border: 2px solid #CFFAFE; border-radius: 24px; padding: 40px 20px; text-align: center; margin-bottom: 32px; position: relative; }" +
                "        .otp-label { font-size: 12px; font-weight: 800; color: #0891B2; text-transform: uppercase; letter-spacing: 2px; margin-bottom: 16px; }" +
                "        .otp-code { font-size: 48px; font-weight: 800; color: #06B6D4; letter-spacing: 12px; margin-left: 12px; font-family: 'Inter', sans-serif; }" +
                "        .validity { font-size: 13px; color: #94a3b8; margin-top: 16px; display: flex; align-items: center; justify-content: center; gap: 4px; }" +
                "        .button-wrap { text-align: center; margin-bottom: 40px; }" +
                "        .button { display: inline-block; padding: 18px 48px; background: #06B6D4; color: #ffffff !important; text-decoration: none; border-radius: 20px; font-weight: 800; font-size: 16px; box-shadow: 0 10px 25px rgba(6,182,212,0.25); transition: all 0.3s ease; }" +
                "        .footer { padding: 40px; text-align: center; background: #f8fafc; border-top: 1px solid #f1f5f9; }" +
                "        .footer-text { font-size: 13px; color: #94a3b8; line-height: 1.5; }" +
                "        .ignore-text { font-size: 13px; color: #cbd5e1; margin-top: 24px; text-align: center; }" +
                "    </style>" +
                "</head>" +
                "<body>" +
                "    <div class='wrapper'>" +
                "        <div class='container'>" +
                "            <div class='header'>" +
                "                <div class='logo'>Little<span class='logo-sparks'>Sparks</span></div>" +
                "            </div>" +
                "            <div class='content'>" +
                "                <h1 class='title'>Account Approved! 🎉</h1>" +
                "                <p class='greeting'>Hello Spark Parent,</p>" +
                "                <p class='text'>Your signup request has been reviewed and <span class='highlight'>approved</span> by our admin team. Use the OTP below to complete your registration and activate your account.</p>" +
                "                " +
                "                <div class='otp-container'>" +
                "                    <div class='otp-label'>Your One-Time Password</div>" +
                "                    <div class='otp-code'>" + otp + "</div>" +
                "                    <div class='validity'>⏰ Valid for 24 hours only</div>" +
                "                </div>" +
                "                " +
                "                <p class='text' style='text-align: center; margin-bottom: 24px;'>Click the button below to go directly to the verification page.</p>" +
                "                " +
                "                <div class='button-wrap'>" +
                "                    <a href='" + verificationUrl + "' class='button'>Verify Account</a>" +
                "                </div>" +
                "            </div>" +
                "            " +
                "            <div class='footer'>" +
                "                <p class='footer-text'>" +
                "                    &copy; 2026 LittleSparks ChildCare. All rights reserved.<br>" +
                "                    Empowering the next generation with love and care." +
                "                </p>" +
                "            </div>" +
                "        </div>" +
                "        <p class='ignore-text'>If you did not request this, please ignore this email.</p>" +
                "    </div>" +
                "</body>" +
                "</html>";

            helper.setFrom("LittleSparks <" + senderEmail + ">");
            helper.setTo(to);
            helper.setSubject("Welcome to LittleSparks - Activate Your Account");
            helper.setText(htmlContent, true);

            mailSender.send(message);
            log.info("OTP Email sent successfully to {}", to);
        } catch (MessagingException e) {
            log.error("Failed to send HTML OTP email", e);
            throw new RuntimeException("Email sending failed");
        }
    }
}
