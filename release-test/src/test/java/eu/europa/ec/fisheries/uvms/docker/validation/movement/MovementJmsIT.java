package eu.europa.ec.fisheries.uvms.docker.validation.movement;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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
import javax.xml.bind.JAXBException;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import eu.europa.ec.fisheries.schema.movement.module.v1.CreateMovementRequest;
import eu.europa.ec.fisheries.schema.movement.module.v1.CreateMovementResponse;
import eu.europa.ec.fisheries.uvms.docker.validation.asset.AssetTestHelper;
import eu.europa.ec.fisheries.uvms.docker.validation.common.AbstractRestServiceTest;
import eu.europa.ec.fisheries.wsdl.asset.types.Asset;

/**
 * The Class MovementJmsIT.
 */
public class MovementJmsIT extends AbstractRestServiceTest {

	private static final String UVMS_MOVEMENT_REQUEST_QUEUE = "UVMSMovementEvent";

	private static final ConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://localhost:61616");
	private static Connection connection;
	private static volatile Message responseMessage;
	private static volatile List<Message> responseMessageList = Collections.synchronizedList(new ArrayList<Message>());

	@BeforeClass
	public static void beforeClass() {
		try {
			connection = connectionFactory.createConnection();
			connection.setClientID(UUID.randomUUID().toString());
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}

	@AfterClass
	public static void afterClass() {
		try {
			connection.close();
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Creates the movement request test.
	 *
	 * @throws Exception
	 *             the exception
	 */
	@Test(timeout = 10000)
	public void createMovementRequestTest() throws Exception {
		responseMessage = null;
		String ResponseQueueName = "createMovementRequestTest" + UUID.randomUUID().toString().replaceAll("-", "");
		setupResponseConsumer(ResponseQueueName);

		Asset testAsset = AssetTestHelper.createTestAsset();
		final CreateMovementRequest createMovementRequest = MovementHelper.createMovementRequest(testAsset);

		sendRequestToMovement(ResponseQueueName, createMovementRequest);

		while (responseMessage == null)
			;

		CreateMovementResponse createMovementResponse = MovementHelper
				.unMarshallCreateMovementResponse(responseMessage);
		assertNotNull(createMovementResponse);
		assertEquals(null, createMovementResponse.getMovement().getCalculatedCourse());
		assertEquals(null, createMovementResponse.getMovement().getCalculatedSpeed());
		assertFalse(createMovementResponse.getMovement().getMetaData().getAreas().isEmpty());
		assertEquals(createMovementRequest.getMovement().getPosition().getLongitude(),
				createMovementResponse.getMovement().getPosition().getLongitude());
		assertEquals(createMovementRequest.getMovement().getPosition().getLatitude(),
				createMovementResponse.getMovement().getPosition().getLatitude());
		// assertEquals(createMovementRequest.getMovement().getPosition().getAltitude(),createMovementResponse.getMovement().getPosition().getAltitude());
	}

	@Test(timeout = 720000)
	public void createRouteTest() throws Exception {
		responseMessageList.clear();
		String ResponseQueueName = "createMovementRouteRequestTest" + UUID.randomUUID().toString().replaceAll("-", "");
		setupResponseConsumer(ResponseQueueName);

		List<LatLong> route = MovementHelper.createRutt();
		int numberOfPossibleMessages = route.size();
		int numberOfMessages = 5;

		// guard
		numberOfMessages = (numberOfMessages > numberOfPossibleMessages ? numberOfMessages : numberOfMessages);

		Asset testAsset = AssetTestHelper.createTestAsset();

		int counter = 0;
		for (LatLong position : route) {
			if (counter > numberOfMessages)
				break;
			counter++;

			final CreateMovementRequest createMovementRequest = MovementHelper.createMovementRequest(testAsset,
					position);
			sendRequestToMovement(ResponseQueueName, createMovementRequest);
		}

		while (responseMessageList.size() != numberOfMessages)
			;

		List<Message> copyList = new ArrayList<>(responseMessageList);
		for (Message msg : copyList) {
			CreateMovementResponse createMovementResponse = MovementHelper.unMarshallCreateMovementResponse(msg);
			assertNotNull(createMovementResponse);
		}
	}

	/**
	 * Check dead letter queue.
	 *
	 * @throws Exception
	 *             the exception
	 */
	@Test
	public void checkAllMovementsRequestProcessedOnQueue() throws Exception {
		assertFalse(checkMovementQueueHasElements());
	}

	/**
	 * Send request to movement.
	 *
	 * @param ResponseQueueName
	 *            the response queue name
	 * @param createMovementRequest
	 *            the create movement request
	 * @throws JMSException
	 *             the JMS exception
	 * @throws JAXBException
	 *             the JAXB exception
	 */
	private void sendRequestToMovement(String ResponseQueueName, final CreateMovementRequest createMovementRequest)
			throws JMSException, JAXBException {
		final Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		final Queue queue = session.createQueue(UVMS_MOVEMENT_REQUEST_QUEUE);

		final MessageProducer messageProducer = session.createProducer(queue);
		messageProducer.setDeliveryMode(DeliveryMode.PERSISTENT);
		messageProducer.setTimeToLive(1000000000);
		String marshalled = MovementHelper.marshall(createMovementRequest);
		TextMessage createTextMessage = session.createTextMessage(marshalled);
		final Queue responseQueue = session.createQueue(ResponseQueueName);
		createTextMessage.setJMSReplyTo(responseQueue);
		messageProducer.send(createTextMessage);
		session.close();
	}

	/**
	 * Sets the up response consumer.
	 *
	 * @param queueName
	 *            the new up response consumer
	 * @throws Exception
	 *             the exception
	 */
	public void setupResponseConsumer(String queueName) throws Exception {
		Connection consumerConnection = connectionFactory.createConnection();
		consumerConnection.setClientID(UUID.randomUUID().toString());
		final Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		final Queue responseQueue = session.createQueue(queueName);
		MessageConsumer consumer = session.createConsumer(responseQueue);
		consumer.setMessageListener(new ResponseQueueMessageListener());
		connection.start();

	}

	/**
	 * The listener interface for receiving responseQueueMessage events. The
	 * class that is interested in processing a responseQueueMessage event
	 * implements this interface, and the object created with that class is
	 * registered with a component using the component's
	 * <code>addResponseQueueMessageListener<code> method. When the
	 * responseQueueMessage event occurs, that object's appropriate method is
	 * invoked.
	 *
	 * @see ResponseQueueMessageEvent
	 */
	public static class ResponseQueueMessageListener implements MessageListener {
		@Override
		public void onMessage(Message message) {
			responseMessage = message;
			responseMessageList.add(message);
		}
	}

	/**
	 * Check movement queue has elements.
	 *
	 * @return true, if successful
	 * @throws Exception
	 *             the exception
	 */
	private boolean checkMovementQueueHasElements() throws Exception {
		final Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		final Queue queue = session.createQueue("UVMSMovementEvent");
		final QueueBrowser browser = session.createBrowser(queue);
		while (browser.getEnumeration().hasMoreElements()) {
			session.close();
			return true;
		}
		session.close();
		return false;
	}

}
