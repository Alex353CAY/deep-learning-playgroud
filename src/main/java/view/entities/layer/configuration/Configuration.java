package view.entities.layer.configuration;


public interface Configuration<Connection extends view.entities.connection.Connection> {
    Connection getConnection(int feature, int neuron);
    int features();
}
