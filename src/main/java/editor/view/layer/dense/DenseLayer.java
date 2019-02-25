package editor.view.layer.dense;

import editor.view.connection.MutableConnection;
import editor.view.layer.Layer;
import editor.view.layer.dense.configuration.Configuration;
import javafx.scene.layout.Pane;
import utils.math.activation.Activation;
import view.entities.neuron.javafx.factories.NeuronFactory;
import view.events.layer.composition.CompositionListener;
import view.events.layer.configuration.ConfigurationListener;
import view.events.layer.configuration.ConfigurationListenerSupport;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Consumer;

public class DenseLayer<Neuron extends editor.view.neuron.Neuron> extends Layer<Neuron, MutableConnection, Configuration<MutableConnection>> implements ConfigurationListenerSupport<MutableConnection> {
    private final Activation activation;
    private final Layer<?, ?, ?> previousLayer;

    public DenseLayer(Activation activation, Pane root, Layer<Neuron, ?, ?> previousLayer, NeuronFactory<Neuron> neuronFactory) {
        this(activation, root, previousLayer, neuronFactory, new ReentrantReadWriteLock(), neuron -> {});
    }

    public DenseLayer(Activation activation, Pane root, Layer<?, ?, ?> previousLayer, NeuronFactory<Neuron> neuronFactory, ReentrantReadWriteLock modificationLock, Consumer<Neuron> onNeuronAdded) {
        super(root, neuronFactory, layer -> new Configuration<>((featureIndex, neuronIndex) -> new MutableConnection(root, previousLayer.getNeuron(featureIndex), layer.getNeuron(neuronIndex), ThreadLocalRandom.current().nextDouble(-1, 1), modificationLock)), modificationLock, onNeuronAdded);
        this.activation = activation;
        this.previousLayer = previousLayer;
        subscribe(new CompositionListener() {
            @Override
            public void onNeuronAdded(int index) {
                configuration.addNeuron(index);
            }

            @Override
            public void onNeuronRemoved(int index) {
                configuration.removeNeuron(index);
            }
        });
    }

    @Override
    public double[] prediction(double... input) {
        modificationLock.readLock().lock();
        double[] output = new double[neurons()];
        for (int neuronIndex = 0; neuronIndex < output.length; neuronIndex++) {
            double sum = 0;
            for (int featureIndex = 0; featureIndex < features(); featureIndex++)
                sum += configuration.getConnection(featureIndex, neuronIndex).getWeight() * input[featureIndex];
            output[neuronIndex] = activation.value(sum);
        }
        modificationLock.readLock().unlock();
        return output;
    }

    @Override
    public double[] inputError(double... error) {
        double[] inputError = new double[features()];
        for (int featureIndex = 0; featureIndex < features(); featureIndex++) {
            for (int neuronIndex = 0; neuronIndex < neurons(); neuronIndex++)
                inputError[featureIndex] += error[neuronIndex] * configuration.getConnection(featureIndex, neuronIndex).getWeight();
        }
        return inputError;
    }

    @Override
    public void train(double[] input, double[] error) {
        for (int neuronIndex = 0; neuronIndex < neurons(); neuronIndex++) {
            double sum = 0;
            for (int featureIndex = 0; featureIndex < features(); featureIndex++)
                sum += configuration.getConnection(featureIndex, neuronIndex).getWeight() * input[featureIndex];
            for (int featureIndex = 0; featureIndex < features(); featureIndex++)
                configuration.getConnection(featureIndex, neuronIndex).setWeight(activation.value(sum));
        }
    }

    @Override
    public void subscribe(ConfigurationListener<MutableConnection> listener) {
        configuration.subscribe(listener);
    }
}
