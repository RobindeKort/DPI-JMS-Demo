package main;

import jms.MessageProduct;
import com.google.gson.Gson;
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
        MessageProduct msg = new MessageProduct(desiredPrice, selectedProduct);
        try {
            msgQueueSender.sendMessage(gson.toJson(msg));
            labelResponse.setText("Awaiting reponse from server...");
            buttonRequest.setDisable(true);
        } catch (JMSException ex) {
            Logger.getLogger(FXMLController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private ListView<Product> listviewProducts;
    
    @FXML
    private Label labelResponse;

    private final Gson gson = new Gson();
    private final String activeMqIp = "127.0.0.1";
    private Producer msgQueueSender;
    private List<Product> products;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        System.out.println("Connecting to ActiveMQ server. . .");
        try {
            msgQueueSender = new Producer("tcp://" + activeMqIp + ":61616", "admin", "secret");
            msgQueueSender.setup("RequestQueue");
        } catch (JMSException ex) {
            Logger.getLogger(FXMLController.class.getName()).log(Level.SEVERE, null, ex);
            Platform.exit();
        }
        initProducts();
        listviewProducts.getItems().addAll(products);
        spinnerPrice.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 10000, 0, 10));
    }
    
    public void stop() {
        try {
            msgQueueSender.close();
        } catch (JMSException ex) {
            Logger.getLogger(FXMLController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void initProducts() {
        products = new ArrayList();
        products.add(new Monitor(new Long(products.size()), "Asus VG248QE Zwart", 24, 1920, 1080));
        products.add(new Monitor(new Long(products.size()), "Dell S2417DG Zwart", 24, 2560, 1440));
        products.add(new Monitor(new Long(products.size()), "Asus ROG Swift PG27UQ Koper, Titanium, Zwart", 27, 3840, 2160));
        products.add(new Smartphone(new Long(products.size()), "Samsung Galaxy S8 Zwart", "Android 8", 4, 64));
        products.add(new Smartphone(new Long(products.size()), "Apple iPhone 8 64GB Grijs", "iOS 11", 2, 64));
        products.add(new Smartphone(new Long(products.size()), "Google Pixel 2 XL 64GB Zwart", "Android 8", 4, 64));
        products.add(new Laptop(new Long(products.size()), "Apple MacBook Pro 2017 13,3\" 128GB ssd Spacegrijs (Qwerty)", "Apple", "Qwerty", false));
        products.add(new Laptop(new Long(products.size()), "HP Pavilion 15-ck093nd", "HP", "Qwerty", false));
        products.add(new Laptop(new Long(products.size()), "Asus VivoBook Pro 15 N580VD-FJ285T", "Asus", "Qwerty", true));
    }
}
