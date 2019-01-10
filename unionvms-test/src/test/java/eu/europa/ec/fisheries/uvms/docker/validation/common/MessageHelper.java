package eu.europa.ec.fisheries.uvms.docker.validation.common;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import javax.jms.*;

import org.apache.activemq.artemis.api.core.TransportConfiguration;
import org.apache.activemq.artemis.api.jms.ActiveMQJMSClient;
import org.apache.activemq.artemis.api.jms.JMSFactoryType;
import org.apache.activemq.artemis.core.remoting.impl.netty.NettyConnectorFactory;

public class MessageHelper {

    private final Connection connection;
    private final Session session;
    private Map<String, Queue> queueMap = new HashMap<>();

    private final static String RESPONSE_QUEUE_NAME = "IntegrationTestsResponseQueue";

    public MessageHelper() throws JMSException {
        Map<String, Object> params = new HashMap<>();
        params.put("host", "localhost");
        params.put("port", 5445);
        TransportConfiguration transportConfiguration = new TransportConfiguration(NettyConnectorFactory.class.getName(), params);
        ConnectionFactory connectionFactory = ActiveMQJMSClient.createConnectionFactoryWithoutHA(JMSFactoryType.CF,transportConfiguration);
        connection = connectionFactory.createConnection("test", "test");
        connection.setClientID(UUID.randomUUID().toString());
        connection.start();
        session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
    }

    private static final String TEST_RESPONSE_QUEUE = "IntegrationTestsResponseQueue";
    private static final String SERVICE_NAME = "ServiceName";
    private static final long TIMEOUT = 30000;

    private Queue createQueue(String queueName) throws JMSException {
        Queue queue = queueMap.get(queueName);
        if(queue == null) {
            queue = session.createQueue(queueName);
            queueMap.put(queueName, queue);
        }
        return queue;

    }

    public Message getMessageResponse(String queueName, final String msg) throws Exception {
        Queue queue = createQueue(queueName);
        MessageProducer messageProducer = session.createProducer(queue);
        try {
            TextMessage createTextMessage = session.createTextMessage(msg);
            Queue responseQueue = session.createQueue(TEST_RESPONSE_QUEUE);
            createTextMessage.setJMSReplyTo(responseQueue);
            messageProducer.send(createTextMessage);
            return listenOnTestResponseQueue(createTextMessage.getJMSMessageID(), TIMEOUT);
        } finally {
            messageProducer.close();
        }
    }

    public String sendMessageAndReturnMessageId(String queueName, final String msg, String asset, String function) throws Exception {
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        try {
            Queue queue = session.createQueue(queueName);
            MessageProducer messageProducer = session.createProducer(queue);
            try {
                TextMessage createTextMessage = session.createTextMessage(msg);
                Queue responseQueue = session.createQueue(TEST_RESPONSE_QUEUE);
                createTextMessage.setJMSReplyTo(responseQueue);
                createTextMessage.setStringProperty("FUNCTION", function);
                createTextMessage.setStringProperty("JMSXGroupID", asset);
                messageProducer.send(createTextMessage);
                return createTextMessage.getJMSMessageID();
            } finally {
                messageProducer.close();
            }
        } finally {
            session.close();
        }
    }

    public Message listenOnTestResponseQueue(String correlationId, long timeoutInMillis) throws JMSException {
        Queue responseQueue = createQueue(TEST_RESPONSE_QUEUE + "?consumer.prefetchSize=5000");
        MessageConsumer messageConsumer = session.createConsumer(responseQueue, "JMSCorrelationID='" + correlationId + "'");
        try {
            return messageConsumer.receive(timeoutInMillis);
        } finally {
            messageConsumer.close();
        }
    }

    public void sendMessage(String queueName, final String msg) throws Exception {
        final Queue queue = createQueue(queueName);

        final MessageProducer messageProducer = session.createProducer(queue);
        try {
            messageProducer.setDeliveryMode(DeliveryMode.PERSISTENT);
            messageProducer.setTimeToLive(0);
            final Queue responseQueue = session
                    .createQueue(TEST_RESPONSE_QUEUE);
            TextMessage createTextMessage = session.createTextMessage(msg);
            createTextMessage.setJMSReplyTo(responseQueue);
            messageProducer.send(createTextMessage);
        } finally {
            messageProducer.close();
        }
    }


    /**
     * Check queue has elements.
     *
     * @param connection
     * @param queueName
     * @return
     * @throws Exception
     */

    public boolean checkQueueHasElements(String queueName) throws Exception {
        Queue queue = createQueue(queueName);
        QueueBrowser browser = session.createBrowser(queue);
        try {
            if (browser.getEnumeration().hasMoreElements()) {
                return true;
            }
            return false;
        } finally {
            browser.close();
        }
    }

    public void sendToEventBus(String text, String selector) throws Exception {
        Topic eventBus = session.createTopic("jms.topic.EventBus");
        TextMessage message = session.createTextMessage(text);
        message.setStringProperty(SERVICE_NAME, selector);
        MessageProducer producer = session.createProducer(eventBus);
        try {
            producer.send(message);
        } finally {
            producer.close();
        }

    }

    public Message listenOnEventBus(String selector, Long timeoutInMillis) throws Exception {
        Topic eventBus = session.createTopic("jms.topic.EventBus");
        MessageConsumer messageConsumer = session.createConsumer(eventBus, selector);
        try {
            return messageConsumer.receive(timeoutInMillis);
        } finally {
            messageConsumer.close();
        }
    }

    public Message listenForResponseOnQueue(String correlationId, String queue) throws Exception {
        Queue responseQueue = createQueue(queue);
        MessageConsumer messageConsumer = session.createConsumer(responseQueue, "JMSCorrelationID='" + correlationId + "'");
        try {
            return messageConsumer.receive(TIMEOUT);
        } finally {
            messageConsumer.close();
        }
    }

    public void close() {

        if(session != null) {
            try {
                session.close();
            } catch (JMSException e) {
                e.printStackTrace();
            }
        }

        if(connection != null) {
            try {
                connection.close();
            } catch (JMSException e) {
                e.printStackTrace();
            }
        }
    }

    public void sendMessageWithFunction(String queueName, final String msg, String function) throws Exception {
        sendMessageWithFunctionAndGroup(queueName, msg, function, null);
    }

    public void sendMessageWithFunctionAndGroup(String queueName, final String msg, String function, String group) throws Exception {
        final Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        try {
            final Queue queue = session.createQueue(queueName);

            final MessageProducer messageProducer = session.createProducer(queue);
            try {
                messageProducer.setDeliveryMode(DeliveryMode.PERSISTENT);
                messageProducer.setTimeToLive(1000000000);
                final Queue responseQueue = session
                        .createQueue(RESPONSE_QUEUE_NAME);
                TextMessage createTextMessage = session.createTextMessage(msg);
                createTextMessage.setJMSReplyTo(responseQueue);
                createTextMessage.setStringProperty("FUNCTION", function);
                createTextMessage.setStringProperty("JMSXGroupID", group);
                messageProducer.send(createTextMessage);
            } finally {
                messageProducer.close();
            }
        } finally {
            session.close();
        }
    }

}
