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

import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.fluent.Request;
import org.junit.Ignore;
import org.junit.Test;

import eu.europa.ec.fisheries.schema.mobileterminal.polltypes.v1.PollListQuery;
import eu.europa.ec.fisheries.schema.mobileterminal.polltypes.v1.PollRequestType;
import eu.europa.ec.fisheries.schema.mobileterminal.polltypes.v1.PollableQuery;
import eu.europa.ec.fisheries.uvms.docker.validation.common.AbstractRestServiceTest;

/**
 * The Class PollRestIT.
 */

public class PollRestIT extends AbstractRestServiceTest {

	/**
	 * Gets the areas test.
	 *
	 * @return the areas test
	 * @throws Exception
	 *             the exception
	 */
	@Test
	public void getRunningProgramPollsTest() throws Exception {
		final HttpResponse response = Request.Get(BASE_URL + "mobileterminal/rest/poll/running")
				.setHeader("Content-Type", "application/json").setHeader("Authorization", getValidJwtToken()).execute()
				.returnResponse();
		List dataList = checkSuccessResponseReturnType(response,List.class);
	}

	/**
	 * Creates the poll test.
	 *
	 * @throws Exception
	 *             the exception
	 */
	@Test
	@Ignore
	public void createPollTest() throws Exception {
		final HttpResponse response = Request.Post(BASE_URL + "mobileterminal/rest/poll")
				.setHeader("Content-Type", "application/json").setHeader("Authorization", getValidJwtToken())
				.bodyByteArray(writeValueAsString(new PollRequestType()).getBytes()).execute().returnResponse();

		Map<String, Object> dataMap = checkSuccessResponseReturnMap(response);
	}

	/**
	 * Start program poll test.
	 *
	 * @throws Exception
	 *             the exception
	 */
	@Test
	@Ignore
	public void startProgramPollTest() throws Exception {
		final HttpResponse response = Request.Put(BASE_URL + "mobileterminal/rest/poll/start/{id}")
				.setHeader("Content-Type", "application/json").setHeader("Authorization", getValidJwtToken()).execute()
				.returnResponse();
		Map<String, Object> dataMap = checkSuccessResponseReturnMap(response);
	}

	/**
	 * Stop program poll test.
	 *
	 * @throws Exception
	 *             the exception
	 */
	@Test
	@Ignore
	public void stopProgramPollTest() throws Exception {
		final HttpResponse response = Request.Put(BASE_URL + "mobileterminal/rest/poll/stop/{id}")
				.setHeader("Content-Type", "application/json").setHeader("Authorization", getValidJwtToken()).execute()
				.returnResponse();
		Map<String, Object> dataMap = checkSuccessResponseReturnMap(response);
	}

	/**
	 * Inactivate program poll test.
	 *
	 * @throws Exception
	 *             the exception
	 */
	@Test
	@Ignore
	public void inactivateProgramPollTest() throws Exception {
		final HttpResponse response = Request.Put(BASE_URL + "mobileterminal/rest/poll/inactivate/{id}")
				.setHeader("Content-Type", "application/json").setHeader("Authorization", getValidJwtToken()).execute()
				.returnResponse();
		Map<String, Object> dataMap = checkSuccessResponseReturnMap(response);
	}

	/**
	 * Gets the poll by search criteria test.
	 *
	 * @return the poll by search criteria test
	 * @throws Exception
	 *             the exception
	 */
	@Test
	@Ignore
	public void getPollBySearchCriteriaTest() throws Exception {
		final HttpResponse response = Request.Post(BASE_URL + "mobileterminal/rest/poll/list")
				.setHeader("Content-Type", "application/json").setHeader("Authorization", getValidJwtToken())
				.bodyByteArray(writeValueAsString(new PollListQuery()).getBytes()).execute().returnResponse();

		Map<String, Object> dataMap = checkSuccessResponseReturnMap(response);
	}

	/**
	 * Gets the pollable channels test.
	 *
	 * @return the pollable channels test
	 * @throws Exception
	 *             the exception
	 */
	@Test
	@Ignore
	public void getPollableChannelsTest() throws Exception {
		final HttpResponse response = Request.Post(BASE_URL + "mobileterminal/rest/poll/pollable")
				.setHeader("Content-Type", "application/json").setHeader("Authorization", getValidJwtToken())
				.bodyByteArray(writeValueAsString(new PollableQuery()).getBytes()).execute().returnResponse();

		Map<String, Object> dataMap = checkSuccessResponseReturnMap(response);
	}

}