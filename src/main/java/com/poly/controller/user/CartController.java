package com.poly.controller.user;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.poly.entity.CartItemRequest;
import com.poly.entity.CartItemResponse;
import com.poly.entity.HoaDon;
import com.poly.entity.OrderRequest;
import com.poly.entity.SanPham;
import com.poly.service.GioHangChiTietService;
import com.poly.service.HoaDonService;
import com.poly.service.LoaiService;
import com.poly.service.SanPhamService;
import com.poly.service.UserService;

@Controller
public class CartController {
	@Autowired
	GioHangChiTietService gioHangChiTietService;
	@Autowired
	UserService userService;
	@Autowired
	SanPhamService sanPhamService;
	@Autowired
	LoaiService loaiService;
	@Autowired
	HoaDonService hoaDonService;

	@GetMapping("/cart")
	public String cart(Model model) {
		model.addAttribute("loais", loaiService.getAllLoai(0, 5));
		return "user/cart";
	}

	@ResponseBody
	@PostMapping("/cartItem")
	public ResponseEntity<Object> cartItem(@RequestBody CartItemRequest cartItemRequest) {
		Map<String, String> response = new HashMap<>();
		try {
			gioHangChiTietService.add(cartItemRequest);
			response.put("message", "Đã thêm sản phẩm vào giỏ hàng thành công!");
			return ResponseEntity.status(200).body(response);
		} catch (Exception e) {
			response.put("message", "Đã có lỗi truy vấn!");
			return ResponseEntity.status(404).body(response);
		}
	}

	@ResponseBody
	@GetMapping("/cartItem")
	public ResponseEntity<Object> cartItem(@RequestParam(name = "userId") String id) {
		try {
			userService.getUserById(id);
			AtomicInteger counter = new AtomicInteger(1);
			Map<String, List<CartItemResponse>> response = new HashMap<>();
			List<CartItemResponse> cartItemResponses = gioHangChiTietService.getAllByIdUser(id).stream()
					.filter(item -> {
						SanPham sanPham = sanPhamService.getSanPhamById(item.getSanPham().getIdSanpham());
						if (sanPham.getSoluong() > 0) {
							return true;
						} else {
							gioHangChiTietService.delete(item.getGioHang().getIdGiohang(), sanPham.getIdSanpham());
							return false;
						}
					})
					.map(item -> new CartItemResponse(counter.getAndIncrement(), item.getGioHang().getIdGiohang(),
							item.getSanPham().getIdSanpham(), item.getSanPham().getTenSanpham(),
							item.getSanPham().getGia(), item.getSanPham().getGiamgia(), item.getSanPham().getSoluong(),
							item.getSanPham().getHinh(), item.getSoluong()))
					.collect(Collectors.toList());
			response.put("cartItems", cartItemResponses);
			return ResponseEntity.status(200).body(response);
		} catch (Exception e) {
			Map<String, String> response = new HashMap<>();
			response.put("message", "Đã có lỗi truy vấn!");
			return ResponseEntity.status(404).body(response);
		}
	}

	@ResponseBody
	@PutMapping("/cartItem")
	public ResponseEntity<Object> cartItem(@RequestBody CartItemRequest cartItemRequest,
			@RequestParam(name = "cartItemId") Integer cartItemId) {
		Map<String, String> response = new HashMap<>();
		try {
			gioHangChiTietService.update(cartItemRequest, cartItemId);
			response.put("message", "Đã cập nhật số lượng của sản phẩm thành công!");
			return ResponseEntity.status(200).body(response);
		} catch (Exception e) {
			e.printStackTrace();
			response.put("message", "Đã có lỗi truy vấn!");
			return ResponseEntity.status(404).body(response);
		}
	}

	@ResponseBody
	@DeleteMapping("/cartItem")
	public ResponseEntity<Object> cartItem(@RequestParam(name = "cartItemId") Integer cartItemId,
			@RequestParam(name = "productId") Integer productId) {
		Map<String, String> response = new HashMap<>();
		try {
			gioHangChiTietService.delete(cartItemId, productId);
			response.put("message", "Đã cập nhật số lượng của sản phẩm thành công!");
			return ResponseEntity.status(200).body(response);
		} catch (Exception e) {
			response.put("message", "Đã có lỗi truy vấn!");
			return ResponseEntity.status(404).body(response);
		}
	}

	@ResponseBody
	@PostMapping("/cart")
	public ResponseEntity<Object> cart(@RequestBody OrderRequest orderRequest) {
		Map<String, String> response = new HashMap<>();
		try {
			hoaDonService.add(orderRequest);
			response.put("message", "Đã thêm sản phẩm vào giỏ hàng thành công!");
			return ResponseEntity.status(200).body(response);
		} catch (RuntimeException e) {
			response.put("message", "Số lượng tồn kho của sản phẩm " + e.getMessage());
			return ResponseEntity.status(422).body(response);
		} catch (Exception e) {
			response.put("message", "Đã có lỗi truy vấn!");
			return ResponseEntity.status(404).body(response);
		}
	}
}
