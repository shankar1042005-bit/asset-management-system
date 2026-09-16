package com.virtusa.assetmanagement.repository;

import com.virtusa.assetmanagement.entity.LoginInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LoginInfoRepository extends JpaRepository<LoginInfo, Long> {

    // Same WHERE username = ? AND password = ? AND role = ? filter
    // Login_servlet.java used to authenticate a user.
    Optional<LoginInfo> findByUsernameAndPasswordAndRole(String username, String password, String role);

    // Same SELECT username FROM Login_info WHERE username = ? check
    // RegisterServlet.java used to reject duplicate sign-ups.
    Optional<LoginInfo> findByUsername(String username);
}
