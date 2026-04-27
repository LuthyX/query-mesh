package io.querymesh.querymesh_engine.registry;

import io.querymesh.querymesh_engine.connector.Connector;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
public class SchemaRegistry {
    private final ConnectorRegistry connectorRegistry;

    // table name → connector name
    // e.g. "users" → "postgres", "events" → "mongodb"
    private final Map<String, String> tableIndex = new HashMap<>();

    public SchemaRegistry(ConnectorRegistry connectorRegistry) {
        this.connectorRegistry = connectorRegistry;
    }

    public void buildIndex(){
        for (Connector connector : connectorRegistry.getAllConnectors()) {
            try {
                for (String table : connector.listTables()) {
                    tableIndex.put(table.toLowerCase(), connector.getName());
                    System.out.println(
                            "Indexed: " + table + " → " + connector.getName()
                    );
                }
            } catch (Exception e) {
                System.err.println(
                        "Failed to index tables for: " + connector.getName()
                );
            }
        }
    }

    public Optional<String> findConnector(String tableName) {
        return Optional.ofNullable(tableIndex.get(tableName.toLowerCase()));
    }

    public Map<String, String> getFullIndex() {
        return new HashMap<>(tableIndex);
    }

    public void refresh() {
        tableIndex.clear();
        buildIndex();
    }
}
