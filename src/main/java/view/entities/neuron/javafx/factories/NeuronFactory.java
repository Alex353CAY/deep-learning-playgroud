package view.entities.neuron.javafx.factories;

import javafx.beans.binding.DoubleBinding;

public interface NeuronFactory<Neuron extends view.entities.neuron.javafx.Neuron> {
    Neuron create(DoubleBinding translateX, DoubleBinding translateY);
}
