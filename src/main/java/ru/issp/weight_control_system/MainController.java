package ru.issp.weight_control_system;


import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import ru.issp.weight_control_system.Model.ModelProperty;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ResourceBundle;
import java.util.TimeZone;
import java.util.concurrent.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MainController implements Initializable {

    public TextArea textArea;
    public LineChart<String,Number> lineChartWeight;
    public CategoryAxis xAxisLineChartWeight;
    public NumberAxis yAxisLineChartWeight;
    public Button toTable;
    public Button toSetPower;
    public ToggleButton startModelCalculations;
    public CategoryAxis xAxisLineChartWeight2;
    public NumberAxis yAxisLineChartWeight2;

    private Scene setPowerScene;
    private Scene setTableScene;
    
    final int WINDOW_SIZE = 20;
    private ObservableList<ModelProperty> list = FXCollections.observableArrayList();
    private ObservableList<Double> realMassList = FXCollections.observableArrayList();


    public LineChart<String,Number> lineChartWeight2;
    //TODO Сделать оди для всех графиков SimpleDateFormat и метод отрисовки
    // - разобраться с синхронизацией времени(чтобы на графике отображались актуальные данные)
    // - сделать дополнительное окно для ввода параметров
    // - добавить возможность и кнопки управления мощностью


    public MainController() {
    }


    //обеспечиваем связность контроллера с главным классом
    //TODO Возможно стоит включить в модель параметр время чтобы не делать scheduledExecutorService в контроллере
    public void addDataToChart() {
        //defining a series to display data

        XYChart.Series<String,Number> series = new XYChart.Series<>();
        series.setName("weight(t)");
        long start = System.currentTimeMillis();

        // this is used to display time in HH:mm:ss format

        // setup a scheduled executor to periodically put data into the chart
        ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();

        // put dummy data onto graph per second
        scheduledExecutorService.scheduleAtFixedRate(() -> {
            // get a random integer between 0-10
            try {
                //Long weight = c1.getOutputQueue().take();

                //TODO разобраться с инициализацией чтоб эксепшэнов небыло
                double weight = 0;
                //if (list.size()!=0)weight = list.get(list.size()-1).getRealMass();
                if (realMassList.size()!=0)weight = realMassList.get(realMassList.size()-1);
                double finalWeight = weight;

            // Update the chart
                Platform.runLater(() -> {
                        series.getData().add(
                        new XYChart.Data<>(createDateFormat().format(System.currentTimeMillis() - start), finalWeight));

                if (series.getData().size() > WINDOW_SIZE)
                    series.getData().remove(0);
            });
            } catch (Throwable e) {
                e.printStackTrace();
                Logger.getLogger(MainController.class.getName()).log(Level.SEVERE,"Caught exception in ScheduledExecutorService.",e);
            }}, 1, 1000, TimeUnit.MILLISECONDS);
        lineChartWeight.getData().add(series);

    }
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        lineChartWeight2.setVisible(false);


    }
    public void setDataList(ObservableList<ModelProperty> sourceList, ObservableList<Double> generalList) {
        list = sourceList;
        realMassList = generalList;
        addDataToChart();
    }

//TODO Как добиться того что при смене сцены оставался отрисовываться график?
// Видимо надо делать его в отдельном Pane и скрывать или показывать

    public void switchToSetPowerSceneButtonClicked(ActionEvent actionEvent) {
        Stage primaryStage = (Stage)((Node)actionEvent.getSource()).getScene().getWindow();
        primaryStage.setScene(setPowerScene);
    }
    public void switchToTableSceneButtonClicked(ActionEvent actionEvent) {
        Stage primaryStage = (Stage)((Node)actionEvent.getSource()).getScene().getWindow();
        primaryStage.setScene(setTableScene);
    }

    public void setSetPowerSceneScene(Scene scene) {
        setPowerScene = scene;
    }
    public void setTableScene(Scene scene) {
        setTableScene = scene;
    }
    
    public void switchSceneButtonClicked1(ActionEvent actionEvent) {
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
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        return dateFormat;
    }

    public void StartModelCalculations(ActionEvent actionEvent) {
        DataTransfer.IsModelCalculationsStarted.set(startModelCalculations.isSelected());
        DataTransfer.IsPowerControlStarted.set(startModelCalculations.isSelected());

    }


}
