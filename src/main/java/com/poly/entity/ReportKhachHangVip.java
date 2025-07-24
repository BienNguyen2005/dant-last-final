package com.poly.entity;

import java.util.Date;

public class ReportKhachHangVip {
	private String tenKhachHang;
	private String hinh;
	private Long tongTienDaMua;
	private Date ngayMuaDauTien;
	private Date ngayMuaSauCung;

	public ReportKhachHangVip(String tenKhachHang, String hinh, Number tongTienDaMua, Date ngayMuaDauTien,
			Date ngayMuaSauCung) {
		this.tenKhachHang = tenKhachHang;
		this.hinh = hinh;
		this.tongTienDaMua = tongTienDaMua != null ? tongTienDaMua.longValue() : 0L;
		this.ngayMuaDauTien = ngayMuaDauTien;
		this.ngayMuaSauCung = ngayMuaSauCung;
	}

	public String getTenKhachHang() {
		return tenKhachHang;
	}

	public void setTenKhachHang(String tenKhachHang) {
		this.tenKhachHang = tenKhachHang;
	}

	public String getHinh() {
		return hinh;
	}

	public void setHinh(String hinh) {
		this.hinh = hinh;
	}

	public Long getTongTienDaMua() {
		return tongTienDaMua;
	}

	public void setTongTienDaMua(Long tongTienDaMua) {
		this.tongTienDaMua = tongTienDaMua;
	}

	public Date getNgayMuaDauTien() {
		return ngayMuaDauTien;
	}

	public void setNgayMuaDauTien(Date ngayMuaDauTien) {
		this.ngayMuaDauTien = ngayMuaDauTien;
	}

	public Date getNgayMuaSauCung() {
		return ngayMuaSauCung;
	}

	public void setNgayMuaSauCung(Date ngayMuaSauCung) {
		this.ngayMuaSauCung = ngayMuaSauCung;
	}
}
