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
import org.springframework.security.access.prepost.PreAuthorize;
import com.poly.service.LoaiService;

@Controller
public class LoaiController {
	@Autowired
	LoaiService loaiService;

	@GetMapping("/admin/loai")
	@PreAuthorize("hasRole('ADMIN')")
	public String loaiManager(Model model, @RequestParam(defaultValue = "0", name = "page") int page) {

		Page<Loai> loaiPage = loaiService.getAllLoai(page, 8);

		model.addAttribute("loais", loaiPage.getContent()); // Danh sách user
		model.addAttribute("currentPage", page); // Trang hiện tại
		model.addAttribute("totalPages", loaiPage.getTotalPages());
		return "admin/loai/loaiManager";
	}

	@GetMapping("/admin/loai/create")
	@PreAuthorize("hasRole('ADMIN')")
	public String userCreate(Model model, @ModelAttribute("loai") Loai loai) {
		return "admin/loai/createLoai";
	}

	@PostMapping("/admin/loai/create")
	@PreAuthorize("hasRole('ADMIN')")
	public String userInsert(Model model, @ModelAttribute("loai") Loai loai) {
		try {
			loaiService.create(loai);
			model.addAttribute("successMessage", "Tạo loại hàng thành công");
		} catch (Exception e) {
			model.addAttribute("errorMessage", e.getMessage());
		}
		return "admin/loai/createLoai";
	}

	@GetMapping("/admin/loai/edit/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	public String showUpdateForm(@PathVariable("id") Integer id, Model model) {
		try {
			Loai loai = loaiService.getLoaiById(id);
			model.addAttribute("loai", loai);
		} catch (Exception e) {
			model.addAttribute("errorMessage", e.getMessage());
		}
		return "admin/loai/updateLoai";
	}

	@PostMapping("/admin/loai/update/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	public String updateUser(Model model, @PathVariable("id") Integer id, @ModelAttribute("loai") Loai updatedLoai) {
		try {
			loaiService.updateUser(id, updatedLoai);
			model.addAttribute("successMessage", "Cập nhật loại hàng thành công");
		} catch (Exception e) {
			model.addAttribute("errorMessage", e.getMessage());
		}
		return "admin/loai/updateLoai";
	}

	@GetMapping("/admin/loai/delete/{id}")
	@PreAuthorize("hasRole('ADMIN')")
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
