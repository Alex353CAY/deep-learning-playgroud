package editor.view.network.processes.prediction;

import java.util.Iterator;

public interface NetworkPrediction<Layer extends view.entities.layer.javafx.Layer> {
    Iterator<LayerPrediction<Layer>> iterator();
    Iterator<LayerPrediction<Layer>> descendingIterator();
}
