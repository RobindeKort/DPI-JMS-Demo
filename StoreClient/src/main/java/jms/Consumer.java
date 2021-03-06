package jms;

import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.RedeliveryPolicy;

/**
 *
 * @author Robin
 */
public class Consumer implements MessageListener {

    public static final String ACTION_ID_HEADER = "actionId";
    public static final String ACTION_HEADER = "action";

    private String activeMqBrokerUri;
    private String username;
    private String password;
    
    private Connection connection;
    private Session session;
    private MessageProducer producer;
    private MessageConsumer consumer;
    
    private ObservableList<TextMessage> receivedMessages;
    
    public ObservableList<TextMessage> getReceivedMessages() {
        return receivedMessages;
    }

    public Consumer(String activeMqBrokerUri, String username, String password) {
        super();
        this.activeMqBrokerUri = activeMqBrokerUri;
        this.username = username;
        this.password = password;
    }

    public void start(String originQueue, String destinationQueue) throws JMSException {
        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(username, password, activeMqBrokerUri);
        factory.setUseAsyncSend(true);
        RedeliveryPolicy policy = factory.getRedeliveryPolicy();
        policy.setInitialRedeliveryDelay(500);
        policy.setBackOffMultiplier(2);
        policy.setUseExponentialBackOff(true);
        policy.setMaximumRedeliveries(2);
        
        connection = factory.createConnection();
        connection.start();
        session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        producer = session.createProducer(session.createQueue(destinationQueue));
        consumer = session.createConsumer(session.createQueue(originQueue));
        receivedMessages = FXCollections.observableArrayList();
        consumer.setMessageListener(this);
    }

    public void stop() throws JMSException {
        if (consumer != null) {
            consumer.close();
            consumer = null;
        }
        
        if (producer != null) {
            producer.close();
            producer = null;
        }

        if (session != null) {
            session.close();
            session = null;
        }
        if (connection != null) {
            connection.close();
            connection = null;
        }
    }
    
    public void sendMessage(String correlationId, String id, final String actionVal) throws JMSException {
        TextMessage textMessage = session.createTextMessage(actionVal);
        textMessage.setJMSCorrelationID(correlationId);
        textMessage.setStringProperty(ACTION_HEADER, actionVal);
        textMessage.setStringProperty(ACTION_ID_HEADER, id);
        // TODO robkor: Is this the correct usage of DeliveryMode, Priority and TimeToLive?
        producer.send(textMessage, DeliveryMode.NON_PERSISTENT, 1, 0);
    }

    @Override
    public void onMessage(Message message) {
        final TextMessage textMessage = (TextMessage) message;
        try {
            // final String messageContent = textMessage.getText();
            System.out.println(textMessage.getJMSCorrelationID());
            System.out.println(textMessage.getText());
            
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    receivedMessages.add(textMessage);
                }
            });
            
            // TODO robkor: Is this the correct usage of DeliveryMode, Priority and TimeToLive?
            // producer.send(textMessage, DeliveryMode.NON_PERSISTENT, 1, 0);
        } catch (JMSException ex) {
            Logger.getLogger(Consumer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
