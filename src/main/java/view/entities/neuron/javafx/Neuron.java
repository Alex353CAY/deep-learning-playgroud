package view.entities.neuron.javafx;

import javafx.beans.binding.DoubleBinding;
import javafx.scene.shape.Circle;

public abstract class Neuron extends Circle implements view.entities.neuron.Neuron {
    public Neuron(double radius) {
        super(radius);
    }

    public abstract DoubleBinding widthProperty();
    public abstract DoubleBinding heightProperty();

    public abstract DoubleBinding mountingPointX();
    public abstract DoubleBinding mountingPointY();
}
