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

import java.util.ArrayList;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.fluent.Request;
import org.junit.Ignore;
import org.junit.Test;

import eu.europa.ec.fisheries.uvms.docker.validation.common.AbstractRestServiceTest;
import eu.europa.ec.fisheries.uvms.reporting.service.dto.rules.AlarmMovement;
import eu.europa.ec.fisheries.uvms.reporting.service.dto.rules.AlarmMovementList;

/**
 * The Class AlarmRestIT.
 */

public class AlarmRestIT extends AbstractRestServiceTest {

	@Test
	@Ignore
	public void getAlarmsTest() throws Exception {
		AlarmMovementList alarmMovementList = new AlarmMovementList();
		ArrayList<AlarmMovement> alarmMovementListContent = new ArrayList<AlarmMovement>();
		alarmMovementList.setAlarmMovementList(alarmMovementListContent);

		final HttpResponse response = Request.Post(getBaseUrl() + "reporting/rest/alarms")
				.setHeader("Content-Type", "application/json").setHeader("Authorization", getValidJwtToken())
				.bodyByteArray(writeValueAsString(alarmMovementList).getBytes()).execute().returnResponse();
		Map<String, Object> dataMap = checkSuccessResponseReturnMap(response);
	}

}
