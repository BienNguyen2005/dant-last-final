// Product Component
class ProductComponent {
    static render(product, relatedProducts = [], purchaseCount = 0) {
        return `
            <div class="row">
                <div class="col-md-6">
                    <div class="text-center">
                        <img src="/image/${product.hinh}" alt="${product.tenSanpham}" class="img-fluid" style="max-height: 400px;">
                    </div>
                </div>
                <div class="col-md-6">
                    <h2>${product.tenSanpham}</h2>
                    <p class="text-muted">Đã bán: ${purchaseCount} sản phẩm</p>
                    
                    <div class="price-section mb-3">
                        ${this.renderPrice(product)}
                    </div>
                    
                    <div class="mb-3">
                        <p><strong>Mô tả ngắn:</strong></p>
                        <p>${product.motangan || 'Không có mô tả'}</p>
                    </div>
                    
                    <div class="mb-3">
                        <label for="quantity" class="form-label">Số lượng:</label>
                        <input type="number" id="quantity" class="form-control" value="1" min="1" max="${product.soluong}" style="width: 100px; display: inline-block;">
                        <span class="ms-2 text-muted">(Còn ${product.soluong} sản phẩm)</span>
                    </div>
                    
                    <div class="mb-3">
                        <button class="btn btn-primary btn-lg me-2" onclick="ProductComponent.addToCart(${product.idSanpham})"
                                ${!app.currentUser ? 'disabled' : ''}>
                            <i class="bi bi-cart-plus"></i> Thêm vào giỏ hàng
                        </button>
                        <button class="btn btn-success btn-lg" onclick="ProductComponent.buyNow(${product.idSanpham})"
                                ${!app.currentUser ? 'disabled' : ''}>
                            <i class="bi bi-bag-check"></i> Mua ngay
                        </button>
                    </div>
                    
                    ${!app.currentUser ? '<p class="text-warning"><i class="bi bi-info-circle"></i> Vui lòng đăng nhập để mua hàng</p>' : ''}
                </div>
            </div>
            
            <div class="mt-5">
                <h4>Mô tả chi tiết</h4>
                <div class="card">
                    <div class="card-body">
                        ${product.mota || 'Không có mô tả chi tiết'}
                    </div>
                </div>
            </div>
            
            ${this.renderRelatedProducts(relatedProducts)}
        `;
    }

    static renderPrice(product) {
        if (product.giamgia > 0) {
            const discountedPrice = product.gia * (100 - product.giamgia) / 100;
            return `
                <span class="h3 text-primary">${app.formatPrice(discountedPrice)}</span>
                <span class="h5 text-muted text-decoration-line-through ms-2">${app.formatPrice(product.gia)}</span>
                <span class="badge bg-danger ms-2">-${product.giamgia}%</span>
            `;
        } else {
            return `<span class="h3 text-primary">${app.formatPrice(product.gia)}</span>`;
        }
    }

    static renderRelatedProducts(relatedProducts) {
        if (!relatedProducts || relatedProducts.length === 0) {
            return '';
        }

        return `
            <div class="mt-5">
                <h4>Sản phẩm liên quan</h4>
                <div class="row">
                    ${relatedProducts.map(product => `
                        <div class="col-md-3">
                            <div class="card mb-3">
                                <img src="/image/${product.hinh}" class="card-img-top" alt="${product.tenSanpham}" style="height: 200px; object-fit: cover;">
                                <div class="card-body">
                                    <h6 class="card-title">${product.tenSanpham}</h6>
                                    <p class="card-text">${app.formatPrice(product.gia)}</p>
                                    <a href="#" onclick="app.navigateTo('product', {id: ${product.idSanpham}})" class="btn btn-sm btn-outline-primary">Xem chi tiết</a>
                                </div>
                            </div>
                        </div>
                    `).join('')}
                </div>
            </div>
        `;
    }

    static async addToCart(productId) {
        if (!app.currentUser) {
            app.showError('Vui lòng đăng nhập để thêm sản phẩm vào giỏ hàng');
            return;
        }

        const quantity = parseInt(document.getElementById('quantity').value) || 1;

        try {
            const cartItem = {
                idUser: app.currentUser.id,
                idSanpham: productId,
                soluong: quantity
            };

            const response = await app.apiCall('POST', '/cart/items', cartItem);
            
            if (response.success) {
                app.showSuccess(response.message);
                app.updateCartBadge();
            } else {
                app.showError(response.message || 'Không thể thêm sản phẩm vào giỏ hàng');
            }
        } catch (error) {
            app.showError('Lỗi khi thêm sản phẩm vào giỏ hàng');
        }
    }

    static async buyNow(productId) {
        await this.addToCart(productId);
        app.navigateTo('cart');
    }
} 