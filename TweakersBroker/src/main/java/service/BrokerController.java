package service;

import javax.jms.JMSException;
import jms.Consumer;

/**
 *
 * @author Robin
 */
public class BrokerController {
    
    private final Consumer consumer;
    
    public BrokerController(String activeMqIp) throws JMSException {
        System.out.println("Connecting to ActiveMQ server. . .");
        this.consumer = new Consumer("tcp://" + activeMqIp + ":61616", "admin", "admin");
        consumer.start("RequestQueue", "RequestTopic");
    }
    
    public void stop() throws JMSException {
        consumer.stop();
    }
}
