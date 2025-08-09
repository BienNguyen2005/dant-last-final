package com.poly.service;

import java.util.Collections;
import java.util.Optional;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.poly.entity.Users;
import com.poly.repository.UsersRepository;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UsersRepository userRepository;

    public UserDetailsServiceImpl(UsersRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Users> userOptional = userRepository.findById(username);
        
        if (userOptional.isEmpty()) {
            throw new UsernameNotFoundException("Không tìm thấy người dùng với tên: " + username);
        }
        
        Users user = userOptional.get();
        
        if (!user.isKichhoat()) {
            throw new UsernameNotFoundException("Tài khoản chưa được kích hoạt");
        }
        
        String role = user.isVaitro() ? "ADMIN" : "USER";
        
        return new User(
            user.getIdUser(),
            user.getMatkhau(),
            user.isKichhoat(),
            true,
            true,
            true,
            Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role))
        );
    }
}
