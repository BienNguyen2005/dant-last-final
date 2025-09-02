package com.poly.controller.admin;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.poly.entity.SanPham;
import org.springframework.security.access.prepost.PreAuthorize;
import com.poly.service.LoaiService;
import com.poly.service.SanPhamService;

@Controller
public class SanPhamController {
	@Autowired
	SanPhamService sanPhamService;
	@Autowired
	LoaiService loaiService;

	@GetMapping("/admin/sanpham/search")
	@PreAuthorize("hasRole('ADMIN')")
	public String search(Model model, @RequestParam(name = "q", required = false) String q,
			@RequestParam(defaultValue = "0", name = "page") int page) {

		Optional<String> query = Optional.ofNullable(q).filter(s -> !s.trim().isEmpty());
		if (query.isPresent()) {
			String queryStr = query.get();
			Page<SanPham> sanPhamPage = sanPhamService.searchByName(page, 8, queryStr);
			model.addAttribute("sanphams", sanPhamPage.getContent()); // Danh sách user
			model.addAttribute("currentPage", page); // Trang hiện tại
			model.addAttribute("totalPages", sanPhamPage.getTotalPages());
			model.addAttribute("query", queryStr);
			return "admin/sanpham/sanphamManager";
		} else {
			return "redirect:/admin/sanpham";
		}
	}

	@GetMapping("/admin/sanpham")
	@PreAuthorize("hasRole('ADMIN')")
	public String sanPhamManager(Model model, @RequestParam(defaultValue = "0", name = "page") int page) {

		Page<SanPham> sanPhamPage = sanPhamService.getAllSanPham(page, 8);

		model.addAttribute("sanphams", sanPhamPage.getContent()); // Danh sách user
		model.addAttribute("currentPage", page); // Trang hiện tại
		model.addAttribute("totalPages", sanPhamPage.getTotalPages());
		return "admin/sanpham/sanphamManager";
	}

	@GetMapping("/admin/sanpham/create")
	@PreAuthorize("hasRole('ADMIN')")
	public String userCreate(Model model, @ModelAttribute("sanpham") SanPham sanPham) {
		model.addAttribute("loais", loaiService.getAllLoai(0, 100));
		return "admin/sanpham/createSanPham";
	}

	@PostMapping("/admin/sanpham/create")
	@PreAuthorize("hasRole('ADMIN')")
	public String sanphamInsert(Model model, @ModelAttribute("sanpham") SanPham sanPham,
			@RequestParam("image") MultipartFile image) {
		try {
			sanPhamService.create(sanPham, image);
			model.addAttribute("successMessage", "Tạo sản phẩm thành công");
		} catch (Exception e) {
			model.addAttribute("errorMessage", e.getMessage());
		}
		model.addAttribute("loais", loaiService.getAllLoai(0, 100));
		return "admin/sanpham/createSanPham";
	}

	@GetMapping("/admin/sanpham/edit/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	public String showUpdateForm(@PathVariable("id") Integer id, Model model) {
		try {
			SanPham sanPham = sanPhamService.getSanPhamById(id);
			model.addAttribute("sanpham", sanPham);
		} catch (Exception e) {
			model.addAttribute("errorMessage", e.getMessage());
		}
		model.addAttribute("loais", loaiService.getAllLoai(0, 100));
		return "admin/sanpham/updateSanPham";
	}

	@PostMapping("/admin/sanpham/update/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	public String updateUser(Model model, @PathVariable("id") Integer id,
			@ModelAttribute("sanpham") SanPham updatedSanPham,
			@RequestParam(name = "image", required = false) MultipartFile image) {
		try {
			model.addAttribute("sanpham", sanPhamService.updateSanPham(id, updatedSanPham, image));
			model.addAttribute("successMessage", "Cập nhật sản phẩm thành công");
		} catch (Exception e) {
			model.addAttribute("errorMessage", e.getMessage());
		}
		model.addAttribute("loais", loaiService.getAllLoai(0, 100));
		return "admin/sanpham/updateSanPham";
	}

	@GetMapping("/admin/sanpham/delete/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	public String deleteSanPham(RedirectAttributes redirectAttributes, @PathVariable("id") Integer id) {
		try {
			sanPhamService.deleteSanPham(id);
			redirectAttributes.addFlashAttribute("successMessage", "Xóa sản phẩm thành công");
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
		}
		return "redirect:/admin/sanpham";
	}

}
