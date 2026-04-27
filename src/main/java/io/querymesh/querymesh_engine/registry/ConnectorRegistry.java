package io.querymesh.querymesh_engine.registry;

import io.querymesh.querymesh_engine.connector.Connector;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
public class ConnectorRegistry {

    private final Map<String, Connector> connectors = new HashMap<>();

    public void register(Connector connector){
        connectors.put(connector.getName(), connector);
        System.out.println("Registered connector: " + connector.getName());
    }

    public Optional<Connector> getConnector(String name){
        return Optional.ofNullable(connectors.get(name));
    }

    public Collection<Connector> getAllConnectors(){
        return connectors.values();
    }

    public boolean hasConnector(String name){
        return connectors.containsKey(name);
    }


}
