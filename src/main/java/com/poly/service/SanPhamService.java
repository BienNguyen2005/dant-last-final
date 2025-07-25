package com.poly.service;

import java.util.Date;
import java.util.Objects;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.poly.entity.SanPham;
import com.poly.repository.SanPhamRepository;
import com.poly.utils.ImageUtils;

@Service
public class SanPhamService {
	@Autowired
	SanPhamRepository sanPhamRepository;

	public SanPham create(SanPham sanPham, MultipartFile image) {
		if (sanPham.getSoluong() < 0) {
			throw new RuntimeException("Vui lòng nhập số lượng >= 0!");
		}
		if (sanPham.getGia() <= 0) {
			throw new RuntimeException("Vui lòng nhập giá > 0!");
		}
		if (sanPham.getGiamgia() < 0) {
			throw new RuntimeException("Vui lòng nhập giảm giá >= 0!");
		}

		String fileName = ImageUtils.upload(image)
				.orElseThrow(() -> new RuntimeException("Có lỗi xảy ra trong quá trình tải ảnh"));
		sanPham.setHinh(fileName);
		sanPham.setNgaytao(new Date());
		sanPhamRepository.save(sanPham);
		return sanPham;
	}

	public SanPham updateSanPham(Integer id, SanPham updatedSanPham, MultipartFile image) {
		if (updatedSanPham.getSoluong() < 0) {
			throw new RuntimeException("Vui lòng nhập số lượng >= 0!");
		}
		if (updatedSanPham.getGia() <= 0) {
			throw new RuntimeException("Vui lòng nhập giá > 0!");
		}
		if (updatedSanPham.getGiamgia() < 0) {
			throw new RuntimeException("Vui lòng nhập giảm giá >= 0!");
		}

		Optional<SanPham> optionalSanPham = sanPhamRepository.findById(id);
		if (optionalSanPham.isPresent()) {
			SanPham sanPham = optionalSanPham.get();
			if (image.getSize() > 0) {
				if (Objects.nonNull(sanPham.getHinh()) || sanPham.getHinh() == "") {
					ImageUtils.delete(sanPham.getHinh());
				}
				sanPham.setHinh(ImageUtils.upload(image)
						.orElseThrow(() -> new RuntimeException("Có lỗi xảy ra trong quá trình tải ảnh")));
			}
			sanPham.setTenSanpham(updatedSanPham.getTenSanpham());
			sanPham.setLoai(updatedSanPham.getLoai());
			sanPham.setMota(updatedSanPham.getMota());
			sanPham.setMotangan(updatedSanPham.getMotangan());
			sanPham.setGiamgia(updatedSanPham.getGiamgia());
			sanPham.setGia(updatedSanPham.getGia());
			sanPham.setSoluong(updatedSanPham.getSoluong());
			return sanPhamRepository.save(sanPham);
		} else {
			throw new RuntimeException("Không tìm thấy mã sản phẩm!");
		}
	}

	public Page<SanPham> getAllSanPham(int pageNumber, int limit) {
		PageRequest pageable = PageRequest.of(pageNumber, limit, Sort.by("ngaytao").descending());
		return sanPhamRepository.findAll(pageable);
	}

	public Page<SanPham> getAllSanPhamGiamGia(int pageNumber, int limit) {
		PageRequest pageable = PageRequest.of(pageNumber, limit, Sort.by("ngaytao").descending());
		return sanPhamRepository.findByGiamgiaGreaterThan(0, pageable);
	}

	public Page<SanPham> searchByName(int pageNumber, int limit, String keyword) {
		PageRequest pageable = PageRequest.of(pageNumber, limit);
		return sanPhamRepository.findByTenSanphamContainingIgnoreCase(keyword, pageable);
	}

	public SanPham getSanPhamById(Integer id) {
		return sanPhamRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("Mã sản phẩm không tồn tại!"));
	}

	public Page<SanPham> getSanPhamByIdLoai(Integer id, int pageNumber, int limit) {
		PageRequest pageable = PageRequest.of(pageNumber, limit);
		return sanPhamRepository.findByLoai_IdLoai(id, pageable);
	}

	public Integer getLuotMuaById(Integer id) {
		return sanPhamRepository.tongSoLuongSanPhamDaBanTheoId(id);
	}

	public void deleteSanPham(Integer id) {
		try {
			SanPham sanPham = sanPhamRepository.findById(id)
					.orElseThrow(() -> new RuntimeException("Mã sản phẩm không tồn tại!"));
			sanPhamRepository.deleteById(id);
			if (Objects.nonNull(sanPham.getHinh()) || sanPham.getHinh() == "") {
				ImageUtils.delete(sanPham.getHinh());
			}
		} catch (Exception e) {
			throw new RuntimeException("Sản phẩm này đã tồn tại trên hóa đơn không thể xóa!");
		}
	}

	public Page<SanPham> filterProducts(Integer idLoai, String priceRanges, String order, int pageNumber, int limit) {
		PageRequest pageable = PageRequest.of(pageNumber, limit);
		Integer minPrice = null;
		Integer maxPrice = null;

		if (priceRanges != null && !priceRanges.isEmpty()) {
			String[] parts = priceRanges.split("-");
			minPrice = Integer.parseInt(parts[0]);
			maxPrice = parts[1].equals("infinity") ? Integer.MAX_VALUE : Integer.parseInt(parts[1]);
		}
		return sanPhamRepository.filterProducts(idLoai, minPrice, maxPrice, order, pageable);
	}

	public Page<SanPham> findSanPhamByUser(String idUser, int pageNumber, int limit) {
		PageRequest pageable = PageRequest.of(pageNumber, limit);
		return sanPhamRepository.findSanPhamByUser(idUser, pageable);
	}

	public Page<SanPham> searchByTenAndLoai(int pageNumber, int limit, String keyword, Integer idLoai) {
		PageRequest pageable = PageRequest.of(pageNumber, limit);
		return sanPhamRepository.findByTenSanphamContainingIgnoreCaseAndLoai_IdLoai(keyword, idLoai, pageable);
	}
}
