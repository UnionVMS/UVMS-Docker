package eu.europa.ec.fisheries.uvms.docker.validation.incident;

import eu.europa.ec.fisheries.schema.movementrules.ticket.v1.TicketStatusType;
import eu.europa.ec.fisheries.uvms.docker.validation.common.AbstractHelper;
import eu.europa.ec.fisheries.uvms.docker.validation.common.MessageHelper;
import eu.europa.ec.fisheries.uvms.incident.model.dto.IncidentDto;
import eu.europa.ec.fisheries.uvms.incident.model.dto.IncidentTicketDto;

import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.locks.LockSupport;

public class IncidentTestHelper extends AbstractHelper {

    public static final String QUEUE_NAME = "IncidentEvent";

    public static IncidentDto createAssetNotSendingIncident(IncidentTicketDto ticket, String eventName) throws Exception {
        sendMessage(ticket, eventName);
        LockSupport.parkNanos(5000000000L);

        return getWebTarget()
                .path("incident/rest/incident/byTicketId")
                .path(ticket.getId().toString())
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getValidJwtToken())
                .get(IncidentDto.class);
    }

    public static List<IncidentDto> getAssetNotSendingIncidentList() {
        return getWebTarget()
                .path("incident/rest/incident/assetNotSending")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getValidJwtToken())
                .get(new GenericType<List<IncidentDto>>() {});
    }

    public static void sendMessage(IncidentTicketDto ticket, String eventName) throws Exception {
        try (MessageHelper messageHelper = new MessageHelper()) {
            String asString = OBJECT_MAPPER.writeValueAsString(ticket);
            messageHelper.sendMessageWithProperty(QUEUE_NAME, asString, eventName);
        }
    }

    public static IncidentTicketDto createTicket(UUID assetId) {
        IncidentTicketDto ticket = new IncidentTicketDto();
        ticket.setId(UUID.randomUUID());
        ticket.setAssetId(assetId.toString());
        ticket.setMovementId(UUID.randomUUID().toString());
        ticket.setMobTermId(UUID.randomUUID().toString());
        ticket.setPollId(UUID.randomUUID().toString());
        ticket.setRuleName("Asset not sending");
        ticket.setRuleGuid("Asset not sending");
        ticket.setUpdatedBy("UVMS");
        ticket.setStatus(TicketStatusType.POLL_PENDING.value());
        ticket.setTicketCount(1L);
        ticket.setCreatedDate(Instant.now());
        ticket.setUpdated(Instant.now());
        return ticket;
    }
}
