package com.poly.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.poly.entity.VoucherUsage;

@Repository
public interface VoucherUsageRepository extends JpaRepository<VoucherUsage, Integer> {

    @Query("SELECT COUNT(vu) FROM VoucherUsage vu WHERE vu.voucher.id = :voucherId AND vu.user.idUser = :userId")
    long countByVoucherAndUser(Integer voucherId, String userId);

    @Query("SELECT COUNT(vu) FROM VoucherUsage vu WHERE vu.voucher.id = :voucherId")
    long countByVoucher(Integer voucherId);

    void deleteByHoaDon_IdHoadon(Integer idHoadon);
}
