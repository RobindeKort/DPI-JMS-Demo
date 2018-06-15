package main;

import jms.MessageProduct;
import com.google.gson.Gson;
import controller.TweakersController;
import java.net.URL;
import java.util.List;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javax.jms.JMSException;
import jms.Producer;
import model.*;

public class FXMLController implements Initializable {

    @FXML
    private Spinner<Integer> spinnerPrice;

    @FXML
    private Button buttonRequest;

    @FXML
    private void handleButtonRequest(ActionEvent event) {
        Product selectedProduct = listviewProducts.getSelectionModel().getSelectedItem();
        if (selectedProduct == null) {
            return;
        }
        int desiredPrice = spinnerPrice.getValue();
        try {
            tweakersController.sendMessage(desiredPrice, selectedProduct);
            labelResponse.setText("Awaiting reponse from server...");
            //buttonRequest.setDisable(true);
        } catch (JMSException ex) {
            Logger.getLogger(FXMLController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private ListView<Product> listviewProducts;
    
    @FXML
    private Label labelResponse;

    private final String activeMqIp = "127.0.0.1";
    
    private static TweakersController tweakersController;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            tweakersController = new TweakersController(activeMqIp);
        } catch (JMSException ex) {
            Logger.getLogger(FXMLController.class.getName()).log(Level.SEVERE, null, ex);
            Platform.exit();
        }
        listviewProducts.getItems().addAll(tweakersController.getProducts());
        spinnerPrice.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 10000, 0, 10));
    }
    
    public void stop() {
        try {
            tweakersController.stop();
        } catch (JMSException ex) {
            Logger.getLogger(FXMLController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
