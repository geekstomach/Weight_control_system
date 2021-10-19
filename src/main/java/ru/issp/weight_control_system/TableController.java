package ru.issp.weight_control_system;


import javafx.collections.FXCollections;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import ru.issp.weight_control_system.Model.ModelProperty;

import java.io.FileNotFoundException;
import java.net.URL;
import java.util.ResourceBundle;




public class TableController implements Initializable {

    public TableView<ModelProperty> table;
    public TableColumn<ModelProperty, Double> realMass;
    public TableColumn<ModelProperty,Double> modelMass;
    public TableColumn<ModelProperty,Double> modelMassDeviation;
    public TableColumn<ModelProperty,Double> modelFirstDerivativeDeviation;
    public TableColumn<ModelProperty,Double> modelSecondDerivativeDeviation;

    public Button toChart;
    public AnchorPane pane;



    private ObservableList<ModelProperty> list = FXCollections.observableArrayList();

    private Scene getWeightScene;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setCellValue();
        //https://docs.oracle.com/javase/8/javafx/api/javafx/collections/ListChangeListener.Change.html

   }



private void setCellValue(){
    realMass.setCellValueFactory(new PropertyValueFactory<ModelProperty,Double>("realMass"));
    modelMass.setCellValueFactory(new PropertyValueFactory<ModelProperty,Double>("modelMass"));
    modelMassDeviation.setCellValueFactory(new PropertyValueFactory<ModelProperty,Double>("modelMassDeviation"));
    modelFirstDerivativeDeviation.setCellValueFactory(new PropertyValueFactory<ModelProperty,Double>("modelFirstDerivativeDeviation"));
    modelSecondDerivativeDeviation.setCellValueFactory(new PropertyValueFactory<ModelProperty,Double>("modelSecondDerivativeDeviation"));

}

        public void setGetWeightScene(Scene scene) {
        getWeightScene = scene;
    }

    public void switchSceneButtonClicked(ActionEvent actionEvent) {
        Stage primaryStage = (Stage)((Node)actionEvent.getSource()).getScene().getWindow();
        primaryStage.setScene(getWeightScene);

    }


    public void setDataList(ObservableList<ModelProperty> generalList) {
        list = generalList;
        table.setItems(list);
    }


}