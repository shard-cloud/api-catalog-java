package com.example.catalog.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;

import javax.sql.DataSource;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.github.cdimascio.dotenv.Dotenv;
import java.net.URI;
import java.net.URISyntaxException;

@Configuration
public class DatabaseConfig {

    @Value("${DATABASE:}")
    private String databaseUrl;

    @Bean
    @Primary
    public DataSource dataSource() {
        // Load .env file
        Dotenv dotenv = Dotenv.configure().load();
        String envDatabaseUrl = dotenv.get("DATABASE");
        
        // Use .env file value if available, otherwise use @Value
        String finalDatabaseUrl = (envDatabaseUrl != null && !envDatabaseUrl.isEmpty()) ? envDatabaseUrl : databaseUrl;
        
        HikariConfig config = new HikariConfig();
        
        try {
            // Parse the URL to extract components
            URI uri = new URI(finalDatabaseUrl);
            String host = uri.getHost();
            int port = uri.getPort();
            String path = uri.getPath();
            String userInfo = uri.getUserInfo();
            
            // Build JDBC URL without credentials
            String jdbcUrl = "jdbc:postgresql://" + host + ":" + port + path;
            
            // Add SSL parameter if present
            String query = uri.getQuery();
            if (query != null && query.contains("ssl=true")) {
                jdbcUrl += "?ssl=true&sslmode=require";
            }
            
            config.setJdbcUrl(jdbcUrl);
            config.setDriverClassName("org.postgresql.Driver");
            
            // Set credentials separately
            if (userInfo != null && userInfo.contains(":")) {
                String[] credentials = userInfo.split(":");
                config.setUsername(credentials[0]);
                config.setPassword(credentials[1]);
            }
            
        } catch (URISyntaxException e) {
            // Fallback to simple conversion
            String jdbcUrl = finalDatabaseUrl.replace("postgres://", "jdbc:postgresql://");
            config.setJdbcUrl(jdbcUrl);
            config.setDriverClassName("org.postgresql.Driver");
        }
        
        // Connection pool settings
        config.setMaximumPoolSize(20);
        config.setMinimumIdle(5);
        config.setConnectionTimeout(30000);
        config.setIdleTimeout(600000);
        config.setMaxLifetime(1800000);
        
        return new HikariDataSource(config);
    }

    @Bean
    public WebServerFactoryCustomizer<TomcatServletWebServerFactory> webServerFactoryCustomizer() {
        return factory -> {
            // Load .env file
            Dotenv dotenv = Dotenv.configure().load();
            String portStr = dotenv.get("PORT");
            if (portStr != null) {
                try {
                    int port = Integer.parseInt(portStr);
                    factory.setPort(port);
                } catch (NumberFormatException e) {
                    // Invalid PORT value, use default
                }
            }
        };
    }
}
