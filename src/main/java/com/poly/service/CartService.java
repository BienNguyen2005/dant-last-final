package com.poly.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.poly.entity.GioHang;
import com.poly.repository.GioHangRepository;

@Service
public class CartService {
	@Autowired
	GioHangRepository gioHangRepository;

	public GioHang getCartById(Integer id) {
		return gioHangRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("Giỏ hàng không tồn tại!"));
	}
}
