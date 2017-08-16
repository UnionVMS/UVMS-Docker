package eu.europa.ec.fisheries.uvms.docker.validation.movement;

import java.util.Date;
import java.util.List;

import org.junit.Test;

import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.MobileTerminalType;
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

	private static  MovementHelper movementHelper = new MovementHelper();



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

		LatLong latLong = new LatLong(16.9, 32.6333333, new Date(System.currentTimeMillis()));
		final CreateMovementRequest createMovementRequest = movementHelper.createMovementRequest(testAsset,mobileTerminalType, latLong);

		CreateMovementResponse createMovementResponse = movementHelper.createMovement(testAsset, mobileTerminalType, createMovementRequest);

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
		List<LatLong> route = movementHelper.createRutt(14);
		//printCoordinates(route);

		String guid = mobileTerminalType.getMobileTerminalId().getGuid();

		for (LatLong position : route) {

			final CreateMovementRequest createMovementRequest = movementHelper.createMovementRequest(testAsset,
					position,guid);

			CreateMovementResponse createMovementResponse = movementHelper.createMovement(testAsset, mobileTerminalType, createMovementRequest);
			assertNotNull(createMovementResponse);

		}

	}

	
	private void printCoordinates(List<LatLong> route){	
		for(LatLong l : route){
			System.out.println(l.latitude+ ","+l.longitude);
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
