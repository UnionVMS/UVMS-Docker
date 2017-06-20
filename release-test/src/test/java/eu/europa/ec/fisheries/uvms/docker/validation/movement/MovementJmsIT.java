package eu.europa.ec.fisheries.uvms.docker.validation.movement;

import java.io.StringWriter;
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
import org.databene.contiperf.PerfTest;
import org.databene.contiperf.Required;
import org.databene.contiperf.junit.ContiPerfRule;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

import eu.europa.ec.fisheries.schema.movement.module.v1.CreateMovementRequest;
import eu.europa.ec.fisheries.schema.movement.module.v1.MovementModuleMethod;
import eu.europa.ec.fisheries.schema.movement.v1.MovementBaseType;

public class MovementJmsIT extends Assert {

	private static final String UVMS_MOVEMENT_REQUEST_QUEUE = "UVMSMovementEvent";

	private static final ConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://localhost:61616");
	private static Connection connection;
	private static JAXBContext jaxbContext;

	@Rule
	public ContiPerfRule contiPerfRule = new ContiPerfRule();

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
	@PerfTest(threads = 8, duration = 6000, warmUp = 1000)
	@Required(max = 3000, average = 300, percentile95 = 400, throughput = 20)
	public void createMovementRequestTest() throws Exception {
		final CreateMovementRequest createMovementRequest = new CreateMovementRequest();
		final MovementBaseType movementBaseType = new MovementBaseType();
		createMovementRequest.setMovement(movementBaseType);
		createMovementRequest.setMethod(MovementModuleMethod.CREATE);

		final Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		final Queue queue = session.createQueue(UVMS_MOVEMENT_REQUEST_QUEUE);
		final MessageProducer messageProducer = session.createProducer(queue);
		messageProducer.setDeliveryMode(DeliveryMode.PERSISTENT);
		messageProducer.setTimeToLive(1000000000);
		messageProducer.send(session.createTextMessage(marshall(createMovementRequest)));
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
