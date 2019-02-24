package editor.view.network.processes.prediction;

import com.google.common.primitives.ImmutableDoubleArray;

@SuppressWarnings("UnstableApiUsage")
public class LayerPrediction<Layer extends view.entities.layer.javafx.Layer> {
    public final Layer layer;
    public final ImmutableDoubleArray input;
    public final ImmutableDoubleArray prediction;

    public LayerPrediction(Layer layer, double[] input, double[] prediction) {
        this.layer = layer;
        this.input = ImmutableDoubleArray.copyOf(input);
        this.prediction = ImmutableDoubleArray.copyOf(prediction);
    }
}
