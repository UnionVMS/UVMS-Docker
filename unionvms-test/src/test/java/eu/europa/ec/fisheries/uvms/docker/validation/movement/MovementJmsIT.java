package eu.europa.ec.fisheries.uvms.docker.validation.movement;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import eu.europa.ec.fisheries.schema.movement.common.v1.SimpleResponse;
import org.junit.Assert;
import org.junit.Test;

import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.MobileTerminalType;
import eu.europa.ec.fisheries.schema.movement.module.v1.CreateMovementBatchRequest;
import eu.europa.ec.fisheries.schema.movement.module.v1.CreateMovementBatchResponse;
import eu.europa.ec.fisheries.schema.movement.module.v1.CreateMovementRequest;
import eu.europa.ec.fisheries.schema.movement.module.v1.CreateMovementResponse;
import eu.europa.ec.fisheries.uvms.docker.validation.asset.AssetTestHelper;
import eu.europa.ec.fisheries.uvms.docker.validation.common.AbstractRestServiceTest;
import eu.europa.ec.fisheries.uvms.docker.validation.common.MessageHelper;
import eu.europa.ec.fisheries.uvms.docker.validation.mobileterminal.MobileTerminalTestHelper;
import eu.europa.ec.fisheries.wsdl.asset.types.Asset;

/**
 * The Class MovementJmsIT.
 */
public class MovementJmsIT extends AbstractRestServiceTest {
	
	public static  int ALL = -1;

	private static MovementHelper movementHelper = new MovementHelper();

	@Test
	public void createMovementBatchRequestTest() throws Exception {
		Asset testAsset = AssetTestHelper.createTestAsset();
		MobileTerminalType mobileTerminalType = MobileTerminalTestHelper.createMobileTerminalType();
		MobileTerminalTestHelper.assignMobileTerminal(testAsset, mobileTerminalType);
		List<LatLong> latLongList = movementHelper.createRuttCobhNewYork(50, 0.4f);

		final CreateMovementBatchRequest createMovementBatchRequest = movementHelper
				.createMovementBatchRequest(testAsset, mobileTerminalType, latLongList);
		CreateMovementBatchResponse createMovementResponse = movementHelper.createMovementBatch(testAsset,
				mobileTerminalType, createMovementBatchRequest);


		Assert.assertNotNull(createMovementResponse);
		SimpleResponse simpleResponse = createMovementResponse.getResponse();
		String val = simpleResponse.value();
		Assert.assertEquals("OK", val);



	}

	/**
	 * Creates the movement request test.
	 *
	 * @throws Exception
	 *             the exception
	 */
	@Test(timeout = 10000)
	public void createMovementRequestTest() throws Exception {

		Asset testAsset = AssetTestHelper.createTestAsset();
		MobileTerminalType mobileTerminalType = MobileTerminalTestHelper.createMobileTerminalType();
		MobileTerminalTestHelper.assignMobileTerminal(testAsset, mobileTerminalType);

		LatLong latLong = movementHelper.createRutt(1).get(0);
		final CreateMovementRequest createMovementRequest = movementHelper.createMovementRequest(testAsset,
				mobileTerminalType, latLong);

		CreateMovementResponse createMovementResponse = movementHelper.createMovement(testAsset, mobileTerminalType,
				createMovementRequest);

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
		Asset testAsset = AssetTestHelper.createTestAsset();
		MobileTerminalType mobileTerminalType = MobileTerminalTestHelper.createMobileTerminalType();
		MobileTerminalTestHelper.assignMobileTerminal(testAsset, mobileTerminalType);
		List<LatLong> route = movementHelper.createRutt(24);

		for (LatLong position : route) {

			final CreateMovementRequest createMovementRequest = movementHelper.createMovementRequest(testAsset,
					mobileTerminalType, position);

			CreateMovementResponse createMovementResponse = movementHelper.createMovement(testAsset, mobileTerminalType,
					createMovementRequest);
			assertNotNull(createMovementResponse);

		}

	}

	@Test
	public void createRouteTestVarbergGrena() throws Exception {
		Asset testAsset = AssetTestHelper.createTestAsset();
		MobileTerminalType mobileTerminalType = MobileTerminalTestHelper.createMobileTerminalType();
		MobileTerminalTestHelper.assignMobileTerminal(testAsset, mobileTerminalType);
		List<LatLong> route = movementHelper.createRuttVarbergGrena(-1);

		for (LatLong position : route) {
			final CreateMovementRequest createMovementRequest = movementHelper.createMovementRequest(testAsset,
					mobileTerminalType, position);
			CreateMovementResponse createMovementResponse = movementHelper.createMovement(testAsset, mobileTerminalType,
					createMovementRequest);
			assertNotNull(createMovementResponse);
		}
	}

	@Test
	public void createRouteTestTitanic() throws Exception {
		Asset testAsset = AssetTestHelper.createTestAsset();
		MobileTerminalType mobileTerminalType = MobileTerminalTestHelper.createMobileTerminalType();
		MobileTerminalTestHelper.assignMobileTerminal(testAsset, mobileTerminalType);
		List<LatLong> route = movementHelper.createRuttCobhNewYork(100, 0.4f);

		for (LatLong position : route) {
			final CreateMovementRequest createMovementRequest = movementHelper.createMovementRequest(testAsset,
					mobileTerminalType, position);
			CreateMovementResponse createMovementResponse = movementHelper.createMovement(testAsset, mobileTerminalType,
					createMovementRequest);
			assertNotNull(createMovementResponse);
		}
	}

	@Test
	public void createRouteAddPositionsInRandomOrder() throws Exception {

		// currently there is no way to check if randomly added positions
		// actually is processed ok
		// so we say its ok just to add them for now . . .

		int NUMBER_OF_POSITIONS = 5;

		Asset testAsset = AssetTestHelper.createTestAsset();
		MobileTerminalType mobileTerminalType = MobileTerminalTestHelper.createMobileTerminalType();
		MobileTerminalTestHelper.assignMobileTerminal(testAsset, mobileTerminalType);
		List<LatLong> route = movementHelper.createRuttVarbergGrena(NUMBER_OF_POSITIONS);

		// shake the content so it is not in a deterministic order
		List<LatLong> routeBeforeShake = new ArrayList<>(route);
		Collections.shuffle(route);

		List<CreateMovementResponse> fromAPI = new ArrayList<>();
		for (LatLong position : route) {
			final CreateMovementRequest createMovementRequest = movementHelper.createMovementRequest(testAsset,
					mobileTerminalType, position);
			CreateMovementResponse createMovementResponse = movementHelper.createMovement(testAsset, mobileTerminalType,
					createMovementRequest);
			assertNotNull(createMovementResponse);
			assertNotNull(createMovementResponse.getMovement());
			assertNotNull(createMovementResponse.getMovement().getPosition());
			fromAPI.add(createMovementResponse);
		}

	}
	
	
	@Test
	public void createSmallFishingTourFromVarberg() throws Exception {



		Asset testAsset = AssetTestHelper.createTestAsset();
		MobileTerminalType mobileTerminalType = MobileTerminalTestHelper.createMobileTerminalType();
		MobileTerminalTestHelper.assignMobileTerminal(testAsset, mobileTerminalType);
		List<LatLong> route = movementHelper.createSmallFishingTourFromVarberg();


		List<CreateMovementResponse> fromAPI = new ArrayList<>();
		for (LatLong position : route) {
			final CreateMovementRequest createMovementRequest = movementHelper.createMovementRequest(testAsset,
					mobileTerminalType, position);
			CreateMovementResponse createMovementResponse = movementHelper.createMovement(testAsset, mobileTerminalType,
					createMovementRequest);
			assertNotNull(createMovementResponse);
			assertNotNull(createMovementResponse.getMovement());
			assertNotNull(createMovementResponse.getMovement().getPosition());
			fromAPI.add(createMovementResponse);
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
		assertFalse(MessageHelper.checkQueueHasElements("UVMSMovementEvent"));
	}

}
