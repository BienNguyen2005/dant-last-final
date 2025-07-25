package com.poly.entity;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "KHACH_HANG")
public class KhachHang {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_khach_hang")
    private Long idKhachHang;

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

    @Column(name = "phan_loai")
    private String phanLoai;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "khachHang")
    private List<HoaDon> hoaDons;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "khachHang")
    private List<GioHang> gioHangs;

    // Getters, setters, constructors
    public KhachHang() {}

    public KhachHang(Long idKhachHang, String hoten, String sdt, String email, String diaChi, boolean trangThai, String phanLoai) {
        this.idKhachHang = idKhachHang;
        this.hoten = hoten;
        this.sdt = sdt;
        this.email = email;
        this.diaChi = diaChi;
        this.trangThai = trangThai;
        this.phanLoai = phanLoai;
    }

    public Long getIdKhachHang() {
        return idKhachHang;
    }
    public void setIdKhachHang(Long idKhachHang) {
        this.idKhachHang = idKhachHang;
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
    public String getPhanLoai() {
        return phanLoai;
    }
    public void setPhanLoai(String phanLoai) {
        this.phanLoai = phanLoai;
    }
    public List<HoaDon> getHoaDons() {
        return hoaDons;
    }
    public void setHoaDons(List<HoaDon> hoaDons) {
        this.hoaDons = hoaDons;
    }
    public List<GioHang> getGioHangs() {
        return gioHangs;
    }
    public void setGioHangs(List<GioHang> gioHangs) {
        this.gioHangs = gioHangs;
    }
} 