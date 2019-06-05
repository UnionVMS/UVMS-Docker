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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import eu.europa.ec.fisheries.uvms.asset.client.model.AssetDTO;
import eu.europa.ec.fisheries.uvms.commons.rest.dto.ResponseDto;
import eu.europa.ec.fisheries.uvms.docker.validation.asset.AssetTestHelper;
import eu.europa.ec.fisheries.uvms.docker.validation.common.AbstractRest;
import eu.europa.ec.fisheries.uvms.docker.validation.mobileterminal.MobileTerminalTestHelper;
import eu.europa.ec.fisheries.uvms.docker.validation.mobileterminal.dto.MobileTerminalDto;
import eu.europa.ec.fisheries.uvms.docker.validation.movement.LatLong;
import eu.europa.ec.fisheries.uvms.docker.validation.movement.MovementDto;
import eu.europa.ec.fisheries.uvms.docker.validation.movement.MovementHelper;
import eu.europa.ec.fisheries.uvms.docker.validation.movement.model.IncomingMovement;
import eu.europa.ec.fisheries.uvms.reporting.service.dto.*;
import eu.europa.ec.fisheries.uvms.reporting.service.dto.report.ReportDTO;
import eu.europa.ec.fisheries.uvms.reporting.service.dto.report.VisibilityEnum;
import eu.europa.ec.fisheries.uvms.reporting.service.entities.FilterType;
import eu.europa.ec.fisheries.uvms.reporting.service.entities.Position;
import eu.europa.ec.fisheries.uvms.reporting.service.entities.Selector;
import eu.europa.ec.fisheries.uvms.reporting.service.enums.ReportTypeEnum;
import eu.europa.ec.fisheries.uvms.reporting.service.enums.VelocityType;
import org.apache.http.HttpResponse;
import org.apache.http.client.fluent.Request;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.JMSException;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;

public class ReportingRestIT extends AbstractRest {
    private static final Logger LOG = LoggerFactory.getLogger(ReportingRestIT.class.getSimpleName());

    private static MovementHelper movementHelper;
    private static AssetDTO testAsset = null;

    @BeforeClass
    public static void createTestAssetWithTerminalAndPositions() throws JMSException {
        movementHelper = new MovementHelper();
        try {
            testAsset = AssetTestHelper.createTestAsset();
            MobileTerminalDto mobileTerminal = MobileTerminalTestHelper.createMobileTerminal();
            MobileTerminalTestHelper.assignMobileTerminal(testAsset, mobileTerminal);
            List<LatLong> route = movementHelper.createRuttVarbergGrena(-1);

            for (LatLong position : route) {
                IncomingMovement createMovementRequest = movementHelper.createIncomingMovement(testAsset, position);
                MovementDto createMovementResponse = movementHelper.createMovement(createMovementRequest);
                assertNotNull(createMovementResponse);
            }
        } catch (Exception e) {
            LOG.error("Error occurred while creating Asset with MT & Positions", e);
        }
    }

    @AfterClass
    public static void cleanup() {
        movementHelper.close();
    }

    @Test
    public void listReportsTest() {
        ResponseDto response = getWebTarget()
                .path("reporting/rest/report/list")
                .request(MediaType.APPLICATION_JSON)
                .header("scopeName", "All Vessels")
                .header("roleName", "AdminAllUVMS")
                .header(HttpHeaders.AUTHORIZATION, getValidJwtToken())
                .get(ResponseDto.class);

        Collection list = (Collection) response.getData();
        assertNotNull(list);
    }

    @Test
    public void listLastExecutedReportsTest() {
        ResponseDto response = getWebTarget()
                .path("reporting/rest/report/list/lastexecuted/10")
                .request(MediaType.APPLICATION_JSON)
                .header("scopeName", "All Vessels")
                .header("roleName", "AdminAllUVMS")
                .header(HttpHeaders.AUTHORIZATION, getValidJwtToken())
                .get(ResponseDto.class);

        Collection list = (Collection) response.getData();
        assertNotNull(list);
    }

    @Test
    public void getReportTest() throws Exception {
        Long reportId = createTwoWeeksReport("createReportTest").getId();

        ResponseDto<ReportDTO> response = getWebTarget()
                .path("reporting/rest/report")
                .path(String.valueOf(reportId))
                .request(MediaType.APPLICATION_JSON)
                .header("scopeName", "All Vessels")
                .header("roleName", "AdminAllUVMS")
                .header(HttpHeaders.AUTHORIZATION, getValidJwtToken())
                .get(new GenericType<ResponseDto<ReportDTO>>() {
                });

        ReportDTO report = response.getData();
        assertNotNull(report);
    }

    @Test
    public void createReportTest() throws Exception {
        createTwoWeeksReport("createReportTest");
    }

    @Test
    public void deleteReportTest() throws Exception {
        Long reportId = createTwoWeeksReport("deleteReportTest").getId();

        ResponseDto response = getWebTarget()
                .path("reporting/rest/report")
                .path(String.valueOf(reportId))
                .request(MediaType.APPLICATION_JSON)
                .header("scopeName", "All Vessels")
                .header("roleName", "AdminAllUVMS")
                .header(HttpHeaders.AUTHORIZATION, getValidJwtToken())
                .delete(ResponseDto.class);

        assertEquals(Response.Status.OK.getStatusCode(), response.getCode());
    }

    @Test
    public void updateReportTest() throws Exception {
        ReportDTO twoWeeksReport = createTwoWeeksReport("updateReportTest");

        assertTrue(twoWeeksReport.getDescription().startsWith("TwoWeeksReports"));

        twoWeeksReport.setDescription("NewDescription");

        Response response = getWebTarget()
                .path("reporting/rest/report")
                .path(String.valueOf(twoWeeksReport.getId()))
                .request(MediaType.APPLICATION_JSON)
                .header("scopeName", "All Vessels")
                .header("roleName", "AdminAllUVMS")
                .header(HttpHeaders.AUTHORIZATION, getValidJwtToken())
                .put(Entity.json(twoWeeksReport));

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    }

    @Test
    public void shareReportTest() throws Exception {
        ReportDTO twoWeeksReport = createTwoWeeksReport("shareReportTest");

        Response response = getWebTarget()
                .path("reporting/rest/report/share")
                .path(String.valueOf(twoWeeksReport.getId()))
                .path(VisibilityEnum.PUBLIC.getName())
                .request(MediaType.APPLICATION_JSON)
                .header("scopeName", "All Vessels")
                .header("roleName", "AdminAllUVMS")
                .header(HttpHeaders.AUTHORIZATION, getValidJwtToken())
                .put(Entity.json(twoWeeksReport));

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    }

    @Test
    public void executeStandardTwoWeekReportWithIdForOneAssetTest() throws Exception {
        ReportDTO twoWeeksReport = createTwoWeeksReport("executeStandardTwoWeekReportWithIdForOneAssetTest",
                ReportTypeEnum.STANDARD, VisibilityEnum.PRIVATE, testAsset);

        DisplayFormat displayFormat = new DisplayFormat();
        displayFormat.setLengthType(LengthType.NM);
        displayFormat.setVelocityType(VelocityType.KTS);
        HashMap<String, Object> additionalProperties = new HashMap<>();
        HashMap<String, Object> valueMap = new HashMap<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        String timeStampValue = dateFormat.format(new Date());
        valueMap.put("timestamp", timeStampValue);
        additionalProperties.put("additionalProperties", valueMap);
        additionalProperties.put("timestamp", timeStampValue);

        displayFormat.setAdditionalProperties(additionalProperties);

        ResponseDto<ObjectNode> response = getWebTarget()
                .path("reporting/rest/report/execute")
                .path(String.valueOf(twoWeeksReport.getId()))
                .request(MediaType.APPLICATION_JSON)
                .header("scopeName", "All Vessels")
                .header("roleName", "AdminAllUVMS")
                .header(HttpHeaders.AUTHORIZATION, getValidJwtToken())
                .post(Entity.json(displayFormat), new GenericType<ResponseDto<ObjectNode>>() {
                });

        ObjectNode node = response.getData();
        JsonNode movements = node.get("movements");
        JsonNode segments = node.get("segments");
        JsonNode tracks = node.get("tracks");
        JsonNode trips = node.get("trips");
        JsonNode activities = node.get("activities");
        JsonNode criteria = node.get("criteria");

        assertTrue(movements != null
                && segments != null
                && tracks != null
                && trips != null
                && activities != null
                && criteria != null);
        // Todo: Although this test passes, every node above is empty. Most likely there is a bug in Reporting.
        // For more info: https://jira.havochvatten.se/jira/browse/UV-124
    }

    @Test // I will refactor this after working on Reporting. /Ksm
    @Ignore("The test is not working due to a bug in Reporting. See: https://jira.havochvatten.se/jira/browse/UV-124")
    public void executeStandardTwoWeekReportWithIdForOneAssetFourLastPositionsTest() throws Exception {
        ReportDTO twoWeeksReport = createTwoWeeksLastFourPositionsReport(ReportTypeEnum.STANDARD, VisibilityEnum.PRIVATE, testAsset);
        DisplayFormat displayFormat = new DisplayFormat();
        displayFormat.setLengthType(LengthType.NM);
        displayFormat.setVelocityType(VelocityType.KTS);
        HashMap<String, Object> additionalProperties = new HashMap<>();
        HashMap<String, Object> valueMap = new HashMap<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        // Added so that the system understands that it is supposed to handle time in utc value instead of local
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        String timeStampValue = dateFormat.format(new Date());
        valueMap.put("timestamp", timeStampValue);
        additionalProperties.put("additionalProperties", valueMap);
        additionalProperties.put("timestamp", timeStampValue);

        displayFormat.setAdditionalProperties(additionalProperties);

        final HttpResponse response = Request
                .Post(getBaseUrl() + "reporting/rest/report/execute/" + twoWeeksReport.getId())
                .setHeader("Content-Type", "application/json").setHeader("scopeName", "All Vessels")
                .setHeader("roleName", "AdminAllUVMS").setHeader("Authorization", getValidJwtToken())
                .bodyByteArray(writeValueAsString(displayFormat).getBytes()).execute().returnResponse();
        Map<String, Object> dataMap = checkSuccessResponseReturnMap(response);
        assertNotNull(dataMap);
        Map<String, Object> movementDataMap = (Map<String, Object>) dataMap.get("movements");
        assertNotNull(movementDataMap);
        List<Map<String, Object>> movementPropertyDataMap = (List<Map<String, Object>>) movementDataMap.get("features");
        assertNotNull(movementPropertyDataMap);

        assertEquals(4, movementPropertyDataMap.size());
        for (Map map : movementPropertyDataMap) {
            assertEquals(testAsset.getCfr(), ((Map) map.get("properties")).get("cfr"));
        }
    }

    @Test // I will refactor this after working on Reporting. /Ksm
    @Ignore("The test is not working due to a bug in Reporting. See: https://jira.havochvatten.se/jira/browse/UV-124")
    public void executeStandardTwoWeekReportWithIdForAllAssetFourLastPositionsKnownBugJira3215Test() throws Exception {
        ReportDTO twoWeeksReport = createTwoWeeksLastFourPositionsReport(ReportTypeEnum.STANDARD, VisibilityEnum.PRIVATE, null);
        DisplayFormat displayFormat = new DisplayFormat();
        displayFormat.setLengthType(LengthType.NM);
        displayFormat.setVelocityType(VelocityType.KTS);
        HashMap<String, Object> additionalProperties = new HashMap<>();
        HashMap<String, Object> valueMap = new HashMap<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        String timeStampValue = dateFormat.format(new Date());
        valueMap.put("timestamp", timeStampValue);
        additionalProperties.put("additionalProperties", valueMap);
        additionalProperties.put("timestamp", timeStampValue);

        displayFormat.setAdditionalProperties(additionalProperties);

        final HttpResponse response = Request
                .Post(getBaseUrl() + "reporting/rest/report/execute/" + twoWeeksReport.getId())
                .setHeader("Content-Type", "application/json").setHeader("scopeName", "All Vessels")
                .setHeader("roleName", "AdminAllUVMS").setHeader("Authorization", getValidJwtToken())
                .bodyByteArray(writeValueAsString(displayFormat).getBytes()).execute().returnResponse();
        Map<String, Object> dataMap = checkSuccessResponseReturnMap(response);
        assertNotNull(dataMap);
        Map<String, Object> movementDataMap = (Map<String, Object>) dataMap.get("movements");
        assertNotNull(movementDataMap);
        List<Map<String, Object>> movementPropertyDataMap = (List<Map<String, Object>>) movementDataMap.get("features");
        assertNotNull(movementPropertyDataMap);

        Map<String, Integer> positionsPerShip = new HashMap<>();
        for (Map map : movementPropertyDataMap) {
            String cfr = (String) ((Map) map.get("properties")).get("cfr");
            int count = positionsPerShip.getOrDefault(cfr, 0);
            positionsPerShip.put(cfr, count + 1);
        }

        assertEquals("Do not contain all ships", AssetTestHelper.getAssetCountSweden(), Integer.valueOf(positionsPerShip.keySet().size()));

        for (Entry<String, Integer> map : positionsPerShip.entrySet()) {
            assertEquals("Ship do not contain 4 positions:" + map.getKey(), new Integer(4), map.getValue());
        }
    }

    @Test // I will refactor this after working on Reporting. /Ksm
    @Ignore("The test is not working due to a bug in Reporting. See: https://jira.havochvatten.se/jira/browse/UV-124")
    public void executeSummaryTwoWeekReportTest() throws Exception {
        ReportDTO twoWeeksReport = createTwoWeeksReport("executeSummaryTwoWeekReportTest",
                ReportTypeEnum.SUMMARY, VisibilityEnum.PRIVATE, null);

        DisplayFormat displayFormat = new DisplayFormat();
        displayFormat.setLengthType(LengthType.NM);
        displayFormat.setVelocityType(VelocityType.KTS);
        HashMap<String, Object> additionalProperties = new HashMap<>();
        HashMap<String, Object> valueMap = new HashMap<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        String timeStampValue = dateFormat.format(new Date());
        valueMap.put("timestamp", timeStampValue);
        additionalProperties.put("additionalProperties", valueMap);
        additionalProperties.put("timestamp", timeStampValue);

        displayFormat.setAdditionalProperties(additionalProperties);

        final HttpResponse response = Request
                .Post(getBaseUrl() + "reporting/rest/report/execute/" + twoWeeksReport.getId())
                .setHeader("Content-Type", "application/json").setHeader("scopeName", "All Vessels")
                .setHeader("roleName", "AdminAllUVMS").setHeader("Authorization", getValidJwtToken())
                .bodyByteArray(writeValueAsString(displayFormat).getBytes()).execute().returnResponse();
        Map<String, Object> dataMap = checkSuccessResponseReturnMap(response);
        Map<String, Object> movementDataMap = (Map<String, Object>) dataMap.get("movements");
        assertNotNull(movementDataMap);
        List<Map<String, Object>> movementPropertyDataMap = (List<Map<String, Object>>) movementDataMap.get("features");
        assertNotNull(movementPropertyDataMap);

        for (Map map : movementPropertyDataMap) {
            assertEquals(testAsset.getCfr(), ((Map) map.get("properties")).get("cfr"));
        }
    }

    private ReportDTO createTwoWeeksReport(final String name) throws IOException {
        return createTwoWeeksReport(name, ReportTypeEnum.STANDARD, VisibilityEnum.PRIVATE, null);
    }

    private ReportDTO createTwoWeeksReport(final String name, ReportTypeEnum reportTypeEnum,
                                           VisibilityEnum visibilityEnum, AssetDTO asset) throws IOException {
        ReportDTO reportDTO = new ReportDTO();
        long time = new Date().getTime();
        reportDTO.setName(name + time);
        reportDTO.setDescription("TwoWeeksReports" + time);
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
            addAssetFilterToReport(asset, reportDTO);
        }

        String writeValueAsString = writeValueAsString(reportDTO);

        ResponseDto response = getWebTarget()
                .path("reporting/rest/report")
                .queryParam("projection", "DEFAULT")
                .request(MediaType.APPLICATION_JSON)
                .header("scopeName", "All Vessels")
                .header("roleName", "AdminAllUVMS")
                .header(HttpHeaders.AUTHORIZATION, getValidJwtToken())
                .post(Entity.json(writeValueAsString), ResponseDto.class);

        Integer reportId = (Integer) response.getData();
        reportDTO.setId(reportId.longValue());
        return reportDTO;
    }

    private ReportDTO createTwoWeeksLastFourPositionsReport(ReportTypeEnum reportTypeEnum, VisibilityEnum visibilityEnum,
                                                            AssetDTO asset) throws IOException {
        ReportDTO reportDTO = new ReportDTO();
        long time = new Date().getTime();
        reportDTO.setName("executeStandardTwoWeekReportWithIdForOneAssetFourLastPositionsTest" + time);
        reportDTO.setDescription("TwoWeeksReports" + time);
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
            addAssetFilterToReport(asset, reportDTO);
        }

        String writeValueAsString = writeValueAsString(reportDTO);

        ResponseDto response = getWebTarget()
                .path("reporting/rest/report")
                .queryParam("projection", "DEFAULT")
                .request(MediaType.APPLICATION_JSON)
                .header("scopeName", "All Vessels")
                .header("roleName", "AdminAllUVMS")
                .header(HttpHeaders.AUTHORIZATION, getValidJwtToken())
                .post(Entity.json(writeValueAsString), ResponseDto.class);

        Integer reportId = (Integer) response.getData();
        reportDTO.setId(reportId.longValue());
        return reportDTO;
    }

    private void addAssetFilterToReport(AssetDTO asset, ReportDTO reportDTO) {
        AssetFilterDTO assetFilterDTO = new AssetFilterDTO();
        assetFilterDTO.setGuid(asset.getId().toString());
        assetFilterDTO.setName(asset.getName());
        reportDTO.addFilter(assetFilterDTO);
    }
}
