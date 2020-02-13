package eu.europa.ec.fisheries.uvms.docker.validation.incident;

import eu.europa.ec.fisheries.schema.movementrules.ticket.v1.TicketStatusType;
import eu.europa.ec.fisheries.schema.movementrules.ticket.v1.TicketType;
import eu.europa.ec.fisheries.uvms.asset.client.model.AssetDTO;
import eu.europa.ec.fisheries.uvms.docker.validation.asset.AssetTestHelper;
import eu.europa.ec.fisheries.uvms.docker.validation.common.AbstractRest;
import eu.europa.ec.fisheries.uvms.docker.validation.common.MessageHelper;
import eu.europa.ec.fisheries.uvms.docker.validation.incident.dto.IncidentDto;
import eu.europa.ec.fisheries.uvms.docker.validation.incident.dto.IncidentLogDto;
import org.junit.Test;

import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.locks.LockSupport;

public class IncidentRestIT extends AbstractRest {

    public final String QUEUE_NAME = "IncidentEvent";
    public final String INCIDENT_CREATE = "Incident";
    public final String INCIDENT_UPDATE = "IncidentUpdate";

    @Test
    public void createAssetNotSendingIncidentTest() throws Exception {
        AssetDTO asset = AssetTestHelper.createAsset(AssetTestHelper.createBasicAsset());
        TicketType ticket = createTicket(asset.getId());
        IncidentDto dto = createAssetNotSendingIncident(ticket, INCIDENT_CREATE);
        assertNotNull(dto);
    }

    @Test
    public void getAssetNotSendingListTest() throws Exception {
        List<IncidentDto> before = getAssetNotSendingIncidentList();
        AssetDTO asset = AssetTestHelper.createAsset(AssetTestHelper.createBasicAsset());
        TicketType ticket = createTicket(asset.getId());
        createAssetNotSendingIncident(ticket, INCIDENT_CREATE);
        List<IncidentDto> after = getAssetNotSendingIncidentList();
        assertEquals(before.size() + 1, after.size());
    }

    @Test
    public void updateAssetNotSendingStatusTest() throws Exception {
        AssetDTO asset = AssetTestHelper.createAsset(AssetTestHelper.createBasicAsset());
        TicketType ticket = createTicket(asset.getId());
        IncidentDto created = createAssetNotSendingIncident(ticket, INCIDENT_CREATE);

        assertNotEquals("RESOLVED", created.getStatus());

        ticket.setStatus(TicketStatusType.CLOSED);
        IncidentDto updated = createAssetNotSendingIncident(ticket, INCIDENT_UPDATE);

        assertEquals("RESOLVED", updated.getStatus());
    }

    @Test
    public void getAssetNotSendingEventChangesTest() throws Exception {
        AssetDTO asset = AssetTestHelper.createAsset(AssetTestHelper.createBasicAsset());
        TicketType ticket = createTicket(asset.getId());
        IncidentDto incident = createAssetNotSendingIncident(ticket, INCIDENT_CREATE);

        ticket.setStatus(TicketStatusType.CLOSED);
        createAssetNotSendingIncident(ticket, INCIDENT_UPDATE);

        List<IncidentLogDto> dtoList = getWebTarget()
                .path("incident/rest/incident/assetNotSendingChanges")
                .path(String.valueOf(incident.getId()))
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getValidJwtToken())
                .get(new GenericType<List<IncidentLogDto>>() {});

        assertTrue(dtoList.size() > 0);
    }

    private IncidentDto createAssetNotSendingIncident(TicketType ticket, String eventName) throws Exception {
        sendMessage(ticket, eventName);
        LockSupport.parkNanos(5000000000L);

        return getWebTarget()
                .path("incident/rest/incident/byTicketId")
                .path(ticket.getGuid())
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getValidJwtToken())
                .get(IncidentDto.class);
    }

    private List<IncidentDto> getAssetNotSendingIncidentList() {
        return getWebTarget()
                .path("incident/rest/incident/assetNotSending")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getValidJwtToken())
                .get(new GenericType<List<IncidentDto>>() {});
    }

    private void sendMessage(TicketType ticket, String eventName) throws Exception {
        try (MessageHelper messageHelper = new MessageHelper()) {
            String asString = OBJECT_MAPPER.writeValueAsString(ticket);
            messageHelper.sendMessageWithProperty(QUEUE_NAME, asString, eventName);
        }
    }

    private TicketType createTicket(UUID assetId) {
        TicketType ticket = new TicketType();
        ticket.setGuid(UUID.randomUUID().toString());
        ticket.setAssetGuid(assetId.toString());
        ticket.setMovementGuid(UUID.randomUUID().toString());
        ticket.setMobileTerminalGuid(UUID.randomUUID().toString());
        ticket.setRuleName("Asset not sending");
        ticket.setRuleGuid("Asset not sending");
        ticket.setUpdatedBy("UVMS");
        ticket.setStatus(TicketStatusType.POLL_PENDING);
        ticket.setTicketCount(1L);
        String date = String.valueOf(Instant.now().getEpochSecond());
        ticket.setOpenDate(date);
        ticket.setUpdated(date);
        return ticket;
    }
}
