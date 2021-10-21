package ru.issp.weight_control_system;


import javafx.collections.ObservableList;
import ru.issp.weight_control_system.Model.Model;
import ru.issp.weight_control_system.Model.ModelProperty;
import ru.issp.weight_control_system.ProdCons.FromByteToWeight;
import ru.issp.weight_control_system.ProdCons.ReadFromFile;
import ru.issp.weight_control_system.data.DataAll;
import ru.issp.weight_control_system.data.DataParam;

import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.TimeZone;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class DataTransfer {
static AtomicBoolean IsModelCalculationsStarted = new AtomicBoolean(false);

    public static void transferData(ObservableList<ModelProperty> sourceList, ObservableList<Double> realMassList) throws FileNotFoundException {

        BlockingQueue<byte[]> q = new LinkedBlockingQueue<>();
        ReadFromFile p = new ReadFromFile(q);//Читаем из файла
        //ReadFromCom p = new ReadFromCom(q);//Читаем из COM-порта
        FromByteToWeight c1 = new FromByteToWeight(q);
        ScheduledExecutorService scheduledExecutorService;
        DataAll dataAll = new DataAll();
        DataParam dataParam = new DataParam();


        System.out.println("Запускаем потоки producer/consumer ");
        new Thread(p).start();
        new Thread(c1).start();

        long start = System.currentTimeMillis();
        AtomicInteger globalCount= new AtomicInteger();
        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        scheduledExecutorService.scheduleAtFixedRate(() -> {
            //TODO Возможно стоит посмотреть в сторону кольцевого буфера.
            int size = c1.getOutputQueue().size();
            //System.out.println(size);
            double currentMass = 0d;
            double sum = 0d;

            try {
                //currentMass = c1.getOutputQueue().take().doubleValue();
                //Набираем все данные за dt и усредняем
                for (int i = 0; i < size; i++) {
                    sum = sum+c1.getOutputQueue().take();
                    //System.out.print(" "+count++);
                }

               if (size!=0)currentMass = sum/size;

            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            realMassList.add(currentMass);
            System.out.println("В main получаем вес " + currentMass);
            System.out.println("globalCount = "+globalCount);
            System.out.println(globalCount.get()%8);
            System.out.println(IsModelCalculationsStarted.get());

            //При каждом запуске расчетов проверяем пусты ли массивы и инициализируем первым значением текущей массы
            //TODO проверит согласование данных с текущеЙ и расчетной массы

            if (IsModelCalculationsStarted.get()&&sourceList.size()==0&&globalCount.get()%8==0){
                System.out.println("Сработал if инициализации");
                System.out.println(sourceList.size());
                System.out.println(dataAll);
                dataAll.initModelMass(currentMass);
                System.out.println(dataAll);
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

            if (globalCount.get()%8==0&&IsModelCalculationsStarted.get()){


            System.out.println("Текущее время :" + createDateFormat().format(System.currentTimeMillis() - start));
            //Здесь возможно надо разделить данные current mass добавлять в один лист для отрисовки на графике работает с самого начала программы и не изменяется до ее завершения.

            //и второй лист для управления, наполнение включается также по кнопке ("Включить расчет")
            // с приравниванием начальной модельной массы текущей реальной, и получением из UI коэффициентов ПИД регулятора
            // наполнять раз в n тактов и запускать управление мощностью по кнопке ("Запустить управление").

            //здесь у нас создается два объекта model и ModelProperty.
            Model model = new Model(1, dataParam, dataAll, currentMass);

            sourceList.add(new ModelProperty(
                    model.realMass,
                    model.modelMass,
                    model.modelMassDeviation,
                    model.modelFirstDerivativeDeviation,
                    model.modelSecondDerivativeDeviation));


            globalCount.set(1);
            }
            globalCount.incrementAndGet();
        }, 0, 1, TimeUnit.SECONDS);

    }
    private static SimpleDateFormat createDateFormat() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss.S");
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        return dateFormat;
    }

}
