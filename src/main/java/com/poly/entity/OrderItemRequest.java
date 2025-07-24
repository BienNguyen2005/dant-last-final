package com.poly.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class OrderItemRequest {
	private final int productId;
	private final double price;
	private final double discount;
	private final int quantity;

	@JsonCreator
	public OrderItemRequest(@JsonProperty("productId") int productId, @JsonProperty("price") double price,
			@JsonProperty("discount") double discount, @JsonProperty("quantity") int quantity) {
		this.productId = productId;
		this.price = price;
		this.discount = discount;
		this.quantity = quantity;
	}

	public int getProductId() {
		return productId;
	}

	public double getPrice() {
		return price;
	}

	public double getDiscount() {
		return discount;
	}

	public int getQuantity() {
		return quantity;
	}
}
