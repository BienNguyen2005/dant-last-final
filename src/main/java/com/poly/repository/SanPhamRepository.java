package com.poly.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.poly.entity.SanPham;

public interface SanPhamRepository extends JpaRepository<SanPham, Integer> {
	Page<SanPham> findByTenSanphamContainingIgnoreCase(String keyword, Pageable pageable);

	Page<SanPham> findByLoai_IdLoai(Integer id, Pageable pageable);

	Page<SanPham> findByGiamgiaGreaterThan(Integer value, Pageable pageable);

	@Query("SELECT COALESCE(SUM(hdct.soluong), 0) " + "FROM HoaDonChiTiet hdct " + "JOIN hdct.hoaDon hd "
			+ "WHERE hd.trangthai = 'received' " + "AND hdct.sanPham.idSanpham = :idSanpham")
	int tongSoLuongSanPhamDaBanTheoId(@Param("idSanpham") Integer idSanpham);

	@Query("SELECT s FROM SanPham s " + "WHERE (:idLoai IS NULL OR s.loai.idLoai = :idLoai) "
			+ "AND (:minPrice IS NULL OR s.gia >= :minPrice) " + "AND (:maxPrice IS NULL OR s.gia <= :maxPrice) "
			+ "ORDER BY "
			+ "CASE WHEN :sortBy = 'totalBuy-DESC' THEN (SELECT SUM(hdct.soluong) FROM HoaDonChiTiet hdct WHERE hdct.sanPham = s) END DESC, "
			+ "CASE WHEN :sortBy = 'createdAt-DESC' THEN s.ngaytao END DESC, "
			+ "CASE WHEN :sortBy = 'price-ASC' THEN s.gia END ASC")
	Page<SanPham> filterProducts(@Param("idLoai") Integer idLoai, @Param("minPrice") Integer minPrice,
			@Param("maxPrice") Integer maxPrice, @Param("sortBy") String sortBy, Pageable pageable);

	@Query("SELECT DISTINCT hct.sanPham FROM HoaDonChiTiet hct " + "JOIN hct.hoaDon hd "
			+ "WHERE hd.users.idUser = :idUser AND hd.trangthai = 'received'")
	Page<SanPham> findSanPhamByUser(@Param("idUser") String idUser, Pageable pageable);

	Page<SanPham> findByTenSanphamContainingIgnoreCaseAndLoai_IdLoai(String keyword, Integer idLoai, Pageable pageable);

}
