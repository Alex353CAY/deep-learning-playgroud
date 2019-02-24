package utils.math.activation;

public class Linear implements Activation {
    public double value(double input) {
        return input;
    }

    public double derivative(double input) {
        return 1;
    }
}
