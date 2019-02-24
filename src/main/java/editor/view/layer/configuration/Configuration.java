package editor.view.layer.configuration;

import editor.view.layer.ConnectionFactory;
import view.entities.layer.configuration.ObservableConfiguration;

public class Configuration<Connection extends view.entities.connection.javafx.Connection> extends ObservableConfiguration<Connection> {
    private final ConnectionFactory<Connection> connectionFactory;

    public Configuration(ConnectionFactory<Connection> connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    protected Connection create(int featureIndex, int neuronIndex) {
        return connectionFactory.create(featureIndex, neuronIndex);
    }
}
