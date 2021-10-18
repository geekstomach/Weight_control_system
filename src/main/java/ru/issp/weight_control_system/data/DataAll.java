package ru.issp.weight_control_system.data;

import java.util.LinkedList;

public class DataAll {




    LinkedList<Double> modelMass;
    LinkedList<Double> modelMassDeviation;
    LinkedList<Double> modelFirstDerivative;
    LinkedList<Double> modelFirstDerivativeDeviation;
    LinkedList<Double> modelSecondDerivativeDeviation;

    public DataAll() {
        this.modelMass = new LinkedList<>();
        modelMass.add(0d);
        this.modelMassDeviation = new LinkedList<>();
        modelMassDeviation.add(0d);
        this.modelFirstDerivative = new LinkedList<>();
        modelFirstDerivative.add(0d);
        this.modelFirstDerivativeDeviation = new LinkedList<>();
        modelFirstDerivativeDeviation.add(0d);
        this.modelSecondDerivativeDeviation = new LinkedList<>();
        modelSecondDerivativeDeviation.add(0d);
    }



    public LinkedList<Double> getModelMass() {
        return modelMass;
    }
    public LinkedList<Double> getModelMassDeviation() {
        return modelMassDeviation;
    }

    public LinkedList<Double> getModelFirstDerivative() {
        return modelFirstDerivative;
    }
    public LinkedList<Double> getModelFirstDerivativeDeviation() {
        return modelFirstDerivativeDeviation;
    }

    public void setModelFirstDerivativeDeviation(LinkedList<Double> modelFirstDerivativeDeviation) {
        this.modelFirstDerivativeDeviation = modelFirstDerivativeDeviation;
    }
    public void setModelFirstDerivative(LinkedList<Double> modelFirstDerivative) {
        this.modelFirstDerivative = modelFirstDerivative;
    }
    public LinkedList<Double> getModelSecondDerivativeDeviation() {
        return modelSecondDerivativeDeviation;
    }

    public void setModelSecondDerivativeDeviation(LinkedList<Double> modelSecondDerivativeDeviation) {
        this.modelSecondDerivativeDeviation = modelSecondDerivativeDeviation;
    }

    @Override
    public String toString() {
        return "DataAll{" +
                "modelMass=" + modelMass +"\n"+
                ", modelMassDeviation=" + modelMassDeviation +"\n"+
                ", modelFirstDerivative=" + modelFirstDerivative +"\n"+
                ", modelFirstDerivativeDeviation=" + modelFirstDerivativeDeviation +"\n"+
                ", modelSecondDerivativeDeviation=" + modelSecondDerivativeDeviation +"\n"+
                '}';
    }
}
