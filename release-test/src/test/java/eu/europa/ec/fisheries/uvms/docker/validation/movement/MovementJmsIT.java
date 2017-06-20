package eu.europa.ec.fisheries.uvms.docker.validation.movement;
import java.io.StringWriter;
import java.util.UUID;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.databene.contiperf.PerfTest;
import org.databene.contiperf.Required;
import org.databene.contiperf.junit.ContiPerfRule;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

import eu.europa.ec.fisheries.schema.movement.module.v1.CreateMovementRequest;
import eu.europa.ec.fisheries.schema.movement.module.v1.MovementModuleMethod;
import eu.europa.ec.fisheries.schema.movement.v1.MovementBaseType;

public class MovementJmsIT {
	 	
		private static final String UVMS_MOVEMENT = "UVMSMovement";
		
		private static final ConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://localhost:61616");
		private static Connection connection;
		private static JAXBContext jaxbContext;
		
		@Rule
		public ContiPerfRule contiPerfRule = new ContiPerfRule();
		    
		@BeforeClass
		public static void beforeClass()  {
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
	    @Required(max = 2000, average = 200, percentile95 = 300, throughput = 20)
		public void createMovementRequestTest() throws Exception {
	        final CreateMovementRequest createMovementRequest = new CreateMovementRequest();
	        final MovementBaseType movementBaseType = new MovementBaseType();
	        createMovementRequest.setMovement(movementBaseType);
	        createMovementRequest.setMethod(MovementModuleMethod.CREATE);
	        	        
	        final Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
	        final Queue queue = session.createQueue(UVMS_MOVEMENT);
	        final MessageProducer messageProducer = session.createProducer(queue);
	    	messageProducer.send(session.createTextMessage(marshall(createMovementRequest)));
	    }


		private String marshall(final CreateMovementRequest createMovementRequest) throws JAXBException {
	    	final StringWriter sw = new StringWriter();
	    	jaxbContext.createMarshaller().marshal(createMovementRequest, sw);
	    	return sw.toString();
		}
}
