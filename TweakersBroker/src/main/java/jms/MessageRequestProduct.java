package jms;

import java.io.Serializable;

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
    
    public MessageRequestProduct(Long productId, int desiredPrice) {
        this.productId = productId;
        this.desiredPrice = desiredPrice;
    }
}
