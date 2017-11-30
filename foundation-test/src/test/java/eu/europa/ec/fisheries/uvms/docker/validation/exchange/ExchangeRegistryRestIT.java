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

import java.io.IOException;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.fluent.Request;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import eu.europa.ec.fisheries.uvms.docker.validation.common.AbstractRestServiceTest;

/**
 * The Class ExchangeRegistryRestIT.
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ExchangeRegistryRestIT extends AbstractRestServiceTest {

	
	/**
	 * Gets the list test.
	 *
	 * @return the list test
	 * @throws Exception the exception
	 */
	@Test
	public void getListTest() throws Exception {
		final HttpResponse response = Request.Get(getBaseUrl() + "exchange/rest/plugin/list")
				.setHeader("Content-Type", "application/json").setHeader("Authorization", getValidJwtToken()).execute()
				.returnResponse();

		List dataList = checkSuccessResponseReturnType(response,List.class);
	}

	/**
	 * Start service test.
	 *
	 * @throws Exception the exception
	 */
	@Test
	public void stopStartServiceTest() throws Exception {
		stopInmarsatPlugin();
		startInmarsatPlugin();
	}

	private void startInmarsatPlugin()
			throws IOException, ClientProtocolException, JsonParseException, JsonMappingException {
		String serviceName = "eu.europa.ec.fisheries.uvms.plugins.inmarsat";
		
		final HttpResponse response = Request.Put(getBaseUrl() + "exchange/rest/plugin/start/" + serviceName)
				.setHeader("Content-Type", "application/json").setHeader("Authorization", getValidJwtToken()).execute()
				.returnResponse();

		Boolean result = checkSuccessResponseReturnType(response,Boolean.class);
		assertTrue(result);
	}

	private void stopInmarsatPlugin()
			throws IOException, ClientProtocolException, JsonParseException, JsonMappingException {
		String serviceName = "eu.europa.ec.fisheries.uvms.plugins.inmarsat";		
		final HttpResponse response = Request.Put(getBaseUrl() + "exchange/rest/plugin/stop/" + serviceName)
				.setHeader("Content-Type", "application/json").setHeader("Authorization", getValidJwtToken()).execute()
				.returnResponse();

		Boolean result = checkSuccessResponseReturnType(response,Boolean.class);
		assertTrue(result);
	}
	
}
