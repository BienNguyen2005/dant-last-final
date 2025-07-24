package com.poly.controller.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.poly.entity.ReportKhachHangVip;
import com.poly.entity.ReportRevenueStatistics;
import com.poly.entity.Users;
import com.poly.service.HoaDonService;
import com.poly.service.LoaiService;
import com.poly.service.SanPhamService;
import com.poly.service.UserService;

import jakarta.servlet.http.HttpSession;

@Controller
public class AdminController {
	@Autowired
	UserService userService;
	@Autowired
	LoaiService loaiService;
	@Autowired
	SanPhamService sanPhamService;
	@Autowired
	HoaDonService hoaDonService;
	@Autowired
	private HttpSession session;

	@GetMapping("/admin")
	public String home(@RequestParam(defaultValue = "0", name = "idLoai") int idLoai, Model model) {
		Users currentUser = (Users) session.getAttribute("currentUser");
		if (currentUser == null || !currentUser.isVaitro()) {
			return "redirect:/signin";
		}
		ReportRevenueStatistics reportRevenueStatistics = hoaDonService.thongKeDoanhThuTheoLoai(idLoai);
		if (reportRevenueStatistics.getTongDoanhThu() != 0L) {
			model.addAttribute("reportRevenueStatistics", reportRevenueStatistics);
		}

		Page<ReportKhachHangVip> reportKhachHangVip = userService.getTop10KhachHangVip();
		if (reportKhachHangVip.getTotalElements() != 0) {
			model.addAttribute("reportKhachHangVip", reportKhachHangVip.getContent());
		}
		model.addAttribute("idLoai", idLoai);
		model.addAttribute("loais", loaiService.getAllLoai(0, 1000));
		return "admin/home";
	}

	@GetMapping("/admin/user")
	public String userManager(Model model, @RequestParam(defaultValue = "0", name = "page") int page) {
		Users currentUser = (Users) session.getAttribute("currentUser");
		if (currentUser == null || !currentUser.isVaitro()) {
			return "redirect:/signin";
		}

		Page<Users> userPage = userService.getAllUsers(page, 8);

		model.addAttribute("users", userPage.getContent()); // Danh sách user
		model.addAttribute("currentPage", page); // Trang hiện tại
		model.addAttribute("totalPages", userPage.getTotalPages());
		return "admin/user/userManager";
	}

	@GetMapping("/admin/user/create")
	public String userCreate(Model model, @ModelAttribute("user") Users user) {
		Users currentUser = (Users) session.getAttribute("currentUser");
		if (currentUser == null || !currentUser.isVaitro()) {
			return "redirect:/signin";
		}
		return "admin/user/createUser";
	}

	@PostMapping("/admin/user/create")
	public String userInsert(Model model, @ModelAttribute("user") Users user) {
		try {
			Users currentUser = (Users) session.getAttribute("currentUser");
			if (currentUser == null || !currentUser.isVaitro()) {
				return "redirect:/signin";
			}
			userService.create(user);
			model.addAttribute("successMessage", "Tạo tài khoản thành công");
		} catch (Exception e) {
			model.addAttribute("errorMessage", e.getMessage());
		}
		return "admin/user/createUser";
	}

	@GetMapping("/admin/user/edit/{id}")
	public String showUpdateForm(@PathVariable("id") String id, Model model) {
		Users currentUser = (Users) session.getAttribute("currentUser");
		if (currentUser == null || !currentUser.isVaitro()) {
			return "redirect:/signin";
		}
		try {
			Users user = userService.getUserById(id);
			model.addAttribute("user", user);
		} catch (Exception e) {
			model.addAttribute("errorMessage", e.getMessage());
		}
		return "admin/user/updateUser";
	}

	@PostMapping("/admin/user/update/{id}")
	public String updateUser(Model model, @PathVariable("id") String id, @ModelAttribute("user") Users updatedUser) {
		try {
			Users currentUser = (Users) session.getAttribute("currentUser");
			if (currentUser == null || !currentUser.isVaitro()) {
				return "redirect:/signin";
			}
			userService.updateUser(id, updatedUser);
			model.addAttribute("successMessage", "Cập nhật tài khoản thành công");
		} catch (Exception e) {
			model.addAttribute("errorMessage", e.getMessage());
		}
		return "admin/user/updateUser";
	}

	@GetMapping("/admin/user/delete/{id}")
	public String deleteUser(Model model, @PathVariable("id") String id, RedirectAttributes redirectAttributes) {
		try {
			userService.deleteUser(id);
			redirectAttributes.addFlashAttribute("successMessage", "Xóa tài khoản thành công");
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
		}
		return "redirect:/admin/user";
	}

}
