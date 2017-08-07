package eu.europa.ec.fisheries.uvms.docker.validation.movement;

import java.io.StringWriter;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.UUID;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.DeliveryMode;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.QueueBrowser;
import javax.jms.Session;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import eu.europa.ec.fisheries.schema.movement.asset.v1.AssetId;
import eu.europa.ec.fisheries.schema.movement.asset.v1.AssetIdType;
import eu.europa.ec.fisheries.schema.movement.asset.v1.AssetType;
import eu.europa.ec.fisheries.schema.movement.module.v1.CreateMovementRequest;
import eu.europa.ec.fisheries.schema.movement.module.v1.MovementModuleMethod;
import eu.europa.ec.fisheries.schema.movement.v1.MovementActivityType;
import eu.europa.ec.fisheries.schema.movement.v1.MovementActivityTypeType;
import eu.europa.ec.fisheries.schema.movement.v1.MovementBaseType;
import eu.europa.ec.fisheries.schema.movement.v1.MovementPoint;
import eu.europa.ec.fisheries.schema.movement.v1.MovementTypeType;
import eu.europa.ec.fisheries.uvms.docker.validation.common.AbstractRestServiceTest;
import eu.europa.ec.fisheries.wsdl.asset.types.Asset;

public class MovementJmsIT extends AbstractRestServiceTest {

	private static final String UVMS_MOVEMENT_REQUEST_QUEUE = "UVMSMovementEvent";

	private static final ConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://localhost:61616");
	private static Connection connection;
	private static JAXBContext jaxbContext;

	@BeforeClass
	public static void beforeClass() {
		try {
			connection = connectionFactory.createConnection();
			connection.setClientID(UUID.randomUUID().toString());
			jaxbContext = JAXBContext.newInstance(CreateMovementRequest.class);
		} catch (JMSException | JAXBException e) {
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

	@Test
	@Ignore
	public void createMovementRequestTest() throws Exception {
		Asset testAsset = createTestAsset();

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
		// Funchal
		movementPoint.setLongitude(-16.9);
		movementPoint.setLatitude(32.6333333);

		movementPoint.setAltitude(5d);
		movementBaseType.setPosition(movementPoint);


		Date positionTime = getDate(2017, Calendar.DECEMBER, 24, 11, 45, 7, 980)	;
		movementBaseType.setPositionTime(positionTime);

		movementBaseType.setMovementType(MovementTypeType.POS);



		final Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		final Queue queue = session.createQueue(UVMS_MOVEMENT_REQUEST_QUEUE);
		final MessageProducer messageProducer = session.createProducer(queue);
		messageProducer.setDeliveryMode(DeliveryMode.PERSISTENT);
		messageProducer.setTimeToLive(1000000000);
		String marshalled = marshall(createMovementRequest);
		messageProducer.send(session.createTextMessage(marshalled));


		final QueueBrowser browser = session.createBrowser(queue);
		Enumeration enumeration = browser.getEnumeration();
		while (enumeration.hasMoreElements()) {
			Object obj = enumeration.nextElement();
			System.out.println(String.valueOf(obj));
		}




		session.close();
	}

	@Test
	@Ignore
	public void checkDeadLetterQueue() throws Exception {
		assertTrue(checkMovementQueueHasElements());
	}

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

	private String marshall(final CreateMovementRequest createMovementRequest) throws JAXBException {
		final StringWriter sw = new StringWriter();
		jaxbContext.createMarshaller().marshal(createMovementRequest, sw);
		return sw.toString();
	}
}
