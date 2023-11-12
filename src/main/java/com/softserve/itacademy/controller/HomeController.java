package com.softserve.itacademy.controller;

import com.softserve.itacademy.repository.UserRepository;
import com.softserve.itacademy.security.UserDetailsImpl;
import com.softserve.itacademy.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    private final UserService userService;

    public HomeController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping({"/", "/home" })
    public String home(Model model, Authentication authentication) {
        if (authentication != null && authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ADMIN"))) {
            model.addAttribute("users", userService.getAll());
            return "users-list";
        } else if (authentication != null) {
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            String username = userDetails.getUsername();
            model.addAttribute("user", userService.readByEmail(username));
            return "user-info";
        }
        return "login-page";
    }
}
