package com.poly.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.poly.entity.GioHangChiTiet;
import com.poly.entity.GioHangChiTietId;

import jakarta.transaction.Transactional;

public interface GioHangChiTietRepository extends JpaRepository<GioHangChiTiet, GioHangChiTietId> {
	@Modifying
	@Transactional
	@Query("DELETE FROM GioHangChiTiet g WHERE g.gioHang.users.idUser = :idUser")
	void deleteByUserId(@Param("idUser") String idUser);
	
	List<GioHangChiTiet> findByGioHang_Users_IdUser(String id);
}
