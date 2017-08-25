package eu.europa.ec.fisheries.uvms.docker.validation.movement;

import java.io.StringWriter;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.json.JsonArray;

import org.json.simple.JSONArray;
import org.junit.Test;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.MobileTerminalType;
import eu.europa.ec.fisheries.schema.movement.module.v1.CreateMovementRequest;
import eu.europa.ec.fisheries.schema.movement.module.v1.CreateMovementResponse;
import eu.europa.ec.fisheries.schema.movement.search.v1.ListCriteria;
import eu.europa.ec.fisheries.schema.movement.search.v1.ListPagination;
import eu.europa.ec.fisheries.schema.movement.search.v1.MovementQuery;
import eu.europa.ec.fisheries.schema.movement.search.v1.SearchKey;
import eu.europa.ec.fisheries.schema.movement.v1.MovementPoint;
import eu.europa.ec.fisheries.schema.movement.v1.MovementType;
import eu.europa.ec.fisheries.uvms.docker.validation.asset.AssetTestHelper;
import eu.europa.ec.fisheries.uvms.docker.validation.common.AbstractRestServiceTest;
import eu.europa.ec.fisheries.uvms.docker.validation.common.MessageHelper;
import eu.europa.ec.fisheries.uvms.docker.validation.mobileterminal.MobileTerminalTestHelper;
import eu.europa.ec.fisheries.wsdl.asset.types.Asset;

/**
 * The Class MovementJmsIT.
 */
public class MovementJmsIT extends AbstractRestServiceTest {

	private static MovementHelper movementHelper = new MovementHelper();

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
		List<LatLong> route = movementHelper.createRutt(14);

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
		List<LatLong> route = movementHelper.createRuttCobhNewYork(-1);

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

		int NUMBER_OF_POSITIONS = 10;

		Asset testAsset = AssetTestHelper.createTestAsset();
		MobileTerminalType mobileTerminalType = MobileTerminalTestHelper.createMobileTerminalType();
		MobileTerminalTestHelper.assignMobileTerminal(testAsset, mobileTerminalType);
		List<LatLong> route = movementHelper.createRuttVarbergGrena(NUMBER_OF_POSITIONS);

		// shake the content so it is not in a deterministic order
		List<LatLong> routeBeforeShake = new ArrayList<>(route);
		Collections.shuffle(route);

		List<MovementPoint> pointFromAPI = new ArrayList<>();
		for (LatLong position : route) {
			final CreateMovementRequest createMovementRequest = movementHelper.createMovementRequest(testAsset,
					mobileTerminalType, position);
			CreateMovementResponse createMovementResponse = movementHelper.createMovement(testAsset, mobileTerminalType,
					createMovementRequest);
			assertNotNull(createMovementResponse);
			assertNotNull(createMovementResponse.getMovement());
			assertNotNull(createMovementResponse.getMovement().getPosition());
			MovementPoint movementPoint = createMovementResponse.getMovement().getPosition();
			pointFromAPI.add(movementPoint);
		}


		MovementQuery movementQuery = new MovementQuery();
		movementQuery.setExcludeFirstAndLastSegment(false);

		ListPagination listPagination = new ListPagination();
		listPagination.setListSize(BigInteger.valueOf(NUMBER_OF_POSITIONS));
		listPagination.setPage(BigInteger.valueOf(1));
		movementQuery.setPagination(listPagination);

		ListCriteria listCriteria = new ListCriteria();

		// @formatter:off
		/*
		    MOVEMENT_ID,
		    SEGMENT_ID,
		    TRACK_ID,
		    CONNECT_ID,
		    MOVEMENT_TYPE,
		    ACTIVITY_TYPE,
		    DATE,
		    AREA,
		    AREA_ID,
		    STATUS,
		    SOURCE,
		    CATEGORY,
		    NR_OF_LATEST_REPORTS;
		*/
		// @formatter:on

		listCriteria.setKey(SearchKey.NR_OF_LATEST_REPORTS);
		listCriteria.setValue(String.valueOf(NUMBER_OF_POSITIONS));

		movementQuery.getMovementSearchCriteria().add(listCriteria);

		Map<String, Object> ret = movementHelper.getMinimalListByQuery(movementQuery);
		List movements = (List) ret.get("movement");

		StringWriter stringWriter = new StringWriter();
		JSONArray ja = new JSONArray();
		ja.writeJSONString(movements, stringWriter);
		
		ObjectMapper mapper = new ObjectMapper();
		TypeReference<List<MovementType>> tr = new TypeReference<List<MovementType>>(){};
		ArrayList<MovementType> movementTypes = mapper.readValue(stringWriter.toString(), tr);
		
		
		
		for(int i = 0 ; i < NUMBER_OF_POSITIONS ; i++){
			
			MovementPoint movementPointFromAPI = pointFromAPI.get(i);
			
			LatLong fromAPI = new LatLong(movementPointFromAPI.getLatitude(), movementPointFromAPI.getLongitude(), null);
			
			LatLong before = routeBeforeShake.get(i);
			
			MovementType movementType = movementTypes.get(i);
			MovementPoint movementPoint = movementType.getPosition();
			LatLong retrieved = new LatLong(movementPoint.getLatitude(), movementPoint.getLongitude(), movementType.getPositionTime());

			
			System.out.println("-------------------------------------------------------------------------------------------------------------------------");
			System.out.println("Before = in order           " + before);
			System.out.println("After  = unordered          " + fromAPI);
			System.out.println("Retr   = should be in order " + retrieved);
			
			
			
		}
		
		System.out.println("");
	


		// jämför och se om den är som routeBeforeShake

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
