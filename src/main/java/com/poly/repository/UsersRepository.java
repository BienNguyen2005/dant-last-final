package com.poly.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.poly.entity.ReportKhachHangVip;
import com.poly.entity.Users;

public interface UsersRepository extends JpaRepository<Users, String> {
	Optional<Users> findBySdt(String sdt);

	@Query("SELECT new com.poly.entity.ReportKhachHangVip( "
			+ "u.hoten,u.hinh, SUM(hdct.soluong * (hdct.gia * (1 - hdct.giamgia/100.0))), " 
			+ "MIN(hd.ngaytao), MAX(hd.ngaytao)) "
			+ "FROM HoaDon hd "
			+ "JOIN hd.users u "
			+ "JOIN hd.hoaDonChiTiets hdct "
			+ "WHERE hd.trangthai = 'received'"
			+ "GROUP BY u.hoten, u.hinh "
			+ "ORDER BY SUM(hdct.gia * hdct.soluong - hdct.giamgia) DESC")
	Page<ReportKhachHangVip> getTop10KhachHangVip(Pageable pageable);
}