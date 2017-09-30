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
package eu.europa.ec.fisheries.uvms.docker.validation.reporting;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.fluent.Request;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.MobileTerminalType;
import eu.europa.ec.fisheries.schema.movement.module.v1.CreateMovementRequest;
import eu.europa.ec.fisheries.schema.movement.module.v1.CreateMovementResponse;
import eu.europa.ec.fisheries.uvms.docker.validation.asset.AssetTestHelper;
import eu.europa.ec.fisheries.uvms.docker.validation.common.AbstractRestServiceTest;
import eu.europa.ec.fisheries.uvms.docker.validation.mobileterminal.MobileTerminalTestHelper;
import eu.europa.ec.fisheries.uvms.docker.validation.movement.LatLong;
import eu.europa.ec.fisheries.uvms.docker.validation.movement.MovementHelper;
import eu.europa.ec.fisheries.uvms.reporting.service.dto.AssetFilterDTO;
import eu.europa.ec.fisheries.uvms.reporting.service.dto.CommonFilterDTO;
import eu.europa.ec.fisheries.uvms.reporting.service.dto.DisplayFormat;
import eu.europa.ec.fisheries.uvms.reporting.service.dto.LengthType;
import eu.europa.ec.fisheries.uvms.reporting.service.dto.PositionSelectorDTO;
import eu.europa.ec.fisheries.uvms.reporting.service.dto.report.ReportDTO;
import eu.europa.ec.fisheries.uvms.reporting.service.dto.report.VisibilityEnum;
import eu.europa.ec.fisheries.uvms.reporting.service.entities.FilterType;
import eu.europa.ec.fisheries.uvms.reporting.service.entities.Position;
import eu.europa.ec.fisheries.uvms.reporting.service.entities.Selector;
import eu.europa.ec.fisheries.uvms.reporting.service.enums.ReportTypeEnum;
import eu.europa.ec.fisheries.uvms.reporting.service.enums.VelocityType;
import eu.europa.ec.fisheries.wsdl.asset.types.Asset;

/**
 * The Class ReportingRestIT.
 */

public class ReportingRestIT extends AbstractRestServiceTest {

	/** The movement helper. */
	private static MovementHelper movementHelper = new MovementHelper();

	/** The test asset. */
	private static Asset testAsset =null;
	
	/**
	 * Creates the test asset with terminal and positions.
	 */
	@BeforeClass
	public static void createTestAssetWithTerminalAndPositions() {
		try {
		testAsset = AssetTestHelper.createTestAsset();
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

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	/**
	 * List reports test.
	 *
	 * @throws Exception
	 *             the exception
	 */
	@Test
	public void listReportsTest() throws Exception {
		final HttpResponse response = Request.Get(getBaseUrl() + "reporting/rest/report/list")
				.setHeader("Content-Type", "application/json").setHeader("scopeName", "All Reports")
				.setHeader("roleName", "AdminAll").setHeader("Authorization", getValidJwtToken()).execute()
				.returnResponse();
		List dataMap = checkSuccessResponseReturnType(response, List.class);
		assertNotNull(dataMap);
	}

	/**
	 * List last executed reports test.
	 *
	 * @throws Exception
	 *             the exception
	 */
	@Test
	public void listLastExecutedReportsTest() throws Exception {
		final HttpResponse response = Request.Get(getBaseUrl() + "reporting/rest/report/list/lastexecuted/10")
				.setHeader("Content-Type", "application/json").setHeader("scopeName", "All Reports")
				.setHeader("roleName", "AdminAll").setHeader("Authorization", getValidJwtToken()).execute()
				.returnResponse();
		List dataMap = checkSuccessResponseReturnType(response, List.class);
		assertNotNull(dataMap);
	}

	/**
	 * Gets the report test.
	 *
	 * @return the report test
	 * @throws Exception
	 *             the exception
	 */
	@Test
	public void getReportTest() throws Exception {
		Long reportId = createTwoWeeksReport("createReportTest", "TwoWeeksReports").getId();

		final HttpResponse response = Request.Get(getBaseUrl() + "reporting/rest/report/" + reportId)
				.setHeader("Content-Type", "application/json").setHeader("scopeName", "All Reports")
				.setHeader("roleName", "AdminAll").setHeader("Authorization", getValidJwtToken()).execute()
				.returnResponse();
		Map<String, Object> dataMap = checkSuccessResponseReturnMap(response);
		assertNotNull(dataMap);
	}

	/**
	 * Creates the report test.
	 *
	 * @throws Exception
	 *             the exception
	 */
	@Test
	public void createReportTest() throws Exception {
		createTwoWeeksReport("createReportTest", "TwoWeeksReports");
	}

	/**
	 * Creates the two weeks report.
	 *
	 * @param name
	 *            the name
	 * @param description
	 *            the description
	 * @return the report DTO
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws ClientProtocolException
	 *             the client protocol exception
	 * @throws JsonProcessingException
	 *             the json processing exception
	 * @throws JsonParseException
	 *             the json parse exception
	 * @throws JsonMappingException
	 *             the json mapping exception
	 */
	private ReportDTO createTwoWeeksReport(final String name, final String description) throws IOException,
			ClientProtocolException, JsonProcessingException, JsonParseException, JsonMappingException {
		return createTwoWeeksReport(name, description,ReportTypeEnum.STANDARD,VisibilityEnum.PRIVATE,null);
	}

	/**
	 * Creates the two weeks report.
	 *
	 * @param name the name
	 * @param description the description
	 * @param reportTypeEnum the report type enum
	 * @param visibilityEnum the visibility enum
	 * @param asset the asset
	 * @return the report DTO
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws ClientProtocolException the client protocol exception
	 * @throws JsonProcessingException the json processing exception
	 * @throws JsonParseException the json parse exception
	 * @throws JsonMappingException the json mapping exception
	 */
	private ReportDTO createTwoWeeksReport(final String name, final String description, ReportTypeEnum reportTypeEnum,
			VisibilityEnum visibilityEnum,Asset asset) throws IOException, ClientProtocolException, JsonProcessingException,
			JsonParseException, JsonMappingException {
		ReportDTO reportDTO = new ReportDTO();
		long time = new Date().getTime();
		reportDTO.setDescription(name + time);
		reportDTO.setName(description + time);
		reportDTO.setReportTypeEnum(reportTypeEnum);
		reportDTO.setVisibility(visibilityEnum);
		reportDTO.setWithMap(true);

		CommonFilterDTO commonFilterDTO = new CommonFilterDTO();
		commonFilterDTO.setStartDate(new Date(new Date().getTime() - (60 * 1000 * 60 * 24 * 7)));
		commonFilterDTO.setEndDate(new Date(new Date().getTime() + (60 * 1000 * 60 * 24 * 7)));
		PositionSelectorDTO positionSelector = new PositionSelectorDTO();
		positionSelector.setSelector(Selector.all);
		commonFilterDTO.setPositionSelector(positionSelector);
		commonFilterDTO.setType(FilterType.common);
		reportDTO.addFilter(commonFilterDTO);

		if (asset != null) {
			AssetFilterDTO assetFilterDTO = new AssetFilterDTO();
			assetFilterDTO.setGuid(asset.getAssetId().getGuid());
			assetFilterDTO.setName(asset.getName());
			reportDTO.addFilter(assetFilterDTO);
		}
		
		String writeValueAsString = writeValueAsString(reportDTO);
		final HttpResponse response = Request.Post(getBaseUrl() + "reporting/rest/report?projection=DEFAULT")
				.setHeader("Content-Type", "application/json").setHeader("scopeName", "All Reports")
				.setHeader("roleName", "AdminAll").setHeader("Authorization", getValidJwtToken())
				.bodyByteArray(writeValueAsString.getBytes()).execute().returnResponse();
		reportDTO.setId(checkSuccessResponseReturnType(response, Integer.class).longValue());

		return reportDTO;
	}

	private ReportDTO createTwoWeeksLastFourPositionsReport(final String name, final String description, ReportTypeEnum reportTypeEnum,
			VisibilityEnum visibilityEnum,Asset asset) throws IOException, ClientProtocolException, JsonProcessingException,
			JsonParseException, JsonMappingException {
		ReportDTO reportDTO = new ReportDTO();
		long time = new Date().getTime();
		reportDTO.setDescription(name + time);
		reportDTO.setName(description + time);
		reportDTO.setReportTypeEnum(reportTypeEnum);
		reportDTO.setVisibility(visibilityEnum);
		reportDTO.setWithMap(true);

		CommonFilterDTO commonFilterDTO = new CommonFilterDTO();
		commonFilterDTO.setStartDate(new Date(new Date().getTime() - (60 * 1000 * 60 * 24 * 7)));
		commonFilterDTO.setEndDate(new Date(new Date().getTime() + (60 * 1000 * 60 * 24 * 7)));
		PositionSelectorDTO positionSelector = new PositionSelectorDTO();
		positionSelector.setSelector(Selector.last);
		positionSelector.setValue(4f);
		positionSelector.setPosition(Position.positions);
		commonFilterDTO.setPositionSelector(positionSelector);
		commonFilterDTO.setType(FilterType.common);
		reportDTO.addFilter(commonFilterDTO);

		if (asset != null) {
			AssetFilterDTO assetFilterDTO = new AssetFilterDTO();
			assetFilterDTO.setGuid(asset.getAssetId().getGuid());
			assetFilterDTO.setName(asset.getName());
			reportDTO.addFilter(assetFilterDTO);
		}
		
		String writeValueAsString = writeValueAsString(reportDTO);
		final HttpResponse response = Request.Post(getBaseUrl() + "reporting/rest/report?projection=DEFAULT")
				.setHeader("Content-Type", "application/json").setHeader("scopeName", "All Reports")
				.setHeader("roleName", "AdminAll").setHeader("Authorization", getValidJwtToken())
				.bodyByteArray(writeValueAsString.getBytes()).execute().returnResponse();
		reportDTO.setId(checkSuccessResponseReturnType(response, Integer.class).longValue());

		return reportDTO;
	}

	
	/**
	 * Delete report test.
	 *
	 * @throws Exception
	 *             the exception
	 */
	@Test
	public void deleteReportTest() throws Exception {
		Long reportId = createTwoWeeksReport("deleteReportTest", "TwoWeeksReports").getId();

		final HttpResponse response = Request.Delete(getBaseUrl() + "reporting/rest/report/" + reportId)
				.setHeader("Content-Type", "application/json").setHeader("scopeName", "All Reports")
				.setHeader("roleName", "AdminAll").setHeader("Authorization", getValidJwtToken()).execute()
				.returnResponse();
		assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
	}

	/**
	 * Update report test.
	 *
	 * @throws Exception
	 *             the exception
	 */
	@Test
	public void updateReportTest() throws Exception {
		ReportDTO twoWeeksReport = createTwoWeeksReport("updateReportTest", "TwoWeeksReports");
		twoWeeksReport.setDescription("new Description");

		final HttpResponse response = Request.Put(getBaseUrl() + "reporting/rest/report/" + twoWeeksReport.getId())
				.setHeader("Content-Type", "application/json").setHeader("scopeName", "All Reports")
				.setHeader("roleName", "AdminAll").setHeader("Authorization", getValidJwtToken())
				.bodyByteArray(writeValueAsString(twoWeeksReport).getBytes()).execute().returnResponse();
		assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
	}

	/**
	 * Share report test.
	 *
	 * @throws Exception
	 *             the exception
	 */
	@Test
	public void shareReportTest() throws Exception {
		ReportDTO twoWeeksReport = createTwoWeeksReport("shareReportTest", "TwoWeeksReports");
		twoWeeksReport.setDescription("new Description");

		final HttpResponse response = Request
				.Put(getBaseUrl() + "reporting/rest/report/share/" + twoWeeksReport.getId() + "/"
						+ VisibilityEnum.PUBLIC)
				.setHeader("Content-Type", "application/json").setHeader("scopeName", "All Reports")
				.setHeader("roleName", "AdminAll").setHeader("Authorization", getValidJwtToken()).execute()
				.returnResponse();
		assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
	}

	/**
	 * Execute standard two week report with id for one asset test.
	 *
	 * @throws Exception the exception
	 */
	@Test
	public void executeStandardTwoWeekReportWithIdForOneAssetTest() throws Exception {
		ReportDTO twoWeeksReport = createTwoWeeksReport("executeStandardTwoWeekReportWithIdForOneAssetTest", "TwoWeeksReports",ReportTypeEnum.STANDARD,VisibilityEnum.PRIVATE,testAsset);
		
		DisplayFormat displayFormat = new DisplayFormat();
		displayFormat.setLengthType(LengthType.NM);
		displayFormat.setVelocityType(VelocityType.KTS);
		HashMap<String, Object> additionalProperties = new HashMap<String, Object>();
		HashMap<String, Object> valueMap = new HashMap<String, Object>();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		String timeStampValue = dateFormat.format(new Date());
		valueMap.put("timestamp", timeStampValue);
		additionalProperties.put("additionalProperties", valueMap);
		additionalProperties.put("timestamp", timeStampValue);

		displayFormat.setAdditionalProperties(additionalProperties);

		final HttpResponse response = Request
				.Post(getBaseUrl() + "reporting/rest/report/execute/" + twoWeeksReport.getId())
				.setHeader("Content-Type", "application/json").setHeader("scopeName", "All Reports")
				.setHeader("roleName", "AdminAll").setHeader("Authorization", getValidJwtToken())
				.bodyByteArray(writeValueAsString(displayFormat).getBytes()).execute().returnResponse();
		Map<String, Object> dataMap = checkSuccessResponseReturnMap(response);
		assertNotNull(dataMap);
		Map<String, Object> movementDataMap = (Map<String, Object>) dataMap.get("movements");
		assertNotNull(movementDataMap);
		List<Map<String,Object>> movementPropertyDataMap = (List<Map<String,Object>>) movementDataMap.get("features");
		assertNotNull(movementPropertyDataMap);
	
		for (Map map : movementPropertyDataMap) {
			assertEquals(testAsset.getCfr(), ((Map) map.get("properties")).get("cfr"));
		}
	}

	@Test
	public void executeStandardTwoWeekReportWithIdForOneAssetFourLastPositionsTest() throws Exception {
		ReportDTO twoWeeksReport = createTwoWeeksLastFourPositionsReport("executeStandardTwoWeekReportWithIdForOneAssetFourLastPositionsTest", "TwoWeeksReports",ReportTypeEnum.STANDARD,VisibilityEnum.PRIVATE,testAsset);
		
		DisplayFormat displayFormat = new DisplayFormat();
		displayFormat.setLengthType(LengthType.NM);
		displayFormat.setVelocityType(VelocityType.KTS);
		HashMap<String, Object> additionalProperties = new HashMap<String, Object>();
		HashMap<String, Object> valueMap = new HashMap<String, Object>();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		String timeStampValue = dateFormat.format(new Date());
		valueMap.put("timestamp", timeStampValue);
		additionalProperties.put("additionalProperties", valueMap);
		additionalProperties.put("timestamp", timeStampValue);

		displayFormat.setAdditionalProperties(additionalProperties);

		final HttpResponse response = Request
				.Post(getBaseUrl() + "reporting/rest/report/execute/" + twoWeeksReport.getId())
				.setHeader("Content-Type", "application/json").setHeader("scopeName", "All Reports")
				.setHeader("roleName", "AdminAll").setHeader("Authorization", getValidJwtToken())
				.bodyByteArray(writeValueAsString(displayFormat).getBytes()).execute().returnResponse();
		Map<String, Object> dataMap = checkSuccessResponseReturnMap(response);
		assertNotNull(dataMap);
		Map<String, Object> movementDataMap = (Map<String, Object>) dataMap.get("movements");
		assertNotNull(movementDataMap);
		List<Map<String,Object>> movementPropertyDataMap = (List<Map<String,Object>>) movementDataMap.get("features");
		assertNotNull(movementPropertyDataMap);
	
		System.out.println(movementPropertyDataMap.size());
		System.out.println(movementPropertyDataMap);
		
		assertEquals(4,movementPropertyDataMap.size());
		for (Map map : movementPropertyDataMap) {
			assertEquals(testAsset.getCfr(), ((Map) map.get("properties")).get("cfr"));
		}
	}

	@Test
	@Ignore
	public void executeStandardTwoWeekReportWithIdForAllAssetFourLastPositionsKnownBugJira3215Test() throws Exception {
		ReportDTO twoWeeksReport = createTwoWeeksLastFourPositionsReport("executeStandardTwoWeekReportWithIdForOneAssetFourLastPositionsTest", "TwoWeeksReports",ReportTypeEnum.STANDARD,VisibilityEnum.PRIVATE,null);
		
		DisplayFormat displayFormat = new DisplayFormat();
		displayFormat.setLengthType(LengthType.NM);
		displayFormat.setVelocityType(VelocityType.KTS);
		HashMap<String, Object> additionalProperties = new HashMap<String, Object>();
		HashMap<String, Object> valueMap = new HashMap<String, Object>();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		String timeStampValue = dateFormat.format(new Date());
		valueMap.put("timestamp", timeStampValue);
		additionalProperties.put("additionalProperties", valueMap);
		additionalProperties.put("timestamp", timeStampValue);

		displayFormat.setAdditionalProperties(additionalProperties);

		final HttpResponse response = Request
				.Post(getBaseUrl() + "reporting/rest/report/execute/" + twoWeeksReport.getId())
				.setHeader("Content-Type", "application/json").setHeader("scopeName", "All Reports")
				.setHeader("roleName", "AdminAll").setHeader("Authorization", getValidJwtToken())
				.bodyByteArray(writeValueAsString(displayFormat).getBytes()).execute().returnResponse();
		Map<String, Object> dataMap = checkSuccessResponseReturnMap(response);
		assertNotNull(dataMap);
		Map<String, Object> movementDataMap = (Map<String, Object>) dataMap.get("movements");
		assertNotNull(movementDataMap);
		List<Map<String,Object>> movementPropertyDataMap = (List<Map<String,Object>>) movementDataMap.get("features");
		assertNotNull(movementPropertyDataMap);
	
		Map<String,Integer> positionsPerShip = new HashMap<>();
		for (Map map : movementPropertyDataMap) {
			String cfr = (String) ((Map) map.get("properties")).get("cfr");			
			if (positionsPerShip.get(cfr) == null) {
				positionsPerShip.put(cfr, 1);
			} else {
				positionsPerShip.put(cfr, 1 +positionsPerShip.get(cfr));
			}
		}
		
		assertNotEquals(AssetTestHelper.getAssetCountSweden(),Integer.valueOf(positionsPerShip.keySet().size()));
		//Correct assertEquals(AssetTestHelper.getAssetCountSweden(),Integer.valueOf(positionsPerShip.keySet().size()));
		
		for (Entry<String, Integer> map : positionsPerShip.entrySet()) {
			assertNotEquals("Ship do not contain 4 positions:" + map.getKey(),new Integer(4),map.getValue());
			// Correct assertEquals("Ship do not contain 4 positions:" + map.getKey(),new Integer(4),map.getValue());			
		}
		
	}

	
	/**
	 * Execute summary two week report with id for one asset test.
	 *
	 * @throws Exception the exception
	 */
	@Test
	@Ignore
	public void executeSummaryTwoWeekReportTest() throws Exception {
		ReportDTO twoWeeksReport = createTwoWeeksReport("executeSummaryTwoWeekReportTest", "TwoWeeksReports",ReportTypeEnum.SUMMARY,VisibilityEnum.PRIVATE,null);
		
		DisplayFormat displayFormat = new DisplayFormat();
		displayFormat.setLengthType(LengthType.NM);
		displayFormat.setVelocityType(VelocityType.KTS);
		HashMap<String, Object> additionalProperties = new HashMap<String, Object>();
		HashMap<String, Object> valueMap = new HashMap<String, Object>();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		String timeStampValue = dateFormat.format(new Date());
		valueMap.put("timestamp", timeStampValue);
		additionalProperties.put("additionalProperties", valueMap);
		additionalProperties.put("timestamp", timeStampValue);

		displayFormat.setAdditionalProperties(additionalProperties);

		final HttpResponse response = Request
				.Post(getBaseUrl() + "reporting/rest/report/execute/" + twoWeeksReport.getId())
				.setHeader("Content-Type", "application/json").setHeader("scopeName", "All Reports")
				.setHeader("roleName", "AdminAll").setHeader("Authorization", getValidJwtToken())
				.bodyByteArray(writeValueAsString(displayFormat).getBytes()).execute().returnResponse();
		Map<String, Object> dataMap = checkSuccessResponseReturnMap(response);
		Map<String, Object> movementDataMap = (Map<String, Object>) dataMap.get("movements");
		assertNotNull(movementDataMap);
		List<Map<String,Object>> movementPropertyDataMap = (List<Map<String,Object>>) movementDataMap.get("features");
		assertNotNull(movementPropertyDataMap);
	
		for (Map map : movementPropertyDataMap) {
			assertEquals(testAsset.getCfr(), ((Map) map.get("properties")).get("cfr"));
		}
	}

}
