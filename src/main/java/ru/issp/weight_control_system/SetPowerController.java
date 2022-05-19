package ru.issp.weight_control_system;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import jssc.SerialPortException;
import ru.issp.weight_control_system.utils.PowerSetter;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SetPowerController implements Initializable {
    public TextArea textArea;
    public TextField setPowerTextField;
    public Button toChart;
    public Button setPowerButton;
    public Button startCoolingButton;
    public Button stopCoolingButton;
    public TextField setCoolingTimeArea;
    public Label currentPowerLabel;
    public Spinner<Integer> manualRangingSpanSpinner;
    public Spinner<Integer> modelTactSpinner;
    public Button startHeatingButton;
    public Button stopHeatingButton;
    public TextField heatingTimeTextField;
    public ProgressBar heatingProgressBar;
    public TextField coolingTextField;
    public ProgressBar coolingProgressBar;
    public TextField heatingPowerTextField;


    private Scene getWeightScene;

    boolean cooling;
    boolean heating;
    int timePeriod = 3;

    private ScheduledExecutorService scheduledExecutorService;

    public void SetPowerButtonClicked(ActionEvent actionEvent) throws SerialPortException, InterruptedException {
        //TODO добавить стринг форматтер для того чтобы исключить ввод неверных данных
        // оставить проверку значения в классе PowerSetter
        try {

    int power = Integer.parseInt(setPowerTextField.getText());
     if ((power>=0)&&(power<=10000)){
         PowerSetter.setPower(power);
         textArea.appendText("Значение мощности изменилось на " + power +"\n");
         currentPowerLabel.setText(Integer.toString(PowerSetter.getPOWER()));
}else {
         PowerSetter.setPower(PowerSetter.getPOWER());
         textArea.appendText("ВВеденные значения находятся вне допустимых границ" +"\n");
         textArea.appendText("Значение мощности Осталось прежним" + power +"\n");
     }
currentPowerLabel.setText(Integer.toString(PowerSetter.getPOWER()));
}catch (NumberFormatException ex) {
            textArea.appendText("ВВедите цифровое значение." +"\n");
}
    }

    public void StartCoolingButtonClicked(ActionEvent actionEvent) {
        if (DataTransfer.IsPowerControlStarted.get()) DataTransfer.IsPowerControlStarted.set(false);
        cooling = true;
        int coolingTimeInMin = Integer.parseInt(setCoolingTimeArea.getText());
        int currentPower = PowerSetter.getPOWER();
        int cyclesDecrease = (coolingTimeInMin*60)/timePeriod;
        int powerDecrease = currentPower/cyclesDecrease;
        AtomicInteger cycleCount = new AtomicInteger();

        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        scheduledExecutorService.scheduleAtFixedRate(() -> {
            try {


    Platform.runLater(() -> {
        System.out.println("Должно срабатывать раз в 3 секунды");
        if (cycleCount.get() <cyclesDecrease) {

            try {
                PowerSetter.setPower(PowerSetter.getPOWER() - powerDecrease);
                coolingProgressBar.setProgress((double) cycleCount.get()/(double) cyclesDecrease);
            } catch (SerialPortException | InterruptedException e) {
                e.printStackTrace();
            }
        currentPowerLabel.setText(Integer.toString(PowerSetter.getPOWER()));
        cycleCount.getAndIncrement();}else{
            try {
                PowerSetter.setPower(0);
                coolingProgressBar.setProgress(1);
                currentPowerLabel.setText(Integer.toString(PowerSetter.getPOWER()));
            } catch (SerialPortException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    });

            } catch (Throwable e) {
                e.printStackTrace();
                Logger.getLogger(SetPowerController.class.getName()).log(Level.SEVERE,"Caught exception in ScheduledExecutorService.",e);
            }}, 0, timePeriod, TimeUnit.SECONDS);
    }
    public void StopCoolingButtonClicked(ActionEvent actionEvent) {
      cooling = false;
      scheduledExecutorService.shutdown();
    }
    public void StartHeatingButtonClicked(ActionEvent actionEvent) {
        if (DataTransfer.IsPowerControlStarted.get()) DataTransfer.IsPowerControlStarted.set(false);
        heating = true;
        int heatingTimeInMin = Integer.parseInt(heatingTimeTextField.getText());
        int currentPower = PowerSetter.getPOWER();
        int targetPower = Integer.parseInt(heatingPowerTextField.getText());
        int cyclesIncrease = (heatingTimeInMin*60)/timePeriod;
        int powerIncrease = (targetPower-currentPower)/cyclesIncrease;
        AtomicInteger cycleCount = new AtomicInteger();

        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        scheduledExecutorService.scheduleAtFixedRate(() -> {
            try {


                Platform.runLater(() -> {
                    System.out.println("Разогрев.Должно срабатывать раз в 3 секунды");
                    if (cycleCount.get() <cyclesIncrease) {

                        try {
                            PowerSetter.setPower(PowerSetter.getPOWER() + powerIncrease);
                            heatingProgressBar.setProgress((double)cyclesIncrease/(double)cycleCount.get());
                        } catch (SerialPortException | InterruptedException e) {
                            e.printStackTrace();
                        }
                        currentPowerLabel.setText(Integer.toString(PowerSetter.getPOWER()));
                        cycleCount.getAndIncrement();}else{
                        try {
                            PowerSetter.setPower(targetPower);
                            heatingProgressBar.setProgress(1);
                            currentPowerLabel.setText(Integer.toString(PowerSetter.getPOWER()));
                        } catch (SerialPortException | InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                });

            } catch (Throwable e) {
                e.printStackTrace();
                Logger.getLogger(SetPowerController.class.getName()).log(Level.SEVERE,"Caught exception in ScheduledExecutorService.",e);
            }}, 0, timePeriod, TimeUnit.SECONDS);
    }
    public void StopHeatingButtonClicked(ActionEvent actionEvent) {
        heating = false;
        scheduledExecutorService.shutdown();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        cooling = false;
        heating = false;
        currentPowerLabel.setText("0");
        initSpinnerDefaultValue();
        initSpinnerListener();
    }

    public void setGetWeightScene(Scene scene) {
        getWeightScene = scene;
    }
    public void switchSceneButtonClicked(ActionEvent actionEvent) {
        Stage primaryStage = (Stage)((Node)actionEvent.getSource()).getScene().getWindow();
        primaryStage.setScene(getWeightScene);

    }

    private void initSpinnerDefaultValue(){
manualRangingSpanSpinner.getValueFactory().setValue(DataTransfer.dataParam.getManualRangingSpan());
modelTactSpinner.getValueFactory().setValue(DataTransfer.dataParam.getModelTact());
    }
    private void initSpinnerListener() {
        manualRangingSpanSpinner.valueProperty().addListener((obs, oldValue, newValue) -> {
            System.out.println("Значение Manual Ranging изменилось с " + oldValue + " на " + newValue);
            DataTransfer.dataParam.setManualRangingSpan(newValue);
        });
        modelTactSpinner.valueProperty().addListener((obs, oldValue, newValue) -> {
            System.out.println("Значение modelTact изменилось с " + oldValue + " на " + newValue);
            DataTransfer.dataParam.setModelTact(newValue);
        });
    }
}
