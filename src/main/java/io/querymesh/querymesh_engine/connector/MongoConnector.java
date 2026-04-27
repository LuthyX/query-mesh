package io.querymesh.querymesh_engine.connector;


import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import io.querymesh.querymesh_engine.model.QueryResult;
import org.bson.Document;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MongoConnector implements Connector {

    private final String name;
    private final MongoClient mongoClient;
    private final String databaseName;


    public MongoConnector(String name, String url, String databaseName) {
        this.name = name;
        this.mongoClient = MongoClients.create(url);
        this.databaseName = databaseName;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public QueryResult execute(String collectionName) throws Exception {
        long start = System.currentTimeMillis();
        MongoDatabase db = mongoClient.getDatabase(databaseName);
        MongoCollection<Document> collection = db.getCollection(collectionName);
        List<Map<String, Object>> rows = new ArrayList<>();
        for (Document doc : collection.find()) {
            rows.add(new HashMap<>(doc));
        }

        long elapsed = System.currentTimeMillis() - start;
        return new QueryResult(rows, elapsed);

    }

    @Override
    public List<String> listTables() throws Exception {
        MongoDatabase db = mongoClient.getDatabase(databaseName);
        List<String> collections = new ArrayList<>();
        db.listCollectionNames().forEach(collections::add);
        return collections;
    }

    @Override
    public boolean testConnection() {
        try {
            mongoClient.getDatabase(databaseName).runCommand(new Document("ping", 1));
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
