package view.entities.layer;

public interface Layer<Neuron extends view.entities.neuron.Neuron> {
    double[] prediction(double... input);
    double[] inputError(double... error);

    void train(double[] input, double[] error);

    Neuron getNeuron(int index);

    int features();
    int neurons();
}
