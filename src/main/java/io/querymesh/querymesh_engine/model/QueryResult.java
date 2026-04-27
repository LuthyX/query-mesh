package io.querymesh.querymesh_engine.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

import java.util.List;
import java.util.Map;

@Data
@Getter
public class QueryResult {
    private List<Map<String, Object>> rows;
    private int rowCount;
    private long executionTimeMs;

    public QueryResult(List<Map<String, Object>> rows, long executionTimeMs) {
        this.rows = rows;
        this.rowCount = rows.size();
        this.executionTimeMs = executionTimeMs;
    }

}
