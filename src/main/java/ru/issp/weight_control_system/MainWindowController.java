package ru.issp.weight_control_system;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleDoubleProperty;
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
import javafx.util.StringConverter;
import ru.issp.weight_control_system.Model.ModelProperty;
import ru.issp.weight_control_system.utils.Maths;
import ru.issp.weight_control_system.utils.PowerSetter;

import java.net.URL;
import java.text.DecimalFormat;
import java.text.ParseException;
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



    public Spinner<Double> pullingRateSpinner;
    public Spinner<Double> rSpinner;
    public Spinner<Double> kpSpinner;
    public Spinner<Double> kiSpinner;
    public Spinner<Double> kdSpinner;
    public Spinner<Double> dNPmaxSpinner;
    public Spinner<Double> kPdefpSpinner;
    public Spinner<Double> kPdefmSpinner;

    //public Label pullingRateLabel;
    public Label powerLabel;
    public Label smaRadiusLabel;

    public static SimpleIntegerProperty POWER_PROPERTY = new SimpleIntegerProperty(PowerSetter.getPOWER());


    public ChoiceBox<String> switchChartChoiceBox;
    public LineChart<String,Number> lineChartRadius;
        public CategoryAxis xAxisLineChartRadius;
        public NumberAxis yAxisLineChartRadius;
    public LineChart<String,Number> lineChartWeight;
        public CategoryAxis xAxisLineChartWeight;
        public NumberAxis yAxisLineChartWeight;
    public LineChart<String,Number> lineChartMassDeviation;
        public CategoryAxis xAxisLineChartMassDeviation;
        public NumberAxis yAxisLineChartMassDeviation;
    public LineChart<String,Number> lineChartMassFirstDerivativeDeviation;
        public CategoryAxis xAxisLineChartMassFirstDerivativeDeviation;
        public NumberAxis yAxisLineChartMassFirstDerivativeDeviation;
    public LineChart<String,Number> lineChartCurrentPower;
        public CategoryAxis xAxisLineChartCurrentPower;
        public NumberAxis yAxisLineChartCurrentPower;
    public LineChart<String,Number> lineChartSMAWeight;
        public CategoryAxis xAxisLineChartSMAWeight;
        public NumberAxis yAxisLineChartSMAWeight;

    public TableView<ModelProperty> table;
    public TableColumn<ModelProperty,String> timeColumn;
    public TableColumn<ModelProperty, String> pullingRateColumn;
    public TableColumn<ModelProperty, String> radiusColumn;
    public TableColumn<ModelProperty, String> currentPowerColumn;
    public TableColumn<ModelProperty,String> modelMassColumn;
    public TableColumn<ModelProperty,String> massDeviationColumn;
    public TableColumn<ModelProperty,String> massFirstDerivativeDeviationColumn;
    public TableColumn<ModelProperty,String> massSecondDerivativeDeviationColumn;



    private Scene setPowerScene;

    final int WINDOW_SIZE = 20;
    private ObservableList<ModelProperty> list = FXCollections.observableArrayList();
    private ObservableList<Double> realMassList = FXCollections.observableArrayList();
    private ObservableList<Double> modelRadiusList = FXCollections.observableArrayList();

    XYChart.Series<String,Number> seriesMassDeviation = new XYChart.Series<>();
    XYChart.Series<String,Number> seriesMassFirstDerivativeDeviation = new XYChart.Series<>();


    public void addDataToChart() {
        //defining a series to display data
        XYChart.Series<String,Number> seriesWeight = new XYChart.Series<>();
        XYChart.Series<String,Number> seriesSMAWeight = new XYChart.Series<>();

        XYChart.Series<String,Number> seriesRadius = new XYChart.Series<>();
        XYChart.Series<String,Number> seriesCurrentPower = new XYChart.Series<>();

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
                            new XYChart.Data<>(createDateFormatForCarts().format(now), finalWeight));
                    //График веса SMA
                    seriesSMAWeight.getData().add(
                            new XYChart.Data<>(createDateFormatForCarts().format(now), Maths.SMA(realMassList,2))
                    );
                    //График расчетного радиуса
                    if (modelRadiusList.size()!=0) {
                    seriesRadius.getData().add(
                            new XYChart.Data<>(createDateFormatForCarts().format(now), modelRadiusList.get(modelRadiusList.size() - 1)));
                    }

                    //График текущей мощности
                    seriesCurrentPower.getData().add(new XYChart.Data<>(createDateFormatForCarts().format(now),POWER_PROPERTY.getValue()));

                    //TODO Возможно стоит добавить в иф что эти расчеты раз в 8 тактов как в TransferData
                    if (startModelCalculations.isSelected()&&list.size()!=0) {
                        seriesMassDeviation.getData().add(new XYChart.Data<>(createDateFormatForCarts().format(now), list.get(0).getMassDeviationProperty()));
                        seriesMassFirstDerivativeDeviation.getData().add(new XYChart.Data<>(createDateFormatForCarts().format(now), list.get(0).getMassFirstDerivativeDeviationProperty()));
                    }
                    //ограничение отображаемых данных
                    if (seriesWeight.getData().size() > WINDOW_SIZE)
                        seriesWeight.getData().remove(0);
                    if (seriesRadius.getData().size() > WINDOW_SIZE)
                        seriesRadius.getData().remove(0);
                    if (seriesMassDeviation.getData().size() > WINDOW_SIZE)
                        seriesMassDeviation.getData().remove(0);
                    if (seriesMassFirstDerivativeDeviation.getData().size() > WINDOW_SIZE)
                        seriesMassFirstDerivativeDeviation.getData().remove(0);

                    smaRadiusLabel.setText(String.valueOf(modelRadiusSMA()));
                });
            } catch (Throwable e) {
                e.printStackTrace();
                Logger.getLogger(MainWindowController.class.getName()).log(Level.SEVERE,"Caught exception in ScheduledExecutorService.",e);
            }}, 0, DataTransfer.readTact, TimeUnit.SECONDS);

        lineChartWeight.getData().add(seriesWeight);
        lineChartSMAWeight.getData().add(seriesSMAWeight);
        lineChartRadius.getData().add(seriesRadius);
        lineChartMassDeviation.getData().add(seriesMassDeviation);
        lineChartMassFirstDerivativeDeviation.getData().add(seriesMassFirstDerivativeDeviation);
        lineChartCurrentPower.getData().add(seriesCurrentPower);

    }

    public void setSetPowerScene(Scene scene) {
        setPowerScene = scene;
    }

    public void setDataList(ObservableList<ModelProperty> sourceList, ObservableList<Double> sourceRealMassList, ObservableList<Double> sourceModelRadiusList) {
        list = sourceList;
        realMassList = sourceRealMassList;
        modelRadiusList = sourceModelRadiusList;
        addDataToChart();
        table.setItems(list);
    }

    public void startModelCalculations() {
        DataTransfer.IsModelCalculationsStarted.set(startModelCalculations.isSelected());
        if (!startModelCalculations.isSelected()){
            seriesMassDeviation.getData().clear();
            seriesMassFirstDerivativeDeviation.getData().clear();

            lineChartMassDeviation.getData().clear();
            lineChartMassFirstDerivativeDeviation.getData().clear();

            lineChartMassDeviation.getData().add(seriesMassDeviation);
            lineChartMassFirstDerivativeDeviation.getData().add(seriesMassFirstDerivativeDeviation);
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



        kpSpinner.getValueFactory().setConverter(stringConverterToThreeDecimalPlaces());
        kpSpinner.getValueFactory().setValue(DataTransfer.dataParam.getKp());

        kiSpinner.getValueFactory().setConverter(stringConverterToThreeDecimalPlaces());
        kiSpinner.getValueFactory().setValue(DataTransfer.dataParam.getKi());

        kdSpinner.getValueFactory().setConverter(stringConverterToThreeDecimalPlaces());
        kdSpinner.getValueFactory().setValue(DataTransfer.dataParam.getKd());

        pullingRateSpinner.getValueFactory().setValue(DataTransfer.dataParam.getV_upper());
        rSpinner.getValueFactory().setValue(DataTransfer.dataParam.getR());



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
            System.out.println("Значение пропорционального коэффициента изменилось с "+oldValue+" на "+newValue+" obs.getValue()"+obs.getValue());
            DataTransfer.dataParam.setKp(obs.getValue());
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
       // pullingRateLabel.textProperty().bind(pullingRateSpinner.valueProperty().asString());
    }

    private void setTableCellValue(){
//TODO отображать время в "HH:mm:ss"
        timeColumn.setCellValueFactory(new PropertyValueFactory<>("timeProperty"));

        pullingRateColumn.setCellValueFactory(cellDataFeatures -> {
            String formattedPullingRate = Double.toString(cellDataFeatures.getValue().getPullingRateProperty());
            formattedPullingRate = formattedPullingRate + "\n" +  cellDataFeatures.getValue().getLengthProperty();
            return new SimpleStringProperty(formattedPullingRate);
        });

        radiusColumn.setCellValueFactory(cellDataFeatures -> {
            String formattedRadius = Double.toString(cellDataFeatures.getValue().getRadiusProperty());
            formattedRadius = formattedRadius + "\n" +  cellDataFeatures.getValue().getMeltLevelHeightProperty();
            return new SimpleStringProperty(formattedRadius);
        });

        currentPowerColumn.setCellValueFactory(cellDataFeatures -> {
            String formattedTime = Integer.toString(cellDataFeatures.getValue().getCurrentPowerProperty());
            formattedTime = formattedTime + "\n" +  cellDataFeatures.getValue().getPowerDeviationProperty();
            return new SimpleStringProperty(formattedTime);
        });

        modelMassColumn.setCellValueFactory(cellDataFeatures -> {
            String formattedRadius = Double.toString(cellDataFeatures.getValue().getModelMassProperty());
            formattedRadius = formattedRadius + "\n" +  cellDataFeatures.getValue().getModelMassFirstDerivativeProperty();
            return new SimpleStringProperty(formattedRadius);
        });

        massDeviationColumn.setCellValueFactory(cellDataFeatures -> {
            String formattedRadius = Double.toString(cellDataFeatures.getValue().getMassDeviationProperty());
            formattedRadius = formattedRadius + "\n" +  cellDataFeatures.getValue().getIntegralPartOfThePowerProperty();
            return new SimpleStringProperty(formattedRadius);
        });

        massFirstDerivativeDeviationColumn.setCellValueFactory(cellDataFeatures -> {
            String formattedRadius = Double.toString(cellDataFeatures.getValue().getMassFirstDerivativeDeviationProperty());
            formattedRadius = formattedRadius + "\n" +  cellDataFeatures.getValue().getProportionalPartOfThePowerProperty();
            return new SimpleStringProperty(formattedRadius);
        });

        massSecondDerivativeDeviationColumn.setCellValueFactory(cellDataFeatures -> {
            String formattedRadius = Double.toString(cellDataFeatures.getValue().getMassSecondDerivativeDeviationProperty());
            formattedRadius = formattedRadius + "\n" +  cellDataFeatures.getValue().getDifferentialPartOfThePowerProperty();
            return new SimpleStringProperty(formattedRadius);
        });

    }


    private StringConverter<Double> stringConverterToThreeDecimalPlaces(){
        return new StringConverter<>() {
            private final DecimalFormat df = new DecimalFormat("#.###");

            @Override
            public String toString(Double value) {
                // If the specified value is null, return a zero-length String
                if (value == null) {
                    return "";
                }

                return df.format(value);
            }

            @Override
            public Double fromString(String value) {
                try {
                    // If the specified value is null or zero-length, return null
                    if (value == null) {
                        return null;
                    }

                    value = value.trim();

                    if (value.length() < 1) {
                        return null;
                    }

                    // Perform the requested parsing
                    return df.parse(value).doubleValue();
                } catch (ParseException ex) {
                    throw new RuntimeException(ex);
                }
            }
        };
    }
    private static SimpleDateFormat createDateFormatForCarts() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        return dateFormat;
    }
    private Double modelRadiusSMA(){
        double smaModelRadius = 0d;
        int count = 0;
        if (modelRadiusList.size()>WINDOW_SIZE){
            for (int i =modelRadiusList.size()-WINDOW_SIZE; i < modelRadiusList.size(); i++) {
                smaModelRadius = smaModelRadius + modelRadiusList.get(i);

            }
        }
        return smaModelRadius/WINDOW_SIZE;
}
    public void switchChart() {
        String targetChart = switchChartChoiceBox.getSelectionModel().getSelectedItem();
        System.out.println("Выбираем график"+ targetChart);
        switch (targetChart) {
            case ("r/Weight") -> {
                lineChartRadius.setVisible(true);
                lineChartWeight.setVisible(true);
                lineChartSMAWeight.setVisible(false);
                lineChartMassDeviation.setVisible(false);
                lineChartMassFirstDerivativeDeviation.setVisible(false);
                lineChartCurrentPower.setVisible(false);
            }
            case ("Power/SMAWeight") -> {
                lineChartRadius.setVisible(false);
                lineChartWeight.setVisible(false);
                lineChartSMAWeight.setVisible(true);
                lineChartMassDeviation.setVisible(false);
                lineChartMassFirstDerivativeDeviation.setVisible(false);
                lineChartCurrentPower.setVisible(true);
            }
            case ("massDeviation/massFirstDerivativeDeviation") -> {
                lineChartRadius.setVisible(false);
                lineChartWeight.setVisible(false);
                lineChartSMAWeight.setVisible(false);
                lineChartMassDeviation.setVisible(true);
                lineChartMassFirstDerivativeDeviation.setVisible(true);
                lineChartCurrentPower.setVisible(false);
            }
        }
    }

}

