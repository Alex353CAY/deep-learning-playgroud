package network.layer.observable;

import network.layer.DenseLayer;
import org.apache.commons.lang3.event.EventListenerSupport;
import utils.math.activation.Activation;

import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ObservableDenseLayer extends DenseLayer implements ObservableMutableLayer {
    private final ReentrantReadWriteLock modificationLock = new ReentrantReadWriteLock();
    private final EventListenerSupport<ObservableLayerListener> listenerSupport = EventListenerSupport.create(ObservableLayerListener.class);

    public ObservableDenseLayer(Activation activation) {
        super(activation);
    }

    @Override
    public void addFeature(int index) {
        modificationLock.writeLock().lock();
        super.addFeature(index);
        for (int i = 0; i < labels(); i++) {
            onFeatureConnected(index, i);
            onConnectionModified(index, i, getWeight(index, i).get());
        }
        modificationLock.writeLock().unlock();
    }

    @Override
    public void removeFeature(int index) {
        modificationLock.writeLock().lock();
        super.removeFeature(index);
        for (int i = 0; i < labels(); i++) {
            onFeatureDisconnected(index, i);
        }
        modificationLock.writeLock().unlock();
    }

    @Override
    public void addNeuron(int index) {
        modificationLock.writeLock().lock();
        super.addNeuron(index);
        onNeuronAdded(index);
        for (int i = 0; i < features(); i++) {
            onFeatureConnected(i, index);
            onConnectionModified(i, index, getWeight(i, index).get());
        }
        modificationLock.writeLock().unlock();
    }

    @Override
    public void removeNeuron(int index) {
        modificationLock.writeLock().lock();
        super.removeNeuron(index);
        for (int i = 0; i < features(); i++) {
            onFeatureDisconnected(i, index);
        }
        onNeuronRemoved(index);
        modificationLock.writeLock().unlock();
    }

    @Override
    public void train(double[] input, double[] error) {
        modificationLock.writeLock().lock();
        super.train(input, error);
        for (int i = 0; i < labels(); i++)
            for (int j = 0; j < features(); j++) onConnectionModified(j, i, getWeight(j, i).get());
        modificationLock.writeLock().unlock();
    }

    protected final void onNeuronAdded(int index) {
        listenerSupport.fire().neuronAdded(index);
    }

    protected final void onNeuronRemoved(int index) {
        listenerSupport.fire().neuronRemoved(index);
    }

    protected final void onFeatureConnected(int feature, int neuron) {
        listenerSupport.fire().featureConnected(feature, neuron);
    }

    protected final void onConnectionModified(int feature, int neuron, double newWeight) {
        listenerSupport.fire().connectionModified(feature, neuron, newWeight);
    }

    protected final void onFeatureDisconnected(int feature, int neuron) {
        listenerSupport.fire().featureDisconnected(feature, neuron);
    }

    @Override
    public final void subscribe(ObservableLayerListener listener) {
        modificationLock.writeLock().lock();
        listenerSupport.addListener(listener, false);
        modificationLock.writeLock().unlock();
    }

    @Override
    public final void unsubscribe(ObservableLayerListener listener) {
        modificationLock.writeLock().lock();
        listenerSupport.removeListener(listener);
        modificationLock.writeLock().unlock();
    }
}
