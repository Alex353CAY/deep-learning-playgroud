package network.layer;

public interface MutableLayer extends Layer {
    void addFeature(int index);
    void removeFeature(int index);
    void addNeuron(int index);
    void removeNeuron(int index);
}
