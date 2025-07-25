package com.poly.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "NHAN_VIEN")
public class NhanVien {
    @Id
    @Column(name = "id_nhan_vien", unique = true, nullable = false, length = 50)
    private String idNhanVien;

    @Column(name = "hoten", nullable = false)
    private String hoten;

    @Column(name = "sdt", nullable = false, length = 10)
    private String sdt;

    @Column(name = "email")
    private String email;

    @Column(name = "dia_chi")
    private String diaChi;

    @Column(name = "trang_thai", nullable = false)
    private boolean trangThai;

    @Column(name = "luong")
    private Double luong;

    @Column(name = "ca_lam_viec")
    private String caLamViec;

    // Getters, setters, constructors
    public NhanVien() {}

    public NhanVien(String idNhanVien, String hoten, String sdt, String email, String diaChi, boolean trangThai, Double luong, String caLamViec) {
        this.idNhanVien = idNhanVien;
        this.hoten = hoten;
        this.sdt = sdt;
        this.email = email;
        this.diaChi = diaChi;
        this.trangThai = trangThai;
        this.luong = luong;
        this.caLamViec = caLamViec;
    }

    public String getIdNhanVien() {
        return idNhanVien;
    }
    public void setIdNhanVien(String idNhanVien) {
        this.idNhanVien = idNhanVien;
    }
    public String getHoten() {
        return hoten;
    }
    public void setHoten(String hoten) {
        this.hoten = hoten;
    }
    public String getSdt() {
        return sdt;
    }
    public void setSdt(String sdt) {
        this.sdt = sdt;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getDiaChi() {
        return diaChi;
    }
    public void setDiaChi(String diaChi) {
        this.diaChi = diaChi;
    }
    public boolean isTrangThai() {
        return trangThai;
    }
    public void setTrangThai(boolean trangThai) {
        this.trangThai = trangThai;
    }
    public Double getLuong() {
        return luong;
    }
    public void setLuong(Double luong) {
        this.luong = luong;
    }
    public String getCaLamViec() {
        return caLamViec;
    }
    public void setCaLamViec(String caLamViec) {
        this.caLamViec = caLamViec;
    }
} 