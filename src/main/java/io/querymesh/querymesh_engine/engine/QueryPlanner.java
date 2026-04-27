package io.querymesh.querymesh_engine.engine;

import io.querymesh.querymesh_engine.registry.ConnectorRegistry;
import io.querymesh.querymesh_engine.registry.SchemaRegistry;
import org.apache.calcite.sql.SqlIdentifier;
import org.apache.calcite.sql.SqlJoin;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.SqlSelect;
import org.apache.calcite.sql.parser.SqlParseException;
import org.apache.calcite.sql.parser.SqlParser;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class QueryPlanner {
    private final ConnectorRegistry connectorRegistry;
    private final SchemaRegistry schemaRegistry;

    public QueryPlanner(ConnectorRegistry registry, SchemaRegistry schemaRegistry) {
        this.connectorRegistry = registry;
        this.schemaRegistry = schemaRegistry;
    }

    public Map<String, String> plan(String sql) throws SqlParseException {
        SqlParser parser = SqlParser.create(sql);
        SqlNode parsed = parser.parseQuery();

        Map<String, String> plan  =  new HashMap<>();
        extractTables(parsed, plan);
        return plan;
    }

    private void extractTables(SqlNode node, Map<String, String> plan) {
        if (node instanceof SqlSelect select) {
            extractTables(select.getFrom(), plan);

        } else if (node instanceof SqlJoin join) {
            extractTables(join.getLeft(), plan);
            extractTables(join.getRight(), plan);

        } else if (node instanceof SqlIdentifier identifier) {

            if (identifier.names.size() == 2) {
                // Explicit prefix: "postgres.users"
                String connectorName = identifier.names.get(0);
                String tableName     = identifier.names.get(1);

                if (connectorRegistry.hasConnector(connectorName)) {
                    plan.put(connectorName, tableName);
                }

            } else if (identifier.names.size() == 1) {
                // No prefix: "users" — look it up in schema registry
                String tableName = identifier.names.get(0);

                schemaRegistry.findConnector(tableName)
                        .ifPresent(connectorName ->
                                plan.put(connectorName, tableName)
                        );
            }
        }
    }
}
