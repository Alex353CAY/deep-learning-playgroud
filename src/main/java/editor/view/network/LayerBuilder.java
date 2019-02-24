package editor.view.network;

import javafx.scene.layout.Pane;

import java.util.concurrent.locks.ReentrantReadWriteLock;

public interface LayerBuilder<Layer extends view.entities.layer.Layer> {
    Layer build(Pane root, Layer previousLayer, ReentrantReadWriteLock modificationLock);
}
