package ru.issp.weight_control_system.data;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

public class DataObject {

    DataParam dataParam;
    private final int dataPoints = 8;
    private final int properTime =3;
    private final int properTimePoints =8;



    private final Queue<Double> datasetDeviation = new LinkedBlockingQueue<>(dataPoints);
    private final Queue<Double> datasetFirstDerivativeDeviation = new LinkedBlockingQueue<>(dataPoints);
    private final Queue<Double> datasetSecondDerivativeDeviation = new LinkedBlockingQueue<>(dataPoints);
    LinkedList<Double> deviationListSMA = new LinkedList<>();
    double previousElementOfDeviationListSMA;
    LinkedList<Double> firstDerivativeDeviationListSMA = new LinkedList<>();
    double previousElementOfFirstDerivativeDeviationList;
    LinkedList<Double> secondDerivativeDeviationListSMA = new LinkedList<>();
    double previousElementOfSecondDerivativeDeviationList;

    LinkedList<Double> setPowerListSMA = new LinkedList<>();






    double currentSMAWeight;
    double currentModelWeight;
    double previousModelWeight;


    public DataObject(double currentSMAWeight) {
        dataParam = new DataParam();
        this.currentSMAWeight = currentSMAWeight;
        currentModelWeight = getCurrentModelWeight();
    }

    double getCurrentModelWeight(){
        double denominator;
        double numerator1;
        double numerator2;
        denominator = Math.pow(dataParam.getR_cruc(),2)-dataParam.getS_die()/Math.PI;
        numerator1 = dataParam.getRol()*Math.pow(dataParam.getR(),2)*(Math.PI*Math.pow(dataParam.getR_cruc(),2)-(dataParam.getS_die()-dataParam.getS_die_cr()))*dataParam.getV_upper();
        numerator2 = Math.PI*dataParam.getRol()*dataParam.getS_die_cr()*Math.pow(dataParam.getR_cruc(),2)*dataParam.getV_lower();
        double firstDerivativeModelWeight = (numerator1-numerator2)/denominator;
    return previousModelWeight+firstDerivativeModelWeight*dataPoints*properTime;}


    double getDeviation(){
        return currentSMAWeight-currentModelWeight;
    }

    //TODO метод SMA можно унифицировать для всех трех отклонений?
    // Надо правильно указать место когда получаем предыдущее значение
    double getSMADeviation(){
        datasetDeviation.add(getDeviation());
        //в начале если значений еще меньше dataPoints то просто добавляем их в dataset
        //как только в dataset наберется достаточно значений для SMA начинаем добавлять их в list,
        // при этом удаляя первое значение из dataset

        if (datasetDeviation.size()>=dataPoints){

           deviationListSMA.add(datasetDeviation.stream().mapToDouble(value -> value).sum()/dataPoints);
           datasetDeviation.remove();
        }


        return deviationListSMA.getLast();
    }

    double getSMAFirstDerivativeDeviation(){
        datasetFirstDerivativeDeviation.add((deviationListSMA.getLast()-previousElementOfDeviationListSMA)/properTimePoints);
        //в начале если значений еще меньше dataPoints то просто добавляем их в dataset
        //как только в dataset наберется достаточно значений для SMA начинаем добавлять их в list,
        // при этом удаляя первое значение из dataset

        if (datasetFirstDerivativeDeviation.size()>=dataPoints){

            firstDerivativeDeviationListSMA.add(datasetFirstDerivativeDeviation.stream().mapToDouble(value -> value).sum()/dataPoints);
            datasetFirstDerivativeDeviation.remove();
        }

        return firstDerivativeDeviationListSMA.getLast();
    }

        double getSMASecondDerivativeDeviation(){
            datasetSecondDerivativeDeviation.add((firstDerivativeDeviationListSMA.getLast()-previousElementOfFirstDerivativeDeviationList)/properTimePoints);
            //в начале если значений еще меньше dataPoints то просто добавляем их в dataset
            //как только в dataset наберется достаточно значений для SMA начинаем добавлять их в list,
            // при этом удаляя первое значение из dataset

            if (datasetSecondDerivativeDeviation.size()>=dataPoints){

                secondDerivativeDeviationListSMA.add(datasetSecondDerivativeDeviation.stream().mapToDouble(value -> value).sum()/dataPoints);
                datasetSecondDerivativeDeviation.remove();
            }

        return secondDerivativeDeviationListSMA.getLast();
    }
    //https://sourceforge.net/p/javapid/code/ci/master/tree/src/main/java/org/deeg/pid/
    //https://www.codeproject.com/Articles/36459/PID-process-control-a-Cruise-Control-example
    //https://github.com/LeiDengDengDeng/pid-controller/tree/master/pid_java/src/main/java/pid
    double getPowerDeviation(){
        //собственно сам ПИД регулятор
        return dataParam.getKp()* firstDerivativeDeviationListSMA.getLast()
                +dataParam.getKi()* secondDerivativeDeviationListSMA.getLast()
                +dataParam.getKd()*(secondDerivativeDeviationListSMA.getLast()-previousElementOfSecondDerivativeDeviationList);
    }

void setPower (){
    //если требуемое изменение мощности выше допустимых границ регулирования, то добавляем граничное значение
        if (getPowerDeviation()>dataParam.getdNPmax()){
            setPowerListSMA.add(setPowerListSMA.getLast()+dataParam.getdNPmax());//
        } else if (getPowerDeviation()<dataParam.getdNPmin())
            setPowerListSMA.add(setPowerListSMA.getLast()+dataParam.getdNPmin());
        else setPowerListSMA.add(setPowerListSMA.getLast()+getPowerDeviation());//
    //далее конвертирую значение в байты и записываю в COM порт
}
}
