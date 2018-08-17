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
package eu.europa.ec.fisheries.uvms.docker.validation.rules;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.apache.http.HttpResponse;
import org.apache.http.client.fluent.Request;
import org.hamcrest.CoreMatchers;
import org.junit.Ignore;
import org.junit.Test;
import eu.europa.ec.fisheries.schema.movementrules.alarm.v1.AlarmReportType;
import eu.europa.ec.fisheries.schema.movementrules.module.v1.GetAlarmListByQueryResponse;
import eu.europa.ec.fisheries.schema.movementrules.search.v1.AlarmListCriteria;
import eu.europa.ec.fisheries.schema.movementrules.search.v1.AlarmQuery;
import eu.europa.ec.fisheries.schema.movementrules.search.v1.AlarmSearchKey;
import eu.europa.ec.fisheries.schema.movementrules.search.v1.ListPagination;
import eu.europa.ec.fisheries.uvms.asset.client.model.Asset;
import eu.europa.ec.fisheries.uvms.docker.validation.asset.AssetTestHelper;
import eu.europa.ec.fisheries.uvms.docker.validation.common.AbstractRestServiceTest;
import eu.europa.ec.fisheries.uvms.docker.validation.movement.LatLong;
import eu.europa.ec.fisheries.uvms.docker.validation.system.helper.NAFHelper;

/**
 * The Class RulesAlarmRestIT.
 */

public class RulesAlarmRestIT extends AbstractRestServiceTest {

	/**
	 * Gets the number of open alarm reports test.
	 *
	 * @return the number of open alarm reports test
	 * @throws Exception the exception
	 */
	@Test
	public void getNumberOfOpenAlarmReportsTest() throws Exception {
		final HttpResponse response = Request.Get(getBaseUrl() + "movement-rules/rest/alarms/countopen")
				.setHeader("Content-Type", "application/json").setHeader("Authorization", getValidJwtToken()).execute().returnResponse();
		Integer openReportsNumber = checkSuccessResponseReturnType(response,Integer.class);
		assertNotNull(openReportsNumber);
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

		final HttpResponse response = Request.Post(getBaseUrl() + "movement-rules/rest/alarms/list")
				.setHeader("Content-Type", "application/json").setHeader("Authorization", getValidJwtToken()).bodyByteArray(writeValueAsString(alarmQuery).getBytes()).execute().returnResponse();
		Map<String, Object> dataMap = checkSuccessResponseReturnMap(response);
	}


	/**
	 * Update alarm status test.
	 *
	 * @throws Exception the exception
	 */
	//Nothing to update, changing to expect a server error
	@Test
	public void updateAlarmStatusTest() throws Exception {
		AlarmReportType alarmReportType = new AlarmReportType();
		final HttpResponse response = Request.Put(getBaseUrl() + "movement-rules/rest/alarms")
				.setHeader("Content-Type", "application/json").setHeader("Authorization", getValidJwtToken()).bodyByteArray(writeValueAsString(alarmReportType).getBytes()).execute().returnResponse();
		checkErrorResponse(response);
	}

	
	/**
	 * Gets the alarm report by guid test.
	 *
	 * @return the alarm report by guid test
	 * @throws Exception the exception
	 */
	//Gets an alarm report by guid, sadly we dont have any AR in the DB, so this is an automatic 500. You could argue that it should be a not found or something like that but that is not how it is right now
	@Test
	public void getAlarmReportByGuidTest() throws Exception {
		final HttpResponse response = Request.Get(getBaseUrl() + "movement-rules/rest/alarms/guid")
				.setHeader("Content-Type", "application/json").setHeader("Authorization", getValidJwtToken()).execute().returnResponse();
		checkErrorResponse(response);
	}

	
	/**
	 * Reprocess alarm test.
	 *
	 * @throws Exception the exception
	 */
	//Call to reprocess alarms, sadly we dont have any alarm reports to reprocess so 500......
	@Test
	public void reprocessAlarmTest() throws Exception {
		final HttpResponse response = Request.Post(getBaseUrl() + "movement-rules/rest/alarms/reprocess")
				.setHeader("Content-Type", "application/json").setHeader("Authorization", getValidJwtToken()).bodyByteArray(writeValueAsString(new ArrayList<String>()).getBytes()).execute().returnResponse();
		checkErrorResponse(response);
	}
	
	@Test
	public void getAlarmReportVerifyRawMovementDataTest() throws Exception {
        ZonedDateTime timestamp = ZonedDateTime.now(ZoneOffset.UTC);

        Asset asset = AssetTestHelper.createBasicAsset();
        NAFHelper.sendPositionToNAFPlugin(new LatLong(56d, 11d, new Date()), asset);

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

        HttpResponse response = Request.Post(getBaseUrl() + "movement-rules/rest/alarms/list")
                .setHeader("Content-Type", "application/json").setHeader("Authorization", getValidJwtToken())
                .bodyByteArray(writeValueAsString(query).getBytes()).execute().returnResponse();

        GetAlarmListByQueryResponse alarmResponse = checkSuccessResponseReturnObject(response,GetAlarmListByQueryResponse.class);
        assertThat(alarmResponse, CoreMatchers.is(CoreMatchers.notNullValue()));
        List<AlarmReportType> alarms = alarmResponse.getAlarms();
        assertThat(alarms.size(), CoreMatchers.is(1));
        AlarmReportType alarm = alarms.get(0);
        assertThat(alarm.getRawMovement(), CoreMatchers.is(CoreMatchers.notNullValue()));
        assertThat(alarm.getRawMovement().getAssetName(), CoreMatchers.is(asset.getName()));
	}
}
