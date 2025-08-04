package com.poly.service;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.poly.entity.HoaDonChiTiet;
import com.poly.entity.Users;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

	@Autowired
	private JavaMailSender emailSender;

	public void sendEmailAcctiveAccount(String to, String subject, String body) throws MessagingException, IOException {
		MimeMessage message = emailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

		helper.setTo(to);
		helper.setSubject(subject);
		helper.setText("<h3>Nhấn vào link sau để kích hoạt tài khoản: http://localhost:8080/active-account?token="
				+ body + "</h3>", true);

		// Gửi email
		emailSender.send(message);
	}

	public void sendEmailPassword(String to, String subject, Users user) throws MessagingException, IOException {
		MimeMessage message = emailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

		helper.setTo(to);
		helper.setSubject(subject);
		helper.setText("<h3>Mật khẩu tài khoản cũ của bạn là: " + user.getMatkhau() + "</h3>", true);

		// Gửi email
		emailSender.send(message);
	}
	
	 public void sendOrderConfirmationEmail(String to, String subject, List<HoaDonChiTiet>listHoaDonChiTiets) throws MessagingException {
	        MimeMessage message = emailSender.createMimeMessage();
	        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
	        AtomicReference<Double> tempPrice = new AtomicReference<>(0.0);
	        StringBuilder orderDetails = new StringBuilder();
	        for (HoaDonChiTiet item : listHoaDonChiTiets) {
	        	double price = (item.getGiamgia() == 0) ? item.getGia() * item.getSoluong()
						: (item.getGia() * (100 - item.getGiamgia()) / 100) * item.getSoluong();
	        	tempPrice.updateAndGet(v -> v + price);
	            orderDetails.append("<tr>")
	                        .append("<td>").append(item.getSanPham().getTenSanpham()).append("</td>")
	                        .append("<td>").append(item.getSoluong()).append("</td>")
	                        .append("<td>").append(item.getGia()).append(" VNĐ</td>")
	                        .append("<td>").append(item.getSoluong() * item.getGia()).append(" VNĐ</td>")
	                        .append("</tr>");
	        }

	        String htmlContent = "<!DOCTYPE html>"
	                + "<html><head><meta charset='UTF-8'><title>Xác nhận đơn hàng</title>"
	                + "<style>body { font-family: Arial, sans-serif; } .container { width: 80%; margin: auto; border: 1px solid #ddd; padding: 20px; } "
	                + "h2 { color: #2c3e50; } table { width: 100%; border-collapse: collapse; margin-top: 20px; } "
	                + "th, td { border: 1px solid #ddd; padding: 10px; text-align: center; } th { background-color: #f8f8f8; } .total { font-weight: bold; color: #e74c3c; }"
	                + "</style></head><body><div class='container'>"
	                + "<h2>Đơn hàng của bạn đã được xác nhận</h2>"
	                + "<p>Xin chào, đơn hàng của bạn đã được xác nhận. Dưới đây là chi tiết đơn hàng:</p>"
	                + "<table><thead><tr><th>Tên sản phẩm</th><th>Số lượng</th><th>Đơn giá</th><th>Thành tiền</th></tr></thead>"
	                + "<tbody>" + orderDetails.toString() + "</tbody>"
	                + "<tfoot><tr><td colspan='3' class='total'>Tổng tiền:</td><td class='total'>" +  tempPrice.get() + " VNĐ</td></tr></tfoot>"
	                + "</table><p>Cảm ơn bạn đã mua sắm cùng chúng tôi!</p></div></body></html>";

	        helper.setTo(to);
	        helper.setSubject(subject);
	        helper.setText(htmlContent, true);

	        // Gửi email
	        emailSender.send(message);
	    }
	 
	 public void sendPasswordResetEmail(String toEmail, String subject, String token) {
	        try {
	            MimeMessage mimeMessage = emailSender.createMimeMessage();
	            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
	            helper.setTo(toEmail);
	            helper.setSubject(subject);
	            
	            String htmlContent = """
	                    <div style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #e0e0e0; border-radius: 5px;">
	                        <div style="text-align: center; margin-bottom: 20px;">
	                            <h2 style="color: #4a6ee0;">Đặt Lại Mật Khẩu</h2>
	                        </div>
	                        <div style="margin-bottom: 20px;">
	                            <p>Xin chào,</p>
	                            <p>Chúng tôi đã nhận được yêu cầu đặt lại mật khẩu cho tài khoản của bạn. Nhấn vào nút bên dưới để đặt lại mật khẩu:</p>
	                        </div>
	                        <div style="text-align: center; margin-bottom: 20px;">
	                            <a href="http://localhost:8080/reset-password?token=TOKEN" style="display: inline-block; padding: 12px 24px; background-color: #4a6ee0; color: white; text-decoration: none; border-radius: 5px; font-weight: bold;">Đặt Lại Mật Khẩu</a>
	                        </div>
	                        <div style="margin-bottom: 20px;">
	                            <p>Nếu bạn không yêu cầu đặt lại mật khẩu, vui lòng bỏ qua email này.</p>
	                            <p>Liên kết này sẽ hết hạn sau 30 phút.</p>
	                        </div>
	                    </div>
	                    """.replace("TOKEN", token);
	            
	            helper.setText(htmlContent, true);
	            emailSender.send(mimeMessage);
	            
	        } catch (Exception e) {
	            throw new RuntimeException("Lỗi khi gửi email: " + e.getMessage());
	        }
	    }
}
