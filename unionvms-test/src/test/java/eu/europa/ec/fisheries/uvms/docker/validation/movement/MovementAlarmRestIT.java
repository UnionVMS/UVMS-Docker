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

import eu.europa.ec.fisheries.schema.movementrules.search.v1.AlarmListCriteria;
import eu.europa.ec.fisheries.schema.movementrules.search.v1.AlarmQuery;
import eu.europa.ec.fisheries.schema.movementrules.search.v1.AlarmSearchKey;
import eu.europa.ec.fisheries.schema.movementrules.search.v1.ListPagination;
import eu.europa.ec.fisheries.uvms.asset.client.model.AssetDTO;
import eu.europa.ec.fisheries.uvms.docker.validation.AppError;
import eu.europa.ec.fisheries.uvms.docker.validation.asset.AssetTestHelper;
import eu.europa.ec.fisheries.uvms.docker.validation.common.AbstractRest;
import eu.europa.ec.fisheries.uvms.docker.validation.movement.model.AlarmReport;
import eu.europa.ec.fisheries.uvms.docker.validation.system.helper.FLUXHelper;
import eu.europa.ec.fisheries.uvms.docker.validation.system.helper.SanityRuleHelper;
import eu.europa.ec.fisheries.uvms.movement.model.dto.MovementDto;
import org.hamcrest.CoreMatchers;
import org.junit.Test;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class MovementAlarmRestIT extends AbstractRest {

	@Test
	public void getNumberOfOpenAlarmReportsTest() throws Exception {
	    AssetDTO asset = AssetTestHelper.createBasicAsset();
        FLUXHelper.sendPositionToFluxPlugin(asset,
				new LatLong(56d, 11d, new Date(System.currentTimeMillis() + 10000)));
	    
        SanityRuleHelper.pollAlarmReportCreated();
        
	    Long response = getWebTarget()
                .path("movement/rest/alarms/countopen")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getValidJwtToken())
                .get(Long.class);
	    
		assertTrue(response > 0);
	}

	@Test
	public void getCustomRuleListTest() {
		AlarmQuery alarmQuery = new AlarmQuery();
		ListPagination lp = new ListPagination();
		lp.setListSize(10);
		lp.setPage(1);  //can not be below 1
		alarmQuery.setPagination(lp);
		alarmQuery.setDynamic(true);
		AlarmListCriteria alc = new AlarmListCriteria();
		alc.setKey(AlarmSearchKey.ALARM_GUID);
		alc.setValue(UUID.randomUUID().toString());
		alarmQuery.getAlarmSearchCriteria().add(alc);

		Response response = getWebTarget()
		        .path("movement/rest/alarms/list")
		        .request(MediaType.APPLICATION_JSON)
		        .header(HttpHeaders.AUTHORIZATION, getValidJwtToken())
		        .post(Entity.json(alarmQuery));
		
		assertThat(response.getStatus(), CoreMatchers.is(Status.OK.getStatusCode()));
	}

	@Test
	public void updateAlarmStatusWithoutPersistedEntityTest() {
		AlarmReport alarmReportType = new AlarmReport();
		
		Response response = getWebTarget()
                .path("movement/rest/alarms")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getValidJwtToken())
                .put(Entity.json(alarmReportType));
		
		assertThat(response.getStatus(), CoreMatchers.is(Status.OK.getStatusCode()));
		AppError appError = response.readEntity(AppError.class);
		assertThat(appError.code, CoreMatchers.is(Status.INTERNAL_SERVER_ERROR.getStatusCode()));
	}

	@Test
	public void getAlarmReportByGuidTest() {
	    Response response = getWebTarget()
                .path("movement/rest/alarms")
                .path("guid")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getValidJwtToken())
                .get();

		assertThat(response.getStatus(), CoreMatchers.is(Status.OK.getStatusCode()));
		AppError appError = response.readEntity(AppError.class);
		assertThat(appError.code, CoreMatchers.is(Status.INTERNAL_SERVER_ERROR.getStatusCode()));
	}

	@Test
	public void reprocessAlarmTest() throws Exception {
	    ZonedDateTime timestamp = ZonedDateTime.now(ZoneOffset.UTC);

        AssetDTO asset = AssetTestHelper.createBasicAsset();
        FLUXHelper.sendPositionToFluxPlugin(asset,new LatLong(56d, 11d, new Date(System.currentTimeMillis() + 50000)));
	    
        AlarmReport alarmReport = SanityRuleHelper.getLatestOpenAlarmReportSince(timestamp);
	    assertThat(alarmReport.getStatus(), CoreMatchers.is("OPEN"));
        
	    Response response = getWebTarget()
                .path("movement/rest/alarms/reprocess")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getValidJwtToken())
                .post(Entity.json(Collections.singletonList(alarmReport.getId())));
	    assertEquals(Status.OK.getStatusCode(), response.getStatus());
        
	    AlarmReport alarmReportAfter = getAlarmReportByGuid(alarmReport.getId().toString());
        assertThat(alarmReportAfter.getStatus(), CoreMatchers.is("REPROCESSED"));
	}
	
	@Test
    public void reprocessAlarmSuccessTest() throws Exception {
		Thread.sleep(5000); //otherwise it gets the alarm report for some other movement.
        ZonedDateTime timestamp = ZonedDateTime.now(ZoneOffset.UTC);

        // Asset does not exist
        AssetDTO asset = AssetTestHelper.createBasicAsset();
        LatLong position = new LatLong(42d, 41d, new Date(System.currentTimeMillis() + 5000));
        FLUXHelper.sendPositionToFluxPlugin(asset,position);
        
        AlarmReport alarmReport = SanityRuleHelper.getLatestOpenAlarmReportSince(timestamp);
        assertThat(alarmReport.getStatus(), CoreMatchers.is("OPEN"));
        
        // Create asset

		// This is here bc we used to have this test working on asset not existing.
		// When we introduced the functionality of creating assets on unknown using time in future and sleep became the workaround
        Thread.sleep(5000);

        Response response = getWebTarget()
                .path("movement/rest/alarms/reprocess")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getValidJwtToken())
                .post(Entity.json(Collections.singletonList(alarmReport.getId())));
		assertEquals(Status.OK.getStatusCode(), response.getStatus());
        
        AlarmReport alarmReportAfter = getAlarmReportByGuid(alarmReport.getId().toString());
        assertThat(alarmReportAfter.getStatus(), CoreMatchers.is("REPROCESSED"));
        
        List<MovementDto> latestMovements = MovementHelper.getLatestMovements(
        		Collections.singletonList(alarmReport.getIncomingMovement().getAssetGuid()));
        assertThat(latestMovements.size(), CoreMatchers.is(1));
        assertThat(latestMovements.get(0).getLocation().getLatitude(), CoreMatchers.is(position.latitude));
        assertThat(latestMovements.get(0).getLocation().getLongitude(), CoreMatchers.is(position.longitude));
    }
	
	@Test
    public void reprocessAlarmFailureTest() throws Exception {
        ZonedDateTime timestamp = ZonedDateTime.now(ZoneOffset.UTC);

        // Asset does not exist
        AssetDTO asset = AssetTestHelper.createBasicAsset();
        LatLong position = new LatLong(42d, 41d, new Date(System.currentTimeMillis() + 5000));
        FLUXHelper.sendPositionToFluxPlugin(asset,position);
        
        SanityRuleHelper.pollAlarmReportCreated();
        
        AlarmReport alarmReport = SanityRuleHelper.getLatestOpenAlarmReportSince(timestamp);
        assertThat(alarmReport.getStatus(), CoreMatchers.is("OPEN"));
        
        Response response = getWebTarget()
                .path("movement/rest/alarms/reprocess")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getValidJwtToken())
                .post(Entity.json(Collections.singletonList(alarmReport.getId())));
		assertEquals(Status.OK.getStatusCode(), response.getStatus());
        
        AlarmReport alarmReportAfter = getAlarmReportByGuid(alarmReport.getId().toString());
        assertThat(alarmReportAfter.getStatus(), CoreMatchers.is("REPROCESSED"));

        AlarmReport latestAlarmReport = SanityRuleHelper.getLatestOpenAlarmReportSince(timestamp);
        
        assertThat(latestAlarmReport.getId(), CoreMatchers.is(CoreMatchers.not(alarmReport.getId())));
        assertThat(latestAlarmReport.getStatus(), CoreMatchers.is("OPEN"));
    }
	
	@Test
	public void getAlarmReportVerifyRawMovementDataTest() throws Exception {
        ZonedDateTime timestamp = ZonedDateTime.now(ZoneOffset.UTC);

		AssetDTO asset = AssetTestHelper.createBasicAsset();
        FLUXHelper.sendPositionToFluxPlugin(asset, new LatLong(56d, 11d, new Date(System.currentTimeMillis() + 5000)));

        SanityRuleHelper.pollAlarmReportCreated();
        
        AlarmReport alarm = SanityRuleHelper.getLatestOpenAlarmReportSince(timestamp);
        assertThat(alarm.getIncomingMovement(), CoreMatchers.is(CoreMatchers.notNullValue()));
        assertThat(alarm.getIncomingMovement().getAssetCFR(), CoreMatchers.is(asset.getCfr()));
	}
	
	private AlarmReport getAlarmReportByGuid(String guid) {
	    return getWebTarget()
                .path("movement/rest/alarms")
                .path(guid)
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getValidJwtToken())
                .get(AlarmReport.class);
	}
}
