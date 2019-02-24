package utils.math.activation;

public class Sigmoid implements Activation {
    public double value(double input) {
        return 1/(1 + Math.exp(-input));
    }

    public double derivative(double input) {
        final double value = value(input);
        return value * (1 - value);
    }
}
