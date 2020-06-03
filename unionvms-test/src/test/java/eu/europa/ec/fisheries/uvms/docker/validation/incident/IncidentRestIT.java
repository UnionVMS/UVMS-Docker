package eu.europa.ec.fisheries.uvms.docker.validation.incident;

import eu.europa.ec.fisheries.schema.movementrules.ticket.v1.TicketStatusType;
import eu.europa.ec.fisheries.uvms.asset.client.model.AssetDTO;
import eu.europa.ec.fisheries.uvms.docker.validation.asset.AssetTestHelper;
import eu.europa.ec.fisheries.uvms.docker.validation.common.AbstractRest;
import eu.europa.ec.fisheries.uvms.incident.model.dto.IncidentDto;
import eu.europa.ec.fisheries.uvms.incident.model.dto.IncidentLogDto;
import eu.europa.ec.fisheries.uvms.incident.model.dto.IncidentTicketDto;
import org.junit.Test;

import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import java.util.List;

public class IncidentRestIT extends AbstractRest {

    public final String INCIDENT_CREATE = "Incident";
    public final String INCIDENT_UPDATE = "IncidentUpdate";

    @Test
    public void createAssetNotSendingIncidentTest() throws Exception {
        AssetDTO asset = AssetTestHelper.createAsset(AssetTestHelper.createBasicAsset());
        IncidentTicketDto ticket = IncidentTestHelper.createTicket(asset.getId());
        IncidentDto dto = IncidentTestHelper.createAssetNotSendingIncident(ticket, INCIDENT_CREATE);
        assertNotNull(dto);
    }

    @Test
    public void getAssetNotSendingListTest() throws Exception {
        List<IncidentDto> before = IncidentTestHelper.getAssetNotSendingIncidentList();
        AssetDTO asset = AssetTestHelper.createAsset(AssetTestHelper.createBasicAsset());
        IncidentTicketDto ticket = IncidentTestHelper.createTicket(asset.getId());
        IncidentTestHelper.createAssetNotSendingIncident(ticket, INCIDENT_CREATE);
        List<IncidentDto> after = IncidentTestHelper.getAssetNotSendingIncidentList();
        assertEquals(before.size() + 1, after.size());
    }

    @Test
    public void updateAssetNotSendingStatusTest() throws Exception {
        AssetDTO asset = AssetTestHelper.createAsset(AssetTestHelper.createBasicAsset());
        IncidentTicketDto ticket = IncidentTestHelper.createTicket(asset.getId());
        IncidentDto created = IncidentTestHelper.createAssetNotSendingIncident(ticket, INCIDENT_CREATE);

        assertNotEquals("RESOLVED", created.getStatus());

        ticket.setStatus(TicketStatusType.CLOSED.toString());
        IncidentDto updated = IncidentTestHelper.createAssetNotSendingIncident(ticket, INCIDENT_UPDATE);

        assertEquals("RESOLVED", updated.getStatus());
    }

    @Test
    public void getAssetNotSendingEventChangesTest() throws Exception {
        AssetDTO asset = AssetTestHelper.createAsset(AssetTestHelper.createBasicAsset());
        IncidentTicketDto ticket = IncidentTestHelper.createTicket(asset.getId());
        IncidentDto incident = IncidentTestHelper.createAssetNotSendingIncident(ticket, INCIDENT_CREATE);

        ticket.setStatus(TicketStatusType.CLOSED.toString());
        IncidentTestHelper.createAssetNotSendingIncident(ticket, INCIDENT_UPDATE);

        List<IncidentLogDto> dtoList = getWebTarget()
                .path("incident/rest/incident/incidentLogForIncident")
                .path(String.valueOf(incident.getId()))
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getValidJwtToken())
                .get(new GenericType<List<IncidentLogDto>>() {});

        assertTrue(dtoList.size() > 0);
    }

}
