package io.querymesh.querymesh_engine.connector;

import io.querymesh.querymesh_engine.model.QueryResult;

import java.util.List;

public interface Connector {

    String getName();

    QueryResult execute(String sql) throws Exception;

    List<String> listTables() throws Exception;

    boolean testConnection();

}
