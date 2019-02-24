package editor;

import editor.view.layer.Layer;
import editor.view.layer.dense.DenseLayer;
import editor.view.layer.feature.FeatureLayer;
import editor.view.network.LayerBuilder;
import editor.view.network.Network;
import editor.view.network.processes.prediction.LayerPrediction;
import editor.view.neuron.Neuron;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import utils.math.activation.Sigmoid;
import view.entities.neuron.javafx.factories.NeuronFactory;

import java.util.Iterator;

public class Editor extends Application {

    private static final Sigmoid sigmoid = new Sigmoid();

    private static final NeuronFactory<Neuron> neuronFactory = (translateX, translateY) -> {
        final Neuron neuron = new editor.view.neuron.Neuron();
        neuron.translateXProperty().bind(translateX);
        neuron.translateYProperty().bind(translateY);
        return neuron;
    };

    @Override
    public void start(Stage stage) throws Exception {
        final Label predictionField = new Label("NULL");
        final Network<Layer> network = new Network<>((root, modificationLock) -> new FeatureLayer<>(root, neuronFactory, modificationLock, neuron -> neuron.setOnMouseClicked(event -> predictionField.setText(String.valueOf(neuron.getPrediction())))));
        final Button addLayer = new Button("Add layer");
        final Button removeLayer = new Button("Remove layer");
        final Button addNeuron = new Button("Add neuron");
        final Button removeNeuron = new Button("Remove neuron");
        final Button predict = new Button("Predict");
        final Button train = new Button("Train");

        final BorderPane options = new BorderPane(predictionField, null, new HBox(addLayer, removeLayer), null, new HBox(predict, train));

        final LayerBuilder<Layer> layerLayerBuilder = (root, previousLayer, modificationLock) -> new DenseLayer<>(sigmoid, root, previousLayer, neuronFactory, modificationLock,
                neuron -> neuron.setOnMouseClicked(event -> predictionField.setText(String.valueOf(neuron.getPrediction()))));
        final Layer denseLayer = network.addLayer(layerLayerBuilder);
        final Layer output = network.addLayer(layerLayerBuilder);
        denseLayer.addNeuron(0);
        denseLayer.addNeuron(1);
        network.addFeature(0);
        network.addFeature(1);
        network.addFeature(2);
        output.addNeuron(0);
        output.addNeuron(0);

        predict.setOnAction(event ->
                network.predict(new double[]{0, 0.5, 0.75}).iterator().forEachRemaining(prediction -> prediction.layer.showPrediction(prediction.prediction.toArray()))
        );

        stage.setScene(new Scene(new BorderPane(new ScrollPane(network), null, null, options, null), 500, 200));
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
