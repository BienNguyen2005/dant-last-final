package com.poly.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class HoaDonChiTietId {

	private int idHoadon;
	private int idSanpham;

	public HoaDonChiTietId() {
	}

	public HoaDonChiTietId(int idHoadon, int idSanpham) {
		this.idHoadon = idHoadon;
		this.idSanpham = idSanpham;
	}

	@Column(name = "id_hoadon", nullable = false)
	public int getIdHoadon() {
		return this.idHoadon;
	}

	public void setIdHoadon(int idHoadon) {
		this.idHoadon = idHoadon;
	}

	@Column(name = "id_sanpham", nullable = false)
	public int getIdSanpham() {
		return this.idSanpham;
	}

	public void setIdSanpham(int idSanpham) {
		this.idSanpham = idSanpham;
	}

	public boolean equals(Object other) {
		if ((this == other))
			return true;
		if ((other == null))
			return false;
		if (!(other instanceof HoaDonChiTietId))
			return false;
		HoaDonChiTietId castOther = (HoaDonChiTietId) other;

		return (this.getIdHoadon() == castOther.getIdHoadon()) && (this.getIdSanpham() == castOther.getIdSanpham());
	}

	public int hashCode() {
		int result = 17;

		result = 37 * result + this.getIdHoadon();
		result = 37 * result + this.getIdSanpham();
		return result;
	}

}
