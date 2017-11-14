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

import java.io.IOException;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.fluent.Request;

import com.fasterxml.jackson.databind.ObjectMapper;

import eu.europa.ec.fisheries.uvms.docker.validation.common.AbstractRestServiceTest;

/**
 * UserHelper
 */
public class UserHelper extends AbstractRestServiceTest {

	private ObjectMapper MAPPER = new ObjectMapper();

	public Map<String, Object> authenticate(String user, String password) throws ClientProtocolException, IOException {
		
		UserPwd userPwd = new UserPwd(user,password);
		String jsonUserPwd = MAPPER.writeValueAsString(userPwd);
		final HttpResponse response = Request.Post(getBaseUrl() + "usm-administration/rest/authenticate")
				.setHeader("Content-Type", "application/json")
				.bodyByteArray(jsonUserPwd.getBytes()).execute()
				.returnResponse();

		assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
		final Map<String, Object> data = getJsonMap(response);
		return data;

	}
	
	
	public Map<String, Object> getChallenge(String jwtoken) throws ClientProtocolException, IOException{
		
		final HttpResponse response = Request.Get(getBaseUrl() + "usm-administration/rest/challenge")
				.setHeader("Content-Type", "application/json").setHeader("authorization", jwtoken).execute()
				.returnResponse();
		assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
		final Map<String, Object> data = getJsonMap(response);
		assertNotNull(data.get("challenge"));
		assertEquals("vms_admin_com", data.get("userName"));
		return data;
	}
	

}
