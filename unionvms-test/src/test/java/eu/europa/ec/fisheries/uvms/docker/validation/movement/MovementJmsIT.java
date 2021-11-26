package eu.europa.ec.fisheries.uvms.docker.validation.movement;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import eu.europa.ec.fisheries.schema.movement.search.v1.ListCriteria;
import eu.europa.ec.fisheries.schema.movement.search.v1.MovementQuery;
import eu.europa.ec.fisheries.schema.movement.search.v1.SearchKey;
import eu.europa.ec.fisheries.schema.movement.v1.MovementType;
import eu.europa.ec.fisheries.uvms.asset.client.model.AssetDTO;
import eu.europa.ec.fisheries.uvms.docker.validation.asset.AssetTestHelper;
import eu.europa.ec.fisheries.uvms.docker.validation.common.AbstractRest;
import eu.europa.ec.fisheries.uvms.docker.validation.common.MessageHelper;
import eu.europa.ec.fisheries.uvms.docker.validation.mobileterminal.MobileTerminalTestHelper;
import eu.europa.ec.fisheries.uvms.docker.validation.mobileterminal.dto.MobileTerminalDto;
import eu.europa.ec.fisheries.uvms.docker.validation.movement.model.IncomingMovement;
import eu.europa.ec.fisheries.uvms.movement.model.dto.MovementDto;
import org.hamcrest.CoreMatchers;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.jms.JMSException;
import javax.ws.rs.sse.SseEventSource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class MovementJmsIT extends AbstractRest {
	
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
		MobileTerminalDto mobileTerminal = MobileTerminalTestHelper.createMobileTerminal();
		MobileTerminalTestHelper.assignMobileTerminal(testAsset, mobileTerminal);
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
        criteria.setValue(testAsset.getId().toString());
        query.getMovementSearchCriteria().add(criteria);
		List<MovementType> movements = MovementHelper.getListByQuery(query);
		assertThat(movements.size(), CoreMatchers.is(numberPositions));
	}

	@Test(timeout = 10000)
	public void createMovementRequestTest() throws Exception {
		AssetDTO testAsset = AssetTestHelper.createTestAsset();
		MobileTerminalDto mobileTerminal = MobileTerminalTestHelper.createMobileTerminal();
		MobileTerminalTestHelper.assignMobileTerminal(testAsset, mobileTerminal);

		LatLong latLong = movementHelper.createRutt(1).get(0);
		IncomingMovement incomingMovement = movementHelper.createIncomingMovement(testAsset, latLong);

		MovementDto createMovementResponse = movementHelper.createMovement(incomingMovement);

		assertNotNull(createMovementResponse);
		assertEquals(createMovementResponse.getLocation().getLongitude(),
		        incomingMovement.getLongitude());
		assertEquals(createMovementResponse.getLocation().getLatitude(),
		        incomingMovement.getLatitude());

	}

	@Test(timeout = 720000)
	public void createRouteTest() throws Exception {
		AssetDTO testAsset = AssetTestHelper.createTestAsset();
		MobileTerminalDto mobileTerminal = MobileTerminalTestHelper.createMobileTerminal();
		MobileTerminalTestHelper.assignMobileTerminal(testAsset, mobileTerminal);
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
		MobileTerminalDto mobileTerminal = MobileTerminalTestHelper.createMobileTerminal();
		MobileTerminalTestHelper.assignMobileTerminal(testAsset, mobileTerminal);
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
		MobileTerminalDto mobileTerminal = MobileTerminalTestHelper.createMobileTerminal();
		MobileTerminalTestHelper.assignMobileTerminal(testAsset, mobileTerminal);
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
		MobileTerminalDto mobileTerminal = MobileTerminalTestHelper.createMobileTerminal();
		MobileTerminalTestHelper.assignMobileTerminal(testAsset, mobileTerminal);
		List<LatLong> route = movementHelper.createRuttVarbergGrena(NUMBER_OF_POSITIONS);

		// shake the content so it is not in a deterministic order
		List<LatLong> routeBeforeShake = new ArrayList<>(route);
		Collections.shuffle(route);

		assertMovementReqAndRes(testAsset, route);
	}

	@Test
	public void createSmallFishingTourFromVarberg() throws Exception {
		AssetDTO testAsset = AssetTestHelper.createTestAsset();
		MobileTerminalDto mobileTerminal = MobileTerminalTestHelper.createMobileTerminal();
		MobileTerminalDto assigned = MobileTerminalTestHelper.assignMobileTerminal(testAsset, mobileTerminal);
		assertNotNull(assigned);
		List<LatLong> route = movementHelper.createSmallFishingTourFromVarberg();

		assertMovementReqAndRes(testAsset, route);
	}

	private void assertMovementReqAndRes(AssetDTO testAsset, List<LatLong> route) throws Exception {
		for (LatLong position : route) {
		    IncomingMovement incomingMovement = movementHelper.createIncomingMovement(testAsset, position);
			MovementDto createMovementResponse = movementHelper.createMovement(incomingMovement);
			assertNotNull(createMovementResponse);
			assertNotNull(createMovementResponse.getLocation().getLatitude());
            assertNotNull(createMovementResponse.getLocation().getLongitude());
		}
	}

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
            messageHelper.sendMessageWithFunction("UVMSMovementEvent", writeValueAsString(
                    incomingMovement), "CREATE");
            
            while(movements.size() < 1) {
                Thread.sleep(100);
            }
        }
        assertThat(movements.size(), CoreMatchers.is(1));
    }
}
