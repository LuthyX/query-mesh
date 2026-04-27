package io.querymesh.querymesh_engine.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class QueryRequest {
    private String sql;
}
