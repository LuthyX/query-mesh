package io.querymesh.querymesh_engine.engine;

import io.querymesh.querymesh_engine.connector.Connector;
import io.querymesh.querymesh_engine.model.QueryResult;
import io.querymesh.querymesh_engine.registry.ConnectorRegistry;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

@Component
public class ParallelExecutor {

    private final ConnectorRegistry registry;

    private final ExecutorService threadPool = Executors.newFixedThreadPool(10);

    public ParallelExecutor(ConnectorRegistry registry) {
        this.registry = registry;
    }

    public QueryResult executeAll(Map<String, String> plan) throws Exception {
        long start = System.currentTimeMillis();

        // Step 1 — create a future for each connector in the plan
        Map<String, CompletableFuture<QueryResult>> futures = new HashMap<>();

        for (Map.Entry<String, String> entry : plan.entrySet()) {
            String connectorName = entry.getKey();
            String table         = entry.getValue();

            Connector connector = registry.getConnector(connectorName)
                    .orElseThrow(() -> new Exception(
                            "No connector found for: " + connectorName
                    ));

            // Fire each query on a separate thread — don't wait
            CompletableFuture<QueryResult> future = CompletableFuture
                    .supplyAsync(() -> {
                        try {
                            System.out.println(
                                    "Executing on " + connectorName +
                                            " in thread: " + Thread.currentThread().getName()
                            );
                            return connector.execute(table);
                        } catch (Exception e) {
                            throw new CompletionException(e);
                        }
                    }, threadPool);

            futures.put(connectorName, future);
        }

        // Step 2 — wait for ALL futures to complete
        CompletableFuture.allOf(
                futures.values().toArray(new CompletableFuture[0])
        ).join();

        // Step 3 — collect and merge all results
        List<Map<String, Object>> allRows = new ArrayList<>();

        for (Map.Entry<String, CompletableFuture<QueryResult>> entry
                : futures.entrySet()) {
            try {
                QueryResult result = entry.getValue().get();
                allRows.addAll(result.getRows());
            } catch (ExecutionException e) {
                System.err.println(
                        "Connector " + entry.getKey() +
                                " failed: " + e.getCause().getMessage()
                );
            }
        }

        long elapsed = System.currentTimeMillis() - start;
        return new QueryResult(allRows, elapsed);
    }


}
