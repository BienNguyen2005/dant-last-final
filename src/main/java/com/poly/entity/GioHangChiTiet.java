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
import lombok.ToString;

@Entity
@ToString
@Table(name = "GIOHANGCHITIET")
public class GioHangChiTiet {

	private GioHangChiTietId id;
	private GioHang gioHang;
	private SanPham sanPham;
	private int soluong;

	public GioHangChiTiet() {
	}

	public GioHangChiTiet(GioHangChiTietId id, GioHang gioHang, SanPham sanPham, int soluong) {
		this.id = id;
		this.gioHang = gioHang;
		this.sanPham = sanPham;
		this.soluong = soluong;
	}

	@EmbeddedId

	@AttributeOverrides({
			@AttributeOverride(name = "idGiohang", column = @Column(name = "id_giohang", nullable = false)),
			@AttributeOverride(name = "idSanpham", column = @Column(name = "id_sanpham", nullable = false)) })
	public GioHangChiTietId getId() {
		return this.id;
	}

	public void setId(GioHangChiTietId id) {
		this.id = id;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_giohang", nullable = false, insertable = false, updatable = false)
	public GioHang getGioHang() {
		return this.gioHang;
	}

	public void setGioHang(GioHang gioHang) {
		this.gioHang = gioHang;
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

}
