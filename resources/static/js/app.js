// Main Application Class
class App {
    constructor() {
        this.currentUser = null;
        this.currentPage = 'home';
        this.apiBaseUrl = '/api';
        this.init();
    }

    init() {
        this.checkAuthStatus();
        this.loadCategories();
        this.updateCartBadge();
        this.navigateTo('home');
    }

    // Authentication methods
    async checkAuthStatus() {
        const token = localStorage.getItem('authToken');
        if (token) {
            try {
                const response = await this.apiCall('POST', '/auth/validate-token', null, token);
                if (response.valid) {
                    this.currentUser = response.user;
                    this.updateAuthUI();
                } else {
                    localStorage.removeItem('authToken');
                }
            } catch (error) {
                localStorage.removeItem('authToken');
            }
        }
        this.updateAuthUI();
    }

    updateAuthUI() {
        const loginButton = document.getElementById('loginButton');
        const userDropdown = document.getElementById('userDropdown');
        const adminButton = document.getElementById('adminButton');
        const userName = document.getElementById('userName');

        if (this.currentUser) {
            loginButton.style.display = 'none';
            userDropdown.style.display = 'block';
            userName.textContent = this.currentUser.fullName || this.currentUser.username;
            
            if (this.currentUser.isAdmin) {
                adminButton.style.display = 'block';
            }
        } else {
            loginButton.style.display = 'block';
            userDropdown.style.display = 'none';
            adminButton.style.display = 'none';
        }
    }

    // API methods
    async apiCall(method, endpoint, data = null, token = null) {
        const headers = {
            'Content-Type': 'application/json'
        };

        if (token) {
            headers['Authorization'] = `Bearer ${token}`;
        } else if (this.currentUser) {
            const authToken = localStorage.getItem('authToken');
            if (authToken) {
                headers['Authorization'] = `Bearer ${authToken}`;
            }
        }

        const config = {
            method: method,
            headers: headers
        };

        if (data && (method === 'POST' || method === 'PUT')) {
            config.body = JSON.stringify(data);
        }

        const response = await fetch(this.apiBaseUrl + endpoint, config);
        return await response.json();
    }

    // Navigation methods
    navigateTo(page, params = {}) {
        this.currentPage = page;
        this.showLoading();

        // Update active nav link
        document.querySelectorAll('.nav-link').forEach(link => {
            link.classList.remove('active');
        });

        switch (page) {
            case 'home':
                this.loadHomePage();
                break;
            case 'login':
                this.loadLoginPage();
                break;
            case 'register':
                this.loadRegisterPage();
                break;
            case 'cart':
                this.loadCartPage();
                break;
            case 'product':
                this.loadProductPage(params.id);
                break;
            case 'category':
                this.loadCategoryPage(params.id);
                break;
            case 'search':
                this.loadSearchPage(params.keyword);
                break;
            case 'orders':
                this.loadOrdersPage();
                break;
            case 'profile':
                this.loadProfilePage();
                break;
            case 'admin':
                this.loadAdminPage();
                break;
            default:
                this.loadHomePage();
        }
    }

    showLoading() {
        document.getElementById('loadingSpinner').classList.remove('d-none');
        document.getElementById('contentArea').innerHTML = '';
    }

    hideLoading() {
        document.getElementById('loadingSpinner').classList.add('d-none');
    }

    // Content loading methods
    async loadHomePage() {
        try {
            const [categories, products, discountedProducts] = await Promise.all([
                this.apiCall('GET', '/public/categories?size=5'),
                this.apiCall('GET', '/public/products?size=12'),
                this.apiCall('GET', '/public/products/discounted?size=12')
            ]);

            const content = HomeComponent.render(categories.data, products.data, discountedProducts.data);
            document.getElementById('contentArea').innerHTML = content;
            this.hideLoading();
        } catch (error) {
            this.showError('Lỗi khi tải trang chủ');
            this.hideLoading();
        }
    }

    async loadLoginPage() {
        const content = AuthComponent.renderLogin();
        document.getElementById('contentArea').innerHTML = content;
        this.hideLoading();
    }

    async loadRegisterPage() {
        const content = AuthComponent.renderRegister();
        document.getElementById('contentArea').innerHTML = content;
        this.hideLoading();
    }

    async loadCartPage() {
        if (!this.currentUser) {
            this.navigateTo('login');
            return;
        }

        try {
            const cartData = await this.apiCall('GET', `/cart/items?userId=${this.currentUser.id}`);
            const content = CartComponent.render(cartData.cartItems);
            document.getElementById('contentArea').innerHTML = content;
            this.hideLoading();
        } catch (error) {
            this.showError('Lỗi khi tải giỏ hàng');
            this.hideLoading();
        }
    }

    async loadProductPage(productId) {
        try {
            const [productData, relatedProducts] = await Promise.all([
                this.apiCall('GET', `/public/products/${productId}`),
                this.apiCall('GET', `/public/products/related/${productId}`)
            ]);

            const content = ProductComponent.render(productData.product, relatedProducts.data, productData.purchaseCount);
            document.getElementById('contentArea').innerHTML = content;
            this.hideLoading();
        } catch (error) {
            this.showError('Lỗi khi tải sản phẩm');
            this.hideLoading();
        }
    }

    async loadCategoryPage(categoryId) {
        try {
            const products = await this.apiCall('GET', `/public/products/category/${categoryId}?size=20`);
            const content = `
                <h3>Sản phẩm theo danh mục</h3>
                <div class="row">
                    ${products.data.map(product => HomeComponent.renderProductCard(product)).join('')}
                </div>
            `;
            document.getElementById('contentArea').innerHTML = content;
            this.hideLoading();
        } catch (error) {
            this.showError('Lỗi khi tải sản phẩm theo danh mục');
            this.hideLoading();
        }
    }

    async loadSearchPage(keyword) {
        try {
            const products = await this.apiCall('GET', `/public/products/search?keyword=${encodeURIComponent(keyword)}&size=20`);
            const content = `
                <h3>Kết quả tìm kiếm cho: "${keyword}"</h3>
                <div class="row">
                    ${products.data.map(product => HomeComponent.renderProductCard(product)).join('')}
                </div>
            `;
            document.getElementById('contentArea').innerHTML = content;
            this.hideLoading();
        } catch (error) {
            this.showError('Lỗi khi tìm kiếm sản phẩm');
            this.hideLoading();
        }
    }

    async loadOrdersPage() {
        if (!this.currentUser) {
            this.navigateTo('login');
            return;
        }
        
        const content = `
            <h3>Đơn hàng của bạn</h3>
            <p class="text-muted">Chức năng đang được phát triển</p>
        `;
        document.getElementById('contentArea').innerHTML = content;
        this.hideLoading();
    }

    async loadProfilePage() {
        if (!this.currentUser) {
            this.navigateTo('login');
            return;
        }
        
        const content = `
            <h3>Thông tin tài khoản</h3>
            <p class="text-muted">Chức năng đang được phát triển</p>
        `;
        document.getElementById('contentArea').innerHTML = content;
        this.hideLoading();
    }

    async loadAdminPage() {
        if (!this.currentUser || !this.currentUser.isAdmin) {
            this.showError('Bạn không có quyền truy cập trang này');
            this.navigateTo('home');
            return;
        }
        
        const content = AdminComponent.render();
        document.getElementById('contentArea').innerHTML = content;
        AdminComponent.loadStats();
        this.hideLoading();
    }

    async loadCategories() {
        try {
            const categories = await this.apiCall('GET', '/public/categories?size=20');
            const categoryMenu = document.getElementById('categoryDropdownMenu');
            categoryMenu.innerHTML = categories.data.map(category => 
                `<li><a class="dropdown-item" href="#" onclick="app.navigateTo('category', {id: ${category.idLoai}})">${category.tenLoai}</a></li>`
            ).join('');
        } catch (error) {
            console.error('Error loading categories:', error);
        }
    }

    async updateCartBadge() {
        if (!this.currentUser) {
            document.getElementById('cartBadge').textContent = '0';
            return;
        }

        try {
            const cartData = await this.apiCall('GET', `/cart/items?userId=${this.currentUser.id}`);
            const itemCount = cartData.cartItems ? cartData.cartItems.length : 0;
            document.getElementById('cartBadge').textContent = itemCount;
        } catch (error) {
            document.getElementById('cartBadge').textContent = '0';
        }
    }

    // Utility methods
    showToast(message, type = 'info') {
        const toast = document.getElementById('toast');
        const toastMessage = document.getElementById('toastMessage');
        
        toastMessage.textContent = message;
        toast.classList.remove('text-bg-success', 'text-bg-danger', 'text-bg-info');
        
        switch (type) {
            case 'success':
                toast.classList.add('text-bg-success');
                break;
            case 'error':
                toast.classList.add('text-bg-danger');
                break;
            default:
                toast.classList.add('text-bg-info');
        }

        const bsToast = new bootstrap.Toast(toast);
        bsToast.show();
    }

    showError(message) {
        this.showToast(message, 'error');
    }

    showSuccess(message) {
        this.showToast(message, 'success');
    }

    formatPrice(price) {
        return new Intl.NumberFormat('vi-VN').format(price) + '₫';
    }

    formatDate(dateString) {
        return new Date(dateString).toLocaleDateString('vi-VN');
    }
}

// Global app instance
let app;

// Global functions
function initializeApp() {
    app = new App();
}

function logout() {
    localStorage.removeItem('authToken');
    app.currentUser = null;
    app.updateAuthUI();
    app.updateCartBadge();
    app.navigateTo('home');
    app.showSuccess('Đã đăng xuất thành công');
}

function handleSearch(event) {
    event.preventDefault();
    const keyword = document.getElementById('searchInput').value.trim();
    if (keyword) {
        app.navigateTo('search', { keyword: keyword });
    }
}

// Global navigation function
function navigateTo(page, params = {}) {
    app.navigateTo(page, params);
} 