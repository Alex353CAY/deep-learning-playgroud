package editor.view.neuron;

import javafx.beans.binding.DoubleBinding;
import javafx.scene.paint.Color;

public class Neuron extends view.entities.neuron.javafx.Neuron {
    private final DoubleBinding mountingPointX = translateXProperty().add(centerXProperty());
    private final DoubleBinding mountingPointY = translateYProperty().add(centerYProperty());
    private double prediction = Double.NaN;

    public Neuron() {
        super(15);
        setFill(Color.WHITE);
        setStrokeWidth(1);
        setStroke(Color.BLACK);
    }

    public double getPrediction() {
        return prediction;
    }

    public void setPrediction(double prediction) {
        this.prediction = prediction;
    }

    @Override
    public DoubleBinding widthProperty() {
        return radiusProperty().multiply(2);
    }

    @Override
    public DoubleBinding heightProperty() {
        return radiusProperty().multiply(2);
    }

    @Override
    public DoubleBinding mountingPointX() {
        return mountingPointX;
    }

    @Override
    public DoubleBinding mountingPointY() {
        return mountingPointY;
    }
}
