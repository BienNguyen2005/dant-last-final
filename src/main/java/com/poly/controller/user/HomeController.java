package com.poly.controller.user;

import java.io.File;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.poly.entity.HoaDon;
import com.poly.entity.SanPham;
import com.poly.entity.Users;
import com.poly.service.HoaDonService;
import com.poly.service.LoaiService;
import com.poly.service.SanPhamService;
import com.poly.service.UserService;

import jakarta.servlet.http.HttpSession;

@Controller
public class HomeController {
	@Autowired
	UserService userService;
	@Autowired
	LoaiService loaiService;
	@Autowired
	SanPhamService sanPhamService;
	@Autowired
	HttpSession session;
	@Autowired
	HoaDonService hoaDonService;

	@GetMapping("/")
	public String home(Model model) {
//		model.addAttribute("loais", loaiService.getAllLoai(0, 5));
//		model.addAttribute("sanphams", sanPhamService.getAllSanPham(0, 12));
//		model.addAttribute("listSPGiamGia", sanPhamService.getAllSanPhamGiamGia(0, 12));
		return "redirect:/signin";
	}

	@GetMapping("/loai-all")
	public String loaiAll(Model model) {
		model.addAttribute("loais", loaiService.getAllLoai(0, 100));
		return "user/categoryAll";
	}

	@GetMapping("/signin")
	public String signin(@ModelAttribute("user") Users user, Model model) {
		Users currentUser = (Users) session.getAttribute("currentUser");
		if (currentUser != null) {
			return "redirect:/banhangtaiquay";
		}
		model.addAttribute("loais", loaiService.getAllLoai(0, 5));
		return "user/signin";
	}

	@PostMapping("/signin")
	public String login(@ModelAttribute("user") Users user, Model model) {
		try {
			userService.login(user);
			return "redirect:/banhangtaiquay";
		} catch (Exception e) {
			model.addAttribute("loais", loaiService.getAllLoai(0, 5));
			model.addAttribute("errorMessage", e.getMessage());
			return "user/signin";
		}
	}

	@GetMapping("/signup")
	public String signup(@ModelAttribute("user") Users user, Model model) {
		Users currentUser = (Users) session.getAttribute("currentUser");
		if (currentUser != null) {
			return "redirect:/";
		}
		model.addAttribute("loais", loaiService.getAllLoai(0, 5));
		return "user/signup";
	}

	@GetMapping("/signout")
	public String signout() {
		userService.logout();
		return "redirect:/";
	}

	@PostMapping("/signup")
	public String regiter(@ModelAttribute("user") Users user, Model model) {
		try {
			userService.register(user);
			model.addAttribute("successMessage", "Tạo tài khoản thành công, vui lòng mở mail để kích hoạt tài khoản");
		} catch (Exception e) {
			model.addAttribute("errorMessage", e.getMessage());
		}
		model.addAttribute("loais", loaiService.getAllLoai(0, 5));
		return "user/signup";
	}

	@GetMapping("/active-account")
	public String resetPassword(@RequestParam(required = true, name = "token") String token,
			RedirectAttributes redirectAttributes) {
		try {
			userService.checkToken(token);
			redirectAttributes.addFlashAttribute("successMessage", "Tài khoản bạn đã được kích hoạt");
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("errorMessage", "Token không hợp lệ hoặc đã hết hạn");
		}
		redirectAttributes.addFlashAttribute("loais", loaiService.getAllLoai(0, 5));
		return "redirect:/signin";
	}

	@ResponseBody
	@GetMapping("/image/{filename:.+}")
	public ResponseEntity<Object> downloadFile(@PathVariable(name = "filename") String filename) {
		File file = new File("c:/var/java5/images/" + filename);
		if (!file.exists()) {
			throw new RuntimeException("File không tồn tại!");
		}

		UrlResource resource;
		try {
			resource = new UrlResource(file.toURI());
		} catch (MalformedURLException ex) {
			throw new RuntimeException("File không tồn tại!");
		}

		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getName() + "\"")
				.body(resource);
	}

	@GetMapping("/sanpham")
	public String sanpham(@RequestParam(name = "id") String id, Model model) {
		try {
			SanPham sanPham = sanPhamService.getSanPhamById(Integer.valueOf(id));
			model.addAttribute("sanpham", sanPham);
			model.addAttribute("sanphams", sanPhamService.getSanPhamByIdLoai(sanPham.getLoai().getIdLoai(), 0, 4)
					.filter(item -> item.getIdSanpham() != sanPham.getIdSanpham()));
			model.addAttribute("loais", loaiService.getAllLoai(0, 5));
			model.addAttribute("luotMua", sanPhamService.getLuotMuaById(sanPham.getIdSanpham()));
			return "user/productDetail";
		} catch (Exception e) {
			return "redirect:/";
		}
	}

	@GetMapping("/order")
	public String order(@RequestParam(defaultValue = "0", name = "page") int page, Model model) {
		Users currentUser = (Users) session.getAttribute("currentUser");
		if (currentUser == null) {
			return "redirect:/";
		}
		Page<HoaDon> hoaDonPage = hoaDonService.getAllHoaDonByIdUser(currentUser.getIdUser(), page, 4);
		model.addAttribute("loais", loaiService.getAllLoai(0, 5));
		model.addAttribute("orders", hoaDonPage.getContent()); // Danh sách user
		model.addAttribute("currentPage", page); // Trang hiện tại
		model.addAttribute("totalPages", hoaDonPage.getTotalPages());
		return "user/orderView";
	}

	@GetMapping("/orderDetail")
	public String orderDetail(@RequestParam(name = "id") String id, Model model) {
		try {
			Users currentUser = (Users) session.getAttribute("currentUser");
			if (currentUser == null) {
				return "redirect:/";
			}
			AtomicReference<Double> tempPrice = new AtomicReference<>(0.0);
			List<SanPham> listSanPham = new ArrayList<>();
			HoaDon hoaDon = hoaDonService.getHoaDonById(Integer.valueOf(id));
			hoaDon.getHoaDonChiTiets().forEach(item -> {
				SanPham sanPham = sanPhamService.getSanPhamById(item.getId().getIdSanpham());
				double price = (item.getGiamgia() == 0) ? item.getGia() * item.getSoluong()
						: (item.getGia() * (100 - item.getGiamgia()) / 100) * item.getSoluong();
				tempPrice.updateAndGet(v -> v + price);
				sanPham.setSoluong(item.getSoluong());
				sanPham.setGia(item.getGia());
				sanPham.setGiamgia(item.getGiamgia());
				listSanPham.add(sanPham);
			});
			model.addAttribute("loais", loaiService.getAllLoai(0, 5));
			model.addAttribute("tempPrice", tempPrice.get());
			model.addAttribute("listSanPham", listSanPham);
			model.addAttribute("deliveryPrice", hoaDon.getGiaohang().equals("Giao hàng nhanh") ? 50000 : 20000);
			model.addAttribute("order", hoaDon);
			return "user/orderDetailView";
		} catch (Exception e) {
			return "redirect:/";
		}
	}

	@PostMapping("/orderDetail")
	public String huyOrderDetail(@RequestParam(name = "id") String id, Model model) {
		try {
			Users currentUser = (Users) session.getAttribute("currentUser");
			if (currentUser == null) {
				return "redirect:/";
			}
			hoaDonService.cancelOrder(Integer.valueOf(id));
			return "redirect:/order";
		} catch (Exception e) {
			return "redirect:/";
		}
	}

	@GetMapping("/setting")
	public String setting(Model model) {
		Users currentUser = (Users) session.getAttribute("currentUser");
		if (currentUser == null) {
			return "redirect:/";
		}
		model.addAttribute("user", userService.getUserById(currentUser.getIdUser()));
		model.addAttribute("loais", loaiService.getAllLoai(0, 5));
		return "user/settingView";
	}

	@PostMapping("/setting")
	public String setting(@RequestParam(name = "image", required = false) MultipartFile image,
			@ModelAttribute("user") Users updateUser, Model model) {
		Users currentUser = (Users) session.getAttribute("currentUser");
		try {
			if (currentUser == null) {
				return "redirect:/";
			}
			model.addAttribute("user", userService.updateProfile(currentUser.getIdUser(), updateUser, image));
			model.addAttribute("loais", loaiService.getAllLoai(0, 5));
			model.addAttribute("successMessage", "Cập nhật tài khoản thành công");
		} catch (Exception e) {
			model.addAttribute("errorMessage", e.getMessage());
		}
		return "user/settingView";
	}

	@GetMapping("/changePassword")
	public String changePassword(Model model) {
		Users currentUser = (Users) session.getAttribute("currentUser");
		if (currentUser == null) {
			return "redirect:/";
		}
		model.addAttribute("loais", loaiService.getAllLoai(0, 5));
		return "user/changePasswordView";
	}

	@PostMapping("/changePassword")
	public String changePassword(@RequestParam(name = "currentPassword") String currentPassword,
			@RequestParam(name = "newPassword") String newPassword,
			@RequestParam(name = "newPasswordAgain") String newPasswordAgain, Model model) {
		Users currentUser = (Users) session.getAttribute("currentUser");
		if (currentUser == null) {
			return "redirect:/";
		}
		
		boolean newPasswordEqualsNewPasswordAgain = newPassword.equals(newPasswordAgain);
		if (!newPasswordEqualsNewPasswordAgain) {
			model.addAttribute("currentPassword", currentPassword);
			model.addAttribute("newPassword", newPassword);
			model.addAttribute("newPasswordAgain", newPasswordAgain);
			model.addAttribute("errorMessage", "Mật khẩu mới và mật khẩu nhập lại không khớp!");
			model.addAttribute("loais", loaiService.getAllLoai(0, 5));
			return "user/changePasswordView";
		}
		
		try {
			userService.changePassword(currentUser.getIdUser(), currentPassword, newPassword);
			model.addAttribute("successMessage", "Đổi mật khẩu thành công!");
		} catch (IllegalArgumentException e) {
			model.addAttribute("currentPassword", currentPassword);
			model.addAttribute("newPassword", newPassword);
			model.addAttribute("newPasswordAgain", newPasswordAgain);
			model.addAttribute("errorMessage", e.getMessage());
		} catch (Exception e) {
			model.addAttribute("currentPassword", currentPassword);
			model.addAttribute("newPassword", newPassword);
			model.addAttribute("newPasswordAgain", newPasswordAgain);
			model.addAttribute("errorMessage", "Lỗi không xác định: " + e.getMessage());
		}
		
		model.addAttribute("loais", loaiService.getAllLoai(0, 5));
		return "user/changePasswordView";
	}

	@GetMapping("/forgot-password")
	public String forgotPassword(@ModelAttribute("user") Users user, Model model) {
		model.addAttribute("loais", loaiService.getAllLoai(0, 5));
		return "user/forgotPassword";
	}

	@PostMapping("/forgot-password")
	public String postForgotPassword(@RequestParam(name = "idUser") String idUser, Model model) {
		try {
			userService.sendMailPass(idUser);
			model.addAttribute("successMessage", "Gửi mail thành công");
		} catch (Exception e) {
			model.addAttribute("errorMessage", e.getMessage());
		}
		model.addAttribute("idUser", idUser);
		model.addAttribute("loais", loaiService.getAllLoai(0, 5));
		return "user/forgotPassword";
	}

	@PostMapping("/cance-account")
	public String canceAccount(@RequestParam(name = "idUser") String idUser, Model model) {
		Users currentUser = (Users) session.getAttribute("currentUser");
		if (currentUser == null) {
			return "redirect:/";
		}

		try {
			userService.canceAccount(idUser);
		} catch (Exception e) {
			model.addAttribute("errorMessage", e.getMessage());
			return "user/settingView";
		}
		return "redirect:/signin";
	}

	@GetMapping("/history")
	public String history(@RequestParam(defaultValue = "0", name = "page") int page, Model model) {
		Users currentUser = (Users) session.getAttribute("currentUser");
		if (currentUser == null) {
			return "redirect:/";
		}
		Page<SanPham> sanphamPage = sanPhamService.findSanPhamByUser(currentUser.getIdUser(), page, 4);
		model.addAttribute("sanphams", sanphamPage.getContent());
		model.addAttribute("loais", loaiService.getAllLoai(0, 5));
		model.addAttribute("currentPage", page); // Trang hiện tại
		model.addAttribute("totalPages", sanphamPage.getTotalPages());
		return "user/history";
	}
}
