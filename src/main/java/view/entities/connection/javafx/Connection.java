package view.entities.connection.javafx;

import com.google.common.util.concurrent.AtomicDouble;
import javafx.scene.shape.Line;
import view.entities.neuron.javafx.Neuron;

import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Connection extends Line implements view.entities.connection.Connection {
    private final ReentrantReadWriteLock modificationLock = new ReentrantReadWriteLock();
    private final AtomicDouble weight;

    public Connection(Neuron source, Neuron target, double initialWeight) {
        startXProperty().bind(source.mountingPointX());
        startYProperty().bind(source.mountingPointY());
        endXProperty().bind(target.mountingPointX());
        endYProperty().bind(target.mountingPointY());
        weight = new AtomicDouble(initialWeight);
    }

    @Override
    public final double getWeight() {
        modificationLock.readLock().lock();
        final double weight = this.weight.get();
        modificationLock.readLock().unlock();
        return weight;
    }

    /**
     * @return previousWeight
     */
    protected final double unconditionallySetWeight(double newWeight) {
        modificationLock.writeLock().lock();
        final double previousWeight = weight.getAndSet(newWeight);
        modificationLock.writeLock().unlock();
        return previousWeight;
    }
}