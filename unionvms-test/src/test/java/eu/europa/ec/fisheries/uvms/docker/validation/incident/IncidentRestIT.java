package eu.europa.ec.fisheries.uvms.docker.validation.incident;

import eu.europa.ec.fisheries.schema.movementrules.ticket.v1.TicketStatusType;
import eu.europa.ec.fisheries.uvms.asset.client.model.AssetDTO;
import eu.europa.ec.fisheries.uvms.docker.validation.asset.AssetTestHelper;
import eu.europa.ec.fisheries.uvms.docker.validation.common.AbstractRest;
import eu.europa.ec.fisheries.uvms.docker.validation.config.ConfigRestHelper;
import eu.europa.ec.fisheries.uvms.docker.validation.movement.LatLong;
import eu.europa.ec.fisheries.uvms.docker.validation.movement.MovementHelper;
import eu.europa.ec.fisheries.uvms.docker.validation.movement.model.IncomingMovement;
import eu.europa.ec.fisheries.uvms.docker.validation.system.helper.FLUXHelper;
import eu.europa.ec.fisheries.uvms.incident.model.dto.AssetNotSendingDto;
import eu.europa.ec.fisheries.uvms.incident.model.dto.IncidentDto;
import eu.europa.ec.fisheries.uvms.incident.model.dto.IncidentLogDto;
import eu.europa.ec.fisheries.uvms.incident.model.dto.IncidentTicketDto;
import eu.europa.ec.fisheries.uvms.incident.model.dto.enums.MovementSourceType;
import eu.europa.ec.fisheries.uvms.incident.model.dto.enums.StatusEnum;
import eu.europa.ec.fisheries.uvms.movement.model.dto.MovementDto;
import org.junit.Test;

import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

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
        AssetNotSendingDto before = IncidentTestHelper.getAssetNotSendingIncidentList();
        AssetDTO asset = AssetTestHelper.createAsset(AssetTestHelper.createBasicAsset());
        IncidentTicketDto ticket = IncidentTestHelper.createTicket(asset.getId());
        IncidentTestHelper.createAssetNotSendingIncident(ticket, INCIDENT_CREATE);
        AssetNotSendingDto after = IncidentTestHelper.getAssetNotSendingIncidentList();
        assertEquals(before.getUnresolved().size() + 1, after.getUnresolved().size());
    }

    @Test
    public void updateAssetNotSendingStatusTest() throws Exception {
        AssetDTO asset = AssetTestHelper.createAsset(AssetTestHelper.createBasicAsset());
        IncidentTicketDto ticket = IncidentTestHelper.createTicket(asset.getId());
        IncidentDto created = IncidentTestHelper.createAssetNotSendingIncident(ticket, INCIDENT_CREATE);

        assertNotEquals("RESOLVED", created.getStatus());

        ticket.setType(null);
        ticket.setMovementId(UUID.randomUUID().toString());
        ticket.setMovementSource(MovementSourceType.FLUX);
        IncidentDto updated = IncidentTestHelper.createAssetNotSendingIncident(ticket, INCIDENT_UPDATE);

        assertEquals(StatusEnum.RESOLVED.name(), updated.getStatus());
    }

    @Test
    public void getAssetNotSendingEventChangesTest() throws Exception {
        AssetDTO asset = AssetTestHelper.createAsset(AssetTestHelper.createBasicAsset());
        IncidentTicketDto ticket = IncidentTestHelper.createTicket(asset.getId());
        IncidentDto incident = IncidentTestHelper.createAssetNotSendingIncident(ticket, INCIDENT_CREATE);

        ticket.setStatus(TicketStatusType.CLOSED.toString());
        IncidentTestHelper.createAssetNotSendingIncident(ticket, INCIDENT_UPDATE);

        Map<Long, IncidentLogDto> dtoList = getWebTarget()
                .path("incident/rest/incident/incidentLogForIncident")
                .path(String.valueOf(incident.getId()))
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getValidJwtToken())
                .get(new GenericType<Map<Long, IncidentLogDto>>() {});

        assertTrue(dtoList.size() > 0);
    }


    @Test
    public void movementReceivedWithAssetInParkedStatusButNoIncident() throws Exception {

        //Fix FlagState
        ConfigRestHelper.setLocalFlagStateToSwe();

        //Actual test
        AssetDTO basicAsset = AssetTestHelper.createBasicAsset();
        basicAsset.setParked(true);
        AssetDTO asset = AssetTestHelper.createAsset(basicAsset);

        FLUXHelper.sendPositionToFluxPlugin(asset,
                new LatLong(56d, 11d, new Date(System.currentTimeMillis() - 10000)));

        MovementHelper.pollMovementCreated();
        Thread.sleep(1000);

        Map<Long, IncidentDto> openTicketsForAsset = IncidentTestHelper.getOpenTicketsForAsset(asset.getId().toString());

        assertEquals(0, openTicketsForAsset.size());
    }

    @Test
    public void getIncidentsForAssetIdTest() throws Exception {
        AssetDTO asset = AssetTestHelper.createAsset(AssetTestHelper.createBasicAsset());
        MovementDto movement = null;
        try (MovementHelper movementHelper = new MovementHelper()) {
            IncomingMovement incomingMovement = movementHelper.createIncomingMovement(asset, new LatLong(56d, 11d, Date.from(Instant.now().minus(1, ChronoUnit.HOURS))));
            movement = movementHelper.createMovement(incomingMovement);
        }
        IncidentTicketDto ticket = IncidentTestHelper.createTicket(asset.getId());
        ticket.setMovementId(movement.getId().toString());
        IncidentDto incident = IncidentTestHelper.createAssetNotSendingIncident(ticket, INCIDENT_CREATE);

        Map<Long, IncidentDto> incidentMap = IncidentTestHelper.getOpenTicketsForAsset(asset.getId().toString());

        assertTrue(incidentMap.size() > 0);
        assertEquals(asset.getId(), incidentMap.get(incident.getId()).getAssetId());
        assertEquals(movement.getId().toString(), incidentMap.get(incident.getId()).getLastKnownLocation().getId());
    }
}
