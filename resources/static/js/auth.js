// Authentication Component
class AuthComponent {
    static renderLogin() {
        return `
            <div class="row justify-content-center">
                <div class="col-md-6">
                    <div class="card">
                        <div class="card-header">
                            <h4 class="mb-0">Đăng nhập</h4>
                        </div>
                        <div class="card-body">
                            <form id="loginForm" onsubmit="AuthComponent.handleLogin(event)">
                                <div class="mb-3">
                                    <label for="username" class="form-label">Email/Tên đăng nhập</label>
                                    <input type="text" class="form-control" id="username" name="username" required>
                                </div>
                                <div class="mb-3">
                                    <label for="password" class="form-label">Mật khẩu</label>
                                    <input type="password" class="form-control" id="password" name="password" required>
                                </div>
                                <div class="d-grid">
                                    <button type="submit" class="btn btn-primary">Đăng nhập</button>
                                </div>
                            </form>
                            <div class="text-center mt-3">
                                <p>Chưa có tài khoản? <a href="#" onclick="app.navigateTo('register')">Đăng ký ngay</a></p>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        `;
    }

    static renderRegister() {
        return `
            <div class="row justify-content-center">
                <div class="col-md-6">
                    <div class="card">
                        <div class="card-header">
                            <h4 class="mb-0">Đăng ký tài khoản</h4>
                        </div>
                        <div class="card-body">
                            <form id="registerForm" onsubmit="AuthComponent.handleRegister(event)">
                                <div class="mb-3">
                                    <label for="regUsername" class="form-label">Email</label>
                                    <input type="email" class="form-control" id="regUsername" name="idUser" required>
                                </div>
                                <div class="mb-3">
                                    <label for="regFullName" class="form-label">Họ và tên</label>
                                    <input type="text" class="form-control" id="regFullName" name="hoten" required>
                                </div>
                                <div class="mb-3">
                                    <label for="regPhone" class="form-label">Số điện thoại</label>
                                    <input type="tel" class="form-control" id="regPhone" name="sdt" pattern="[0-9]{10}" required>
                                </div>
                                <div class="mb-3">
                                    <label for="regPassword" class="form-label">Mật khẩu</label>
                                    <input type="password" class="form-control" id="regPassword" name="matkhau" minlength="6" required>
                                </div>
                                <div class="mb-3">
                                    <label for="regPasswordConfirm" class="form-label">Xác nhận mật khẩu</label>
                                    <input type="password" class="form-control" id="regPasswordConfirm" name="passwordConfirm" required>
                                </div>
                                <div class="d-grid">
                                    <button type="submit" class="btn btn-primary">Đăng ký</button>
                                </div>
                            </form>
                            <div class="text-center mt-3">
                                <p>Đã có tài khoản? <a href="#" onclick="app.navigateTo('login')">Đăng nhập</a></p>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        `;
    }

    static async handleLogin(event) {
        event.preventDefault();
        
        const formData = new FormData(event.target);
        const loginData = {
            username: formData.get('username'),
            password: formData.get('password')
        };

        try {
            const response = await app.apiCall('POST', '/auth/login', loginData);
            
            if (response.success) {
                localStorage.setItem('authToken', response.token);
                app.currentUser = response.user;
                app.updateAuthUI();
                app.updateCartBadge();
                app.showSuccess('Đăng nhập thành công!');
                app.navigateTo('home');
            } else {
                app.showError(response.message || 'Đăng nhập thất bại');
            }
        } catch (error) {
            app.showError('Lỗi kết nối, vui lòng thử lại');
        }
    }

    static async handleRegister(event) {
        event.preventDefault();
        
        const formData = new FormData(event.target);
        const password = formData.get('matkhau');
        const passwordConfirm = formData.get('passwordConfirm');

        if (password !== passwordConfirm) {
            app.showError('Mật khẩu xác nhận không khớp');
            return;
        }

        const registerData = {
            idUser: formData.get('idUser'),
            hoten: formData.get('hoten'),
            sdt: formData.get('sdt'),
            matkhau: password,
            vaitro: false
        };

        try {
            const response = await app.apiCall('POST', '/auth/register', registerData);
            
            if (response.success) {
                app.showSuccess(response.message || 'Đăng ký thành công! Vui lòng đăng nhập.');
                app.navigateTo('login');
            } else {
                app.showError(response.message || 'Đăng ký thất bại');
            }
        } catch (error) {
            app.showError('Lỗi kết nối, vui lòng thử lại');
        }
    }
} 