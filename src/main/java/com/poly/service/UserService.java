package com.poly.service;

import java.util.Objects;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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

	public Users register(Users user) {
		Optional<Users> existingUserById = usersRepository.findById(user.getIdUser());
		if (existingUserById.isPresent()) {
			throw new IllegalArgumentException("Email người dùng đã tồn tại!");
		}

		Optional<Users> existingUserByPhone = usersRepository.findBySdt(user.getSdt());
		if (existingUserByPhone.isPresent()) {
			throw new IllegalArgumentException("Số điện thoại đã được sử dụng!");
		}
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

			return usersRepository.save(user);
		} else {
			throw new RuntimeException("Không tìm thấy người dùng!");
		}
	}

	public Users changePassword(String id, String newPassword) {
		Optional<Users> optionalUser = usersRepository.findById(id);
		if (optionalUser.isPresent()) {
			Users user = optionalUser.get();
			user.setMatkhau(newPassword);
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
			if (image.getSize() > 0) {
				if (Objects.nonNull(user.getHinh()) || user.getHinh() == "") {
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
		if (!user.getMatkhau().equals(users.getMatkhau())) {
			throw new IllegalArgumentException("Tài khoản hoặc mật khẩu không chính xác!");
		}
		session.setAttribute("currentUser", user);
		return user;
	}

	public void logout() {
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
			emailService.sendEmailPassword(id, "Thông Báo Gửi Lại Mật Khẩu", userOptional.get());
		} catch (Exception e) {
			throw new IllegalArgumentException("Đã xảy ra lỗi trong quá trình gửi email!");
		}
	}

	public Page<ReportKhachHangVip> getTop10KhachHangVip() {
		PageRequest pageable = PageRequest.of(0, 10);
		return usersRepository.getTop10KhachHangVip(pageable);
	}
}
