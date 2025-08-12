package com.poly.controller.user;

import com.poly.entity.SanPham;
import com.poly.repository.SanPhamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.util.List;

@RestController
@RequestMapping("/api/recommend")
public class RecommendationController {
    @Autowired
    private SanPhamRepository sanPhamRepository;

    // Gợi ý sản phẩm chăm sóc đi kèm (upsell) dựa trên sản phẩm đã xem / chi tiết
    @GetMapping("/upsell/{productId}")
    public ResponseEntity<?> upsell(@PathVariable("productId") Integer productId,
                                    @RequestParam(defaultValue = "5") int limit) {
        List<SanPham> list = sanPhamRepository
                .recommendByCoPurchaseSameCategory(productId, PageRequest.of(0, Math.min(limit, 10)))
                .getContent();
        return ResponseEntity.ok(list);
    }
}
