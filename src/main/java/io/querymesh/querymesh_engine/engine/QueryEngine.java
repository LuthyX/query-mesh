package io.querymesh.querymesh_engine.engine;

import io.querymesh.querymesh_engine.connector.Connector;
import io.querymesh.querymesh_engine.registry.ConnectorRegistry;
import io.querymesh.querymesh_engine.model.QueryResult;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class QueryEngine {
    private final QueryPlanner planner;
    private final ConnectorRegistry registry;
    private final ParallelExecutor parallelExecutor;


    public QueryEngine(QueryPlanner planner, ConnectorRegistry registry, ParallelExecutor parallelExecutor) {
        this.planner = planner;
        this.registry = registry;
        this.parallelExecutor = parallelExecutor;
    }

    public QueryResult execute(String sql) throws Exception {
        // Step 1 — plan
        Map<String, String> plan = planner.plan(sql);
        System.out.println("Query plan: " + plan);

        // Step 2 — execute in parallel
        return parallelExecutor.executeAll(plan);
    }
}
