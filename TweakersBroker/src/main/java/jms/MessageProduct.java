package jms;

import java.io.Serializable;

/**
 *
 * @author Robin
 */
public class MessageProduct implements Serializable {
    
    private int desiredPrice;
    private Long productId;
    
    public MessageProduct(int desiredPrice, Long productId) {
        this.desiredPrice = desiredPrice;
        this.productId = productId;
    }
}
