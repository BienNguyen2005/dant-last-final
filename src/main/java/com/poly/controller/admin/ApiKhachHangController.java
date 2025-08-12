package com.poly.controller.admin;

import com.poly.entity.KhachHang;
import com.poly.entity.HoaDon;
import com.poly.service.KhachHangService;
import com.poly.repository.HoaDonRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
    @Autowired
    private HoaDonRepository hoaDonRepository;

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

    // Cập nhật thông tin y tế cơ bản
    @PostMapping("/{id}/medical-notes")
    public ResponseEntity<?> updateMedicalNotes(@PathVariable("id") Long id,
                                                @RequestParam(value = "diUng", required = false) String diUng,
                                                @RequestParam(value = "chongChiDinh", required = false) String chongChiDinh,
                                                @RequestParam(value = "ghiChuYTe", required = false) String ghiChuYTe) {
        KhachHang kh = khachHangService.getKhachHangById(id).orElse(null);
        if (kh == null) return ResponseEntity.notFound().build();
        if (diUng != null) kh.setDiUng(diUng);
        if (chongChiDinh != null) kh.setChongChiDinh(chongChiDinh);
        if (ghiChuYTe != null) kh.setGhiChuYTe(ghiChuYTe);
        khachHangService.saveKhachHang(kh);
        Map<String,Object> rs = new HashMap<>();
        rs.put("message", "Cập nhật thành công");
        return ResponseEntity.ok(rs);
    }

    // Lịch sử hóa đơn theo khách
    @GetMapping("/{id}/orders")
    public ResponseEntity<?> orderHistory(@PathVariable("id") Long id,
                                          @RequestParam(defaultValue = "0") int page,
                                          @RequestParam(defaultValue = "10") int size) {
        Page<HoaDon> p = hoaDonRepository.findByKhachHang_IdKhachHang(id, PageRequest.of(page, size));
        return ResponseEntity.ok(p);
    }
} 