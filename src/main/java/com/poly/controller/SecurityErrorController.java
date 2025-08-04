package com.poly.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.poly.service.LoaiService;

@Controller
public class SecurityErrorController {
    
    @Autowired
    private LoaiService loaiService;

    @GetMapping("/access-denied")
    public String accessDenied(Model model) {
        model.addAttribute("loais", loaiService.getAllLoai(0, 5));
        return "error/accessDenied";
    }
}
