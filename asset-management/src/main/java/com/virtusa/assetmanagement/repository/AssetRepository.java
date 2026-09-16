package com.virtusa.assetmanagement.repository;

import com.virtusa.assetmanagement.entity.Asset;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AssetRepository extends JpaRepository<Asset, String> {
}
