package com.poly.controller.admin;

import com.poly.entity.KhachHang;
import com.poly.service.KhachHangService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.MediaType;
import java.util.Optional;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/khachhang")
public class ApiKhachHangController {
    @Autowired
    private KhachHangService khachHangService;

    // Tìm kiếm khách hàng theo SĐT
    @GetMapping("/search")
    public ResponseEntity<?> searchBySdt(@RequestParam("sdt") String sdt) {
        Optional<KhachHang> opt = khachHangService.findBySdt(sdt);
        if(opt.isPresent()) {
            KhachHang kh = opt.get();
            Map<String, Object> result = new HashMap<>();
            result.put("idKhachHang", kh.getIdKhachHang());
            result.put("hoten", kh.getHoten());
            result.put("sdt", kh.getSdt());
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.ok(new HashMap<>());
        }
    }

    // Tạo khách hàng mới
    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createKhachHang(@RequestParam("hoten") String hoten, @RequestParam("sdt") String sdt) {
        KhachHang kh = new KhachHang();
        kh.setHoten(hoten);
        kh.setSdt(sdt);
        kh.setTrangThai(true);
        kh.setPhanLoai("Khách lẻ");
        KhachHang saved = khachHangService.saveKhachHang(kh);
        Map<String, Object> result = new HashMap<>();
        result.put("idKhachHang", saved.getIdKhachHang());
        result.put("hoten", saved.getHoten());
        result.put("sdt", saved.getSdt());
        return ResponseEntity.ok(result);
    }
} 