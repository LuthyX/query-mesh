package io.querymesh.querymesh_engine.connector;

import io.querymesh.querymesh_engine.model.QueryResult;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;

public class MySQLConnector implements Connector{

    private final String name;
    private final JdbcTemplate jdbcTemplate;

    public MySQLConnector(String name, String url, String username, String password) {
        this.name = name;
        this.jdbcTemplate = new JdbcTemplate(buildDataSource(url, username, password));
    }

    private DataSource buildDataSource(String url, String username, String password){
        DriverManagerDataSource ds = new DriverManagerDataSource();
        ds.setDriverClassName("com.mysql.cj.jdbc.Driver");
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
    public QueryResult execute(String query) throws Exception {
        long start = System.currentTimeMillis();
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(query);
        long elapsed = System.currentTimeMillis() - start;
        return new QueryResult(rows, elapsed);
    }

    @Override
    public List<String> listTables() throws Exception {
        return jdbcTemplate.queryForList(
                "SELECT table_name FROM information_schema.tables " +
                        "WHERE table_schema = DATABASE()",
                String.class
        );
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
