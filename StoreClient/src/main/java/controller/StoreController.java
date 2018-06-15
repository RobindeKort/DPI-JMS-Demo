package controller;

import javafx.collections.ObservableList;
import javax.jms.JMSException;
import javax.jms.TextMessage;
import jms.Consumer;

/**
 *
 * @author Robin
 */
public class StoreController {
    
    private final Consumer consumer;
    
    public ObservableList<TextMessage> getReceivedMessages() {
        return consumer.getReceivedMessages();
    }
    
    public StoreController(String activeMqIp, String storeName) throws JMSException {
        System.out.println("Connecting to ActiveMQ server. . .");
        this.consumer = new Consumer("tcp://" + activeMqIp + ":61616", "admin", "admin");
        /*MessageListener messageListener = new MessageListener() {
            @Override
            public void onMessage(Message message) {
            }
        };*/
        // The usage of a Virtual Topic requires a specific naming convention
        // https://tuhrig.de/virtual-topics-in-activemq/
        consumer.start("Consumer." + storeName + ".VirtualTopic.RequestStoreTopic",
                "ResponseBrokerQueue");
        System.out.println("Connected to ActiveMQ server.");
    }
    
    public void stop() throws JMSException {
        consumer.stop();
    }
}
