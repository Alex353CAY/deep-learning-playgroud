package editor.view.layer.feature;

import editor.view.neuron.Neuron;
import javafx.scene.layout.Pane;
import view.entities.connection.javafx.mutable.MutableConnection;
import view.entities.layer.configuration.ObservableConfiguration;
import editor.view.layer.Layer;
import view.entities.neuron.javafx.factories.NeuronFactory;

import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Consumer;

public class FeatureLayer<Neuron extends editor.view.neuron.Neuron> extends Layer<Neuron, MutableConnection, ObservableConfiguration<MutableConnection>> {
    public FeatureLayer(Pane root, NeuronFactory<Neuron> neuronFactory) {
        this(root, neuronFactory, new ReentrantReadWriteLock(), neuron -> {});
    }

    public FeatureLayer(Pane root, NeuronFactory<Neuron> neuronFactory, ReentrantReadWriteLock modificationLock, Consumer<Neuron> onNeuronAdded) {
        super(root, neuronFactory, layer -> new ObservableConfiguration<>(), modificationLock, onNeuronAdded);
    }

    @Override
    public double[] prediction(double... input) {
        return input;
    }

    @Override
    public double[] inputError(double... input) {
        throw new IllegalArgumentException();
    }

    @Override
    public void train(double[] input, double[] error) {}

    @Override
    public void removeFeature(int index) {
        throw new IllegalStateException();
    }

    @Override
    public int features() {
        return super.features();
    }
}
