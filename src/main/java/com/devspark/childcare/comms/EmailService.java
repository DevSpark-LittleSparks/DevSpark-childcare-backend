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
            <div style="display:flex;align-items:center;justify-content:center;gap:8px;margin-bottom:12px;">
              <div style="background:rgba(255,255,255,0.15);padding:8px;border-radius:12px;display:flex;align-items:center;justify-content:center">
                <svg version="1.0" xmlns="http://www.w3.org/2000/svg" width="28" height="28" viewBox="0 0 260.000000 280.000000" preserveAspectRatio="xMidYMid meet" fill="#ffffff">
                  <g transform="translate(0.000000,280.000000) scale(0.100000,-0.100000)"><path d="M1270 2460 c-62 -18 -141 -70 -295 -194 -126 -101 -160 -131 -318 -286 -171 -166 -267 -283 -267 -325 0 -26 62 -45 144 -45 103 0 106 -4 106 -138 0 -120 21 -291 46 -385 63 -236 230 -415 457 -491 58 -19 67 -20 67 -3 0 7 -26 21 -59 31 -89 27 -178 83 -262 166 -147 147 -200 301 -220 645 -5 99 -12 181 -15 184 -7 7 -103 19 -159 20 -69 2 -83 12 -65 46 27 50 208 242 357 379 180 165 337 288 433 339 69 38 72 39 120 27 141 -34 446 -280 702 -567 55 -62 113 -130 129 -152 35 -47 33 -48 -82 -57 -67 -6 -83 -11 -94 -28 -9 -15 -14 -87 -18 -266 -4 -214 -7 -253 -26 -311 -52 -164 -186 -288 -370 -343 -78 -23 -207 -21 -283 5 -220 74 -378 314 -378 573 0 189 68 331 175 366 80 26 170 -46 210 -169 16 -49 23 -196 13 -271 -6 -47 17 -35 34 17 32 100 108 185 206 230 127 59 229 -10 201 -135 -30 -137 -171 -257 -335 -285 -43 -7 -64 -16 -64 -25 0 -31 169 12 250 63 147 93 224 277 158 375 -66 98 -242 64 -358 -67 -22 -25 -40 -50 -40 -54 0 -5 -4 -9 -9 -9 -4 0 -11 33 -13 73 -11 146 -79 257 -175 282 -132 34 -222 -53 -268 -259 -70 -313 118 -656 404 -740 175 -51 400 13 541 153 76 76 121 159 140 256 7 33 14 165 17 293 4 158 9 235 17 238 6 2 49 8 95 13 96 10 120 25 100 62 -63 120 -431 489 -639 641 -144 105 -246 147 -310 128z"/></g>
                </svg>
              </div>
              <h1 style="color:#000000 ;margin:0;font-size:28px;font-family:'Nunito',sans-serif;font-weight:900;letter-spacing:-0.5px;">Little<span style="color:#CFFAFE;">Sparks</span></h1>
            </div>
            <p style="color:#bae6fd;margin:0;font-size:14px">Your gateway to seamless childcare management</p>
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
            <p style="color:#64748b;font-size:13px;line-height:1.6;text-align:center">
              Click the button below to go directly to the verification page. Your email will be pre-filled.
            </p>
            <div style="text-align:center;margin:24px 0">
              <a href="http://localhost:5173/verify-otp?email=%s"
                 style="background:#0891b2;color:#fff;padding:14px 32px;text-decoration:none;border-radius:8px;font-weight:600;font-size:15px;display:inline-block;box-shadow:0 4px 6px -1px rgba(8, 145, 178, 0.2)">
                Verify Account
              </a>
            </div>
            <p style="color:#64748b;font-size:13px;line-height:1.6">
              If you did not request this, please ignore this email.
            </p>
          </div>
          <div style="background:#f8fafc;border-top:1px solid #e2e8f0;padding:20px 32px;text-align:center">
            <p style="color:#94a3b8;font-size:12px;margin:0">© 2025 LittleSparks ChildCare. All rights reserved.</p>
          </div>
        </div>
        """
        .formatted(recipientName, otp, to);

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
            <div style="display:flex;align-items:center;justify-content:center;gap:8px;margin-bottom:12px;">
              <div style="background:rgba(255,255,255,0.15);padding:8px;border-radius:12px;display:flex;align-items:center;justify-content:center">
                <svg version="1.0" xmlns="http://www.w3.org/2000/svg" width="28" height="28" viewBox="0 0 260.000000 280.000000" preserveAspectRatio="xMidYMid meet" fill="#ffffff">
                  <g transform="translate(0.000000,280.000000) scale(0.100000,-0.100000)"><path d="M1270 2460 c-62 -18 -141 -70 -295 -194 -126 -101 -160 -131 -318 -286 -171 -166 -267 -283 -267 -325 0 -26 62 -45 144 -45 103 0 106 -4 106 -138 0 -120 21 -291 46 -385 63 -236 230 -415 457 -491 58 -19 67 -20 67 -3 0 7 -26 21 -59 31 -89 27 -178 83 -262 166 -147 147 -200 301 -220 645 -5 99 -12 181 -15 184 -7 7 -103 19 -159 20 -69 2 -83 12 -65 46 27 50 208 242 357 379 180 165 337 288 433 339 69 38 72 39 120 27 141 -34 446 -280 702 -567 55 -62 113 -130 129 -152 35 -47 33 -48 -82 -57 -67 -6 -83 -11 -94 -28 -9 -15 -14 -87 -18 -266 -4 -214 -7 -253 -26 -311 -52 -164 -186 -288 -370 -343 -78 -23 -207 -21 -283 5 -220 74 -378 314 -378 573 0 189 68 331 175 366 80 26 170 -46 210 -169 16 -49 23 -196 13 -271 -6 -47 17 -35 34 17 32 100 108 185 206 230 127 59 229 -10 201 -135 -30 -137 -171 -257 -335 -285 -43 -7 -64 -16 -64 -25 0 -31 169 12 250 63 147 93 224 277 158 375 -66 98 -242 64 -358 -67 -22 -25 -40 -50 -40 -54 0 -5 -4 -9 -9 -9 -4 0 -11 33 -13 73 -11 146 -79 257 -175 282 -132 34 -222 -53 -268 -259 -70 -313 118 -656 404 -740 175 -51 400 13 541 153 76 76 121 159 140 256 7 33 14 165 17 293 4 158 9 235 17 238 6 2 49 8 95 13 96 10 120 25 100 62 -63 120 -431 489 -639 641 -144 105 -246 147 -310 128z"/></g>
                </svg>
              </div>
              <h1 style="color:#ffffff;margin:0;font-size:26px;font-family:'Nunito',sans-serif;font-weight:900;letter-spacing:-0.5px;">Little<span style="color:#CFFAFE;">Sparks</span></h1>
            </div>
            <p style="color:#bae6fd;margin:0;font-size:14px">Application Status Update</p>
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
        """
        .formatted(recipientName, role, reason);

    sendHtml(to, subject, html);
  }

  // ─── Admin Notification — New Pending Request ─────────────────────────

  public void notifyAdminNewRequest(String requesterName, String requesterEmail, String role) {
    String subject = "🔔 New " + capitalize(role) + " Signup Request — Action Required";
    String html = """
        <div style="font-family:'Segoe UI',Arial,sans-serif;max-width:600px;margin:0 auto;background:#fff;border-radius:12px;overflow:hidden;box-shadow:0 4px 20px rgba(0,0,0,0.1)">
          <div style="background:linear-gradient(135deg,#0891b2,#1d4ed8);padding:40px 32px;text-align:center">
            <div style="display:flex;align-items:center;justify-content:center;gap:8px;margin-bottom:12px;">
              <div style="background:rgba(255,255,255,0.15);padding:8px;border-radius:12px;display:flex;align-items:center;justify-content:center">
                <svg version="1.0" xmlns="http://www.w3.org/2000/svg" width="28" height="28" viewBox="0 0 260.000000 280.000000" preserveAspectRatio="xMidYMid meet" fill="#ffffff">
                  <g transform="translate(0.000000,280.000000) scale(0.100000,-0.100000)"><path d="M1270 2460 c-62 -18 -141 -70 -295 -194 -126 -101 -160 -131 -318 -286 -171 -166 -267 -283 -267 -325 0 -26 62 -45 144 -45 103 0 106 -4 106 -138 0 -120 21 -291 46 -385 63 -236 230 -415 457 -491 58 -19 67 -20 67 -3 0 7 -26 21 -59 31 -89 27 -178 83 -262 166 -147 147 -200 301 -220 645 -5 99 -12 181 -15 184 -7 7 -103 19 -159 20 -69 2 -83 12 -65 46 27 50 208 242 357 379 180 165 337 288 433 339 69 38 72 39 120 27 141 -34 446 -280 702 -567 55 -62 113 -130 129 -152 35 -47 33 -48 -82 -57 -67 -6 -83 -11 -94 -28 -9 -15 -14 -87 -18 -266 -4 -214 -7 -253 -26 -311 -52 -164 -186 -288 -370 -343 -78 -23 -207 -21 -283 5 -220 74 -378 314 -378 573 0 189 68 331 175 366 80 26 170 -46 210 -169 16 -49 23 -196 13 -271 -6 -47 17 -35 34 17 32 100 108 185 206 230 127 59 229 -10 201 -135 -30 -137 -171 -257 -335 -285 -43 -7 -64 -16 -64 -25 0 -31 169 12 250 63 147 93 224 277 158 375 -66 98 -242 64 -358 -67 -22 -25 -40 -50 -40 -54 0 -5 -4 -9 -9 -9 -4 0 -11 33 -13 73 -11 146 -79 257 -175 282 -132 34 -222 -53 -268 -259 -70 -313 118 -656 404 -740 175 -51 400 13 541 153 76 76 121 159 140 256 7 33 14 165 17 293 4 158 9 235 17 238 6 2 49 8 95 13 96 10 120 25 100 62 -63 120 -431 489 -639 641 -144 105 -246 147 -310 128z"/></g>
                </svg>
              </div>
              <h1 style="color:#ffffff;margin:0;font-size:26px;font-family:'Nunito',sans-serif;font-weight:900;letter-spacing:-0.5px;">Little<span style="color:#CFFAFE;">Sparks</span></h1>
            </div>
            <p style="color:#bae6fd;margin:0;font-size:14px">Admin Notification</p>
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
        """
        .formatted(capitalize(role), requesterName, requesterEmail, role);

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
    if (s == null || s.isEmpty())
      return s;
    return s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase();
  }
  // ─── Password Reset Email ─────────────────────────────────────────────

  public void sendPasswordResetEmail(String to, String resetLink) {
    String subject = "🔐 Password Reset Request — DevSpark ChildCare";
    String html = """
        <div style="font-family:'Segoe UI',Arial,sans-serif;max-width:600px;margin:0 auto;background:#fff;border-radius:12px;overflow:hidden;box-shadow:0 4px 20px rgba(0,0,0,0.1)">
          <div style="background:linear-gradient(135deg,#6366f1,#4f46e5);padding:40px 32px;text-align:center">
            <div style="display:flex;align-items:center;justify-content:center;gap:8px;margin-bottom:12px;">
              <div style="background:rgba(255,255,255,0.15);padding:8px;border-radius:12px;display:flex;align-items:center;justify-content:center">
                <svg version="1.0" xmlns="http://www.w3.org/2000/svg" width="28" height="28" viewBox="0 0 260.000000 280.000000" preserveAspectRatio="xMidYMid meet" fill="#ffffff">
                  <g transform="translate(0.000000,280.000000) scale(0.100000,-0.100000)"><path d="M1270 2460 c-62 -18 -141 -70 -295 -194 -126 -101 -160 -131 -318 -286 -171 -166 -267 -283 -267 -325 0 -26 62 -45 144 -45 103 0 106 -4 106 -138 0 -120 21 -291 46 -385 63 -236 230 -415 457 -491 58 -19 67 -20 67 -3 0 7 -26 21 -59 31 -89 27 -178 83 -262 166 -147 147 -200 301 -220 645 -5 99 -12 181 -15 184 -7 7 -103 19 -159 20 -69 2 -83 12 -65 46 27 50 208 242 357 379 180 165 337 288 433 339 69 38 72 39 120 27 141 -34 446 -280 702 -567 55 -62 113 -130 129 -152 35 -47 33 -48 -82 -57 -67 -6 -83 -11 -94 -28 -9 -15 -14 -87 -18 -266 -4 -214 -7 -253 -26 -311 -52 -164 -186 -288 -370 -343 -78 -23 -207 -21 -283 5 -220 74 -378 314 -378 573 0 189 68 331 175 366 80 26 170 -46 210 -169 16 -49 23 -196 13 -271 -6 -47 17 -35 34 17 32 100 108 185 206 230 127 59 229 -10 201 -135 -30 -137 -171 -257 -335 -285 -43 -7 -64 -16 -64 -25 0 -31 169 12 250 63 147 93 224 277 158 375 -66 98 -242 64 -358 -67 -22 -25 -40 -50 -40 -54 0 -5 -4 -9 -9 -9 -4 0 -11 33 -13 73 -11 146 -79 257 -175 282 -132 34 -222 -53 -268 -259 -70 -313 118 -656 404 -740 175 -51 400 13 541 153 76 76 121 159 140 256 7 33 14 165 17 293 4 158 9 235 17 238 6 2 49 8 95 13 96 10 120 25 100 62 -63 120 -431 489 -639 641 -144 105 -246 147 -310 128z"/></g>
                </svg>
              </div>
              <h1 style="color:#ffffff;margin:0;font-size:26px;font-family:'Nunito',sans-serif;font-weight:900;letter-spacing:-0.5px;">Little<span style="color:#CFFAFE;">Sparks</span></h1>
            </div>
            <p style="color:#e0e7ff;margin:0;font-size:14px">Secure Password Reset</p>
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
        """
        .formatted(resetLink, resetLink);

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
        """
        .formatted(title, content);

    sendHtml(to, subject, html);
  }
}
