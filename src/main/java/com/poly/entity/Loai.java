package com.poly.entity;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.ToString;

@Entity
@Table(name = "LOAI")
public class Loai {

	private Integer idLoai;
	private String tenLoai;
	private List<SanPham> sanPhams;

	public Loai() {
	}

	public Loai(String tenLoai) {
		this.tenLoai = tenLoai;
	}

	public Loai(String tenLoai, List<SanPham> sanPhams) {
		this.tenLoai = tenLoai;
		this.sanPhams = sanPhams;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)

	@Column(name = "id_loai", unique = true, nullable = false)
	public Integer getIdLoai() {
		return this.idLoai;
	}

	public void setIdLoai(Integer idLoai) {
		this.idLoai = idLoai;
	}

	@Column(name = "ten_loai", nullable = false)
	public String getTenLoai() {
		return this.tenLoai;
	}

	public void setTenLoai(String tenLoai) {
		this.tenLoai = tenLoai;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "loai")
	public List<SanPham> getSanPhams() {
		return this.sanPhams;
	}

	public void setSanPhams(List<SanPham> sanPhams) {
		this.sanPhams = sanPhams;
	}

}
