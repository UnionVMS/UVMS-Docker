package eu.europa.ec.fisheries.uvms.docker.validation.exchange;

import eu.europa.ec.fisheries.schema.movementrules.asset.v1.AssetId;
import eu.europa.ec.fisheries.schema.movementrules.asset.v1.AssetIdList;
import eu.europa.ec.fisheries.schema.movementrules.asset.v1.AssetIdType;
import eu.europa.ec.fisheries.schema.movementrules.asset.v1.AssetType;
import eu.europa.ec.fisheries.uvms.asset.client.model.AssetDTO;
import eu.europa.ec.fisheries.uvms.commons.message.impl.JMSUtils;
import eu.europa.ec.fisheries.uvms.docker.validation.asset.AssetTestHelper;
import eu.europa.ec.fisheries.uvms.docker.validation.common.AbstractRest;
import eu.europa.ec.fisheries.uvms.docker.validation.mobileterminal.MobileTerminalTestHelper;
import eu.europa.ec.fisheries.uvms.docker.validation.mobileterminal.dto.MobileTerminalDto;
import eu.europa.ec.fisheries.uvms.docker.validation.movement.AuthorizationHeaderWebTarget;
import eu.europa.ec.fisheries.uvms.docker.validation.movement.LatLong;
import eu.europa.ec.fisheries.uvms.docker.validation.movement.MovementHelper;
import eu.europa.ec.fisheries.uvms.docker.validation.system.helper.NAFHelper;
import org.apache.activemq.artemis.api.core.TransportConfiguration;
import org.apache.activemq.artemis.api.jms.ActiveMQJMSClient;
import org.apache.activemq.artemis.api.jms.JMSFactoryType;
import org.apache.activemq.artemis.core.remoting.impl.netty.NettyConnectorFactory;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import javax.jms.*;
import javax.jms.Queue;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.sse.SseEventSource;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;
import java.io.StringReader;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class NAFExchangePerformanceIT extends AbstractRest {

    private static final String MOVEMENTRULES_QUEUE = "UVMSMovementRulesEvent";
    private static final String RESPONSE_QUEUE = "IntegrationTestsResponseQueue";

    private Map<String, JAXBContext> contexts = new HashMap<>();

    private static Connection connection;
    private static MessageProducer messageProducer;
    private static Session session;

    private static MovementHelper movementHelper;

    @BeforeClass
    public static void setup() throws JMSException {
        Map<String, Object> params = new HashMap<>();
        params.put("host", "localhost");
        params.put("port", 5445);
        TransportConfiguration transportConfiguration = new TransportConfiguration(NettyConnectorFactory.class.getName(), params);
        ConnectionFactory connectionFactory = ActiveMQJMSClient.createConnectionFactoryWithoutHA(JMSFactoryType.CF,transportConfiguration);
        connection = connectionFactory.createConnection("test", "test");
        connection.setClientID(UUID.randomUUID().toString());
        connection.start();
        session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        Queue movementQueue = session.createQueue(MOVEMENTRULES_QUEUE);
        messageProducer = session.createProducer(movementQueue);
        movementHelper = new MovementHelper();
    }

    @AfterClass
    public static void cleanup() {
        movementHelper.close();
        JMSUtils.disconnectQueue(connection, session, messageProducer);
    }

    @Test
    @Ignore
    public void createRouteTestTitanic10ships10PositionsAsync() throws Exception { //Needs a special version of exchange that respond on the sales queue to work!!!!
        List<LatLong> route = movementHelper.createRuttCobhNewYork(10, 0.06f); //0.1F = 654 pos    0.01 = 6543     0.07 = 934   0.06 = 1090
        sendRouteToNAFOnXShipsAsync(10, route);
    }

    @Test
    @Ignore
    public void createRouteTestTitanic1000PositionsAsync() throws Exception{ //Needs a special version of exchange that respond on the sales queue to work!!!!
        List<LatLong> route = movementHelper.createRuttCobhNewYork(1000, 0.06f); //0.1F = 654 pos    0.01 = 6543     0.07 = 934   0.06 = 1090
        sendRouteToNAFOnXShipsAsync(1, route);
    }

    @Test
    @Ignore
    public void createRouteTestTitanic10ships1000PositionsAsync() throws Exception{ //Needs a special version of exchange that respond on the sales queue to work!!!!
        List<LatLong> route = movementHelper.createRuttCobhNewYork(1000, 0.06f); //0.1F = 654 pos    0.01 = 6543     0.07 = 934   0.06 = 1090
        sendRouteToNAFOnXShipsAsync(10, route);
    }

    @Test
    @Ignore
    public void createRouteTestTitanic10ships6000PositionsAsync() throws Exception{ //Needs a special version of exchange that respond on the sales queue to work!!!!
        List<LatLong> route = movementHelper.createRuttCobhNewYork(6000, 0.01f); //0.1F = 654 pos    0.01 = 6543     0.07 = 934   0.06 = 1090
        sendRouteToNAFOnXShipsAsync(20, route);
    }

    @Test
    @Ignore
    public void createRouteTestTitanic10ships60000PositionsAsync() throws Exception{ //Needs a special version of exchange that respond on the sales queue to work!!!!
        List<LatLong> route = movementHelper.createRuttCobhNewYork(60000, 0.001f); //0.1F = 654 pos    0.01 = 6543     0.07 = 934   0.06 = 1090
        sendRouteToNAFOnXShipsAsync(10, route);
    }

    @Test
    @Ignore
    public void createRouteTestTitanic10ships600000PositionsAsync() throws Exception{ //Needs a special version of exchange that respond on the sales queue to work!!!!
        List<LatLong> route = movementHelper.createRuttCobhNewYork(600000, 0.0001f); //0.1F = 654 pos    0.01 = 6543     0.07 = 934   0.06 = 1090
        sendRouteToNAFOnXShipsAsync(10, route);
    }

    @Test
    @Ignore
    public void createRouteTestTitanic10ships100000PositionsAsync() throws Exception{ //Needs a special version of exchange that respond on the sales queue to work!!!!
        List<LatLong> route = movementHelper.createRuttCobhNewYork(100000, 0.0006f); //0.1F = 654 pos    0.01 = 6543     0.07 = 934   0.06 = 1090
        sendRouteToNAFOnXShipsAsync(10, route);
    }

    @Test
    @Ignore
    public void createRouteTestTitanic60ships6000PositionsAsync() throws Exception{ //Needs a special version of exchange that respond on the sales queue to work!!!!
        List<LatLong> route = movementHelper.createRuttCobhNewYork(6000, 0.01f); //0.1F = 654 pos    0.01 = 6543     0.07 = 934   0.06 = 1090
        sendRouteToNAFOnXShipsAsync(60, route);
    }

    private void sendRouteToNAFOnXShipsAsync(int nrOfShips, List<LatLong> route) throws Exception{ //Needs a special version of exchange that respond on the sales queue to work!!!!

        List<AssetDTO> assetDTOList = new ArrayList<>();

        System.out.println("Start creating assets");

        for(int i = 0; i < nrOfShips; i++ ){
            AssetDTO testAsset = AssetTestHelper.createTestAsset();
            MobileTerminalDto mobileTerminal = MobileTerminalTestHelper.createMobileTerminal();
            MobileTerminalTestHelper.assignMobileTerminal(testAsset, mobileTerminal);

            assetDTOList.add(testAsset);

            AssetId assetId = new AssetId();
            assetId.setAssetType(AssetType.VESSEL);
            AssetIdList assetIdList = new AssetIdList();
            assetIdList.setIdType(AssetIdType.IRCS);
            assetIdList.setValue(testAsset.getIrcs());
            assetId.getAssetIdList().add(assetIdList);

            assetIdList = new AssetIdList();
            assetIdList.setIdType(AssetIdType.CFR);
            assetIdList.setValue(testAsset.getCfr());
            assetId.getAssetIdList().add(assetIdList);
        }

        System.out.println("Done with creating assets");

        int i = 0;
        Instant b4 = Instant.now();
        Instant lastSent = Instant.now();
        List<Duration> averageDurations = new ArrayList<>();
        List<String> movements = Collections.synchronizedList(new ArrayList<>());
        Instant lastRec = Instant.now();

        for (LatLong pos : route) {
            AssetDTO asset = assetDTOList.get(i % nrOfShips);
            NAFHelper.sendPositionToNAFPlugin(pos, asset);
            i++;
            if ((i % 10) == 0) {
                System.out.println("Created movement number: " + i + " Time so far: "
                        + humanReadableFormat(Duration.between(b4, Instant.now())) + " Time since last 10 sent: "
                        + humanReadableFormat(Duration.between(lastSent, Instant.now())));

                //System.out.println("Time for 10 movement for last iteration: " + Duration.between(lastIteration,Instant.now()).toString());
                averageDurations.add(Duration.between(lastSent, Instant.now()));
                lastSent = Instant.now();
            }
        }

        while (movements.size() < route.size()) {
            Thread.sleep(100);
            if(Duration.between(lastRec, Instant.now()).getSeconds() > 90){
                throw new RuntimeException("More then 30 seconds since last received. Received so far: "
                        + movements.size() + " Time of death: " + humanReadableFormat(Duration.between(b4, Instant.now())));
            }
        }

        averageDurations.forEach(dur -> System.out.print(humanReadableFormat(dur) + ", "));
        System.out.println();
    }

    public String sendMessageToRules(String text, String requestType) throws Exception {
        try (Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE)) {
            Queue responseQueue = session.createQueue(RESPONSE_QUEUE);
            Queue assetQueue = session.createQueue(MOVEMENTRULES_QUEUE);

            TextMessage message = session.createTextMessage();
            message.setJMSReplyTo(responseQueue);
            message.setText(text);
            message.setStringProperty("FUNCTION", requestType);

            session.createProducer(assetQueue).send(message);
            return message.getJMSMessageID();
        }
    }

    public <R> R unmarshallTextMessage(TextMessage textMessage, Class clazz) {
        try {
            JAXBContext jc = contexts.get(clazz.getName());
            if (jc == null) {
                jc = JAXBContext.newInstance(clazz);
                contexts.put(clazz.getName(), jc);

            }
            Unmarshaller unmarshaller = jc.createUnmarshaller();
            StringReader sr = new StringReader(textMessage.getText());
            StreamSource source = new StreamSource(sr);
            R object = (R) unmarshaller.unmarshal(source);
            return object;
        } catch (JMSException | JAXBException ex) {
            throw new IllegalArgumentException("[Error when unmarshalling response in ResponseMapper. Expected class was "
                    + clazz.getName() + " ]", ex);
        }
    }

    public Message listenForResponseOnQueue(String correlationId, String queue) throws Exception {
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        try {
            Queue responseQueue = session.createQueue(queue);
            //return session.createConsumer(responseQueue).receive(TIMEOUT);
            return session.createConsumer(responseQueue).receive(60000);
        } finally {
            session.close();
        }
    }

    private static String humanReadableFormat(Duration duration) {
        return duration.toString()
                .substring(2)
                .replaceAll("(\\d[HMS])(?!$)", "$1 ")
                .toLowerCase();
    }

    public static SseEventSource getSseStream() {
        WebTarget target = getWebTarget().path("movement-rules/rest/sse/subscribe");
        AuthorizationHeaderWebTarget jwtTarget = new AuthorizationHeaderWebTarget(target, getValidJwtToken());
        return SseEventSource.
                target(jwtTarget).reconnectingEvery(1, TimeUnit.SECONDS).build();
    }
}
