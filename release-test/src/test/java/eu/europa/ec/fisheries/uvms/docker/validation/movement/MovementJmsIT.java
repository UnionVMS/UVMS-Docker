package eu.europa.ec.fisheries.uvms.docker.validation.movement;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;

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
	
	private static  MovementHelper movementHelper = new MovementHelper();


	private static final ConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://localhost:61616");
	private static Connection connection;

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

		movementHelper.setResponseMessage(null);
		
		final CreateMovementRequest createMovementRequest = movementHelper.createMovement(connectionFactory,connection);
		while (movementHelper.getResponseMessage() == null)
			;

		Message msg = movementHelper.getResponseMessage();
		CreateMovementResponse createMovementResponse = movementHelper
				.unMarshallCreateMovementResponse(msg);
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
		movementHelper.clearResponseMessageList();
		String ResponseQueueName = "createMovementRouteRequestTest" + UUID.randomUUID().toString().replaceAll("-", "");
		movementHelper.setupResponseConsumer(connectionFactory, connection, ResponseQueueName);

		List<LatLong> route = movementHelper.createRutt();
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

			final CreateMovementRequest createMovementRequest = movementHelper.createMovementRequest(testAsset,
					position);
			movementHelper.sendRequest(connection,  ResponseQueueName,
					createMovementRequest);
		}

		while (movementHelper.getResponseMessageList().size() != numberOfMessages)
			;
		List<Message> responseMessageList = movementHelper.getResponseMessageList();

		List<Message> copyList = new ArrayList<>(responseMessageList);
		for (Message msg : copyList) {
			CreateMovementResponse createMovementResponse = movementHelper.unMarshallCreateMovementResponse(msg);
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
		assertFalse(movementHelper.checkQueueHasElements(connection, "UVMSMovementEvent"));
	}



}
