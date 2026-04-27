package io.querymesh.querymesh_engine.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConfigurationProperties(prefix = "querymesh")
public class DataSourceProperties {
    private List<SourceConfig> sources;

    public List<SourceConfig> getSources() {
        return sources;
    }

    public void setSources(List<SourceConfig> sources) {
        this.sources = sources;
    }

    public static class SourceConfig {
        private String name;
        private String type;
        private String url;
        private String username;
        private String password;
        private String database; // for MongoDB

        public String getName() { return name; }
        public String getType() { return type; }
        public String getUrl() { return url; }
        public String getUsername() { return username; }
        public String getPassword() { return password; }
        public String getDatabase() { return database; }

        public void setName(String name) { this.name = name; }
        public void setType(String type) { this.type = type; }
        public void setUrl(String url) { this.url = url; }
        public void setUsername(String username) { this.username = username; }
        public void setPassword(String password) { this.password = password; }
        public void setDatabase(String database) { this.database = database; }
    }
}
