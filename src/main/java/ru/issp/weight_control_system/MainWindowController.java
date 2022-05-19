package ru.issp.weight_control_system;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.robot.Robot;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.util.StringConverter;
import jssc.SerialPortException;
import ru.issp.weight_control_system.Model.ModelProperty;
import ru.issp.weight_control_system.utils.PowerSetter;

import java.net.URL;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.ResourceBundle;
import java.util.TimeZone;
import java.util.concurrent.atomic.AtomicInteger;

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
    public Spinner<Double> lowPassFilterPeriodSpinner;

    //public Label pullingRateLabel;
    public Label powerLabel;
    public Label smaRadiusLabel;

    public static SimpleIntegerProperty POWER_PROPERTY = new SimpleIntegerProperty(PowerSetter.getPOWER());


    public ChoiceBox<String> switchChartChoiceBox;
    public LineChart<String,Number> lineChartRadius;
        public CategoryAxis xAxisLineChartRadius;
        public NumberAxis yAxisLineChartRadius;
    public LineChart<Number,Number> lineChartWeight;
        public NumberAxis xAxisLineChartWeight;
        public NumberAxis yAxisLineChartWeight;
    public LineChart<String,Number> lineChartMassDeviation;
        public CategoryAxis xAxisLineChartMassDeviation;
        public NumberAxis yAxisLineChartMassDeviation;
    public LineChart<String,Number> lineChartMassFirstDerivativeDeviation;
        public CategoryAxis xAxisLineChartMassFirstDerivativeDeviation;
        public NumberAxis yAxisLineChartMassFirstDerivativeDeviation;
    public LineChart<String,Number> lineChartPowerDeviation;
        public CategoryAxis xAxisLineChartPowerDeviation;
        public NumberAxis yAxisLineChartPowerDeviation;
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

    final int WINDOW_SIZE = 240;

    private ObservableList<ModelProperty> list = FXCollections.observableArrayList();


    private ObservableList<Double> realMassList = FXCollections.observableArrayList();
    private ObservableList<Double> modelRadiusList = FXCollections.observableArrayList();

    //defining a series to display for model calc time
    XYChart.Series<String,Number> seriesMassDeviation = new XYChart.Series<>();
    XYChart.Series<String,Number> seriesMassFirstDerivativeDeviation = new XYChart.Series<>();
    XYChart.Series<String,Number> seriesPowerDeviation = new XYChart.Series<>();


//TODO реализовать графики на Timeline
    public void addDataToChartWithTimeline(){
        lineChartWeight.setCreateSymbols(false);
        xAxisLineChartWeight.setTickLabelFormatter(stringConverterLong());
        xAxisLineChartWeight.setForceZeroInRange(false);
        AtomicInteger count = new AtomicInteger(0);
        long start = System.currentTimeMillis();
        //defining a series to display for all time
        XYChart.Series<Number,Number> seriesWeight = new XYChart.Series<>();
        XYChart.Series<String,Number> seriesRadius = new XYChart.Series<>();

        EventHandler<ActionEvent> chartUpdaterWeight = actionEvent -> {

            long now =System.currentTimeMillis() - start;
            double weight = 0;
            if (realMassList.size()!=0)weight = realMassList.get(realMassList.size()-1);
            double finalWeight = weight;

            //График текущего веса
            seriesWeight.getData().add(
                    new XYChart.Data<>(now, finalWeight));
            //График MassDeviation
            if ((count.getAndIncrement()%DataTransfer.dataParam.getModelTact()==0)&&startModelCalculations.isSelected()&&list.size()!=0) {
System.out.println("Сработал такт модели");
seriesMassDeviation.getData().add(new XYChart.Data<>(createDateFormatForCarts().format(now), list.get(0).getMassDeviationProperty()));
if (seriesMassDeviation.getData().size() > WINDOW_SIZE)
    seriesMassDeviation.getData().remove(0);
}
            if (seriesWeight.getData().size() > WINDOW_SIZE)
                seriesWeight.getData().remove(0);
        };
        EventHandler<ActionEvent> chartUpdaterOthers = actionEvent -> {
            long now =System.currentTimeMillis() - start;
            //График расчетного радиуса
            if (modelRadiusList.size()!=0) {
                seriesRadius.getData().add(
                        new XYChart.Data<>(createDateFormatForCarts().format(now), modelRadiusList.get(modelRadiusList.size() - 1)));
            }
            //График MassFirstDerivativeDeviation
            if (startModelCalculations.isSelected()&&list.size()!=0) {
                System.out.println("Сработал такт модели");
                seriesMassFirstDerivativeDeviation.getData().add(
                        new XYChart.Data<>(createDateFormatForCarts().format(now), list.get(0).getMassFirstDerivativeDeviationProperty()));
                seriesPowerDeviation.getData().add(
                        new XYChart.Data<>(createDateFormatForCarts().format(now), list.get(0).getPowerDeviationProperty()));
            }
            if (seriesRadius.getData().size() > WINDOW_SIZE)
                seriesRadius.getData().remove(0);

            if (seriesMassFirstDerivativeDeviation.getData().size() > WINDOW_SIZE)
                seriesMassFirstDerivativeDeviation.getData().remove(0);
            if (seriesPowerDeviation.getData().size() > WINDOW_SIZE)
                seriesPowerDeviation.getData().remove(0);

            smaRadiusLabel.setText(stringFormatToFourDecimalPlaces(modelRadiusSMA()));
        };

        lineChartWeight.getData().add(seriesWeight);
        lineChartRadius.getData().add(seriesRadius);
        lineChartMassDeviation.getData().add(seriesMassDeviation);
        lineChartMassFirstDerivativeDeviation.getData().add(seriesMassFirstDerivativeDeviation);
        lineChartPowerDeviation.getData().add(seriesPowerDeviation);


        Timeline updateChartWeight = new Timeline(new KeyFrame(Duration.seconds(DataTransfer.dataParam.getReadTact()), chartUpdaterWeight));
        Timeline updateChartOthers = new Timeline(new KeyFrame(Duration.seconds(DataTransfer.dataParam.getReadTact()*DataTransfer.dataParam.getModelTact()), chartUpdaterOthers));

        updateChartWeight.setCycleCount(Timeline.INDEFINITE);
        updateChartOthers.setCycleCount(Timeline.INDEFINITE);
        updateChartWeight.play();
        updateChartOthers.play();
    }
    public void setSetPowerScene(Scene scene) {
        setPowerScene = scene;
    }

    public void setDataList(ObservableList<ModelProperty> sourceList, ObservableList<Double> sourceRealMassList, ObservableList<Double> sourceModelRadiusList) {
        list = sourceList;
        realMassList = sourceRealMassList;
        modelRadiusList = sourceModelRadiusList;
        //addDataToChart();
        addDataToChartWithTimeline();
        table.setItems(list);

        //Todo разобраться в какой момент добавлять обработчиков нажатий клавиш , т.к в init крашит программу
        setOnKeyPressedListener();
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
        System.out.println("Нажата кнопка "+DataTransfer.IsPowerControlStarted.get());
    }

    public void switchToSetPowerSceneButtonClicked(ActionEvent actionEvent) {
        Stage primaryStage = (Stage)((Node)actionEvent.getSource()).getScene().getWindow();
        primaryStage.setScene(setPowerScene);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //TODO сделать отдельный метод инициализации для всех Spinner,
        // поправить границы и убрать дефолтные значения,
        // ограничить скорость близкой но не равной нулю.

        initSpinnerDefaultValue();
        initSpinnerListener();
        initBindings();
        setTableCellValue();
        keepComputerAwake();
        //Одни из вариантов изменения цвета графика
        //lineChartRadius.getStylesheets().add(Objects.requireNonNull(this.getClass().getResource("/css/lineChart.css")).toExternalForm());
        //lineChartRadius.lookup(".chart-plot-background").setStyle("-fx-background-color: transparent;");
    }




    private void initSpinnerDefaultValue(){

        kpSpinner.getValueFactory().setConverter(stringConverterToThreeDecimalPlaces());
        kpSpinner.getValueFactory().setValue(DataTransfer.dataParam.getKp());

        kiSpinner.getValueFactory().setConverter(stringConverterToThreeDecimalPlaces());
        kiSpinner.getValueFactory().setValue(DataTransfer.dataParam.getKi());

        kdSpinner.getValueFactory().setConverter(stringConverterToThreeDecimalPlaces());
        kdSpinner.getValueFactory().setValue(DataTransfer.dataParam.getKd());

        pullingRateSpinner.getValueFactory().setValue(DataTransfer.dataParam.getV_upper()*3600.0d);
        rSpinner.getValueFactory().setValue(DataTransfer.dataParam.getR());

        dNPmaxSpinner.getValueFactory().setValue(DataTransfer.dataParam.getdNPmax());
        kPdefpSpinner.getValueFactory().setValue(DataTransfer.dataParam.getkPdefp());
        kPdefmSpinner.getValueFactory().setValue(DataTransfer.dataParam.getkPdefm());
        lowPassFilterPeriodSpinner.getValueFactory().setValue(DataTransfer.dataParam.getLowPassFilterPeriod());
    }
    private void initSpinnerListener(){
        pullingRateSpinner.valueProperty().addListener((obs, oldValue, newValue) ->{
            System.out.println("Значение скорости изменилось с "+oldValue+" на "+newValue);
            System.out.println("Значение скорости изменилось с "+oldValue*0.000278d+" на "+newValue*0.000278d);
            DataTransfer.dataParam.setV_upper(newValue*0.000278d);
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
        lowPassFilterPeriodSpinner.valueProperty().addListener((obs, oldValue, newValue) ->{
            System.out.println("Значение периода фильтрации  радиуса  изменилось с "+oldValue+" на "+newValue);
            DataTransfer.dataParam.setLowPassFilterPeriod(newValue);
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
            String formattedRadius = stringFormatToFourDecimalPlaces(cellDataFeatures.getValue().getRadiusProperty());
            formattedRadius = formattedRadius + "\n" +  cellDataFeatures.getValue().getMeltLevelHeightProperty();
            return new SimpleStringProperty(formattedRadius);
        });

        currentPowerColumn.setCellValueFactory(cellDataFeatures -> {
            String formattedTime = Integer.toString(cellDataFeatures.getValue().getCurrentPowerProperty());
            formattedTime = formattedTime + "\n" +  stringFormatToFourDecimalPlaces(cellDataFeatures.getValue().getPowerDeviationProperty());
            return new SimpleStringProperty(formattedTime);
        });

        modelMassColumn.setCellValueFactory(cellDataFeatures -> {
            String formattedRadius = stringFormatToFourDecimalPlaces(cellDataFeatures.getValue().getModelMassProperty());
            formattedRadius = formattedRadius + "\n" +  stringFormatToFourDecimalPlaces(cellDataFeatures.getValue().getModelMassFirstDerivativeProperty());
            return new SimpleStringProperty(formattedRadius);
        });

        massDeviationColumn.setCellValueFactory(cellDataFeatures -> {
            String formattedRadius = stringFormatToFourDecimalPlaces(cellDataFeatures.getValue().getMassDeviationProperty());
            formattedRadius = formattedRadius + "\n" +  stringFormatToFourDecimalPlaces(cellDataFeatures.getValue().getIntegralPartOfThePowerProperty());
            return new SimpleStringProperty(formattedRadius);
        });

        massFirstDerivativeDeviationColumn.setCellValueFactory(cellDataFeatures -> {
            String formattedRadius = stringFormatToFourDecimalPlaces(cellDataFeatures.getValue().getMassFirstDerivativeDeviationProperty());
            formattedRadius = formattedRadius + "\n" +  stringFormatToFourDecimalPlaces(cellDataFeatures.getValue().getProportionalPartOfThePowerProperty());
            return new SimpleStringProperty(formattedRadius);
        });

        massSecondDerivativeDeviationColumn.setCellValueFactory(cellDataFeatures -> {
            //String formattedRadius = Double.toString(cellDataFeatures.getValue().getMassSecondDerivativeDeviationProperty());
            String formattedRadius = stringFormatToFourDecimalPlaces(cellDataFeatures.getValue().getMassSecondDerivativeDeviationProperty());
            formattedRadius = formattedRadius + "\n" +  stringFormatToFourDecimalPlaces(cellDataFeatures.getValue().getDifferentialPartOfThePowerProperty());
            return new SimpleStringProperty(formattedRadius);
        });

    }
    private String stringFormatToFourDecimalPlaces(Double d){
        DecimalFormat format = new DecimalFormat("0.0000");
        return format.format(d);
    }
    private StringConverter<Number> stringConverterLong(){
        return new StringConverter<>() {
            @Override
            public String toString(Number value) {
                // If the specified value is null, return a zero-length String
                if (value == null) {
                    return "";
                }

                return createDateFormatForCarts().format(value);
            }

            @Override
            public Long fromString(String value) {
                // If the specified value is null or zero-length, return null
                if (value == null) {
                    return null;
                }

                value = value.trim();

                if (value.length() < 1) {
                    return null;
                }

                // Perform the requested parsing
                return Long.valueOf(value);
            }
        };
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
        int period = 10;
          if (modelRadiusList.size()>period){
            for (int i =modelRadiusList.size()-period; i < modelRadiusList.size(); i++) {
                smaModelRadius = smaModelRadius + modelRadiusList.get(i);

            }
        }
        return smaModelRadius/period;
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
                lineChartPowerDeviation.setVisible(false);
            }
            case ("PowerDeviation/SMAWeight") -> {
                lineChartRadius.setVisible(false);
                lineChartWeight.setVisible(false);
                lineChartSMAWeight.setVisible(true);
                lineChartMassDeviation.setVisible(false);
                lineChartMassFirstDerivativeDeviation.setVisible(false);
                lineChartPowerDeviation.setVisible(true);
            }
            case ("massDeviation/massFirstDerivativeDeviation") -> {
                lineChartRadius.setVisible(false);
                lineChartWeight.setVisible(false);
                lineChartSMAWeight.setVisible(false);
                lineChartMassDeviation.setVisible(true);
                lineChartMassFirstDerivativeDeviation.setVisible(true);
                lineChartPowerDeviation.setVisible(false);
            }
        }
    }

    public void setOnKeyPressedListener(){
        //TODO разобраться почему и как правильно
        // Set - это контракт говорит что он задаёт новое значение. Используйте addEvent
        lineChartWeight.getScene().setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case W -> {
                    System.out.println("Кнопка W нажата, увеличиваем мощность на 10 единиц");
                    try {
                        PowerSetter.setPower(PowerSetter.getPOWER() + 10);
                    } catch (SerialPortException | InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                case Q -> {
                    System.out.println("Кнопка Q нажата, уменьшаем мощность на 10 единиц");
                    try {
                        PowerSetter.setPower(PowerSetter.getPOWER() - 10);
                    } catch (SerialPortException | InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                case A-> {
                    System.out.println("Кнопка A нажата,включаем Auto Ranging для M_fast ");
                    yAxisLineChartWeight.setAutoRanging(true);                }
                case S -> {
                    System.out.println("Кнопка S нажата,включаем Manual Ranging для M_fast ");
                    yAxisLineChartWeight.setAutoRanging(false);
                    int LowerBound = (int) yAxisLineChartWeight.getLowerBound();
                    yAxisLineChartWeight.setLowerBound(LowerBound);
                    yAxisLineChartWeight.setUpperBound(LowerBound+DataTransfer.dataParam.getManualRangingSpan()*2);
                }
                case Z -> {
                    if (!yAxisLineChartWeight.isAutoRanging()){
                        System.out.println("Кнопка Z нажата,Manual Ranging включен, снижаем диапазон на 500 ");
                        int LowerBound = (int) yAxisLineChartWeight.getLowerBound();
                        yAxisLineChartWeight.setLowerBound(LowerBound-DataTransfer.dataParam.getManualRangingSpan());
                        yAxisLineChartWeight.setUpperBound(LowerBound+DataTransfer.dataParam.getManualRangingSpan());
                    }

                }
                case X -> {
                    if (!yAxisLineChartWeight.isAutoRanging()){
                        System.out.println("Кнопка X нажата,Manual Ranging включен, повышаем диапазон на 500 ");
                        int LowerBound = (int) yAxisLineChartWeight.getLowerBound();
                        yAxisLineChartWeight.setLowerBound(LowerBound+DataTransfer.dataParam.getManualRangingSpan());
                        yAxisLineChartWeight.setUpperBound(LowerBound+DataTransfer.dataParam.getManualRangingSpan()*3);
                    }
                }
            }
        });
    }
    public void keepComputerAwake(){

        Robot computerAwake = new Robot();

        EventHandler<ActionEvent> mouseMoveHandler = actionEvent -> {
            //TODO проверить работоспособность
            // возможно стоит попробовать JNA для более элегантного решения
            computerAwake.mouseMove(computerAwake.getMouseX(),computerAwake.getMouseY());
        };
        Timeline mouseMoveTimeline = new Timeline(new KeyFrame(Duration.seconds(60), mouseMoveHandler));
        mouseMoveTimeline.setCycleCount(Timeline.INDEFINITE);
        mouseMoveTimeline.play();
    }
}

