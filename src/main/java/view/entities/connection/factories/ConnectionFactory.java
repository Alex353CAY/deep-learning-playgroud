package view.entities.connection.factories;

import view.entities.neuron.javafx.Neuron;

public interface ConnectionFactory<Connection extends view.entities.connection.Connection> {
    Connection create(Neuron source, Neuron target, double initialWeight);
}
