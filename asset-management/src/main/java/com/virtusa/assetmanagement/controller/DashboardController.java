package com.virtusa.assetmanagement.controller;

import com.virtusa.assetmanagement.repository.AssetAssignedRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Replaces:
 *  - the <% %> block in employee_dashboard.html (session check +
 *    "SELECT ... FROM asset_assigned WHERE student_id = ?")
 *  - the <% %> block in profile.html (session check)
 *  - admin_dashboard.html / asset_registration.html / asset_list.html
 *    (plain static pages in the original - just served as templates)
 *  - index.html's meta-refresh redirect to login.html
 */
@Controller
public class DashboardController {

    private final AssetAssignedRepository assetAssignedRepository;

    public DashboardController(AssetAssignedRepository assetAssignedRepository) {
        this.assetAssignedRepository = assetAssignedRepository;
    }

    @GetMapping("/admin_dashboard.html")
    public String adminDashboard() {
        return "admin_dashboard";
    }

    @GetMapping("/employee_dashboard.html")
    public String employeeDashboard(HttpSession session, Model model) {
        String username = (session != null) ? (String) session.getAttribute("username") : null;

        if (username == null) {
            return "redirect:/login.html?error=session";
        }

        model.addAttribute("username", username);
        model.addAttribute("assignments", assetAssignedRepository.findByStudentIdOrderByAssignedOnDesc(username));
        return "employee_dashboard";
    }

    @GetMapping("/profile.html")
    public String profile(HttpSession session, Model model) {
        String username = (session != null) ? (String) session.getAttribute("username") : null;
        String role = (session != null) ? (String) session.getAttribute("role") : null;

        if (username == null) {
            return "redirect:/login.html?error=session";
        }

        model.addAttribute("username", username);
        model.addAttribute("role", role);
        return "profile";
    }

    @GetMapping("/asset_registration.html")
    public String assetRegistrationStatic() {
        return "asset_registration";
    }

    @GetMapping("/asset_list.html")
    public String assetList() {
        return "asset_list";
    }

    @GetMapping({"/", "/index.html"})
    public String index() {
        return "redirect:/login.html";
    }
}
