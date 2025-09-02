package com.poly.entity;

import java.util.Date;
import org.springframework.format.annotation.DateTimeFormat;
import jakarta.persistence.*;

@Entity
@Table(name = "VOUCHER")
public class Voucher {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_voucher")
    private Integer id;

    @Column(name = "code", unique = true, nullable = false, length = 50)
    private String code;

    @Column(name = "type", nullable = false, length = 20)
    private String type; // PERCENT or FIXED

    @Column(name = "value", nullable = false)
    private Double value;

    @Column(name = "max_discount")
    private Double maxDiscount;

    @Column(name = "min_order_value")
    private Double minOrderValue;

    @Column(name = "usage_limit")
    private Integer usageLimit; // total allowed global

    @Column(name = "usage_per_user")
    private Integer usagePerUser; // per user cap

    @Column(name = "total_used", nullable = false)
    private Integer totalUsed = 0;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "start_date")
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private Date startDate;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "end_date")
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private Date endDate;

    @Column(name = "active", nullable = false)
    private Boolean active = true;

    public Voucher() {}

    // Getters / Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public Double getValue() { return value; }
    public void setValue(Double value) { this.value = value; }
    public Double getMaxDiscount() { return maxDiscount; }
    public void setMaxDiscount(Double maxDiscount) { this.maxDiscount = maxDiscount; }
    public Double getMinOrderValue() { return minOrderValue; }
    public void setMinOrderValue(Double minOrderValue) { this.minOrderValue = minOrderValue; }
    public Integer getUsageLimit() { return usageLimit; }
    public void setUsageLimit(Integer usageLimit) { this.usageLimit = usageLimit; }
    public Integer getUsagePerUser() { return usagePerUser; }
    public void setUsagePerUser(Integer usagePerUser) { this.usagePerUser = usagePerUser; }
    public Integer getTotalUsed() { return totalUsed; }
    public void setTotalUsed(Integer totalUsed) { this.totalUsed = totalUsed; }
    public Date getStartDate() { return startDate; }
    public void setStartDate(Date startDate) { this.startDate = startDate; }
    public Date getEndDate() { return endDate; }
    public void setEndDate(Date endDate) { this.endDate = endDate; }
    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }
}
