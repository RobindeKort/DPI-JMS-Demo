package controller;

import com.google.gson.Gson;
import dal.IProductDao;
import dal.ProductDaoMem;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javafx.collections.ObservableList;
import javax.jms.JMSException;
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
public class TweakersController {

    private Consumer consumer;
    private IProductDao productDao;
    private Gson gson;
    
    public Product getProduct(Long id) {
        return productDao.getProduct(id);
    }

    public Collection<Product> getProducts() {
        return productDao.getProducts();
    }
    
    public ObservableList<MessageResponseProduct> getReceivedMessages() {
        return consumer.getReceivedMessages();
    }

    public TweakersController(String activeMqIp) throws JMSException {
        System.out.println("Connecting to ActiveMQ server. . .");
        consumer = new Consumer("tcp://" + activeMqIp + ":61616", "admin", "admin");
        consumer.start("ResponseClientQueue", "RequestBrokerQueue");
        System.out.println("Connected to ActiveMQ server.");

        gson = new Gson();
        productDao = new ProductDaoMem(initProducts());
    }

    public void stop() throws JMSException {
        consumer.stop();
    }

    public void sendMessage(int desiredPrice, Product selectedProduct) throws JMSException {
        MessageRequestProduct msg = new MessageRequestProduct(selectedProduct, desiredPrice);
        consumer.sendMessage(gson.toJson(msg));
    }

    private List<Product> initProducts() {
        List<Product> products = new ArrayList();
        products.add(new Monitor(new Long(products.size()), "Asus VG248QE Zwart", 24, 1920, 1080));
        products.add(new Monitor(new Long(products.size()), "Dell S2417DG Zwart", 24, 2560, 1440));
        products.add(new Monitor(new Long(products.size()), "Asus ROG Swift PG27UQ Koper, Titanium, Zwart", 27, 3840, 2160));
        products.add(new Smartphone(new Long(products.size()), "Samsung Galaxy S8 Zwart", "Android 8", 4, 64));
        products.add(new Smartphone(new Long(products.size()), "Apple iPhone 8 64GB Grijs", "iOS 11", 2, 64));
        products.add(new Smartphone(new Long(products.size()), "Google Pixel 2 XL 64GB Zwart", "Android 8", 4, 64));
        products.add(new Laptop(new Long(products.size()), "Apple MacBook Pro 2017 13,3\" 128GB ssd Spacegrijs (Qwerty)", "Apple", "Qwerty", false));
        products.add(new Laptop(new Long(products.size()), "HP Pavilion 15-ck093nd", "HP", "Qwerty", false));
        products.add(new Laptop(new Long(products.size()), "Asus VivoBook Pro 15 N580VD-FJ285T", "Asus", "Qwerty", true));
        return products;
    }
}
