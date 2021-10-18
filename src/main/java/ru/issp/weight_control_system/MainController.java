package ru.issp.weight_control_system;


import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import jssc.SerialPortException;
import ru.issp.weight_control_system.Model.ModelProperty;
import ru.issp.weight_control_system.ProdCons.FromByteToWeight;
import ru.issp.weight_control_system.ProdCons.ReadFromCom;
import ru.issp.weight_control_system.ProdCons.ReadFromFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.TimeZone;
import java.util.concurrent.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MainController implements Initializable {

    public Button OneSample;
    public Button Start;
    public Button Stop;
    public TextArea textArea;
    public LineChart<String,Number> lineChartWeight;
    public CategoryAxis xAxisLineChartWeight;
    public NumberAxis yAxisLineChartWeight;
    public Button toTable;
    public Button toSetPower;

    private Scene setPowerScene;
    private Scene setTableScene;
    
    final int WINDOW_SIZE = 20;
    private ObservableList<ModelProperty> list = FXCollections.observableArrayList();
    public LineChart lineChartWeight2;
    //TODO Сделать оди для всех графиков SimpleDateFormat и метод отрисовки
    // - разобраться с синхронизацией времени(чтобы на графике отображались актуальные данные)
    // - сделать дополнительное окно для ввода параметров
    // - добавить возможность и кнопки управления мощностью
    BlockingQueue<byte[]> q = new LinkedBlockingQueue<>();
    ReadFromFile p = new ReadFromFile(q);//Читаем из файла
    //ReadFromCom p = new ReadFromCom(q);//Читаем из COM-порта
    FromByteToWeight c1 = new FromByteToWeight(q);



    private ScheduledExecutorService scheduledExecutorService;

    public MainController() throws FileNotFoundException {
    }

    public ScheduledExecutorService getScheduledExecutorService() {
        return scheduledExecutorService;
    }




    public void OneSampleButtonClicked(ActionEvent actionEvent) {
        System.out.println("Initialize chart");
        textArea.appendText(OneSample.getText()+ System.lineSeparator());
        //FXML defining the axes and creating the line chart with two axis created above

    }

    public void StartButtonClicked(ActionEvent actionEvent) {
        System.out.println("Start");

        textArea.appendText(Start.getText()+ System.lineSeparator());
        addDataToChart();
    }

    public void StopButtonClicked(ActionEvent actionEvent) throws SerialPortException {
        System.out.println("Stop");

        textArea.appendText(Stop.getText()+ System.lineSeparator());


        //Останавливаем отображение
        scheduledExecutorService.shutdown();
        //TODO организовать закрытие порта при нажатии на закрытие программы
        //p.serialPort.closePort();
    }
    //обеспечиваем связность контроллера с главным классом

    public void addDataToChart(){
        //defining a series to display data
        XYChart.Series<String,Number> series = new XYChart.Series<>();
        series.setName("weight(t)");
        long start = System.currentTimeMillis();

        // this is used to display time in HH:mm:ss format

        // setup a scheduled executor to periodically put data into the chart
        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();

        // put dummy data onto graph per second
        scheduledExecutorService.scheduleAtFixedRate(() -> {
            // get a random integer between 0-10
            try {
                //Long weight = c1.getOutputQueue().take();
                double weight = list.get(list.size()-1).getRealMass();
                System.out.println("В листе столько-то значений " + list.size());
            // Update the chart
            Platform.runLater(() -> {
                        series.getData().add(
                        new XYChart.Data<>(createDateFormat().format(System.currentTimeMillis() - start), weight));

                if (series.getData().size() > WINDOW_SIZE)
                    series.getData().remove(0);
            });
            } catch (Throwable e) {
                e.printStackTrace();
                Logger.getLogger(MainController.class.getName()).log(Level.SEVERE,"Caught exception in ScheduledExecutorService.",e);
            }}, 0, 1000, TimeUnit.MILLISECONDS);
        lineChartWeight.getData().add(series);

    }
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("Запускаем потоки producer/consumer ");
/*        new Thread(p).start();
        new Thread(c1).start();*/
        addDataToChart();
        lineChartWeight2.setVisible(false);


    }
    public void setDataList(ObservableList<ModelProperty> list) {
        this.list = list;
    }
//TODO Как добиться того что при смене сцены оставался отрисовываться график?
// Видимо надо делать его в отдельном Pane и скрывать или показывать

    public void switchToSetPowerSceneButtonClicked(ActionEvent actionEvent) throws IOException {
        Stage primaryStage = (Stage)((Node)actionEvent.getSource()).getScene().getWindow();
        primaryStage.setScene(setPowerScene);
    }
    public void switchToTableSceneButtonClicked(ActionEvent actionEvent) throws IOException {
        Stage primaryStage = (Stage)((Node)actionEvent.getSource()).getScene().getWindow();
        primaryStage.setScene(setTableScene);
    }

    public void setSetPowerSceneScene(Scene scene) {
setPowerScene = scene;
    }
    public void setTableScene(Scene scene) {
        setTableScene = scene;
    }
    
    public void switchSceneButtonClicked1(ActionEvent actionEvent) throws IOException {
//так можно скрывать графики на gridpane
        if (lineChartWeight.isVisible()){
            lineChartWeight.setVisible(false);
            lineChartWeight2.setVisible(true);
        }else {
            lineChartWeight.setVisible(true);
            lineChartWeight2.setVisible(false);
        }

    }
    private static SimpleDateFormat createDateFormat() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss.S");
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        return dateFormat;
    }
}
