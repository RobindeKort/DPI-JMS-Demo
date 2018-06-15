package jms;

import com.google.gson.Gson;
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
public class ClientConsumer implements MessageListener {

    private final String ACTION_ID_HEADER = "actionId";
    private final String ACTION_HEADER = "action";

    private Long id;

    private String activeMqBrokerUri;
    private String username;
    private String password;
    private Gson gson;

    private Connection connection;
    private Session session;
    private MessageProducer storeProducer;
    private MessageProducer clientProducer;
    private MessageConsumer clientConsumer;

    public ClientConsumer(String activeMqBrokerUri, String username, String password) {
        super();
        this.id = 1L;
        this.activeMqBrokerUri = activeMqBrokerUri;
        this.username = username;
        this.password = password;
        this.gson = new Gson();
    }

    public void start(String clientOriginQueue, String clientDestinationQueue, String storeDestinationTopic) throws JMSException {
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
        storeProducer = session.createProducer(session.createTopic(storeDestinationTopic));
        clientProducer = session.createProducer(session.createQueue(clientDestinationQueue));
        clientConsumer = session.createConsumer(session.createQueue(clientOriginQueue));
        clientConsumer.setMessageListener(this);
    }

    public void stop() throws JMSException {
        if (clientConsumer != null) {
            clientConsumer.close();
            clientConsumer = null;
        }

        if (clientProducer != null) {
            clientProducer.close();
            clientProducer = null;
        }

        if (storeProducer != null) {
            storeProducer.close();
            storeProducer = null;
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

    private void sendMessageToClient(String id, String actionVal) throws JMSException {
        TextMessage textMessage = session.createTextMessage(actionVal);
        textMessage.setStringProperty(ACTION_HEADER, actionVal);
        textMessage.setStringProperty(ACTION_ID_HEADER, id);
        // TODO robkor: Is this the correct usage of DeliveryMode, Priority and TimeToLive?
        clientProducer.send(textMessage, DeliveryMode.NON_PERSISTENT, 1, 0);
    }

    @Override
    public void onMessage(Message message) {
        TextMessage textMessage = (TextMessage) message;
        try {
            final String reqId = textMessage.getStringProperty(ACTION_ID_HEADER);
            final String correlationId = String.valueOf(id);
            final Long requestedProductId = gson.fromJson(textMessage.getText(), MessageRequestProduct.class).getProductId();

            System.out.println(reqId);
            System.out.println(textMessage.getText());

            textMessage.setJMSCorrelationID(correlationId);
            // TODO robkor: Is this the correct usage of DeliveryMode, Priority and TimeToLive?
            storeProducer.send(textMessage, DeliveryMode.NON_PERSISTENT, 1, 0);
            id++;

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(ClientConsumer.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    
                    MessageResponseProduct retMsg = null;
                    try {
                        for (TextMessage t : AggregationMapper.getInstance().getAggregation(correlationId)) {
                            MessageResponseProduct msg = gson.fromJson(t.getText(), MessageResponseProduct.class);
                            if (retMsg == null) {
                                retMsg = msg;
                                continue;
                            }
                            System.out.println(retMsg.getProductId());
                            System.out.println(requestedProductId);
                            if (retMsg.getProductId().equals(requestedProductId)) {
                                if (msg.getProductId().equals(requestedProductId)
                                        && msg.getOfferedPrice() > retMsg.getOfferedPrice()) {
                                    continue;
                                }
                            } else {
                                if (!msg.getProductId().equals(requestedProductId)
                                        && msg.getOfferedPrice() > retMsg.getOfferedPrice()) {
                                    continue;
                                }
                            }
                            retMsg = msg;
                        }

                        sendMessageToClient(reqId, gson.toJson(retMsg));
                    } catch (JMSException ex) {
                        Logger.getLogger(ClientConsumer.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }).start();
        } catch (JMSException ex) {
            Logger.getLogger(ClientConsumer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
