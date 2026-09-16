package com.virtusa.assetmanagement.config;

import com.virtusa.assetmanagement.entity.LoginInfo;
import com.virtusa.assetmanagement.repository.LoginInfoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * Runs once, automatically, when the application starts - same job the
 * original AppInitListener.java did. Table creation is now handled by
 * spring.jpa.hibernate.ddl-auto=update, so this class only has to seed
 * the two default accounts so the app is usable immediately:
 *
 *      username: admin        password: admin123     role: admin
 *      username: student001   password: student123   role: student
 */
@Component
public class DataSeeder implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

    private final LoginInfoRepository loginInfoRepository;

    public DataSeeder(LoginInfoRepository loginInfoRepository) {
        this.loginInfoRepository = loginInfoRepository;
    }

    @Override
    public void run(ApplicationArguments args) {
        seedIfMissing("admin", "admin123", "admin");
        seedIfMissing("student001", "student123", "student");
        log.info("DataSeeder: Login_info verified / seeded successfully.");
    }

    private void seedIfMissing(String username, String password, String role) {
        if (loginInfoRepository.findByUsername(username).isEmpty()) {
            LoginInfo li = new LoginInfo();
            li.setUsername(username);
            li.setPassword(password);
            li.setRole(role);
            loginInfoRepository.save(li);
        }
    }
}
