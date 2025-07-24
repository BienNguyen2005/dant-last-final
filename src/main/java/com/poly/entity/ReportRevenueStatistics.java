package com.poly.entity;

public class ReportRevenueStatistics {
	private Long tongDoanhThu;
	private Long tongSoLuong;
	private Long giaCaoNhat;
	private Long giaThapNhat;
	private Double giaTrungBinh;

	public ReportRevenueStatistics(Number tongDoanhThu, Number tongSoLuong, Number giaCaoNhat, Number giaThapNhat,
			Number giaTrungBinh) {
		this.tongDoanhThu = tongDoanhThu != null ? tongDoanhThu.longValue() : 0L;
		this.tongSoLuong = tongSoLuong != null ? tongSoLuong.longValue() : 0L;
		this.giaCaoNhat = giaCaoNhat != null ? giaCaoNhat.longValue() : 0L;
		this.giaThapNhat = giaThapNhat != null ? giaThapNhat.longValue() : 0L;
		this.giaTrungBinh = giaTrungBinh != null ? giaTrungBinh.doubleValue() : 0.0;
	}

	public Long getTongDoanhThu() {
		return tongDoanhThu;
	}

	public void setTongDoanhThu(Long tongDoanhThu) {
		this.tongDoanhThu = tongDoanhThu;
	}

	public Number getTongSoLuong() {
		return tongSoLuong;
	}

	public void setTongSoLuong(Long tongSoLuong) {
		this.tongSoLuong = tongSoLuong;
	}

	public Long getGiaCaoNhat() {
		return giaCaoNhat;
	}

	public void setGiaCaoNhat(Long giaCaoNhat) {
		this.giaCaoNhat = giaCaoNhat;
	}

	public Number getGiaThapNhat() {
		return giaThapNhat;
	}

	public void setGiaThapNhat(Long giaThapNhat) {
		this.giaThapNhat = giaThapNhat;
	}

	public Double getGiaTrungBinh() {
		return giaTrungBinh;
	}

	public void setGiaTrungBinh(Double giaTrungBinh) {
		this.giaTrungBinh = giaTrungBinh;
	}
}
