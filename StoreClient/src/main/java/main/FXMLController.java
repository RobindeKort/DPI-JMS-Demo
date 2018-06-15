package main;

import controller.StoreController;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javax.jms.JMSException;

public class FXMLController implements Initializable {

    @FXML
    private TextField textfieldName;

    @FXML
    private void handleTextfieldName(ActionEvent event) {
        initStore(textfieldName.getText());
    }

    @FXML
    private Button buttonSubmit;

    @FXML
    private void handleButtonSubmit(ActionEvent event) {
        initStore(textfieldName.getText());
    }

    @FXML
    private ListView listviewRequests;

    private ListProperty listProperty = new SimpleListProperty();

    private final String activeMqIp = "127.0.0.1";

    private static StoreController storeController;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

    }

    public void stop() {
        if (storeController == null) {
            return;
        }
        try {
            storeController.stop();
        } catch (JMSException ex) {
            Logger.getLogger(FXMLController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void initStore(String storeName) {
        if (storeName == null || storeName == "") {
            return;
        }
        try {
            storeController = new StoreController(activeMqIp, storeName);
            textfieldName.setDisable(true);
            buttonSubmit.setDisable(true);
            listviewRequests.itemsProperty().bind(listProperty);
            listProperty.set(storeController.getReceivedMessages());
        } catch (JMSException ex) {
            Logger.getLogger(FXMLController.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("Error occurred while connecting to ActiveMQ server.");
            // Platform.exit();
        }
    }
}
