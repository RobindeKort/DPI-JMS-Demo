package controller;

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
        // The usage of a Virtual Topic requires a specific naming convention
        // https://tuhrig.de/virtual-topics-in-activemq/
        consumer.start("RequestBrokerQueue", "VirtualTopic.RequestStoreTopic");
        System.out.println("Connected to ActiveMQ server.");
    }
    
    public void stop() throws JMSException {
        consumer.stop();
    }
}
