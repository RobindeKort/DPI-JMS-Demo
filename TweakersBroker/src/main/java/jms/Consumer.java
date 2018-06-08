package jms;

import java.util.logging.Level;
import java.util.logging.Logger;
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

    private final String ACTION_ID_HEADER = "actionId";
    private final String ACTION_HEADER = "action";
    
    private Long id;

    private String activeMqBrokerUri;
    private String username;
    private String password;
    
    private Connection connection;
    private Session session;
    private MessageProducer producer;
    private MessageConsumer consumer;

    public Consumer(String activeMqBrokerUri, String username, String password) {
        super();
        this.id = 1L;
        this.activeMqBrokerUri = activeMqBrokerUri;
        this.username = username;
        this.password = password;
    }

    public void start(String originQueue, String destinationTopic) throws JMSException {
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
        producer = session.createProducer(session.createTopic(destinationTopic));
        consumer = session.createConsumer(session.createQueue(originQueue));
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

    @Override
    public void onMessage(Message message) {
        TextMessage textMessage = (TextMessage) message;
        try {
            System.out.println(textMessage.getJMSMessageID());
            System.out.println(textMessage.getText());
            // TODO robkor: Is this the correct usage of DeliveryMode, Priority and TimeToLive?
            this.producer.send(textMessage, DeliveryMode.NON_PERSISTENT, 1, 0);
        } catch (JMSException ex) {
            Logger.getLogger(Consumer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
