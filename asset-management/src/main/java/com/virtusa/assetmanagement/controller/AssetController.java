package com.virtusa.assetmanagement.controller;

import com.virtusa.assetmanagement.entity.Asset;
import com.virtusa.assetmanagement.repository.AssetRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;

/**
 * Replaces AssetRegistrationServlet.java, AssetDeleteServlet.java and the
 * Java block inside asset_reg.jsp. URL paths kept identical to the
 * original (/asset_reg.jsp, /AssetRegistrationServlet,
 * /AssetDeleteServlet) so admin_dashboard.html's links and the JSP's
 * own form action work without any changes.
 */
@Controller
public class AssetController {

    private static final Logger log = LoggerFactory.getLogger(AssetController.class);

    private final AssetRepository assetRepository;

    public AssetController(AssetRepository assetRepository) {
        this.assetRepository = assetRepository;
    }

    @GetMapping("/asset_reg.jsp")
    public String showAssetRegistration(Model model) {
        model.addAttribute("assetList", assetRepository.findAll());
        return "asset_reg";
    }

    @PostMapping("/AssetRegistrationServlet")
    public String registerAsset(@RequestParam String assetId,
                                 @RequestParam String assetName,
                                 @RequestParam String category,
                                 @RequestParam(required = false) String brand,
                                 @RequestParam("model") String assetModel,
                                 @RequestParam(required = false) String serialNumber,
                                 @RequestParam String purchaseDate,
                                 @RequestParam String purchaseCost,
                                 @RequestParam(required = false) String location,
                                 @RequestParam(required = false) String warrantyExpiry,
                                 @RequestParam(required = false) String status,
                                 @RequestParam(required = false) String description,
                                 Model model) {

        log.info("Received Asset ID = [{}]", assetId);

        // -----------------------------------
        // CHECK DUPLICATE ASSET ID (same check AssetRegistrationServlet did)
        // -----------------------------------
        if (assetRepository.existsById(assetId)) {
            model.addAttribute("title", "Duplicate Asset ID");
            model.addAttribute("heading", "Asset ID already exists!");
            model.addAttribute("message", "Asset ID " + assetId + " is already registered.");
            model.addAttribute("backLink", "/asset_reg.jsp");
            model.addAttribute("backText", "Back to Asset Registration");
            return "message";
        }

        try {
            Asset asset = new Asset();
            asset.setAssetId(assetId);
            asset.setAssetName(assetName);
            asset.setCategory(category);
            asset.setBrand(brand);
            asset.setModel(assetModel);
            asset.setSerialNumber(serialNumber);
            asset.setPurchaseDate(LocalDate.parse(purchaseDate));
            asset.setPurchaseCost(Double.parseDouble(purchaseCost));
            asset.setLocation(location);

            if (warrantyExpiry != null && !warrantyExpiry.trim().isEmpty()) {
                asset.setWarrantyExpiry(LocalDate.parse(warrantyExpiry));
            }

            asset.setStatus(status);
            asset.setDescription(description);

            assetRepository.save(asset);
            log.info("Asset registered successfully: {}", assetId);

        } catch (Exception e) {
            log.error("Error while registering asset", e);
            model.addAttribute("title", "Error");
            model.addAttribute("heading", "Error while registering asset");
            model.addAttribute("message", e.getMessage());
            model.addAttribute("backLink", "/asset_reg.jsp");
            model.addAttribute("backText", "Back to Asset Registration");
            return "message";
        }

        // Same as the original: display the updated list on the same page.
        model.addAttribute("assetList", assetRepository.findAll());
        return "asset_reg";
    }

    @GetMapping("/AssetDeleteServlet")
    public String deleteAsset(@RequestParam(required = false) String assetId) {
        log.info("Deleting Asset ID: {}", assetId);

        if (assetId != null && !assetId.trim().isEmpty()) {
            assetRepository.findById(assetId).ifPresent(assetRepository::delete);
        }

        return "redirect:/asset_reg.jsp";
    }
}
