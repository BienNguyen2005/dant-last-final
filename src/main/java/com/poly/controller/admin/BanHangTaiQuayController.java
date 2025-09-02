package com.poly.controller.admin;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.poly.entity.GioHang;
import com.poly.entity.GioHangChiTiet;
import com.poly.entity.GioHangChiTietId;
import com.poly.entity.HoaDon;
import com.poly.entity.HoaDonChiTiet;
import com.poly.entity.HoaDonChiTietId;
import com.poly.entity.Loai;
import com.poly.entity.SanPham;
import com.poly.entity.Users;
import com.poly.entity.KhachHang;
import com.poly.repository.GioHangChiTietRepository;
import com.poly.repository.GioHangRepository;
import com.poly.repository.HoaDonChiTietRepository;
import com.poly.repository.HoaDonRepository;
import com.poly.repository.SanPhamRepository;
import com.poly.service.LoaiService;
import com.poly.service.SanPhamService;
import com.poly.service.UserService;
import com.poly.service.KhachHangService;
import com.poly.service.VoucherService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import com.poly.service.CurrentUserService;
import vn.payos.PayOS;
import vn.payos.type.CheckoutResponseData;
import vn.payos.type.ItemData;
import vn.payos.type.PaymentData;
import org.springframework.http.ResponseEntity;

@Controller
public class BanHangTaiQuayController {
	@Autowired
	private SanPhamService sanPhamService;

	@Autowired
	private LoaiService loaiService;

	@Autowired
	UserService userService;

	@Autowired
	CurrentUserService currentUserService;

	@Autowired
	private PayOS payOS;

	@Autowired
	private GioHangRepository gioHangRepository;

	@Autowired
	private GioHangChiTietRepository gioHangChiTietRepository;

	@Autowired
	private SanPhamRepository sanPhamRepository;

	@Autowired
	private HoaDonRepository hoaDonRepository;
	@Autowired
	private HoaDonChiTietRepository hoaDonChiTietRepository;

	@Autowired
	private KhachHangService khachHangService;

	@Autowired
	private VoucherService voucherService;

	@GetMapping("/banhangtaiquay")
	public String banHangTaiQuay(@RequestParam(name = "page", defaultValue = "0") int page,
			@RequestParam(name = "keyword", required = false) String keyword, HttpSession session,
			@RequestParam(name = "idLoai", required = false) Integer idLoai, Model model, HttpServletRequest request) {
		Users currentUser = currentUserService.getCurrentUser().orElse(null);
		if (currentUser == null) return "redirect:/signin";

		Page<SanPham> dsSanPham;

		if ((keyword != null && !keyword.isEmpty()) && idLoai != null) {
			dsSanPham = sanPhamService.searchByTenAndLoai(page, 9, keyword, idLoai);
		} else if (keyword != null && !keyword.isEmpty()) {
			dsSanPham = sanPhamService.searchByName(page, 9, keyword);
		} else if (idLoai != null) {
			dsSanPham = sanPhamService.getSanPhamByIdLoai(idLoai, page, 9);
		} else {
			dsSanPham = sanPhamService.getAllSanPham(page, 9);
		}

		List<Loai> dsLoai = loaiService.getAllLoai(0, 100).getContent();
		GioHang gioHang = gioHangRepository.findByUsers_IdUser(currentUser.getIdUser());
		List<GioHangChiTiet> gioHangChiTietList = gioHang.getGioHangChiTiets();
		int tongCong = gioHangChiTietList.stream().mapToInt(ct -> {
			int giaSauGiam = ct.getSanPham().getGia() * (100 - ct.getSanPham().getGiamgia()) / 100;
			return giaSauGiam * ct.getSoluong();
		}).sum();

		model.addAttribute("tongCong", tongCong);
		model.addAttribute("dsSanPham", dsSanPham);
		model.addAttribute("dsLoai", dsLoai);
		model.addAttribute("keyword", keyword);
		model.addAttribute("idLoai", idLoai);
		model.addAttribute("gioHang", gioHangChiTietList);
		model.addAttribute("khachHangs", khachHangService.getAllKhachHang());
		return "admin/banhangtaiquay/banhangtaiquay";
	}

	@PostMapping("giohang/them")
	public ResponseEntity<?> themVaoGio(@RequestParam("idSanpham") Integer idSanpham, HttpSession session,
			@RequestParam("soluong") Integer soluong, RedirectAttributes redirectAttributes,
			HttpServletRequest request) {
		Users currentUser = currentUserService.getCurrentUser().orElse(null);
		if (currentUser == null) {
			if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
				return ResponseEntity.status(401).body("{\"success\":false, \"message\":\"Chưa đăng nhập\"}");
			}
			return ResponseEntity.status(302).header("Location", "/signin").build();
		}

		GioHang gioHang = gioHangRepository.findByUsers_IdUser(currentUser.getIdUser());
		SanPham sanPham = sanPhamRepository.findById(idSanpham)
				.orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm"));

		GioHangChiTietId chiTietId = new GioHangChiTietId(gioHang.getIdGiohang(), sanPham.getIdSanpham());
		Optional<GioHangChiTiet> opt = gioHangChiTietRepository.findById(chiTietId);
		GioHangChiTiet chiTiet;
		if (opt.isPresent()) {
			chiTiet = opt.get();
			chiTiet.setSoluong(chiTiet.getSoluong() + soluong);
		} else {
			chiTiet = new GioHangChiTiet(chiTietId, gioHang, sanPham, soluong);
		}
		gioHangChiTietRepository.save(chiTiet);

		if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
			return ResponseEntity.ok("{\"success\":true}");
		}

		redirectAttributes.addFlashAttribute("successMessage", "Đã thêm vào giỏ hàng!");
		String referer = request.getHeader("referer");
		return ResponseEntity.status(302).header("Location", referer != null ? referer : "/banhangtaiquay").build();
	}

	@PostMapping("/giohang/xoa")
	public String xoaSanPhamKhoiGioHang(@RequestParam("idSanpham") Integer idSanpham, HttpSession session,
			HttpServletRequest request) {
		Users currentUser = currentUserService.getCurrentUser().orElse(null);
		if (currentUser == null) return "redirect:/signin";

		GioHang gioHang = gioHangRepository.findByUsers_IdUser(currentUser.getIdUser());
		if (gioHang != null) {
			GioHangChiTietId chiTietId = new GioHangChiTietId();
			chiTietId.setIdGiohang(gioHang.getIdGiohang());
			chiTietId.setIdSanpham(idSanpham);

			gioHangChiTietRepository.deleteById(chiTietId);
		}

		String referer = request.getHeader("referer");
		return "redirect:" + (referer != null ? referer : "/banhangtaiquay");
	}

	@PostMapping("/banhangtaiquay/thanh-toan")
	public String thanhToan(@RequestParam("phuongthuc") String phuongThuc,
					   @RequestParam(value = "khachHangId", required = false) String khachHangId,
					   @RequestParam(value = "adminDiscount", required = false) Integer adminDiscount,
					   @RequestParam(value = "voucherCode", required = false) String voucherCode,
					   @RequestParam(value = "voucherDiscount", required = false) Double voucherDiscountClient,
					   HttpSession session,
					   RedirectAttributes redirectAttributes,
					   HttpServletRequest request) {
		Users currentUser = currentUserService.getCurrentUser().orElse(null);
		if (currentUser == null) return "redirect:/signin";
		GioHang gioHang = gioHangRepository.findByUsers_IdUser(currentUser.getIdUser());
		if (gioHang == null) {
			redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy giỏ hàng!");
			return "redirect:/banhangtaiquay";
		}
		List<GioHangChiTiet> chiTietList = gioHang.getGioHangChiTiets();
		if (chiTietList.isEmpty()) {
			redirectAttributes.addFlashAttribute("errorMessage", "Giỏ hàng đang trống!");
			return "redirect:/banhangtaiquay";
		}
		KhachHang khachHang;
		if (khachHangId == null || khachHangId.isEmpty()) {
			Optional<KhachHang> guessOpt = khachHangService.findBySdt("guess");
			if (guessOpt.isEmpty()) {
				KhachHang guess = new KhachHang();
				guess.setHoten("Khách vãng lai");
				guess.setSdt("guess");
				guess.setTrangThai(true);
				guess.setPhanLoai("Khách vãng lai");
				khachHang = khachHangService.saveKhachHang(guess);
			} else {
				khachHang = guessOpt.get();
			}
		} else {
			khachHang = khachHangService.getKhachHangById(Long.valueOf(khachHangId)).orElse(null);
		}

		// TÍNH GIẢM GIÁ
		int totalQuantity = chiTietList.stream().mapToInt(GioHangChiTiet::getSoluong).sum();
		int tongTienTruocGiam = chiTietList.stream().mapToInt(ct -> {
			int giaSauGiam = ct.getSanPham().getGia() * (100 - ct.getSanPham().getGiamgia()) / 100;
			return giaSauGiam * ct.getSoluong();
		}).sum();
		int discountPercent = 0;
		String loaiKh = khachHang.getPhanLoai() != null ? khachHang.getPhanLoai().toLowerCase() : "";
		if (adminDiscount != null && adminDiscount > 0) {
			discountPercent = adminDiscount;
		} else if (loaiKh.contains("vip")) {
			discountPercent = 30;
		} else if (loaiKh.contains("thường") && totalQuantity > 3) {
			discountPercent = 10;
		}
		int tongGiamAdmin = tongTienTruocGiam * discountPercent / 100;
		double voucherDiscount = 0d;
		if (voucherCode != null && !voucherCode.isBlank()) {
			try {
				Map<String, Object> validation = voucherService.validate(voucherCode, tongTienTruocGiam, currentUser.getIdUser());
				Object da = validation.get("discountAmount");
				if (da instanceof Number) voucherDiscount = ((Number) da).doubleValue();
			} catch (IllegalArgumentException e) {
				redirectAttributes.addFlashAttribute("errorMessage", "Voucher không hợp lệ: " + e.getMessage());
				return "redirect:/banhangtaiquay";
			}
		}
		double tongGiam = tongGiamAdmin + voucherDiscount;
		int tongTienSauGiam = (int) Math.max(0, Math.round(tongTienTruocGiam - tongGiam));

		if ("BANK".equals(phuongThuc)) {
			String currentTimeString = String.valueOf(new Date().getTime());
			long orderCode = Long.parseLong(currentTimeString.substring(currentTimeString.length() - 6));

			String baseUrl = request.getScheme() + "://" + request.getServerName()
					+ (request.getServerPort() == 80 ? "" : ":" + request.getServerPort()) + request.getContextPath();

			try {
				List<ItemData> items = chiTietList.stream()
						.map(ct -> ItemData.builder().name(ct.getSanPham().getTenSanpham()).quantity(ct.getSoluong())
								.price(ct.getSanPham().getGia() * (100 - ct.getSanPham().getGiamgia()) / 100).build())
						.toList();
				System.out.println(baseUrl + "/banhangtaiquay/success");
				PaymentData paymentData = PaymentData.builder().orderCode(orderCode).amount(tongTienSauGiam)
						.description("Thanh toán đơn hàng")
						.returnUrl(baseUrl + "/banhangtaiquay/success?idUser=" + currentUser.getIdUser())
						.cancelUrl(baseUrl + "/banhangtaiquay").items(items).build();

				CheckoutResponseData data = payOS.createPaymentLink(paymentData);

				return "redirect:" + data.getCheckoutUrl();
			} catch (Exception e) {
				e.printStackTrace();
				return "redirect:/banhangtaiquay";
			}
		}
		else {
			HoaDon hoaDon = new HoaDon();
			hoaDon.setUsers(currentUser);
			hoaDon.setNgaytao(new Date());
			hoaDon.setDiachi("");
			hoaDon.setGiaohang("");
			hoaDon.setTrangthai(phuongThuc.equals("CASH") ? "ondelivery" : "received");
			hoaDon.setDiscountPercent(discountPercent);
			hoaDon.setDiscountAmount(tongGiamAdmin);
			// Set voucher info if applicable
			if (voucherCode != null && !voucherCode.isBlank() && voucherDiscount > 0) {
				hoaDon.setVoucherCode(voucherCode.trim());
				hoaDon.setVoucherDiscountAmount(voucherDiscount);
			}
			hoaDonRepository.save(hoaDon);

			for (GioHangChiTiet ct : chiTietList) {
				SanPham sp = ct.getSanPham();
				int soLuongMua = ct.getSoluong();

				if (sp.getSoluong() < soLuongMua) {
					redirectAttributes.addFlashAttribute("errorMessage",
							"Sản phẩm " + sp.getTenSanpham() + " không đủ tồn kho!");
					return "redirect:/banhangtaiquay";
				}

				HoaDonChiTiet cthd = new HoaDonChiTiet();
				cthd.setId(new HoaDonChiTietId(hoaDon.getIdHoadon(), sp.getIdSanpham()));
				cthd.setHoaDon(hoaDon);
				cthd.setSanPham(sp);
				cthd.setGiamgia(sp.getGiamgia());
				cthd.setGia(sp.getGia());
				cthd.setSoluong(soLuongMua);
				hoaDonChiTietRepository.save(cthd);

				sp.setSoluong(sp.getSoluong() - soLuongMua);
				sanPhamRepository.save(sp);
			}

			gioHangChiTietRepository.deleteAll(chiTietList);
			// Increase voucher usage after order persistence
			if (voucherCode != null && !voucherCode.isBlank()) {
				try {
					voucherService.increaseUsageOnOrder(hoaDon, voucherCode, tongTienTruocGiam, currentUser.getIdUser());
				} catch (IllegalArgumentException ex) {
					// Race condition / invalid voucher at finalize time; ignore or log.
				}
			}

			String msg = "Thanh toán thành công! Tổng tiền: " + tongTienSauGiam + "₫ (Giảm admin: " + discountPercent + "%;";
			if (voucherDiscount > 0) msg += " Voucher: -" + ((long) voucherDiscount) + "₫)"; else msg += ")";
			redirectAttributes.addFlashAttribute("successMessage", msg);
			redirectAttributes.addFlashAttribute("idHoaDon", hoaDon.getIdHoadon());
			return "redirect:/banhangtaiquay";
		}
	}

	@GetMapping("/banhangtaiquay/success/**")
	public String success(@RequestParam("idUser") String idUser, RedirectAttributes redirectAttributes) {
		try {

			GioHang gioHang = gioHangRepository.findByUsers_IdUser(idUser);
			if (gioHang == null) {
				redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy giỏ hàng!");
				return "redirect:/banhangtaiquay";
			}
			List<GioHangChiTiet> chiTietList = gioHang.getGioHangChiTiets();

			if (chiTietList.isEmpty()) {
				redirectAttributes.addFlashAttribute("errorMessage", "Giỏ hàng đang trống!");
				return "redirect:/banhangtaiquay";
			}

			HoaDon hoaDon = new HoaDon();
			hoaDon.setUsers(userService.getUserById(idUser));
			hoaDon.setNgaytao(new Date());
			hoaDon.setDiachi("");
			hoaDon.setGiaohang("");
			hoaDon.setTrangthai("received");
			hoaDonRepository.save(hoaDon);

			for (GioHangChiTiet ct : gioHang.getGioHangChiTiets()) {
				SanPham sp = ct.getSanPham();
				int soLuongMua = ct.getSoluong();

				if (sp.getSoluong() < soLuongMua) {
					throw new RuntimeException("Sản phẩm " + sp.getTenSanpham() + " không đủ tồn kho");
				}

				HoaDonChiTiet cthd = new HoaDonChiTiet();
				cthd.setId(new HoaDonChiTietId(hoaDon.getIdHoadon(), sp.getIdSanpham()));
				cthd.setHoaDon(hoaDon);
				cthd.setSanPham(sp);
				cthd.setGiamgia(sp.getGiamgia());
				cthd.setGia(sp.getGia());
				cthd.setSoluong(soLuongMua);
				hoaDonChiTietRepository.save(cthd);

				sp.setSoluong(sp.getSoluong() - soLuongMua);
				sanPhamRepository.save(sp);
			}

			gioHangChiTietRepository.deleteAll(gioHang.getGioHangChiTiets());

		} catch (Exception e) {
			e.printStackTrace();
		}
		return "redirect:/banhangtaiquay";
	}

	@GetMapping("/banhangtaiquay/cart-fragment")
	public String getCartFragment(HttpSession session, Model model, HttpServletRequest request, HttpServletResponse response) {
		Users currentUser = currentUserService.getCurrentUser().orElse(null);
		if (currentUser == null) {
			if ("XMLHttpRequest".equalsIgnoreCase(request.getHeader("X-Requested-With"))) {
				response.setStatus(401);
				model.addAttribute("tongCong", 0);
				model.addAttribute("gioHang", List.of());
				return "admin/banhangtaiquay/cart-fragment :: cartArea";
			}
			return "redirect:/signin";
		}
		GioHang gioHang = gioHangRepository.findByUsers_IdUser(currentUser.getIdUser());
    
		// Create cart if it doesn't exist
		if (gioHang == null) {
			gioHang = new GioHang();
			gioHang.setUsers(currentUser);
			gioHang = gioHangRepository.save(gioHang);
		}
		List<GioHangChiTiet> gioHangChiTietList = gioHang.getGioHangChiTiets();
		int tongCong = gioHangChiTietList.stream().mapToInt(ct -> {
			int giaSauGiam = ct.getSanPham().getGia() * (100 - ct.getSanPham().getGiamgia()) / 100;
			return giaSauGiam * ct.getSoluong();
		}).sum();
		model.addAttribute("tongCong", tongCong);
		model.addAttribute("gioHang", gioHangChiTietList);
		return "admin/banhangtaiquay/cart-fragment :: cartArea";
	}

}
