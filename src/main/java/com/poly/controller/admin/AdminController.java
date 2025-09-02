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

import org.springframework.security.access.prepost.PreAuthorize;

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

	@GetMapping("/admin")
	@PreAuthorize("hasRole('ADMIN')")
	public String home(@RequestParam(defaultValue = "0", name = "idLoai") int idLoai, Model model) {
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
	@PreAuthorize("hasRole('ADMIN')")
	public String userManager(Model model, @RequestParam(defaultValue = "0", name = "page") int page) {

		Page<Users> userPage = userService.getAllUsers(page, 8);

		model.addAttribute("users", userPage.getContent()); // Danh sách user
		model.addAttribute("currentPage", page); // Trang hiện tại
		model.addAttribute("totalPages", userPage.getTotalPages());
		return "admin/user/userManager";
	}

	@GetMapping("/admin/user/create")
	@PreAuthorize("hasRole('ADMIN')")
	public String userCreate(Model model, @ModelAttribute("user") Users user) {
		return "admin/user/createUser";
	}

	@PostMapping("/admin/user/create")
	@PreAuthorize("hasRole('ADMIN')")
	public String userInsert(Model model, @ModelAttribute("user") Users user) {
		try {
			userService.create(user);
			model.addAttribute("successMessage", "Tạo tài khoản thành công");
		} catch (Exception e) {
			model.addAttribute("errorMessage", e.getMessage());
		}
		return "admin/user/createUser";
	}

	@GetMapping("/admin/user/edit/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	public String showUpdateForm(@PathVariable("id") String id, Model model) {
		try {
			Users user = userService.getUserById(id);
			model.addAttribute("user", user);
		} catch (Exception e) {
			model.addAttribute("errorMessage", e.getMessage());
		}
		return "admin/user/updateUser";
	}

	@PostMapping("/admin/user/update/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	public String updateUser(Model model, @PathVariable("id") String id, @ModelAttribute("user") Users updatedUser) {
		try {
			userService.updateUser(id, updatedUser);
			model.addAttribute("successMessage", "Cập nhật tài khoản thành công");
		} catch (Exception e) {
			model.addAttribute("errorMessage", e.getMessage());
		}
		return "admin/user/updateUser";
	}

	@GetMapping("/admin/user/delete/{id}")
	@PreAuthorize("hasRole('ADMIN')")
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
