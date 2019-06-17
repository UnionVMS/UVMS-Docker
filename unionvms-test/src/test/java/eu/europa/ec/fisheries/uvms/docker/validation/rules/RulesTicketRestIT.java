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

import eu.europa.ec.fisheries.schema.movementrules.module.v1.GetTicketListByQueryResponse;
import eu.europa.ec.fisheries.schema.movementrules.search.v1.TicketQuery;
import eu.europa.ec.fisheries.schema.movementrules.ticket.v1.TicketStatusType;
import eu.europa.ec.fisheries.schema.movementrules.ticket.v1.TicketType;
import eu.europa.ec.fisheries.uvms.docker.validation.common.AbstractRest;
import org.junit.Test;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RulesTicketRestIT extends AbstractRest {

	@Test
	public void getTicketListTest() {
		TicketQuery ticketQuery = CustomRulesTestHelper.getTicketQuery();

		GetTicketListByQueryResponse response = getWebTarget()
				.path("movement-rules/rest/tickets/list")
				.path("vms_admin_se")
				.request(MediaType.APPLICATION_JSON)
				.header(HttpHeaders.AUTHORIZATION, getValidJwtToken())
				.post(Entity.json(ticketQuery), new GenericType<GetTicketListByQueryResponse>(){});

		assertNotNull(response);
	}

	@Test
	public void getTicketsByMovementsTest() throws Exception {
        String movementGuid = CustomRulesTestHelper.createRuleAndGetMovementGuid();
	    ArrayList<String> list = new ArrayList<>();
	    list.add(movementGuid);

		GetTicketListByQueryResponse response = getWebTarget()
				.path("movement-rules/rest/tickets/listByMovements")
				.request(MediaType.APPLICATION_JSON)
				.header(HttpHeaders.AUTHORIZATION, getValidJwtToken())
				.post(Entity.json(list), new GenericType<GetTicketListByQueryResponse>(){});

        List<TicketType> tickets = response.getTickets();

		assertFalse(tickets.isEmpty());
	}

	@Test
	public void countTicketsByMovementsTest() throws Exception {
        String movementGuid = CustomRulesTestHelper.createRuleAndGetMovementGuid();
        ArrayList<String> list = new ArrayList<>();
        list.add(movementGuid);

		Long response = getWebTarget()
				.path("movement-rules/rest/tickets/countByMovements")
				.request(MediaType.APPLICATION_JSON)
				.header(HttpHeaders.AUTHORIZATION, getValidJwtToken())
				.post(Entity.json(list), new GenericType<Long>(){});

		assertTrue(response > 0);
	}

	@Test
	public void updateTicketStatus() throws Exception {
        String movementGuid = CustomRulesTestHelper.createRuleAndGetMovementGuid();
        TicketQuery ticketQuery = CustomRulesTestHelper.getTicketQuery();
        List<TicketType> tickets = getTicketTypeList(ticketQuery);

        Optional<TicketType> ticketOptional = tickets
				.stream()
				.filter(ticket -> movementGuid.equals(ticket.getMovementGuid()))
				.findFirst();

		TicketType ticketType = ticketOptional.orElse(null);

		if (ticketType == null) {
			fail("There is no ticket to update");
		}

		assertEquals(TicketStatusType.OPEN, ticketType.getStatus());

		ticketType.setStatus(TicketStatusType.CLOSED);

        TicketType updateResponse = getWebTarget()
                .path("movement-rules/rest/tickets/status")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getValidJwtToken())
                .put(Entity.json(ticketType), new GenericType<TicketType>(){});

        assertEquals(TicketStatusType.CLOSED, updateResponse.getStatus());
	}

    @Test
	public void updateTicketStatusByQueryTest() throws Exception {
        String movementGuid = CustomRulesTestHelper.createRuleAndGetMovementGuid();
        TicketQuery ticketQuery = CustomRulesTestHelper.getTicketQuery();
        List<TicketType> tickets = getTicketTypeList(ticketQuery);

        TicketType ticketType = tickets
                .stream()
                .filter(ticket -> movementGuid.equals(ticket.getMovementGuid()))
                .findFirst()
                .orElse(null);

		if (ticketType == null) {
			fail("There is no ticket to update");
		}

		assertEquals(TicketStatusType.OPEN, ticketType.getStatus());

		ticketType.setStatus(TicketStatusType.CLOSED);

		List<TicketType> updatedTicketList = getWebTarget()
				.path("movement-rules/rest/tickets/status")
				.path("vms_admin_se")
				.path(TicketStatusType.CLOSED.name())
				.request(MediaType.APPLICATION_JSON)
				.header(HttpHeaders.AUTHORIZATION, getValidJwtToken())
				.post(Entity.json(ticketQuery), new GenericType<List<TicketType>>(){});

        TicketType updatedTicket = updatedTicketList
                .stream()
                .filter(ticket -> ticketType.getGuid().equals(ticket.getGuid()))
                .findFirst()
                .orElse(null);

		if (updatedTicket == null) {
			fail("Update Ticket Status failed");
		}
		assertEquals(TicketStatusType.CLOSED, updatedTicket.getStatus());
	}

    @Test
	public void getTicketByGuidTest() throws Exception {
        String movementGuid = CustomRulesTestHelper.createRuleAndGetMovementGuid();
        TicketQuery ticketQuery = CustomRulesTestHelper.getTicketQuery();
        List<TicketType> tickets = getTicketTypeList(ticketQuery);

        TicketType ticketType = tickets
                .stream()
                .filter(ticket -> movementGuid.equals(ticket.getMovementGuid()))
                .findFirst()
                .orElse(null);

        if (ticketType == null) {
            fail("There is no ticket to update");
        }

        TicketType ticketResponse = getWebTarget()
                .path("movement-rules/rest/tickets")
                .path(ticketType.getGuid())
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getValidJwtToken())
                .get(TicketType.class);

        assertNotNull(ticketResponse);
        assertEquals(ticketType.getGuid(), ticketResponse.getGuid());
	}

	@Test
	public void getNumberOfOpenTicketReportsTest() throws Exception {
        CustomRulesTestHelper.createRuleAndGetMovementGuid();
        Long count = getWebTarget()
                .path("movement-rules/rest/tickets/countopen")
                .path("vms_admin_se")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getValidJwtToken())
                .get(Long.class);

        assertTrue(count > 0);
    }

	@Test
	public void getNumberOfAssetsNotSendingTest() {
        Long count = getWebTarget()
                .path("movement-rules/rest/tickets/countAssetsNotSending")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getValidJwtToken())
                .get(Long.class);
        assertNotNull(count);
	}

    private List<TicketType> getTicketTypeList(TicketQuery ticketQuery) {
        GetTicketListByQueryResponse response = getWebTarget()
                .path("movement-rules/rest/tickets/list")
                .path("vms_admin_se")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getValidJwtToken())
                .post(Entity.json(ticketQuery), new GenericType<GetTicketListByQueryResponse>() {
                });

        return response.getTickets();
    }
}
