package network.layer.observable;

import network.layer.MutableLayer;

public interface ObservableMutableLayer extends MutableLayer {
    void subscribe(ObservableLayerListener listener);
    void unsubscribe(ObservableLayerListener listener);
}
