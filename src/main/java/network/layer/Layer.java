package network.layer;

import network.NeuralNetwork;

public interface Layer extends NeuralNetwork {
    double[] inputError(double[] error);
}
