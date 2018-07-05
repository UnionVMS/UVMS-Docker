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
package eu.europa.ec.fisheries.uvms.docker.validation.exchange;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationModule;
import eu.europa.ec.fisheries.schema.exchange.module.v1.GetServiceListRequest;
import eu.europa.ec.fisheries.schema.exchange.plugin.types.v1.PluginType;
import eu.europa.ec.fisheries.schema.exchange.source.v1.GetServiceListResponse;
import eu.europa.ec.fisheries.uvms.docker.validation.common.AbstractRestServiceTest;
import org.apache.http.HttpResponse;
import org.apache.http.client.fluent.Request;
import org.junit.Test;

/**
 * The Class ExchangeRegistryRestIT.
 */
public class ExchangeAPIRestIT extends AbstractRestServiceTest {

	protected static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

	static {
		JaxbAnnotationModule module = new JaxbAnnotationModule();
		// configure as necessary
		OBJECT_MAPPER.registerModule(module);
	}

	/**
	 * Gets the list test.
	 *
	 * @return the list test
	 * @throws Exception the exception
	 */
	@Test
	public void getListTest() throws Exception {
		GetServiceListRequest getServiceListRequest = new GetServiceListRequest();
		getServiceListRequest.getType().add(PluginType.SATELLITE_RECEIVER);

		String json = OBJECT_MAPPER.writeValueAsString(getServiceListRequest);
		final HttpResponse response = Request.Post(getBaseUrl() + "exchange/unsecured/rest/api/serviceList")
				.setHeader("Content-Type", "application/json")
				.bodyByteArray(json.getBytes())
				.execute()
				.returnResponse();

		if(response.getStatusLine().getStatusCode() == 200) {
			GetServiceListResponse answer = OBJECT_MAPPER.readValue(response.getEntity().getContent(), GetServiceListResponse.class);
			assertNotNull(answer);
			assertTrue(answer.getService().size() > 0);
		} else {
			fail("Call to Exchange failed");
		}

	}

}
