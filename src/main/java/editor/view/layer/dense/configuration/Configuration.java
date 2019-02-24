package editor.view.layer.dense.configuration;

import editor.view.layer.ConnectionFactory;
import view.entities.layer.configuration.ObservableConfiguration;

import javax.annotation.concurrent.ThreadSafe;
import java.util.concurrent.atomic.AtomicInteger;

@ThreadSafe
public class Configuration<Connection extends view.entities.connection.javafx.Connection> extends ObservableConfiguration<Connection> {
    private final ConnectionFactory<Connection> connectionFactory;
    private final AtomicInteger neurons = new AtomicInteger();

    public Configuration(ConnectionFactory<Connection> connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    public void addNeuron(int index) {
        modificationLock.writeLock().lock();
        neurons.incrementAndGet();
        for (int i = 0; i < features(); i++) {
            addConnection(i, index, connectionFactory.create(i, index));
        }
        modificationLock.writeLock().unlock();
    }

    public void removeNeuron(int index) {
        modificationLock.writeLock().lock();
        neurons.decrementAndGet();
        for (int i = 0; i < features(); i++) {
            removeConnection(i, index);
        }
        modificationLock.writeLock().unlock();
    }

    @Override
    protected void onFeatureAdded(int index) {
        super.onFeatureAdded(index);
        synchronized (neurons) {
            for (int i = 0; i < neurons.get(); i++) {
                addConnection(index, i, connectionFactory.create(index, i));
            }
        }
    }
}
