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

}
