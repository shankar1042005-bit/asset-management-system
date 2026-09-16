package com.virtusa.assetmanagement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point. Everything - embedded Tomcat, MVC controllers,
 * Spring Data JPA repositories, Thymeleaf views, the login filter -
 * runs inside this one Spring Boot process. No external Tomcat/NetBeans
 * needed; just run this class (or `mvn spring-boot:run`).
 */
@SpringBootApplication
public class AssetManagementApplication {

    public static void main(String[] args) {
        SpringApplication.run(AssetManagementApplication.class, args);
    }
}
