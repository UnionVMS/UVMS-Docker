/*

Developed with the contribution of the European Commission - Directorate General for Maritime Affairs and Fisheries
� European Union, 2017.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can
redistribute it and/or modify it under the terms of the GNU General Public License as published by the
Free Software Foundation, either version 3 of the License, or any later version. The IFDM Suite is distributed in
the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. You should have received a
copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

*/
package eu.europa.ec.fisheries.uvms.docker.validation.movement;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.HttpResponse;
import org.apache.http.client.fluent.Request;
import org.junit.Ignore;
import org.junit.Test;

import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.MobileTerminalType;
import eu.europa.ec.fisheries.schema.movement.module.v1.CreateMovementRequest;
import eu.europa.ec.fisheries.schema.movement.module.v1.CreateMovementResponse;
import eu.europa.ec.fisheries.schema.movement.search.v1.ListCriteria;
import eu.europa.ec.fisheries.schema.movement.search.v1.ListPagination;
import eu.europa.ec.fisheries.schema.movement.search.v1.MovementAreaAndTimeIntervalCriteria;
import eu.europa.ec.fisheries.schema.movement.search.v1.MovementQuery;
import eu.europa.ec.fisheries.schema.movement.search.v1.RangeCriteria;
import eu.europa.ec.fisheries.schema.movement.search.v1.RangeKeyType;
import eu.europa.ec.fisheries.schema.movement.search.v1.SearchKey;
import eu.europa.ec.fisheries.uvms.docker.validation.asset.AssetTestHelper;
import eu.europa.ec.fisheries.uvms.docker.validation.common.AbstractRestServiceTest;
import eu.europa.ec.fisheries.uvms.docker.validation.mobileterminal.MobileTerminalTestHelper;
import eu.europa.ec.fisheries.uvms.reporting.service.dto.MovementDTO;
import eu.europa.ec.fisheries.wsdl.asset.types.Asset;

/**
 * The Class MovementMovementRestIT.
 */

public class MovementMovementRestIT extends AbstractRestServiceTest {
	
	/** The movement helper. */
	private MovementHelper movementHelper = new MovementHelper();


	/**
	 * Gets the list by query test.
	 *
	 * @return the list by query test
	 * @throws Exception
	 *             the exception
	 */
	@Test
	public void getListByQueryTest() throws Exception {		
		Map<String, Object> dataMap = movementHelper.getListByQuery(createMovementQuery());
		assertNotNull(dataMap);
	}

	/**
	 * Gets the list by query number of latest report test.
	 *
	 * @return the list by query number of latest report test
	 * @throws Exception the exception
	 */
	@Test
	@Ignore
	public void getListByQueryNumberOfLatestReportTest() throws Exception {	
		int numberPositions = 4;
		Map<String, Object> dataMap = movementHelper.getListByQuery(createMovementQueryNumberOfLatestReports(numberPositions));
				
		validateNumberMovementPosisitionsPerShip(numberPositions, dataMap);		
	}

	/**
	 * Creates the movement query number of latest reports.
	 *
	 * @param numberPositions the number positions
	 * @return the movement query
	 */
	private MovementQuery createMovementQueryNumberOfLatestReports(int numberPositions) {

		MovementQuery movementQuery = new MovementQuery();
		movementQuery.setExcludeFirstAndLastSegment(false);
		ListPagination listPagination = new ListPagination();
		listPagination.setListSize(BigInteger.valueOf(1000000));
		listPagination.setPage(BigInteger.valueOf(1));
		movementQuery.setPagination(listPagination);
		
		
		ListCriteria listCriteria = new ListCriteria();
		listCriteria.setKey(SearchKey.NR_OF_LATEST_REPORTS);
		listCriteria.setValue("" + numberPositions);
		movementQuery.getMovementSearchCriteria().add(listCriteria);

		RangeCriteria rangeCriteria = new RangeCriteria();
		rangeCriteria.setKey(RangeKeyType.DATE);
		rangeCriteria.setFrom("2017-09-25 15:33:14 +0200");
		rangeCriteria.setTo("2017-10-09 15:33:14 +0200");
		movementQuery.getMovementRangeSearchCriteria().add(rangeCriteria);
		
		return movementQuery;
	}

	
	/**
	 * Creates the movement query.
	 *
	 * @return the movement query
	 */
	private MovementQuery createMovementQuery() {

		MovementQuery movementQuery = new MovementQuery();
		movementQuery.setExcludeFirstAndLastSegment(false);
		ListPagination listPagination = new ListPagination();
		listPagination.setListSize(BigInteger.valueOf(100));
		listPagination.setPage(BigInteger.valueOf(1));
		movementQuery.setPagination(listPagination);
		ListCriteria listCriteria = new ListCriteria();
		listCriteria.setKey(SearchKey.CONNECT_ID);
		listCriteria.setValue("Some connectId");
		movementQuery.getMovementSearchCriteria().add(listCriteria);

		return movementQuery;
	}

	/**
	 * Gets the minimal list by query test.
	 *
	 * @return the minimal list by query test
	 * @throws Exception
	 *             the exception
	 */
	@Test
	@Ignore
	public void getMinimalListByQueryNumberOfLatestReportTest() throws Exception {
		int numberPositions = 4;
		final HttpResponse response = Request.Post(getBaseUrl() + "movement/rest/movement/list/minimal")
				.setHeader("Content-Type", "application/json").setHeader("Authorization", getValidJwtToken())
				.bodyByteArray(writeValueAsString(createMovementQueryNumberOfLatestReports(numberPositions)).getBytes()).execute().returnResponse();

		Map<String, Object> dataMap = checkSuccessResponseReturnMap(response);
					
		validateNumberMovementPosisitionsPerShip(numberPositions, dataMap);		

	}

	/**
	 * Validate number movement posisitions per ship.
	 *
	 * @param numberPositions the number positions
	 * @param dataMap the data map
	 */
	private void validateNumberMovementPosisitionsPerShip(int numberPositions, Map<String, Object> dataMap) {
		List<Map<String,Object>> movementDataMap = (List<Map<String,Object>>) dataMap.get("movement");
		assertNotNull(movementDataMap);
		
		Map<String,Integer> positionsPerShip = new HashMap<>();
		
		for (Map<String, Object> map : movementDataMap) {
			String connectId = (String) map.get("connectId");			
			if (positionsPerShip.get(connectId) == null) {
				positionsPerShip.put(connectId, 1);
			} else {
				positionsPerShip.put(connectId, 1 +positionsPerShip.get(connectId));
			}
		}

		for (Entry<String, Integer> map : positionsPerShip.entrySet()) {
			assertEquals("Ship do not contain 4 positions:" + map.getKey(),new Integer(numberPositions),map.getValue());
		}
	}

	/**
	 * Gets the minimal list by query test.
	 *
	 * @return the minimal list by query test
	 * @throws Exception the exception
	 */
	@Test
	public void getMinimalListByQueryTest() throws Exception {
		final HttpResponse response = Request.Post(getBaseUrl() + "movement/rest/movement/list/minimal")
				.setHeader("Content-Type", "application/json").setHeader("Authorization", getValidJwtToken())
				.bodyByteArray(writeValueAsString(createMovementQuery()).getBytes()).execute().returnResponse();

		Map<String, Object> dataMap = checkSuccessResponseReturnMap(response);
		assertNotNull(dataMap);
	}

	
	/**
	 * Gets the latest movements by connect ids test.
	 *
	 * @return the latest movements by connect ids test
	 * @throws Exception
	 *             the exception
	 */
	@Test
	public void getLatestMovementsByConnectIdsTest() throws Exception {
		
		
		Asset testAsset = AssetTestHelper.createTestAsset();
		MobileTerminalType mobileTerminalType = MobileTerminalTestHelper.createMobileTerminalType();
		MobileTerminalTestHelper.assignMobileTerminal(testAsset, mobileTerminalType);
		LatLong latLong = new LatLong(16.9, 32.6333333, new Date(System.currentTimeMillis()));
		CreateMovementRequest createMovementRequest = movementHelper.createMovementRequest(testAsset, mobileTerminalType, latLong);		
		CreateMovementResponse createMovementResponse = movementHelper.createMovement(testAsset, mobileTerminalType, createMovementRequest);

		List<String> connectIds = new ArrayList<>();
		
		assertNotNull(createMovementResponse);
		assertNotNull(createMovementResponse.getMovement());
		assertNotNull(createMovementResponse.getMovement().getConnectId());	
		
		String connectId = createMovementResponse.getMovement().getConnectId();
		
		connectIds.add(connectId);
		
		// give it some time to execute before retrieving
		Thread.sleep(10000);
		
		
		final HttpResponse response = Request.Post(getBaseUrl() + "movement/rest/movement/latest")
				.setHeader("Content-Type", "application/json").setHeader("Authorization", getValidJwtToken())
				.bodyByteArray(writeValueAsString(connectIds).getBytes()).execute().returnResponse();

		List<MovementDTO> dataList = checkSuccessResponseReturnType(response, ArrayList.class);
		
		assertTrue(dataList.size() > 0);
	}

	/**
	 * Gets the latest movements test.
	 *
	 * @return the latest movements test
	 * @throws Exception
	 *             the exception
	 */
	@Test
	public void getLatestMovementsTest() throws Exception {
		
		
		Asset testAsset = AssetTestHelper.createTestAsset();
		MobileTerminalType mobileTerminalType = MobileTerminalTestHelper.createMobileTerminalType();
		Map<String, Object> assignedMap = MobileTerminalTestHelper.assignMobileTerminal(testAsset, mobileTerminalType);
		
		LatLong latLong = new LatLong(16.9, 32.6333333, new Date(System.currentTimeMillis()));
		CreateMovementRequest createMovementRequest = movementHelper.createMovementRequest(testAsset, mobileTerminalType, latLong);		
		CreateMovementResponse createMovementResponse = movementHelper.createMovement(testAsset, mobileTerminalType, createMovementRequest);
		
		final HttpResponse response = Request.Get(getBaseUrl() + "movement/rest/movement/latest/100")
				.setHeader("Content-Type", "application/json").setHeader("Authorization", getValidJwtToken()).execute()
				.returnResponse();

		List dataList = checkSuccessResponseReturnType(response, List.class);
		assertTrue(dataList.size() > 0);
	}

	/**
	 * Gets the by id test.
	 *
	 * @return the by id test
	 * @throws Exception
	 *             the exception
	 */
	@Test
	public void getByIdTest() throws Exception {
		
		Asset testAsset = AssetTestHelper.createTestAsset();
		MobileTerminalType mobileTerminalType = MobileTerminalTestHelper.createMobileTerminalType();
		MobileTerminalTestHelper.assignMobileTerminal(testAsset, mobileTerminalType);
		
		LatLong latLong = movementHelper.createRutt(1).get(0);

		CreateMovementRequest createMovementRequest = movementHelper.createMovementRequest(testAsset, mobileTerminalType, latLong);		
		CreateMovementResponse createMovementResponse = movementHelper.createMovement(testAsset, mobileTerminalType, createMovementRequest);
		
		assertNotNull(createMovementResponse);
		assertNotNull(createMovementResponse.getMovement());
		assertNotNull(createMovementResponse.getMovement().getGuid());
		String id = createMovementResponse.getMovement().getGuid();

		// give it some time to execute before retrieving
		Thread.sleep(10000);
		
		final HttpResponse response = Request.Get(getBaseUrl() + "movement/rest/movement/" + id)
				.setHeader("Content-Type", "application/json").setHeader("Authorization", getValidJwtToken()).execute()
				.returnResponse();

		Map<String, Object> map = checkSuccessResponseReturnMap(response);
		assertNotNull(map);
	}

	/**
	 * Gets the list movement by area and time interval test.
	 *
	 * @return the list movement by area and time interval test
	 * @throws Exception
	 *             the exception
	 */
	@Test
	public void getListMovementByAreaAndTimeIntervalTest() throws Exception {
		final HttpResponse response = Request.Post(getBaseUrl() + "movement/rest/movement/listByAreaAndTimeInterval")
				.setHeader("Content-Type", "application/json").setHeader("Authorization", getValidJwtToken())
				.bodyByteArray(writeValueAsString(new MovementAreaAndTimeIntervalCriteria()).getBytes()).execute()
				.returnResponse();

		Map<String, Object> dataMap = checkSuccessResponseReturnMap(response);
		assertNotNull(dataMap);
	}

}
