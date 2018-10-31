package eu.europa.ec.fisheries.uvms.docker.validation.common;

import java.util.UUID;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.DeliveryMode;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.QueueBrowser;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;
import org.apache.activemq.ActiveMQConnectionFactory;

public final class MessageHelper {

    private static final ConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://localhost:61616");
    
    private static final String TEST_RESPONSE_QUEUE = "IntegrationTestsResponseQueue";
    private static final String SERVICE_NAME = "ServiceName";
    private static final long TIMEOUT = 30000;

    public static Message getMessageResponse(String queueName, final String msg) throws Exception {
        Connection connection = connectionFactory.createConnection();
        connection.setClientID(UUID.randomUUID().toString());

        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        Queue queue = session.createQueue(queueName);

        MessageProducer messageProducer = session.createProducer(queue);
        TextMessage createTextMessage = session.createTextMessage(msg);
        Queue responseQueue = session.createQueue(TEST_RESPONSE_QUEUE);
        createTextMessage.setJMSReplyTo(responseQueue);
        messageProducer.send(createTextMessage);

        connection.close();

        return listenOnTestResponseQueue(createTextMessage.getJMSMessageID(), TIMEOUT);
    }

    public static String sendMessageAndReturnMessageId(String queueName, final String msg, String asset, int order) throws Exception {
        Connection connection = connectionFactory.createConnection();
        connection.setClientID(UUID.randomUUID().toString());

        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        Queue queue = session.createQueue(queueName);

        MessageProducer messageProducer = session.createProducer(queue);
        TextMessage createTextMessage = session.createTextMessage(msg);
        Queue responseQueue = session.createQueue(TEST_RESPONSE_QUEUE);
        createTextMessage.setJMSReplyTo(responseQueue);
        createTextMessage.setStringProperty("JMSXGroupID", asset);
        //createTextMessage.setIntProperty("JMSXGroupSeq", order);
        messageProducer.send(createTextMessage);

        connection.close();

        return createTextMessage.getJMSMessageID();
    }
    
    public static Message listenOnTestResponseQueue(String correlationId, long timeoutInMillis) throws JMSException {
        Connection connection = connectionFactory.createConnection();
        try {
            connection.start();
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Queue responseQueue = session.createQueue(TEST_RESPONSE_QUEUE + "?consumer.prefetchSize=5000");
            return session.createConsumer(responseQueue, "JMSCorrelationID='" + correlationId + "'").receive(timeoutInMillis);
        } finally {
            connection.close();
        }
    }

    public static void sendMessage(String queueName, final String msg) throws Exception {
        String responseQueueName = queueName + "Response" + UUID.randomUUID().toString().replaceAll("-", "");

        Connection connection = connectionFactory.createConnection();
        connection.setClientID(UUID.randomUUID().toString());

        final Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        final Queue queue = session.createQueue(queueName);

        final MessageProducer messageProducer = session.createProducer(queue);
        messageProducer.setDeliveryMode(DeliveryMode.PERSISTENT);
        messageProducer.setTimeToLive(1000000000);
        final Queue responseQueue = session
                .createQueue(responseQueueName);
        TextMessage createTextMessage = session.createTextMessage(msg);
        createTextMessage.setJMSReplyTo(responseQueue);
        messageProducer.send(createTextMessage);
        session.close();
        connection.close();
    }


    /**
     * Check queue has elements.
     *
     * @param connection
     * @param queueName
     * @return
     * @throws Exception
     */

    public static boolean checkQueueHasElements(String queueName) throws Exception {
        Connection connection = null;
        try {
            connection = connectionFactory.createConnection();
            connection.start();
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Queue queue = session.createQueue(queueName);
            QueueBrowser browser = session.createBrowser(queue);
            if (browser.getEnumeration().hasMoreElements()) {
                return true;
            }
            return false;
        } finally {
            connection.close();
        }
    }

    public static void sendToEventBus(String text, String selector) throws Exception {
        Connection connection = null;
        try {
            connection = connectionFactory.createConnection();
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Topic eventBus = session.createTopic("EventBus");
            
            TextMessage message = session.createTextMessage(text);
            message.setStringProperty(SERVICE_NAME, selector);
            session.createProducer(eventBus).send(message);
        } finally {
            if (connection != null) {
                connection.close();
            }
        }
    }
    
    public static Message listenOnEventBus(String selector, Long timeoutInMillis) throws Exception {
        Connection connection = null;
        try {
            connection = connectionFactory.createConnection();
            connection.start();
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Topic eventBus = session.createTopic("EventBus");
            return session.createConsumer(eventBus, selector).receive(timeoutInMillis);
        } finally {
            if (connection != null) {
                connection.close();
            }
        }
    }

    public static Message listenForResponseOnQueue(String correlationId, String queue) throws Exception {
        Connection connection = connectionFactory.createConnection();
        try {
            connection.start();
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Queue responseQueue = session.createQueue(queue);

            //return session.createConsumer(responseQueue).receive(TIMEOUT);
            return session.createConsumer(responseQueue, "JMSCorrelationID='" + correlationId + "'").receive(TIMEOUT);
        } finally {
            connection.close();
        }
    }
}
