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

import eu.europa.ec.fisheries.uvms.asset.client.model.AssetDTO;
import eu.europa.ec.fisheries.uvms.docker.validation.asset.AssetTestHelper;
import eu.europa.ec.fisheries.uvms.docker.validation.common.AbstractRest;
import eu.europa.ec.fisheries.uvms.docker.validation.mobileterminal.MobileTerminalTestHelper;
import eu.europa.ec.fisheries.uvms.docker.validation.mobileterminal.dto.MobileTerminalDto;
import eu.europa.ec.fisheries.uvms.docker.validation.movement.LatLong;
import eu.europa.ec.fisheries.uvms.docker.validation.movement.MovementHelper;
import eu.europa.ec.fisheries.uvms.docker.validation.movement.model.IncomingMovement;
import eu.europa.ec.fisheries.uvms.movement.model.dto.MovementDto;
import eu.europa.ec.fisheries.uvms.reporting.service.dto.*;
import eu.europa.ec.fisheries.uvms.reporting.service.dto.report.ReportDTO;
import eu.europa.ec.fisheries.uvms.reporting.service.dto.report.VisibilityEnum;
import eu.europa.ec.fisheries.uvms.reporting.service.entities.FilterType;
import eu.europa.ec.fisheries.uvms.reporting.service.entities.Position;
import eu.europa.ec.fisheries.uvms.reporting.service.entities.Selector;
import eu.europa.ec.fisheries.uvms.reporting.service.enums.ReportTypeEnum;
import eu.europa.ec.fisheries.uvms.reporting.service.enums.VelocityType;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.JMSException;
import javax.json.JsonObject;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

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
    @Ignore
    public void listReportsTest() {
        Response response = getWebTarget()
                .path("reporting/rest/report/list")
                .request(MediaType.APPLICATION_JSON)
                .header("scopeName", "All Vessels")
                .header("roleName", "AdminAllUVMS")
                .header(HttpHeaders.AUTHORIZATION, getValidJwtToken())
                .get();

        assertEquals(Status.OK.getStatusCode(), response.getStatus());
        Collection list = response.readEntity(List.class);
        assertNotNull(list);
    }

    @Test
    @Ignore
    public void listLastExecutedReportsTest() {
        Response response = getWebTarget()
                .path("reporting/rest/report/list/lastexecuted/10")
                .request(MediaType.APPLICATION_JSON)
                .header("scopeName", "All Vessels")
                .header("roleName", "AdminAllUVMS")
                .header(HttpHeaders.AUTHORIZATION, getValidJwtToken())
                .get();

        assertEquals(Status.OK.getStatusCode(), response.getStatus());
        Collection list = response.readEntity(List.class);
        assertNotNull(list);
    }

    @Test
    @Ignore
    public void getReportTest() throws Exception {
        Long reportId = createTwoWeeksReport("createReportTest").getId();

        ReportDTO response = getWebTarget()
                .path("reporting/rest/report")
                .path(String.valueOf(reportId))
                .request(MediaType.APPLICATION_JSON)
                .header("scopeName", "All Vessels")
                .header("roleName", "AdminAllUVMS")
                .header(HttpHeaders.AUTHORIZATION, getValidJwtToken())
                .get(ReportDTO.class);

        assertNotNull(response);
    }

    @Test
    @Ignore
    public void createReportTest() throws Exception {
        createTwoWeeksReport("createReportTest");
    }

    @Test
    @Ignore
    public void deleteReportTest() throws Exception {
        Long reportId = createTwoWeeksReport("deleteReportTest").getId();

        Response response = getWebTarget()
                .path("reporting/rest/report")
                .path(String.valueOf(reportId))
                .request(MediaType.APPLICATION_JSON)
                .header("scopeName", "All Vessels")
                .header("roleName", "AdminAllUVMS")
                .header(HttpHeaders.AUTHORIZATION, getValidJwtToken())
                .delete();

        assertEquals(Status.OK.getStatusCode(), response.getStatus());
    }

    @Test
    @Ignore
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

        assertEquals(Status.OK.getStatusCode(), response.getStatus());
    }

    @Test
    @Ignore
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

        assertEquals(Status.OK.getStatusCode(), response.getStatus());
    }

    @Test
    @Ignore
    public void executeStandardTwoWeekReportWithIdForOneAssetTest() throws Exception {
        ReportDTO twoWeeksReport = createTwoWeeksReport("executeStandardTwoWeekReportWithIdForOneAssetTest",
                ReportTypeEnum.STANDARD, VisibilityEnum.PRIVATE, testAsset);

        DisplayFormat displayFormat = new DisplayFormat();
        displayFormat.setLengthType(LengthType.NM);
        displayFormat.setVelocityType(VelocityType.KTS);
        HashMap<String, Object> additionalProperties = new HashMap<>();
        HashMap<String, Object> valueMap = new HashMap<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");
        String timeStampValue = dateFormat.format(new Date());
        valueMap.put("timestamp", timeStampValue);
        additionalProperties.put("additionalProperties", valueMap);
        additionalProperties.put("timestamp", timeStampValue);

        displayFormat.setAdditionalProperties(additionalProperties);

        JsonObject node = getWebTarget()
                .path("reporting/rest/report/execute")
                .path(String.valueOf(twoWeeksReport.getId()))
                .request(MediaType.APPLICATION_JSON)
                .header("scopeName", "All Vessels")
                .header("roleName", "AdminAllUVMS")
                .header(HttpHeaders.AUTHORIZATION, getValidJwtToken())
                .post(Entity.json(displayFormat), JsonObject.class);

        JsonObject movements = node.getJsonObject("movements");
        JsonObject segments = node.getJsonObject("segments");
        JsonObject tracks = node.getJsonObject("tracks");
        JsonObject trips = node.getJsonObject("trips");
        JsonObject activities = node.getJsonObject("activities");
        JsonObject criteria = node.getJsonObject("criteria");

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

        JsonObject node = getWebTarget()
                .path("reporting/rest/report/execute/")
                .path(String.valueOf(twoWeeksReport.getId()))
                .request(MediaType.APPLICATION_JSON)
                .header("scopeName", "All Vessels")
                .header("roleName", "AdminAllUVMS")
                .header(HttpHeaders.AUTHORIZATION, getValidJwtToken())
                .post(Entity.json(displayFormat), JsonObject.class);

        JsonObject movements = node.getJsonObject("movements");
        JsonObject segments = node.getJsonObject("segments");
        JsonObject tracks = node.getJsonObject("tracks");
        JsonObject trips = node.getJsonObject("trips");
        JsonObject activities = node.getJsonObject("activities");
        JsonObject criteria = node.getJsonObject("criteria");

        assertTrue(movements != null
                && segments != null
                && tracks != null
                && trips != null
                && activities != null
                && criteria != null);
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

        JsonObject node = getWebTarget()
                .path("reporting/rest/report/execute/")
                .path(String.valueOf(twoWeeksReport.getId()))
                .request(MediaType.APPLICATION_JSON)
                .header("scopeName", "All Vessels")
                .header("roleName", "AdminAllUVMS")
                .header(HttpHeaders.AUTHORIZATION, getValidJwtToken())
                .post(Entity.json(displayFormat), JsonObject.class);

        JsonObject movements = node.getJsonObject("movements");
        JsonObject segments = node.getJsonObject("segments");
        JsonObject tracks = node.getJsonObject("tracks");
        JsonObject trips = node.getJsonObject("trips");
        JsonObject activities = node.getJsonObject("activities");
        JsonObject criteria = node.getJsonObject("criteria");

        assertTrue(movements != null
                && segments != null
                && tracks != null
                && trips != null
                && activities != null
                && criteria != null);
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

        JsonObject node = getWebTarget()
                .path("reporting/rest/report/execute/")
                .path(String.valueOf(twoWeeksReport.getId()))
                .request(MediaType.APPLICATION_JSON)
                .header("scopeName", "All Vessels")
                .header("roleName", "AdminAllUVMS")
                .header(HttpHeaders.AUTHORIZATION, getValidJwtToken())
                .post(Entity.json(displayFormat), JsonObject.class);

        JsonObject movements = node.getJsonObject("movements");
        JsonObject segments = node.getJsonObject("segments");
        JsonObject tracks = node.getJsonObject("tracks");
        JsonObject trips = node.getJsonObject("trips");
        JsonObject activities = node.getJsonObject("activities");
        JsonObject criteria = node.getJsonObject("criteria");

        assertTrue(movements != null
                && segments != null
                && tracks != null
                && trips != null
                && activities != null
                && criteria != null);
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

        Response response = getWebTarget()
                .path("reporting/rest/report")
                .queryParam("projection", "DEFAULT")
                .request(MediaType.APPLICATION_JSON)
                .header("scopeName", "All Vessels")
                .header("roleName", "AdminAllUVMS")
                .header(HttpHeaders.AUTHORIZATION, getValidJwtToken())
                .post(Entity.json(writeValueAsString));

        Integer reportId = response.readEntity(Integer.class);
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

        Response response = getWebTarget()
                .path("reporting/rest/report")
                .queryParam("projection", "DEFAULT")
                .request(MediaType.APPLICATION_JSON)
                .header("scopeName", "All Vessels")
                .header("roleName", "AdminAllUVMS")
                .header(HttpHeaders.AUTHORIZATION, getValidJwtToken())
                .post(Entity.json(writeValueAsString));

        Integer reportId = response.readEntity(Integer.class);
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
