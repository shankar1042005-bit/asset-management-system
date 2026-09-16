package com.virtusa.assetmanagement.repository;

import com.virtusa.assetmanagement.entity.AssetAssigned;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AssetAssignedRepository extends JpaRepository<AssetAssigned, Long> {

    // Same SELECT ... FROM asset_assigned WHERE student_id = ? ORDER BY assigned_on DESC
    // employee_dashboard.html used to build the student's dashboard table.
    List<AssetAssigned> findByStudentIdOrderByAssignedOnDesc(String studentId);
}
