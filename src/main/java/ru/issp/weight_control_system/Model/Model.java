package ru.issp.weight_control_system.Model;

import ru.issp.weight_control_system.data.DataAll;
import ru.issp.weight_control_system.data.DataParam;

public class Model {
    private final int dt;

    private final DataParam dataParam;
    private final DataAll dataAll;

    public Double realMass;
    public Double modelMass;
    public Double modelMassDeviation;
    public Double modelFirstDerivative;
    public Double modelFirstDerivativeDeviation;
    public Double modelSecondDerivativeDeviation;

    @Override
    public String toString() {
        return "Model{" +
                "dt=" + dt +
                ", modelMass=" + modelMass +
                ", modelMassDeviation=" + modelMassDeviation +
                ", modelFirstDerivative=" + modelFirstDerivative +
                ", modelFirstDerivativeDeviation=" + modelFirstDerivativeDeviation +
                ", modelSecondDerivativeDeviation=" + modelSecondDerivativeDeviation +
                '}';
    }

    public Model(int dt, DataParam dataParam, DataAll dataAll, double currentMass) {
        this.dt = dt;
        this.dataParam = dataParam;
        this.dataAll = dataAll;
        this.realMass = currentMass;
        this.modelFirstDerivative = getModelFirstDerivative();
        dataAll.getModelFirstDerivative().add(modelFirstDerivative);
        this.modelMass = getModelMass();
        this.modelMassDeviation = getModelMassDeviation(currentMass);


        this.modelFirstDerivativeDeviation = getModelFirstDerivativeDeviation();
        dataAll.getModelMass().add(modelMass);
        dataAll.getModelMassDeviation().add(modelMassDeviation);
        this.modelSecondDerivativeDeviation = getModelSecondDerivativeDeviation();
        dataAll.getModelFirstDerivativeDeviation().add(modelFirstDerivativeDeviation);

        dataAll.getModelSecondDerivativeDeviation().add(modelSecondDerivativeDeviation);
    }
    Double getModelFirstDerivative() {
        //Всегда одинакова до смены параметров?
        double denominator;
        double numerator1;
        double numerator2;
        denominator = Math.pow(dataParam.getR_cruc(), 2) - dataParam.getS_die() / Math.PI;
        numerator1 = dataParam.getRol() * Math.pow(dataParam.getR(), 2) * (Math.PI * Math.pow(dataParam.getR_cruc(), 2) - (dataParam.getS_die() - dataParam.getS_die_cr())) * dataParam.getV_upper();
        numerator2 = Math.PI * dataParam.getRol() * dataParam.getS_die_cr() * Math.pow(dataParam.getR_cruc(), 2) * dataParam.getV_lower();
        return (numerator1 - numerator2) / denominator;
    }

    Double getModelMass() {
        return dataAll.getModelMass().getLast() + modelFirstDerivative * dt;
    }
    Double getModelMassDeviation(Double currentMass) {
        return currentMass - modelMass;
    }


    Double getModelFirstDerivativeDeviation() {
        return (modelMassDeviation-dataAll.getModelMassDeviation().getLast())/dt;
    }
    Double getModelSecondDerivativeDeviation() {
        return (modelFirstDerivativeDeviation-dataAll.getModelFirstDerivativeDeviation().getLast())/dt;
    }




}