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

import eu.europa.ec.fisheries.schema.movement.search.v1.MovementSearchGroup;
import eu.europa.ec.fisheries.uvms.docker.validation.common.AbstractRestServiceTest;


/**
 * The Class MovementPerformanceIT.
 */
@PerfTest(threads = 4, duration = 3000, warmUp = 1000)
@Required(max = 5000, average = 3000, percentile95 = 3500, throughput = 2)
public class MovementMovementSearchRestIT extends AbstractRestServiceTest {

	/** The i. */
	@Rule
	public ContiPerfRule contiPerfRule = new ContiPerfRule();

	/**
	 * Creates the movement search group test.
	 *
	 * @throws Exception the exception
	 */
	@Test
	public void createMovementSearchGroupTest() throws Exception {
		final HttpResponse response = Request.Post(BASE_URL + "movement/rest/search/group")
				.setHeader("Content-Type", "application/json").setHeader("Authorization", getValidJwtToken())
				.bodyByteArray(writeValueAsString(createMovementSearchGroup()).getBytes()).execute()
				.returnResponse();

		assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
		final Map<String, Object> data = getJsonMap(response);
		assertFalse(data.isEmpty());
		assertNotNull(data.get("data"));		
	}

	/**
	 * Creates the movement search group.
	 *
	 * @return the movement search group
	 */
	public MovementSearchGroup createMovementSearchGroup() {
		return new MovementSearchGroup();
	}

	/**
	 * Gets the movement search group test.
	 *
	 * @return the movement search group test
	 * @throws Exception the exception
	 */
	@Test
	@Ignore
	public void getMovementSearchGroupTest() throws Exception {
		final HttpResponse response = Request.Get(BASE_URL + "movement/rest/search/group/{id}")
				.setHeader("Content-Type", "application/json").setHeader("Authorization", getValidJwtToken()).execute()
				.returnResponse();

		assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
		final Map<String, Object> data = getJsonMap(response);
		assertFalse(data.isEmpty());
		assertNotNull(data.get("data"));		
	}

	/**
	 * Update movement seach group test.
	 *
	 * @throws Exception the exception
	 */
	@Test
	public void updateMovementSeachGroupTest() throws Exception {
		final HttpResponse response = Request.Put(BASE_URL + "movement/rest/search/group")
				.setHeader("Content-Type", "application/json").setHeader("Authorization", getValidJwtToken())
				.bodyByteArray(writeValueAsString(createMovementSearchGroup()).getBytes()).execute()
				.returnResponse();

		assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
		final Map<String, Object> data = getJsonMap(response);
		assertFalse(data.isEmpty());
		assertNotNull(data.get("data"));		
	}

	/**
	 * Gets the movement search groups by user test.
	 *
	 * @return the movement search groups by user test
	 * @throws Exception the exception
	 */
	@Test
	@Ignore
	public void getMovementSearchGroupsByUserTest() throws Exception {
		final HttpResponse response = Request.Get(BASE_URL + "movement/rest/search/group/?user=usernmae")
				.setHeader("Content-Type", "application/json").setHeader("Authorization", getValidJwtToken()).execute()
				.returnResponse();

		assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
		final Map<String, Object> data = getJsonMap(response);
		assertFalse(data.isEmpty());
		assertNotNull(data.get("data"));		
	}

	/**
	 * Delete movement search group test.
	 *
	 * @throws Exception the exception
	 */
	@Test
	@Ignore
	public void deleteMovementSearchGroupTest() throws Exception {
		final HttpResponse response = Request.Delete(BASE_URL + "movement/rest/search/group/{id}")
				.setHeader("Content-Type", "application/json").setHeader("Authorization", getValidJwtToken()).execute()
				.returnResponse();

		assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
		final Map<String, Object> data = getJsonMap(response);
		assertFalse(data.isEmpty());
		assertNotNull(data.get("data"));		
	}

}
