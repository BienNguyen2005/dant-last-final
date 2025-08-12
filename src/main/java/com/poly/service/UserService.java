package com.poly.service;

import java.util.Objects;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.poly.entity.GioHang;
import com.poly.entity.ReportKhachHangVip;
import com.poly.entity.Users;
import com.poly.repository.GioHangChiTietRepository;
import com.poly.repository.GioHangRepository;
import com.poly.repository.HoaDonChiTietRepository;
import com.poly.repository.HoaDonRepository;
import com.poly.repository.UsersRepository;
import com.poly.utils.ImageUtils;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Service
public class UserService {
	@Autowired
	UsersRepository usersRepository;
	@Autowired
	private HttpSession session;
	@Autowired
	GioHangRepository gioHangRepository;
	@Autowired
	GioHangChiTietRepository gioHangChiTietRepository;
	@Autowired
	HoaDonChiTietRepository hoaDonChiTietRepository;
	@Autowired
	HoaDonRepository hoaDonRepository;
	@Autowired
	EmailService emailService;
	@Autowired
	JWTService jwtService;
	@Autowired
	PasswordEncoder passwordEncoder;
	@Autowired
	HttpServletResponse response;

	public Users register(Users user) {
		Optional<Users> existingUserById = usersRepository.findById(user.getIdUser());
		if (existingUserById.isPresent()) {
			throw new IllegalArgumentException("Email người dùng đã tồn tại!");
		}

		Optional<Users> existingUserByPhone = usersRepository.findBySdt(user.getSdt());
		if (existingUserByPhone.isPresent()) {
			throw new IllegalArgumentException("Số điện thoại đã được sử dụng!");
		}
		
		// Encode password before saving
		user.setMatkhau(passwordEncoder.encode(user.getMatkhau()));
		user.setVaitro(false);
		user.setKichhoat(false);
		usersRepository.save(user);

		GioHang gioHang = new GioHang();
		gioHang.setUsers(user);
		gioHangRepository.save(gioHang);

		activeAccount(user.getIdUser());
		return user;
	}

	public Users create(Users user) {
		Optional<Users> existingUserById = usersRepository.findById(user.getIdUser());
		if (existingUserById.isPresent()) {
			throw new IllegalArgumentException("Email người dùng đã tồn tại!");
		}

		Optional<Users> existingUserByPhone = usersRepository.findBySdt(user.getSdt());
		if (existingUserByPhone.isPresent()) {
			throw new IllegalArgumentException("Số điện thoại đã được sử dụng!");
		}
		
		// Encode password before saving
		user.setMatkhau(passwordEncoder.encode(user.getMatkhau()));
		usersRepository.save(user);

		GioHang gioHang = new GioHang();
		gioHang.setUsers(user);
		gioHangRepository.save(gioHang);

		return user;
	}

	public Users updateUser(String id, Users updatedUser) {
		Optional<Users> optionalUser = usersRepository.findById(id);
		if (optionalUser.isPresent()) {
			Users user = optionalUser.get();
			user.setHoten(updatedUser.getHoten());
			user.setSdt(updatedUser.getSdt());
			user.setHinh(updatedUser.getHinh());
			user.setVaitro(updatedUser.isVaitro());
			user.setKichhoat(updatedUser.isKichhoat());
			
			// If password is provided, encode and update it
			if (updatedUser.getMatkhau() != null && !updatedUser.getMatkhau().isEmpty()) {
				user.setMatkhau(passwordEncoder.encode(updatedUser.getMatkhau()));
			}

			return usersRepository.save(user);
		} else {
			throw new RuntimeException("Không tìm thấy người dùng!");
		}
	}

	public Users changePassword(String id, String currentPassword, String newPassword) {
		Optional<Users> optionalUser = usersRepository.findById(id);
		if (optionalUser.isPresent()) {
			Users user = optionalUser.get();
			
			// Verify current password
			if (!passwordEncoder.matches(currentPassword, user.getMatkhau())) {
				throw new IllegalArgumentException("Mật khẩu hiện tại không chính xác!");
			}
			
			// Encode and save new password
			user.setMatkhau(passwordEncoder.encode(newPassword));
			session.setAttribute("currentUser", usersRepository.save(user));
			return user;
		} else {
			throw new RuntimeException("Không tìm thấy người dùng!");
		}
	}

	public Users updateProfile(String id, Users updatedUser, MultipartFile image) {
		Optional<Users> optionalUser = usersRepository.findById(id);
		if (optionalUser.isPresent()) {
			Users user = optionalUser.get();
			if (image != null && image.getSize() > 0) {
				if (Objects.nonNull(user.getHinh()) && !user.getHinh().isEmpty()) {
					ImageUtils.delete(user.getHinh());
				}
				user.setHinh(ImageUtils.upload(image)
						.orElseThrow(() -> new RuntimeException("Có lỗi xảy ra trong quá trình tải ảnh")));
			}
			user.setHoten(updatedUser.getHoten());
			user.setSdt(updatedUser.getSdt());
			session.setAttribute("currentUser", usersRepository.save(user));
			return user;
		} else {
			throw new RuntimeException("Không tìm thấy người dùng!");
		}
	}

	public Users login(Users users) {
		Optional<Users> userOptional = usersRepository.findById(users.getIdUser());
		if (userOptional.isEmpty()) {
			throw new IllegalArgumentException("Tài khoản không tồn tại!");
		}

		Users user = userOptional.get();
		if (!user.isKichhoat()) {
			throw new IllegalArgumentException("Tài khoản của bạn chưa được kích hoạt!");
		}
		// --- Password verification (supports legacy plaintext & auto-upgrade) ---
		String rawPassword = users.getMatkhau();
		String storedPassword = user.getMatkhau();
		boolean storedLooksBCrypt = storedPassword != null && (storedPassword.startsWith("$2a$") || storedPassword.startsWith("$2b$") || storedPassword.startsWith("$2y$"));
		boolean passwordOk = storedLooksBCrypt ? passwordEncoder.matches(rawPassword, storedPassword) : rawPassword.equals(storedPassword);
		if (!passwordOk) {
			throw new IllegalArgumentException("Tài khoản hoặc mật khẩu không chính xác!");
		}
		// If legacy plaintext password matched, upgrade it to bcrypt
		if (!storedLooksBCrypt) {
			user.setMatkhau(passwordEncoder.encode(rawPassword));
			usersRepository.save(user);
		}
		
		// Generate JWT token and store in session
		String accessToken = jwtService.generateAccessToken(user);
		String refreshToken = jwtService.generateRefreshToken(user);
		
		session.setAttribute("currentUser", user);
		session.setAttribute("jwt_token", accessToken);
		
		// Store tokens in cookies
		Cookie accessTokenCookie = new Cookie("jwt_token", accessToken);
		accessTokenCookie.setPath("/");
		accessTokenCookie.setHttpOnly(true);
		accessTokenCookie.setMaxAge(30 * 60); // 30 minutes
		response.addCookie(accessTokenCookie);
		response.addHeader("Set-Cookie", String.format("jwt_token=%s; Max-Age=%d; Path=/; HttpOnly; SameSite=Lax", accessToken, 30*60));
		
		Cookie refreshTokenCookie = new Cookie("jwt_refresh_token", refreshToken);
		refreshTokenCookie.setPath("/");
		refreshTokenCookie.setHttpOnly(true);
		refreshTokenCookie.setMaxAge(7 * 24 * 60 * 60); // 7 days
		response.addCookie(refreshTokenCookie);
		response.addHeader("Set-Cookie", String.format("jwt_refresh_token=%s; Max-Age=%d; Path=/; HttpOnly; SameSite=Lax", refreshToken, 7*24*60*60));
		
		return user;
	}

	public void logout() {
		// Remove JWT cookies
		Cookie accessTokenCookie = new Cookie("jwt_token", null);
		accessTokenCookie.setPath("/");
		accessTokenCookie.setHttpOnly(true);
		accessTokenCookie.setMaxAge(0);
		response.addCookie(accessTokenCookie);
		
		Cookie refreshTokenCookie = new Cookie("jwt_refresh_token", null);
		refreshTokenCookie.setPath("/");
		refreshTokenCookie.setHttpOnly(true);
		refreshTokenCookie.setMaxAge(0);
		response.addCookie(refreshTokenCookie);
		
		session.invalidate();
	}

	public Page<Users> getAllUsers(int pageNumber, int limit) {
		PageRequest pageable = PageRequest.of(pageNumber, limit);
		return usersRepository.findAll(pageable);
	}

	public Users getUserById(String email) {
		return usersRepository.findById(email)
				.orElseThrow(() -> new IllegalArgumentException("Tài khoản không tồn tại!"));
	}

	public void deleteUser(String id) {
		if (!usersRepository.existsById(id)) {
			throw new RuntimeException("Người dùng không tồn tại!");
		}
		hoaDonChiTietRepository.deleteByUserId(id);
		hoaDonRepository.deleteByUserId(id);
		gioHangChiTietRepository.deleteByUserId(id);
		gioHangRepository.deleteByUserId(id);
		usersRepository.deleteById(id);
	}

	public void activeAccount(String idUser) {
		try {
			Users users = getUserById(idUser);
			emailService.sendEmailAcctiveAccount(idUser, "Thư Kích Hoạt Tài Khoản",
					jwtService.generateTokenUser(users));
		} catch (Exception e) {
			throw new RuntimeException("Đã có lỗi xảy ra trong quá trình gửi mail!");
		}
	}

	public void checkToken(String token) {
		if (!jwtService.validateToken(token)) {
			throw new IllegalArgumentException("Token không hợp lệ hoặc đã hết hạn!");
		}
		
		String idUser = jwtService.extractUsername(token);
		Users users = getUserById(idUser);
		users.setKichhoat(true);
		usersRepository.save(users);
	}

	public void canceAccount(String idUser) {
		Users users = getUserById(idUser);
		users.setKichhoat(false);
		usersRepository.save(users);
		logout();
	}

	public void sendMailPass(String id) {
		Optional<Users> userOptional = usersRepository.findById(id);
		if (userOptional.isEmpty()) {
			throw new IllegalArgumentException("Email không tồn tại!");
		}
		try {
			Users user = userOptional.get();
			String resetToken = jwtService.generatePasswordResetToken(user);
			emailService.sendPasswordResetEmail(id, "Yêu cầu đặt lại mật khẩu", resetToken);
		} catch (Exception e) {
			throw new IllegalArgumentException("Đã xảy ra lỗi trong quá trình gửi email!");
		}
	}

	public void resetPassword(String token, String newPassword) {
		if (!jwtService.validateToken(token)) {
			throw new IllegalArgumentException("Token không hợp lệ hoặc đã hết hạn!");
		}
		
		String idUser = jwtService.extractUsername(token);
		Users user = getUserById(idUser);
		user.setMatkhau(passwordEncoder.encode(newPassword));
		usersRepository.save(user);
	}

	public Page<ReportKhachHangVip> getTop10KhachHangVip() {
		PageRequest pageable = PageRequest.of(0, 10);
		return usersRepository.getTop10KhachHangVip(pageable);
	}
	
	// Method to refresh tokens
	public String refreshAccessToken(String refreshToken) {
		if (!jwtService.validateToken(refreshToken)) {
			throw new IllegalArgumentException("Refresh token không hợp lệ hoặc đã hết hạn!");
		}
		
		String username = jwtService.extractUsername(refreshToken);
		Users user = getUserById(username);
		
		String newAccessToken = jwtService.generateAccessToken(user);
		
		// Update access token cookie
		Cookie accessTokenCookie = new Cookie("jwt_token", newAccessToken);
		accessTokenCookie.setPath("/");
		accessTokenCookie.setHttpOnly(true);
		accessTokenCookie.setMaxAge(30 * 60); // 30 minutes
		response.addCookie(accessTokenCookie);
		response.addHeader("Set-Cookie", String.format("jwt_token=%s; Max-Age=%d; Path=/; HttpOnly; SameSite=Lax", newAccessToken, 30*60));
		
		return newAccessToken;
	}
}
