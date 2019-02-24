package editor.view.connection;

import view.entities.neuron.javafx.Neuron;

import java.util.concurrent.locks.ReentrantReadWriteLock;

public class MutableConnection extends view.entities.connection.javafx.mutable.MutableConnection {
    private final ReentrantReadWriteLock modificationLock;

    public MutableConnection(Neuron source, Neuron target, double initialWeight, ReentrantReadWriteLock modificationLock) {
        super(source, target, initialWeight);
        this.modificationLock = modificationLock;
    }

    @Override
    protected boolean modificationIsAllowed(double newWeight) {
        modificationLock.writeLock().lock();
        final boolean modificationIsAllowed = super.modificationIsAllowed(newWeight);
        modificationLock.writeLock().unlock();
        return modificationIsAllowed;
    }
}
