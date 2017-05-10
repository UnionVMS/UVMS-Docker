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
package eu.europa.ec.fisheries.uvms.docker.validation.movement;

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

import eu.europa.ec.fisheries.uvms.docker.validation.common.AbstractRestServiceTest;


/**
 * The Class MovementConfigRestIT.
 */
@PerfTest(threads = 4, duration = 10000, warmUp = 1000)
@Required(max = 3000, average = 2000, percentile95 = 2500, throughput = 2)
public class MovementConfigRestIT extends AbstractRestServiceTest {

	/** The i. */
	@Rule
	public ContiPerfRule contiPerfRule = new ContiPerfRule();

	/**
	 * Gets the movement types test.
	 *
	 * @return the movement types test
	 * @throws Exception the exception
	 */
	@Test
	@Ignore
	public void getMovementTypesTest() throws Exception {
		final HttpResponse response = Request.Post(BASE_URL + "movement/rest/config/movementTypes")
				.setHeader("Content-Type", "application/json").setHeader("Authorization", getValidJwtToken()).execute()
				.returnResponse();

		assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
		final Map<String, Object> data = getJsonMap(response);
		assertFalse(data.isEmpty());
	}

	/**
	 * Gets the segmet types test.
	 *
	 * @return the segmet types test
	 * @throws Exception the exception
	 */
	@Test
	@Ignore
	public void getSegmetTypesTest() throws Exception {
		final HttpResponse response = Request.Post(BASE_URL + "movement/rest/config/segmentCategoryTypes")
				.setHeader("Content-Type", "application/json").setHeader("Authorization", getValidJwtToken()).execute()
				.returnResponse();

		assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
		final Map<String, Object> data = getJsonMap(response);
		assertFalse(data.isEmpty());
	}

	/**
	 * Gets the movement search keys test.
	 *
	 * @return the movement search keys test
	 * @throws Exception the exception
	 */
	@Test
	@Ignore
	public void getMovementSearchKeysTest() throws Exception {
		final HttpResponse response = Request.Post(BASE_URL + "movement/rest/config/searchKeys")
				.setHeader("Content-Type", "application/json").setHeader("Authorization", getValidJwtToken()).execute()
				.returnResponse();

		assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
		final Map<String, Object> data = getJsonMap(response);
		assertFalse(data.isEmpty());
	}

	/**
	 * Gets the movement source types test.
	 *
	 * @return the movement source types test
	 * @throws Exception the exception
	 */
	@Test
	@Ignore
	public void getMovementSourceTypesTest() throws Exception {
		final HttpResponse response = Request.Post(BASE_URL + "movement/rest/config/movementSourceTypes")
				.setHeader("Content-Type", "application/json").setHeader("Authorization", getValidJwtToken()).execute()
				.returnResponse();

		assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
		final Map<String, Object> data = getJsonMap(response);
		assertFalse(data.isEmpty());
	}

	/**
	 * Gets the activity types test.
	 *
	 * @return the activity types test
	 * @throws Exception the exception
	 */
	@Test
	@Ignore
	public void getActivityTypesTest() throws Exception {
		final HttpResponse response = Request.Post(BASE_URL + "movement/rest/config/activityTypes")
				.setHeader("Content-Type", "application/json").setHeader("Authorization", getValidJwtToken()).execute()
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
	@Ignore
	public void getConfigurationTest() throws Exception {
		final HttpResponse response = Request.Post(BASE_URL + "movement/rest/config/")
				.setHeader("Content-Type", "application/json").setHeader("Authorization", getValidJwtToken()).execute()
				.returnResponse();

		assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
		final Map<String, Object> data = getJsonMap(response);
		assertFalse(data.isEmpty());
	}

}
