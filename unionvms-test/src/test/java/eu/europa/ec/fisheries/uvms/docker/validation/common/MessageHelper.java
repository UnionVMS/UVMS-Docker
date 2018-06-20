package eu.europa.ec.fisheries.uvms.docker.validation.common;

import java.util.UUID;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.DeliveryMode;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.QueueBrowser;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;
import org.apache.activemq.ActiveMQConnectionFactory;

public final class MessageHelper {

	private static final ConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://localhost:61616");

	public static Message getMessageResponse(String queueName, final String msg) throws Exception {
		ResponseQueueMessageListener listener = new ResponseQueueMessageListener();
		String responseQueueName = queueName + "Response" + UUID.randomUUID().toString().replaceAll("-", "");
		setupResponseConsumer(responseQueueName, listener);

		Connection connection = connectionFactory.createConnection();
		connection.setClientID(UUID.randomUUID().toString());

		final Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		final Queue queue = session.createQueue(queueName);

		final MessageProducer messageProducer = session.createProducer(queue);
		messageProducer.setDeliveryMode(DeliveryMode.PERSISTENT);
		messageProducer.setTimeToLive(1000000000);
		TextMessage createTextMessage = session.createTextMessage(msg);
		final Queue responseQueue = session
				.createQueue(responseQueueName);
		createTextMessage.setJMSReplyTo(responseQueue);
		messageProducer.send(createTextMessage);
		session.close();

		while (listener.message == null)
			;

		connection.close();

		return listener.getMessage();
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


	private static void setupResponseConsumer(String queueName, ResponseQueueMessageListener listener) throws Exception {
		Connection consumerConnection = connectionFactory.createConnection();
		consumerConnection.setClientID(UUID.randomUUID().toString());
		final Session session = consumerConnection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		final Queue responseQueue = session.createQueue(queueName);
		MessageConsumer consumer = session.createConsumer(responseQueue);

		consumer.setMessageListener(listener);
		listener.setConsumerConnection(consumerConnection);
		consumerConnection.start();

	}

	private static class ResponseQueueMessageListener implements MessageListener {
		private volatile Message message = null;
		private Connection consumerConnection;


		public void setConsumerConnection(Connection consumerConnection) {
			this.consumerConnection = consumerConnection;
		}

		@Override
		public void onMessage(Message message) {
			this.message = message;
			try {
				this.consumerConnection.close();
			} catch (JMSException e) {
				e.printStackTrace();
			}
		}

		public Message getMessage() {
			return message;
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

	public static boolean checkQueueHasElements(String queueName) throws Exception {
		Connection connection = connectionFactory.createConnection();
		final Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		final Queue queue = session.createQueue(queueName);
		final QueueBrowser browser = session.createBrowser(queue);
		while (browser.getEnumeration().hasMoreElements()) {
			session.close();
			connection.close();
			return true;
		}
		session.close();
		connection.close();
		return false;
	}

    public static Message listenOnEventBus(String selector, Long timeoutInMillis) throws Exception {
        Connection connection = connectionFactory.createConnection();
        try {
            connection.start();
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Topic eventBus = session.createTopic("EventBus");
            return session.createConsumer(eventBus, selector).receive(timeoutInMillis);
        } finally {
            connection.close();
        }
	}
	
}
