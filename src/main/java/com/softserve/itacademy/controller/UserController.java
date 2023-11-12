package com.softserve.itacademy.controller;

import com.softserve.itacademy.model.User;
import com.softserve.itacademy.security.UserDetailsImpl;
import com.softserve.itacademy.service.RoleService;
import com.softserve.itacademy.service.UserService;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final RoleService roleService;

    public UserController(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    @GetMapping("/create")
    public String create(Model model) {
        model.addAttribute("user", new User());
        return "create-user";
    }

    @PostMapping("/create")
    public String create(Model model, @Validated @ModelAttribute("user") User user, BindingResult result) {
        if (result.hasErrors()) {
            return "create-user";
        }
        if (userService.getAll().stream().map(User::getEmail).anyMatch(u -> u.equals(user.getEmail()))) {
            model.addAttribute("error", "This email has already taken.");
            return "create-user";
        }
        else if (user.getFirstName() != "" && user.getLastName() != "" && user.getPassword() != "" && user.getEmail() != "") {
            user.setPassword(user.getPassword());
            user.setRole(roleService.readById(2));
            User newUser = userService.create(user);
            return "redirect:/users/all";
        }
        else {
            model.addAttribute("error", "Please, check if the data are correct.");
            return "create-user";
        }
    }

    @PreAuthorize("hasAuthority('ADMIN') or #id == authentication.principal.id")
    @GetMapping("/{id}/read")
    public String read(@PathVariable long id, Model model) {
        User user = userService.readById(id);
        model.addAttribute("user", user);
        return "user-info";
    }

    @PreAuthorize("hasAuthority('ADMIN') or #id == authentication.principal.id")
    @GetMapping("/{id}/update")
    public String update(@PathVariable long id, Model model) {
        User user = userService.readById(id);
        model.addAttribute("user", user);
        model.addAttribute("roles", roleService.getAll());
        return "update-user";
    }


    @PostMapping("/update")
    public String update(Model model, @Validated @ModelAttribute("user") User user, @RequestParam("roleId") Long roleId, BindingResult result) {
        User oldUser = userService.readById(user.getId());
//        if (result.hasErrors()) {
//            user.setRole(oldUser.getRole());
//            model.addAttribute("roles", roleService.getAll());
//            return "update-user";
//        }
        user.setRole(roleService.readById(roleId));
        user.setPassword(oldUser.getPassword());
        userService.update(user);
        return "redirect:/users/" + user.getId() + "/read";
    }

    @PreAuthorize("hasAuthority('ADMIN') or #id == authentication.principal.id")
    @GetMapping("/{id}/delete")
    public String delete(@PathVariable("id") long id, Authentication authentication) {
        userService.delete(id);
        if (authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ADMIN")))
            return "redirect:/users/all";
        else
            return "redirect:/login-form";
    }

    @GetMapping("/all")
    public String getAll(Model model, Authentication authentication) {
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
        return "users-list";
    }
}
