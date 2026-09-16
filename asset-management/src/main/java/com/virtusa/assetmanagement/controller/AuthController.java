package com.virtusa.assetmanagement.controller;

import com.virtusa.assetmanagement.entity.LoginInfo;
import com.virtusa.assetmanagement.repository.LoginInfoRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Replaces Login_servlet.java and RegisterServlet.java, plus the tiny
 * scriptlet block login.html had for handling ?logout=true.
 *
 * URL paths are kept identical to the original project
 * (/login.html, /Login_servlet, /register.html, /RegisterServlet) so
 * every hardcoded link/form action in the pages keeps working unchanged.
 */
@Controller
public class AuthController {

    private final LoginInfoRepository loginInfoRepository;

    public AuthController(LoginInfoRepository loginInfoRepository) {
        this.loginInfoRepository = loginInfoRepository;
    }

    @GetMapping("/login.html")
    public String loginPage(@RequestParam(required = false) String logout,
                             @RequestParam(required = false) String error,
                             @RequestParam(required = false) String registered,
                             HttpServletRequest request,
                             Model model) {

        if ("true".equals(logout)) {
            HttpSession session = request.getSession(false);
            if (session != null) {
                session.invalidate();
            }
        }

        model.addAttribute("error", error);
        model.addAttribute("registered", registered);
        return "login";
    }

    @PostMapping("/Login_servlet")
    public String login(@RequestParam String username,
                         @RequestParam String password,
                         @RequestParam String role,
                         HttpServletRequest request) {

        if (username == null || password == null || role == null
                || username.trim().isEmpty() || password.trim().isEmpty()) {
            return "redirect:/login.html?error=invalid";
        }

        // Same WHERE username = ? AND password = ? AND role = ? filter
        // the original Login_servlet used.
        return loginInfoRepository.findByUsernameAndPasswordAndRole(username.trim(), password, role)
                .map(loginInfo -> {
                    HttpSession session = request.getSession(true);
                    session.setAttribute("username", loginInfo.getUsername());
                    session.setAttribute("role", loginInfo.getRole());
                    return "admin".equals(loginInfo.getRole())
                            ? "redirect:/admin_dashboard.html"
                            : "redirect:/employee_dashboard.html";
                })
                .orElse("redirect:/login.html?error=invalid");
    }

    @GetMapping("/register.html")
    public String registerPage(@RequestParam(required = false) String error, Model model) {
        model.addAttribute("error", error);
        return "register";
    }

    @PostMapping("/RegisterServlet")
    public String register(@RequestParam String username,
                            @RequestParam String password,
                            @RequestParam String confirmPassword,
                            @RequestParam String role) {

        if (username == null || password == null || confirmPassword == null || role == null
                || username.trim().isEmpty() || password.trim().isEmpty()) {
            return "redirect:/register.html?error=invalid";
        }

        String trimmedUsername = username.trim();

        if (!password.equals(confirmPassword)) {
            return "redirect:/register.html?error=mismatch";
        }

        if (loginInfoRepository.findByUsername(trimmedUsername).isPresent()) {
            return "redirect:/register.html?error=exists";
        }

        try {
            LoginInfo loginInfo = new LoginInfo();
            loginInfo.setUsername(trimmedUsername);
            loginInfo.setPassword(password);
            loginInfo.setRole(role);
            loginInfoRepository.save(loginInfo);
        } catch (DataIntegrityViolationException e) {
            // Same backstop the original relied on: the UNIQUE constraint
            // on username catches a race between two simultaneous sign-ups.
            return "redirect:/register.html?error=exists";
        }

        return "redirect:/login.html?registered=true";
    }

    @GetMapping("/RegisterServlet")
    public String registerGet() {
        return "redirect:/register.html";
    }
}
