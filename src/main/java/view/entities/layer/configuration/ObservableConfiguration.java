package view.entities.layer.configuration;

import org.apache.commons.lang3.event.EventListenerSupport;
import view.events.layer.configuration.ConfigurationListener;
import view.events.layer.configuration.ConfigurationListenerSupport;

import javax.annotation.concurrent.ThreadSafe;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@ThreadSafe
public class ObservableConfiguration<Connection extends view.entities.connection.Connection> implements Configuration<Connection>, ConfigurationListenerSupport<Connection> {
    protected final ReentrantReadWriteLock modificationLock = new ReentrantReadWriteLock();
    private final List<List<Connection>> connections = new ArrayList<>();
    private final EventListenerSupport<ConfigurationListener> listenerSupport = EventListenerSupport.create(ConfigurationListener.class);

    public ObservableConfiguration() {
    }

    public final void addFeature(int index) {
        modificationLock.writeLock().lock();
        try {
            connections.add(index, new ArrayList<>());
            onFeatureAdded(index);
        } finally {
            modificationLock.writeLock().unlock();
        }
    }

    @SuppressWarnings({"unchecked"})
    public final void removeFeature(int index) {
        modificationLock.writeLock().lock();
        connectionsBoundCheck(index);
        final List<Connection> removedConnections = connections.remove(index);
        for (int neuronIndex = 0; neuronIndex < removedConnections.size(); neuronIndex++) {
            listenerSupport.fire().onConnectionRemoved(index, neuronIndex, removedConnections.get(neuronIndex));
        }
        modificationLock.writeLock().unlock();
    }

    @SuppressWarnings("unchecked")
    protected final void addConnection(int featureIndex, int neuronIndex, Connection connection) {
        modificationLock.writeLock().lock();
        connectionsBoundCheck(featureIndex);
        connections.get(featureIndex).add(neuronIndex, connection);
        listenerSupport.fire().onConnectionAdded(featureIndex, neuronIndex, connection);
        modificationLock.writeLock().unlock();
    }

    @SuppressWarnings("unchecked")
    protected final Connection removeConnection(int featureIndex, int neuronIndex) {
        modificationLock.writeLock().lock();
        connectionsBoundCheck(featureIndex);
        final Connection removedConnection = connections.get(featureIndex).remove(neuronIndex);
        listenerSupport.fire().onConnectionRemoved(featureIndex, neuronIndex, removedConnection);
        modificationLock.writeLock().unlock();
        return removedConnection;
    }

    @Override
    public Connection getConnection(int featureIndex, int neuronIndex) {
        modificationLock.readLock().lock();
        connectionsBoundCheck(featureIndex);
        final Connection connection = connections.get(featureIndex).get(neuronIndex);
        modificationLock.readLock().unlock();
        return connection;
    }

    @Override
    public int features() {
        return connections.size();
    }

    @Override
    public void subscribe(ConfigurationListener<Connection> listener) {
        modificationLock.readLock().lock();
        listenerSupport.addListener(listener);
        modificationLock.readLock().unlock();
    }

    protected void onFeatureAdded(int index) {}

    private void connectionsBoundCheck(int index) {
        if (index < 0 || index >= connections.size()) {
            modificationLock.writeLock().unlock();
            throw new IllegalArgumentException();
        }
    }
}
