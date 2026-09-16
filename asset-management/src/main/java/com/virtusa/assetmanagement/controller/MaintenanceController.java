package com.virtusa.assetmanagement.controller;

import com.virtusa.assetmanagement.entity.AssetMaintenance;
import com.virtusa.assetmanagement.repository.AssetMaintenanceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;

/**
 * Replaces the Java block inside maintenance_request.jsp (which used to
 * open its own raw JDBC connection and CREATE TABLE IF NOT EXISTS on
 * every submit). Table creation is now handled once by
 * spring.jpa.hibernate.ddl-auto=update.
 */
@Controller
public class MaintenanceController {

    private static final Logger log = LoggerFactory.getLogger(MaintenanceController.class);

    private final AssetMaintenanceRepository assetMaintenanceRepository;

    public MaintenanceController(AssetMaintenanceRepository assetMaintenanceRepository) {
        this.assetMaintenanceRepository = assetMaintenanceRepository;
    }

    @GetMapping("/maintenance_request.jsp")
    public String showForm() {
        return "maintenance_request";
    }

    @PostMapping("/maintenance_request.jsp")
    public String submit(@RequestParam String assetId,
                          @RequestParam String assetName,
                          @RequestParam String category,
                          @RequestParam(required = false) String brand,
                          @RequestParam("model") String assetModel,
                          @RequestParam(required = false) String serialNumber,
                          @RequestParam(required = false) String location,
                          @RequestParam(required = false) String status,
                          @RequestParam(required = false) String description,
                          Model model) {

        String message;

        try {
            AssetMaintenance maintenance = new AssetMaintenance();
            maintenance.setAssetId(assetId);
            maintenance.setAssetName(assetName);
            maintenance.setCategory(category);
            maintenance.setBrand(brand);
            maintenance.setModel(assetModel);
            maintenance.setSerialNumber(serialNumber);
            maintenance.setLocation(location);
            maintenance.setStatus(status);
            maintenance.setDescription(description);
            maintenance.setRequestedOn(LocalDateTime.now());

            assetMaintenanceRepository.save(maintenance);

            message = "Maintenance request submitted successfully and saved to database!";

        } catch (Exception e) {
            log.error("Error while saving maintenance request", e);
            message = "Error while saving maintenance request: " + e.getMessage();
        }

        model.addAttribute("message", message);
        return "maintenance_request";
    }
}
