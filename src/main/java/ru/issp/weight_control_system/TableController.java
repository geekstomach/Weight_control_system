package ru.issp.weight_control_system;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import ru.issp.weight_control_system.Model.Model;
import ru.issp.weight_control_system.Model.ModelProperty;
import ru.issp.weight_control_system.data.DataAll;
import ru.issp.weight_control_system.data.DataParam;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;


public class TableController implements Initializable {

    public TableView<ModelProperty> table;
    public TableColumn<ModelProperty, Double> realMass;
    public TableColumn<ModelProperty,Double> modelMass;
    public TableColumn<ModelProperty,Double> modelMassDeviation;
    public TableColumn<ModelProperty,Double> modelFirstDerivativeDeviation;
    public TableColumn<ModelProperty,Double> modelSecondDerivativeDeviation;

    public Button toChart;
    ScheduledExecutorService scheduledExecutorService;

    private ObservableList<ModelProperty> list = FXCollections.observableArrayList();

    private Scene getWeightScene;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setCellValue();



        list.addListener((ListChangeListener<ModelProperty>) change -> {
            System.out.println("**************");
            table.setItems(list);
        });
}



private void setCellValue(){
    realMass.setCellValueFactory(new PropertyValueFactory<ModelProperty,Double>("realMass"));
    modelMass.setCellValueFactory(new PropertyValueFactory<ModelProperty,Double>("modelMass"));
    modelMassDeviation.setCellValueFactory(new PropertyValueFactory<ModelProperty,Double>("modelMassDeviation"));
    modelFirstDerivativeDeviation.setCellValueFactory(new PropertyValueFactory<ModelProperty,Double>("modelFirstDerivativeDeviation"));
    modelSecondDerivativeDeviation.setCellValueFactory(new PropertyValueFactory<ModelProperty,Double>("modelSecondDerivativeDeviation"));

}
/*private void startExecutorService(){

    DataAll dataAll = new DataAll();
    DataParam dataParam = new DataParam();
    AtomicReference<Double> currentMass = new AtomicReference<>((double) 0);
    scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
    scheduledExecutorService.scheduleAtFixedRate(() -> {
        try {

            // Update the ObservableList
            Platform.runLater(() -> {
                //генерируем текущую массу (ту что придет к нам с COM порта)
                currentMass.set(currentMass.get() + 993d + Math.random());
                //здесь у нас создается два объекта model и ModelProperty.
                Model model = new Model(1,dataParam,dataAll, currentMass.get()-Math.random());
                list.add(new ModelProperty(
                                model.realMass,
                                model.modelMass,
                                model.modelMassDeviation,
                                model.modelFirstDerivativeDeviation,
                                model.modelSecondDerivativeDeviation)
                );
            });
        } catch (Throwable e) {
            e.printStackTrace();
        }}, 0, 1, TimeUnit.SECONDS);

}*/


    public void setGetWeightScene(Scene scene) {
        getWeightScene = scene;
    }
    public void switchSceneButtonClicked(ActionEvent actionEvent) throws IOException {
        Stage primaryStage = (Stage)((Node)actionEvent.getSource()).getScene().getWindow();
        primaryStage.setScene(getWeightScene);

    }


    public void setDataList(ObservableList<ModelProperty> list) {
        this.list = list;
    }
}