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

import java.util.List;
import java.util.Map;
import org.apache.http.HttpResponse;
import org.apache.http.client.fluent.Request;
import org.junit.Test;
import eu.europa.ec.fisheries.uvms.docker.validation.common.AbstractRest;

/**
 * The Class MovementConfigRestIT.
 */

public class MovementConfigRestIT extends AbstractRest {

	/**
	 * Gets the movement types test.
	 *
	 * @return the movement types test
	 * @throws Exception
	 *             the exception
	 */
	@Test
	public void getMovementTypesTest() throws Exception {
		final HttpResponse response = Request.Get(getBaseUrl() + "movement/rest/config/movementTypes")
				.setHeader("Content-Type", "application/json").setHeader("Authorization", getValidJwtToken()).execute()
				.returnResponse();

		List dataList = checkSuccessResponseReturnType(response,List.class);
	}

	/**
	 * Gets the segmet types test.
	 *
	 * @return the segmet types test
	 * @throws Exception
	 *             the exception
	 */
	@Test
	public void getSegmentTypesTest() throws Exception {
		final HttpResponse response = Request.Get(getBaseUrl() + "movement/rest/config/segmentCategoryTypes")
				.setHeader("Content-Type", "application/json").setHeader("Authorization", getValidJwtToken()).execute()
				.returnResponse();

		List dataList = checkSuccessResponseReturnType(response,List.class);

	}

	/**
	 * Gets the movement search keys test.
	 *
	 * @return the movement search keys test
	 * @throws Exception
	 *             the exception
	 */
	@Test
	public void getMovementSearchKeysTest() throws Exception {
		final HttpResponse response = Request.Get(getBaseUrl() + "movement/rest/config/searchKeys")
				.setHeader("Content-Type", "application/json").setHeader("Authorization", getValidJwtToken()).execute()
				.returnResponse();

		List dataList = checkSuccessResponseReturnType(response,List.class);
	}

	/**
	 * Gets the movement source types test.
	 *
	 * @return the movement source types test
	 * @throws Exception
	 *             the exception
	 */
	@Test
	public void getMovementSourceTypesTest() throws Exception {
		final HttpResponse response = Request.Get(getBaseUrl() + "movement/rest/config/movementSourceTypes")
				.setHeader("Content-Type", "application/json").setHeader("Authorization", getValidJwtToken()).execute()
				.returnResponse();

		List dataList = checkSuccessResponseReturnType(response,List.class);
	}

	/**
	 * Gets the activity types test.
	 *
	 * @return the activity types test
	 * @throws Exception
	 *             the exception
	 */
	@Test
	public void getActivityTypesTest() throws Exception {
		final HttpResponse response = Request.Get(getBaseUrl() + "movement/rest/config/activityTypes")
				.setHeader("Content-Type", "application/json").setHeader("Authorization", getValidJwtToken()).execute()
				.returnResponse();

		List dataList = checkSuccessResponseReturnType(response,List.class);
	}

	/**
	 * Gets the configuration test.
	 *
	 * @return the configuration test
	 * @throws Exception
	 *             the exception
	 */
	@Test
	public void getConfigurationTest() throws Exception {
		final HttpResponse response = Request.Get(getBaseUrl() + "movement/rest/config/")
				.setHeader("Content-Type", "application/json").setHeader("Authorization", getValidJwtToken()).execute()
				.returnResponse();

		Map<String, Object> dataMap = checkSuccessResponseReturnMap(response);
	}

}
