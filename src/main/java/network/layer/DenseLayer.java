package network.layer;

import utils.math.activation.Activation;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class DenseLayer implements MutableLayer {
    private final ReentrantReadWriteLock modificationLock = new ReentrantReadWriteLock();

    private Activation activation;
    private int features;
    private int neurons;
    private final List<List<Double>> weights = new ArrayList<>();

    public DenseLayer(Activation activation) {
        this.activation = activation;
    }

    public void setActivation(Activation activation) {
        modificationLock.writeLock().lock();
        this.activation = activation;
        modificationLock.writeLock().unlock();
    }

    @Override
    public void addFeature(int index) {
        modificationLock.writeLock().lock();
        features++;
        for (List<Double> neuronsWeights: weights)
            neuronsWeights.add(index, ThreadLocalRandom.current().nextDouble(-1, 1));
        modificationLock.writeLock().unlock();
    }

    @Override
    public void removeFeature(int index) {
        modificationLock.writeLock().lock();
        features--;
        for (List<Double> neuronsWeights: weights)
            neuronsWeights.remove(index);
        modificationLock.writeLock().unlock();
    }

    @Override
    public void addNeuron(int index) {
        modificationLock.writeLock().lock();
        neurons++;
        final ArrayList<Double> neuronsWeights = new ArrayList<>(features);
        weights.add(index, neuronsWeights);
        for (int i = 0; i < features; i++)
            neuronsWeights.add(ThreadLocalRandom.current().nextDouble(-1, 1));
        modificationLock.writeLock().unlock();
    }

    @Override
    public void removeNeuron(int index) {
        modificationLock.writeLock().lock();
        neurons--;
        weights.remove(index);
        modificationLock.writeLock().unlock();
    }

    public Optional<Double> getWeight(int feature, int neuronIndex) {
        modificationLock.readLock().lock();
        Optional<Double> weight = Optional.of(weights.get(neuronIndex).get(feature));
        modificationLock.readLock().unlock();
        return weight;
    }

    public void setWeight(int feature, int neuronIndex, double value) {
        modificationLock.writeLock().lock();
        weights.get(neuronIndex).set(feature, value);
        modificationLock.writeLock().unlock();
    }

    @Override
    public double[] inputError(double[] error) {
        double[] inputError = new double[features()];
        for (int i = 0; i < inputError.length; i++) {
            for (int j = 0; j < labels(); j++)
                inputError[i] += error[j] * weights.get(j).get(i);
        }
        return inputError;
    }

    @Override
    public int features() {
        return features;
    }

    @Override
    public int labels() {
        return neurons;
    }

    @Override
    public double[] prediction(double... input) {
        modificationLock.readLock().lock();
        double[] output = new double[labels()];
        for (int i = 0; i < output.length; i++) {
            output[i] = activation.value(scalar(input, weights.get(i)));
        }
        modificationLock.readLock().unlock();
        return output;
    }

    @Override
    public void train(double[] input, double[] error) {
        for (int i = 0; i < labels(); i++) {
            double weightDelta = activation.value(scalar(input, weights.get(i))) * error[i];
            for (int j = 0; j < features(); j++) {
                weights.get(i).set(j, weights.get(i).get(j) + weightDelta * input[j]);
            }
        }
    }

    private static double scalar(double[] op0, List<Double> op1) {
        double sum = 0;
        for (int i = 0; i < op0.length; i++) {
            sum += op0[i]*op1.get(i);
        }
        return sum;
    }
}
