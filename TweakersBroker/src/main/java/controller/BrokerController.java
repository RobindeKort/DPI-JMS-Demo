package controller;

import javax.jms.JMSException;
import jms.ClientConsumer;
import jms.StoreConsumer;

/**
 *
 * @author Robin
 */
public class BrokerController {
    
    private final ClientConsumer clientConsumer;
    private final StoreConsumer storeConsumer;
    
    public BrokerController(String activeMqIp) throws JMSException {
        System.out.println("Connecting to ActiveMQ server. . .");
        this.clientConsumer = new ClientConsumer("tcp://" + activeMqIp + ":61616", "admin", "admin");
        // The usage of a Virtual Topic requires a specific naming convention
        // https://tuhrig.de/virtual-topics-in-activemq/
        clientConsumer.start("RequestBrokerQueue", "ResponseClientQueue", "VirtualTopic.RequestStoreTopic");
        this.storeConsumer = new StoreConsumer("tcp://" + activeMqIp + ":61616", "admin", "admin");
        storeConsumer.start("ResponseBrokerQueue");
        System.out.println("Connected to ActiveMQ server.");
    }
    
    public void stop() throws JMSException {
        clientConsumer.stop();
        storeConsumer.stop();
    }
}
