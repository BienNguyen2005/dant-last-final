package com.poly.controller.admin;

import com.poly.entity.KhachHang;
import com.poly.service.KhachHangService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin/khachhang")
public class KhachHangController {
    @Autowired
    private KhachHangService khachHangService;

    @GetMapping("")
    public String listKhachHang(Model model) {
        List<KhachHang> list = khachHangService.getAllKhachHang();
        model.addAttribute("khachHangs", list);
        return "admin/khachhang/khachhangManager";
    }

    @GetMapping("/view/{id}")
    public String viewKhachHang(@PathVariable String id, Model model) {
        Optional<KhachHang> kh = khachHangService.getKhachHangById(Long.valueOf(id));
        kh.ifPresent(khachHang -> model.addAttribute("khachHang", khachHang));
        return "admin/khachhang/khachhangDetail";
    }

    @GetMapping("/create")
    public String createForm(Model model) {
        model.addAttribute("khachHang", new KhachHang());
        return "admin/khachhang/createKhachHang";
    }

    @PostMapping("/create")
    public String create(@ModelAttribute KhachHang khachHang) {
        khachHangService.saveKhachHang(khachHang);
        return "redirect:/admin/khachhang";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable String id, Model model) {
        Optional<KhachHang> kh = khachHangService.getKhachHangById(Long.valueOf(id));
        kh.ifPresent(khachHang -> model.addAttribute("khachHang", khachHang));
        return "admin/khachhang/editKhachHang";
    }

    @PostMapping("/edit/{id}")
    public String edit(@PathVariable String id, @ModelAttribute KhachHang khachHang) {
        khachHang.setIdKhachHang(Long.valueOf(id));
        khachHangService.saveKhachHang(khachHang);
        return "redirect:/admin/khachhang";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable String id) {
        khachHangService.deleteKhachHang(Long.valueOf(id));
        return "redirect:/admin/khachhang";
    }

    @GetMapping("/search")
    public String search(@RequestParam(required = false) String hoten,
                        @RequestParam(required = false) String phanLoai,
                        @RequestParam(required = false) Boolean trangThai,
                        Model model) {
        List<KhachHang> result;
        if (hoten != null && !hoten.isEmpty()) {
            result = khachHangService.findByHotenContainingIgnoreCase(hoten);
        } else if (phanLoai != null && trangThai != null) {
            result = khachHangService.findByPhanLoaiAndTrangThai(phanLoai, trangThai);
        } else if (phanLoai != null) {
            result = khachHangService.findByPhanLoai(phanLoai);
        } else if (trangThai != null) {
            result = khachHangService.findByTrangThai(trangThai);
        } else {
            result = khachHangService.getAllKhachHang();
        }
        model.addAttribute("khachHangs", result);
        return "admin/khachhang/khachhangManager";
    }
} 