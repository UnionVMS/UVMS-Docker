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
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.fluent.Request;
import org.junit.Test;

import eu.europa.ec.fisheries.schema.mobileterminal.polltypes.v1.PollAttribute;
import eu.europa.ec.fisheries.schema.mobileterminal.polltypes.v1.PollAttributeType;
import eu.europa.ec.fisheries.schema.mobileterminal.polltypes.v1.PollListQuery;
import eu.europa.ec.fisheries.schema.mobileterminal.polltypes.v1.PollMobileTerminal;
import eu.europa.ec.fisheries.schema.mobileterminal.polltypes.v1.PollRequestType;
import eu.europa.ec.fisheries.schema.mobileterminal.polltypes.v1.PollSearchCriteria;
import eu.europa.ec.fisheries.schema.mobileterminal.polltypes.v1.PollType;
import eu.europa.ec.fisheries.schema.mobileterminal.polltypes.v1.PollableQuery;
import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.ListPagination;
import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.MobileTerminalAssignQuery;
import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.MobileTerminalAttribute;
import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.MobileTerminalType;
import eu.europa.ec.fisheries.wsdl.asset.types.Asset;

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
		final HttpResponse response = Request.Get(BASE_URL + "mobileterminal/rest/poll/running")
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

		Map<String, Object> dataMap = createPoll_Helper();
	}

	/**
	 * Start program poll test.
	 *
	 * @throws Exception
	 *             the exception
	 */
	@Test
	public void startProgramPollTest() throws Exception {

		Map<String, Object> programPollDataMap = createPoll_Helper();
		ArrayList sendPolls = (ArrayList) programPollDataMap.get("sentPolls");
		String uid = (String) sendPolls.get(0);

		final HttpResponse response = Request.Put(BASE_URL + "mobileterminal/rest/poll/start/" + uid)
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

		// create a program poll
		Map<String, Object> programPollDataMap = createPoll_Helper();
		ArrayList sendPolls = (ArrayList) programPollDataMap.get("sentPolls");
		String uid = (String) sendPolls.get(0);

		// start it
		{
			final HttpResponse response = Request.Put(BASE_URL + "mobileterminal/rest/poll/start/" + uid)
					.setHeader("Content-Type", "application/json").setHeader("Authorization", getValidJwtToken())
					.execute().returnResponse();
			Map<String, Object> dataMap = checkSuccessResponseReturnMap(response);
		}

		// stop it
		final HttpResponse response = Request.Put(BASE_URL + "mobileterminal/rest/poll/stop/" + uid)
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
		
		// create a program poll
		Map<String, Object> programPollDataMap = createPoll_Helper();
		ArrayList sendPolls = (ArrayList) programPollDataMap.get("sentPolls");
		String uid = (String) sendPolls.get(0);

		// start it
		{
			final HttpResponse response = Request.Put(BASE_URL + "mobileterminal/rest/poll/start/" + uid)
					.setHeader("Content-Type", "application/json").setHeader("Authorization", getValidJwtToken())
					.execute().returnResponse();
			Map<String, Object> dataMap = checkSuccessResponseReturnMap(response);
		}


		// inactivate it
		final HttpResponse response = Request.Put(BASE_URL + "mobileterminal/rest/poll/inactivate/" + uid)
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

		final HttpResponse response = Request.Post(BASE_URL + "mobileterminal/rest/poll/list")
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

		final HttpResponse response = Request.Post(BASE_URL + "mobileterminal/rest/poll/pollable")
				.setHeader("Content-Type", "application/json").setHeader("Authorization", getValidJwtToken())
				.bodyByteArray(writeValueAsString(pollableQuery).getBytes()).execute().returnResponse();

		Map<String, Object> dataMap = checkSuccessResponseReturnMap(response);
	}

	private Map<String, Object> createPoll_Helper() throws Exception {
		Asset testAsset = createTestAsset();
		MobileTerminalType createdMobileTerminalType = createMobileTerminalType();

		{
			MobileTerminalAssignQuery mobileTerminalAssignQuery = new MobileTerminalAssignQuery();
			mobileTerminalAssignQuery.setMobileTerminalId(createdMobileTerminalType.getMobileTerminalId());
			mobileTerminalAssignQuery.setConnectId(testAsset.getAssetId().getGuid());
			// Assign first
			final HttpResponse response = Request
					.Post(BASE_URL + "mobileterminal/rest/mobileterminal/assign?comment=comment")
					.setHeader("Content-Type", "application/json").setHeader("Authorization", getValidJwtToken())
					.bodyByteArray(writeValueAsString(mobileTerminalAssignQuery).getBytes()).execute().returnResponse();

			Map<String, Object> dataMap = checkSuccessResponseReturnMap(response);
		}

		String comChannelId = createdMobileTerminalType.getChannels().get(0).getGuid();

		PollRequestType pollRequestType = new PollRequestType();
		pollRequestType.setPollType(PollType.PROGRAM_POLL);
		pollRequestType.setUserName("vms_admin_com");
		pollRequestType.setComment("Manual poll created by test");

		PollMobileTerminal pollMobileTerminal = new PollMobileTerminal();
		pollMobileTerminal.setComChannelId(comChannelId);
		pollMobileTerminal.setConnectId(testAsset.getAssetId().getGuid());
		pollMobileTerminal.setMobileTerminalId(createdMobileTerminalType.getMobileTerminalId().getGuid());

		List<MobileTerminalAttribute> mobileTerminalAttributes = createdMobileTerminalType.getAttributes();
		List<PollAttribute> pollAttributes = pollRequestType.getAttributes();

		for (MobileTerminalAttribute mobileTerminalAttribute : mobileTerminalAttributes) {
			String type = mobileTerminalAttribute.getType();
			String value = mobileTerminalAttribute.getValue();
			PollAttribute pollAttribute = new PollAttribute();
			try {
				PollAttributeType pollAttributeType = PollAttributeType.valueOf(type);
				pollAttribute.setKey(pollAttributeType);
				pollAttribute.setValue(value);
				pollAttributes.add(pollAttribute);
			} catch (RuntimeException rte) {
				// ignore
			}
		}

		PollAttribute frequency = new PollAttribute();
		PollAttribute startDate = new PollAttribute();
		PollAttribute endDate = new PollAttribute();

		pollAttributes.add(frequency);
		frequency.setKey(PollAttributeType.FREQUENCY);
		frequency.setValue("1000");

		pollAttributes.add(startDate);
		startDate.setKey(PollAttributeType.START_DATE);
		startDate.setValue(getDateAsString(2001, Calendar.JANUARY, 7, 1, 7, 23, 45));

		pollAttributes.add(endDate);
		endDate.setKey(PollAttributeType.END_DATE);
		endDate.setValue(getDateAsString(2017, Calendar.DECEMBER, 24, 11, 45, 7, 980));

		pollRequestType.getMobileTerminals().add(pollMobileTerminal);

		final HttpResponse response = Request.Post(BASE_URL + "mobileterminal/rest/poll")
				.setHeader("Content-Type", "application/json").setHeader("Authorization", getValidJwtToken())
				.bodyByteArray(writeValueAsString(pollRequestType).getBytes()).execute().returnResponse();

		Map<String, Object> dataMap = checkSuccessResponseReturnMap(response);

		return dataMap;
	}

}