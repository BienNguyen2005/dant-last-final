package com.poly.controller.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.poly.service.JWTService;
import com.poly.service.LoaiService;
import com.poly.service.UserService;

@Controller
public class PasswordResetController {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private JWTService jwtService;
    
    @Autowired
    private LoaiService loaiService;
    
    @GetMapping("/reset-password")
    public String showResetPasswordForm(@RequestParam(required = true, name = "token") String token, Model model) {
        try {
            // Validate token
            if (!jwtService.validateToken(token)) {
                model.addAttribute("errorMessage", "Liên kết đặt lại mật khẩu không hợp lệ hoặc đã hết hạn!");
                model.addAttribute("invalidToken", true);
            } else {
                model.addAttribute("token", token);
            }
            
            model.addAttribute("loais", loaiService.getAllLoai(0, 5));
            return "user/resetPasswordView";
            
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Có lỗi xảy ra: " + e.getMessage());
            model.addAttribute("invalidToken", true);
            model.addAttribute("loais", loaiService.getAllLoai(0, 5));
            return "user/resetPasswordView";
        }
    }
    
    @PostMapping("/reset-password")
    public String processResetPassword(
            @RequestParam("token") String token,
            @RequestParam("newPassword") String newPassword,
            @RequestParam("confirmPassword") String confirmPassword,
            RedirectAttributes redirectAttributes) {
        
        try {
            // Validate token
            if (!jwtService.validateToken(token)) {
                redirectAttributes.addFlashAttribute("errorMessage", "Liên kết đặt lại mật khẩu không hợp lệ hoặc đã hết hạn!");
                return "redirect:/forgot-password";
            }
            
            // Check if passwords match
            if (!newPassword.equals(confirmPassword)) {
                redirectAttributes.addFlashAttribute("errorMessage", "Mật khẩu xác nhận không khớp!");
                redirectAttributes.addAttribute("token", token);
                return "redirect:/reset-password";
            }
            
            // Reset password
            userService.resetPassword(token, newPassword);
            
            redirectAttributes.addFlashAttribute("successMessage", "Đặt lại mật khẩu thành công! Vui lòng đăng nhập bằng mật khẩu mới.");
            return "redirect:/signin";
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Có lỗi xảy ra: " + e.getMessage());
            redirectAttributes.addAttribute("token", token);
            return "redirect:/reset-password";
        }
    }
}
