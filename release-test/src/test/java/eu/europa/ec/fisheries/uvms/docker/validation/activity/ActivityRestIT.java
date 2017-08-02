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
import org.databene.contiperf.PerfTest;
import org.databene.contiperf.Required;
import org.databene.contiperf.junit.ContiPerfRule;
import org.junit.Rule;
import org.junit.Test;

import eu.europa.ec.fisheries.uvms.docker.validation.common.AbstractRestServiceTest;

/**
 * The Class ConfigRestIT.
 */
@PerfTest(threads = 4, duration = 3000, warmUp = 1000)
@Required(max = 5000, average = 3000, percentile95 = 3500, throughput = 2)
public class ActivityRestIT extends AbstractRestServiceTest {

	/** The i. */
	@Rule
	public ContiPerfRule contiPerfRule = new ContiPerfRule();

	/**
	 * Gets the config search fields test.
	 *
	 * @return the config search fields test
	 * @throws Exception the exception
	 */
	@Test
	public void getAdminConfigTest() throws Exception {
		final HttpResponse response = Request.Get(BASE_URL + "activity/rest/config/admin")
				.setHeader("Content-Type", "application/json").setHeader("scopeName", "EC").setHeader("roleName", "REP_POWER_ROLE").setHeader("Authorization", getValidJwtToken()).execute()
				.returnResponse();
		assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
		final Map<String, Object> data = getJsonMap(response);		
		assertFalse(data.isEmpty());
		
	}


	/**
	 * Gets the configuration test.
	 *
	 * @return the configuration test
	 * @throws Exception the exception
	 */
	@Test
	public void getUserConfigTest() throws Exception {
		final HttpResponse response = Request.Get(BASE_URL + "asset/rest/user")
				.setHeader("Content-Type", "application/json").setHeader("scopeName", "EC").setHeader("roleName", "REP_POWER_ROLE").setHeader("Authorization", getValidJwtToken()).execute()
				.returnResponse();
		assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
		final Map<String, Object> data = getJsonMap(response);
		assertFalse(data.isEmpty());
	}
	
}
