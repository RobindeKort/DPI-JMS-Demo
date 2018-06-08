package jms;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.RedeliveryPolicy;

public class Producer {

    private static final String ACTION_ID_HEADER = "actionId";
    private static final String ACTION_HEADER = "action";

    protected long id;

    private String activeMqBrokerUri;
    private String username;
    private String password;

    private Connection connection;
    private Session session;
    private Destination destination;
    // https://docs.oracle.com/javaee/7/api/javax/jms/MessageProducer.html
    private MessageProducer producer;

    public Producer(final String activeMqBrokerUri, final String username, final String password) {
        super();        
        id = 1L;
        this.activeMqBrokerUri = activeMqBrokerUri;
        this.username = username;
        this.password = password;
    }

    public void setup(final String destinationQueue) throws JMSException {
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
        producer = session.createProducer(session.createTopic(destinationQueue));
    }

    public void close() throws JMSException {
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

    public void commit(final boolean transacted) throws JMSException {
        if (transacted) {
            session.commit();
        }
    }

    public void sendMessage(final String actionVal) throws JMSException {
        TextMessage textMessage = session.createTextMessage(actionVal);
        textMessage.setStringProperty(ACTION_HEADER, actionVal);
        textMessage.setStringProperty(ACTION_ID_HEADER, String.valueOf(id));
        producer.send(destination, textMessage);
        this.id++;
    }
}
