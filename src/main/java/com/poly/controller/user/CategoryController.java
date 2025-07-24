package com.poly.controller.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.poly.entity.Loai;
import com.poly.entity.SanPham;
import com.poly.service.LoaiService;
import com.poly.service.SanPhamService;

@Controller
public class CategoryController {
	@Autowired
	SanPhamService sanPhamService;
	@Autowired
	LoaiService loaiService;
	private static final int PRODUCTS_PER_PAGE = 100;

	@GetMapping("/loai")
	public String search(Model model, @RequestParam(name = "id", required = false) String idStr,
			@RequestParam(name = "priceRanges", required = false) String priceRanges,
			@RequestParam(name = "order", defaultValue = "totalBuy-DESC", required = false) String order,
			@RequestParam(defaultValue = "0", name = "page") int page) {
		try {
			Integer id = Integer.valueOf(idStr);
			Loai loai = loaiService.getLoaiById(id);
			if (loai != null) {
				Page<SanPham> sanPhamPage = sanPhamService.filterProducts(id, priceRanges, order, page,
						PRODUCTS_PER_PAGE);
				model.addAttribute("sanphams", sanPhamPage.getContent()); // Danh sách user
				model.addAttribute("totalProducts", sanPhamPage.getTotalElements()); // Danh sách user
				model.addAttribute("currentPage", page); // Trang hiện tại
				model.addAttribute("totalPages", sanPhamPage.getTotalPages());
				model.addAttribute("currentPage", page); // Trang hiện tại
				model.addAttribute("order", order);
				model.addAttribute("priceRanges", priceRanges);
				model.addAttribute("loais", loaiService.getAllLoai(0, 5));
				model.addAttribute("loai", loai);
				return "user/categoryView";
			} else {
				return "redirect:/";
			}
		} catch (Exception e) {
			e.printStackTrace();
			return "redirect:/";
		}
	}
}
