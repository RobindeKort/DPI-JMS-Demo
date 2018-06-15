package jms;

import java.io.Serializable;
import model.Product;

/**
 *
 * @author Robin
 */
public class MessageRequestProduct implements Serializable {
    
    private Long productId;
    private int desiredPrice;

    public Long getProductId() {
        return productId;
    }

    public int getDesiredPrice() {
        return desiredPrice;
    }
    
    public MessageRequestProduct(Product product, int desiredPrice) {
        this.productId = product.getId();
        this.desiredPrice = desiredPrice;
    }
}
