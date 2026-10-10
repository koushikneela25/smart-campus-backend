package com.campus.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;
import java.net.URI;

@Configuration
public class DatabaseConfig {

    private static final Logger log = LoggerFactory.getLogger(DatabaseConfig.class);

    @Value("${spring.datasource.url:}")
    private String rawUrl;

    @Value("${spring.datasource.username:}")
    private String rawUsername;

    @Value("${spring.datasource.password:}")
    private String rawPassword;

    @Bean
    @Primary
    public DataSource dataSource() {
        HikariConfig config = new HikariConfig();
        
        String url = rawUrl != null ? rawUrl.trim() : "";
        String username = rawUsername != null ? rawUsername.trim() : "";
        String password = rawPassword != null ? rawPassword.trim() : "";

        // Check if rawUrl starts with postgres:// or postgresql:// (Render / Railway / Heroku format)
        if (url.startsWith("postgres://") || url.startsWith("postgresql://")) {
            try {
                // Replace scheme temporarily to parse using java.net.URI
                String parseableUri = url.startsWith("postgres://") 
                        ? "http://" + url.substring("postgres://".length())
                        : "http://" + url.substring("postgresql://".length());

                URI uri = new URI(parseableUri);
                String host = uri.getHost();
                int port = uri.getPort() == -1 ? 5432 : uri.getPort();
                String path = uri.getPath(); // e.g. /campus
                String query = uri.getQuery(); // e.g. sslmode=require

                String jdbcUrl = "jdbc:postgresql://" + host + ":" + port + path;
                if (query != null && !query.isEmpty()) {
                    jdbcUrl += "?" + query;
                }

                config.setJdbcUrl(jdbcUrl);

                if (uri.getUserInfo() != null && !uri.getUserInfo().isEmpty()) {
                    String[] userInfo = uri.getUserInfo().split(":", 2);
                    config.setUsername(userInfo[0]);
                    if (userInfo.length > 1) {
                        config.setPassword(userInfo[1]);
                    }
                } else {
                    if (!username.isEmpty()) config.setUsername(username);
                    if (!password.isEmpty()) config.setPassword(password);
                }

                log.info("Configured PostgreSQL DataSource from DATABASE_URL: {}", jdbcUrl);
            } catch (Exception e) {
                log.warn("Failed to parse PostgreSQL DATABASE_URL as URI, using direct jdbc prefix: {}", e.getMessage());
                String fallbackUrl = url.startsWith("jdbc:") ? url : "jdbc:" + url;
                config.setJdbcUrl(fallbackUrl);
                config.setUsername(username);
                config.setPassword(password);
            }
        } else {
            // Standard JDBC URL or fallback
            config.setJdbcUrl(url.isEmpty() ? "jdbc:postgresql://localhost:5432/campus" : url);
            if (!username.isEmpty()) config.setUsername(username);
            if (!password.isEmpty()) config.setPassword(password);
            log.info("Configured PostgreSQL DataSource: {}", config.getJdbcUrl());
        }

        config.setDriverClassName("org.postgresql.Driver");
        config.setMaximumPoolSize(5);
        config.setMinimumIdle(1);
        config.setConnectionTimeout(30000);
        config.setIdleTimeout(600000);
        config.setMaxLifetime(1800000);

        return new HikariDataSource(config);
    }
}
