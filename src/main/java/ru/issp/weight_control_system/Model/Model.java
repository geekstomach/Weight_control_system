package ru.issp.weight_control_system.Model;

import ru.issp.weight_control_system.data.DataAll;
import ru.issp.weight_control_system.data.DataParam;

public class Model {
    private final int dt;

    private final DataParam dataParam;
    private final DataAll dataAll;

    public Double realMass;
    public Double modelMass;
    public Double massDeviation;
    public Double modelFirstDerivative;
    public Double massFirstDerivativeDeviation;
    public Double massSecondDerivativeDeviation;



    public Model(int dt, DataParam dataParam, DataAll dataAll, double currentMass) {
        this.dt = dt;
        this.dataParam = dataParam;
        this.dataAll = dataAll;
        this.realMass = currentMass;
        this.modelFirstDerivative = getModelFirstDerivative();
        dataAll.getModelMassFirstDerivative().add(modelFirstDerivative);
        this.modelMass = getModelMass();
        this.massDeviation = getModelMassDeviation(currentMass);


        this.massFirstDerivativeDeviation = getMassFirstDerivativeDeviation();
        dataAll.getModelMass().add(modelMass);
        dataAll.getMassDeviation().add(massDeviation);
        this.massSecondDerivativeDeviation = getMassSecondDerivativeDeviation();
        dataAll.getMassFirstDerivativeDeviation().add(massFirstDerivativeDeviation);
        dataAll.getMassSecondDerivativeDeviation().add(massSecondDerivativeDeviation);
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
        return currentMass - dataAll.getModelMass().getLast();
    }
    Double getMassFirstDerivativeDeviation() {
        return (massDeviation -dataAll.getMassDeviation().getLast())/dt;
    }
    Double getMassSecondDerivativeDeviation() {
        return (massFirstDerivativeDeviation -dataAll.getMassFirstDerivativeDeviation().getLast())/dt;
    }


    @Override
    public String toString() {
        return "Model{" +
                "realMass=" + realMass +
                ", modelMass=" + modelMass +
                ", massDeviation=" + massDeviation +
                ", modelFirstDerivative=" + modelFirstDerivative +
                ", massFirstDerivativeDeviation=" + massFirstDerivativeDeviation +
                ", massSecondDerivativeDeviation=" + massSecondDerivativeDeviation +
                '}';
    }
}