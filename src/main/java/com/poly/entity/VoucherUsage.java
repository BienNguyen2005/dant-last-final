package com.poly.entity;

import java.util.Date;
import jakarta.persistence.*;

@Entity
@Table(name = "VOUCHER_USAGE")
public class VoucherUsage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usage")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_voucher")
    private Voucher voucher;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_user")
    private Users user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_hoadon")
    private HoaDon hoaDon;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "used_at", nullable = false)
    private Date usedAt;

    public VoucherUsage() {}

    public VoucherUsage(Voucher voucher, Users user, HoaDon hoaDon) {
        this.voucher = voucher;
        this.user = user;
        this.hoaDon = hoaDon;
        this.usedAt = new Date();
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Voucher getVoucher() { return voucher; }
    public void setVoucher(Voucher voucher) { this.voucher = voucher; }
    public Users getUser() { return user; }
    public void setUser(Users user) { this.user = user; }
    public HoaDon getHoaDon() { return hoaDon; }
    public void setHoaDon(HoaDon hoaDon) { this.hoaDon = hoaDon; }
    public Date getUsedAt() { return usedAt; }
    public void setUsedAt(Date usedAt) { this.usedAt = usedAt; }
}
