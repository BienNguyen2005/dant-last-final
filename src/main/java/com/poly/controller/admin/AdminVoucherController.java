package com.poly.controller.admin;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import org.springframework.beans.propertyeditors.CustomDateEditor;

import com.poly.entity.Voucher;
import com.poly.service.VoucherService;

@Controller
@RequestMapping("/admin/vouchers")
public class AdminVoucherController {

    private final VoucherService voucherService;

    public AdminVoucherController(VoucherService voucherService) { this.voucherService = voucherService; }

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
        sdf.setLenient(false);
        binder.registerCustomEditor(Date.class, new CustomDateEditor(sdf, true));
    }

    @GetMapping
    public String list(Model model) {
        List<Voucher> vouchers = voucherService.listAll();
        model.addAttribute("vouchers", vouchers);
        return "admin/voucher/list";
    }

    @GetMapping("/create")
    public String createForm(Model model) {
        model.addAttribute("voucher", new Voucher());
        return "admin/voucher/create";
    }

    @PostMapping
    public String create(@ModelAttribute("voucher") Voucher voucher, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("error", "Dữ liệu không hợp lệ");
            return "admin/voucher/create";
        }
        try {
            voucherService.create(voucher);
            return "redirect:/admin/vouchers";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "admin/voucher/create";
        }
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Integer id, Model model) {
        model.addAttribute("voucher", voucherService.getById(id));
        return "admin/voucher/edit";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Integer id, @ModelAttribute("voucher") Voucher voucher, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("error", "Dữ liệu không hợp lệ");
            return "admin/voucher/edit";
        }
        try {
            voucherService.update(id, voucher);
            return "redirect:/admin/vouchers";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("voucher", voucher);
            return "admin/voucher/edit";
        }
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Integer id) {
        voucherService.delete(id);
        return "redirect:/admin/vouchers";
    }
}
