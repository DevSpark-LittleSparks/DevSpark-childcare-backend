package com.devspark.childcare.comms;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.admin.email:admin@devspark.com}")
    private String adminEmail;

    @Value("${spring.mail.username:noreply@devspark.com}")
    private String fromEmail;

    // ─── OTP Approval Email ───────────────────────────────────────────────

    public void sendOtpEmail(String to, String recipientName, String otp) {
        String subject = "✅ Your Account Has Been Approved — Complete Signup";
        String html = """
            <div style="font-family:'Segoe UI',Arial,sans-serif;max-width:600px;margin:0 auto;background:#fff;border-radius:12px;overflow:hidden;box-shadow:0 4px 20px rgba(0,0,0,0.1)">
              <div style="background:linear-gradient(135deg,#0891b2,#1d4ed8);padding:40px 32px;text-align:center">
                <h1 style="color:#fff;margin:0;font-size:26px;font-weight:700">DevSpark ChildCare</h1>
                <p style="color:#bae6fd;margin:8px 0 0;font-size:14px">Your gateway to seamless childcare management</p>
              </div>
              <div style="padding:40px 32px">
                <h2 style="color:#0f172a;font-size:22px;margin:0 0 8px">Account Approved! 🎉</h2>
                <p style="color:#475569;font-size:15px;line-height:1.6">Hi <strong>%s</strong>,</p>
                <p style="color:#475569;font-size:15px;line-height:1.6">
                  Your signup request has been reviewed and <strong style="color:#16a34a">approved</strong> by our admin team.
                  Use the OTP below to complete your registration and activate your account.
                </p>
                <div style="background:#f0f9ff;border:2px solid #0891b2;border-radius:12px;padding:28px;text-align:center;margin:28px 0">
                  <p style="color:#0369a1;font-size:13px;font-weight:600;margin:0 0 12px;text-transform:uppercase;letter-spacing:1px">Your One-Time Password</p>
                  <div style="font-size:40px;font-weight:800;letter-spacing:10px;color:#0891b2;font-family:monospace">%s</div>
                  <p style="color:#94a3b8;font-size:12px;margin:12px 0 0">⏰ Valid for 1 hour only</p>
                </div>
                <p style="color:#64748b;font-size:13px;line-height:1.6">
                  Go to the app, enter this OTP on your first login to activate your account.<br>
                  If you did not request this, please ignore this email.
                </p>
              </div>
              <div style="background:#f8fafc;border-top:1px solid #e2e8f0;padding:20px 32px;text-align:center">
                <p style="color:#94a3b8;font-size:12px;margin:0">© 2025 DevSpark ChildCare. All rights reserved.</p>
              </div>
            </div>
            """.formatted(recipientName, otp, to);

        sendHtml(to, subject, html);
    }

    // Backward-compatible overload (for cases without name)
    public void sendOtpEmail(String to, String otp) {
        sendOtpEmail(to, "User", otp);
    }

    // ─── Rejection Email ──────────────────────────────────────────────────

    public void sendRejectionEmail(String to, String recipientName, String role, String reason) {
        String subject = "❌ Update on Your " + role + " Application — DevSpark ChildCare";
        String html = """
            <div style="font-family:'Segoe UI',Arial,sans-serif;max-width:600px;margin:0 auto;background:#fff;border-radius:12px;overflow:hidden;box-shadow:0 4px 20px rgba(0,0,0,0.1)">
              <div style="background:linear-gradient(135deg,#0891b2,#1d4ed8);padding:40px 32px;text-align:center">
                <h1 style="color:#fff;margin:0;font-size:26px;font-weight:700">DevSpark ChildCare</h1>
                <p style="color:#bae6fd;margin:8px 0 0;font-size:14px">Application Status Update</p>
              </div>
              <div style="padding:40px 32px">
                <h2 style="color:#0f172a;font-size:20px;margin:0 0 8px">Application Not Approved</h2>
                <p style="color:#475569;font-size:15px;line-height:1.6">Hi <strong>%s</strong>,</p>
                <p style="color:#475569;font-size:15px;line-height:1.6">
                  Thank you for your interest in joining DevSpark ChildCare as a <strong>%s</strong>.
                  After reviewing your application, we are unable to approve your request at this time.
                </p>
                <div style="background:#fff5f5;border:1px solid #fecaca;border-radius:10px;padding:20px;margin:24px 0">
                  <p style="color:#b91c1c;font-size:13px;font-weight:600;margin:0 0 6px;text-transform:uppercase;letter-spacing:0.5px">Reason</p>
                  <p style="color:#7f1d1d;font-size:14px;margin:0;line-height:1.6">%s</p>
                </div>
                <p style="color:#64748b;font-size:13px;line-height:1.6">
                  If you believe this is an error or would like to appeal, please contact our admin team directly.<br>
                  You are welcome to reapply once the issue has been resolved.
                </p>
              </div>
              <div style="background:#f8fafc;border-top:1px solid #e2e8f0;padding:20px 32px;text-align:center">
                <p style="color:#94a3b8;font-size:12px;margin:0">© 2025 DevSpark ChildCare. All rights reserved.</p>
              </div>
            </div>
            """.formatted(recipientName, role, reason);

        sendHtml(to, subject, html);
    }

    // ─── Admin Notification — New Pending Request ─────────────────────────

    public void notifyAdminNewRequest(String requesterName, String requesterEmail, String role) {
        String subject = "🔔 New " + capitalize(role) + " Signup Request — Action Required";
        String html = """
            <div style="font-family:'Segoe UI',Arial,sans-serif;max-width:600px;margin:0 auto;background:#fff;border-radius:12px;overflow:hidden;box-shadow:0 4px 20px rgba(0,0,0,0.1)">
              <div style="background:linear-gradient(135deg,#0891b2,#1d4ed8);padding:40px 32px;text-align:center">
                <h1 style="color:#fff;margin:0;font-size:26px;font-weight:700">DevSpark ChildCare</h1>
                <p style="color:#bae6fd;margin:8px 0 0;font-size:14px">Admin Notification</p>
              </div>
              <div style="padding:40px 32px">
                <h2 style="color:#0f172a;font-size:20px;margin:0 0 16px">New %s Signup Request</h2>
                <p style="color:#475569;font-size:15px;line-height:1.6">A new signup request is pending your review:</p>
                <div style="background:#f8fafc;border-left:4px solid #0891b2;border-radius:8px;padding:20px;margin:20px 0">
                  <table style="width:100%%">
                    <tr><td style="color:#94a3b8;font-size:13px;padding:4px 0;width:100px">Name</td><td style="color:#0f172a;font-size:14px;font-weight:600">%s</td></tr>
                    <tr><td style="color:#94a3b8;font-size:13px;padding:4px 0">Email</td><td style="color:#0f172a;font-size:14px">%s</td></tr>
                    <tr><td style="color:#94a3b8;font-size:13px;padding:4px 0">Role</td><td style="color:#0891b2;font-size:14px;font-weight:600;text-transform:capitalize">%s</td></tr>
                  </table>
                </div>
                <p style="color:#475569;font-size:14px">Please log in to the Admin Dashboard to review and approve or reject this request.</p>
              </div>
              <div style="background:#f8fafc;border-top:1px solid #e2e8f0;padding:20px 32px;text-align:center">
                <p style="color:#94a3b8;font-size:12px;margin:0">© 2025 DevSpark ChildCare. All rights reserved.</p>
              </div>
            </div>
            """.formatted(capitalize(role), requesterName, requesterEmail, role);

        sendHtml(adminEmail, subject, html);
    }

    // ─── Internal Helper ──────────────────────────────────────────────────

    private void sendHtml(String to, String subject, String html) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom("DevSpark ChildCare <" + fromEmail + ">");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(html, true);
            mailSender.send(message);
            log.info("Email sent to {}: {}", to, subject);
        } catch (Exception e) {
            log.error("Failed to send email to {}: {}", to, e.getMessage());
        }
    }

    private String capitalize(String s) {
        if (s == null || s.isEmpty()) return s;
        return s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase();
    }
    // ─── Password Reset Email ─────────────────────────────────────────────

    public void sendPasswordResetEmail(String to, String resetLink) {
        String subject = "🔐 Password Reset Request — DevSpark ChildCare";
        String html = """
            <div style="font-family:'Segoe UI',Arial,sans-serif;max-width:600px;margin:0 auto;background:#fff;border-radius:12px;overflow:hidden;box-shadow:0 4px 20px rgba(0,0,0,0.1)">
              <div style="background:linear-gradient(135deg,#6366f1,#4f46e5);padding:40px 32px;text-align:center">
                <h1 style="color:#fff;margin:0;font-size:26px;font-weight:700">DevSpark ChildCare</h1>
                <p style="color:#e0e7ff;margin:8px 0 0;font-size:14px">Secure Password Reset</p>
              </div>
              <div style="padding:40px 32px">
                <h2 style="color:#1e293b;font-size:20px;margin:0 0 16px;font-weight:600">Reset Your Password</h2>
                <p style="color:#475569;font-size:15px;line-height:1.6;margin:0 0 24px">
                  We received a request to reset the password for your DevSpark account. 
                  Click the button below to choose a new password.
                </p>
                <div style="text-align:center;margin:32px 0">
                  <a href="%s" 
                     style="background:#4f46e5;color:#fff;padding:16px 32px;text-decoration:none;border-radius:12px;font-weight:700;font-size:16px;display:inline-block;box-shadow:0 10px 20px rgba(79,70,229,0.2)">
                    Reset Password Now
                  </a>
                </div>
                <p style="color:#475569;font-size:14px;line-height:1.6">
                  If you didn't request this, you can safely ignore this email. Your password will remain unchanged.
                  This link will expire in 1 hour for your security.
                </p>
                <div style="margin-top:32px;padding-top:24px;border-top:1px solid #f1f5f9">
                  <p style="color:#94a3b8;font-size:12px;margin:0">
                    If the button above doesn't work, copy and paste this link into your browser:
                  </p>
                  <p style="color:#6366f1;font-size:12px;word-break:break-all;margin:8px 0 0">%s</p>
                </div>
              </div>
              <div style="background:#f8fafc;border-top:1px solid #e2e8f0;padding:20px 32px;text-align:center">
                <p style="color:#94a3b8;font-size:12px;margin:0">© 2025 DevSpark ChildCare. All rights reserved.</p>
              </div>
            </div>
            """.formatted(resetLink, resetLink);

        sendHtml(to, subject, html);
    }

    // ─── Announcement Email ───────────────────────────────────────────────

    public void sendAnnouncementEmail(String to, String title, String content) {
        String subject = "📢 Announcement: " + title + " — DevSpark ChildCare";
        String html = """
            <div style="font-family:'Segoe UI',Arial,sans-serif;max-width:600px;margin:0 auto;background:#fff;border-radius:12px;overflow:hidden;box-shadow:0 4px 20px rgba(0,0,0,0.1)">
              <div style="background:linear-gradient(135deg,#6366f1,#8b5cf6);padding:40px 32px;text-align:center">
                <h1 style="color:#fff;margin:0;font-size:26px;font-weight:700">LittleSparks News</h1>
                <p style="color:#e0e7ff;margin:8px 0 0;font-size:14px">Important Update for Our Families</p>
              </div>
              <div style="padding:40px 32px">
                <h2 style="color:#1e293b;font-size:22px;margin:0 0 16px;font-weight:700">%s</h2>
                <p style="color:#475569;font-size:15px;line-height:1.7;margin:0 0 24px;white-space:pre-wrap">%s</p>
                <div style="margin-top:32px;padding:20px;background:#f5f3ff;border-radius:12px;text-align:center">
                  <p style="color:#6d28d9;font-size:13px;font-weight:600;margin:0">Thank you for being part of our community!</p>
                </div>
              </div>
              <div style="background:#f8fafc;border-top:1px solid #e2e8f0;padding:20px 32px;text-align:center">
                <p style="color:#94a3b8;font-size:12px;margin:0">© 2025 LittleSparks ChildCare. All rights reserved.</p>
              </div>
            </div>
            """.formatted(title, content);

        sendHtml(to, subject, html);
    }
}



