package jms;

import java.util.Enumeration;
import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;
import org.apache.activemq.ActiveMQConnectionFactory;

/**
 *
 * @author Robin
 */
public class Consumer implements MessageListener {

    private String activeMqBrokerUri;
    private String username;
    private String password;

    private boolean isDestinationTopic;
    private String destinationName;
    private String selector;
    private String clientId;

    public Consumer(String activeMqBrokerUri, String username, String password) {
        super();
        this.activeMqBrokerUri = activeMqBrokerUri;
        this.username = username;
        this.password = password;
    }

    public void run() throws JMSException {
        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(username, password, activeMqBrokerUri);
        if (clientId != null) {
            factory.setClientID(clientId);
        }
        Connection connection = factory.createConnection();
        if (clientId != null) {
            connection.setClientID(clientId);
        }
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

        setComsumer(session);

        connection.start();
        System.out.println(Thread.currentThread().getName() + ": ActiveMQMessageConsumer Waiting for messages at "
                + destinationName);
    }

    private void setComsumer(Session session) throws JMSException {
        MessageConsumer consumer = null;
        if (isDestinationTopic) {
            Topic topic = session.createTopic(destinationName);

            if (selector == null) {
                consumer = session.createConsumer(topic);
            } else {
                consumer = session.createConsumer(topic, selector);
            }
        } else {
            Destination destination = session.createQueue(destinationName);

            if (selector == null) {
                consumer = session.createConsumer(destination);
            } else {
                consumer = session.createConsumer(destination, selector);
            }
        }
        consumer.setMessageListener(this);
    }

    @Override
    public void onMessage(Message message) {
        String msg;
        try {
            msg = String.format(
                    "[%s]: ActiveMQMessageConsumer Received message from [ %s] - Headers: [ %s] Message: [ %s ]",
                    Thread.currentThread().getName(), destinationName, getPropertyNames(message),
                    ((TextMessage) message).getText());
            System.out.println(msg);
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    private String getPropertyNames(Message message) throws JMSException {
        String props = "";
        @SuppressWarnings("unchecked")
        Enumeration properties = message.getPropertyNames();
        while (properties.hasMoreElements()) {
            String propKey = properties.nextElement().toString();
            props += propKey + "=" + message.getStringProperty(propKey) + " ";
        }
        return props;
    }

    public void setSelector(String selector) {
        this.selector = selector;
    }

    public boolean isDestinationTopic() {
        return isDestinationTopic;
    }

    public String getDestinationName() {
        return destinationName;
    }

    public String getSelector() {
        return selector;
    }

    public String getClientId() {
        return clientId;
    }

    public void setDestinationTopic(boolean isDestinationTopic) {
        this.isDestinationTopic = isDestinationTopic;
    }

    public void setDestinationName(String destinationName) {
        this.destinationName = destinationName;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }
}
