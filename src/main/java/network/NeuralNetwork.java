package network;

public interface NeuralNetwork {
    int features();
    int labels();
    double[] prediction(double... input);
    void train(double[] input, double[] error);
}
