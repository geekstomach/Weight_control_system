package ru.issp.weight_control_system.Model;

import javafx.beans.property.*;

public class ModelProperty {

    private final StringProperty timeProperty = new SimpleStringProperty();
    private final DoubleProperty realMassProperty = new SimpleDoubleProperty();
    private final DoubleProperty modelMassProperty = new SimpleDoubleProperty();
    private final DoubleProperty modelMassFirstDerivativeProperty = new SimpleDoubleProperty();
    private final DoubleProperty massDeviationProperty = new SimpleDoubleProperty();
    private final DoubleProperty massFirstDerivativeDeviationProperty = new SimpleDoubleProperty();
    private final DoubleProperty massSecondDerivativeDeviationProperty = new SimpleDoubleProperty();
    private final DoubleProperty integralPartOfThePowerProperty = new SimpleDoubleProperty();
    private final DoubleProperty proportionalPartOfThePowerProperty = new SimpleDoubleProperty();
    private final DoubleProperty differentialPartOfThePowerProperty = new SimpleDoubleProperty();
    private final DoubleProperty powerDeviationProperty = new SimpleDoubleProperty();
    private final IntegerProperty currentPowerProperty = new SimpleIntegerProperty();
    private final DoubleProperty pullingRateProperty = new SimpleDoubleProperty();
    private final DoubleProperty lengthProperty = new SimpleDoubleProperty();
    private final DoubleProperty meltLevelHeightProperty = new SimpleDoubleProperty();
    private final DoubleProperty radiusProperty = new SimpleDoubleProperty();

    public ModelProperty(String time,
                         Double realMass,
                         Double modelMass,
                         Double modelMassFirstDerivative,
                         Double massDeviation,
                         Double massFirstDerivativeDeviation,
                         Double massSecondDerivativeDeviation,
                         Double integralPartOfThePower,
                         Double proportionalPartOfThePower,
                         Double differentialPartOfThePower,
                         Double powerDeviation,
                         Integer currentPower,
                         Double pullingRate,
                         Double length,
                         Double meltLevelHeight,
                         Double radius) {
        setTimeProperty(time);
        setRealMassProperty(realMass);
        setModelMassProperty(modelMass);
        setModelMassFirstDerivativeProperty(modelMassFirstDerivative);
        setMassDeviationProperty(massDeviation);
        setMassFirstDerivativeDeviationProperty(massFirstDerivativeDeviation);
        setMassSecondDerivativeDeviationProperty(massSecondDerivativeDeviation);
        setIntegralPartOfThePowerProperty(integralPartOfThePower);
        setProportionalPartOfThePowerProperty(proportionalPartOfThePower);
        setDifferentialPartOfThePowerProperty(differentialPartOfThePower);
        setPowerDeviationProperty(powerDeviation);
        setCurrentPowerProperty(currentPower);
        setPullingRateProperty(pullingRate);
        setLengthProperty(length);
        setMeltLevelHeightProperty(meltLevelHeight);
        setRadiusProperty(radius);
    }


    public String getTimeProperty() {
        return timeProperty.get();
    }

    public StringProperty timePropertyProperty() {
        return timeProperty;
    }

    public void setTimeProperty(String timeProperty) {
        this.timeProperty.set(timeProperty);
    }

    public double getRealMassProperty() {
        return realMassProperty.get();
    }

    public DoubleProperty realMassPropertyProperty() {
        return realMassProperty;
    }

    public void setRealMassProperty(double realMassProperty) {
        this.realMassProperty.set(realMassProperty);
    }

    public double getModelMassProperty() {
        return modelMassProperty.get();
    }

    public DoubleProperty modelMassPropertyProperty() {
        return modelMassProperty;
    }

    public void setModelMassProperty(double modelMassProperty) {
        this.modelMassProperty.set(modelMassProperty);
    }

    public double getModelMassFirstDerivativeProperty() {
        return modelMassFirstDerivativeProperty.get();
    }

    public DoubleProperty modelMassFirstDerivativePropertyProperty() {
        return modelMassFirstDerivativeProperty;
    }

    public void setModelMassFirstDerivativeProperty(double modelMassFirstDerivativeProperty) {
        this.modelMassFirstDerivativeProperty.set(modelMassFirstDerivativeProperty);
    }

    public double getMassDeviationProperty() {
        return massDeviationProperty.get();
    }

    public DoubleProperty massDeviationPropertyProperty() {
        return massDeviationProperty;
    }

    public void setMassDeviationProperty(double massDeviationProperty) {
        this.massDeviationProperty.set(massDeviationProperty);
    }

    public double getMassFirstDerivativeDeviationProperty() {
        return massFirstDerivativeDeviationProperty.get();
    }

    public DoubleProperty massFirstDerivativeDeviationPropertyProperty() {
        return massFirstDerivativeDeviationProperty;
    }

    public void setMassFirstDerivativeDeviationProperty(double massFirstDerivativeDeviationProperty) {
        this.massFirstDerivativeDeviationProperty.set(massFirstDerivativeDeviationProperty);
    }

    public double getMassSecondDerivativeDeviationProperty() {
        return massSecondDerivativeDeviationProperty.get();
    }

    public DoubleProperty massSecondDerivativeDeviationPropertyProperty() {
        return massSecondDerivativeDeviationProperty;
    }

    public void setMassSecondDerivativeDeviationProperty(double massSecondDerivativeDeviationProperty) {
        this.massSecondDerivativeDeviationProperty.set(massSecondDerivativeDeviationProperty);
    }

    public double getIntegralPartOfThePowerProperty() {
        return integralPartOfThePowerProperty.get();
    }

    public DoubleProperty integralPartOfThePowerPropertyProperty() {
        return integralPartOfThePowerProperty;
    }

    public void setIntegralPartOfThePowerProperty(double integralPartOfThePowerProperty) {
        this.integralPartOfThePowerProperty.set(integralPartOfThePowerProperty);
    }

    public double getProportionalPartOfThePowerProperty() {
        return proportionalPartOfThePowerProperty.get();
    }

    public DoubleProperty proportionalPartOfThePowerPropertyProperty() {
        return proportionalPartOfThePowerProperty;
    }

    public void setProportionalPartOfThePowerProperty(double proportionalPartOfThePowerProperty) {
        this.proportionalPartOfThePowerProperty.set(proportionalPartOfThePowerProperty);
    }

    public double getDifferentialPartOfThePowerProperty() {
        return differentialPartOfThePowerProperty.get();
    }

    public DoubleProperty differentialPartOfThePowerPropertyProperty() {
        return differentialPartOfThePowerProperty;
    }

    public void setDifferentialPartOfThePowerProperty(double differentialPartOfThePowerProperty) {
        this.differentialPartOfThePowerProperty.set(differentialPartOfThePowerProperty);
    }

    public double getPowerDeviationProperty() {
        return powerDeviationProperty.get();
    }

    public DoubleProperty powerDeviationPropertyProperty() {
        return powerDeviationProperty;
    }

    public void setPowerDeviationProperty(double powerDeviationProperty) {
        this.powerDeviationProperty.set(powerDeviationProperty);
    }

    public int getCurrentPowerProperty() {
        return currentPowerProperty.get();
    }

    public IntegerProperty currentPowerPropertyProperty() {
        return currentPowerProperty;
    }

    public void setCurrentPowerProperty(int currentPowerProperty) {
        this.currentPowerProperty.set(currentPowerProperty);
    }

    public double getPullingRateProperty() {
        return pullingRateProperty.get();
    }

    public DoubleProperty pullingRatePropertyProperty() {
        return pullingRateProperty;
    }

    public void setPullingRateProperty(double pullingRateProperty) {
        this.pullingRateProperty.set(pullingRateProperty);
    }

    public double getLengthProperty() {
        return lengthProperty.get();
    }

    public DoubleProperty lengthPropertyProperty() {
        return lengthProperty;
    }

    public void setLengthProperty(double lengthProperty) {
        this.lengthProperty.set(lengthProperty);
    }

    public double getMeltLevelHeightProperty() {
        return meltLevelHeightProperty.get();
    }

    public DoubleProperty meltLevelHeightPropertyProperty() {
        return meltLevelHeightProperty;
    }

    public void setMeltLevelHeightProperty(double meltLevelHeightProperty) {
        this.meltLevelHeightProperty.set(meltLevelHeightProperty);
    }

    public double getRadiusProperty() {
        return radiusProperty.get();
    }

    public DoubleProperty radiusPropertyProperty() {
        return radiusProperty;
    }

    public void setRadiusProperty(double radiusProperty) {
        this.radiusProperty.set(radiusProperty);
    }

    @Override
    public String toString() {
        return "ModelProperty{" +
                "time=" + timeProperty +"\n"+
                ", realMass=" + realMassProperty +"\n"+
                ", modelMass=" + modelMassProperty +"\n"+
                ", modelMassFirstDerivative=" + modelMassFirstDerivativeProperty +"\n"+
                ", massDeviation=" + massDeviationProperty +"\n"+
                ", massFirstDerivativeDeviation=" + massFirstDerivativeDeviationProperty +"\n"+
                ", massSecondDerivativeDeviation=" + massSecondDerivativeDeviationProperty +"\n"+
                ", integralPartOfThePower=" + integralPartOfThePowerProperty +"\n"+
                ", proportionalPartOfThePower=" + proportionalPartOfThePowerProperty +"\n"+
                ", differentialPartOfThePower=" + differentialPartOfThePowerProperty +"\n"+
                ", powerDeviation=" + powerDeviationProperty +"\n"+
                ", currentPower=" + currentPowerProperty +"\n"+
                ", pullingRate=" + pullingRateProperty +"\n"+
                ", length=" + lengthProperty +"\n"+
                ", meltLevelHeight=" + meltLevelHeightProperty +"\n"+
                ", radius=" + radiusProperty +"\n"+
                '}';
    }




}
