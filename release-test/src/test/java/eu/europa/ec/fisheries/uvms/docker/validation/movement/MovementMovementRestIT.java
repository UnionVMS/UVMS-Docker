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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.fluent.Request;
import org.junit.Ignore;
import org.junit.Test;

import eu.europa.ec.fisheries.schema.movement.search.v1.MovementAreaAndTimeIntervalCriteria;
import eu.europa.ec.fisheries.schema.movement.search.v1.MovementQuery;
import eu.europa.ec.fisheries.uvms.docker.validation.common.AbstractRestServiceTest;

/**
 * The Class MovementMovementRestIT.
 */

public class MovementMovementRestIT extends AbstractRestServiceTest {

	/**
	 * Gets the list by query test.
	 *
	 * @return the list by query test
	 * @throws Exception
	 *             the exception
	 */
	@Test
	@Ignore
	public void getListByQueryTest() throws Exception {
		final HttpResponse response = Request.Post(BASE_URL + "movement/rest/movement/list")
				.setHeader("Content-Type", "application/json").setHeader("Authorization", getValidJwtToken())
				.bodyByteArray(writeValueAsString(createMovementQuery()).getBytes()).execute().returnResponse();

		Map<String, Object> dataMap = checkSuccessResponseReturnMap(response);
	}

	/**
	 * Creates the movement query.
	 *
	 * @return the movement query
	 */
	private MovementQuery createMovementQuery() {
		return new MovementQuery();
	}

	/**
	 * Gets the minimal list by query test.
	 *
	 * @return the minimal list by query test
	 * @throws Exception
	 *             the exception
	 */
	@Test
	@Ignore
	public void getMinimalListByQueryTest() throws Exception {
		final HttpResponse response = Request.Post(BASE_URL + "movement/rest/movement/list/minimal")
				.setHeader("Content-Type", "application/json").setHeader("Authorization", getValidJwtToken())
				.bodyByteArray(writeValueAsString(createMovementQuery()).getBytes()).execute().returnResponse();

		Map<String, Object> dataMap = checkSuccessResponseReturnMap(response);
	}

	/**
	 * Gets the latest movements by connect ids test.
	 *
	 * @return the latest movements by connect ids test
	 * @throws Exception
	 *             the exception
	 */
	@Test
	@Ignore
	public void getLatestMovementsByConnectIdsTest() throws Exception {
		final HttpResponse response = Request.Post(BASE_URL + "movement/rest/movement/latest")
				.setHeader("Content-Type", "application/json").setHeader("Authorization", getValidJwtToken())
				.bodyByteArray(writeValueAsString(new ArrayList<String>()).getBytes()).execute().returnResponse();

		Map<String, Object> dataMap = checkSuccessResponseReturnMap(response);
	}

	/**
	 * Gets the latest movements test.
	 *
	 * @return the latest movements test
	 * @throws Exception
	 *             the exception
	 */
	@Test
	public void getLatestMovementsTest() throws Exception {
		final HttpResponse response = Request.Get(BASE_URL + "movement/rest/movement/latest/100")
				.setHeader("Content-Type", "application/json").setHeader("Authorization", getValidJwtToken()).execute()
				.returnResponse();

		List dataList = checkSuccessResponseReturnType(response,List.class);
	}

	/**
	 * Gets the by id test.
	 *
	 * @return the by id test
	 * @throws Exception
	 *             the exception
	 */
	@Test
	@Ignore
	public void getByIdTest() throws Exception {
		final HttpResponse response = Request.Get(BASE_URL + "movement/rest/movement/id")
				.setHeader("Content-Type", "application/json").setHeader("Authorization", getValidJwtToken()).execute()
				.returnResponse();

		Map<String, Object> dataMap = checkSuccessResponseReturnMap(response);
	}

	/**
	 * Gets the list movement by area and time interval test.
	 *
	 * @return the list movement by area and time interval test
	 * @throws Exception
	 *             the exception
	 */
	@Test
	public void getListMovementByAreaAndTimeIntervalTest() throws Exception {
		final HttpResponse response = Request.Post(BASE_URL + "movement/rest/movement/listByAreaAndTimeInterval")
				.setHeader("Content-Type", "application/json").setHeader("Authorization", getValidJwtToken())
				.bodyByteArray(writeValueAsString(new MovementAreaAndTimeIntervalCriteria()).getBytes()).execute()
				.returnResponse();

		Map<String, Object> dataMap = checkSuccessResponseReturnMap(response);
	}

}
