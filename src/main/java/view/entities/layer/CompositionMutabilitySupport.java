package view.entities.layer;

public interface CompositionMutabilitySupport<Neuron extends view.entities.neuron.Neuron>{
    Neuron addNeuron(int index);
    Neuron removeNeuron(int index);
}
