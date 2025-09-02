package com.poly.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.poly.entity.HoaDon;
import com.poly.entity.HoaDonChiTiet;
import com.poly.entity.HoaDonChiTietId;
import com.poly.entity.OrderRequest;
import com.poly.entity.ReportRevenueStatistics;
import com.poly.entity.SanPham;
import com.poly.entity.Users;
import com.poly.repository.HoaDonChiTietRepository;
import com.poly.repository.HoaDonRepository;
import com.poly.repository.SanPhamRepository;

@Service
public class HoaDonService {
	@Autowired
	UserService userService;
	@Autowired
	SanPhamService sanPhamService;
	@Autowired
	SanPhamRepository sanPhamRepository;
	@Autowired
	HoaDonRepository hoaDonRepository;
	@Autowired
	HoaDonChiTietRepository hoaDonChiTietRepository;
	@Autowired
	GioHangChiTietService gioHangChiTietService;
	@Autowired
	EmailService emailService;
	@Autowired
	VoucherService voucherService;

	public void add(OrderRequest orderRequest) throws RuntimeException {
		try {
			List<HoaDonChiTiet> listHoaDonChiTiets = new ArrayList<>();
			Users users = userService.getUserById(orderRequest.getUserId());
			orderRequest.getOrderItems().forEach(item -> {
				SanPham sanPham = sanPhamService.getSanPhamById(item.getProductId());
				if (item.getQuantity() > sanPham.getSoluong()) {
					throw new RuntimeException("Số lượng tồn kho của sản phẩm " + sanPham.getTenSanpham());
				}
			});

			HoaDon hoaDon = new HoaDon(users, new Date(), "ondelivery", orderRequest.getAddress(),
					orderRequest.getDeliveryMethod() == 1 ? "Giao hàng tiêu chuẩn" : "Giao hàng nhanh");
			hoaDonRepository.save(hoaDon);
			orderRequest.getOrderItems().forEach(item -> {
				SanPham sanPham = sanPhamService.getSanPhamById(item.getProductId());
				HoaDonChiTiet hoaDonChiTiet = new HoaDonChiTiet(
						new HoaDonChiTietId(hoaDon.getIdHoadon(), sanPham.getIdSanpham()), hoaDon, sanPham,
						item.getQuantity(), sanPham.getGia(), sanPham.getGiamgia());
				listHoaDonChiTiets.add(hoaDonChiTietRepository.save(hoaDonChiTiet));

				sanPham.setSoluong(sanPham.getSoluong() - item.getQuantity());
				sanPhamRepository.save(sanPham);

				gioHangChiTietService.delete(orderRequest.getCartId(), item.getProductId());

			});
			emailService.sendOrderConfirmationEmail(users.getIdUser(), "Thông tin đơn hàng #" + hoaDon.getIdHoadon(),
					listHoaDonChiTiets);
		} catch (Exception e) {
			e.printStackTrace();
			throw new IllegalArgumentException("Đã có lỗi xảy ra!");
		}

	}

	public Page<HoaDon> getAllHoaDonByIdUser(String email, int pageNumber, int limit) {
		PageRequest pageable = PageRequest.of(pageNumber, limit, Sort.by("ngaytao", "idHoadon").descending());
		return hoaDonRepository.findByUsers_idUser(email, pageable);
	}

	public HoaDon getHoaDonById(Integer id) {
		return hoaDonRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("Mã hóa đơn không tồn tại!"));
	}

	public void cancelOrder(Integer id) {
		HoaDon hoaDon = getHoaDonById(id);
		hoaDon.setTrangthai("cancel");
		hoaDonRepository.save(hoaDon);

		hoaDon.getHoaDonChiTiets().forEach(item -> {
			SanPham sanPham = sanPhamService.getSanPhamById(item.getId().getIdSanpham());
			sanPham.setSoluong(sanPham.getSoluong() + item.getSoluong());
			sanPhamRepository.save(sanPham);
		});
		// Rollback voucher usage if any
		try { voucherService.rollbackUsageOnCancel(hoaDon); } catch (Exception ignored) {}
	}

	public Page<HoaDon> getAllHoaDon(int pageNumber, int limit) {
		PageRequest pageable = PageRequest.of(pageNumber, limit, Sort.by("ngaytao", "idHoadon").descending());
		return hoaDonRepository.findAll(pageable);
	}

	public String updateHoadon(Integer id, String action) throws IllegalArgumentException {
		HoaDon hoaDon = getHoaDonById(id);
		if ("CONFIRM".equals(action)) {
			hoaDon.setTrangthai("received");
			hoaDonRepository.save(hoaDon);
			return "Đã xác nhận đã giao đơn hàng #" + id + " thành công!";
		} else if ("CANCEL".equals(action)) {
			hoaDon.setTrangthai("cancel");
			hoaDonRepository.save(hoaDon);

			hoaDon.getHoaDonChiTiets().forEach(item -> {
				SanPham sanPham = sanPhamService.getSanPhamById(item.getId().getIdSanpham());
				sanPham.setSoluong(sanPham.getSoluong() + item.getSoluong());
				sanPhamRepository.save(sanPham);
			});
			// Rollback voucher usage if any
			try { voucherService.rollbackUsageOnCancel(hoaDon); } catch (Exception ignored) {}
			return "Đã hủy đơn hàng #" + id + " thành công!";
		} else {
			if (hoaDon.getTrangthai().equals("cancel")) {
				hoaDon.getHoaDonChiTiets().forEach(item -> {
					SanPham sanPham = sanPhamService.getSanPhamById(item.getId().getIdSanpham());
					sanPham.setSoluong(sanPham.getSoluong() - item.getSoluong());
					sanPhamRepository.save(sanPham);
				});
			}
			hoaDon.setTrangthai("ondelivery");
			hoaDonRepository.save(hoaDon);
			return "Đã đặt lại trạng thái là đang giao hàng cho đơn hàng #" + id + " thành công!";
		}
	}

	public long countReceivedOrders() {
		return hoaDonRepository.countReceivedOrders();
	}

	public ReportRevenueStatistics thongKeDoanhThuTheoLoai(Integer loaiId) {
		return hoaDonRepository.thongKeDoanhThuTheoLoai(loaiId);
	}
}
