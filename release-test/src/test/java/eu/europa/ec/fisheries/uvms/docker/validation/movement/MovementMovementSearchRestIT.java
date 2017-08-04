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
import org.apache.http.client.fluent.Request;
import org.junit.Ignore;
import org.junit.Test;

import eu.europa.ec.fisheries.schema.movement.search.v1.MovementSearchGroup;
import eu.europa.ec.fisheries.uvms.docker.validation.common.AbstractRestServiceTest;

/**
 * The Class MovementPerformanceIT.
 */

public class MovementMovementSearchRestIT extends AbstractRestServiceTest {

	/**
	 * Creates the movement search group test.
	 *
	 * @throws Exception
	 *             the exception
	 */
	@Test
	@Ignore
	public void createMovementSearchGroupTest() throws Exception {
		final HttpResponse response = Request.Post(BASE_URL + "movement/rest/search/group")
				.setHeader("Content-Type", "application/json").setHeader("Authorization", getValidJwtToken())
				.bodyByteArray(writeValueAsString(createMovementSearchGroup()).getBytes()).execute().returnResponse();

		Map<String, Object> dataMap = checkSuccessResponseReturnMap(response);
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
	 * @throws Exception
	 *             the exception
	 */
	@Test
	@Ignore
	public void getMovementSearchGroupTest() throws Exception {
		final HttpResponse response = Request.Get(BASE_URL + "movement/rest/search/group/{id}")
				.setHeader("Content-Type", "application/json").setHeader("Authorization", getValidJwtToken()).execute()
				.returnResponse();

		Map<String, Object> dataMap = checkSuccessResponseReturnMap(response);
	}

	/**
	 * Update movement seach group test.
	 *
	 * @throws Exception
	 *             the exception
	 */
	@Test
	@Ignore
	public void updateMovementSeachGroupTest() throws Exception {
		final HttpResponse response = Request.Put(BASE_URL + "movement/rest/search/group")
				.setHeader("Content-Type", "application/json").setHeader("Authorization", getValidJwtToken())
				.bodyByteArray(writeValueAsString(createMovementSearchGroup()).getBytes()).execute().returnResponse();

		Map<String, Object> dataMap = checkSuccessResponseReturnMap(response);
	}

	/**
	 * Gets the movement search groups by user test.
	 *
	 * @return the movement search groups by user test
	 * @throws Exception
	 *             the exception
	 */
	@Test
	@Ignore
	public void getMovementSearchGroupsByUserTest() throws Exception {
		final HttpResponse response = Request.Get(BASE_URL + "movement/rest/search/group/?user=usernmae")
				.setHeader("Content-Type", "application/json").setHeader("Authorization", getValidJwtToken()).execute()
				.returnResponse();

		Map<String, Object> dataMap = checkSuccessResponseReturnMap(response);
	}

	/**
	 * Delete movement search group test.
	 *
	 * @throws Exception
	 *             the exception
	 */
	@Test
	@Ignore
	public void deleteMovementSearchGroupTest() throws Exception {
		final HttpResponse response = Request.Delete(BASE_URL + "movement/rest/search/group/{id}")
				.setHeader("Content-Type", "application/json").setHeader("Authorization", getValidJwtToken()).execute()
				.returnResponse();

		Map<String, Object> dataMap = checkSuccessResponseReturnMap(response);
	}

}
