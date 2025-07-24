// Cart Component
class CartComponent {
    static render(cartItems = []) {
        if (!cartItems || cartItems.length === 0) {
            return this.renderEmptyCart();
        }

        return `
            <div class="row">
                <div class="col-md-8">
                    <h3>Giỏ hàng của bạn</h3>
                    <div class="card">
                        <div class="card-body">
                            ${cartItems.map(item => this.renderCartItem(item)).join('')}
                        </div>
                    </div>
                </div>
                <div class="col-md-4">
                    ${this.renderOrderSummary(cartItems)}
                </div>
            </div>
        `;
    }

    static renderEmptyCart() {
        return `
            <div class="text-center py-5">
                <i class="bi bi-cart-x" style="font-size: 4rem; color: #6c757d;"></i>
                <h3 class="mt-3">Giỏ hàng trống</h3>
                <p class="text-muted">Bạn chưa có sản phẩm nào trong giỏ hàng</p>
                <button class="btn btn-primary" onclick="app.navigateTo('home')">
                    <i class="bi bi-arrow-left"></i> Tiếp tục mua sắm
                </button>
            </div>
        `;
    }

    static renderCartItem(item) {
        const price = item.giamgia > 0 
            ? item.gia * (100 - item.giamgia) / 100 
            : item.gia;
        const totalPrice = price * item.soluong;

        return `
            <div class="row border-bottom py-3" id="cart-item-${item.stt}">
                <div class="col-md-2">
                    <img src="/image/${item.hinh}" alt="${item.tenSanpham}" class="img-fluid" style="max-height: 80px;">
                </div>
                <div class="col-md-4">
                    <h6>${item.tenSanpham}</h6>
                    <p class="text-muted mb-0">${app.formatPrice(price)}</p>
                </div>
                <div class="col-md-3">
                    <div class="input-group">
                        <button class="btn btn-outline-secondary btn-sm" onclick="CartComponent.updateQuantity(${item.stt}, ${item.soluong - 1})">-</button>
                        <input type="number" class="form-control form-control-sm text-center" value="${item.soluong}" min="1" max="${item.soluongton}" 
                               onchange="CartComponent.updateQuantity(${item.stt}, this.value)">
                        <button class="btn btn-outline-secondary btn-sm" onclick="CartComponent.updateQuantity(${item.stt}, ${item.soluong + 1})">+</button>
                    </div>
                    <small class="text-muted">Còn ${item.soluongton} sản phẩm</small>
                </div>
                <div class="col-md-2">
                    <p class="fw-bold mb-0">${app.formatPrice(totalPrice)}</p>
                </div>
                <div class="col-md-1">
                    <button class="btn btn-outline-danger btn-sm" onclick="CartComponent.removeItem(${item.stt}, ${item.idSanpham})">
                        <i class="bi bi-trash"></i>
                    </button>
                </div>
            </div>
        `;
    }

    static renderOrderSummary(cartItems) {
        const subtotal = cartItems.reduce((sum, item) => {
            const price = item.giamgia > 0 
                ? item.gia * (100 - item.giamgia) / 100 
                : item.gia;
            return sum + (price * item.soluong);
        }, 0);

        const shipping = 20000; // Fixed shipping cost
        const total = subtotal + shipping;

        return `
            <div class="card">
                <div class="card-header">
                    <h5 class="mb-0">Tóm tắt đơn hàng</h5>
                </div>
                <div class="card-body">
                    <div class="d-flex justify-content-between">
                        <span>Tạm tính:</span>
                        <span>${app.formatPrice(subtotal)}</span>
                    </div>
                    <div class="d-flex justify-content-between">
                        <span>Phí vận chuyển:</span>
                        <span>${app.formatPrice(shipping)}</span>
                    </div>
                    <hr>
                    <div class="d-flex justify-content-between fw-bold">
                        <span>Tổng cộng:</span>
                        <span class="text-primary">${app.formatPrice(total)}</span>
                    </div>
                    <button class="btn btn-primary w-100 mt-3" onclick="CartComponent.checkout()">
                        <i class="bi bi-bag-check"></i> Thanh toán
                    </button>
                </div>
            </div>
        `;
    }

    static async updateQuantity(cartItemId, newQuantity) {
        if (newQuantity < 1) {
            return;
        }

        try {
            const cartItem = {
                soluong: parseInt(newQuantity)
            };

            const response = await app.apiCall('PUT', `/cart/items/${cartItemId}`, cartItem);
            
            if (response.success) {
                app.showSuccess(response.message);
                app.loadCartPage(); // Reload cart page
                app.updateCartBadge();
            } else {
                app.showError(response.message || 'Không thể cập nhật số lượng');
            }
        } catch (error) {
            app.showError('Lỗi khi cập nhật số lượng');
        }
    }

    static async removeItem(cartItemId, productId) {
        if (!confirm('Bạn có chắc chắn muốn xóa sản phẩm này khỏi giỏ hàng?')) {
            return;
        }

        try {
            const response = await app.apiCall('DELETE', `/cart/items/${cartItemId}/products/${productId}`);
            
            if (response.success) {
                app.showSuccess(response.message);
                app.loadCartPage(); // Reload cart page
                app.updateCartBadge();
            } else {
                app.showError(response.message || 'Không thể xóa sản phẩm');
            }
        } catch (error) {
            app.showError('Lỗi khi xóa sản phẩm');
        }
    }

    static async checkout() {
        // This is a simplified checkout process
        // In a real application, you would collect shipping info, payment method, etc.
        try {
            const orderRequest = {
                idUser: app.currentUser.id,
                giaohang: "Giao hàng tiêu chuẩn",
                ghichu: ""
            };

            const response = await app.apiCall('POST', '/cart/checkout', orderRequest);
            
            if (response.success) {
                app.showSuccess('Đặt hàng thành công!');
                app.updateCartBadge();
                app.navigateTo('orders');
            } else {
                app.showError(response.message || 'Không thể đặt hàng');
            }
        } catch (error) {
            app.showError('Lỗi khi đặt hàng');
        }
    }
} 