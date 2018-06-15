package controller;

import com.google.gson.Gson;
import dal.IProductDao;
import dal.ProductDaoMem;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javax.jms.JMSException;
import javax.jms.TextMessage;
import jms.Consumer;
import jms.MessageRequestProduct;
import jms.MessageResponseProduct;
import model.Laptop;
import model.Monitor;
import model.Product;
import model.Smartphone;

/**
 *
 * @author Robin
 */
public class StoreController {

    private final Consumer consumer;
    private IProductDao productDao;
    private Gson gson;

    private final String storeName;

    public ObservableList<TextMessage> getReceivedMessages() {
        return consumer.getReceivedMessages();
    }

    public StoreController(String activeMqIp, String storeName) throws JMSException {
        this.storeName = storeName;

        System.out.println("Connecting to ActiveMQ server. . .");
        this.consumer = new Consumer("tcp://" + activeMqIp + ":61616", "admin", "admin");
        // The usage of a Virtual Topic requires a specific naming convention
        // https://tuhrig.de/virtual-topics-in-activemq/
        consumer.start("Consumer." + storeName + ".VirtualTopic.RequestStoreTopic",
                "ResponseBrokerQueue");
        System.out.println("Connected to ActiveMQ server.");

        this.productDao = new ProductDaoMem(initProducts());
        this.gson = new Gson();
        initListener();
    }

    public void stop() throws JMSException {
        consumer.stop();
    }

    private List<Product> initProducts() {
        List<Product> products = new ArrayList();
        products.add(new Monitor(new Long(products.size()), "Asus VG248QE Zwart", 270, 24, 1920, 1080));
        products.add(new Monitor(new Long(products.size()), "Dell S2417DG Zwart", 410, 24, 2560, 1440));
        products.add(new Monitor(new Long(products.size()), "Asus ROG Swift PG27UQ Koper, Titanium, Zwart", 2000, 27, 3840, 2160));
        products.add(new Smartphone(new Long(products.size()), "Samsung Galaxy S8 Zwart", 560, "Android 8", 4, 64));
        products.add(new Smartphone(new Long(products.size()), "Apple iPhone 8 64GB Grijs", 800, "iOS 11", 2, 64));
        products.add(new Smartphone(new Long(products.size()), "Google Pixel 2 XL 64GB Zwart", 1000, "Android 8", 4, 64));
        products.add(new Laptop(new Long(products.size()), "Apple MacBook Pro 2017 13,3\" 128GB ssd Spacegrijs (Qwerty)", 1900, "Apple", "Qwerty", false));
        products.add(new Laptop(new Long(products.size()), "HP Pavilion 15-ck093nd", 1200, "HP", "Qwerty", false));
        products.add(new Laptop(new Long(products.size()), "Asus VivoBook Pro 15 N580VD-FJ285T", 800, "Asus", "Qwerty", true));
        return products;
    }

    private void initListener() {
        consumer.getReceivedMessages().addListener(new ListChangeListener<TextMessage>() {
            @Override
            public void onChanged(ListChangeListener.Change<? extends TextMessage> c) {
                c.next();
                if (c.wasAdded()) {
                    try {
                        final TextMessage msg = c.getAddedSubList().get(0);
                        if (msg == null) {
                            return;
                        }
                        MessageRequestProduct req = gson.fromJson(msg.getText(), MessageRequestProduct.class);
                        Product reqProd = productDao.getProduct(req.getProductId());
                        if (reqProd == null) {
                            return;
                        }
                        Product retProd = reqProd;
                        // If the product is too expensive
                        if (reqProd.getPrice() > req.getDesiredPrice()) {
                            for (Product p : productDao.getProducts()) {
                                // Find a similar but cheaper product
                                if (p.getType().equals(reqProd.getType())) {
                                    if (p.getPrice() < retProd.getPrice()) {
                                        retProd = p;
                                    }
                                }
                            }
                        }
                        MessageResponseProduct retMsg = new MessageResponseProduct(storeName, retProd.getId(), retProd.getPrice());
                        consumer.sendMessage(msg.getJMSCorrelationID(), msg.getStringProperty(Consumer.ACTION_ID_HEADER), gson.toJson(retMsg));
                    } catch (JMSException ex) {
                        Logger.getLogger(StoreController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        });
    }
}
