package view.entities.layer.configuration;

import view.entities.connection.javafx.Connection;
import view.entities.layer.javafx.Layer;

public interface ConfigurationFactory<Configuration extends view.entities.layer.configuration.Configuration<? extends Connection>> {
    Configuration build(Layer layer);
}
