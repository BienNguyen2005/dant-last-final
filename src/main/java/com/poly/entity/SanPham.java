package com.poly.entity;

import java.util.Date;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.ToString;

@Entity
@ToString
@Table(name = "SANPHAM")
public class SanPham {

	private Integer idSanpham;
	private Loai loai;
	private String tenSanpham;
	private int soluong;
	private int gia;
	private int giamgia;
	private String hinh;
	private String mota;
	private String motangan;
	private Date ngaytao;
	private List<GioHangChiTiet> gioHangChiTiets;
	private List<HoaDonChiTiet> hoaDonChiTiets;

	public SanPham() {
	}

	public SanPham(String tenSanpham, int soluong, String mota, String motangan, Date ngaytao, int gia, int giamgia) {
		this.tenSanpham = tenSanpham;
		this.soluong = soluong;
		this.mota = mota;
		this.ngaytao = ngaytao;
		this.gia = gia;
		this.giamgia = giamgia;
		this.motangan = motangan;
	}

	public SanPham(Loai loai, String tenSanpham, int soluong, String hinh, String mota, String motangan, Date ngaytao,
			int gia, int giamgia, List<GioHangChiTiet> gioHangChiTiets, List<HoaDonChiTiet> hoaDonChiTiets) {
		this.loai = loai;
		this.tenSanpham = tenSanpham;
		this.soluong = soluong;
		this.hinh = hinh;
		this.mota = mota;
		this.motangan = motangan;	
		this.ngaytao = ngaytao;
		this.gia = gia;
		this.giamgia = giamgia;
		this.gioHangChiTiets = gioHangChiTiets;
		this.hoaDonChiTiets = hoaDonChiTiets;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)

	@Column(name = "id_sanpham", unique = true, nullable = false)
	public Integer getIdSanpham() {
		return this.idSanpham;
	}

	public void setIdSanpham(Integer idSanpham) {
		this.idSanpham = idSanpham;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_loai")
	public Loai getLoai() {
		return this.loai;
	}

	public void setLoai(Loai loai) {
		this.loai = loai;
	}

	@Column(name = "ten_sanpham", nullable = false)
	public String getTenSanpham() {
		return this.tenSanpham;
	}

	public void setTenSanpham(String tenSanpham) {
		this.tenSanpham = tenSanpham;
	}

	@Column(name = "soluong", nullable = false)
	public int getSoluong() {
		return this.soluong;
	}

	public void setSoluong(int soluong) {
		this.soluong = soluong;
	}

	@Column(name = "gia", nullable = false)
	public int getGia() {
		return this.gia;
	}

	public void setGia(int gia) {
		this.gia = gia;
	}

	@Column(name = "giamgia", nullable = false)
	public int getGiamgia() {
		return this.giamgia;
	}

	public void setGiamgia(int giamgia) {
		this.giamgia = giamgia;
	}

	@Column(name = "hinh")
	public String getHinh() {
		return this.hinh;
	}

	public void setHinh(String hinh) {
		this.hinh = hinh;
	}

	@Column(name = "motangan", nullable = false)
	public String getMotangan() {
		return this.motangan;
	}

	public void setMotangan(String motangan) {
		this.motangan = motangan;
	}
	
	@Column(name = "mota", nullable = false)
	public String getMota() {
		return this.mota;
	}

	public void setMota(String mota) {
		this.mota = mota;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "ngaytao", nullable = false, length = 10)
	public Date getNgaytao() {
		return this.ngaytao;
	}

	public void setNgaytao(Date ngaytao) {
		this.ngaytao = ngaytao;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "sanPham")
	public List<GioHangChiTiet> getGioHangChiTiets() {
		return this.gioHangChiTiets;
	}

	public void setGioHangChiTiets(List<GioHangChiTiet> gioHangChiTiets) {
		this.gioHangChiTiets = gioHangChiTiets;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "sanPham")
	public List<HoaDonChiTiet> getHoaDonChiTiets() {
		return this.hoaDonChiTiets;
	}

	public void setHoaDonChiTiets(List<HoaDonChiTiet> hoaDonChiTiets) {
		this.hoaDonChiTiets = hoaDonChiTiets;
	}

}
