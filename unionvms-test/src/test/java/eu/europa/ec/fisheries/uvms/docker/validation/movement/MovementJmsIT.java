package eu.europa.ec.fisheries.uvms.docker.validation.movement;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import javax.jms.JMSException;
import javax.ws.rs.sse.SseEventSource;
import org.hamcrest.CoreMatchers;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.MobileTerminalType;
import eu.europa.ec.fisheries.schema.movement.search.v1.ListCriteria;
import eu.europa.ec.fisheries.schema.movement.search.v1.MovementQuery;
import eu.europa.ec.fisheries.schema.movement.search.v1.SearchKey;
import eu.europa.ec.fisheries.schema.movement.v1.MovementType;
import eu.europa.ec.fisheries.uvms.asset.client.model.AssetDTO;
import eu.europa.ec.fisheries.uvms.docker.validation.asset.AssetTestHelper;
import eu.europa.ec.fisheries.uvms.docker.validation.common.AbstractRest;
import eu.europa.ec.fisheries.uvms.docker.validation.common.MessageHelper;
import eu.europa.ec.fisheries.uvms.docker.validation.mobileterminal.MobileTerminalTestHelper;
import eu.europa.ec.fisheries.uvms.docker.validation.movement.model.IncomingMovement;

/**
 * The Class MovementJmsIT.
 */
public class MovementJmsIT extends AbstractRest {
	
	public static  int ALL = -1;
	
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

	@Test(timeout = 20000)
	public void createMovementBatchRequestTest() throws Exception {
		AssetDTO testAsset = AssetTestHelper.createTestAsset();
		MobileTerminalType mobileTerminalType = MobileTerminalTestHelper.createMobileTerminalType();
		MobileTerminalTestHelper.assignMobileTerminal(testAsset, mobileTerminalType);
		int numberPositions = 50;
        List<LatLong> latLongList = movementHelper.createRuttCobhNewYork(numberPositions, 0.4f);

		List<IncomingMovement> createMovementBatchRequest = movementHelper.createMovementBatchRequest(testAsset, latLongList);
		List<String> responses = new ArrayList<>();
        try (SseEventSource source = MovementHelper.getSseStream()) {
            source.register((inboundSseEvent) -> {
                if (inboundSseEvent.getComment() != null && inboundSseEvent.getComment().equals("New Movement")) {
                    responses.add(inboundSseEvent.readData());
                }
            });
            source.open();
            movementHelper.createMovementBatch(createMovementBatchRequest);
            
            while(responses.size() < numberPositions) {
                Thread.sleep(100);
            }
        }
        assertThat(responses.size(), CoreMatchers.is(numberPositions));

		MovementQuery query = MovementHelper.getBasicMovementQuery();
        ListCriteria criteria = new ListCriteria();
        criteria.setKey(SearchKey.CONNECT_ID);
        criteria.setValue(testAsset.getHistoryId().toString());
        query.getMovementSearchCriteria().add(criteria);
		List<MovementType> movements = MovementHelper.getListByQuery(query);
		assertThat(movements.size(), CoreMatchers.is(numberPositions));
	}

	/**
	 * Creates the movement request test.
	 *
	 * @throws Exception
	 *             the exception
	 */
	@Test(timeout = 10000)
	public void createMovementRequestTest() throws Exception {

		AssetDTO testAsset = AssetTestHelper.createTestAsset();
		MobileTerminalType mobileTerminalType = MobileTerminalTestHelper.createMobileTerminalType();
		MobileTerminalTestHelper.assignMobileTerminal(testAsset, mobileTerminalType);

		LatLong latLong = movementHelper.createRutt(1).get(0);
		IncomingMovement incomingMovement = movementHelper.createIncomingMovement(testAsset, latLong);

		MovementDto createMovementResponse = movementHelper.createMovement(incomingMovement);

		assertNotNull(createMovementResponse);
		assertEquals(null, createMovementResponse.getCalculatedSpeed());
		assertEquals(createMovementResponse.getLongitude(),
		        incomingMovement.getLongitude());
		assertEquals(createMovementResponse.getLatitude(),
		        incomingMovement.getLatitude());
	}

	@Test(timeout = 720000)
	public void createRouteTest() throws Exception {
		AssetDTO testAsset = AssetTestHelper.createTestAsset();
		MobileTerminalType mobileTerminalType = MobileTerminalTestHelper.createMobileTerminalType();
		MobileTerminalTestHelper.assignMobileTerminal(testAsset, mobileTerminalType);
		List<LatLong> route = movementHelper.createRutt(24);

		for (LatLong position : route) {

		    IncomingMovement incomingMovement = movementHelper.createIncomingMovement(testAsset, position);

			MovementDto createMovementResponse = movementHelper.createMovement(incomingMovement);
			assertNotNull(createMovementResponse);

		}
	}

	@Test
	public void createRouteTestVarbergGrena() throws Exception {
		AssetDTO testAsset = AssetTestHelper.createTestAsset();
		MobileTerminalType mobileTerminalType = MobileTerminalTestHelper.createMobileTerminalType();
		MobileTerminalTestHelper.assignMobileTerminal(testAsset, mobileTerminalType);
		List<LatLong> route = movementHelper.createRuttVarbergGrena(-1);

		for (LatLong position : route) {
		    IncomingMovement incomingMovement = movementHelper.createIncomingMovement(testAsset, position);
			MovementDto createMovementResponse = movementHelper.createMovement(incomingMovement);
			assertNotNull(createMovementResponse);
		}
	}

	@Test
	public void createRouteTestTitanic() throws Exception {
		AssetDTO testAsset = AssetTestHelper.createTestAsset();
		MobileTerminalType mobileTerminalType = MobileTerminalTestHelper.createMobileTerminalType();
		MobileTerminalTestHelper.assignMobileTerminal(testAsset, mobileTerminalType);
		List<LatLong> route = movementHelper.createRuttCobhNewYork(100, 0.4f);

		for (LatLong position : route) {
			IncomingMovement incomingMovement = movementHelper.createIncomingMovement(testAsset, position);
			MovementDto createMovementResponse = movementHelper.createMovement(incomingMovement);
			assertNotNull(createMovementResponse);
		}
	}

	@Test
	public void createRouteAddPositionsInRandomOrder() throws Exception {

		// currently there is no way to check if randomly added positions
		// actually is processed ok
		// so we say its ok just to add them for now . . .

		int NUMBER_OF_POSITIONS = 5;

		AssetDTO testAsset = AssetTestHelper.createTestAsset();
		MobileTerminalType mobileTerminalType = MobileTerminalTestHelper.createMobileTerminalType();
		MobileTerminalTestHelper.assignMobileTerminal(testAsset, mobileTerminalType);
		List<LatLong> route = movementHelper.createRuttVarbergGrena(NUMBER_OF_POSITIONS);

		// shake the content so it is not in a deterministic order
		List<LatLong> routeBeforeShake = new ArrayList<>(route);
		Collections.shuffle(route);

		for (LatLong position : route) {
		    IncomingMovement incomingMovement = movementHelper.createIncomingMovement(testAsset, position);
			MovementDto createMovementResponse = movementHelper.createMovement(incomingMovement);
			assertNotNull(createMovementResponse);
			assertNotNull(createMovementResponse.getLatitude());
			assertNotNull(createMovementResponse.getLongitude());
		}

	}
	
	
	@Test
	public void createSmallFishingTourFromVarberg() throws Exception {

	    AssetDTO testAsset = AssetTestHelper.createTestAsset();
		MobileTerminalType mobileTerminalType = MobileTerminalTestHelper.createMobileTerminalType();
		MobileTerminalTestHelper.assignMobileTerminal(testAsset, mobileTerminalType);
		List<LatLong> route = movementHelper.createSmallFishingTourFromVarberg();

		for (LatLong position : route) {
		    IncomingMovement incomingMovement = movementHelper.createIncomingMovement(testAsset, position);
			MovementDto createMovementResponse = movementHelper.createMovement(incomingMovement);
			assertNotNull(createMovementResponse);
			assertNotNull(createMovementResponse.getLatitude());
            assertNotNull(createMovementResponse.getLongitude());
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
        assertFalse(messageHelper.checkQueueHasElements("UVMSMovementEvent"));
    }

    @Test(timeout = 10000)
    public void sseTest() throws Exception {
        AssetDTO testAsset = AssetTestHelper.createTestAsset();
        LatLong position = new LatLong(11d, 12d, new Date());
        IncomingMovement incomingMovement = movementHelper.createIncomingMovement(testAsset, position);

        List<String> movements = new ArrayList<>();
        try (SseEventSource source = MovementHelper.getSseStream()) {
            source.register((inboundSseEvent) -> {
                if (inboundSseEvent.getComment() != null && inboundSseEvent.getComment().equals("New Movement")) {
                    movements.add(inboundSseEvent.readData());
                }
            });
            source.open();
            messageHelper.sendMessageWithFunction("UVMSMovementEvent", OBJECT_MAPPER.writeValueAsString(
                    incomingMovement), "CREATE");
            
            while(movements.size() < 1) {
                Thread.sleep(100);
            }
        }
        
        assertThat(movements.size(), CoreMatchers.is(1));
    }
}
