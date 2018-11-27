package eu.europa.ec.fisheries.uvms.docker.validation.exchange;

import eu.europa.ec.fisheries.schema.exchange.module.v1.ProcessedMovementResponse;
import eu.europa.ec.fisheries.schema.exchange.movement.v1.MovementRefTypeType;
import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.MobileTerminalType;
import eu.europa.ec.fisheries.schema.movementrules.asset.v1.AssetId;
import eu.europa.ec.fisheries.schema.movementrules.asset.v1.AssetIdList;
import eu.europa.ec.fisheries.schema.movementrules.asset.v1.AssetIdType;
import eu.europa.ec.fisheries.schema.movementrules.asset.v1.AssetType;
import eu.europa.ec.fisheries.schema.movementrules.exchange.v1.PluginType;
import eu.europa.ec.fisheries.schema.movementrules.module.v1.RulesModuleMethod;
import eu.europa.ec.fisheries.schema.movementrules.module.v1.SetMovementReportRequest;
import eu.europa.ec.fisheries.schema.movementrules.movement.v1.*;
import eu.europa.ec.fisheries.uvms.asset.client.model.AssetDTO;
import eu.europa.ec.fisheries.uvms.docker.validation.asset.AssetTestHelper;
import eu.europa.ec.fisheries.uvms.docker.validation.mobileterminal.MobileTerminalTestHelper;
import eu.europa.ec.fisheries.uvms.docker.validation.movement.LatLong;
import eu.europa.ec.fisheries.uvms.docker.validation.movement.MovementHelper;
import eu.europa.ec.fisheries.uvms.docker.validation.system.helper.NAFHelper;
import eu.europa.ec.fisheries.uvms.movementrules.model.exception.MovementRulesModelMapperException;
import eu.europa.ec.fisheries.uvms.movementrules.model.exception.MovementRulesModelMarshallException;
import eu.europa.ec.fisheries.uvms.movementrules.model.mapper.JAXBMarshaller;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.junit.Ignore;
import org.junit.Test;

import javax.jms.*;
import javax.jms.Queue;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;
import java.io.StringReader;
import java.time.Duration;
import java.time.Instant;
import java.util.*;

public class NAFExchangePerformanceTests {

        private ConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://localhost:61616");
        //private ConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://livm73u.havochvatten.se:61616");
        private static final String MOVEMENTRULES_QUEUE = "UVMSMovementRulesEvent";
        private static final String RESPONSE_QUEUE = "IntegrationTestsResponseQueue";

        private static MovementHelper movementHelper = new MovementHelper();

        private static Map<String, JAXBContext> contexts = new HashMap<>();


        @Test
        @Ignore
        public void createRouteTestTitanic1000PositionsSync() throws Exception{   //Needs a special version of exchange that respond on the sales queue to work!!!!

            AssetDTO testAsset = AssetTestHelper.createTestAsset();
            MobileTerminalType mobileTerminalType = MobileTerminalTestHelper.createMobileTerminalType();
            MobileTerminalTestHelper.assignMobileTerminal(testAsset, mobileTerminalType);
            List<LatLong> route = movementHelper.createRuttCobhNewYork(1000, 0.06f);                //0.1F = 654 pos    0.01 = 6543     0.07 = 934   0.06 = 1090


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


            int i = 0;
            Instant b4 = Instant.now();
            Instant lastIteration = Instant.now();
            List<Duration> averageDurations = new ArrayList<>();

            for(LatLong pos : route) {
                RawMovementType move = createBasicMovement(assetId, testAsset.getName(), pos);
                String request = createSetMovementReportRequest(PluginType.FLUX, move, "PerformanceTester");

                String corrId = sendMessageToRules(request, RulesModuleMethod.SET_MOVEMENT_REPORT.value());

                Message message = /*MessageHelper.*/listenForResponseOnQueue("PerformanceTester", "IntegrationTestsResponseQueue");

                ProcessedMovementResponse movementResponse = JAXBMarshaller.unmarshallTextMessage((TextMessage) message, ProcessedMovementResponse.class);
                if(movementResponse.getMovementRefType().getType().equals(MovementRefTypeType.ALARM)){
                    System.out.println("Alarm: " + i + ", ");
                }
                i++;
                if((i % 10) == 0){
                    System.out.println("Created movement number: " + i + " Time so far: " + humanReadableFormat(Duration.between(b4, Instant.now())) + " Time since last 10: " + humanReadableFormat(Duration.between(lastIteration, Instant.now())));
                    //System.out.println("Time for 10 movement for last iteration: " + Duration.between(lastIteration,Instant.now()).toString());
                    averageDurations.add(Duration.between(lastIteration, Instant.now()));
                    lastIteration = Instant.now();

                }


            }

            averageDurations.stream().forEach(dur -> System.out.print(humanReadableFormat(dur) + ", "));
            System.out.println();
        }

    @Test
    @Ignore
    public void createRouteTestTitanic10ships10PositionsAsync() throws Exception {   //Needs a special version of exchange that respond on the sales queue to work!!!!
        List<LatLong> route = movementHelper.createRuttCobhNewYork(10, 0.06f);                //0.1F = 654 pos    0.01 = 6543     0.07 = 934   0.06 = 1090
        sendRouteToNAFOnXShipsAsync(10, route);
    }

    @Test
    @Ignore
    public void createRouteTestTitanic1000PositionsAsync() throws Exception{   //Needs a special version of exchange that respond on the sales queue to work!!!!

        List<LatLong> route = movementHelper.createRuttCobhNewYork(1000, 0.06f);                //0.1F = 654 pos    0.01 = 6543     0.07 = 934   0.06 = 1090
        sendRouteToNAFOnXShipsAsync(1, route);

    }

    @Test
    @Ignore
    public void createRouteTestTitanic10ships1000PositionsAsync() throws Exception{   //Needs a special version of exchange that respond on the sales queue to work!!!!
        List<LatLong> route = movementHelper.createRuttCobhNewYork(1000, 0.06f);                //0.1F = 654 pos    0.01 = 6543     0.07 = 934   0.06 = 1090
        sendRouteToNAFOnXShipsAsync(10, route);

    }

    @Test
    @Ignore
    public void createRouteTestTitanic10ships6000PositionsAsync() throws Exception{   //Needs a special version of exchange that respond on the sales queue to work!!!!
        List<LatLong> route = movementHelper.createRuttCobhNewYork(6000, 0.01f);                //0.1F = 654 pos    0.01 = 6543     0.07 = 934   0.06 = 1090
        sendRouteToNAFOnXShipsAsync(10, route);
    }

    @Test
    @Ignore
    public void createRouteTestTitanic10ships60000PositionsAsync() throws Exception{   //Needs a special version of exchange that respond on the sales queue to work!!!!
        List<LatLong> route = movementHelper.createRuttCobhNewYork(60000, 0.001f);                //0.1F = 654 pos    0.01 = 6543     0.07 = 934   0.06 = 1090
        sendRouteToNAFOnXShipsAsync(10, route);
    }

    @Test
    @Ignore
    public void createRouteTestTitanic10ships600000PositionsAsync() throws Exception{   //Needs a special version of exchange that respond on the sales queue to work!!!!
        List<LatLong> route = movementHelper.createRuttCobhNewYork(600000, 0.0001f);                //0.1F = 654 pos    0.01 = 6543     0.07 = 934   0.06 = 1090
        sendRouteToNAFOnXShipsAsync(10, route);
    }

    @Test
    @Ignore
    public void createRouteTestTitanic10ships100000PositionsAsync() throws Exception{   //Needs a special version of exchange that respond on the sales queue to work!!!!
        List<LatLong> route = movementHelper.createRuttCobhNewYork(100000, 0.0006f);                //0.1F = 654 pos    0.01 = 6543     0.07 = 934   0.06 = 1090
        sendRouteToNAFOnXShipsAsync(10, route);
    }

    @Test
    @Ignore
    public void createRouteTestTitanic60ships6000PositionsAsync() throws Exception{   //Needs a special version of exchange that respond on the sales queue to work!!!!
        List<LatLong> route = movementHelper.createRuttCobhNewYork(6000, 0.01f);                //0.1F = 654 pos    0.01 = 6543     0.07 = 934   0.06 = 1090
        sendRouteToNAFOnXShipsAsync(60, route);
    }

    public void sendRouteToNAFOnXShipsAsync(int nrOfShips, List<LatLong> route) throws Exception{   //Needs a special version of exchange that respond on the sales queue to work!!!! 

        List<AssetId> assetList = new ArrayList<>();
        List<AssetDTO> assetDTOList = new ArrayList<>();
        List<String> nameList = new ArrayList<>();

        System.out.println("Start creating assets");
        for(int i = 0; i < nrOfShips; i++ ){
            AssetDTO testAsset = AssetTestHelper.createTestAsset();
            MobileTerminalType mobileTerminalType = MobileTerminalTestHelper.createMobileTerminalType();
            MobileTerminalTestHelper.assignMobileTerminal(testAsset, mobileTerminalType);

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

            assetList.add(assetId);
            nameList.add(testAsset.getName());
        }
        System.out.println("Done with creating assets");


        int i = 0;
        Instant b4 = Instant.now();
        Instant lastIteration = Instant.now();
        List<Duration> averageDurations = new ArrayList<>();
        List<String> corrList = new ArrayList<>();

        for(LatLong pos : route) {
            AssetDTO asset = assetDTOList.get(i % nrOfShips);

            NAFHelper.sendPositionToNAFPlugin(pos, asset);

            //RawMovementType move = createBasicMovement(assetId, nameList.get(i % nrOfShips), pos);
            //String request = createSetMovementReportRequest(PluginType.FLUX, move, "PerformanceTester");

            //String corrId = sendMessageToRules(request, RulesModuleMethod.SET_MOVEMENT_REPORT.value());
            //corrList.add(corrId);


            i++;
            if((i % 10) == 0){
                System.out.println("Created movement number: " + i + " Time so far: " + humanReadableFormat(Duration.between(b4, Instant.now())) + " Time since last 10: " + humanReadableFormat(Duration.between(lastIteration, Instant.now())));
                //System.out.println("Time for 10 movement for last iteration: " + Duration.between(lastIteration,Instant.now()).toString());
                averageDurations.add(Duration.between(lastIteration, Instant.now()));
                lastIteration = Instant.now();

            }


        }

        Instant middle = Instant.now();
        i = 0;
        for(LatLong pos : route){
            Message message = /*MessageHelper.*/listenForResponseOnQueue("PerformanceTester", "UVMSSalesEvent");
            /*ProcessedMovementResponse movementResponse = JAXBMarshaller.unmarshallTextMessage((TextMessage) message, ProcessedMovementResponse.class);
            if(movementResponse.getMovementRefType().getType().equals(MovementRefTypeType.ALARM)){
                System.out.println("Alarm: " + i + ", ");
            }*/
            i++;

            if((i % 10) == 0){
                System.out.println("Recieved movement number: " + i + " Time so far: " + humanReadableFormat(Duration.between(b4, Instant.now())) + " Time since last 10: " + humanReadableFormat(Duration.between(lastIteration, Instant.now())));
                //System.out.println("Time for 10 movement for last iteration: " + Duration.between(lastIteration,Instant.now()).toString());
                averageDurations.add(Duration.between(lastIteration, Instant.now()));
                lastIteration = Instant.now();

            }
        }

        averageDurations.stream().forEach(dur -> System.out.print(humanReadableFormat(dur) + ", "));
        System.out.println();
    }

    public String sendMessageToRules(String text, String requestType) throws Exception {
        Connection connection = connectionFactory.createConnection();
        try {
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Queue responseQueue = session.createQueue(RESPONSE_QUEUE);
            Queue assetQueue = session.createQueue(MOVEMENTRULES_QUEUE);

            TextMessage message = session.createTextMessage();
            message.setJMSReplyTo(responseQueue);
            message.setText(text);
            message.setStringProperty("FUNCTION", requestType);

            session.createProducer(assetQueue).send(message);

            return message.getJMSMessageID();
        } finally {
            connection.close();
        }
    }

    public static RawMovementType createBasicMovement(AssetId assetId, String assetName,  LatLong pos) {
        RawMovementType movement = new RawMovementType();


        movement.setAssetId(assetId);
        movement.setAssetName(assetName);
        movement.setFlagState("SWE");
        movement.setDateRecieved(new Date());
        movement.setMovementType(MovementTypeType.POS);
        movement.setPluginName("PLUGIN");
        movement.setPluginType("SATELLITE_RECEIVER");
        MovementPoint movementPoint = new MovementPoint();
        movementPoint.setLatitude(pos.latitude);
        movementPoint.setLongitude(pos.longitude);
        movement.setPosition(movementPoint);
        movement.setPositionTime(new Date());
        movement.setReportedCourse(pos.bearing);
        movement.setReportedSpeed(pos.speed);
        movement.setSource(MovementSourceType.INMARSAT_C);
        movement.setComChannelType(MovementComChannelType.NAF);
        return movement;
    }

    public static String createSetMovementReportRequest(PluginType type, RawMovementType rawMovementType, String username) throws MovementRulesModelMapperException {
        SetMovementReportRequest request = new SetMovementReportRequest();
        request.setMethod(RulesModuleMethod.SET_MOVEMENT_REPORT);
        request.setType(type);
        request.setUsername(username);
        request.setRequest(rawMovementType);
        return JAXBMarshaller.marshallJaxBObjectToString(request);
    }

    public static <R> R unmarshallTextMessage(TextMessage textMessage, Class clazz) throws MovementRulesModelMarshallException {
        try {
            JAXBContext jc = contexts.get(clazz.getName());
            if (jc == null) {
                long before = System.currentTimeMillis();
                jc = JAXBContext.newInstance(clazz);
                contexts.put(clazz.getName(), jc);

            }
            Unmarshaller unmarshaller = jc.createUnmarshaller();
            StringReader sr = new StringReader(textMessage.getText());
            StreamSource source = new StreamSource(sr);
            long before = System.currentTimeMillis();
            R object = (R) unmarshaller.unmarshal(source);
            return object;
        } catch (JMSException | JAXBException ex) {
            throw new MovementRulesModelMarshallException("[Error when unmarshalling response in ResponseMapper. Expected class was " + clazz.getName() + " ]", ex);
        }
    }

    public Message listenForResponseOnQueue(String correlationId, String queue) throws Exception {
        Connection connection = connectionFactory.createConnection();
        try {
            connection.start();
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Queue responseQueue = session.createQueue(queue);

            //return session.createConsumer(responseQueue).receive(TIMEOUT);
            return session.createConsumer(responseQueue).receive(60000);
        } finally {
            connection.close();
        }
    }

    public static String humanReadableFormat(Duration duration) {
        return duration.toString()
                .substring(2)
                .replaceAll("(\\d[HMS])(?!$)", "$1 ")
                .toLowerCase();
    }
}


