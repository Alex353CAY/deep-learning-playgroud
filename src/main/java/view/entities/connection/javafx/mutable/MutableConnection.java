package view.entities.connection.javafx.mutable;

import org.apache.commons.lang3.event.EventListenerSupport;
import view.entities.connection.javafx.Connection;
import view.entities.neuron.javafx.Neuron;
import view.events.connection.WeightModificationListener;
import view.events.connection.WeightModificationListenerSupport;

public class MutableConnection extends Connection implements view.entities.connection.MutableConnection, WeightModificationListenerSupport {
    private final EventListenerSupport<WeightModificationListener> listenerSupport = EventListenerSupport.create(WeightModificationListener.class);

    public MutableConnection(Neuron source, Neuron target, double initialWeight) {
        super(source, target, initialWeight);
    }

    @Override
    public final void setWeight(double newWeight) {
        if (modificationIsAllowed(newWeight)) {
            final double previousWeight = unconditionallySetWeight(newWeight);
            synchronized (listenerSupport) {
                listenerSupport.fire().onWeightModified(previousWeight, newWeight);
            }
        }
    }

    @Override
    public void subscribe(WeightModificationListener listener) {
        listenerSupport.addListener(listener, false);
    }

    protected boolean modificationIsAllowed(double newWeight) {
        return true;
    }

}
