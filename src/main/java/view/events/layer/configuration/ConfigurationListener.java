package view.events.layer.configuration;

public interface ConfigurationListener<Connection extends view.entities.connection.Connection> {
    void onConnectionAdded(int featureIndex, int neuronIndex, Connection connection);
    void onConnectionRemoved(int featureIndex, int neuronIndex, Connection connection);
}
