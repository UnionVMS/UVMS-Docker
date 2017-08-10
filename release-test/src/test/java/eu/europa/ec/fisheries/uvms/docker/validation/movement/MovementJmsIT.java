package eu.europa.ec.fisheries.uvms.docker.validation.movement;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.http.client.ClientProtocolException;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import eu.europa.ec.fisheries.schema.movement.asset.v1.AssetId;
import eu.europa.ec.fisheries.schema.movement.asset.v1.AssetIdType;
import eu.europa.ec.fisheries.schema.movement.asset.v1.AssetType;
import eu.europa.ec.fisheries.schema.movement.module.v1.CreateMovementRequest;
import eu.europa.ec.fisheries.schema.movement.module.v1.CreateMovementResponse;
import eu.europa.ec.fisheries.schema.movement.module.v1.MovementModuleMethod;
import eu.europa.ec.fisheries.schema.movement.v1.MovementActivityType;
import eu.europa.ec.fisheries.schema.movement.v1.MovementActivityTypeType;
import eu.europa.ec.fisheries.schema.movement.v1.MovementBaseType;
import eu.europa.ec.fisheries.schema.movement.v1.MovementPoint;
import eu.europa.ec.fisheries.schema.movement.v1.MovementTypeType;
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
	private static volatile List<Message> responseMessageList = new ArrayList<>();
	

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
		responseMessage=null;
		String ResponseQueueName = "createMovementRequestTest" + UUID.randomUUID().toString().replaceAll("-", "");
		setupResponseConsumer(ResponseQueueName);

		Asset testAsset = createTestAsset();
		final CreateMovementRequest createMovementRequest = createMovementRequest(testAsset);

		sendRequestToMovement(ResponseQueueName, createMovementRequest);

		while (responseMessage == null)
			;

		CreateMovementResponse createMovementResponse = unMarshallCreateMovementResponse(responseMessage);
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
		String ResponseQueueName = "createMovementRouteRequestTest" + UUID.randomUUID().toString().replaceAll("-", "");
		setupResponseConsumer(ResponseQueueName);
		
		

		List<LatLong> route = MovementHelper.createRutt();
		int numberOfPossibleMessages = route.size();
		int numberOfMessages = 5;
		
		// guard
		numberOfMessages = (numberOfMessages > numberOfPossibleMessages ? numberOfMessages : numberOfMessages);

		Asset testAsset = createTestAsset();

		int counter = 0;
		for (LatLong position : route) {
			if(counter > numberOfMessages )break;
			counter++;

			final CreateMovementRequest createMovementRequest = createMovementRequest(testAsset, position);
			sendRequestToMovement(ResponseQueueName, createMovementRequest);
		}

		while (responseMessageList.size() < numberOfMessages)
			;

		for(Message msg   : responseMessageList){
			CreateMovementResponse createMovementResponse = unMarshallCreateMovementResponse(msg);
			assertNotNull(createMovementResponse);			
		}
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
		String marshalled = marshall(createMovementRequest);
		TextMessage createTextMessage = session.createTextMessage(marshalled);
		final Queue responseQueue = session.createQueue(ResponseQueueName);
		createTextMessage.setJMSReplyTo(responseQueue);
		messageProducer.send(createTextMessage);
		session.close();
	}

	/**
	 * Creates the movement request.
	 *
	 * @param testAsset
	 *            the test asset
	 * @return the creates the movement request
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws ClientProtocolException
	 *             the client protocol exception
	 * @throws JsonProcessingException
	 *             the json processing exception
	 * @throws JsonParseException
	 *             the json parse exception
	 * @throws JsonMappingException
	 *             the json mapping exception
	 */

	private CreateMovementRequest createMovementRequest(Asset testAsset) throws IOException, ClientProtocolException,
			JsonProcessingException, JsonParseException, JsonMappingException {
		Date positionTime = getDate(2017, Calendar.DECEMBER, 24, 11, 45, 7, 980);
		return createMovementRequest(testAsset, -16.9, 32.6333333, 5, positionTime);
	}

	private CreateMovementRequest createMovementRequest(Asset testAsset, LatLong obs) throws IOException,
			ClientProtocolException, JsonProcessingException, JsonParseException, JsonMappingException {
		return createMovementRequest(testAsset, obs.longitude, obs.latitude, 5, obs.positionTime);
	}

	/**
	 * 
	 * @param testAsset
	 * @param longitude
	 * @param latitude
	 * @param altitude
	 * @param positionTime
	 * @return CreateMovementRequest
	 * @throws IOException
	 * @throws ClientProtocolException
	 * @throws JsonProcessingException
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 */
	private CreateMovementRequest createMovementRequest(Asset testAsset, double longitude, double latitude,
			double altitude, Date positionTime) throws IOException, ClientProtocolException, JsonProcessingException,
			JsonParseException, JsonMappingException {

		final CreateMovementRequest createMovementRequest = new CreateMovementRequest();
		final MovementBaseType movementBaseType = new MovementBaseType();
		AssetId assetId = new AssetId();
		assetId.setAssetType(AssetType.VESSEL);
		assetId.setIdType(AssetIdType.GUID);
		assetId.setValue(testAsset.getAssetId().getGuid());
		movementBaseType.setAssetId(assetId);
		movementBaseType.setConnectId(testAsset.getAssetId().getGuid());

		MovementActivityType movementActivityType = new MovementActivityType();
		movementBaseType.setActivity(movementActivityType);
		movementActivityType.setMessageId(UUID.randomUUID().toString());
		movementActivityType.setMessageType(MovementActivityTypeType.ANC);

		createMovementRequest.setMovement(movementBaseType);
		createMovementRequest.setMethod(MovementModuleMethod.CREATE);
		createMovementRequest.setUsername("vms_admin_com");

		MovementPoint movementPoint = new MovementPoint();
		movementPoint.setLongitude(longitude);
		movementPoint.setLatitude(latitude);
		movementPoint.setAltitude(altitude);

		movementBaseType.setPosition(movementPoint);
		movementBaseType.setPositionTime(positionTime);

		movementBaseType.setMovementType(MovementTypeType.POS);
		return createMovementRequest;

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
	 * Check dead letter queue.
	 *
	 * @throws Exception
	 *             the exception
	 */
	@Test
	@Ignore
	public void checkDeadLetterQueue() throws Exception {
		assertTrue(checkMovementQueueHasElements());
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
		final Queue queue = session.createQueue("DLQ");
		final QueueBrowser browser = session.createBrowser(queue);
		while (browser.getEnumeration().hasMoreElements()) {
			session.close();
			return true;
		}
		session.close();
		return false;
	}

	/**
	 * Marshall.
	 *
	 * @param createMovementRequest
	 *            the create movement request
	 * @return the string
	 * @throws JAXBException
	 *             the JAXB exception
	 */
	private String marshall(final CreateMovementRequest createMovementRequest) throws JAXBException {
		final StringWriter sw = new StringWriter();
		JAXBContext.newInstance(CreateMovementRequest.class).createMarshaller().marshal(createMovementRequest, sw);
		return sw.toString();
	}

	/**
	 * Un marshall create movement response.
	 *
	 * @param response
	 *            the response
	 * @return the creates the movement response
	 * @throws Exception
	 *             the exception
	 */
	private CreateMovementResponse unMarshallCreateMovementResponse(final Message response) throws Exception {
		TextMessage textMessage = (TextMessage) response;
		JAXBContext jaxbContext = JAXBContext.newInstance(CreateMovementResponse.class);
		return (CreateMovementResponse) jaxbContext.createUnmarshaller()
				.unmarshal(new StringReader(textMessage.getText()));
	}

}
