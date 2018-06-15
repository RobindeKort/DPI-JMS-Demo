package jms;

import java.io.Serializable;

/**
 *
 * @author Robin
 */
public class MessageResponseProduct implements Serializable {
    
    private String storeName;
    private Long productId;
    private int offeredPrice;

    public String getStoreName() {
        return storeName;
    }

    public Long getProductId() {
        return productId;
    }

    public int getOfferedPrice() {
        return offeredPrice;
    }
    
    public MessageResponseProduct() {
        
    }
    
    public MessageResponseProduct(String storeName, Long productId, int offeredPrice) {
        this.storeName = storeName;
        this.productId = productId;
        this.offeredPrice = offeredPrice;
    }
}
