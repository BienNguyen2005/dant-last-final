package com.poly.controller.user;

import java.util.stream.Collectors;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.poly.service.GioHangChiTietService;

@Controller
public class FragmentController {

    @Autowired
    GioHangChiTietService gioHangChiTietService;

    @GetMapping("/fragment/cart-indicator")
    public String cartIndicator(@RequestParam(name = "userId", required = false) String userId, Model model) {
        int count = 0;
        try {
            if (userId == null || userId.isBlank()) {
                Authentication auth = SecurityContextHolder.getContext().getAuthentication();
                if (auth != null && auth.isAuthenticated() && auth.getPrincipal() instanceof org.springframework.security.core.userdetails.User userDetails) {
                    userId = userDetails.getUsername();
                }
            }
            if (userId != null && !userId.isBlank()) {
                count = gioHangChiTietService.getAllByIdUser(userId)
                        .stream()
                        .collect(Collectors.summingInt(it -> it.getSoluong()));
            }
        } catch (Exception ignored) {
            count = 0; 
        }
        model.addAttribute("cartCount", count);
        return "fragments/_cartIndicator :: cartIndicator";
    }
}
