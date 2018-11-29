/*
  *
  * Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries © European Union, 2015-2016.
  *
  * This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it
  * and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of
  * the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
  * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
  * details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.
  *
  *
  */
package eu.europa.ec.fisheries.uvms.docker.validation.activity;

import java.util.Map;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.fluent.Request;
import org.junit.Test;
import eu.europa.ec.fisheries.uvms.docker.validation.common.AbstractRest;

/**
 * The Class ActivityRestIT.
 */
public class ActivityRestIT extends AbstractRest {

	/**
	 * Gets the config search fields test.
	 *
	 * @return the config search fields test
	 * @throws Exception
	 *             the exception
	 */
	@Test
	public void getAdminConfigTest() throws Exception {
		final HttpResponse response = Request.Get(BASE_URL + "activity/rest/config/admin")
				.setHeader("Content-Type", "application/json").setHeader("scopeName", "EC")
				.setHeader("roleName", "REP_POWER_ROLE").setHeader("Authorization", getValidJwtToken("rep_power","abcd-1234")).execute()
				.returnResponse();
		assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
		final Map<String, Object> data = getJsonMap(response);
		assertFalse(data.isEmpty());

	}

	/**
	 * Gets the configuration test.
	 *
	 * @return the configuration test
	 * @throws Exception
	 *             the exception
	 */
	@Test
	public void getUserConfigTest() throws Exception {
		final HttpResponse response = Request.Get(BASE_URL + "activity/rest/config/user")
				.setHeader("Content-Type", "application/json").setHeader("scopeName", "EC")
				.setHeader("roleName", "REP_POWER_ROLE").setHeader("Authorization", getValidJwtToken("rep_power","abcd-1234")).execute()
				.returnResponse();
		assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
		final Map<String, Object> data = getJsonMap(response);
		assertFalse(data.isEmpty());
	}

}