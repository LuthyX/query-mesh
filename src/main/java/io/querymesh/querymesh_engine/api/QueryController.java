package io.querymesh.querymesh_engine.api;

import io.querymesh.querymesh_engine.registry.ConnectorRegistry;
import io.querymesh.querymesh_engine.engine.QueryEngine;
import io.querymesh.querymesh_engine.model.QueryRequest;
import io.querymesh.querymesh_engine.model.QueryResult;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class QueryController {

    private final QueryEngine queryEngine;
    private final ConnectorRegistry registry;

    public QueryController(QueryEngine queryEngine, ConnectorRegistry registry) {
        this.queryEngine = queryEngine;
        this.registry = registry;
    }

    @PostMapping("/query")
    public ResponseEntity<?> executeQuery(@RequestBody QueryRequest request) {
        try {
            QueryResult result = queryEngine.execute(request.getSql());
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Query failed: " + e.getMessage());
        }
    }

    @GetMapping("/tables")
    public ResponseEntity<?> listTables() {
        try {
            var tables = registry.getAllConnectors()
                    .stream()
                    .flatMap(c -> {
                        try {
                            return c.listTables().stream()
                                    .map(t -> c.getName() + "." + t);
                        } catch (Exception e) {
                            return java.util.stream.Stream.empty();
                        }
                    })
                    .toList();
            return ResponseEntity.ok(tables);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/health")
    public ResponseEntity<?> health() {
        var status = registry.getAllConnectors()
                .stream()
                .collect(java.util.stream.Collectors.toMap(
                        c -> c.getName(),
                        c -> c.testConnection() ? "connected" : "disconnected"
                ));
        return ResponseEntity.ok(status);
    }
}