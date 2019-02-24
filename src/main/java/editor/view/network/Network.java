package editor.view.network;

import editor.view.network.processes.prediction.LayerPrediction;
import editor.view.network.processes.prediction.NetworkPrediction;
import editor.view.network.processes.prediction.eager.EagerNetworkPrediction;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import view.entities.neuron.javafx.Neuron;
import view.events.layer.composition.CompositionListener;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Network<Layer extends editor.view.layer.Layer> extends Region {
    private final ReentrantReadWriteLock modificationLock = new ReentrantReadWriteLock();
    private final Pane root = new Pane();

    private final Deque<Layer> layers = new ArrayDeque<>();

    public Network(FeatureLayerFactory<Layer> featureLayerFactory) {
        layers.addFirst(featureLayerFactory.create(root, modificationLock));
        root.getChildren().add(layers.getFirst());
        getChildren().add(root);
    }

    public void addFeature(int index) {
        layers.getFirst().addNeuron(index);
    }

    public void removeFeature(int index) {
        layers.getFirst().removeNeuron(index);
    }

    public Layer addLayer(LayerBuilder<Layer> builder) {
        modificationLock.writeLock().lock();
        final Layer previousLayer = layers.getLast();
        final Layer layer = builder.build(root, previousLayer, modificationLock);
        previousLayer.subscribe(new CompositionListener() {
            @Override
            public void onNeuronAdded(int index) {
                layer.addFeature(index);
            }

            @Override
            public void onNeuronRemoved(int index) {
                layer.removeFeature(index);
            }
        });
        layer.translateXProperty().bind(previousLayer.translateXProperty().add(previousLayer.widthProperty()).add(80));
        layers.add(layer);
        layer.toBack();
        root.getChildren().add(layer);
        for (int i = 0; i < previousLayer.neurons(); i++) {
            layer.addFeature(i);
        }
        layer.heightProperty().addListener((observable, oldValue, newValue) -> {
            synchronized (minHeightProperty()) {
                if (newValue.doubleValue() > minHeightProperty().get()) {
                    minHeightProperty().setValue(newValue);
                }
            }
        });
        modificationLock.writeLock().unlock();
        return layer;
    }

    public Layer removeLayer() {
        modificationLock.writeLock().lock();
        if (layers.size() == 1) {
            modificationLock.writeLock().unlock();
            throw new IllegalStateException();
        }
        final Layer removedLayer = layers.removeLast();
        modificationLock.writeLock().unlock();
        return removedLayer;
    }

    public NetworkPrediction<Layer> predict(double[] input) {
        modificationLock.readLock().lock();
        AtomicReference<double[]> inputRef = new AtomicReference<>(input);
        List<LayerPrediction<Layer>> predictions = new ArrayList<>();
        layers.forEach(layer -> {
            final double[] prediction = layer.prediction(inputRef.get());
            predictions.add(new LayerPrediction<>(layer, input, prediction));
            inputRef.getAndSet(prediction);
        });
        final EagerNetworkPrediction<Layer> prediction = new EagerNetworkPrediction<>(predictions.iterator());
        modificationLock.readLock().unlock();
        return prediction;
    }

    public void train(double[] input, double[] expected) {
        modificationLock.writeLock().lock();
        ArrayDeque<double[]> inputs = new ArrayDeque<>();
        for (Layer layer : layers) {
            final double[] prediction = layer.prediction(input);
            inputs.addLast(prediction);
            input = prediction;
        }
        modificationLock.writeLock().unlock();
    }
}
