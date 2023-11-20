package app.core.config;

import org.eclipse.microprofile.config.inject.ConfigProperties;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ConfigProperties(prefix = "quarkus.datasource.jdbc")
public class DatabaseConfig {

    @ConfigProperty(name = "url")
    String jdbcUrl;

    public String getJdbcUrl() {
        return jdbcUrl;
    }
}