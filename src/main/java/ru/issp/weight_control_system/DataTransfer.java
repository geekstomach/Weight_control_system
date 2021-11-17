package ru.issp.weight_control_system;


import javafx.collections.ObservableList;
import jssc.SerialPortException;
import ru.issp.weight_control_system.Model.Model;
import ru.issp.weight_control_system.Model.ModelProperty;
import ru.issp.weight_control_system.ProdCons.FromByteToWeight;
import ru.issp.weight_control_system.ProdCons.ReadFromFile;
import ru.issp.weight_control_system.data.DataAll;
import ru.issp.weight_control_system.data.DataParam;
import ru.issp.weight_control_system.utils.PowerSetter;

import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.TimeZone;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;


public class DataTransfer {
static AtomicBoolean IsModelCalculationsStarted = new AtomicBoolean(false);
static AtomicBoolean IsPowerControlStarted = new AtomicBoolean(false);
static  DataParam dataParam = new DataParam();
static int modelTact = 9;
static int readTact = 3;

    public static void transferData(ObservableList<ModelProperty> sourceList, ObservableList<Double> realMassList, ObservableList<Double> modelRadiusList) throws FileNotFoundException {

//инициализируем получение данных
        BlockingQueue<byte[]> q = new LinkedBlockingQueue<>();
        ReadFromFile p = new ReadFromFile(q);//Читаем из файла
        //ReadFromCom p = new ReadFromCom(q);//Читаем из COM-порта
        FromByteToWeight c1 = new FromByteToWeight(q);
        ScheduledExecutorService scheduledExecutorService;

        DataAll dataAll = new DataAll();
//Запускаем получение данных
        System.out.println("Запускаем потоки producer/consumer ");
        new Thread(p).start();
        new Thread(c1).start();
//запускаем расчеты по расписанию
        long startGlobal = System.currentTimeMillis();
        AtomicLong startLocal = new AtomicLong(System.currentTimeMillis());
        AtomicInteger globalCount= new AtomicInteger();//используется для запуска startModelCalculations раз в n тактов
        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        scheduledExecutorService.scheduleAtFixedRate(() -> {
            //Инициализируем данные для sourceList
            String time;
            double realMass;
            double modelMass;
            double modelMassFirstDerivative;
            double massDeviation;
            double massFirstDerivativeDeviation;
            double massSecondDerivativeDeviation;
            double integralPartOfThePower = 0;
            double proportionalPartOfThePower = 0;
            double differentialPartOfThePower = 0;
            double powerDeviation = 0;
            double length = 0;
            double meltLevelHeight = 0;
            double radius = 0;
//Получаем все данные веса за интервал и усредняем
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
            }
//текущая масса(средняя за прошлый интервал)
// current mass добавляем в один лист для отрисовки на графике, т.к. работает с самого начала программы и не изменяется до ее завершения.
            realMassList.add(currentMass);

//расчет радиуса
            if (realMassList.size()>=2) {
                System.out.println("Реальный радиус :" + calcRealR(realMassList.get(realMassList.size() - 1) - realMassList.get(realMassList.size() - 2)));
            modelRadiusList.add(calcRealR(realMassList.get(realMassList.size() - 1) - realMassList.get(realMassList.size() - 2)));
            radius = calcRealR(realMassList.get(realMassList.size() - 1) - realMassList.get(realMassList.size() - 2));

            }
//Расчет уровня расплава в тигле
//TODO добавить рассчет уровня расплава
            System.out.println("В main получаем вес " + currentMass);
            System.out.println("globalCount = "+globalCount);
            System.out.println(globalCount.get()%modelTact);
            System.out.println(IsModelCalculationsStarted.get());
//TODO проверить согласование данных с текущеЙ и расчетной массы
//следующий блок работает раз в modelTact

//При каждом запуске расчетов проверяем пусты ли массивы и инициализируем первым значением текущей массы
            //При каждом начале расчетов инициализируем массивы
            if (IsModelCalculationsStarted.get()&&sourceList.size()==0&&globalCount.get()%modelTact==0){
                System.out.println("Сработал if инициализации");
                System.out.println(sourceList.size());
                System.out.println(dataAll);
                dataAll.initModelMass(currentMass);
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


            System.out.println("Текущее время работы от старта приложения :" + createDateFormat().format(System.currentTimeMillis() - startGlobal));
            System.out.println("Текущее время работы от начала расчетов :" + createDateFormat().format(System.currentTimeMillis() - startLocal.get()));
            time = createDateFormat().format(System.currentTimeMillis() - startLocal.get());

            //и второй лист для управления, наполнение включается также по кнопке ("Включить расчет")
            // с приравниванием начальной модельной массы текущей реальной, и получением из UI коэффициентов ПИД регулятора
            // наполнять раз в n тактов и запускать управление мощностью по кнопке ("Запустить управление").

            //здесь у нас создается объект model, где рассчитываются данные.
            Model model = new Model(readTact*modelTact, dataParam, dataAll, currentMass);
                realMass = model.realMass;
                modelMass = model.modelMass;
                modelMassFirstDerivative = model.modelFirstDerivative;
                massDeviation = model.modelMassDeviation;
                massFirstDerivativeDeviation = model.modelFirstDerivativeDeviation;
                massSecondDerivativeDeviation = model.modelSecondDerivativeDeviation;

                //TODO избавиться от sourceList в рассечете мощности
            if (sourceList.size()>=2) {
                ArrayList<Double> strings = new ArrayList<>();
                //расчет требуемого управления
                integralPartOfThePower = dataParam.getKp() * sourceList.get(0).getMassFirstDerivativeDeviationProperty();
                proportionalPartOfThePower = dataParam.getKi() * sourceList.get(0).getMassSecondDerivativeDeviationProperty();
                differentialPartOfThePower = dataParam.getKd() * (sourceList.get(0).getMassSecondDerivativeDeviationProperty() - sourceList.get(1).getMassSecondDerivativeDeviationProperty());

                double dP = integralPartOfThePower+proportionalPartOfThePower+differentialPartOfThePower;

                strings.add(integralPartOfThePower);
                strings.add(proportionalPartOfThePower);
                strings.add(differentialPartOfThePower);

                powerDeviation = dP;

                //если нажата кнопка начать управление мощностью
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
            strings.add((double) PowerSetter.getPOWER());
            PowerSetter.setPower((int) (PowerSetter.getPOWER()+dP));
            strings.add((double) PowerSetter.getPOWER());
        } catch (SerialPortException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
                System.out.println(strings);
            }
            globalCount.set(1);

//добавляем полученные данные в НАЧАЛО списка
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
            }
            globalCount.incrementAndGet();

           // System.out.println(sourceList.get(0).getRadiusProperty()+" "+radius);


        }, 0, readTact, TimeUnit.SECONDS);

    }


    private static SimpleDateFormat createDateFormat() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss.S");
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        return dateFormat;
    }

    private static double calcRealR (double dM){
        double realR;
        double one = (Math.pow(dataParam.getR_cruc(),2)-dataParam.getS_die()/Math.PI)*dM;
        double two = Math.PI*dataParam.getRol()*Math.pow(dataParam.getR_cruc(),2)*dataParam.getS_die_cr()*dataParam.getV_cr_upper();
        double three = dataParam.getRos()*(Math.PI*Math.pow(dataParam.getR_cruc(),2)-(dataParam.getS_die()-dataParam.getS_die_cr()))*dataParam.getV_upper();
        try {
            realR = (one+two)/three;
        }catch (RuntimeException e){
            System.out.println("Деление на ноль");
            realR = -1d;
        }
    return realR;}
}
