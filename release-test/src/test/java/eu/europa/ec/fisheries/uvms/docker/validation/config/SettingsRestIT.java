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
package eu.europa.ec.fisheries.uvms.docker.validation.config;

import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.fluent.Request;
import org.databene.contiperf.PerfTest;
import org.databene.contiperf.Required;
import org.databene.contiperf.junit.ContiPerfRule;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

import eu.europa.ec.fisheries.schema.config.types.v1.SettingType;
import eu.europa.ec.fisheries.schema.config.types.v1.SettingsCreateQuery;
import eu.europa.ec.fisheries.uvms.docker.validation.common.AbstractRestServiceTest;

/**
 * The Class SettingsRestIT.
 */
@PerfTest(threads = 4, duration = 6000, warmUp = 1000)
@Required(max = 5000, average = 3000, percentile95 = 3500, throughput = 2)
public class SettingsRestIT extends AbstractRestServiceTest {

	/** The i. */
	@Rule
	public ContiPerfRule contiPerfRule = new ContiPerfRule();

	
	
	@Test
	public void getByModuleNameTest() throws Exception {
		final HttpResponse response = Request.Get(BASE_URL + "config/rest/settings?moduleName=audit")
				.setHeader("Content-Type", "application/json").setHeader("Authorization", getValidJwtToken()).execute()
				.returnResponse();
		assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
		final Map<String, Object> data = getJsonMap(response);		
		assertFalse(data.isEmpty());
		assertNotNull(data.get("data"));
		
	}

	@Test
	public void getByIdTest() throws Exception {
		final HttpResponse response = Request.Get(BASE_URL + "config/rest/settings/1")
				.setHeader("Content-Type", "application/json").setHeader("Authorization", getValidJwtToken()).execute()
				.returnResponse();
		assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
		final Map<String, Object> data = getJsonMap(response);		
		assertFalse(data.isEmpty());
		assertNotNull(data.get("data"));
		
	}

	
	@Test
	@Ignore
	public void deleteTest() throws Exception {
		final HttpResponse response = Request.Delete(BASE_URL + "config/rest/settings/{id}")
				.setHeader("Content-Type", "application/json").setHeader("Authorization", getValidJwtToken()).execute()
				.returnResponse();
		assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
		final Map<String, Object> data = getJsonMap(response);		
		assertFalse(data.isEmpty());
		assertNotNull(data.get("data"));
		
	}

	@Test
	@Ignore
	public void updateTest() throws Exception {
		SettingType settingType = new SettingType();
		final HttpResponse response = Request.Put(BASE_URL + "config/rest/settings/{id}")
				.setHeader("Content-Type", "application/json").setHeader("Authorization", getValidJwtToken()).bodyByteArray(writeValueAsString(settingType).getBytes()).execute()
				.returnResponse();
		assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
		final Map<String, Object> data = getJsonMap(response);		
		assertFalse(data.isEmpty());
		assertNotNull(data.get("data"));
		
	}

	@Test
	@Ignore
	public void createTest() throws Exception {
		SettingsCreateQuery settingsCreateQuery = new SettingsCreateQuery();
		
		final HttpResponse response = Request.Post(BASE_URL + "config/rest/settings")
				.setHeader("Content-Type", "application/json").setHeader("Authorization", getValidJwtToken()).bodyByteArray(writeValueAsString(settingsCreateQuery).getBytes()).execute()
				.returnResponse();
		assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
		final Map<String, Object> data = getJsonMap(response);		
		assertFalse(data.isEmpty());
		assertNotNull(data.get("data"));
		
	}

	
	@Test
	public void catalogTest() throws Exception {
		final HttpResponse response = Request.Get(BASE_URL + "config/rest/catalog")
				.setHeader("Content-Type", "application/json").setHeader("Authorization", getValidJwtToken()).execute()
				.returnResponse();
		assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
		final Map<String, Object> data = getJsonMap(response);		
		assertFalse(data.isEmpty());
		assertNotNull(data.get("data"));
		
	}


	@Test
	public void getPingsTest() throws Exception {
		final HttpResponse response = Request.Get(BASE_URL + "config/rest/pings")
				.setHeader("Content-Type", "application/json").setHeader("Authorization", getValidJwtToken()).execute()
				.returnResponse();
		assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
		final Map<String, Object> data = getJsonMap(response);		
		assertFalse(data.isEmpty());
		assertNotNull(data.get("data"));
		
	}


	@Test
	public void getGlobalSettingsTest() throws Exception {
		final HttpResponse response = Request.Get(BASE_URL + "config/rest/globals")
				.setHeader("Content-Type", "application/json").setHeader("Authorization", getValidJwtToken()).execute()
				.returnResponse();
		assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
		final Map<String, Object> data = getJsonMap(response);		
		assertFalse(data.isEmpty());
		assertNotNull(data.get("data"));
		
	}

}
