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
package eu.europa.ec.fisheries.uvms.docker.validation.exchange;

import com.fasterxml.jackson.core.JsonProcessingException;
import eu.europa.ec.fisheries.schema.exchange.plugin.v1.SetCommandRequest;
import eu.europa.ec.fisheries.schema.exchange.plugin.v1.SetReportRequest;
import eu.europa.ec.fisheries.uvms.docker.validation.common.AbstractRest;
import eu.europa.ec.fisheries.uvms.docker.validation.common.TopicListener;
import eu.europa.ec.fisheries.uvms.docker.validation.exchange.dto.PluginType;
import eu.europa.ec.fisheries.uvms.docker.validation.exchange.dto.SendingGroupLog;
import eu.europa.ec.fisheries.uvms.docker.validation.exchange.dto.SendingLog;
import eu.europa.ec.fisheries.uvms.docker.validation.system.helper.VMSSystemHelper;
import org.junit.Test;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ExchangeSendingQueueRestIT extends AbstractRest {
    
    @Test
	public void getSendingQueueTest() throws Exception {
	    String fluxEndpoint = "DNK";
	    SetReportRequest reportRequest = VMSSystemHelper.triggerBasicRuleAndSendToFlux(fluxEndpoint);
	    String unsentMessageGuid = reportRequest.getReport().getUnsentMessageGuid();
		assertSendingLogContainsUnsentMessageGuid(fluxEndpoint, unsentMessageGuid);
	}

	@Test
	public void getSendTest() throws Exception {
		boolean sent = sendSendingGroupIds(new ArrayList<>());
		assertTrue(sent);
	}

	@Test
	public void resendToFLUXTest() throws Exception {
	    String fluxEndpoint = "DNK";
	    SetReportRequest reportRequest = VMSSystemHelper.triggerBasicRuleAndSendToFlux(fluxEndpoint);
	    String unsentMessageGuid = reportRequest.getReport().getUnsentMessageGuid();
	    assertSendingLogContainsUnsentMessageGuid(fluxEndpoint, unsentMessageGuid);

	    SetReportRequest reportRequest2;
	    try (TopicListener topicListener = new TopicListener(VMSSystemHelper.FLUX_SELECTOR)) {
	        sendSendingGroupIds(Collections.singletonList(unsentMessageGuid));
	        reportRequest2 = topicListener.listenOnEventBusForSpecificMessage(SetReportRequest.class);
        }
	    assertThat(reportRequest2, is(notNullValue()));

		assertEquals(reportRequest.getReport().getMovement(), reportRequest2.getReport().getMovement());
	    assertThat(reportRequest.getReport().getRecipient(), is(reportRequest2.getReport().getRecipient()));
	}
	
	@Test
    public void resendToEmailTest() throws Exception {
	    VMSSystemHelper.registerEmailPluginIfNotExisting();
	    
        String email = UUID.randomUUID() + "@mail.com";
        SetCommandRequest commandRequest = VMSSystemHelper.triggerBasicRuleAndSendEmail(email);
        String unsentMessageGuid = commandRequest.getCommand().getUnsentMessageGuid();
        assertSendingLogContainsUnsentMessageGuid(eu.europa.ec.fisheries.schema.exchange.plugin.types.v1.PluginType.EMAIL.name(), unsentMessageGuid);

        SetCommandRequest commandRequest2;
        try (TopicListener topicListener = new TopicListener(VMSSystemHelper.emailSelector)) {
            sendSendingGroupIds(Collections.singletonList(unsentMessageGuid));
            commandRequest2 = topicListener.listenOnEventBusForSpecificMessage(SetCommandRequest.class);
        }
        assertThat(commandRequest2, is(notNullValue()));

		assertEquals(commandRequest.getCommand().getEmail(), commandRequest2.getCommand().getEmail());
        assertThat(commandRequest.getCommand().getFwdRule(), is(commandRequest2.getCommand().getFwdRule()));
    }
	
	private void assertSendingLogContainsUnsentMessageGuid(String msgType, String unsentMessageGuid) {
	    List<SendingLog> list = getSendingLogListForMsgType(msgType);
        assertFalse(list.isEmpty());
        assertTrue(list.stream().anyMatch(log -> log.getMessageId().equals(unsentMessageGuid)));
	}
	
	private List<SendingLog> getSendingLogListForMsgType(String recipient) {
    	List<SendingLog> sendingLogs = new ArrayList<>();
	    List<SendingGroupLog> sendGroupList = getSendGroupList();
	    for (SendingGroupLog sendingGroupLog : sendGroupList) {
	        if (sendingGroupLog.getRecipient().equals(recipient)) {
	            for (PluginType plugin : sendingGroupLog.getPluginList()) {
	                sendingLogs.addAll(plugin.getSendingLogList());
	            }
	        }
        }
	    return sendingLogs;
	}
	
	private List<SendingGroupLog> getSendGroupList() {
	    Response response = getWebTarget()
                .path("exchange/rest/sendingqueue/list")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getValidJwtToken())
                .get(Response.class);

        assertEquals(200, response.getStatus());
        return response.readEntity(new GenericType<List<SendingGroupLog>>() {});
	}
	
	private Boolean sendSendingGroupIds(List<String> ids) throws JsonProcessingException {
	    Response response = getWebTarget()
                .path("exchange/rest/sendingqueue/send")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getValidJwtToken())
                .put(Entity.json(writeValueAsString(ids).getBytes()), Response.class);
	    
	    assertEquals(200, response.getStatus());
	    return response.readEntity(Boolean.class);
	}
}
