package com.poly.service;

import com.poly.entity.NhanVien;
import com.poly.repository.NhanVienRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class NhanVienService {
    @Autowired
    private NhanVienRepository nhanVienRepository;

    public List<NhanVien> getAllNhanVien() {
        return nhanVienRepository.findAll();
    }

    public Optional<NhanVien> getNhanVienById(String id) {
        return nhanVienRepository.findById(id);
    }

    public NhanVien saveNhanVien(NhanVien nhanVien) {
        return nhanVienRepository.save(nhanVien);
    }

    public void deleteNhanVien(String id) {
        nhanVienRepository.deleteById(id);
    }

    public Optional<NhanVien> findBySdt(String sdt) {
        return nhanVienRepository.findBySdt(sdt);
    }

    public List<NhanVien> findByCaLamViec(String caLamViec) {
        return nhanVienRepository.findByCaLamViec(caLamViec);
    }

    public List<NhanVien> findByTrangThai(boolean trangThai) {
        return nhanVienRepository.findByTrangThai(trangThai);
    }

    public List<NhanVien> findByHotenContainingIgnoreCase(String hoten) {
        return nhanVienRepository.findByHotenContainingIgnoreCase(hoten);
    }
} 