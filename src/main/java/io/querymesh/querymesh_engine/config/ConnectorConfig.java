package io.querymesh.querymesh_engine.config;

import io.querymesh.querymesh_engine.connector.MySQLConnector;
import io.querymesh.querymesh_engine.registry.ConnectorRegistry;
import io.querymesh.querymesh_engine.connector.MongoConnector;
import io.querymesh.querymesh_engine.connector.PostgresConnector;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConnectorConfig {
    private final DataSourceProperties dataSourceProperties;
    private final ConnectorRegistry registry;

    public ConnectorConfig(DataSourceProperties dataSourceProperties, ConnectorRegistry connectorRegistry) {
        this.dataSourceProperties = dataSourceProperties;
        this.registry = connectorRegistry;
    }

    @PostConstruct
    public void registerConnectors() {
        for (DataSourceProperties.SourceConfig source : dataSourceProperties.getSources()) {
            switch (source.getType().toLowerCase()) {
                case "postgres" -> registry.register(
                        new PostgresConnector(source.getName(), source.getUrl(), source.getUsername(), source.getPassword())
                );
                case "mongodb" -> registry.register(
                        new MongoConnector(source.getName(), source.getUrl(), source.getDatabase())
                );
                case "mysql" -> registry.register(
                        new MySQLConnector(
                                source.getName(),
                                source.getUrl(),
                                source.getUsername(),
                                source.getPassword()
                        )
                );
                default -> System.out.println(
                        "Unknown connector type: " + source.getType()
                );
            }
        }
    }
}
