package com.poly.service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.poly.entity.HoaDon;
import com.poly.entity.Users;
import com.poly.entity.Voucher;
import com.poly.entity.VoucherUsage;
import com.poly.repository.VoucherRepository;
import com.poly.repository.VoucherUsageRepository;

@Service
public class VoucherService {

    private final VoucherRepository voucherRepository;
    private final VoucherUsageRepository voucherUsageRepository;
    private final UserService userService;

    public VoucherService(VoucherRepository voucherRepository, VoucherUsageRepository voucherUsageRepository, UserService userService) {
        this.voucherRepository = voucherRepository;
        this.voucherUsageRepository = voucherUsageRepository;
        this.userService = userService;
    }

    public Voucher create(Voucher voucher) {
        if (voucher.getId() != null) voucher.setId(null);
        validateVoucherFields(voucher);
        if (voucherRepository.existsByCode(voucher.getCode())) {
            throw new IllegalArgumentException("Mã voucher đã tồn tại");
        }
        return voucherRepository.save(voucher);
    }

    public Voucher update(Integer id, Voucher data) {
        Voucher v = getById(id);
        if (!v.getCode().equals(data.getCode()) && voucherRepository.existsByCode(data.getCode())) {
            throw new IllegalArgumentException("Mã voucher đã tồn tại");
        }
        v.setCode(data.getCode());
        v.setType(data.getType());
        v.setValue(data.getValue());
        v.setMaxDiscount(data.getMaxDiscount());
        v.setMinOrderValue(data.getMinOrderValue());
        v.setUsageLimit(data.getUsageLimit());
        v.setUsagePerUser(data.getUsagePerUser());
        v.setStartDate(data.getStartDate());
        v.setEndDate(data.getEndDate());
        v.setActive(data.getActive());
        validateVoucherFields(v);
        return voucherRepository.save(v);
    }

    public void delete(Integer id) {
        voucherRepository.delete(getById(id));
    }

    public Voucher getById(Integer id) {
        return voucherRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Voucher không tồn tại"));
    }

    public List<Voucher> listAll() { return voucherRepository.findAll(); }

    private void validateVoucherFields(Voucher v) {
        if (v.getCode() == null || v.getCode().isBlank()) throw new IllegalArgumentException("Mã voucher không được để trống");
        if (!"PERCENT".equalsIgnoreCase(v.getType()) && !"FIXED".equalsIgnoreCase(v.getType())) {
            throw new IllegalArgumentException("Loại voucher không hợp lệ (PERCENT|FIXED)");
        }
        if (v.getValue() == null || v.getValue() <= 0) throw new IllegalArgumentException("Giá trị voucher phải > 0");
        if (v.getStartDate() != null && v.getEndDate() != null && v.getEndDate().before(v.getStartDate())) {
            throw new IllegalArgumentException("Ngày kết thúc phải sau ngày bắt đầu");
        }
    }

    public Map<String, Object> validate(String code, double subtotal, String userId) {
        Voucher voucher = voucherRepository.findByCode(code).orElseThrow(() -> new IllegalArgumentException("Voucher không tồn tại"));
        if (Boolean.FALSE.equals(voucher.getActive())) throw new IllegalArgumentException("Voucher đã bị vô hiệu hóa");
        Date now = new Date();
        if (voucher.getStartDate() != null && now.before(voucher.getStartDate())) throw new IllegalArgumentException("Voucher chưa bắt đầu hiệu lực");
        if (voucher.getEndDate() != null && now.after(voucher.getEndDate())) throw new IllegalArgumentException("Voucher đã hết hạn");
        if (voucher.getMinOrderValue() != null && subtotal < voucher.getMinOrderValue()) throw new IllegalArgumentException("Chưa đạt giá trị tối thiểu");

        long totalUsed = voucherUsageRepository.countByVoucher(voucher.getId());
        if (voucher.getUsageLimit() != null && totalUsed >= voucher.getUsageLimit()) throw new IllegalArgumentException("Voucher đã hết lượt sử dụng");

        long userUsed = voucherUsageRepository.countByVoucherAndUser(voucher.getId(), userId);
        if (voucher.getUsagePerUser() != null && userUsed >= voucher.getUsagePerUser()) throw new IllegalArgumentException("Bạn đã sử dụng voucher này đủ số lần cho phép");

        double discountAmount;
        if ("PERCENT".equalsIgnoreCase(voucher.getType())) {
            discountAmount = subtotal * (voucher.getValue() / 100.0);
            if (voucher.getMaxDiscount() != null) {
                discountAmount = Math.min(discountAmount, voucher.getMaxDiscount());
            }
        } else { // FIXED
            discountAmount = voucher.getValue();
        }
        if (discountAmount > subtotal) discountAmount = subtotal; // safety

        Map<String, Object> resp = new HashMap<>();
        resp.put("code", voucher.getCode());
        resp.put("discountAmount", discountAmount);
        resp.put("type", voucher.getType());
        resp.put("value", voucher.getValue());
        resp.put("remainingGlobal", voucher.getUsageLimit() == null ? null : Math.max(0, voucher.getUsageLimit() - (int) totalUsed));
        resp.put("remainingUser", voucher.getUsagePerUser() == null ? null : Math.max(0, voucher.getUsagePerUser() - (int) userUsed));
        return resp;
    }

    @Transactional
    public void increaseUsageOnOrder(HoaDon hoaDon, String code, double subtotal, String userId) {
        if (code == null || code.isBlank()) return; // no voucher
        Voucher voucher = voucherRepository.findByCode(code).orElseThrow(() -> new IllegalArgumentException("Voucher không tồn tại"));
        // Re-validate minimal checks to avoid race conditions
        validate(code, subtotal, userId);
        Users user = userService.getUserById(userId);
        voucher.setTotalUsed(voucher.getTotalUsed() == null ? 1 : voucher.getTotalUsed() + 1);
        voucherRepository.save(voucher);
        VoucherUsage usage = new VoucherUsage(voucher, user, hoaDon);
        voucherUsageRepository.save(usage);
    }

    @Transactional
    public void rollbackUsageOnCancel(HoaDon hoaDon) {
        if (hoaDon.getIdHoadon() == null) return;
        // Capture voucher code or relation before deleting
        String code = hoaDon.getVoucherCode();
        Voucher related = null;
        try { related = hoaDon.getVoucher(); } catch (Exception ignored) {}

        // Delete usage records for this order
        voucherUsageRepository.deleteByHoaDon_IdHoadon(hoaDon.getIdHoadon());

        // Recalculate totalUsed precisely if we can resolve voucher
        Voucher voucher = null;
        if (related != null && related.getId() != null) {
            voucher = voucherRepository.findById(related.getId()).orElse(null);
        } else if (code != null && !code.isBlank()) {
            voucher = voucherRepository.findByCode(code).orElse(null);
        }
        if (voucher != null) {
            long remaining = voucherUsageRepository.countByVoucher(voucher.getId());
            voucher.setTotalUsed((int) remaining);
            voucherRepository.save(voucher);
        }
    }
}
