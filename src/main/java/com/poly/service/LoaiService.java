package com.poly.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.poly.entity.GioHang;
import com.poly.entity.Loai;
import com.poly.entity.Users;
import com.poly.repository.GioHangRepository;
import com.poly.repository.LoaiRepository;
import com.poly.repository.UsersRepository;

import jakarta.servlet.http.HttpSession;

@Service
public class LoaiService {
	@Autowired
	LoaiRepository loaiRepository;
	@Autowired
	GioHangRepository gioHangRepository;

	public Loai create(Loai loai) {
		Optional<Loai> existingLoaiByName = loaiRepository.findByTenLoai(loai.getTenLoai());
		if (existingLoaiByName.isPresent()) {
			throw new IllegalArgumentException("Tên loại này đã được sử dụng!");
		}
		loaiRepository.save(loai);
		return loai;
	}

	public Loai updateUser(Integer id, Loai updatedLoai) {
		Optional<Loai> optionalLoai = loaiRepository.findById(id);
		if (optionalLoai.isPresent()) {
			Loai loai = optionalLoai.get();
			loai.setTenLoai(updatedLoai.getTenLoai());
			return loaiRepository.save(loai);
		} else {
			throw new RuntimeException("Không tìm thấy mã loại!");
		}
	}

	public Page<Loai> getAllLoai(int pageNumber, int limit) {
		PageRequest pageable = PageRequest.of(pageNumber, limit);
		return loaiRepository.findAll(pageable);
	}

	public Loai getLoaiById(Integer id) {
		return loaiRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Mã loại không tồn tại!"));
	}

	public void deleteLoai(Integer id) {
		if (!loaiRepository.existsById(id)) {
			throw new RuntimeException("Mã loại không tồn tại!");
		}
		loaiRepository.deleteById(id);
	}
}
