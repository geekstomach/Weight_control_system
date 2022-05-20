package ru.issp.weight_control_system.data;

import java.util.LinkedList;

public class DataAll {




    LinkedList<Double> modelMass;
    LinkedList<Double> massDeviation;
    LinkedList<Double> modelMassFirstDerivative;
    LinkedList<Double> massFirstDerivativeDeviation;
    LinkedList<Double> massSecondDerivativeDeviation;
    LinkedList<Double> length;


    public void initModelMass(Double currentMass) {
        modelMass.add(currentMass);
        massDeviation.add(0d);
        modelMassFirstDerivative.add(0d);
        massFirstDerivativeDeviation.add(0d);
        massSecondDerivativeDeviation.add(0d);
        length.add(0d);
    }
public void clear (){
       modelMass.clear();
       massDeviation.clear();
       modelMassFirstDerivative.clear();
       massFirstDerivativeDeviation.clear();
       massSecondDerivativeDeviation.clear();
       length.clear();
}


    public DataAll() {
        this.modelMass = new LinkedList<>();
        //modelMass.add(0d);
        this.massDeviation = new LinkedList<>();
       // modelMassDeviation.add(0d);
        this.modelMassFirstDerivative = new LinkedList<>();
        //modelFirstDerivative.add(0d);
        this.massFirstDerivativeDeviation = new LinkedList<>();
        //modelFirstDerivativeDeviation.add(0d);
        this.massSecondDerivativeDeviation = new LinkedList<>();
        //modelSecondDerivativeDeviation.add(0d);
        this.length = new LinkedList<>();
    }



    public LinkedList<Double> getModelMass() {
        return modelMass;
    }
    public LinkedList<Double> getMassDeviation() {
        return massDeviation;
    }

    public LinkedList<Double> getModelMassFirstDerivative() {
        return modelMassFirstDerivative;
    }
    public LinkedList<Double> getMassFirstDerivativeDeviation() {
        return massFirstDerivativeDeviation;
    }

    public void setMassFirstDerivativeDeviation(LinkedList<Double> massFirstDerivativeDeviation) {
        this.massFirstDerivativeDeviation = massFirstDerivativeDeviation;
    }
    public void setModelMassFirstDerivative(LinkedList<Double> modelMassFirstDerivative) {
        this.modelMassFirstDerivative = modelMassFirstDerivative;
    }
    public LinkedList<Double> getMassSecondDerivativeDeviation() {
        return massSecondDerivativeDeviation;
    }

    public void setMassSecondDerivativeDeviation(LinkedList<Double> massSecondDerivativeDeviation) {
        this.massSecondDerivativeDeviation = massSecondDerivativeDeviation;
    }

    public LinkedList<Double> getLength() {
        return length;
    }

    public void setLength(Double currentLength) {
        this.length.add(currentLength);
    }

    @Override
    public String toString() {
        return "DataAll{" +
                "modelMass=" + modelMass +"\n"+
                ", modelMassDeviation=" + massDeviation +"\n"+
                ", modelFirstDerivative=" + modelMassFirstDerivative +"\n"+
                ", modelFirstDerivativeDeviation=" + massFirstDerivativeDeviation +"\n"+
                ", modelSecondDerivativeDeviation=" + massSecondDerivativeDeviation +"\n"+
                '}';
    }
    public String lastToString() {
        return "DataAll{" +
                "modelMass=" + modelMass.getLast() +"\n"+
                ", modelMassDeviation=" + massDeviation.getLast() +"\n"+
                ", modelFirstDerivative=" + modelMassFirstDerivative.getLast() +"\n"+
                ", modelFirstDerivativeDeviation=" + massFirstDerivativeDeviation.getLast() +"\n"+
                ", modelSecondDerivativeDeviation=" + massSecondDerivativeDeviation.getLast() +"\n"+
                '}';
    }
}
