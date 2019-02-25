package view.entities.layer.javafx;

import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import org.apache.commons.lang3.event.EventListenerSupport;
import view.entities.layer.CompositionMutabilitySupport;
import view.entities.layer.ConfigurationMutabilitySupport;
import view.entities.layer.configuration.ConfigurationFactory;
import view.entities.layer.configuration.ObservableConfiguration;
import view.entities.neuron.javafx.factories.NeuronFactory;
import view.events.layer.composition.CompositionListener;
import view.events.layer.composition.CompositionListenerSupport;

import javax.annotation.concurrent.ThreadSafe;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@ThreadSafe
public abstract class Layer<Neuron extends view.entities.neuron.javafx.Neuron, Connection extends view.entities.connection.javafx.Connection, Configuration extends ObservableConfiguration<Connection>> extends Region implements view.entities.layer.Layer<Neuron>, ConfigurationMutabilitySupport, CompositionMutabilitySupport<Neuron>, CompositionListenerSupport {
    //concurrency
    protected final ReentrantReadWriteLock modificationLock;

    //composition
    private final NeuronFactory<Neuron> neuronFactory;
    private final List<Neuron> neurons = new ArrayList<>();

    //configuration
    protected final Configuration configuration;

    //layer
    private final Pane root;
    private final DoubleProperty spacing = new SimpleDoubleProperty();
    private final DoubleProperty paddingTop = new SimpleDoubleProperty();
    private final DoubleProperty paddingRight = new SimpleDoubleProperty();
    private final DoubleProperty paddingBottom = new SimpleDoubleProperty();
    private final DoubleProperty paddingLeft = new SimpleDoubleProperty();

    //events
    private final EventListenerSupport<CompositionListener> listenerSupport = EventListenerSupport.create(CompositionListener.class);

    public Layer(Pane root, NeuronFactory<Neuron> neuronFactory, ConfigurationFactory<Configuration> configurationFactory) {
        this(root, neuronFactory, configurationFactory, new ReentrantReadWriteLock());
    }

    public Layer(Pane root, NeuronFactory<Neuron> neuronFactory, ConfigurationFactory<Configuration> configurationFactory, ReentrantReadWriteLock modificationLock) {
        this.root = root;
        this.neuronFactory = neuronFactory;
        this.configuration = configurationFactory.build(this);
        this.modificationLock = modificationLock;
        paddingProperty().addListener((observable, oldValue, newValue) -> {
            paddingTop.setValue(newValue.getTop());
            paddingRight.setValue(newValue.getRight());
            paddingBottom.setValue(newValue.getBottom());
            paddingLeft.setValue(newValue.getLeft());
        });
    }

    public final double getSpacing() {
        return spacing.get();
    }

    public final void setSpacing(double value) {
        synchronized (spacing) {
            spacing.setValue(value);
        }
    }

    @Override
    public final void addFeature(int index) {
        modificationLock.writeLock().lock();
        try {
            configuration.addFeature(index);
        } finally {
            modificationLock.writeLock().unlock();
        }
    }

    @Override
    public void removeFeature(int index) {
        modificationLock.writeLock().lock();
        try {
            configuration.removeFeature(index);
        } finally {
            modificationLock.writeLock().unlock();
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public final Neuron addNeuron(int index) {
        modificationLock.writeLock().lock();
        neuronsBoundCheck(index);
        DoubleBinding translateX, translateY;
        if (index == 0) {
            translateX = translateXProperty().add(paddingLeft);
            translateY = translateYProperty().add(paddingTop);
        } else {
            final Neuron previousNeuron = neurons.get(index - 1);
            translateX = previousNeuron.translateXProperty().add(0);
            translateY = previousNeuron.translateYProperty().add(previousNeuron.heightProperty()).add(spacing);
        }
        final Neuron neuron = neuronFactory.create(translateX, translateY);
        if (index == 0) {
            neuron.translateXProperty().bind(translateX.add(neuron.widthProperty().divide(2)));
            neuron.translateYProperty().bind(translateY.add(neuron.heightProperty().divide(2)));
        }
        neuron.widthProperty().addListener((observable, oldValue, newValue) -> {
            setWidth(newValue.doubleValue());
        });
        neurons.add(index, neuron);
        synchronized (root) {
            root.getChildren().add(neuron);
        }
        listenerSupport.fire().onNeuronAdded(index);
        if (++index != neurons.size()) {
            neurons.get(index).translateXProperty().bind(neuron.translateXProperty());
            neurons.get(index).translateYProperty().bind(neuron.translateYProperty().add(neuron.heightProperty()).add(spacing));
        }
        modificationLock.writeLock().unlock();
        return neuron;
    }

    @Override
    public final Neuron getNeuron(int index) {
        modificationLock.readLock().lock();
        neuronsBoundCheck(index);
        final Neuron neuron = neurons.get(index);
        modificationLock.readLock().unlock();
        return neuron;
    }

    @Override
    @SuppressWarnings("unchecked")
    public final Neuron removeNeuron(int index) {
        modificationLock.writeLock().lock();
        neuronsBoundCheck(index);
        final Neuron neuron = neurons.remove(index);
        synchronized (root) {
            root.getChildren().remove(neuron);
        }
        if (index == 0 && neurons.size() != 0) {
            //если удаленный нейрон был первым
            neurons.get(0).translateXProperty().bind(translateXProperty().add(paddingLeft));
            neurons.get(0).translateYProperty().bind(translateYProperty().add(paddingTop));
        } else if (index < neurons.size()) {
            //если удаленный нейрон не был первым и последним
            final Neuron previousNeuron = neurons.get(index - 1);
            neurons.get(index).translateYProperty().bind(previousNeuron.translateYProperty().add(previousNeuron.heightProperty()).add(spacing));
            neurons.get(index).translateXProperty().bind(previousNeuron.translateXProperty());
        }
        listenerSupport.fire().onNeuronRemoved(index);
        modificationLock.writeLock().unlock();
        return neuron;
    }

    @Override
    public final void subscribe(CompositionListener listener) {
        modificationLock.readLock().lock();
        listenerSupport.addListener(listener);
        modificationLock.readLock().unlock();
    }

    private void neuronsBoundCheck(int index) {
        if (index < 0 || index > neurons.size()) {
            modificationLock.writeLock().unlock();
            throw new IllegalArgumentException();
        }
    }

    @Override
    public int features() {
        modificationLock.readLock().lock();
        final int features = configuration.features();
        modificationLock.readLock().unlock();
        return features;
    }

    @Override
    public int neurons() {
        modificationLock.readLock().lock();
        final int neuronsCount = neurons.size();
        modificationLock.readLock().unlock();
        return neuronsCount;
    }
}
