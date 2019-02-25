package editor.view.connection;

import javafx.beans.binding.DoubleBinding;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import view.entities.neuron.javafx.Neuron;

import java.text.DecimalFormat;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class MutableConnection extends view.entities.connection.javafx.mutable.MutableConnection {
    private final DecimalFormat format = new DecimalFormat("#.###");
    private final Label weight = new Label();
    private final ReentrantReadWriteLock modificationLock;

    public MutableConnection(Pane root, Neuron source, Neuron target, double initialWeight, ReentrantReadWriteLock modificationLock) {
        super(source, target, initialWeight);
        this.modificationLock = modificationLock;

        weight.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, new Insets(0,0,0,0))));
        weight.setText(format.format(initialWeight));
        setStrokeWidth(2.5);
        relocateWeight(source, target);
        source.translateXProperty().addListener((observable, oldValue, newValue) -> {
            relocateWeight(source, target);
        });
        target.translateXProperty().addListener((observable, oldValue, newValue) -> {
            relocateWeight(source, target);
        });
        source.translateYProperty().addListener((observable, oldValue, newValue) -> {
            relocateWeight(source, target);
        });
        target.translateYProperty().addListener((observable, oldValue, newValue) -> {
            relocateWeight(source, target);
        });

        setStrokeWidth(3);

        setOnMouseEntered(event -> {
            setStrokeWidth(6);
            root.getChildren().add(weight);
            weight.toFront();
            toFront();
            source.toFront();
            target.toFront();
        });
        setOnMouseExited(event -> {
            setStrokeWidth(3);
            root.getChildren().remove(weight);
        });
        subscribe((oldValue, newValue) -> {
            weight.setText(format.format(initialWeight));
        });
    }

    @Override
    protected boolean modificationIsAllowed(double newWeight) {
        modificationLock.writeLock().lock();
        final boolean modificationIsAllowed = super.modificationIsAllowed(newWeight);
        modificationLock.writeLock().unlock();
        return modificationIsAllowed;
    }

    private synchronized final void relocateWeight(Neuron source, Neuron target) {
        final double sourceX = source.getTranslateX();
        final double targetX = target.getTranslateX();

        final double sourceY = source.getTranslateY();
        final double targetY = target.getTranslateY();
        double horizontalDelta = (targetX - sourceX)/2;
        double verticalDelta = (targetY - sourceY)/2;
        weight.translateXProperty().bind(weight.widthProperty().divide(-2).add(sourceX + horizontalDelta));
        weight.translateYProperty().bind(weight.heightProperty().divide(2).add(sourceY + verticalDelta));
    }

    @Override
    public void toBack() {
        super.toBack();
        weight.toBack();
    }
}
