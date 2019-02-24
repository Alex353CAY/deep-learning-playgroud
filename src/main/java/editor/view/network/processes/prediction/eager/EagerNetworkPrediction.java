package editor.view.network.processes.prediction.eager;

import editor.view.network.processes.prediction.LayerPrediction;
import editor.view.network.processes.prediction.NetworkPrediction;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;

public class EagerNetworkPrediction<Layer extends view.entities.layer.javafx.Layer> implements NetworkPrediction<Layer> {
    private final Deque<LayerPrediction<Layer>> predictions = new ArrayDeque<>();

    public EagerNetworkPrediction(Iterator<LayerPrediction<Layer>> predictionsIterator) {
        while (predictionsIterator.hasNext()) {
            predictions.addLast(predictionsIterator.next());
        }
    }

    @Override
    public Iterator<LayerPrediction<Layer>> iterator() {
        return predictions.iterator();
    }

    @Override
    public Iterator<LayerPrediction<Layer>> descendingIterator() {
        return predictions.descendingIterator();
    }
}
