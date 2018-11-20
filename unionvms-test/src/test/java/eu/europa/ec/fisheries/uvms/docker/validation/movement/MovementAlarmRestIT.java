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

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import org.hamcrest.CoreMatchers;
import org.junit.Test;
import eu.europa.ec.fisheries.schema.movementrules.search.v1.AlarmListCriteria;
import eu.europa.ec.fisheries.schema.movementrules.search.v1.AlarmQuery;
import eu.europa.ec.fisheries.schema.movementrules.search.v1.AlarmSearchKey;
import eu.europa.ec.fisheries.schema.movementrules.search.v1.ListPagination;
import eu.europa.ec.fisheries.uvms.asset.client.model.AssetDTO;
import eu.europa.ec.fisheries.uvms.docker.validation.asset.AssetTestHelper;
import eu.europa.ec.fisheries.uvms.docker.validation.common.AbstractRest;
import eu.europa.ec.fisheries.uvms.docker.validation.movement.model.AlarmListResponseDto;
import eu.europa.ec.fisheries.uvms.docker.validation.movement.model.AlarmReport;
import eu.europa.ec.fisheries.uvms.docker.validation.system.helper.NAFHelper;
import eu.europa.ec.fisheries.uvms.docker.validation.system.helper.SanityRuleHelper;

/**
 * The Class RulesAlarmRestIT.
 */

public class MovementAlarmRestIT extends AbstractRest {

	/**
	 * Gets the number of open alarm reports test.
	 *
	 * @return the number of open alarm reports test
	 * @throws Exception the exception
	 */
	@Test
	public void getNumberOfOpenAlarmReportsTest() throws Exception {
	    AssetDTO asset = AssetTestHelper.createBasicAsset();
        NAFHelper.sendPositionToNAFPlugin(new LatLong(56d, 11d, new Date()), asset);
	    
	    Long response = getWebTarget()
                .path("movement/rest/alarms/countopen")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getValidJwtToken())
                .get(Long.class);
	    
		assertTrue(response > 0);
	}


	/**
	 * Gets the custom rule list test.
	 *
	 * @return the custom rule list test
	 * @throws Exception the exception
	 */
	//Added enough to make the query complete
	@Test
	public void getCustomRuleListTest() throws Exception {
		AlarmQuery alarmQuery = new AlarmQuery();
		ListPagination lp = new ListPagination();
		lp.setListSize(10);
		lp.setPage(1);  //can not be below 1
		alarmQuery.setPagination(lp);
		alarmQuery.setDynamic(true);
		AlarmListCriteria alc = new AlarmListCriteria();
		alc.setKey(AlarmSearchKey.ALARM_GUID);
		alc.setValue("dummyguid");
		alarmQuery.getAlarmSearchCriteria().add(alc);

		Response response = getWebTarget()
		        .path("movement/rest/alarms/list")
		        .request(MediaType.APPLICATION_JSON)
		        .header(HttpHeaders.AUTHORIZATION, getValidJwtToken())
		        .post(Entity.json(alarmQuery));
		
		assertThat(response.getStatus(), CoreMatchers.is(Status.OK.getStatusCode()));
	}


	/**
	 * Update alarm status test.
	 *
	 * @throws Exception the exception
	 */
	//Nothing to update, changing to expect a server error
	@Test
	public void updateAlarmStatusTest() throws Exception {
		AlarmReport alarmReportType = new AlarmReport();
		
		Response response = getWebTarget()
                .path("movement/rest/alarms")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getValidJwtToken())
                .put(Entity.json(alarmReportType));
		
		assertThat(response.getStatus(), CoreMatchers.is(Status.INTERNAL_SERVER_ERROR.getStatusCode()));
	}

	
	/**
	 * Gets the alarm report by guid test.
	 *
	 * @return the alarm report by guid test
	 * @throws Exception the exception
	 */
	@Test
	public void getAlarmReportByGuidTest() throws Exception {
	    Response response = getWebTarget()
                .path("movement/rest/alarms")
                .path("guid")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getValidJwtToken())
                .get();
	    
	    assertThat(response.getStatus(), CoreMatchers.is(Status.INTERNAL_SERVER_ERROR.getStatusCode()));
	}

	
	/**
	 * Reprocess alarm test.
	 *
	 * @throws Exception the exception
	 */
	@Test
	public void reprocessAlarmTest() throws Exception {
	    ZonedDateTime timestamp = ZonedDateTime.now(ZoneOffset.UTC);

        AssetDTO asset = AssetTestHelper.createBasicAsset();
        NAFHelper.sendPositionToNAFPlugin(new LatLong(56d, 11d, new Date()), asset);
	    
        AlarmReport alarmReport = getLatestAlarmReportSince(timestamp);
	    assertThat(alarmReport.getStatus(), CoreMatchers.is("OPEN"));
        
	    Response response = getWebTarget()
                .path("movement/rest/alarms/reprocess")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getValidJwtToken())
                .post(Entity.json(Arrays.asList(alarmReport.getGuid())), Response.class);
        
	    AlarmReport alarmReportAfter = getAlarmReportByGuid(alarmReport.getGuid());
        assertThat(alarmReportAfter.getStatus(), CoreMatchers.is("REPROCESSED"));
	}
	
	@Test
	public void getAlarmReportVerifyRawMovementDataTest() throws Exception {
        ZonedDateTime timestamp = ZonedDateTime.now(ZoneOffset.UTC);

		AssetDTO asset = AssetTestHelper.createBasicAsset();
        NAFHelper.sendPositionToNAFPlugin(new LatLong(56d, 11d, new Date()), asset);

        SanityRuleHelper.pollAlarmReportCreated();
        
        AlarmReport alarm = getLatestAlarmReportSince(timestamp);
        assertThat(alarm.getIncomingMovement(), CoreMatchers.is(CoreMatchers.notNullValue()));
        assertThat(alarm.getIncomingMovement().getAssetName(), CoreMatchers.is(asset.getName()));
	}
	
	private AlarmReport getAlarmReportByGuid(String guid) {
	    return getWebTarget()
                .path("movement/rest/alarms")
                .path(guid)
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getValidJwtToken())
                .get(AlarmReport.class);
	}
	
	private AlarmReport getLatestAlarmReportSince(ZonedDateTime timestamp) {
	    AlarmQuery query = new AlarmQuery();
        ListPagination pagination = new ListPagination();
        pagination.setListSize(100);
        pagination.setPage(1);
        query.setPagination(pagination);
        query.setDynamic(true);
        AlarmListCriteria criteria = new AlarmListCriteria();
        criteria.setKey(AlarmSearchKey.FROM_DATE);
        criteria.setValue(timestamp.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss Z")));
        query.getAlarmSearchCriteria().add(criteria);

        AlarmListResponseDto alarmResponse = getWebTarget()
                .path("movement/rest/alarms/list")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getValidJwtToken())
                .post(Entity.json(query), AlarmListResponseDto.class);
        

        assertThat(alarmResponse, CoreMatchers.is(CoreMatchers.notNullValue()));
        List<AlarmReport> alarms = alarmResponse.getAlarmList();
        alarms.sort((a1, a2) -> a1.getUpdated().compareTo(a2.getUpdated()));
        return alarms.get(0);
	}
}
