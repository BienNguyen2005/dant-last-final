package com.poly.controller.admin;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.ui.Model;

@ControllerAdvice(basePackages = "com.poly.controller.admin")
public class StoreInfoAdvice {
    @Value("${store.name}")
    private String storeName;

    @Value("${store.address}")
    private String storeAddress;

    @Value("${store.phone}")
    private String storePhone;

    @ModelAttribute
    public void addStoreInfo(Model model) {
        model.addAttribute("storeName", storeName);
        model.addAttribute("storeAddress", storeAddress);
        model.addAttribute("storePhone", storePhone);
    }
} 