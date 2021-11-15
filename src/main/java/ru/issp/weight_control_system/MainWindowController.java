package ru.issp.weight_control_system;

import javafx.application.Platform;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
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
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
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
    public Spinner<Double> rSpinner;
    public Spinner<Double> kpSpinner;
    public Spinner<Double> kiSpinner;
    public Spinner<Double> kdSpinner;
    public Spinner<Double> dNPmaxSpinner;
    public Spinner<Double> kPdefpSpinner;
    public Spinner<Double> kPdefmSpinner;

    public Label pullingRateLabel;
    public Label powerLabel;

    public static SimpleIntegerProperty POWER_PROPERTY = new SimpleIntegerProperty(PowerSetter.getPOWER());

    public ChoiceBox<String> switchChartChoiceBox;
    public LineChart<String,Number> lineChartM;
    public CategoryAxis xAxisLineChartM;
    public NumberAxis yAxisLineChartM;
    public LineChart<String,Number> lineChartdM;
    public CategoryAxis xAxisLineChartdM;
    public NumberAxis yAxisLineChartdM;
    public LineChart<String,Number> lineChartddM;
    public CategoryAxis xAxisLineChartddM;
    public NumberAxis yAxisLineChartddM;

    public TableView<ModelProperty> table;

    public TableColumn<ModelProperty,String> time;
    public TableColumn<ModelProperty, Double> realMass;
    public TableColumn<ModelProperty,Double> modelMass;
    public TableColumn<ModelProperty,Double> modelMassDeviation;
    public TableColumn<ModelProperty,Double> modelFirstDerivativeDeviation;
    public TableColumn<ModelProperty,Double> modelSecondDerivativeDeviation;

    private Scene setPowerScene;

    final int WINDOW_SIZE = 20;
    private ObservableList<ModelProperty> list = FXCollections.observableArrayList();
    private ObservableList<Double> realMassList = FXCollections.observableArrayList();
    private ObservableList<Double> modelRadiusList = FXCollections.observableArrayList();

    XYChart.Series<String,Number> seriesM = new XYChart.Series<>();
    XYChart.Series<String,Number> seriesdM = new XYChart.Series<>();
    XYChart.Series<String,Number> seriesddM = new XYChart.Series<>();

    public void addDataToChart() {
        //defining a series to display data
        XYChart.Series<String,Number> seriesWeight = new XYChart.Series<>();
        XYChart.Series<String,Number> seriesRadius = new XYChart.Series<>();


        seriesWeight.setName("weight(t)");
        seriesRadius.setName("radius(t)");
        seriesM.setName("Model M");
        seriesdM.setName("Model dM");
        seriesddM.setName("Model ddM");

        long start = System.currentTimeMillis();

        // this is used to display time in HH:mm:ss format

        // setup a scheduled executor to periodically put data into the chart
        ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();

        // put dummy data onto graph per second
        scheduledExecutorService.scheduleAtFixedRate(() -> {
            // get a random integer between 0-10
            try {
                //TODO разобраться с инициализацией чтоб эксепшэнов небыло
                double weight = 0;
                //if (list.size()!=0)weight = list.get(list.size()-1).getRealMass();
                if (realMassList.size()!=0)weight = realMassList.get(realMassList.size()-1);
                double finalWeight = weight;

                // Update the chart
                Platform.runLater(() -> {

                    long now =System.currentTimeMillis() - start;


                    //График текущего веса
                    seriesWeight.getData().add(
                            new XYChart.Data<>(createDateFormat().format(now), finalWeight));
                    //График расчетного радиуса
                    if (modelRadiusList.size()!=0) {
                    seriesRadius.getData().add(
                            new XYChart.Data<>(createDateFormat().format(now), modelRadiusList.get(modelRadiusList.size() - 1)));
                    }
                    //TODO Возможно стоит добавить в иф что эти расчеты раз в 8 тактов как в TransferData
                    if (startModelCalculations.isSelected()&&list.size()!=0) {
                        seriesM.getData().add(new XYChart.Data<>(createDateFormat().format(now), list.get(0).getModelMassProperty()));
                        seriesdM.getData().add(new XYChart.Data<>(createDateFormat().format(now), list.get(0).getMassFirstDerivativeDeviationProperty()));
                        seriesddM.getData().add(new XYChart.Data<>(createDateFormat().format(now), list.get(0).getMassSecondDerivativeDeviationProperty()));
                    }
                    //ограничение отображаемых данных
                    if (seriesWeight.getData().size() > WINDOW_SIZE)
                        seriesWeight.getData().remove(0);
                    if (seriesRadius.getData().size() > WINDOW_SIZE)
                        seriesRadius.getData().remove(0);
                    if (seriesM.getData().size() > WINDOW_SIZE)
                        seriesM.getData().remove(0);
                    if (seriesdM.getData().size() > WINDOW_SIZE)
                        seriesdM.getData().remove(0);
                    if (seriesddM.getData().size() > WINDOW_SIZE)
                        seriesddM.getData().remove(0);
                });
            } catch (Throwable e) {
                e.printStackTrace();
                Logger.getLogger(MainWindowController.class.getName()).log(Level.SEVERE,"Caught exception in ScheduledExecutorService.",e);
            }}, 0, 1000, TimeUnit.MILLISECONDS);

        lineChartWeight.getData().add(seriesWeight);
        lineChartRadius.getData().add(seriesRadius);
        lineChartM.getData().add(seriesM);
        lineChartdM.getData().add(seriesdM);
        lineChartddM.getData().add(seriesddM);

    }

    public void setSetPowerScene(Scene scene) {
        setPowerScene = scene;
    }

    public void setDataList(ObservableList<ModelProperty> sourceList, ObservableList<Double> realMassList, ObservableList<Double> generalList) {
        list = sourceList;
        this.realMassList = realMassList;
        this.modelRadiusList = generalList;
        addDataToChart();
        table.setItems(list);
    }

    public void startModelCalculations() {
        DataTransfer.IsModelCalculationsStarted.set(startModelCalculations.isSelected());
        if (!startModelCalculations.isSelected()){
            seriesM.getData().clear();
            seriesdM.getData().clear();
            seriesddM.getData().clear();

            lineChartM.getData().clear();
            lineChartdM.getData().clear();
            lineChartddM.getData().clear();

            lineChartM.getData().add(seriesM);
            lineChartdM.getData().add(seriesdM);
            lineChartddM.getData().add(seriesddM);
        }
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
        //TODO сделать отдельный метод инициализации для всех спиннеров,
        // поправить зраницы и убрать дефолтные значения,
        // ограничить скорость близкой но не равной нулю.

        initSpinnerDefaultValue();
        initSpinnerListener();
        initBindings();
        setTableCellValue();
    }
    private void initSpinnerDefaultValue(){
        pullingRateSpinner.getValueFactory().setValue(DataTransfer.dataParam.getV_upper());
        rSpinner.getValueFactory().setValue(DataTransfer.dataParam.getR());
        kpSpinner.getValueFactory().setValue(DataTransfer.dataParam.getKp());
        kiSpinner.getValueFactory().setValue(DataTransfer.dataParam.getKi());
        kdSpinner.getValueFactory().setValue(DataTransfer.dataParam.getKd());
        dNPmaxSpinner.getValueFactory().setValue(DataTransfer.dataParam.getdNPmax());
        kPdefpSpinner.getValueFactory().setValue(DataTransfer.dataParam.getkPdefp());
        kPdefmSpinner.getValueFactory().setValue(DataTransfer.dataParam.getkPdefm());
    }
    private void initSpinnerListener(){
        pullingRateSpinner.valueProperty().addListener((obs, oldValue, newValue) ->{
            System.out.println("Значение скорости изменилось с "+oldValue+" на "+newValue);
            DataTransfer.dataParam.setV_upper(newValue);
            //pullingRateLabel.setText(String.valueOf(DataTransfer.dataParam.getR()));
        });
        rSpinner.valueProperty().addListener((obs, oldValue, newValue) ->{
            System.out.println("Значение радиуса изменилось с "+oldValue+" на "+newValue);
            DataTransfer.dataParam.setR(newValue);
            //pullingRateLabel.setText(String.valueOf(DataTransfer.dataParam.getR()));
        });
        kpSpinner.valueProperty().addListener((obs, oldValue, newValue) ->{
            System.out.println("Значение пропорционального коэффициента изменилось с "+oldValue+" на "+newValue);
            DataTransfer.dataParam.setKp(newValue);
        });
        kiSpinner.valueProperty().addListener((obs, oldValue, newValue) ->{
            System.out.println("Значение интегрального коэффициента изменилось с "+oldValue+" на "+newValue);
            DataTransfer.dataParam.setKi(newValue);
        });
        kdSpinner.valueProperty().addListener((obs, oldValue, newValue) ->{
            System.out.println("Значение дифференциального коэффициента изменилось с "+oldValue+" на "+newValue);
            DataTransfer.dataParam.setKd(newValue);
        });
        dNPmaxSpinner.valueProperty().addListener((obs, oldValue, newValue) ->{
            System.out.println("Значение единицы регулирования изменилось с "+oldValue+" на "+newValue);
            DataTransfer.dataParam.setdNPmax(newValue);
        });
        kPdefpSpinner.valueProperty().addListener((obs, oldValue, newValue) ->{
            System.out.println("Значение положительного перекоса изменилось с "+oldValue+" на "+newValue);
            DataTransfer.dataParam.setkPdefp(newValue);
        });
        kPdefmSpinner.valueProperty().addListener((obs, oldValue, newValue) ->{
            System.out.println("Значение отрицательного перекоса изменилось с "+oldValue+" на "+newValue);
            DataTransfer.dataParam.setkPdefm(newValue);
        });
    }
    private void initBindings(){
        powerLabel.textProperty().bind(POWER_PROPERTY.asString());
        pullingRateLabel.textProperty().bind(pullingRateSpinner.valueProperty().asString());
    }
    private void setTableCellValue(){

        //time.setCellValueFactory(new PropertyValueFactory<>("time"));
        //time.setCellValueFactory(cellDataFeatures -> cellDataFeatures.getValue().timeProperty());
        time.setCellValueFactory(cellDataFeatures -> {
            String formattedTime = cellDataFeatures.getValue().getTimeProperty();
            formattedTime = formattedTime + "\n" +  POWER_PROPERTY;
            return new SimpleStringProperty(formattedTime);
        });
        realMass.setCellValueFactory(new PropertyValueFactory<>("realMassProperty"));
        modelMass.setCellValueFactory(new PropertyValueFactory<>("modelMassProperty"));
        modelMassDeviation.setCellValueFactory(new PropertyValueFactory<>("massDeviationProperty"));
/*        modelMassDeviation.setCellValueFactory(cellDataFeatures -> {
            String formattedModelMassDeviation = String.valueOf(cellDataFeatures.getValue().getMassDeviation());
            formattedModelMassDeviation = formattedModelMassDeviation +"\n";
        });*/
        modelFirstDerivativeDeviation.setCellValueFactory(new PropertyValueFactory<>("massFirstDerivativeDeviationProperty"));
        modelSecondDerivativeDeviation.setCellValueFactory(new PropertyValueFactory<>("massSecondDerivativeDeviationProperty"));

    }

    private static SimpleDateFormat createDateFormat() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        return dateFormat;
    }

    public void switchChart() {
        String targetChart = switchChartChoiceBox.getSelectionModel().getSelectedItem();
        System.out.println("Выбираем график"+ targetChart);
        switch (targetChart) {
            case ("r") -> {
                lineChartRadius.setVisible(true);
                lineChartM.setVisible(false);
                lineChartdM.setVisible(false);
                lineChartddM.setVisible(false);
            }
            case ("Model M") -> {
                lineChartRadius.setVisible(false);
                lineChartM.setVisible(true);
                lineChartdM.setVisible(false);
                lineChartddM.setVisible(false);
            }
            case ("Model dM") -> {
                lineChartRadius.setVisible(false);
                lineChartM.setVisible(false);
                lineChartdM.setVisible(true);
                lineChartddM.setVisible(false);
            }
            case ("Model ddM") -> {
                lineChartRadius.setVisible(false);
                lineChartM.setVisible(false);
                lineChartdM.setVisible(false);
                lineChartddM.setVisible(true);
            }
        }
    }
}

