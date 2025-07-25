package com.poly.controller.admin;

import com.poly.entity.NhanVien;
import com.poly.service.NhanVienService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin/nhanvien")
public class NhanVienController {
    @Autowired
    private NhanVienService nhanVienService;

    @GetMapping("")
    public String listNhanVien(Model model) {
        List<NhanVien> list = nhanVienService.getAllNhanVien();
        model.addAttribute("nhanViens", list);
        return "admin/nhanvien/nhanvienManager";
    }

    @GetMapping("/view/{id}")
    public String viewNhanVien(@PathVariable String id, Model model) {
        Optional<NhanVien> nv = nhanVienService.getNhanVienById(id);
        nv.ifPresent(nhanVien -> model.addAttribute("nhanVien", nhanVien));
        return "admin/nhanvien/nhanvienDetail";
    }

    @GetMapping("/create")
    public String createForm(Model model) {
        model.addAttribute("nhanVien", new NhanVien());
        return "admin/nhanvien/createNhanVien";
    }

    @PostMapping("/create")
    public String create(@ModelAttribute NhanVien nhanVien) {
        nhanVienService.saveNhanVien(nhanVien);
        return "redirect:/admin/nhanvien";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable String id, Model model) {
        Optional<NhanVien> nv = nhanVienService.getNhanVienById(id);
        nv.ifPresent(nhanVien -> model.addAttribute("nhanVien", nhanVien));
        return "admin/nhanvien/editNhanVien";
    }

    @PostMapping("/edit/{id}")
    public String edit(@PathVariable String id, @ModelAttribute NhanVien nhanVien) {
        nhanVien.setIdNhanVien(id);
        nhanVienService.saveNhanVien(nhanVien);
        return "redirect:/admin/nhanvien";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable String id) {
        nhanVienService.deleteNhanVien(id);
        return "redirect:/admin/nhanvien";
    }

    @GetMapping("/search")
    public String search(@RequestParam(required = false) String hoten,
                        @RequestParam(required = false) String caLamViec,
                        @RequestParam(required = false) Boolean trangThai,
                        Model model) {
        List<NhanVien> result;
        if (hoten != null && !hoten.isEmpty()) {
            result = nhanVienService.findByHotenContainingIgnoreCase(hoten);
        } else if (caLamViec != null) {
            result = nhanVienService.findByCaLamViec(caLamViec);
        } else if (trangThai != null) {
            result = nhanVienService.findByTrangThai(trangThai);
        } else {
            result = nhanVienService.getAllNhanVien();
        }
        model.addAttribute("nhanViens", result);
        return "admin/nhanvien/nhanvienManager";
    }
} 