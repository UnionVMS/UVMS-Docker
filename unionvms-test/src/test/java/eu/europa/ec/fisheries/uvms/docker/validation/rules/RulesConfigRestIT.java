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

import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.fluent.Request;
import org.junit.Test;
import org.junit.Ignore;

import eu.europa.ec.fisheries.uvms.docker.validation.common.AbstractRestServiceTest;

/**
 * The Class RulesConfigRestIT.
 */
@Ignore
public class RulesConfigRestIT extends AbstractRestServiceTest {

	/**
	 * Gets the config test.
	 *
	 * @return the config test
	 * @throws Exception the exception
	 */
	@Test
	public void getConfigTest() throws Exception {
		final HttpResponse response = Request.Get(getBaseUrl() + "movement-rules/rest/config")
				.setHeader("Content-Type", "application/json").setHeader("Authorization", getValidJwtToken()).execute().returnResponse();
		Map<String, Object> dataMap = checkSuccessResponseReturnMap(response);
	}

	
	/**
	 * Gets the alarm statuses test.
	 *
	 * @return the alarm statuses test
	 * @throws Exception the exception
	 */
	@Test
	public void getAlarmStatusesTest() throws Exception {
		final HttpResponse response = Request.Get(getBaseUrl() + "movement-rules/rest/config/alarmstatus")
				.setHeader("Content-Type", "application/json").setHeader("Authorization", getValidJwtToken()).execute().returnResponse();
		List dataList = checkSuccessResponseReturnType(response,List.class);
	}
	
	/**
	 * Gets the ticket statuses test.
	 *
	 * @return the ticket statuses test
	 * @throws Exception the exception
	 */
	@Test
	public void getTicketStatusesTest() throws Exception {
		final HttpResponse response = Request.Get(getBaseUrl() + "movement-rules/rest/config/ticketstatus")
				.setHeader("Content-Type", "application/json").setHeader("Authorization", getValidJwtToken()).execute().returnResponse();
		List dataList = checkSuccessResponseReturnType(response,List.class);
	}

}
