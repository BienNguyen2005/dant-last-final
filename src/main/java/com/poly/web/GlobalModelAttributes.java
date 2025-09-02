package com.poly.web;

import com.poly.entity.Users;
import com.poly.repository.UsersRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

/**
 * Exposes the authenticated Users entity (if any) to all Thymeleaf model contexts as 'currentUser'.
 * Now resolves via SecurityContext principal (username) instead of HttpSession to align with
 * stateless JWT strategy.
 */
@ControllerAdvice
@Component
public class GlobalModelAttributes {
    private final UsersRepository usersRepository;

    public GlobalModelAttributes(UsersRepository usersRepository) {
        this.usersRepository = usersRepository;
    }

    @ModelAttribute("currentUser")
    public Users exposeCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getPrincipal() == null || "anonymousUser".equals(auth.getPrincipal())) {
            return null;
        }
        String username;
        Object principal = auth.getPrincipal();
        if (principal instanceof org.springframework.security.core.userdetails.User userDetails) {
            username = userDetails.getUsername();
        } else if (principal instanceof String s) {
            username = s; // JwtAuthenticationFilter may have set principal to username String
        } else {
            return null;
        }
        return usersRepository.findById(username).orElse(null);
    }
}
