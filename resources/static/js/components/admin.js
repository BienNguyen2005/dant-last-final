// Admin Component
class AdminComponent {
    static render() {
        return `
            <div class="row">
                <div class="col-12">
                    <h2>Bảng điều khiển quản trị</h2>
                    <p class="text-muted">Chào mừng bạn đến với trang quản trị hệ thống</p>
                </div>
            </div>
            
            <div class="row" id="adminStats">
                <div class="col-md-3">
                    <div class="card text-white bg-primary">
                        <div class="card-body">
                            <h5 class="card-title">Người dùng</h5>
                            <h3 id="totalUsers">0</h3>
                        </div>
                    </div>
                </div>
                <div class="col-md-3">
                    <div class="card text-white bg-success">
                        <div class="card-body">
                            <h5 class="card-title">Sản phẩm</h5>
                            <h3 id="totalProducts">0</h3>
                        </div>
                    </div>
                </div>
                <div class="col-md-3">
                    <div class="card text-white bg-info">
                        <div class="card-body">
                            <h5 class="card-title">Danh mục</h5>
                            <h3 id="totalCategories">0</h3>
                        </div>
                    </div>
                </div>
                <div class="col-md-3">
                    <div class="card text-white bg-warning">
                        <div class="card-body">
                            <h5 class="card-title">Đơn hàng</h5>
                            <h3 id="totalOrders">0</h3>
                        </div>
                    </div>
                </div>
            </div>
            
            <div class="row mt-4">
                <div class="col-md-4">
                    <div class="card">
                        <div class="card-body text-center">
                            <i class="bi bi-people" style="font-size: 3rem; color: #007bff;"></i>
                            <h5 class="mt-2">Quản lý người dùng</h5>
                            <p class="text-muted">Thêm, sửa, xóa tài khoản người dùng</p>
                            <button class="btn btn-primary" onclick="AdminComponent.manageUsers()">Quản lý</button>
                        </div>
                    </div>
                </div>
                <div class="col-md-4">
                    <div class="card">
                        <div class="card-body text-center">
                            <i class="bi bi-box" style="font-size: 3rem; color: #28a745;"></i>
                            <h5 class="mt-2">Quản lý sản phẩm</h5>
                            <p class="text-muted">Thêm, sửa, xóa sản phẩm</p>
                            <button class="btn btn-success" onclick="AdminComponent.manageProducts()">Quản lý</button>
                        </div>
                    </div>
                </div>
                <div class="col-md-4">
                    <div class="card">
                        <div class="card-body text-center">
                            <i class="bi bi-list-ul" style="font-size: 3rem; color: #17a2b8;"></i>
                            <h5 class="mt-2">Quản lý đơn hàng</h5>
                            <p class="text-muted">Xem và xử lý đơn hàng</p>
                            <button class="btn btn-info" onclick="AdminComponent.manageOrders()">Quản lý</button>
                        </div>
                    </div>
                </div>
            </div>
        `;
    }

    static async loadStats() {
        try {
            const response = await app.apiCall('GET', '/admin/dashboard');
            
            if (response.success) {
                document.getElementById('totalUsers').textContent = response.totalUsers;
                document.getElementById('totalProducts').textContent = response.totalProducts;
                document.getElementById('totalCategories').textContent = response.totalCategories;
                document.getElementById('totalOrders').textContent = response.totalOrders;
            }
        } catch (error) {
            console.error('Error loading admin stats:', error);
        }
    }

    static manageUsers() {
        app.showToast('Chức năng quản lý người dùng đang được phát triển', 'info');
        // In a full implementation, this would load a user management interface
    }

    static manageProducts() {
        app.showToast('Chức năng quản lý sản phẩm đang được phát triển', 'info');
        // In a full implementation, this would load a product management interface
    }

    static manageOrders() {
        app.showToast('Chức năng quản lý đơn hàng đang được phát triển', 'info');
        // In a full implementation, this would load an order management interface
    }
} 