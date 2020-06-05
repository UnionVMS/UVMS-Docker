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

import com.fasterxml.jackson.databind.ObjectMapper;
import eu.europa.ec.fisheries.schema.movement.search.v1.ListCriteria;
import eu.europa.ec.fisheries.schema.movement.search.v1.ListPagination;
import eu.europa.ec.fisheries.schema.movement.search.v1.MovementQuery;
import eu.europa.ec.fisheries.schema.movement.search.v1.SearchKey;
import eu.europa.ec.fisheries.schema.movement.v1.MovementType;
import eu.europa.ec.fisheries.uvms.asset.client.model.AssetDTO;
import eu.europa.ec.fisheries.uvms.commons.date.DateUtils;
import eu.europa.ec.fisheries.uvms.docker.validation.AppError;
import eu.europa.ec.fisheries.uvms.docker.validation.asset.AssetJMSHelper;
import eu.europa.ec.fisheries.uvms.docker.validation.asset.AssetTestHelper;
import eu.europa.ec.fisheries.uvms.docker.validation.common.AbstractRest;
import eu.europa.ec.fisheries.uvms.docker.validation.common.TopicListener;
import eu.europa.ec.fisheries.uvms.docker.validation.mobileterminal.MobileTerminalTestHelper;
import eu.europa.ec.fisheries.uvms.docker.validation.mobileterminal.dto.MobileTerminalDto;
import eu.europa.ec.fisheries.uvms.docker.validation.movement.model.IncomingMovement;
import org.hamcrest.CoreMatchers;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import javax.jms.JMSException;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.math.BigInteger;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class MovementMovementRestIT extends AbstractRest {

	private static MovementHelper movementHelper;
	private static AssetJMSHelper jmsHelper;

	@BeforeClass
	public static void setup() throws JMSException {
		movementHelper = new MovementHelper();
		jmsHelper = new AssetJMSHelper();
	}

	@AfterClass
	public static void cleanup() {
		movementHelper.close();
		jmsHelper.close();
	}

	@Test
	public void getListByQueryTest() {
	    List<MovementType> dataMap = MovementHelper.getListByQuery(createMovementQuery());
		assertNotNull(dataMap);
	}

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

	@Test
	public void getMinimalListByQueryTest() {
		List<MovementType> response = MovementHelper.getMinimalListByQuery(createMovementQuery());
		assertThat(response, CoreMatchers.is(CoreMatchers.notNullValue()));
	}

	@Test
	public void getLatestMovementsByConnectIdsTest() throws Exception {
		AssetDTO testAsset = AssetTestHelper.createTestAsset();
		MobileTerminalDto mobileTerminal = MobileTerminalTestHelper.createMobileTerminal();
		MobileTerminalTestHelper.assignMobileTerminal(testAsset, mobileTerminal);
		LatLong latLong = new LatLong(16.9, 32.6333333, new Date(System.currentTimeMillis()));
		IncomingMovement createMovementRequest = movementHelper.createIncomingMovement(testAsset, latLong);
		MovementDto createMovementResponse = movementHelper.createMovement(createMovementRequest);

		assertNotNull(createMovementResponse);
		assertNotNull(createMovementResponse.getConnectId());

		List<String> connectIds = new ArrayList<>();
		String connectId = createMovementResponse.getConnectId();
		connectIds.add(connectId);

		List<MovementDto> latestMovements = MovementHelper.getLatestMovements(connectIds);
		assertTrue(latestMovements.size() > 0);
	}

	@Test
	public void getLatestMovementsTest() throws Exception {
		AssetDTO testAsset = AssetTestHelper.createTestAsset();
		MobileTerminalDto mobileTerminal = MobileTerminalTestHelper.createMobileTerminal();
		MobileTerminalTestHelper.assignMobileTerminal(testAsset, mobileTerminal);

		LatLong latLong = new LatLong(16.9, 32.6333333, new Date(System.currentTimeMillis()));
		IncomingMovement createMovementRequest = movementHelper.createIncomingMovement(testAsset, latLong);
		movementHelper.createMovement(createMovementRequest);

		List<MovementDto> latestMovements = MovementHelper.getLatestMovements(100);
		assertTrue(latestMovements.size() > 0);
	}

	@Test
	public void getByIdTest() throws Exception {
		AssetDTO testAsset = AssetTestHelper.createTestAsset();
		MobileTerminalDto mobileTerminal = MobileTerminalTestHelper.createMobileTerminal();
		MobileTerminalTestHelper.assignMobileTerminal(testAsset, mobileTerminal);

		LatLong latLong = movementHelper.createRutt(1).get(0);

		IncomingMovement incomingMovement = movementHelper.createIncomingMovement(testAsset, latLong);
		MovementDto createMovementResponse = movementHelper.createMovement(incomingMovement);

		assertNotNull(createMovementResponse);
		assertNotNull(createMovementResponse.getMovementGUID());

		String id = createMovementResponse.getMovementGUID();
		MovementType movementById = MovementHelper.getMovementById(id);
		assertNotNull(movementById);
	}

	@Test
	@Ignore("Since internal resources are secured now, this test will fail without removing security from this endpoint.")
    public void countMovementsForAsset() {
	    String assetId = "4f87e873-214c-4ebd-b161-5a934904f4fc";

        Long response = getWebTarget()
                .path("movement/rest/internal/countMovementsInDateAndTheDayBeforeForAsset/" + assetId)
                .queryParam("after", DateUtils.dateToEpochMilliseconds(Instant.now()))    //yyyy-MM-dd HH:mm:ss Z
                .request(MediaType.APPLICATION_JSON)
                .get(Long.class);

        assertNotNull(response);
    }

    @Test
	public void mergeAssetsAndMoveMovementsTest() throws Exception{
		AssetDTO assetWithMMSI = AssetTestHelper.createBasicAsset();
		assetWithMMSI.setIrcs(null);
		assetWithMMSI.setCfr(null);
		assetWithMMSI = AssetTestHelper.createAsset(assetWithMMSI);
		AssetDTO assetWithIRCS = AssetTestHelper.createBasicAsset();
		assetWithIRCS.setMmsi(null);
		assetWithIRCS.setSource("NATIONAL");
		assetWithIRCS = AssetTestHelper.createAsset(assetWithIRCS);

		List<LatLong> latLongs = movementHelper.createRutt(10);
		List<MovementDto> input = new ArrayList<>(10);

		for (LatLong pos:latLongs) {
			IncomingMovement incomingMovement = movementHelper.createIncomingMovement(assetWithMMSI, pos);
			incomingMovement.setAssetMMSI(assetWithMMSI.getMmsi());
			input.add(movementHelper.createMovement(incomingMovement));
		}

		AssetDTO mergeAsset = AssetTestHelper.createBasicAsset();
		mergeAsset.setMmsi(assetWithMMSI.getMmsi());
		mergeAsset.setIrcs(assetWithIRCS.getIrcs());
		List<AssetDTO> assetDTOList = new ArrayList<>();
		assetDTOList.add(mergeAsset);
		ObjectMapper om = new ObjectMapper();
		String assetMessage = om.writeValueAsString(assetDTOList);

		jmsHelper.sendStringToAssetWithFunction(assetMessage, "ASSET_INFORMATION");
		Thread.sleep(5000);

		MovementQuery query = MovementHelper.getBasicMovementQuery();
		ListCriteria criteria = new ListCriteria();
		criteria.setKey(SearchKey.CONNECT_ID);
		criteria.setValue(assetWithIRCS.getId().toString());
		query.getMovementSearchCriteria().add(criteria);
		List<MovementType> output = MovementHelper.getListByQuery(query);

		assertEquals(assetWithIRCS.getId().toString(), input.size(), output.size());
		for (MovementDto move :input) {
			output.stream().anyMatch(o -> o.getGuid().equals(move.getMovementGUID()));
		}
	}

	@Test
	public void mergeAssetsAndCheckIfWeCanDeleteMCTest() throws Exception{
		AssetDTO assetWithMMSI = AssetTestHelper.createBasicAsset();
		assetWithMMSI.setIrcs(null);
		assetWithMMSI.setCfr(null);
		assetWithMMSI = AssetTestHelper.createAsset(assetWithMMSI);
		AssetDTO assetWithIRCS = AssetTestHelper.createBasicAsset();
		assetWithIRCS.setMmsi(null);
		assetWithIRCS.setSource("NATIONAL");
		assetWithIRCS = AssetTestHelper.createAsset(assetWithIRCS);

		List<LatLong> latLongs = movementHelper.createRutt(10);
		List<MovementDto> input = new ArrayList<>(10);

		for (LatLong pos:latLongs) {
			IncomingMovement incomingMovement = movementHelper.createIncomingMovement(assetWithMMSI, pos);
			incomingMovement.setAssetMMSI(assetWithMMSI.getMmsi());
			input.add(movementHelper.createMovement(incomingMovement));
		}

		AssetDTO mergeAsset = AssetTestHelper.createBasicAsset();
		mergeAsset.setMmsi(assetWithMMSI.getMmsi());
		mergeAsset.setIrcs(assetWithIRCS.getIrcs());
		List<AssetDTO> assetDTOList = new ArrayList<>();
		assetDTOList.add(mergeAsset);
		ObjectMapper om = new ObjectMapper();
		String assetMessage = om.writeValueAsString(assetDTOList);

		try (TopicListener listener = new TopicListener(TopicListener.EVENT_STREAM, "")) {
		    jmsHelper.sendStringToAssetWithFunction(assetMessage, "ASSET_INFORMATION");
		    listener.listenOnEventBus();
		}

		Response response = getWebTarget()
				.path("movement/rest/internal/removeMovementConnect")
				.queryParam("MovementConnectId", assetWithMMSI.getId().toString())
				.request(MediaType.APPLICATION_JSON)
				.header(HttpHeaders.AUTHORIZATION, getValidJwtToken())
				.delete(Response.class);
		assertEquals(200, response.getStatus());

	}

	@Test
	public void tryToDeleteMCWithMovementsStillConnectedTest() throws Exception{
		AssetDTO assetWithMMSI = AssetTestHelper.createBasicAsset();
		assetWithMMSI.setIrcs(null);
		assetWithMMSI.setCfr(null);
		assetWithMMSI = AssetTestHelper.createAsset(assetWithMMSI);

		List<LatLong> latLongs = movementHelper.createRutt(10);
		List<MovementDto> input = new ArrayList<>(10);

		for (LatLong pos:latLongs) {
			IncomingMovement incomingMovement = movementHelper.createIncomingMovement(assetWithMMSI, pos);
			incomingMovement.setAssetMMSI(assetWithMMSI.getMmsi());
			input.add(movementHelper.createMovement(incomingMovement));
		}

		Response response = getWebTarget()
				.path("movement/rest/internal/removeMovementConnect")
				.queryParam("MovementConnectId", assetWithMMSI.getId().toString())
				.request(MediaType.APPLICATION_JSON)
				.header(HttpHeaders.AUTHORIZATION, getValidJwtToken())
				.delete(Response.class);
		assertEquals(200, response.getStatus());
		AppError appError = response.readEntity(AppError.class);
		assertEquals(500, appError.code.intValue());
	}

	@Test
	public void tryToDeleteNonExistingMCTest() throws Exception{
		Response response = getWebTarget()
				.path("movement/rest/internal/removeMovementConnect")
				.queryParam("MovementConnectId", UUID.randomUUID().toString())
				.request(MediaType.APPLICATION_JSON)
				.header(HttpHeaders.AUTHORIZATION, getValidJwtToken())
				.delete(Response.class);
		assertEquals(200, response.getStatus());
	}
}
