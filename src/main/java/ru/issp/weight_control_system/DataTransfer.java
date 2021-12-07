package ru.issp.weight_control_system;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import jssc.SerialPortException;
import ru.issp.weight_control_system.Model.ModelProperty;
import ru.issp.weight_control_system.ProdCons.ExcelFileWriter;
import ru.issp.weight_control_system.ProdCons.FromByteToWeight;
import ru.issp.weight_control_system.data.DataAll;
import ru.issp.weight_control_system.data.DataParam;
import ru.issp.weight_control_system.utils.Filters;
import ru.issp.weight_control_system.utils.PowerSetter;

import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.TimeZone;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;


public class DataTransfer {
static AtomicBoolean IsModelCalculationsStarted = new AtomicBoolean(false);
static AtomicBoolean IsPowerControlStarted = new AtomicBoolean(false);
static  DataParam dataParam = new DataParam();



    public static void transferData(ObservableList<ModelProperty> sourceList, ObservableList<Double> realMassList, ObservableList<Double> filteredRadiusList) throws FileNotFoundException {


        ObservableList<Double> modelRadiusList = FXCollections.observableArrayList();

        filteredRadiusList.add(0d);

//инициализируем получение данных
        BlockingQueue<byte[]> q = new LinkedBlockingQueue<>();
        // ReadFromFile p = new ReadFromFile(q);//Читаем из файла
        //ReadFromCom p = new ReadFromCom(q);//Читаем из COM-порта
        TestData testData = new TestData();
        FromByteToWeight c1 = new FromByteToWeight(q);
        ScheduledExecutorService scheduledExecutorService;


        DataAll dataAll = new DataAll();
//Запускаем получение данных
        System.out.println("Запускаем потоки producer/consumer ");
        //new Thread(p).start();
        new Thread(c1).start();
        new Thread(testData).start();
//запускаем расчеты по расписанию

        AtomicLong startLocal = new AtomicLong(System.currentTimeMillis());
        AtomicInteger globalCount= new AtomicInteger();//используется для запуска startModelCalculations раз в n тактов
        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();

        scheduledExecutorService.scheduleAtFixedRate(() -> {

/*//Получаем все данные веса за интервал и усредняем
           //TODO Возможно стоит посмотреть в сторону кольцевого буфера.
            int size = c1.getOutputQueue().size();
           //System.out.println(size);
            double currentMass = 0d;
            double sum = 0d;

            try {
                //currentMass = c1.getOutputQueue().take().doubleValue();
                //Набираем все данные за dt и усредняем
                for (int i = 0; i < size; i++) {
                    sum = sum + c1.getOutputQueue().take();
                    //System.out.print(" "+count++);
                }
                if (size != 0) currentMass = sum / size;

            } catch (InterruptedException e) {
                e.printStackTrace();
            }*/
//Тестовые данные
            int sizeTestData = testData.getOutputQueue().size();
            double currentMass = 0d;
            double sum = 0d;

            try {
                //Набираем все данные за dt и усредняем
                for (int i = 0; i < sizeTestData; i++) {
                    sum = sum + testData.getOutputQueue().take();
                    }
                if (sizeTestData != 0) currentMass = sum / sizeTestData;

            } catch (InterruptedException e) {
                e.printStackTrace();
            }

//Текущая масса(средняя за прошлый интервал)
// current mass добавляем в один лист для отрисовки на графике, т.к. работает с самого начала программы и не изменяется до ее завершения.
            realMassList.add(currentMass);

            //расчет радиуса
            if (realMassList.size()>=2) {
            modelRadiusList.add(calcRealR((realMassList.get(realMassList.size() - 1) - realMassList.get(realMassList.size() - 2))/dataParam.getReadTact()));
            filteredRadiusList.add(Filters.lowPassFilter(modelRadiusList,filteredRadiusList,60,dataParam.getModelTact()*dataParam.getReadTact()));
            }
            System.out.println(filteredRadiusList.get(filteredRadiusList.size()-1));
            //TODO добавить расчет уровня расплава
            //TODO проверить согласование данных с текущеЙ и расчетной массы
            //следующий блок работает раз в modelTact
            modelCalculations(globalCount,startLocal,sourceList,realMassList,dataAll);
            //увеличиваем счетчик циклов расчета
            globalCount.incrementAndGet();

        }, 0, dataParam.getReadTact(), TimeUnit.SECONDS);

    }


    private static SimpleDateFormat createDateFormatForTableView() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        return dateFormat;
    }

    private static double calcRealR (double dM){
        //скорость изменения измеряемого весового сигнала
        double realR;
        double one = (Math.pow(dataParam.getR_cruc(),2)-dataParam.getS_die()/Math.PI)*dM;
        double two = Math.PI*dataParam.getRol()*Math.pow(dataParam.getR_cruc(),2)*dataParam.getS_die_cr()*dataParam.getV_cr_upper();
        double three = dataParam.getRos()*(Math.PI*Math.pow(dataParam.getR_cruc(),2)-(dataParam.getS_die()-dataParam.getS_die_cr()))*dataParam.getV_upper();
        try {
            realR = Math.sqrt((one+two)/three);
        }catch (RuntimeException e){
            System.out.println("Деление на ноль");
            realR = -1d;
        }
    return realR;}

    private static void  setPower(double dP){
        if (IsPowerControlStarted.get()){

            //Может быть стоит сделать константу POWER double, а cast в инт только при отправке в генератор
            //TODO Поправить в соответствии с коэффициентами перекоса
            if (dP>dataParam.getdNPmax()){
                try {
                    PowerSetter.setPower((int) (PowerSetter.getPOWER()+dataParam.getdNPmax()*dataParam.getkPdefp()));
                } catch (SerialPortException | InterruptedException e) {
                    e.printStackTrace();
                }
            } else if (dP<-dataParam.getdNPmax()){
                try {
                    PowerSetter.setPower((int) (PowerSetter.getPOWER()-dataParam.getdNPmax()*dataParam.getkPdefm()));
                } catch (SerialPortException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
            else {
                try {
                    PowerSetter.setPower((int) (PowerSetter.getPOWER()+dP));
                } catch (SerialPortException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static void modelCalculationsTest(AtomicInteger globalCount,AtomicLong startLocal,ObservableList<ModelProperty> sourceList, ObservableList<Double> realMassList, DataAll dataAll){
        //При каждом запуске расчетов проверяем пусты ли массивы и инициализируем первым значением текущей массы
        //При каждом начале расчетов инициализируем массивы
        if (IsModelCalculationsStarted.get()&&sourceList.size()==0&&globalCount.get()% dataParam.getModelTact()==0){
            System.out.println("Сработал if инициализации");
            System.out.println(sourceList.size());
            System.out.println(dataAll);
            dataAll.initModelMass(realMassList.get(realMassList.size()-1));
            System.out.println(dataAll);
            startLocal.set(System.currentTimeMillis());
        }
        //При каждом окончании расчетов очищаем массивы

        if (!IsModelCalculationsStarted.get()&&sourceList.size()!=0){
            System.out.println("Сработал if очистки");
            System.out.println(sourceList.size());
            System.out.println(dataAll);
            sourceList.clear();
            dataAll.clear();
            System.out.println(sourceList.size());
            System.out.println(dataAll);
        }
        //startModelCalculations

        if (globalCount.get()%dataParam.getModelTact()==0&&IsModelCalculationsStarted.get()){

/*            System.out.println("Проверка предыдущих значений до{"+
                    "prevMassDeviation"+dataAll.getMassDeviation().getLast()+"\n"+
                    "prevMassFirstDerivativeDeviation"+dataAll.getMassFirstDerivativeDeviation().getLast()+"\n"+
                    "prevMassSecondDerivativeDeviation"+dataAll.getMassSecondDerivativeDeviation().getLast()+"}");*/

                    String localTime = createDateFormatForTableView().format(System.currentTimeMillis() - startLocal.get());
            int dt = dataParam.getModelTact()*dataParam.getReadTact();
            //берем последний добавленный элемент в realMassList, текущую массу
            double realMass = realMassList.get(realMassList.size()-1);
            double modelMass = dataAll.getModelMass().getLast();
            System.out.println("realMass = "+realMass);
            System.out.println("modelMass = "+modelMass);
            double modelMassFirstDerivative = getModelMassFirstDerivative();



            double massDeviation = realMass - modelMass;
            double massFirstDerivativeDeviation = (massDeviation -dataAll.getMassDeviation().getLast())/dt;

            dataAll.getMassDeviation().add(massDeviation);
            double massSecondDerivativeDeviation = (massFirstDerivativeDeviation -dataAll.getMassFirstDerivativeDeviation().getLast())/dt;
            dataAll.getMassFirstDerivativeDeviation().add(massFirstDerivativeDeviation);
            dataAll.getMassSecondDerivativeDeviation().add(massSecondDerivativeDeviation);



            double prevMassDeviation = 0d;
            double prevMassFirstDerivativeDeviation = 0d;
            double prevMassSecondDerivativeDeviation = 0d;

            if ((dataAll.getMassDeviation().size()-2)>=0){
                prevMassDeviation = dataAll.getMassDeviation().get(dataAll.getMassDeviation().size()-2);
                prevMassFirstDerivativeDeviation = dataAll.getMassFirstDerivativeDeviation().get(dataAll.getMassFirstDerivativeDeviation().size()-2);
                prevMassSecondDerivativeDeviation = dataAll.getMassSecondDerivativeDeviation().get(dataAll.getMassSecondDerivativeDeviation().size()-2);
/*
                System.out.println("Проверка предыдущих значений после{"+
                        "prevMassDeviation"+dataAll.getMassDeviation().getLast()+"\n"+
                        "prevMassFirstDerivativeDeviation"+dataAll.getMassFirstDerivativeDeviation().getLast()+"\n"+
                        "prevMassSecondDerivativeDeviation"+dataAll.getMassSecondDerivativeDeviation().getLast()+"}");
*/

            }



            double length = 0;
            double meltLevelHeight = 0;
            double realRadius = calcRealR((realMassList.get(realMassList.size() - 1) - realMassList.get(realMassList.size() - 2))/dataParam.getReadTact());
            double modelRadius = 0;
            if((dataAll.getModelMass().size()-2)>=0){
                modelRadius = calcRealR((dataAll.getModelMass().getLast()-dataAll.getModelMass().get(dataAll.getModelMass().size()-2))/dataParam.getModelTact());

 /*           System.out.println("Mass rate{" +"\n"+
                    "real Mass rate=" + ((realMassList.get(realMassList.size() - 1) - realMassList.get(realMassList.size() - 2))/readTact) +"\n"+
                    "model Mass rate=" + ((dataAll.getModelMass().getLast()-dataAll.getModelMass().get(dataAll.getModelMass().size()-2))/modelTact));
        */}
           /* System.out.println("Radius{" +"\n"+
                    "Radius=" + dataParam.getR() +"\n"+
                    "realRadius=" + realRadius +"\n"+
                    "modelRadius=" + modelRadius+"}"+"\n");*/


            double integralPartOfThePower = dataParam.getKi() * (massDeviation-prevMassDeviation);
            double proportionalPartOfThePower = dataParam.getKp() * (massFirstDerivativeDeviation - prevMassFirstDerivativeDeviation);
            double differentialPartOfThePower = dataParam.getKd() * (massSecondDerivativeDeviation - prevMassSecondDerivativeDeviation);

            double powerDeviation = integralPartOfThePower+proportionalPartOfThePower+differentialPartOfThePower;


            sourceList.add(0,new ModelProperty(localTime,//+
                    realMass,//+
                    modelMass,//+
                    modelMassFirstDerivative,//+
                    massDeviation,//+
                    massFirstDerivativeDeviation,//+
                    massSecondDerivativeDeviation,//+
                    integralPartOfThePower,//+
                    proportionalPartOfThePower,//+
                    differentialPartOfThePower,//+
                    powerDeviation,//+
                    PowerSetter.getPOWER(),//+
                    dataParam.getV_upper(),//+
                    length,
                    meltLevelHeight,
                    realRadius)//+
            );

            LinkedList<Double> linkedList = new LinkedList<>();

            linkedList.add(realMass);
            linkedList.add(modelMass);
            linkedList.add(modelMassFirstDerivative);
            linkedList.add(massDeviation);
            linkedList.add(massDeviation-prevMassDeviation);
            linkedList.add(massFirstDerivativeDeviation);
            linkedList.add(massFirstDerivativeDeviation - prevMassFirstDerivativeDeviation);
            linkedList.add(massSecondDerivativeDeviation);
            linkedList.add(massSecondDerivativeDeviation - prevMassSecondDerivativeDeviation);
            linkedList.add(integralPartOfThePower);
            linkedList.add(proportionalPartOfThePower);
            linkedList.add(differentialPartOfThePower);
            linkedList.add(powerDeviation);
            linkedList.add((double) PowerSetter.getPOWER());
            linkedList.add(dataParam.getV_upper());
            linkedList.add(length);
            linkedList.add(meltLevelHeight);
            linkedList.add(modelRadius);
            linkedList.add(realRadius);
            linkedList.add(dataParam.getR());
            try {
                ExcelFileWriter.writeToExcelFile(localTime,linkedList);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            modelMass = modelMass + modelMassFirstDerivative * dt;
            dataAll.getModelMass().add(modelMass);

            //если нажата кнопка начать управление мощностью
            setPower(powerDeviation);
            //обнуляем счетчик циклов расчета
            globalCount.set(0);

            System.out.println(sourceList.get(0));
        }
    }
    private static void modelCalculations(AtomicInteger globalCount,AtomicLong startLocal,ObservableList<ModelProperty> sourceList, ObservableList<Double> realMassList, DataAll dataAll){
        //При каждом запуске расчетов проверяем пусты ли массивы и инициализируем первым значением текущей массы
        //При каждом начале расчетов инициализируем массивы
        if (IsModelCalculationsStarted.get()&&sourceList.size()==0&&globalCount.get()%dataParam.getModelTact()==0){
            dataAll.initModelMass(realMassList.get(realMassList.size()-1));
            startLocal.set(System.currentTimeMillis());
        }
        //При каждом окончании расчетов очищаем массивы

        if (!IsModelCalculationsStarted.get()&&sourceList.size()!=0){
            sourceList.clear();
            dataAll.clear();
        }
        //startModelCalculations

        if (globalCount.get()%dataParam.getModelTact()==0&&IsModelCalculationsStarted.get()){

            String localTime = createDateFormatForTableView().format(System.currentTimeMillis() - startLocal.get());
            int dt = dataParam.getModelTact()*dataParam.getReadTact();
            //берем последний добавленный элемент в realMassList, текущую массу
            double realMass = realMassList.get(realMassList.size()-1);
            double modelMass = dataAll.getModelMass().getLast();
            double modelMassFirstDerivative = getModelMassFirstDerivative();

            double massDeviation = realMass - modelMass;
            double massFirstDerivativeDeviation = (massDeviation -dataAll.getMassDeviation().getLast())/dt;

            dataAll.getMassDeviation().add(massDeviation);
            double massSecondDerivativeDeviation = (massFirstDerivativeDeviation -dataAll.getMassFirstDerivativeDeviation().getLast())/dt;
            dataAll.getMassFirstDerivativeDeviation().add(massFirstDerivativeDeviation);
            dataAll.getMassSecondDerivativeDeviation().add(massSecondDerivativeDeviation);

            double prevMassDeviation = 0d;
            double prevMassFirstDerivativeDeviation = 0d;
            double prevMassSecondDerivativeDeviation = 0d;

            if ((dataAll.getMassDeviation().size()-2)>=0){
                prevMassDeviation = dataAll.getMassDeviation().get(dataAll.getMassDeviation().size()-2);
                prevMassFirstDerivativeDeviation = dataAll.getMassFirstDerivativeDeviation().get(dataAll.getMassFirstDerivativeDeviation().size()-2);
                prevMassSecondDerivativeDeviation = dataAll.getMassSecondDerivativeDeviation().get(dataAll.getMassSecondDerivativeDeviation().size()-2);
            }

            double length = 0;
            double meltLevelHeight = 0;
            double realRadius = calcRealR((realMassList.get(realMassList.size() - 1) - realMassList.get(realMassList.size() - 2))/dataParam.getReadTact());
            double modelRadius = 0;
            if((dataAll.getModelMass().size()-2)>=0){
                modelRadius = calcRealR((dataAll.getModelMass().getLast()-dataAll.getModelMass().get(dataAll.getModelMass().size()-2))/dataParam.getModelTact());
}

            double integralPartOfThePower = dataParam.getKi() * (massDeviation-prevMassDeviation);
            double proportionalPartOfThePower = dataParam.getKp() * (massFirstDerivativeDeviation - prevMassFirstDerivativeDeviation);
            double differentialPartOfThePower = dataParam.getKd() * (massSecondDerivativeDeviation - prevMassSecondDerivativeDeviation);

            double powerDeviation = integralPartOfThePower+proportionalPartOfThePower+differentialPartOfThePower;

            sourceList.add(0,new ModelProperty(localTime,//+
                    realMass,//+
                    modelMass,//+
                    modelMassFirstDerivative,//+
                    massDeviation,//+
                    massFirstDerivativeDeviation,//+
                    massSecondDerivativeDeviation,//+
                    integralPartOfThePower,//+
                    proportionalPartOfThePower,//+
                    differentialPartOfThePower,//+
                    powerDeviation,//+
                    PowerSetter.getPOWER(),//+
                    dataParam.getV_upper(),//+
                    length,
                    meltLevelHeight,
                    realRadius)//+
            );

            LinkedList<Double> linkedList = new LinkedList<>();

            linkedList.add(realMass);
            linkedList.add(modelMass);
            linkedList.add(modelMassFirstDerivative);
            linkedList.add(massDeviation);
            linkedList.add(massDeviation-prevMassDeviation);
            linkedList.add(massFirstDerivativeDeviation);
            linkedList.add(massFirstDerivativeDeviation - prevMassFirstDerivativeDeviation);
            linkedList.add(massSecondDerivativeDeviation);
            linkedList.add(massSecondDerivativeDeviation - prevMassSecondDerivativeDeviation);
            linkedList.add(integralPartOfThePower);
            linkedList.add(proportionalPartOfThePower);
            linkedList.add(differentialPartOfThePower);
            linkedList.add(powerDeviation);
            linkedList.add((double) PowerSetter.getPOWER());
            linkedList.add(dataParam.getV_upper());
            linkedList.add(length);
            linkedList.add(meltLevelHeight);
            linkedList.add(modelRadius);
            linkedList.add(realRadius);
            linkedList.add(dataParam.getR());
            try {
                ExcelFileWriter.writeToExcelFile(localTime,linkedList);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            modelMass = modelMass + modelMassFirstDerivative * dt;
            dataAll.getModelMass().add(modelMass);
            //если нажата кнопка начать управление мощностью
            setPower(powerDeviation);
            //обнуляем счетчик циклов расчета
            globalCount.set(0);
        }
    }

    static Double getModelMassFirstDerivative() {
        //Всегда одинакова до смены параметров?
        double denominator;
        double numerator1;
        double numerator2;
        denominator = Math.pow(dataParam.getR_cruc(), 2) - dataParam.getS_die() / Math.PI;
        numerator1 = dataParam.getRos() * Math.pow(dataParam.getR(), 2) * (Math.PI * Math.pow(dataParam.getR_cruc(), 2) - (dataParam.getS_die() - dataParam.getS_die_cr())) * dataParam.getV_upper();
        numerator2 = Math.PI * dataParam.getRol() * dataParam.getS_die_cr() * Math.pow(dataParam.getR_cruc(), 2) * dataParam.getV_lower();
        return (numerator1 - numerator2) / denominator;
    }

}
