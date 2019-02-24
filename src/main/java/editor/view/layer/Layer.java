package editor.view.layer;

import javafx.scene.layout.Pane;
import view.entities.layer.configuration.ConfigurationFactory;
import view.entities.layer.configuration.ObservableConfiguration;
import view.entities.neuron.javafx.factories.NeuronFactory;
import view.events.layer.composition.CompositionListener;

import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Consumer;

public abstract class Layer<Neuron extends editor.view.neuron.Neuron, Connection extends view.entities.connection.javafx.Connection, Configuration extends ObservableConfiguration<Connection>> extends view.entities.layer.javafx.Layer<Neuron, Connection, Configuration> {
    public Layer(Pane root, NeuronFactory<Neuron> neuronFactory, ConfigurationFactory<Configuration> configurationFactory) {
        super(root, neuronFactory, configurationFactory);
    }

    public Layer(Pane root, NeuronFactory<Neuron> neuronFactory, ConfigurationFactory<Configuration> configurationFactory, ReentrantReadWriteLock modificationLock, Consumer<Neuron> onNeuronAdded) {
        super(root, neuronFactory, configurationFactory, modificationLock);
        subscribe(new CompositionListener() {
            @Override
            public void onNeuronAdded(int index) {
                onNeuronAdded.accept(getNeuron(index));
            }

            @Override
            public void onNeuronRemoved(int index) {

            }
        });
    }

    public void showPrediction(double[] prediction) {
        modificationLock.readLock().lock();
        for (int i = 0; i < neurons(); i++) {
            getNeuron(i).setPrediction(prediction[i]);
        }
        modificationLock.readLock().unlock();
    }
}
