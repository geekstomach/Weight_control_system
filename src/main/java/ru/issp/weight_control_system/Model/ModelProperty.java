package ru.issp.weight_control_system.Model;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class ModelProperty {

    public ModelProperty(String time, Double realMass,Double modelMass,Double modelMassDeviation,Double modelFirstDerivativeDeviation,Double modelSecondDerivativeDeviation) {
        setTime(time);
        setRealMass(realMass);
        setModelMass(modelMass);
        setModelMassDeviation(modelMassDeviation);
        setModelFirstDerivativeDeviation(modelFirstDerivativeDeviation);
        setModelSecondDerivativeDeviation(modelSecondDerivativeDeviation);
    }

    public double getRealMass() {
        return realMass.get();
    }

    public DoubleProperty realMassProperty() {
        return realMass;
    }

    public void setRealMass(double realMass) {
        this.realMass.set(realMass);
    }

    public double getModelMass() {
        return modelMass.get();
    }

    public DoubleProperty modelMassProperty() {
        return modelMass;
    }

    public void setModelMass(double modelMass) {
        this.modelMass.set(modelMass);
    }

    public double getModelMassDeviation() {
        return modelMassDeviation.get();
    }

    public DoubleProperty modelMassDeviationProperty() {
        return modelMassDeviation;
    }

    public void setModelMassDeviation(double modelMassDeviation) {
        this.modelMassDeviation.set(modelMassDeviation);
    }

    public double getModelFirstDerivativeDeviation() {
        return modelFirstDerivativeDeviation.get();
    }

    public DoubleProperty modelFirstDerivativeDeviationProperty() {
        return modelFirstDerivativeDeviation;
    }

    public void setModelFirstDerivativeDeviation(double modelFirstDerivativeDeviation) {
        this.modelFirstDerivativeDeviation.set(modelFirstDerivativeDeviation);
    }

    public double getModelSecondDerivativeDeviation() {
        return modelSecondDerivativeDeviation.get();
    }

    public DoubleProperty modelSecondDerivativeDeviationProperty() {
        return modelSecondDerivativeDeviation;
    }

    public void setModelSecondDerivativeDeviation(double modelSecondDerivativeDeviation) {
        this.modelSecondDerivativeDeviation.set(modelSecondDerivativeDeviation);
    }

    public String getTime() {
        return time.get();
    }

    public StringProperty timeProperty() {
        return time;
    }

    public void setTime(String time) {
        this.time.set(time);
    }

    private final StringProperty time = new SimpleStringProperty();
    private final DoubleProperty realMass = new SimpleDoubleProperty();
    private final DoubleProperty modelMass = new SimpleDoubleProperty();
    private final DoubleProperty modelMassDeviation = new SimpleDoubleProperty();
    private final DoubleProperty modelFirstDerivativeDeviation = new SimpleDoubleProperty();
    private final DoubleProperty modelSecondDerivativeDeviation = new SimpleDoubleProperty();

    @Override
    public String toString() {
        return "ModelProperty{" +
                "time=" + time +
                "realMass=" + realMass +
                ", modelMass=" + modelMass +
                ", modelMassDeviation=" + modelMassDeviation +
                ", modelFirstDerivativeDeviation=" + modelFirstDerivativeDeviation +
                ", modelSecondDerivativeDeviation=" + modelSecondDerivativeDeviation +
                '}';
    }
}
