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
package eu.europa.ec.fisheries.uvms.docker.validation.spatial;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.fluent.Request;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import eu.europa.ec.fisheries.uvms.docker.validation.common.AbstractRestServiceTest;

/**
 * The Class SpatialConfigRestIT.
 */
public class SpatialConfigRestIT extends AbstractRestServiceTest {

	private ObjectMapper mapper = new ObjectMapper();

	@Test
	public void getReportMapConfig() throws Exception {

		String uid = "rep_power";
		String pwd = "abcd-1234";
		String token = getValidJwtToken(uid, pwd);
		String scopeName = "EC";
		String roleName = "rep_power_role";

		String id = "5";

		ConfigResourceDto dto = new ConfigResourceDto();
		dto.setTimeStamp(new Date().toString());
		String theDto = mapper.writeValueAsString(dto);

		// @formatter:off
		final HttpResponse response = Request.Post(getBaseUrl() + "spatial/rest/config/" + id)
				.setHeader("Content-Type", "application/json")
				.setHeader("Authorization", token)
				.setHeader(AuthConstants.HTTP_HEADER_SCOPE_NAME, scopeName)
				.setHeader(AuthConstants.HTTP_HEADER_ROLE_NAME, roleName)
				.bodyByteArray(theDto.getBytes())
				.execute()
				.returnResponse();
		// @formatter:on

		assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
		final Map<String, Object> data = getJsonMap(response);

		// for now
		assertTrue(data != null);
		assertTrue(data.size() > 0);

	}

	@Test
	public void getBasicReportMapConfig() throws Exception {

		String uid = "rep_power";
		String pwd = "abcd-1234";
		String token = getValidJwtToken(uid, pwd);
		String scopeName = "EC";
		String roleName = "rep_power_role";

		// @formatter:off
		final HttpResponse response = Request.Get(getBaseUrl() + "spatial/rest/config/basic")
				.setHeader("Content-Type", "application/json")
				.setHeader("Authorization", token)
				.setHeader(AuthConstants.HTTP_HEADER_SCOPE_NAME, scopeName)
				.setHeader(AuthConstants.HTTP_HEADER_ROLE_NAME, roleName)
				.execute()
				.returnResponse();
		// @formatter:on

		assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
		final Map<String, Object> data = getJsonMap(response);

		// for now
		assertTrue(data != null);
		assertTrue(data.size() > 0);

	}
	
	
	
	@Test
	public void getReportMapConfigWithoutSave() throws Exception {

		String uid = "rep_power";
		String pwd = "abcd-1234";
		String token = getValidJwtToken(uid, pwd);
		String scopeName = "EC";
		String roleName = "rep_power_role";

		String id = "5";

		ConfigResourceDto dto = new ConfigResourceDto();
		dto.setTimeStamp(new Date().toString());
		String theDto = mapper.writeValueAsString(dto);

		// @formatter:off
		final HttpResponse response = Request.Post(getBaseUrl() + "spatial/rest/fromreport" )
				.setHeader("Content-Type", "application/json")
				.setHeader("Authorization", token)
				.setHeader(AuthConstants.HTTP_HEADER_SCOPE_NAME, scopeName)
				.setHeader(AuthConstants.HTTP_HEADER_ROLE_NAME, roleName)
				.bodyByteArray(theDto.getBytes())
				.execute()
				.returnResponse();
		// @formatter:on

		assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
		final Map<String, Object> data = getJsonMap(response);

		// for now
		assertTrue(data != null);
		assertTrue(data.size() > 0);

	}
	
	
	

	/**
	 * Gets the all projections test.
	 *
	 * @return the all projections test
	 * @throws Exception
	 *             the exception
	 */
	@Test
	public void getAllProjectionsTest() throws Exception {

		String uid = "rep_power";
		String pwd = "abcd-1234";
		String token = getValidJwtToken(uid, pwd);
		String scopeName = "EC";
		String roleName = "rep_power_role";

		// @formatter:off
		final HttpResponse response = Request.Get(getBaseUrl() + "spatial/rest/config/projections")
				.setHeader("Content-Type", "application/json")
				.setHeader("Authorization", token)
				.setHeader(AuthConstants.HTTP_HEADER_SCOPE_NAME, scopeName)
				.setHeader(AuthConstants.HTTP_HEADER_ROLE_NAME, roleName)
				.execute()
				.returnResponse();
		// @formatter:on

		List dataList = checkSuccessResponseReturnType(response, List.class);

		// for now
		assertTrue(dataList != null);
		assertTrue(dataList.size() > 0);

	}

	@Test
	public void admin() throws Exception {

		String uid = "rep_power";
		String pwd = "abcd-1234";
		String token = getValidJwtToken(uid, pwd);
		String scopeName = "EC";
		String roleName = "rep_power_role";

		// @formatter:off
		final HttpResponse response = Request.Get(getBaseUrl() + "spatial/rest/config/admin")
				.setHeader("Content-Type", "application/json")
				.setHeader("Authorization", token)
				.setHeader(AuthConstants.HTTP_HEADER_SCOPE_NAME, scopeName)
				.setHeader(AuthConstants.HTTP_HEADER_ROLE_NAME, roleName)
				.execute()
				.returnResponse();
		// @formatter:on

		assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
		final Map<String, Object> data = getJsonMap(response);

		// for now
		assertTrue(data != null);
		assertTrue(data.size() > 0);
	}

}
