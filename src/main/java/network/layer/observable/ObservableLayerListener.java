package network.layer.observable;

public interface ObservableLayerListener {
    default void neuronAdded(int index) {}
    default void neuronRemoved(int index) {}

    default void featureConnected(int feature, int neuron) {}
    default void connectionModified(int feature, int neuron, double weight) {}
    default void featureDisconnected(int feature, int neuron) {}
}
