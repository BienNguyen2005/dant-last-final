package com.poly.service;

import com.poly.entity.KhachHang;
import com.poly.repository.KhachHangRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class KhachHangService {
    @Autowired
    private KhachHangRepository khachHangRepository;

    public List<KhachHang> getAllKhachHang() {
        return khachHangRepository.findAll();
    }

    public Optional<KhachHang> getKhachHangById(Long id) {
        return khachHangRepository.findById(id);
    }

    public KhachHang saveKhachHang(KhachHang khachHang) {
        return khachHangRepository.save(khachHang);
    }

    public void deleteKhachHang(Long id) {
        khachHangRepository.deleteById(id);
    }

    public Optional<KhachHang> findBySdt(String sdt) {
        return khachHangRepository.findBySdt(sdt);
    }

    public Optional<KhachHang> findByEmail(String email) {
        return khachHangRepository.findByEmail(email);
    }

    public List<KhachHang> findByPhanLoai(String phanLoai) {
        return khachHangRepository.findByPhanLoai(phanLoai);
    }

    public List<KhachHang> findByTrangThai(boolean trangThai) {
        return khachHangRepository.findByTrangThai(trangThai);
    }

    public List<KhachHang> findByHotenContainingIgnoreCase(String hoten) {
        return khachHangRepository.findByHotenContainingIgnoreCase(hoten);
    }

    public List<KhachHang> findByPhanLoaiAndTrangThai(String phanLoai, boolean trangThai) {
        return khachHangRepository.findByPhanLoaiAndTrangThai(phanLoai, trangThai);
    }
} 