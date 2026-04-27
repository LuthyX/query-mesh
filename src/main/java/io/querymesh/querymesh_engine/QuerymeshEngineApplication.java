package io.querymesh.querymesh_engine;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {
        DataSourceAutoConfiguration.class,
})
@EnableConfigurationProperties
public class QuerymeshEngineApplication {

	public static void main(String[] args) {
		SpringApplication.run(QuerymeshEngineApplication.class, args);
	}

}
