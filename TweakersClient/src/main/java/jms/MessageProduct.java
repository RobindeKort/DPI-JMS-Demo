package jms;

import java.io.Serializable;
import model.Product;

/**
 *
 * @author Robin
 */
public class MessageProduct implements Serializable {
    
    private int desiredPrice;
    private Long productId;
    
    public MessageProduct(int desiredPrice, Product product) {
        this.desiredPrice = desiredPrice;
        this.productId = product.getId();
    }
}
