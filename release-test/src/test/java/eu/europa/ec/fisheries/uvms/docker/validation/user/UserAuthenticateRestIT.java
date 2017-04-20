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
package eu.europa.ec.fisheries.uvms.docker.validation.user;

import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.fluent.Request;
import org.junit.Test;

import eu.europa.ec.fisheries.uvms.docker.validation.common.AbstractRestServiceTest;


/**
 * The Class UserAuthenticateRestIT.
 */
public class UserAuthenticateRestIT extends AbstractRestServiceTest {

	/** The base url. */
	private final String BASE_URL = "http://localhost:28080/";

	/**
	 * Authenticate get jwt token success test.
	 *
	 * @throws Exception the exception
	 */
	@Test
	public void authenticateGetJwtTokenSuccessTest() throws Exception {
		final HttpResponse response = Request.Post(BASE_URL + "usm-administration/rest/authenticate")
				.setHeader("Content-Type", "application/json")
				.bodyByteArray("{\"userName\":\"vms_admin_com\",\"password\":\"password\"}".getBytes()).execute().returnResponse();

		assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
		final Map<String, Object> data = getJsonMap(response);
		assertEquals(true, data.get("authenticated"));
		assertNotNull(data.get("jwtoken"));
	}

	/**
	 * Authenticate get jwt token failure test.
	 *
	 * @throws Exception the exception
	 */
	@Test
	public void authenticateGetJwtTokenFailureTest() throws Exception {
		final HttpResponse response = Request.Post(BASE_URL + "usm-administration/rest/authenticate")
				.setHeader("Content-Type", "application/json")
				.bodyByteArray("{\"userName\":\"vms_admin_com\",\"password\":\"invalidpassword\"}".getBytes()).execute().returnResponse();

		assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
		final Map<String, Object> data = getJsonMap(response);
		assertEquals(false, data.get("authenticated"));
		assertNull(data.get("jwtoken"));
	}

}
