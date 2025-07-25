package com.poly.repository;

import com.poly.entity.KhachHang;
import org.springframework.data.jpa.repository.JpaRepository;

public interface KhachHangRepository extends JpaRepository<KhachHang, Long> {
    java.util.Optional<KhachHang> findBySdt(String sdt);
    java.util.Optional<KhachHang> findByEmail(String email);
    java.util.List<KhachHang> findByPhanLoai(String phanLoai);
    java.util.List<KhachHang> findByTrangThai(boolean trangThai);
    java.util.List<KhachHang> findByHotenContainingIgnoreCase(String hoten);
    java.util.List<KhachHang> findByPhanLoaiAndTrangThai(String phanLoai, boolean trangThai);
} 