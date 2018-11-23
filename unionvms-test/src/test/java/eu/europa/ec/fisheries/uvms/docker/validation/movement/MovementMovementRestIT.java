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
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import eu.europa.ec.fisheries.uvms.movement.model.util.DateUtil;
import org.hamcrest.CoreMatchers;
import org.junit.Test;
import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.MobileTerminalType;
import eu.europa.ec.fisheries.schema.movement.module.v1.CreateMovementRequest;
import eu.europa.ec.fisheries.schema.movement.module.v1.CreateMovementResponse;
import eu.europa.ec.fisheries.schema.movement.search.v1.ListCriteria;
import eu.europa.ec.fisheries.schema.movement.search.v1.ListPagination;
import eu.europa.ec.fisheries.schema.movement.search.v1.MovementAreaAndTimeIntervalCriteria;
import eu.europa.ec.fisheries.schema.movement.search.v1.MovementQuery;
import eu.europa.ec.fisheries.schema.movement.search.v1.SearchKey;
import eu.europa.ec.fisheries.schema.movement.source.v1.GetMovementListByAreaAndTimeIntervalResponse;
import eu.europa.ec.fisheries.schema.movement.v1.MovementType;
import eu.europa.ec.fisheries.uvms.asset.client.model.AssetDTO;
import eu.europa.ec.fisheries.uvms.commons.rest.dto.ResponseDto;
import eu.europa.ec.fisheries.uvms.docker.validation.asset.AssetTestHelper;
import eu.europa.ec.fisheries.uvms.docker.validation.common.AbstractRest;
import eu.europa.ec.fisheries.uvms.docker.validation.mobileterminal.MobileTerminalTestHelper;

/**
 * The Class MovementMovementRestIT.
 */


public class MovementMovementRestIT extends AbstractRest {
	
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
	    List<MovementType> dataMap = MovementHelper.getListByQuery(createMovementQuery());
		assertNotNull(dataMap);
	}

	//Two tests (and an unused helper function) removed since they assumed that some specific data where already in the database. Since the DB is empty, the test would never pass.

	
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
		listCriteria.setValue(UUID.randomUUID().toString());
		movementQuery.getMovementSearchCriteria().add(listCriteria);

		return movementQuery;
	}

	/**
	 * Gets the minimal list by query test.
	 *
	 * @return the minimal list by query test
	 * @throws Exception the exception
	 */
	@Test
	public void getMinimalListByQueryTest() throws Exception {
		List<MovementType> response = MovementHelper.getMinimalListByQuery(createMovementQuery());
		assertThat(response, CoreMatchers.is(CoreMatchers.notNullValue()));
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


		AssetDTO testAsset = AssetTestHelper.createTestAsset();
		MobileTerminalType mobileTerminalType = MobileTerminalTestHelper.createMobileTerminalType();
		MobileTerminalTestHelper.assignMobileTerminal(testAsset, mobileTerminalType);
		LatLong latLong = new LatLong(16.9, 32.6333333, new Date(System.currentTimeMillis()));
		CreateMovementRequest createMovementRequest = movementHelper.createMovementRequest(testAsset, latLong);
		CreateMovementResponse createMovementResponse = movementHelper.createMovement(createMovementRequest);

		List<String> connectIds = new ArrayList<>();
		
		assertNotNull(createMovementResponse);
		assertNotNull(createMovementResponse.getMovement());
		assertNotNull(createMovementResponse.getMovement().getConnectId());	
		
		String connectId = createMovementResponse.getMovement().getConnectId();
		
		connectIds.add(connectId);
		
		List<MovementDto> latestMovements = MovementHelper.getLatestMovements(connectIds);
		assertTrue(latestMovements.size() > 0);
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
		AssetDTO testAsset = AssetTestHelper.createTestAsset();
		MobileTerminalType mobileTerminalType = MobileTerminalTestHelper.createMobileTerminalType();
		MobileTerminalTestHelper.assignMobileTerminal(testAsset, mobileTerminalType);
		
		LatLong latLong = new LatLong(16.9, 32.6333333, new Date(System.currentTimeMillis()));
		CreateMovementRequest createMovementRequest = movementHelper.createMovementRequest(testAsset, latLong);
		CreateMovementResponse createMovementResponse = movementHelper.createMovement(createMovementRequest);

		List<MovementDto> latestMovements = MovementHelper.getLatestMovements(100);
		assertTrue(latestMovements.size() > 0);
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

		AssetDTO testAsset = AssetTestHelper.createTestAsset();
		MobileTerminalType mobileTerminalType = MobileTerminalTestHelper.createMobileTerminalType();
		MobileTerminalTestHelper.assignMobileTerminal(testAsset, mobileTerminalType);
		
		LatLong latLong = movementHelper.createRutt(1).get(0);

		CreateMovementRequest createMovementRequest = movementHelper.createMovementRequest(testAsset, latLong);
		CreateMovementResponse createMovementResponse = movementHelper.createMovement(createMovementRequest);
		
		assertNotNull(createMovementResponse);
		assertNotNull(createMovementResponse.getMovement());
		assertNotNull(createMovementResponse.getMovement().getGuid());
		String id = createMovementResponse.getMovement().getGuid();

		MovementType movementById = MovementHelper.getMovementById(id);
		assertNotNull(movementById);
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
		ResponseDto<GetMovementListByAreaAndTimeIntervalResponse> response = getWebTarget()
		        .path("movement/rest/movement/listByAreaAndTimeInterval")
		        .request(MediaType.APPLICATION_JSON)
		        .header(HttpHeaders.AUTHORIZATION, getValidJwtToken())
		        .post(Entity.json(new MovementAreaAndTimeIntervalCriteria()), new GenericType<ResponseDto<GetMovementListByAreaAndTimeIntervalResponse>>() {});
		
		assertNotNull(response);
	}

	@Test
    public void countMovementsForAsset() throws Exception{
	    String asset = "4f87e873-214c-4ebd-b161-5a934904f4fc";
        Instant now = Instant.now();

        long response = getWebTarget()
                .path("movement/rest/internal/countMovementsInTheLastDayForAsset/" + asset)
                .queryParam("after", DateUtil.parseUTCDateToString(now))    //yyyy-MM-dd HH:mm:ss Z
                .request(MediaType.APPLICATION_JSON)
                //.header(HttpHeaders.AUTHORIZATION, getValidJwtToken())
                .get(long.class);

        assertNotNull(response);
        System.out.println(response);

    }

}
