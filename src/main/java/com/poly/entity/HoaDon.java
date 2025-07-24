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
@Table(name = "HOADON")
public class HoaDon {

	private Integer idHoadon;
	private Users users;
	private Date ngaytao;
	private String trangthai;
	private String giaohang;
	private String diachi;
	private List<HoaDonChiTiet> hoaDonChiTiets;

	public HoaDon() {
	}

	public HoaDon(Users users, Date ngaytao, String trangthai, String diachi, String giaohang) {
		this.users = users;
		this.ngaytao = ngaytao;
		this.trangthai = trangthai;
		this.diachi = diachi;
		this.giaohang = giaohang;
	}

	public HoaDon(Users users, Date ngaytao, String trangthai, String diachi, List<HoaDonChiTiet> hoaDonChiTiets) {
		this.users = users;
		this.ngaytao = ngaytao;
		this.trangthai = trangthai;
		this.diachi = diachi;
		this.hoaDonChiTiets = hoaDonChiTiets;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_hoadon", unique = true, nullable = false)
	public Integer getIdHoadon() {
		return this.idHoadon;
	}

	public void setIdHoadon(Integer idHoadon) {
		this.idHoadon = idHoadon;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_user")
	public Users getUsers() {
		return this.users;
	}

	public void setUsers(Users users) {
		this.users = users;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "ngaytao", nullable = false, length = 10)
	public Date getNgaytao() {
		return this.ngaytao;
	}

	public void setNgaytao(Date ngaytao) {
		this.ngaytao = ngaytao;
	}

	@Column(name = "trangthai", nullable = false, length = 30)
	public String getTrangthai() {
		return this.trangthai;
	}

	public void setTrangthai(String trangthai) {
		this.trangthai = trangthai;
	}

	@Column(name = "giaohang", nullable = false)
	public String getGiaohang() {
		return this.giaohang;
	}

	public void setGiaohang(String giaohang) {
		this.giaohang = giaohang;
	}

	@Column(name = "diachi", nullable = false)
	public String getDiachi() {
		return this.diachi;
	}

	public void setDiachi(String diachi) {
		this.diachi = diachi;
	}

	@OneToMany(fetch = FetchType.EAGER, mappedBy = "hoaDon")
	public List<HoaDonChiTiet> getHoaDonChiTiets() {
		return this.hoaDonChiTiets;
	}

	public void setHoaDonChiTiets(List<HoaDonChiTiet> hoaDonChiTiets) {
		this.hoaDonChiTiets = hoaDonChiTiets;
	}

}
