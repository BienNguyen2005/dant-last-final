package com.poly.entity;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.ToString;

@ToString
public class OrderRequest {
	private final int cartId;
	private final String userId;
	private final String address;
	private final int deliveryMethod;
	private final double deliveryPrice;
	private final List<OrderItemRequest> orderItems;

	@JsonCreator
	public OrderRequest(@JsonProperty("cartId") int cartId, @JsonProperty("userId") String userId,
			@JsonProperty("address") String address, @JsonProperty("deliveryMethod") int deliveryMethod,
			@JsonProperty("deliveryPrice") double deliveryPrice,
			@JsonProperty("orderItems") List<OrderItemRequest> orderItems) {
		this.cartId = cartId;
		this.userId = userId;
		this.address = address;
		this.deliveryMethod = deliveryMethod;
		this.deliveryPrice = deliveryPrice;
		this.orderItems = orderItems;
	}

	public int getCartId() {
		return cartId;
	}

	public String getUserId() {
		return userId;
	}

	public String getAddress() {
		return address;
	}

	public int getDeliveryMethod() {
		return deliveryMethod;
	}

	public double getDeliveryPrice() {
		return deliveryPrice;
	}

	public List<OrderItemRequest> getOrderItems() {
		return orderItems;
	}
}
