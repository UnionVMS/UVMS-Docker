package eu.europa.ec.fisheries.uvms.docker.validation.movement;

import java.io.StringReader;
import java.io.StringWriter;
import java.math.BigInteger;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.sse.InboundSseEvent;
import javax.ws.rs.sse.SseEventSource;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.jboss.resteasy.client.jaxrs.internal.ClientWebTarget;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.MobileTerminalType;
import eu.europa.ec.fisheries.schema.movement.common.v1.ExceptionType;
import eu.europa.ec.fisheries.schema.movement.module.v1.GetMovementListByQueryRequest;
import eu.europa.ec.fisheries.schema.movement.module.v1.GetMovementListByQueryResponse;
import eu.europa.ec.fisheries.schema.movement.module.v1.MovementModuleMethod;
import eu.europa.ec.fisheries.schema.movement.search.v1.ListCriteria;
import eu.europa.ec.fisheries.schema.movement.search.v1.ListPagination;
import eu.europa.ec.fisheries.schema.movement.search.v1.MovementQuery;
import eu.europa.ec.fisheries.schema.movement.search.v1.SearchKey;
import eu.europa.ec.fisheries.uvms.asset.client.model.AssetDTO;
import eu.europa.ec.fisheries.uvms.docker.validation.asset.AssetTestHelper;
import eu.europa.ec.fisheries.uvms.docker.validation.common.AbstractRest;
import eu.europa.ec.fisheries.uvms.docker.validation.common.MessageHelper;
import eu.europa.ec.fisheries.uvms.docker.validation.mobileterminal.MobileTerminalTestHelper;
import eu.europa.ec.fisheries.uvms.docker.validation.mobileterminal.dto.MobileTerminalDto;
import eu.europa.ec.fisheries.uvms.docker.validation.movement.model.IncomingMovement;
import org.junit.Ignore;
import org.junit.Test;

import javax.jms.Message;
import javax.jms.TextMessage;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.sse.InboundSseEvent;
import javax.ws.rs.sse.SseEventSource;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import java.io.StringReader;
import java.io.StringWriter;
import java.math.BigInteger;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

public class MovementPerformanceIT extends AbstractRest {

    private static MovementHelper movementHelper;
    private static MessageHelper messageHelper;

    @BeforeClass
    public static void setup() throws JMSException {
        movementHelper = new MovementHelper();
        messageHelper = new MessageHelper();
    }

    @AfterClass
    public static void cleanup() {
        movementHelper.close();
        messageHelper.close();
    }

    @Test
    @Ignore
    public void runEveryAreaTest20TimesAndGetAverageTime() throws Exception{

        Instant b4 = Instant.now();
        Instant start = Instant.now();
        for (int i = 0; i < 20; i++){
            spikyThingOverIrelandTest();
        }
        System.out.println("Done with spikyThingOverIrelandTest. Total time: " + humanReadableFormat(Duration.between(start, Instant.now())) + " Average time: " + humanReadableFormat(Duration.between(start,Instant.now()).dividedBy(20l)));

        start = Instant.now();
        for (int i = 0; i < 20; i++){
            multipointPolygonInAVeryRoughCircleTest();
        }
        System.out.println("Done with multipointPolygonInAVeryRoughCircleTest. Total time: " + humanReadableFormat(Duration.between(start, Instant.now())) + " Average time: " + humanReadableFormat(Duration.between(start,Instant.now()).dividedBy(20l)));

        start = Instant.now();
        for (int i = 0; i < 20; i++){
            sphereOverIrelandTest();
        }
        System.out.println("Done with sphereOverIrelandTest. Total time: " + humanReadableFormat(Duration.between(start, Instant.now())) + " Average time: " + humanReadableFormat(Duration.between(start,Instant.now()).dividedBy(20l)));

        start = Instant.now();
        for (int i = 0; i < 20; i++){
            triangleAroundSouthernIrelandTest();
        }
        System.out.println("Done with triangleAroundSouthernIrelandTest. Total time: " + humanReadableFormat(Duration.between(start, Instant.now())) + " Average time: " + humanReadableFormat(Duration.between(start,Instant.now()).dividedBy(20l)));

        start = Instant.now();
        for (int i = 0; i < 20; i++){
            spikyBallOverIrelandTest();
        }
        System.out.println("Done with spikyBallOverIrelandTest. Total time: " + humanReadableFormat(Duration.between(start, Instant.now())) + " Average time: " + humanReadableFormat(Duration.between(start,Instant.now()).dividedBy(20l)));

        start = Instant.now();
        for (int i = 0; i < 20; i++){
            hollowSphereOverIrelandTest();
        }
        System.out.println("Done with hollowSphereOverIrelandTest. Total time: " + humanReadableFormat(Duration.between(start, Instant.now())) + " Average time: " + humanReadableFormat(Duration.between(start,Instant.now()).dividedBy(20l)));

        start = Instant.now();
        for (int i = 0; i < 20; i++){
            squareInTheMiddleOfSpainTest();
        }
        System.out.println("Done with squareInTheMiddleOfSpainTest. Total time: " + humanReadableFormat(Duration.between(start, Instant.now())) + " Average time: " + humanReadableFormat(Duration.between(start,Instant.now()).dividedBy(20l)));

        start = Instant.now();
        for (int i = 0; i < 20; i++){
            fishOverIrelandTest();
        }
        System.out.println("Done with fishOverIrelandTest. Total time: " + humanReadableFormat(Duration.between(start, Instant.now())) + " Average time: " + humanReadableFormat(Duration.between(start,Instant.now()).dividedBy(20l)));

        start = Instant.now();
        for (int i = 0; i < 20; i++){
            pentagramOverIrelandTest();
        }
        System.out.println("Done with pentagramOverIrelandTest. Total time: " + humanReadableFormat(Duration.between(start, Instant.now())) + " Average time: " + humanReadableFormat(Duration.between(start,Instant.now()).dividedBy(20l)));

        start = Instant.now();
        for (int i = 0; i < 20; i++){
            twoTrianglesAroundIrelandTest();
        }
        System.out.println("Done with twoTrianglesAroundIrelandTest. Total time: " + humanReadableFormat(Duration.between(start, Instant.now())) + " Average time: " + humanReadableFormat(Duration.between(start,Instant.now()).dividedBy(20l)));

        System.out.println("All done. Total time: " + humanReadableFormat(Duration.between(b4, Instant.now())));
    }

    @Test
    @Ignore
    public void createRouteTestTitanic1000PositionsSync() throws Exception {
        AssetDTO testAsset = AssetTestHelper.createTestAsset();
        MobileTerminalDto mobileTerminal = MobileTerminalTestHelper.createMobileTerminal();
        MobileTerminalTestHelper.assignMobileTerminal(testAsset, mobileTerminal);
        List<LatLong> route = movementHelper.createRuttCobhNewYork(1000, 0.06f);                //0.1F = 654 pos    0.01 = 6543     0.07 = 934   0.06 = 1090

        MovementDto createMovementResponse = null;
        int i = 0;
        Instant b4 = Instant.now();
        Instant lastIteration = Instant.now();
        List<Duration> averageDurations = new ArrayList<>();

        for (LatLong position : route) {
            final IncomingMovement createMovementRequest = movementHelper.createIncomingMovement(testAsset, position);
            createMovementResponse = movementHelper.createMovement(createMovementRequest);
            assertNotNull(createMovementResponse);
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

        //WKT is taken from https://clydedacruz.github.io/openstreetmap-wkt-playground/
        //simple "square" around ireland
        String areaInWKT = "POLYGON((-19.6435546875 55.379110448010465,-18.9404296875 50.0641917366591,-5.493164062500001 49.525208341974405,-6.811523437499997 56.145549500679095,-19.6435546875 55.379110448010465))";

        buildAndSendQuery(areaInWKT);
    }

    @Test
    @Ignore
    public void createRouteTestTitanic1000PositionsReverseOrderSync() throws Exception {
        AssetDTO testAsset = AssetTestHelper.createTestAsset();
        MobileTerminalDto mobileTerminal = MobileTerminalTestHelper.createMobileTerminal();
        MobileTerminalTestHelper.assignMobileTerminal(testAsset, mobileTerminal);
        List<LatLong> route = movementHelper.createRuttCobhNewYork(1000, 0.06f);                //0.1F = 654 pos    0.01 = 6543     0.07 = 934   0.06 = 1090

        MovementDto createMovementResponse = null;
        int i = 0;
        Instant b4 = Instant.now();
        Instant lastIteration = Instant.now();
        List<Duration> averageDurations = new ArrayList<>();
        List<IncomingMovement> createMovementList = new ArrayList<>();

        for (LatLong position : route) {
            createMovementList.add(movementHelper.createIncomingMovement(testAsset, position));
        }
        Collections.reverse(createMovementList);
        for (IncomingMovement createMovementRequest : createMovementList){
            createMovementResponse = movementHelper.createMovement(createMovementRequest);
            assertNotNull(createMovementResponse);
            i++;
            if ((i % 10) == 0) {
                System.out.println("Created movement number: " + i + " Time so far: " + humanReadableFormat(Duration.between(b4, Instant.now()))+ " Time since last 10: " + humanReadableFormat(Duration.between(lastIteration, Instant.now())));   //on run: 4.30 ish
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
    public void createRouteTestTitanic1000PositionsAsync() throws Exception {

        Long l = new Long(50);
        l.intValue();

        BigInteger b = new BigInteger(System.currentTimeMillis()+ "");

        System.out.println(b.intValue());
        b.intValue();

        AtomicInteger ai = new AtomicInteger();
        ai.incrementAndGet();

        List<LatLong> route = movementHelper.createRuttCobhNewYork(1000, 0.06f);                //0.1F = 654 pos    0.01 = 6543     0.07 = 934   0.06 = 1090
        sendRouteToMovementOnXShipsAsync(1, route);
    }

    @Test
    @Ignore
    public void camelErrorHandlingTest() throws Exception {
        Message message = messageHelper.getMessageResponse("UVMSMovementEvent","");


        ExceptionType response = unMarshallErrorResponse(message);

        assertNotNull(response);
        assertEquals("Error when processing message in movement. All redeliveries exhausted: ", response.getFault());
    }

    @Test
    @Ignore
    public void createRouteTestTitanic1000PositionsAnd8ShipsAsync() throws Exception {

        List<LatLong> route = movementHelper.createRuttCobhNewYork(10, 0.06f);                //0.1F = 654 pos    0.01 = 6543     0.07 = 934   0.06 = 1090
        sendRouteToMovementOnXShipsAsync(8, route);
    }


    @Test
    @Ignore
    public void createRouteTestTitanic1000PositionsAndShipsAsync() throws Exception {
        List<LatLong> route = movementHelper.createRuttCobhNewYork(1000, 0.06f);                //0.1F = 654 pos    0.01 = 6543     0.07 = 934   0.06 = 1090
        sendRouteToMovementOnXShipsAsync(1000, route);
    }

    @Test
    @Ignore
    public void createRouteTestTitanic6000PositionsAnd8ShipsAsync() throws Exception {
        List<LatLong> route = movementHelper.createRuttCobhNewYork(6000, 0.01f);                //0.1F = 654 pos    0.01 = 6543     0.07 = 934   0.06 = 1090
        sendRouteToMovementOnXShipsAsync(8, route);
    }

    @Test
    @Ignore
    public void createRouteTestTitanic6000PositionsAnd8ShipsAsyncReverseOrder() throws Exception {
        List<LatLong> route = movementHelper.createRuttCobhNewYork(6000, 0.01f);                //0.1F = 654 pos    0.01 = 6543     0.07 = 934   0.06 = 1090
        Collections.reverse(route);
        sendRouteToMovementOnXShipsAsync(8, route);
    }

    @Test
    @Ignore
    public void createRouteTestTitanic6000PositionsAnd20ShipsAsync() throws Exception {
        List<LatLong> route = movementHelper.createRuttCobhNewYork(6000, 0.01f);                //0.1F = 654 pos    0.01 = 6543     0.07 = 934   0.06 = 1090
        sendRouteToMovementOnXShipsAsync(20, route);
    }

    @Test
    @Ignore
    public void createRouteTestTitanic6000PositionsAnd1100ShipsAsync() throws Exception {
        List<LatLong> route = movementHelper.createRuttCobhNewYork(6000, 0.01f);                //0.1F = 654 pos    0.01 = 6543     0.07 = 934   0.06 = 1090
        sendRouteToMovementOnXShipsAsync(1100, route);
    }

    @Test
    public void twoTrianglesAroundIrelandTest() throws Exception {

        //two triangles,base to base, over ireland
        String areaInWKT = "POLYGON((-17.622070312499996 53.22576843579023,-8.701171874999996 48.86471476180279,-3.3837890624999916 54.05938788662357,-10.502929687499998 57.040729838360875,-5.9326171874999885 51.61801654877371,-17.622070312499996 53.22576843579023))";
        buildAndSendQuery(areaInWKT);
    }

    @Test
    public void multipointPolygonInAVeryRoughCircleTest() throws Exception {
        //many points in a rough circle around ireland
        String areaInWKT = "POLYGON((-11.953124999999998 55.15376626853558,-12.612304687499998 54.5975278521139,-12.700195312499996 54.007768761934784,-12.612304687499998 53.409531853086435,-12.524414062499993 52.69636107827449,-12.128906249999995 52.375599176659136,-11.777343749999996 51.8086147519852,-11.0302734375 51.15178610143039,-10.810546874999998 50.56928286558244,-9.53613281249999 50.45750402042057,-7.910156250000001 50.62507306341436,-6.943359374999995 51.0690166596039,-5.9326171874999885 51.59072264312016,-5.449218750000001 52.40241887397332,-4.965820312499998 53.38332836757158,-5.185546874999992 53.82659674299413,-5.449218750000001 54.470037612805754,-5.712890624999994 55.103516058019665,-6.503906249999989 55.37911044801049,-7.2070312500000036 55.75184939173528,-7.910156250000001 56.19448087726974,-8.876953124999991 56.84897198026974,-9.711914062500002 56.559482483762224,-9.931640624999996 55.973798205076605,-10.107421875 55.47885346331037,-10.458984374999998 55.05320258537114,-10.986328124999993 54.85131525968609,-11.953124999999998 55.15376626853558))";

        buildAndSendQuery(areaInWKT);
    }

    @Test
    public void triangleAroundSouthernIrelandTest () throws Exception {
        //one triangle over the south of ireland
        String areaInWKT = "POLYGON((-12.963867187499998 55.05320258537114,-12.700195312499996 50.79204706440686,-3.823242187499996 50.736455137010665,-12.963867187499998 55.05320258537114))";

        buildAndSendQuery(areaInWKT);
    }

    @Test
    public void squareInTheMiddleOfSpainTest () throws Exception {
        //one big square over the middle of spain
        String areaInWKT = "POLYGON((-5.844726562499999 41.525029573238015,-0.37353515624999817 41.541477666790286,-1.0107421874999976 37.77071473849608,-7.316894531249996 38.08268954483802,-5.844726562499999 41.525029573238015))";

        buildAndSendQuery(areaInWKT);
    }

    @Test
    public void spikyThingOverIrelandTest () throws Exception {
        //this is one spiky polygon placed over southern ireland
        String areaInWKT = "POLYGON((-17.2265625 55.103516058019665,-17.314453124999996 50.54136296522162,-5.361328124999988 50.62507306341436,-5.361328124999988 55.578344672182055,-8.173828124999993 48.57478991092884,-9.448242187499993 55.2540770670727,-10.766601562499998 48.83579746243092,-11.909179687499993 55.02802211299252,-12.656249999999998 48.95136647094773,-13.886718749999998 54.80068486732233,-14.633789062499991 49.095452162534826,-15.249023437499995 54.699233528481386,-16.215820312499993 48.980216985374994,-20.083007812499996 54.033586335210856,-17.2265625 55.103516058019665))";

        buildAndSendQuery(areaInWKT);
    }

    @Test
    public void spikyBallOverIrelandTest () throws Exception {
        //this is one spiky "ball" placed over southern ireland
        String areaInWKT = "POLYGON((-14.853515624999996 54.59752785211387,-14.677734374999993 51.563412328675895,-10.019531249999995 50.14874640066279,-4.130859374999988 52.02545860348815,-4.2626953125 55.103516058019665,-9.711914062500002 56.84897198026974,-0.7910156249999951 53.173119202640635,-11.293945312499993 55.998380955359636,-1.3183593749999973 51.17934297928926,-12.128906249999995 55.42901345240739,-3.7353515624999982 49.894634395734215,-9.7998046875 56.365250136856076,-7.646484374999992 48.922499263758226,-5.009765624999997 56.292156685076435,-12.568359374999991 48.980216985374994,-1.186523437500001 54.952385690633605,-16.56738281249999 50.56928286558244,-0.2636718749999928 52.40241887397332,-17.314453124999996 53.64463782485652,-2.4609374999999996 50.54136296522162,-14.853515624999996 54.59752785211387))";

        buildAndSendQuery(areaInWKT);
    }

    @Test
    public void pentagramOverIrelandTest () throws Exception {
        //pentagram placed over ireland
        String areaInWKT = "POLYGON((-13.754882812499993 54.085173420886775,-3.6914062499999996 54.11094294272428,-13.666992187499993 50.09239321093878,-8.129882812499996 56.72862197314072,-5.229492187499991 50.205033264943324,-13.754882812499993 54.085173420886775))";

        buildAndSendQuery(areaInWKT);
    }

    @Test
    public void fishOverIrelandTest () throws Exception {
        //"fish" placed over ireland
        String areaInWKT = "POLYGON((-24.565429687500007 51.672555148396754,-20.7421875 52.776185688961704,-15.161132812500004 53.09402405506327,-11.250000000000002 52.509534770327264,-6.328125000000003 50.31740811261869,-6.547851562500004 53.06762664238738,-10.810546875000007 50.00773901463688,-14.853515625000004 49.32512199104002,-20.346679687500004 50.205033264943324,-23.466796875000004 50.81981826215653,-24.565429687500007 51.672555148396754))";

        buildAndSendQuery(areaInWKT);
    }

    @Test
    public void sphereOverIrelandTest () throws Exception {
        //sphere with missing parts over southern ireland
        String areaInWKT = "POLYGON((-9.887695312499996 52.48278022207825,-11.777343749999996 51.563412328675895,-12.260742187499998 49.75287993415023,-10.151367187500007 48.1367666796927,-7.031250000000009 48.341646172374595,-5.449218750000001 49.837982453084834,-5.581054687499998 51.699799849741936,-7.338867187499999 52.48278022207825,-10.722656250000007 52.935396658623205,-13.315429687500004 51.12421275782688,-13.403320312500002 49.26780455063752,-10.986328125000002 47.5172006978394,-6.416015625000009 47.5172006978394,-4.350585937499998 49.86631672953868,-4.306640625 52.02545860348815,-6.679687500000002 53.409531853086435,-2.592773437499996 50.56928286558244,-6.020507812500003 46.31658418182218,-11.821289062499995 46.73986059969266,-14.458007812499996 48.86471476180279,-14.765624999999998 51.34433866059925,-11.118164062499996 53.72271667491847,-9.887695312499996 52.48278022207825))";

        buildAndSendQuery(areaInWKT);
    }

    @Test
    public void hollowSphereOverIrelandTest () throws Exception {
        //hollow "sphere" over southern ireland
        String areaInWKT = "POLYGON((-10.283203125000004 52.321910885947716,-12.216796875 50.98609893339352,-11.865234374999995 49.582226044621706,-10.195312500000005 48.74894534343292,-7.382812499999999 48.661942846070076,-5.141601562499994 49.32512199104002,-4.2626953125 50.98609893339352,-5.6689453124999964 52.02545860348815,-7.514648437499995 52.69636107827449,-9.711914062500002 52.456009392640766,-7.4707031249999964 52.2143386082582,-5.888671875000006 51.59072264312016,-5.4052734375000036 50.68079714532166,-6.020507812500003 49.75287993415023,-7.426757812499998 49.239120832466966,-9.843749999999998 49.26780455063752,-11.0302734375 49.92293545449576,-11.162109374999996 50.93073802371819,-8.657226562499998 52.509534770327264,-10.283203125000004 52.321910885947716))";

        buildAndSendQuery(areaInWKT);
    }

    private void sendRouteToMovementOnXShipsAsync(int nrOfShips, List<LatLong> route) throws Exception {
        List<AssetDTO> assetList = new ArrayList<>();

        for (int i = 0; i < nrOfShips; i++) {
            //Code for if one wants an actual correct ship to be able to plot the positions on the map from frontend
            AssetDTO testAsset = AssetTestHelper.createTestAsset();
            MobileTerminalDto mobileTerminalType = MobileTerminalTestHelper.createMobileTerminal();
            MobileTerminalTestHelper.assignMobileTerminal(testAsset, mobileTerminalType);
            assetList.add(testAsset);
        }

        int i = 0;
        Instant b4 = Instant.now();
        Instant lastIteration = Instant.now();
        List<Duration> averageDurations = new ArrayList<>();

        List<String> movements = new ArrayList<>();
        try (SseEventSource source = MovementHelper.getSseStream()) {
            source.register((inboundSseEvent) -> {
                if (inboundSseEvent.getComment() != null && inboundSseEvent.getComment().equals("New Movement")) {
                    movements.add(inboundSseEvent.readData());
                    if ((movements.size() % 10) == 0) {
                        System.out.println("Received " + movements.size() + "/" + route.size());
                    }
                }
            });
            source.open();

            for (LatLong position : route) {
                AssetDTO testAsset = assetList.get((int)(Math.random()* (double)nrOfShips));
                final IncomingMovement createMovementRequest = movementHelper.createIncomingMovement(testAsset, position);
                movementHelper.createMovementDontWaitForResponse(testAsset, createMovementRequest);

                i++;
                if ((i % 10) == 0) {
                    System.out.println("Sent movement number: " + i + " Time so far: " + humanReadableFormat(Duration.between(b4, Instant.now())) + " Time since last 10: " + humanReadableFormat(Duration.between(lastIteration, Instant.now())));
                    averageDurations.add(Duration.between(lastIteration, Instant.now()));
                    lastIteration = Instant.now();

                    while (movements.size() < route.size()) {
                        Thread.sleep(100);
                    }
                }
            }
        }
    }

    private static Consumer<InboundSseEvent> onEvent = (inboundSseEvent) -> {
        String data = inboundSseEvent.readData();
        System.out.println(data);
    };

    //Error
    private static Consumer<Throwable> onError = (throwable) -> {
        throwable.printStackTrace();
    };

    //Connection close and there is nothing to receive
    private static Runnable onComplete = () -> {
        System.out.println("Done!");
    };

    private void buildAndSendQuery(String areaInWKT) throws Exception {
        Instant start = Instant.now();
        GetMovementListByQueryRequest input = new GetMovementListByQueryRequest();
        MovementQuery movementQuery = new MovementQuery();
        movementQuery.setExcludeFirstAndLastSegment(false);
        ListPagination page = new ListPagination();
        page.setListSize(BigInteger.valueOf(10000L));
        page.setPage(BigInteger.ONE);
        movementQuery.setPagination(page);
        ListCriteria listCriteria = new ListCriteria();
        listCriteria.setKey(SearchKey.AREA);
        listCriteria.setValue(areaInWKT);
        movementQuery.getMovementSearchCriteria().add(listCriteria);
        input.setQuery(movementQuery);
        input.setMethod(MovementModuleMethod.MOVEMENT_LIST);

        String inputString = marshall(input);

        Message output = messageHelper.getMessageResponse("UVMSMovementEvent",inputString);
        assertNotNull(output);

        GetMovementListByQueryResponse response = unMarshallCreateMovementBatchResponse(output);
        System.out.println("Amount of movement: " + response.getMovement().size());
        System.out.println("Time search query: " + humanReadableFormat(Duration.between(start, Instant.now()))); //last time 1.015S

    }

    private static String marshall(final GetMovementListByQueryRequest createMovementRequest) throws JAXBException {
        final StringWriter sw = new StringWriter();
        JAXBContext.newInstance(GetMovementListByQueryRequest.class).createMarshaller().marshal(createMovementRequest, sw);
        return sw.toString();
    }

    private GetMovementListByQueryResponse unMarshallCreateMovementBatchResponse(final Message response) throws Exception {
        TextMessage textMessage = (TextMessage) response;
        JAXBContext jaxbContext = JAXBContext.newInstance(GetMovementListByQueryResponse.class);
        return (GetMovementListByQueryResponse) jaxbContext.createUnmarshaller()
                .unmarshal(new StringReader(textMessage.getText()));
    }

    private List<LatLong> reorder(int order, List<LatLong> positions) {
        switch (order) {
            case 1:
                Collections.reverse(positions);
                break;
            case 2:
                Collections.shuffle(positions);
                break;
        }

        return positions;
    }

    private static String humanReadableFormat(Duration duration) {
        return duration.toString()
                .substring(2)
                .replaceAll("(\\d[HMS])(?!$)", "$1 ")
                .toLowerCase();
    }

    private ExceptionType unMarshallErrorResponse(final Message response) throws Exception {
        TextMessage textMessage = (TextMessage) response;
        JAXBContext jaxbContext = JAXBContext.newInstance(ExceptionType.class);
        return (ExceptionType) jaxbContext.createUnmarshaller()
                .unmarshal(new StringReader(textMessage.getText()));
    }
    public static SseEventSource getSseStream() {
        WebTarget target = getWebTarget().path("exchange/unsecured/rest/sse/subscribe");
        AuthorizationHeaderWebTarget jwtTarget = new AuthorizationHeaderWebTarget(target, getValidJwtToken());
        return SseEventSource.
                target(jwtTarget).build();
    }
}
