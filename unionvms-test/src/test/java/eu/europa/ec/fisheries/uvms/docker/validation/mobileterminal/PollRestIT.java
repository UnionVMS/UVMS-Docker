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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.fluent.Request;
import org.junit.Test;

import eu.europa.ec.fisheries.schema.mobileterminal.polltypes.v1.PollListQuery;
import eu.europa.ec.fisheries.schema.mobileterminal.polltypes.v1.PollSearchCriteria;
import eu.europa.ec.fisheries.schema.mobileterminal.polltypes.v1.PollableQuery;
import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.ListPagination;
import eu.europa.ec.fisheries.uvms.asset.client.model.Asset;
import eu.europa.ec.fisheries.uvms.docker.validation.asset.AssetTestHelper;

/**
 * The Class PollRestIT.
 */

public class PollRestIT extends AbstractMobileTerminalTest {

	/**
	 * Gets the areas test.
	 *
	 * @return the areas test
	 * @throws Exception
	 *             the exception
	 */
	@Test
	public void getRunningProgramPollsTest() throws Exception {
		final HttpResponse response = Request.Get(getBaseUrl() + "mobileterminal/rest/poll/running")
				.setHeader("Content-Type", "application/json").setHeader("Authorization", getValidJwtToken()).execute()
				.returnResponse();
		List dataList = checkSuccessResponseReturnType(response, List.class);
	}

	/**
	 * Creates the poll test.
	 *
	 * @throws Exception
	 *             the exception
	 */
	@Test
	public void createPollTest() throws Exception {
		Asset testAsset = AssetTestHelper.createTestAsset();
		Map<String, Object> programPollDataMap = MobileTerminalTestHelper.createPoll_Helper(testAsset);
	}

	/**
	 * Start program poll test.
	 *
	 * @throws Exception
	 *             the exception
	 */
	@Test
	public void startProgramPollTest() throws Exception {
		Asset testAsset = AssetTestHelper.createTestAsset();
		Map<String, Object> programPollDataMap = MobileTerminalTestHelper.createPoll_Helper(testAsset);
		ArrayList sendPolls = (ArrayList) programPollDataMap.get("sentPolls");
		String uid = (String) sendPolls.get(0);

		final HttpResponse response = Request.Put(getBaseUrl() + "mobileterminal/rest/poll/start/" + uid)
				.setHeader("Content-Type", "application/json").setHeader("Authorization", getValidJwtToken()).execute()
				.returnResponse();
		Map<String, Object> dataMap = checkSuccessResponseReturnMap(response);
		
		ArrayList values = (ArrayList)dataMap.get("value");
		assertNotNull(values);
		assertTrue(values.size() == 10);
		
	}

	/**
	 * Stop program poll test.
	 *
	 * @throws Exception
	 *             the exception
	 */
	@Test
	public void stopProgramPollTest() throws Exception {
		Asset testAsset = AssetTestHelper.createTestAsset();
		Map<String, Object> programPollDataMap = MobileTerminalTestHelper.createPoll_Helper(testAsset);
		ArrayList sendPolls = (ArrayList) programPollDataMap.get("sentPolls");
		String uid = (String) sendPolls.get(0);

		// start it
		{
			final HttpResponse response = Request.Put(getBaseUrl() + "mobileterminal/rest/poll/start/" + uid)
					.setHeader("Content-Type", "application/json").setHeader("Authorization", getValidJwtToken())
					.execute().returnResponse();
			Map<String, Object> dataMap = checkSuccessResponseReturnMap(response);
		}

		// stop it
		final HttpResponse response = Request.Put(getBaseUrl() + "mobileterminal/rest/poll/stop/" + uid)
				.setHeader("Content-Type", "application/json").setHeader("Authorization", getValidJwtToken()).execute()
				.returnResponse();
		Map<String, Object> dataMap = checkSuccessResponseReturnMap(response);

		ArrayList values = (ArrayList)dataMap.get("value");
		assertNotNull(values);
		assertTrue(values.size() == 10);
	}

	/**
	 * Inactivate program poll test.
	 *
	 * @throws Exception
	 *             the exception
	 */
	@Test
	public void inactivateProgramPollTest() throws Exception {
		Asset testAsset = AssetTestHelper.createTestAsset();
		Map<String, Object> programPollDataMap = MobileTerminalTestHelper.createPoll_Helper(testAsset);
		ArrayList sendPolls = (ArrayList) programPollDataMap.get("sentPolls");
		String uid = (String) sendPolls.get(0);

		// start it
		{
			final HttpResponse response = Request.Put(getBaseUrl() + "mobileterminal/rest/poll/start/" + uid)
					.setHeader("Content-Type", "application/json").setHeader("Authorization", getValidJwtToken())
					.execute().returnResponse();
			Map<String, Object> dataMap = checkSuccessResponseReturnMap(response);
		}


		// inactivate it
		final HttpResponse response = Request.Put(getBaseUrl() + "mobileterminal/rest/poll/inactivate/" + uid)
				.setHeader("Content-Type", "application/json").setHeader("Authorization", getValidJwtToken()).execute()
				.returnResponse();
		Map<String, Object> dataMap = checkSuccessResponseReturnMap(response);
		ArrayList values = (ArrayList)dataMap.get("value");
		assertNotNull(values);
		assertTrue(values.size() == 10);
	}

	/**
	 * Gets the poll by search criteria test.
	 *
	 * @return the poll by search criteria test
	 * @throws Exception
	 *             the exception
	 */
	@Test
	public void getPollBySearchCriteriaTest() throws Exception {
		PollListQuery pollListQuery = new PollListQuery();
		ListPagination pagination = new ListPagination();
		pollListQuery.setPagination(pagination);
		pagination.setListSize(100);
		pagination.setPage(1);

		PollSearchCriteria pollSearchCriteria = new PollSearchCriteria();
		pollListQuery.setPollSearchCriteria(pollSearchCriteria);
		pollSearchCriteria.setIsDynamic(true);

		final HttpResponse response = Request.Post(getBaseUrl() + "mobileterminal/rest/poll/list")
				.setHeader("Content-Type", "application/json").setHeader("Authorization", getValidJwtToken())
				.bodyByteArray(writeValueAsString(pollListQuery).getBytes()).execute().returnResponse();

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
	public void getPollableChannelsTest() throws Exception {
		PollableQuery pollableQuery = new PollableQuery();
		ListPagination listPagination = new ListPagination();
		listPagination.setListSize(100);
		listPagination.setPage(1);
		pollableQuery.setPagination(listPagination);
		pollableQuery.getConnectIdList().add("connectId");

		final HttpResponse response = Request.Post(getBaseUrl() + "mobileterminal/rest/poll/pollable")
				.setHeader("Content-Type", "application/json").setHeader("Authorization", getValidJwtToken())
				.bodyByteArray(writeValueAsString(pollableQuery).getBytes()).execute().returnResponse();

		Map<String, Object> dataMap = checkSuccessResponseReturnMap(response);
	}

}