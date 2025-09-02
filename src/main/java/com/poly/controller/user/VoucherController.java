package com.poly.controller.user;

import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.poly.service.VoucherService;

@RestController
@RequestMapping("/api/vouchers")
public class VoucherController {

    private final VoucherService voucherService;

    public VoucherController(VoucherService voucherService) { this.voucherService = voucherService; }

    @GetMapping("/validate")
    public ResponseEntity<?> validate(@RequestParam String code, @RequestParam double subtotal, @RequestParam String userId) {
        try {
            Map<String, Object> resp = voucherService.validate(code, subtotal, userId);
            return ResponseEntity.ok(resp);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
