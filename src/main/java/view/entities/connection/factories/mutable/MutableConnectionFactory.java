package view.entities.connection.factories.mutable;

import view.entities.connection.MutableConnection;
import view.entities.neuron.javafx.Neuron;

public interface MutableConnectionFactory<Connection extends MutableConnection> {
    Connection create(Neuron source, Neuron target, double initialWeight);
}
