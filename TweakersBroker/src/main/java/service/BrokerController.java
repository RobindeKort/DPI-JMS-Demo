package service;

import jms.Producer;

/**
 *
 * @author Robin
 */
public class BrokerController {
    
    private final Producer producer;
    
    public BrokerController(Producer producer) {
        this.producer = producer;
    }
    
    public void stop() {
        
    }
}
