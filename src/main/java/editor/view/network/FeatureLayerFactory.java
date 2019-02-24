package editor.view.network;

import javafx.scene.layout.Pane;

import java.util.concurrent.locks.ReentrantReadWriteLock;

public interface FeatureLayerFactory<Layer extends view.entities.layer.javafx.Layer> {
    Layer create(Pane root, ReentrantReadWriteLock modificationLock);
}
