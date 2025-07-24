 package com.poly.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class GioHangChiTietId {

	private int idGiohang;
	private int idSanpham;

	public GioHangChiTietId() {
	}

	public GioHangChiTietId(int idGiohang, int idSanpham) {
		this.idGiohang = idGiohang;
		this.idSanpham = idSanpham;
	}

	@Column(name = "id_giohang", nullable = false)
	public int getIdGiohang() {
		return this.idGiohang;
	}

	public void setIdGiohang(int idGiohang) {
		this.idGiohang = idGiohang;
	}

	@Column(name = "id_sanpham", nullable = false)
	public int getIdSanpham() {
		return this.idSanpham;
	}

	public void setIdSanpham(int idSanpham) {
		this.idSanpham = idSanpham;
	}

}
