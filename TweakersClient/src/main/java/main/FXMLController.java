package main;

import controller.TweakersController;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javax.jms.JMSException;
import jms.MessageResponseProduct;
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
        tweakersController.getReceivedMessages().addListener(new ListChangeListener<MessageResponseProduct>() {
            @Override
            public void onChanged(ListChangeListener.Change<? extends MessageResponseProduct> c) {
                c.next();
                if (c.wasAdded()) {
                    final MessageResponseProduct msg = c.getAddedSubList().get(0);
                    if (msg == null) {
                        System.out.println("test");
                        return;
                    }
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            labelResponse.setText(String.format("%1$s is offering you a %2$s for your desired price of %3$s.",
                                    msg.getStoreName(), tweakersController.getProduct(msg.getProductId()), msg.getOfferedPrice()));
                        }
                    });
                }
            }
        });
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
