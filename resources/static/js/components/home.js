// Home Component
class HomeComponent {
    static render(categories, products, discountedProducts) {
        return `
            ${this.renderCategoriesSection(categories)}
            ${this.renderProductsSection('Sản phẩm mới nhất', products)}
            ${this.renderProductsSection('Sản phẩm giảm giá', discountedProducts)}
        `;
    }

    static renderCategoriesSection(categories) {
        if (!categories || categories.length === 0) {
            return '';
        }

        return `
            <section class="section-content mb-2">
                <div class="container">
                    <header class="section-heading py-4 d-flex justify-content-between">
                        <h3 class="section-title">Danh mục sản phẩm</h3>
                        <button class="btn btn-secondary" onclick="app.navigateTo('categories')" style="height: fit-content;">Xem tất cả</button>
                    </header>
                    <div class="row item-grid">
                        ${categories.map(category => this.renderCategoryCard(category)).join('')}
                    </div>
                </div>
            </section>
        `;
    }

    static renderCategoryCard(category) {
        return `
            <div class="col-lg-2 col-md-6">
                <div class="card mb-4">
                    <div class="card-body">
                        <a href="#" onclick="app.navigateTo('category', {id: ${category.idLoai}})" class="stretched-link">
                            <div class="text-center">
                                <span class="category-title">${category.tenLoai}</span>
                            </div>
                        </a>
                    </div>
                </div>
            </div>
        `;
    }

    static renderProductsSection(title, products) {
        if (!products || products.length === 0) {
            return '';
        }

        return `
            <section class="section-content mb-5">
                <div class="container">
                    <header class="section-heading py-4">
                        <h3 class="section-title">${title}</h3>
                    </header>
                    <div class="row item-grid">
                        ${products.map(product => this.renderProductCard(product)).join('')}
                    </div>
                </div>
            </section>
        `;
    }

    static renderProductCard(product) {
        const price = product.giamgia > 0 
            ? product.gia * (100 - product.giamgia) / 100 
            : product.gia;
        
        const priceHtml = product.giamgia > 0 
            ? `
                <span class="price mt-1 fw-bold">${app.formatPrice(price)}</span>
                <span class="ms-2 text-muted text-decoration-line-through">${app.formatPrice(product.gia)}</span>
                <span class="ms-2 badge bg-info">-${product.giamgia}%</span>
              `
            : `<span class="price mt-1 fw-bold">${app.formatPrice(product.gia)}</span>`;

        return `
            <div class="col-xl-3 col-lg-4 col-md-6">
                <div class="card p-3 mb-4">
                    <a href="#" onclick="app.navigateTo('product', {id: ${product.idSanpham}})" class="img-wrap text-center">
                        <img width="200" height="200" class="img-fluid" src="/image/${product.hinh}" alt="${product.tenSanpham}" />
                    </a>
                    <figcaption class="info-wrap mt-2">
                        <a href="#" onclick="app.navigateTo('product', {id: ${product.idSanpham}})" class="title td-text">${product.tenSanpham}</a>
                        <div>
                            ${priceHtml}
                        </div>
                        <div class="mt-2">
                            <button class="btn btn-primary btn-sm" onclick="HomeComponent.addToCart(${product.idSanpham})" 
                                    ${!app.currentUser ? 'disabled title="Vui lòng đăng nhập để mua hàng"' : ''}>
                                <i class="bi bi-cart-plus"></i> Thêm vào giỏ
                            </button>
                        </div>
                    </figcaption>
                </div>
            </div>
        `;
    }

    static async addToCart(productId) {
        if (!app.currentUser) {
            app.showError('Vui lòng đăng nhập để thêm sản phẩm vào giỏ hàng');
            return;
        }

        try {
            const cartItem = {
                idUser: app.currentUser.id,
                idSanpham: productId,
                soluong: 1
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
} 