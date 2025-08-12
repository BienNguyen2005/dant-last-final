package com.poly.entity;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.ToString;

@Entity
@ToString
@Table(name = "USERS")
public class Users {

	private String idUser;
	private String sdt;
	private String hinh;
	private String hoten;
	private String matkhau;
	private boolean vaitro;
	private boolean kichhoat;
	private List<GioHang> gioHangs;
	private List<HoaDon> hoaDons;

	public Users() {
	}

	public Users(String idUser, String sdt, String hinh, String hoten, String matkhau, boolean vaitro,
			List<GioHang> gioHangs, List<HoaDon> hoaDons) {
		this.idUser = idUser;
		this.sdt = sdt;
		this.hinh = hinh;
		this.hoten = hoten;
		this.matkhau = matkhau;
		this.vaitro = vaitro;
		this.gioHangs = gioHangs;
		this.hoaDons = hoaDons;
	}

	@Id

	@Column(name = "id_user", unique = true, nullable = false, length = 50)
	public String getIdUser() {
		return this.idUser;
	}

	public void setIdUser(String idUser) {
		this.idUser = idUser;
	}

	@Column(name = "sdt", nullable = false, length = 10)
	public String getSdt() {
		return this.sdt;
	}

	public void setSdt(String sdt) {
		this.sdt = sdt;
	}

	@Column(name = "hinh")
	public String getHinh() {
		return this.hinh;
	}

	public void setHinh(String hinh) {
		this.hinh = hinh;
	}

	@Column(name = "hoten", nullable = false)
	public String getHoten() {
		return this.hoten;
	}

	public void setHoten(String hoten) {
		this.hoten = hoten;
	}

	@Column(name = "matkhau", nullable = false, length = 60)
	public String getMatkhau() {
		return this.matkhau;
	}

	public void setMatkhau(String matkhau) {
		this.matkhau = matkhau;
	}

	@Column(name = "vaitro", nullable = false)
	public boolean isVaitro() {
		return this.vaitro;
	}

	public void setVaitro(boolean vaitro) {
		this.vaitro = vaitro;
	}
	
	@Column(name = "kichhoat", nullable = false)
	public boolean isKichhoat() {
		return this.kichhoat;
	}

	public void setKichhoat(boolean kichhoat) {
		this.kichhoat = kichhoat;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "users")
	public List<GioHang> getGioHangs() {
		return this.gioHangs;
	}

	public void setGioHangs(List<GioHang> gioHangs) {
		this.gioHangs = gioHangs;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "users")
	public List<HoaDon> getHoaDons() {
		return this.hoaDons;
	}

	public void setHoaDons(List<HoaDon> hoaDons) {
		this.hoaDons = hoaDons;
	}

}
