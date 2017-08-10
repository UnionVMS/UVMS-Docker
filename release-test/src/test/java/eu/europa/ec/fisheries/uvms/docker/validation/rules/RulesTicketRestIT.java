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
package eu.europa.ec.fisheries.uvms.docker.validation.rules;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.fluent.Request;
import org.junit.Ignore;
import org.junit.Test;

import eu.europa.ec.fisheries.schema.rules.search.v1.ListPagination;
import eu.europa.ec.fisheries.schema.rules.search.v1.TicketListCriteria;
import eu.europa.ec.fisheries.schema.rules.search.v1.TicketQuery;
import eu.europa.ec.fisheries.schema.rules.search.v1.TicketSearchKey;
import eu.europa.ec.fisheries.schema.rules.ticket.v1.TicketStatusType;
import eu.europa.ec.fisheries.schema.rules.ticket.v1.TicketType;
import eu.europa.ec.fisheries.uvms.docker.validation.common.AbstractRestServiceTest;

/**
 * The Class RulesTicketRestIT.
 */

public class RulesTicketRestIT extends AbstractRestServiceTest {

	/**
	 * Gets the ticket list test.
	 *
	 * @return the ticket list test
	 * @throws Exception the exception
	 */
	@Test
	public void getTicketListTest() throws Exception {
		TicketQuery ticketQuery = new TicketQuery();
		ListPagination listPagination = new ListPagination();
		listPagination.setListSize(100);
		listPagination.setPage(1);
		ticketQuery.setPagination(listPagination);
		TicketListCriteria ticketListCriteria = new TicketListCriteria();
		ticketListCriteria.setKey(TicketSearchKey.STATUS);
		ticketListCriteria.setValue("Open");
		ticketQuery.getTicketSearchCriteria().add(ticketListCriteria);
		final HttpResponse response = Request.Post(getBaseUrl() + "rules/rest/tickets/list/" + URLEncoder.encode("vms_admin_com"))
				.setHeader("Content-Type", "application/json").setHeader("Authorization", getValidJwtToken())
				.bodyByteArray(writeValueAsString(ticketQuery).getBytes()).execute().returnResponse();
		Map<String, Object> dataMap = checkSuccessResponseReturnMap(response);
	}

	/**
	 * Gets the tickets by movements test.
	 *
	 * @return the tickets by movements test
	 * @throws Exception the exception
	 */
	@Test
	@Ignore
	public void getTicketsByMovementsTest() throws Exception {
		final HttpResponse response = Request.Post(getBaseUrl() + "rules/rest/tickets/listByMovements")
				.setHeader("Content-Type", "application/json").setHeader("Authorization", getValidJwtToken())
				.bodyByteArray(writeValueAsString(new ArrayList<String>()).getBytes()).execute().returnResponse();
		Map<String, Object> dataMap = checkSuccessResponseReturnMap(response);
	}

	/**
	 * Count tickets by movements test.
	 *
	 * @throws Exception the exception
	 */
	@Test
	@Ignore
	public void countTicketsByMovementsTest() throws Exception {
		final HttpResponse response = Request.Post(getBaseUrl() + "rules/rest/tickets/countByMovements")
				.setHeader("Content-Type", "application/json").setHeader("Authorization", getValidJwtToken())
				.bodyByteArray(writeValueAsString(new ArrayList<String>()).getBytes()).execute().returnResponse();
		Map<String, Object> dataMap = checkSuccessResponseReturnMap(response);
	}

	/**
	 * Update ticket status.
	 *
	 * @throws Exception the exception
	 */
	@Test
	@Ignore
	public void updateTicketStatus() throws Exception {
		TicketType TicketType = new TicketType();
		final HttpResponse response = Request.Put(getBaseUrl() + "rules/rest/tickets/status")
				.setHeader("Content-Type", "application/json").setHeader("Authorization", getValidJwtToken())
				.bodyByteArray(writeValueAsString(TicketType).getBytes()).execute().returnResponse();
		Map<String, Object> dataMap = checkSuccessResponseReturnMap(response);
	}

	/**
	 * Update ticket status by query test.
	 *
	 * @throws Exception the exception
	 */
	@Test
	@Ignore
	public void updateTicketStatusByQueryTest() throws Exception {
		final HttpResponse response = Request.Post(getBaseUrl() + "rules/rest/tickets/status/{loggedInUser}/{status}")
				.setHeader("Content-Type", "application/json").setHeader("Authorization", getValidJwtToken())
				.bodyByteArray(writeValueAsString(TicketStatusType.OPEN).getBytes()).execute().returnResponse();
		Map<String, Object> dataMap = checkSuccessResponseReturnMap(response);
	}

	/**
	 * Gets the ticket by guid test.
	 *
	 * @return the ticket by guid test
	 * @throws Exception the exception
	 */
	@Test
	@Ignore
	public void getTicketByGuidTest() throws Exception {
		final HttpResponse response = Request.Get(getBaseUrl() + "rules/rest/tickets/{guid}")
				.setHeader("Content-Type", "application/json").setHeader("Authorization", getValidJwtToken()).execute()
				.returnResponse();
		Map<String, Object> dataMap = checkSuccessResponseReturnMap(response);
	}

	/**
	 * Gets the number of open ticket reports test.
	 *
	 * @return the number of open ticket reports test
	 * @throws Exception the exception
	 */
	@Test
	public void getNumberOfOpenTicketReportsTest() throws Exception {
		final HttpResponse response = Request.Get(getBaseUrl() + "rules/rest/tickets/countopen/" +URLEncoder.encode("vms_admin_com"))
				.setHeader("Content-Type", "application/json").setHeader("Authorization", getValidJwtToken()).execute()
				.returnResponse();
		Integer numberOpenTickets = checkSuccessResponseReturnType(response,Integer.class);
	}

	/**
	 * Gets the number of assets not sending test.
	 *
	 * @return the number of assets not sending test
	 * @throws Exception the exception
	 */
	@Test
	public void getNumberOfAssetsNotSendingTest() throws Exception {
		final HttpResponse response = Request.Get(getBaseUrl() + "rules/rest/tickets/countAssetsNotSending")
				.setHeader("Content-Type", "application/json").setHeader("Authorization", getValidJwtToken()).execute()
				.returnResponse();
		Integer numberAssetsNotSending = checkSuccessResponseReturnType(response,Integer.class);
	}

}
