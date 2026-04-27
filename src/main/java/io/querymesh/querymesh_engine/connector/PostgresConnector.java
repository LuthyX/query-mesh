package io.querymesh.querymesh_engine.connector;

import io.querymesh.querymesh_engine.model.QueryResult;
import lombok.Data;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import java.util.List;
import java.util.Map;


public class PostgresConnector implements Connector{

    private final String name;
    private final JdbcTemplate jdbcTemplate;

    public PostgresConnector(String name, String url, String username, String password) {
        this.name = name;
        this.jdbcTemplate = new JdbcTemplate(buildDataSource(url, username, password));
    }

    private DriverManagerDataSource buildDataSource(String url, String username, String password){
        DriverManagerDataSource ds = new DriverManagerDataSource();
        ds.setDriverClassName("org.postgresql.Driver");
        ds.setUrl(url);
        ds.setUsername(username);
        ds.setPassword(password);
        return ds;
    }


    @Override
    public String getName() {
        return name;
    }

    @Override
    public QueryResult execute(String sql) throws Exception {
       long start = System.currentTimeMillis();
       List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
       long elapsed = System.currentTimeMillis() - start;
       return new QueryResult(rows, elapsed);
    }

    @Override
    public List<String> listTables() throws Exception {
        String sql = """
            SELECT table_name
            FROM information_schema.tables
            WHERE table_schema = 'public'
            ORDER BY table_name
            """;
        return jdbcTemplate.queryForList(sql, String.class);
    }

    @Override
    public boolean testConnection() {
        try {
            jdbcTemplate.queryForObject("SELECT 1", Integer.class);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
