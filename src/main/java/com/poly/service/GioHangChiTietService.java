package com.poly.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.poly.entity.CartItemRequest;
import com.poly.entity.GioHang;
import com.poly.entity.GioHangChiTiet;
import com.poly.entity.GioHangChiTietId;
import com.poly.entity.SanPham;
import com.poly.repository.GioHangChiTietRepository;
import com.poly.repository.GioHangRepository;
import com.poly.repository.UsersRepository;


@Service
public class GioHangChiTietService {
	@Autowired
	UsersRepository usersRepository;
	@Autowired
	SanPhamService sanPhamService;
	@Autowired
	CartService cartService;
	@Autowired
	GioHangRepository gioHangRepository;
	@Autowired
	GioHangChiTietRepository gioHangChiTietRepository;

	public void add(CartItemRequest itemRequest) throws IllegalArgumentException {
		GioHang gioHang = gioHangRepository.findByUsers_IdUser(itemRequest.getUserId());
		SanPham sanPham = sanPhamService.getSanPhamById(itemRequest.getProductId());
		Optional<GioHangChiTiet> gioHangChiTietOptional = gioHangChiTietRepository
				.findById(new GioHangChiTietId(gioHang.getIdGiohang(), sanPham.getIdSanpham()));
		if (gioHangChiTietOptional.isPresent()) {
			GioHangChiTiet gioHangChiTiet = gioHangChiTietOptional.get();
			gioHangChiTiet.setSoluong(gioHangChiTiet.getSoluong() + itemRequest.getQuantity());
			gioHangChiTietRepository.save(gioHangChiTiet);
		} else {
			GioHangChiTiet gioHangChiTiet = new GioHangChiTiet(
					new GioHangChiTietId(gioHang.getIdGiohang(), sanPham.getIdSanpham()), gioHang, sanPham,
					itemRequest.getQuantity());
			gioHangChiTietRepository.save(gioHangChiTiet);
		}
	}

	public void update(CartItemRequest itemRequest, Integer cartId) throws IllegalArgumentException {
		GioHang gioHang = cartService.getCartById(cartId);
		SanPham sanPham = sanPhamService.getSanPhamById(itemRequest.getProductId());
		GioHangChiTiet gioHangChiTiet = gioHangChiTietRepository
				.findById(new GioHangChiTietId(gioHang.getIdGiohang(), sanPham.getIdSanpham()))
				.orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm trong giỏ hàng"));
		gioHangChiTiet.setSoluong(itemRequest.getQuantity());
		gioHangChiTietRepository.save(gioHangChiTiet);
	}

	public void delete(Integer cartId, Integer productId) throws IllegalArgumentException {
		GioHang gioHang = cartService.getCartById(cartId);
		SanPham sanPham = sanPhamService.getSanPhamById(productId);
		GioHangChiTiet gioHangChiTiet = gioHangChiTietRepository
				.findById(new GioHangChiTietId(gioHang.getIdGiohang(), sanPham.getIdSanpham()))
				.orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm trong giỏ hàng"));
		gioHangChiTietRepository.delete(gioHangChiTiet);
	}

	public List<GioHangChiTiet> getAllByIdUser(String id) {
		return gioHangChiTietRepository.findByGioHang_Users_IdUser(id);
	}
}
