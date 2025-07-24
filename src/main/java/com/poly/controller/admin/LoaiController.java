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

import com.poly.entity.Loai;
import com.poly.entity.Users;
import com.poly.service.LoaiService;

import jakarta.servlet.http.HttpSession;

@Controller
public class LoaiController {
	@Autowired
	LoaiService loaiService;
	@Autowired
	private HttpSession session;

	@GetMapping("/admin/loai")
	public String loaiManager(Model model, @RequestParam(defaultValue = "0", name = "page") int page) {
		Users currentUser = (Users) session.getAttribute("currentUser");
		if (currentUser == null || !currentUser.isVaitro()) {
			return "redirect:/";
		}

		Page<Loai> loaiPage = loaiService.getAllLoai(page, 8);

		model.addAttribute("loais", loaiPage.getContent()); // Danh sách user
		model.addAttribute("currentPage", page); // Trang hiện tại
		model.addAttribute("totalPages", loaiPage.getTotalPages());
		return "admin/loai/loaiManager";
	}

	@GetMapping("/admin/loai/create")
	public String userCreate(Model model, @ModelAttribute("loai") Loai loai) {
		Users currentUser = (Users) session.getAttribute("currentUser");
		if (currentUser == null || !currentUser.isVaitro()) {
			return "redirect:/";
		}
		return "admin/loai/createLoai";
	}

	@PostMapping("/admin/loai/create")
	public String userInsert(Model model, @ModelAttribute("loai") Loai loai) {
		try {
			Users currentUser = (Users) session.getAttribute("currentUser");
			if (currentUser == null || !currentUser.isVaitro()) {
				return "redirect:/";
			}
			loaiService.create(loai);
			model.addAttribute("successMessage", "Tạo loại hàng thành công");
		} catch (Exception e) {
			model.addAttribute("errorMessage", e.getMessage());
		}
		return "admin/loai/createLoai";
	}

	@GetMapping("/admin/loai/edit/{id}")
	public String showUpdateForm(@PathVariable("id") Integer id, Model model) {
		Users currentUser = (Users) session.getAttribute("currentUser");
		if (currentUser == null || !currentUser.isVaitro()) {
			return "redirect:/";
		}
		try {
			Loai loai = loaiService.getLoaiById(id);
			model.addAttribute("loai", loai);
		} catch (Exception e) {
			model.addAttribute("errorMessage", e.getMessage());
		}
		return "admin/loai/updateLoai";
	}

	@PostMapping("/admin/loai/update/{id}")
	public String updateUser(Model model, @PathVariable("id") Integer id, @ModelAttribute("loai") Loai updatedLoai) {
		try {
			Users currentUser = (Users) session.getAttribute("currentUser");
			if (currentUser == null || !currentUser.isVaitro()) {
				return "redirect:/";
			}
			loaiService.updateUser(id, updatedLoai);
			model.addAttribute("successMessage", "Cập nhật loại hàng thành công");
		} catch (Exception e) {
			model.addAttribute("errorMessage", e.getMessage());
		}
		return "admin/loai/updateLoai";
	}

	@GetMapping("/admin/loai/delete/{id}")
	public String deleteUser(RedirectAttributes redirectAttributes, @PathVariable("id") Integer id) {
		try {
			loaiService.deleteLoai(id);
			redirectAttributes.addFlashAttribute("successMessage", "Xóa loại hàng thành công");
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
		}
		return "redirect:/admin/loai";
	}

}
