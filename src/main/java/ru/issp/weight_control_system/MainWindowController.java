package ru.issp.weight_control_system;

import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.ToggleButton;
import javafx.stage.Stage;
import ru.issp.weight_control_system.Model.ModelProperty;
import ru.issp.weight_control_system.utils.PowerSetter;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ResourceBundle;
import java.util.TimeZone;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MainWindowController implements Initializable {


    public ToggleButton startModelCalculations;
    public ToggleButton startControl;
    public Button toSetPower;
    public LineChart<String,Number> lineChartRadius;
    public LineChart<String,Number> lineChartWeight;
    public CategoryAxis xAxisLineChartWeight;
    public NumberAxis yAxisLineChartWeight;
    public CategoryAxis xAxisLineChartRadius;
    public NumberAxis yAxisLineChartRadius;
    public Spinner<Double> pullingRateSpinner;
    public Label pullingRateLabel;
    public Label powerLabel;

    public static SimpleIntegerProperty POWER_PROPERTY;

    private Scene setPowerScene;

    final int WINDOW_SIZE = 20;
    private ObservableList<ModelProperty> list = FXCollections.observableArrayList();
    private ObservableList<Double> realMassList = FXCollections.observableArrayList();
    private ObservableList<Double> modelRadiusList = FXCollections.observableArrayList();
    public void addDataToChart() {
        //defining a series to display data
        XYChart.Series<String,Number> seriesWeight = new XYChart.Series<>();
        XYChart.Series<String,Number> seriesRadius = new XYChart.Series<>();
        seriesWeight.setName("weight(t)");
        seriesRadius.setName("radius(t)");
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
                    
                    long now =System.currentTimeMillis() - start;
                    
                    seriesWeight.getData().add(
                            new XYChart.Data<>(createDateFormat().format(now), finalWeight));
                    if (modelRadiusList.size()!=0) {
                        seriesRadius.getData().add(
                                new XYChart.Data<>(createDateFormat().format(now), modelRadiusList.get(modelRadiusList.size() - 1)));
                    }
                    if (seriesWeight.getData().size() > WINDOW_SIZE)
                        seriesWeight.getData().remove(0);
                    
                    if (seriesRadius.getData().size() > WINDOW_SIZE)
                        seriesRadius.getData().remove(0);
                });
            } catch (Throwable e) {
                e.printStackTrace();
                Logger.getLogger(MainWindowController.class.getName()).log(Level.SEVERE,"Caught exception in ScheduledExecutorService.",e);
            }}, 0, 1000, TimeUnit.MILLISECONDS);

        lineChartWeight.getData().add(seriesWeight);
        lineChartRadius.getData().add(seriesRadius);

    }

    public void setSetPowerScene(Scene scene) {
        setPowerScene = scene;
    }

    public void setDataList(ObservableList<ModelProperty> sourceList, ObservableList<Double> realMassList, ObservableList<Double> generalList) {
        list = sourceList;
        this.realMassList = realMassList;
        this.modelRadiusList = generalList;
        addDataToChart();
    }

    public void startModelCalculations() {
        DataTransfer.IsModelCalculationsStarted.set(startModelCalculations.isSelected());
    }

    public void startControl() {
        //запускать только при нажатой кнопке startModelCalculations
        DataTransfer.IsPowerControlStarted.set(startModelCalculations.isSelected());
    }

    public void switchToSetPowerSceneButtonClicked(ActionEvent actionEvent) {
        Stage primaryStage = (Stage)((Node)actionEvent.getSource()).getScene().getWindow();
        primaryStage.setScene(setPowerScene);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        System.out.println(PowerSetter.getPOWER());
/*pullingRateSpinner.valueProperty().addListener((obs, oldValue, newValue) ->{
    System.out.println("Значение спиннера изменилось с "+oldValue+" на "+newValue);
    DataTransfer.dataParam.setR(pullingRateSpinner.getValue());
    //pullingRateLabel.setText(String.valueOf(DataTransfer.dataParam.getR()));
        });*/
pullingRateLabel.textProperty().bind(pullingRateSpinner.valueProperty().asString());
//powerLabel.textProperty().bind(POWER_PROPERTY.asString());
        System.out.println(POWER_PROPERTY);

//powerLabel.textProperty().bind(new SimpleIntegerProperty(PowerSetter.getPOWER()).asString());
      }

    private static SimpleDateFormat createDateFormat() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        return dateFormat;
    }
}
/*
на место верхнего графика поставить кнопки переключения сежду графиками
public void switchSceneButtonClicked1(ActionEvent actionEvent) {
//так можно скрывать графики на gridpane
        if (lineChartWeight.isVisible()){
        lineChartWeight.setVisible(false);
        lineChartWeight2.setVisible(true);
        }else {
        lineChartWeight.setVisible(true);
        lineChartWeight2.setVisible(false);
        }

        }*/
