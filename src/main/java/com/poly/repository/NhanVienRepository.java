package com.poly.repository;

import com.poly.entity.NhanVien;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NhanVienRepository extends JpaRepository<NhanVien, String> {
    java.util.Optional<NhanVien> findBySdt(String sdt);
    java.util.List<NhanVien> findByCaLamViec(String caLamViec);
    java.util.List<NhanVien> findByTrangThai(boolean trangThai);
    java.util.List<NhanVien> findByHotenContainingIgnoreCase(String hoten);
} 