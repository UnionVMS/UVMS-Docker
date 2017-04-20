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
package eu.europa.ec.fisheries.uvms.docker.validation.common;

import java.io.IOException;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.fluent.Request;
import org.junit.Assert;
import org.junit.BeforeClass;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.MapType;


/**
 * The Class AbstractRestServiceTest.
 */
public abstract class AbstractRestServiceTest extends Assert {

	/** The Constant BASE_URL. */
	protected static final String BASE_URL = "http://localhost:28080/";

	/** The valid jwt token. */
	private static String validJwtToken;

	/** The Constant OBJECT_MAPPER. */
	private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();


	/**
	 * Aquire jwt token for test.
	 *
	 * @throws Exception the exception
	 */
	@BeforeClass
	public static void aquireJwtTokenForTest() throws Exception {
		final HttpResponse response = Request.Post(BASE_URL + "usm-administration/rest/authenticate")
				.setHeader("Content-Type", "application/json")
				.bodyByteArray("{\"userName\":\"vms_admin_com\",\"password\":\"password\"}".getBytes()).execute()
				.returnResponse();

		assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
		final Map<String, Object> data = getJsonMap(response);
		assertEquals(true, data.get("authenticated"));
		validJwtToken = (String) data.get("jwtoken");
	}

	/**
	 * Gets the valid jwt token.
	 *
	 * @return the valid jwt token
	 */
	protected final String getValidJwtToken() {
		return validJwtToken;
	}

	/**
	 * Write value as string.
	 *
	 * @param value the value
	 * @return the string
	 * @throws JsonProcessingException the json processing exception
	 */
	protected final String writeValueAsString(final Object value) throws JsonProcessingException {
		return OBJECT_MAPPER.writeValueAsString(value);
	}

	/**
	 * Gets the json map.
	 *
	 * @param response the response
	 * @return the json map
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws JsonParseException the json parse exception
	 * @throws JsonMappingException the json mapping exception
	 * @throws ClientProtocolException the client protocol exception
	 */
	protected final static Map<String, Object> getJsonMap(final HttpResponse response)
			throws IOException, JsonParseException, JsonMappingException, ClientProtocolException {
		final ObjectMapper mapper = new ObjectMapper();
		final MapType type = mapper.getTypeFactory().constructMapType(Map.class, String.class, Object.class);
		final Map<String, Object> data = mapper.readValue( response.getEntity().getContent(), type);
		return data;
	}

}
