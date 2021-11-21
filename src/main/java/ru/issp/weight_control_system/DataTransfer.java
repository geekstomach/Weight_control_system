package ru.issp.weight_control_system;


import javafx.collections.ObservableList;
import jssc.SerialPortException;
import ru.issp.weight_control_system.Model.Model;
import ru.issp.weight_control_system.Model.ModelProperty;
import ru.issp.weight_control_system.ProdCons.ExcelFileWriter;
import ru.issp.weight_control_system.ProdCons.FromByteToWeight;
import ru.issp.weight_control_system.ProdCons.ReadFromFile;
import ru.issp.weight_control_system.data.DataAll;
import ru.issp.weight_control_system.data.DataParam;
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
static int modelTact = 3;
static int readTact = 1;

    public static void transferData(ObservableList<ModelProperty> sourceList, ObservableList<Double> realMassList, ObservableList<Double> modelRadiusList) throws FileNotFoundException {

//инициализируем получение данных
        BlockingQueue<byte[]> q = new LinkedBlockingQueue<>();
        ReadFromFile p = new ReadFromFile(q);//Читаем из файла
        //ReadFromCom p = new ReadFromCom(q);//Читаем из COM-порта
        TestData testData = new TestData();
        FromByteToWeight c1 = new FromByteToWeight(q);
        ScheduledExecutorService scheduledExecutorService;

        DataAll dataAll = new DataAll();
//Запускаем получение данных
        System.out.println("Запускаем потоки producer/consumer ");
        new Thread(p).start();
        new Thread(c1).start();
        new Thread(testData).start();
//запускаем расчеты по расписанию
        long startGlobal = System.currentTimeMillis();
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

//текущая масса(средняя за прошлый интервал)
// current mass добавляем в один лист для отрисовки на графике, т.к. работает с самого начала программы и не изменяется до ее завершения.
            realMassList.add(currentMass);

            //расчет радиуса
            if (realMassList.size()>=2) {
            modelRadiusList.add(calcRealR((realMassList.get(realMassList.size() - 1) - realMassList.get(realMassList.size() - 2)))/readTact);
            }

            //TODO добавить расчет уровня расплава
            //TODO проверить согласование данных с текущеЙ и расчетной массы
            //следующий блок работает раз в modelTact
            modelCalculations(globalCount,startLocal,sourceList,realMassList,dataAll);
            //увеличиваем счетчик циклов расчета
            globalCount.incrementAndGet();

        }, 0, readTact, TimeUnit.SECONDS);

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

            //Может быть стоит сделать константу POWER double, а кастовать в инт только при отправке в генератор
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

    private static void modelCalculations1(AtomicInteger globalCount,AtomicLong startLocal,ObservableList<ModelProperty> sourceList, ObservableList<Double> realMassList, DataAll dataAll){
        //При каждом запуске расчетов проверяем пусты ли массивы и инициализируем первым значением текущей массы
        //При каждом начале расчетов инициализируем массивы
        if (IsModelCalculationsStarted.get()&&sourceList.size()==0&&globalCount.get()%modelTact==0){
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

        if (globalCount.get()%modelTact==0&&IsModelCalculationsStarted.get()){
            System.out.println("currentMass "+realMassList.get(realMassList.size()-1));
            System.out.println(dataAll);

            String time = createDateFormatForTableView().format(System.currentTimeMillis() - startLocal.get());
            double realMass = realMassList.get(realMassList.size()-1);
            double modelMass = dataAll.getModelMass().getLast();
            double modelMassFirstDerivative = dataAll.getModelMassFirstDerivative().getLast();
            double massDeviation = dataAll.getMassDeviation().getLast();
            double massFirstDerivativeDeviation = dataAll.getMassFirstDerivativeDeviation().getLast();
            double massSecondDerivativeDeviation = dataAll.getMassSecondDerivativeDeviation().getLast();
            double length = 0;
            double meltLevelHeight = 0;
            double radius = 0;

            Model model = new Model(readTact*modelTact, dataParam, dataAll, realMassList.get(realMassList.size()-2));

            System.out.println(model);

            double integralPartOfThePower = dataParam.getKi() * (model.massDeviation -massDeviation);
            double proportionalPartOfThePower = dataParam.getKp() * (model.massFirstDerivativeDeviation - massFirstDerivativeDeviation);
            double differentialPartOfThePower = dataParam.getKd() * (model.massSecondDerivativeDeviation - massSecondDerivativeDeviation);

            double powerDeviation = integralPartOfThePower+proportionalPartOfThePower+differentialPartOfThePower;


            sourceList.add(0,new ModelProperty(time,//+
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
                    radius)//+
            );

            //если нажата кнопка начать управление мощностью
            setPower(powerDeviation);
            //обнуляем счетчик циклов расчета
            globalCount.set(0);
        }
    }
    private static void modelCalculations(AtomicInteger globalCount,AtomicLong startLocal,ObservableList<ModelProperty> sourceList, ObservableList<Double> realMassList, DataAll dataAll){
        //При каждом запуске расчетов проверяем пусты ли массивы и инициализируем первым значением текущей массы
        //При каждом начале расчетов инициализируем массивы
        if (IsModelCalculationsStarted.get()&&sourceList.size()==0&&globalCount.get()%modelTact==0){
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

        if (globalCount.get()%modelTact==0&&IsModelCalculationsStarted.get()){
            System.out.println();
            System.out.println("Начало");
            System.out.println("currentMass "+realMassList.get(realMassList.size()-1));
            System.out.println(dataAll.lastToString());



            String localTime = createDateFormatForTableView().format(System.currentTimeMillis() - startLocal.get());
            double realMass = realMassList.get(realMassList.size()-1);
            double modelMass = dataAll.getModelMass().getLast();
            double modelMassFirstDerivative = dataAll.getModelMassFirstDerivative().getLast();
            double massDeviation = dataAll.getMassDeviation().getLast();
            double prevMassDeviation = 0d;
            double prevMassFirstDerivativeDeviation = 0d;
            double prevMassSecondDerivativeDeviation = 0d;

            if ((dataAll.getMassDeviation().size()-2)>=0){
                prevMassDeviation = dataAll.getMassDeviation().get(dataAll.getMassDeviation().size()-2);
                prevMassFirstDerivativeDeviation = dataAll.getMassDeviation().get(dataAll.getMassFirstDerivativeDeviation().size()-2);
                prevMassSecondDerivativeDeviation = dataAll.getMassDeviation().get(dataAll.getMassSecondDerivativeDeviation().size()-2);
            }

            double massFirstDerivativeDeviation = dataAll.getMassFirstDerivativeDeviation().getLast();
            double massSecondDerivativeDeviation = dataAll.getMassSecondDerivativeDeviation().getLast();
            double length = 0;
            double meltLevelHeight = 0;
            double radius = calcRealR((realMassList.get(realMassList.size() - 1) - realMassList.get(realMassList.size() - 2)))/readTact;

            Model model = new Model(readTact*modelTact, dataParam, dataAll, realMassList.get(realMassList.size()-1));

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
                    radius)//+
            );

            LinkedList linkedList = new LinkedList<>();
            linkedList.add(localTime);
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
            linkedList.add(radius);
            linkedList.add(dataParam.getR());

            try {
                ExcelFileWriter.writeToExcelFile(linkedList);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            //если нажата кнопка начать управление мощностью
            setPower(powerDeviation);
            //обнуляем счетчик циклов расчета
            globalCount.set(0);

            System.out.println(sourceList.get(0));
        }
    }
}
