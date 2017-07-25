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
package eu.europa.ec.fisheries.uvms.docker.validation.mobileterminal;

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

import eu.europa.ec.fisheries.schema.mobileterminal.polltypes.v1.PollListQuery;
import eu.europa.ec.fisheries.schema.mobileterminal.polltypes.v1.PollRequestType;
import eu.europa.ec.fisheries.schema.mobileterminal.polltypes.v1.PollableQuery;
import eu.europa.ec.fisheries.uvms.docker.validation.common.AbstractRestServiceTest;


/**
 * The Class PollRestIT.
 */
@PerfTest(threads = 4, duration = 3000, warmUp = 1000)
@Required(max = 5000, average = 3000, percentile95 = 3500, throughput = 2)
public class PollRestIT extends AbstractRestServiceTest {

	/** The i. */
	@Rule
	public ContiPerfRule contiPerfRule = new ContiPerfRule();

	/**
	 * Gets the areas test.
	 *
	 * @return the areas test
	 * @throws Exception the exception
	 */
	@Test
	public void getRunningProgramPollsTest() throws Exception {
		final HttpResponse response = Request.Get(BASE_URL + "mobileterminal/rest/poll/running")
				.setHeader("Content-Type", "application/json").setHeader("Authorization",getValidJwtToken()).execute().returnResponse();
		assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
		final Map<String, Object> data = getJsonMap(response);
		assertFalse(data.isEmpty());
		assertNotNull(data.get("data"));		
	}

	/**
	 * Creates the poll test.
	 *
	 * @throws Exception the exception
	 */
	@Test
	public void createPollTest() throws Exception {
		final HttpResponse response = Request.Post(BASE_URL + "mobileterminal/rest/poll")
				.setHeader("Content-Type", "application/json").setHeader("Authorization", getValidJwtToken())
				.bodyByteArray(writeValueAsString(new PollRequestType()).getBytes())
				.execute().returnResponse();

		assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
		final Map<String, Object> data = getJsonMap(response);
		assertFalse(data.isEmpty());
		assertNotNull(data.get("data"));		
	}
	
	/**
	 * Start program poll test.
	 *
	 * @throws Exception the exception
	 */
	@Test
	@Ignore
	public void startProgramPollTest() throws Exception {
		final HttpResponse response = Request.Put(BASE_URL + "mobileterminal/rest/poll/start/{id}")
				.setHeader("Content-Type", "application/json").setHeader("Authorization",getValidJwtToken()).execute().returnResponse();
		assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
		final Map<String, Object> data = getJsonMap(response);
		assertFalse(data.isEmpty());
		assertNotNull(data.get("data"));		
	}

	/**
	 * Stop program poll test.
	 *
	 * @throws Exception the exception
	 */
	@Test
	@Ignore
	public void stopProgramPollTest() throws Exception {
		final HttpResponse response = Request.Put(BASE_URL + "mobileterminal/rest/poll/stop/{id}")
				.setHeader("Content-Type", "application/json").setHeader("Authorization",getValidJwtToken()).execute().returnResponse();
		assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
		final Map<String, Object> data = getJsonMap(response);
		assertFalse(data.isEmpty());
		assertNotNull(data.get("data"));		
	}

	/**
	 * Inactivate program poll test.
	 *
	 * @throws Exception the exception
	 */
	@Test
	@Ignore
	public void inactivateProgramPollTest() throws Exception {
		final HttpResponse response = Request.Put(BASE_URL + "mobileterminal/rest/poll/inactivate/{id}")
				.setHeader("Content-Type", "application/json").setHeader("Authorization",getValidJwtToken()).execute().returnResponse();
		assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
		final Map<String, Object> data = getJsonMap(response);
		assertFalse(data.isEmpty());
		assertNotNull(data.get("data"));		
	}

	
	/**
	 * Gets the poll by search criteria test.
	 *
	 * @return the poll by search criteria test
	 * @throws Exception the exception
	 */
	@Test
	public void getPollBySearchCriteriaTest() throws Exception {
		final HttpResponse response = Request.Post(BASE_URL + "mobileterminal/rest/poll/list")
				.setHeader("Content-Type", "application/json").setHeader("Authorization", getValidJwtToken())
				.bodyByteArray(writeValueAsString(new PollListQuery()).getBytes())
				.execute().returnResponse();

		assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
		final Map<String, Object> data = getJsonMap(response);
		assertFalse(data.isEmpty());
		assertNotNull(data.get("data"));		
	}

	
	/**
	 * Gets the pollable channels test.
	 *
	 * @return the pollable channels test
	 * @throws Exception the exception
	 */
	@Test
	public void getPollableChannelsTest() throws Exception {
		final HttpResponse response = Request.Post(BASE_URL + "mobileterminal/rest/poll/pollable")
				.setHeader("Content-Type", "application/json").setHeader("Authorization", getValidJwtToken())
				.bodyByteArray(writeValueAsString(new PollableQuery()).getBytes())
				.execute().returnResponse();

		assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
		final Map<String, Object> data = getJsonMap(response);
		assertFalse(data.isEmpty());
		assertNotNull(data.get("data"));		
	}

}