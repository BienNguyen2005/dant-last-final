package com.poly.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.poly.entity.HoaDon;
import com.poly.entity.ReportRevenueStatistics;

import jakarta.transaction.Transactional;

public interface HoaDonRepository extends JpaRepository<HoaDon, Integer> {
	@Modifying
	@Transactional
	@Query("DELETE FROM HoaDon h WHERE h.users.idUser = :idUser")
	void deleteByUserId(@Param("idUser") String idUser);

	Page<HoaDon> findByUsers_idUser(String email, Pageable pageable);

	@Query("SELECT COUNT(h) FROM HoaDon h WHERE h.trangthai = 'received'")
	long countReceivedOrders();
	
	@Query("SELECT new com.poly.entity.ReportRevenueStatistics( " 
			+ "SUM(hdct.soluong * (hdct.gia * (1 - hdct.giamgia/100.0))), "
			+ "SUM(hdct.soluong), " 
			+ "MAX(hdct.gia * (1 - hdct.giamgia/100.0)), " 
			+ "MIN((hdct.gia * (1 - hdct.giamgia/100.0))), " 
			+ "AVG(hdct.gia * (1 - hdct.giamgia/100.0))) " 
			+ "FROM HoaDonChiTiet hdct "
			+ "JOIN hdct.sanPham sp " 
			+ "JOIN hdct.hoaDon hd "
			+ "JOIN sp.loai l "
			+ "WHERE l.idLoai = :idLoai AND hd.trangthai = 'received'")
	ReportRevenueStatistics thongKeDoanhThuTheoLoai(@Param("idLoai") Integer loaiId);
}
