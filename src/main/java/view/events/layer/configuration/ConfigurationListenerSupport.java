package view.events.layer.configuration;

public interface ConfigurationListenerSupport<Connection extends view.entities.connection.Connection> {
    void subscribe(ConfigurationListener<Connection> listener);
}
