package com.logiflow.api.LogiFlow.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import javax.sql.DataSource;
import java.net.URI;

@Configuration
@Profile("prod")
public class DataSourceConfig {

    @Value("${DATABASE_URL:}")
    private String databaseUrl;

    @Bean
    public DataSource dataSource() throws Exception {
        if (databaseUrl == null || databaseUrl.isBlank()) {
            throw new IllegalStateException(
                "A variável DATABASE_URL não está configurada. " +
                "Verifique se a variável DATABASE_URL está configurada no serviço de deploy."
            );
        }

        // Remove prefixo jdbc: se presente para parsear como URI padrão
        String uriString = databaseUrl.startsWith("jdbc:") ? databaseUrl.substring(5) : databaseUrl;
        URI dbUri = new URI(uriString);

        String host = dbUri.getHost();
        int port = dbUri.getPort() > 0 ? dbUri.getPort() : 5432;
        String rawPath = dbUri.getPath();
        if (rawPath == null || rawPath.isBlank()) {
            throw new IllegalStateException(
                "DATABASE_URL inválida: falta o nome do banco. Formato esperado: postgresql://user:senha@host:porta/nome_banco"
            );
        }
        String dbName = rawPath.replaceFirst("/", "");
        String userInfo = dbUri.getUserInfo();
        String username = userInfo.split(":")[0];
        String password = userInfo.substring(username.length() + 1);

        String jdbcUrl = String.format(
            "jdbc:postgresql://%s:%d/%s?sslmode=disable", host, port, dbName
        );

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(jdbcUrl);
        config.setUsername(username);
        config.setPassword(password);
        config.setMaximumPoolSize(5);
        config.setConnectionTimeout(30000);

        return new HikariDataSource(config);
    }
}
