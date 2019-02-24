package editor.view.layer;

public interface ConnectionFactory<Connection extends view.entities.connection.javafx.Connection> {
    Connection create(int featureIndex, int neuronIndex);
}
