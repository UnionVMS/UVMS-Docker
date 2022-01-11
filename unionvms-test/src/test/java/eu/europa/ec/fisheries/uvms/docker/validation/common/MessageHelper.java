package eu.europa.ec.fisheries.uvms.docker.validation.common;

import org.apache.activemq.artemis.api.core.TransportConfiguration;
import org.apache.activemq.artemis.api.jms.ActiveMQJMSClient;
import org.apache.activemq.artemis.api.jms.JMSFactoryType;
import org.apache.activemq.artemis.core.remoting.impl.netty.NettyConnectorFactory;
import eu.europa.ec.fisheries.uvms.commons.message.api.MessageConstants;
import javax.jms.*;
import java.io.Closeable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MessageHelper implements Closeable {

    private final Connection connection;
    private final Session session;
    private Map<String, Queue> queueMap = new HashMap<>();

    private final static String RESPONSE_QUEUE_NAME = "IntegrationTestsResponseQueue";

    public MessageHelper() throws JMSException {
        Map<String, Object> params = new HashMap<>();
        params.put("host", "localhost");
        params.put("port", 5445);
        TransportConfiguration transportConfiguration = new TransportConfiguration(NettyConnectorFactory.class.getName(), params);
        ConnectionFactory connectionFactory = ActiveMQJMSClient.createConnectionFactoryWithoutHA(JMSFactoryType.CF, transportConfiguration);
        connection = connectionFactory.createConnection("test", "test");
        connection.setClientID(UUID.randomUUID().toString());
        connection.start();
        session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
    }

    private static final String TEST_RESPONSE_QUEUE = "IntegrationTestsResponseQueue";
    private static final String SERVICE_NAME = "ServiceName";
    private static final long TIMEOUT = 15 * 1000;

    private Queue createQueue(String queueName) throws JMSException {
        Queue queue = queueMap.get(queueName);
        if (queue == null) {
            queue = session.createQueue(queueName);
            queueMap.put(queueName, queue);
        }
        return queue;

    }

    public Message getMessageResponse(String queueName, final String msg) throws Exception {
        Queue queue = createQueue(queueName);
        try (MessageProducer messageProducer = session.createProducer(queue)) {
            TextMessage createTextMessage = session.createTextMessage(msg);
            Queue responseQueue = session.createQueue(TEST_RESPONSE_QUEUE);
            createTextMessage.setJMSReplyTo(responseQueue);
            messageProducer.send(createTextMessage);
            return listenOnTestResponseQueue(createTextMessage.getJMSMessageID(), TIMEOUT);
        }
    }

    public String sendMessageAndReturnMessageId(String queueName, final String msg, String asset, String function) throws Exception {
        try (Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE)) {
            Queue queue = session.createQueue(queueName);
            try (MessageProducer messageProducer = session.createProducer(queue)) {
                TextMessage createTextMessage = session.createTextMessage(msg);
                Queue responseQueue = session.createQueue(TEST_RESPONSE_QUEUE);
                createTextMessage.setJMSReplyTo(responseQueue);
                createTextMessage.setStringProperty("FUNCTION", function);
                createTextMessage.setStringProperty("JMSXGroupID", asset);
                messageProducer.send(createTextMessage);
                return createTextMessage.getJMSMessageID();
            }
        }
    }

    private Message listenOnTestResponseQueue(String correlationId, long timeoutInMillis) throws JMSException {
        Queue responseQueue = createQueue(TEST_RESPONSE_QUEUE + "?consumer.prefetchSize=5000");
        try (MessageConsumer messageConsumer = session.createConsumer(responseQueue, "JMSCorrelationID='" + correlationId + "'")) {
            return messageConsumer.receive(timeoutInMillis);
        }
    }

    public void sendMessage(String queueName, final String msg) throws Exception {
        final Queue queue = createQueue(queueName);
        try (MessageProducer messageProducer = session.createProducer(queue)) {
            messageProducer.setDeliveryMode(DeliveryMode.PERSISTENT);
            messageProducer.setTimeToLive(0);
            final Queue responseQueue = session.createQueue(TEST_RESPONSE_QUEUE);
            TextMessage createTextMessage = session.createTextMessage(msg);
            createTextMessage.setJMSReplyTo(responseQueue);
            messageProducer.send(createTextMessage);
        }
    }

    public void sendMessageWithProperty(String queueName, final String msg, String eventName) throws Exception {
        final Queue queue = createQueue(queueName);
        try (MessageProducer messageProducer = session.createProducer(queue)) {
            messageProducer.setDeliveryMode(DeliveryMode.PERSISTENT);
            TextMessage textMessage = session.createTextMessage(msg);
            textMessage.setStringProperty("eventName", eventName);
            messageProducer.send(textMessage);
        }
    }

    public boolean checkQueueHasElements(String queueName) throws Exception {
        Queue queue = createQueue(queueName);
        try (QueueBrowser browser = session.createBrowser(queue)) {
            return browser.getEnumeration().hasMoreElements();
        }
    }

    public void sendToEventBus(String text, String selector) throws Exception {
        sendToEventBus(text, selector, null);
    }

    public void sendToEventBus(String text, String selector, String function) throws Exception {
        Topic eventBus = session.createTopic("jms.topic.EventBus");
        TextMessage message = session.createTextMessage(text);
        message.setStringProperty(MessageConstants.JMS_FUNCTION_PROPERTY, function);
        message.setStringProperty(SERVICE_NAME, selector);
        try (MessageProducer producer = session.createProducer(eventBus)) {
            producer.send(message);
        }
    }

    public Message listenOnEventBus(String selector, Long timeoutInMillis) throws Exception {
        Topic eventBus = session.createTopic("jms.topic.EventBus");
        try (MessageConsumer messageConsumer = session.createConsumer(eventBus, selector)) {
            return messageConsumer.receive(timeoutInMillis);
        }
    }

    public Message listenForResponseOnQueue(String correlationId, String queue) throws Exception {
        Queue responseQueue = createQueue(queue);
        try (MessageConsumer messageConsumer = session.createConsumer(responseQueue, "JMSCorrelationID='" + correlationId + "'")) {
            return messageConsumer.receive(TIMEOUT);
        }
    }

    @Override
    public void close() {
        if (session != null) {
            try {
                session.close();
            } catch (JMSException e) {
                e.printStackTrace();
            }
        }
        if (connection != null) {
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

    public void sendMessageWithMethod(String queueName, final String msg, String function) throws Exception {
        sendMessageWithFunctionAndGroup(queueName, msg, "METHOD", function, null);
    }

    public void sendMessageWithFunctionAndGroup(String queueName, final String msg, String function, String group) throws Exception {
        sendMessageWithFunctionAndGroup(queueName, msg, "FUNCTION", function, group);
    }

    public void sendMessageWithFunctionAndGroup(String queueName, final String msg, String functionProperty, String function, String group) throws Exception {
        try (Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE)) {
            final Queue queue = session.createQueue(queueName);
            try (MessageProducer messageProducer = session.createProducer(queue)) {
                messageProducer.setDeliveryMode(DeliveryMode.PERSISTENT);
                messageProducer.setTimeToLive(1000000000);
                final Queue responseQueue = session.createQueue(RESPONSE_QUEUE_NAME);
                TextMessage createTextMessage = session.createTextMessage(msg);
                createTextMessage.setJMSReplyTo(responseQueue);
                createTextMessage.setStringProperty(functionProperty, function);
                createTextMessage.setStringProperty("JMSXGroupID", group);
                messageProducer.send(createTextMessage);
            }
        }
    }
}
