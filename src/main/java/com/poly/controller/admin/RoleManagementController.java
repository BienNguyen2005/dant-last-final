package com.poly.controller.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.poly.entity.Users;
import com.poly.service.UserService;

@Controller
@RequestMapping("/admin/roles")
@PreAuthorize("hasRole('ADMIN')")
public class RoleManagementController {

    @Autowired
    private UserService userService;

    @GetMapping
    public String showRoleManagement(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            Model model) {
        
        Page<Users> userPage = userService.getAllUsers(page, size);
        
        model.addAttribute("users", userPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", userPage.getTotalPages());
        model.addAttribute("totalItems", userPage.getTotalElements());
        
        return "admin/role/roleManagement";
    }
    
    @GetMapping("/{id}")
    public String showUserRoleForm(@PathVariable("id") String id, Model model) {
        try {
            Users user = userService.getUserById(id);
            model.addAttribute("user", user);
            return "admin/role/roleEdit";
        } catch (Exception e) {
            return "redirect:/admin/roles?error=user-not-found";
        }
    }
    
    @PostMapping("/update/{id}")
    public String updateUserRole(
            @PathVariable("id") String id,
            @RequestParam("vaitro") boolean isAdmin,
            RedirectAttributes redirectAttributes) {
        
        try {
            Users user = userService.getUserById(id);
            user.setVaitro(isAdmin);
            userService.updateUser(id, user);
            
            redirectAttributes.addFlashAttribute("successMessage", 
                    "Cập nhật vai trò cho người dùng " + user.getIdUser() + " thành công!");
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                    "Lỗi cập nhật vai trò: " + e.getMessage());
        }
        
        return "redirect:/admin/roles";
    }
    
    @GetMapping("/toggle-status/{id}")
    public String toggleUserStatus(
            @PathVariable("id") String id,
            RedirectAttributes redirectAttributes) {
        
        try {
            Users user = userService.getUserById(id);
            user.setKichhoat(!user.isKichhoat());
            userService.updateUser(id, user);
            
            String status = user.isKichhoat() ? "kích hoạt" : "vô hiệu hóa";
            redirectAttributes.addFlashAttribute("successMessage", 
                    "Đã " + status + " tài khoản " + user.getIdUser() + " thành công!");
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                    "Lỗi thay đổi trạng thái: " + e.getMessage());
        }
        
        return "redirect:/admin/roles";
    }
}
