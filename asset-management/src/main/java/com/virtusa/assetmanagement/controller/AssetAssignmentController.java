package com.virtusa.assetmanagement.controller;

import com.virtusa.assetmanagement.entity.AssetAssigned;
import com.virtusa.assetmanagement.repository.AssetAssignedRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Replaces the Java block inside asset_assignment.jsp. URL path kept as
 * /asset_assignment.jsp so admin_dashboard.html's link and the page's
 * own form action work unchanged.
 */
@Controller
public class AssetAssignmentController {

    private static final Logger log = LoggerFactory.getLogger(AssetAssignmentController.class);

    private final AssetAssignedRepository assetAssignedRepository;

    public AssetAssignmentController(AssetAssignedRepository assetAssignedRepository) {
        this.assetAssignedRepository = assetAssignedRepository;
    }

    @GetMapping("/asset_assignment.jsp")
    public String showForm() {
        return "asset_assignment";
    }

    @PostMapping("/asset_assignment.jsp")
    public String assign(@RequestParam String assetId,
                          @RequestParam String assetName,
                          @RequestParam String studentId,
                          @RequestParam String studentName,
                          @RequestParam(required = false) String usedate,
                          @RequestParam(required = false) String endDate,
                          Model model) {

        String message;

        try {
            AssetAssigned assigned = new AssetAssigned();
            assigned.setAssetId(assetId);
            assigned.setAssetName(assetName);
            assigned.setStudentId(studentId);
            assigned.setStudentName(studentName);

            if (usedate != null && !usedate.trim().isEmpty()) {
                assigned.setStartDate(LocalDate.parse(usedate));
            }
            if (endDate != null && !endDate.trim().isEmpty()) {
                assigned.setEndDate(LocalDate.parse(endDate));
            }

            assigned.setAssignedOn(LocalDateTime.now());
            assetAssignedRepository.save(assigned);

            message = "Asset assigned successfully! It will now appear on " + studentId + "'s Student Dashboard.";

        } catch (Exception e) {
            log.error("Error while saving assignment", e);
            message = "Error while saving assignment: " + e.getMessage();
        }

        model.addAttribute("message", message);
        return "asset_assignment";
    }
}
