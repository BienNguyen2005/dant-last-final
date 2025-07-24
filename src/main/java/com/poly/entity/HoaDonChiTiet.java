package com.poly.entity;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "HOADONCHITIET")
public class HoaDonChiTiet {

	private HoaDonChiTietId id;
	private HoaDon hoaDon;
	private SanPham sanPham;
	private int soluong;
	private int gia;
	private int giamgia;

	public HoaDonChiTiet() {
	}

	public HoaDonChiTiet(HoaDonChiTietId id, HoaDon hoaDon, SanPham sanPham, int soluong, int gia, int giamgia) {
		this.id = id;
		this.hoaDon = hoaDon;
		this.sanPham = sanPham;
		this.soluong = soluong;
		this.gia = gia;
		this.giamgia = giamgia;
	}

	@EmbeddedId

	@AttributeOverrides({ @AttributeOverride(name = "idHoadon", column = @Column(name = "id_hoadon", nullable = false)),
			@AttributeOverride(name = "idSanpham", column = @Column(name = "id_sanpham", nullable = false)) })
	public HoaDonChiTietId getId() {
		return this.id;
	}

	public void setId(HoaDonChiTietId id) {
		this.id = id;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_hoadon", nullable = false, insertable = false, updatable = false)
	public HoaDon getHoaDon() {
		return this.hoaDon;
	}

	public void setHoaDon(HoaDon hoaDon) {
		this.hoaDon = hoaDon;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_sanpham", nullable = false, insertable = false, updatable = false)
	public SanPham getSanPham() {
		return this.sanPham;
	}

	public void setSanPham(SanPham sanPham) {
		this.sanPham = sanPham;
	}

	@Column(name = "soluong", nullable = false)
	public int getSoluong() {
		return this.soluong;
	}

	public void setSoluong(int soluong) {
		this.soluong = soluong;
	}

	@Column(name = "giamgia")
	public int getGiamgia() {
		return this.giamgia;
	}

	public void setGiamgia(int giamgia) {
		this.giamgia = giamgia;
	}

	@Column(name = "gia", nullable = false)
	public int getGia() {
		return this.gia;
	}

	public void setGia(int gia) {
		this.gia = gia;
	}

}
