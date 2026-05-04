package com.devspark.childcare.comms;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendOtpEmail(String to, String otp) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("DevSpark ChildCare <noreply@devspark.com>");
        message.setTo(to);
        message.setSubject("Your One-Time Password (OTP) for Signup");
        message.setText("Hello,\n\nYour signup request has been approved! Use the following OTP to complete your registration:\n\n" 
                        + otp + "\n\nThis OTP is valid for 1 hour.\n\nThank you,\nDevSpark Team");
        mailSender.send(message);
    }
}
