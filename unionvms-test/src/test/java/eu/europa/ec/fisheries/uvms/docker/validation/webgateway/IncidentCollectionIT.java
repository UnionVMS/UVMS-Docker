package eu.europa.ec.fisheries.uvms.docker.validation.webgateway;

import eu.europa.ec.fisheries.schema.exchange.v1.ExchangeLogStatusType;
import eu.europa.ec.fisheries.schema.exchange.v1.TypeRefType;
import eu.europa.ec.fisheries.schema.mobileterminal.polltypes.v1.PollMobileTerminal;
import eu.europa.ec.fisheries.schema.mobileterminal.polltypes.v1.PollRequestType;
import eu.europa.ec.fisheries.schema.mobileterminal.polltypes.v1.PollType;
import eu.europa.ec.fisheries.uvms.asset.client.model.AssetDTO;
import eu.europa.ec.fisheries.uvms.asset.client.model.Note;
import eu.europa.ec.fisheries.uvms.docker.validation.asset.AssetTestHelper;
import eu.europa.ec.fisheries.uvms.docker.validation.common.AbstractRest;
import eu.europa.ec.fisheries.uvms.docker.validation.incident.IncidentTestHelper;
import eu.europa.ec.fisheries.uvms.docker.validation.mobileterminal.MobileTerminalTestHelper;
import eu.europa.ec.fisheries.uvms.docker.validation.mobileterminal.dto.MobileTerminalDto;
import eu.europa.ec.fisheries.uvms.docker.validation.movement.LatLong;
import eu.europa.ec.fisheries.uvms.docker.validation.movement.ManualMovementRestHelper;
import eu.europa.ec.fisheries.uvms.docker.validation.movement.MovementDto;
import eu.europa.ec.fisheries.uvms.docker.validation.movement.MovementHelper;
import eu.europa.ec.fisheries.uvms.docker.validation.movement.model.ManualMovementDto;
import eu.europa.ec.fisheries.uvms.docker.validation.movement.model.MicroMovement;
import eu.europa.ec.fisheries.uvms.docker.validation.webgateway.dto.ExtendedIncidentLogDto;
import eu.europa.ec.fisheries.uvms.docker.validation.webgateway.dto.NoteAndIncidentDto;
import eu.europa.ec.fisheries.uvms.docker.validation.webgateway.dto.PollAndIncidentDto;
import eu.europa.ec.fisheries.uvms.incident.model.dto.IncidentDto;
import eu.europa.ec.fisheries.uvms.incident.model.dto.IncidentLogDto;
import eu.europa.ec.fisheries.uvms.incident.model.dto.IncidentTicketDto;
import eu.europa.ec.fisheries.uvms.incident.model.dto.enums.EventTypeEnum;
import eu.europa.ec.fisheries.uvms.mobileterminal.model.dto.CommentDto;
import org.junit.Test;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;

public class IncidentCollectionIT extends AbstractRest {

    public final String INCIDENT_CREATE = "Incident";
    public final String INCIDENT_UPDATE = "IncidentUpdate";

    @Test
    public void linkIncidentLogToNoteTest() throws Exception {
        AssetDTO asset = AssetTestHelper.createAsset(AssetTestHelper.createBasicAsset());
        IncidentTicketDto ticket = IncidentTestHelper.createTicket(asset.getId());
        IncidentDto incidentDto = IncidentTestHelper.createAssetNotSendingIncident(ticket, INCIDENT_CREATE);

        Note note = new Note();
        note.setAssetId(asset.getId());
        note.setNote("link incident log to note test");

        Response response = getWebTarget()
                .path("web-gateway/rest/incidents/addNoteToIncident")
                .path(incidentDto.getId().toString())
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getValidJwtToken())
                .post(Entity.json(note), Response.class);
        assertEquals(200, response.getStatus());

        NoteAndIncidentDto noteAdded = response.readEntity(NoteAndIncidentDto.class);
        assertNotNull(noteAdded.getIncident());
        assertNotNull(noteAdded.getNote());
        assertNotNull(noteAdded.getNote().getId());

        response = getWebTarget()
                .path("web-gateway/rest/incidents/incidentLogForIncident")
                .path(incidentDto.getId().toString())
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getValidJwtToken())
                .get(Response.class);
        assertEquals(200, response.getStatus());

        String json = response.readEntity(String.class);
        assertTrue(json.contains(noteAdded.getNote().getId().toString()));
        assertTrue(json.contains(noteAdded.getNote().getNote()));
        assertTrue(json.contains(noteAdded.getNote().getAssetId().toString()));

        ExtendedIncidentLogDto incidentLogDto = OBJECT_MAPPER.readValue(json, ExtendedIncidentLogDto.class);

        assertEquals(2, incidentLogDto.getIncidentLogs().size());
        assertTrue(incidentLogDto.getIncidentLogs().values().stream().allMatch(dto -> dto != null));
        assertTrue(incidentLogDto.getIncidentLogs().values().stream().allMatch(dto -> dto.getIncidentId() == incidentDto.getId().longValue()));

        Optional<IncidentLogDto> noteIncidentLog = incidentLogDto.getIncidentLogs().values().stream().filter(dto -> dto.getEventType().equals(EventTypeEnum.NOTE_CREATED)).findAny();
        assertTrue(noteIncidentLog.isPresent());
        Note incidentNote = incidentLogDto.getRelatedObjects().getNotes().get(noteIncidentLog.get().getRelatedObjectId().toString());
        assertTrue(incidentNote != null);

        assertTrue(incidentNote.getAssetId().equals(note.getAssetId()));
        assertTrue(incidentNote.getNote().equals(note.getNote()));

    }

    @Test
    public void linkIncidentToSimplePollTest() throws Exception {
        AssetDTO asset = AssetTestHelper.createAsset(AssetTestHelper.createBasicAsset());
        MobileTerminalDto mt = MobileTerminalTestHelper.createMobileTerminal();
        MobileTerminalTestHelper.assignMobileTerminal(asset, mt);
        IncidentTicketDto ticket = IncidentTestHelper.createTicket(asset.getId());
        IncidentDto incidentDto = IncidentTestHelper.createAssetNotSendingIncident(ticket, INCIDENT_CREATE);
        CommentDto comment = new CommentDto();
        comment.setComment("link poll to incident test");

        Response response = getWebTarget()
                .path("web-gateway/rest/incidents/createSimplePollForIncident")
                .path(incidentDto.getId().toString())
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getValidJwtToken())
                .post(Entity.json(comment), Response.class);
        assertEquals(200, response.getStatus());

        PollAndIncidentDto pollAdded = response.readEntity(PollAndIncidentDto.class);
        assertNotNull(pollAdded.getIncident());
        assertNotNull(pollAdded.getPollId());

        response = getWebTarget()
                .path("web-gateway/rest/incidents/incidentLogForIncident")
                .path(incidentDto.getId().toString())
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getValidJwtToken())
                .get(Response.class);
        assertEquals(200, response.getStatus());

        String json = response.readEntity(String.class);

        ExtendedIncidentLogDto incidentLogDto = OBJECT_MAPPER.readValue(json, ExtendedIncidentLogDto.class);

        assertEquals(2, incidentLogDto.getIncidentLogs().size());
        assertTrue(incidentLogDto.getIncidentLogs().values().stream().allMatch(dto -> dto != null));
        assertTrue(incidentLogDto.getIncidentLogs().values().stream().allMatch(dto -> dto.getIncidentId() == incidentDto.getId().longValue()));

        Optional<IncidentLogDto> pollIncidentLog = incidentLogDto.getIncidentLogs().values().stream().filter(dto -> dto.getEventType().equals(EventTypeEnum.POLL_CREATED)).findAny();
        assertTrue(pollIncidentLog.isPresent());
        ExchangeLogStatusType logStatusType = incidentLogDto.getRelatedObjects().getPolls().get(pollIncidentLog.get().getRelatedObjectId().toString());
        assertTrue(logStatusType != null);

        assertEquals(TypeRefType.POLL, logStatusType.getTypeRef().getType());
        assertEquals(pollAdded.getPollId(), logStatusType.getTypeRef().getRefGuid());
        assertFalse(logStatusType.getHistory().isEmpty());

    }

    @Test
    public void linkIncidentToPollTest() throws Exception {
        AssetDTO asset = AssetTestHelper.createAsset(AssetTestHelper.createBasicAsset());
        MobileTerminalDto mt = MobileTerminalTestHelper.createMobileTerminal();
        MobileTerminalTestHelper.assignMobileTerminal(asset, mt);
        IncidentTicketDto ticket = IncidentTestHelper.createTicket(asset.getId());
        IncidentDto incidentDto = IncidentTestHelper.createAssetNotSendingIncident(ticket, INCIDENT_CREATE);

        String comChannelId = mt.getChannels().iterator().next().getId().toString();

        PollRequestType pollRequestType = new PollRequestType();
        pollRequestType.setPollType(PollType.MANUAL_POLL);
        pollRequestType.setUserName("vms_admin_com");
        pollRequestType.setComment("Manual poll created by test");

        PollMobileTerminal pollMobileTerminal = new PollMobileTerminal();
        pollMobileTerminal.setComChannelId(comChannelId);
        pollMobileTerminal.setConnectId(asset.getId().toString());
        pollMobileTerminal.setMobileTerminalId(mt.getId().toString());
        pollRequestType.getMobileTerminals().add(pollMobileTerminal);

        Response response = getWebTarget()
                .path("web-gateway/rest/incidents/createPollForIncident")
                .path(incidentDto.getId().toString())
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getValidJwtToken())
                .post(Entity.json(pollRequestType), Response.class);
        assertEquals(200, response.getStatus());

        PollAndIncidentDto pollAdded = response.readEntity(PollAndIncidentDto.class);
        assertNotNull(pollAdded.getIncident());
        assertNotNull(pollAdded.getPollId());

        response = getWebTarget()
                .path("web-gateway/rest/incidents/incidentLogForIncident")
                .path(incidentDto.getId().toString())
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getValidJwtToken())
                .get(Response.class);
        assertEquals(200, response.getStatus());

        String json = response.readEntity(String.class);

        ExtendedIncidentLogDto incidentLogDto = OBJECT_MAPPER.readValue(json, ExtendedIncidentLogDto.class);

        assertEquals(2, incidentLogDto.getIncidentLogs().size());
        assertTrue(incidentLogDto.getIncidentLogs().values().stream().allMatch(dto -> dto != null));
        assertTrue(incidentLogDto.getIncidentLogs().values().stream().allMatch(dto -> dto.getIncidentId() == incidentDto.getId().longValue()));

        Optional<IncidentLogDto> pollIncidentLog = incidentLogDto.getIncidentLogs().values().stream().filter(dto -> dto.getEventType().equals(EventTypeEnum.POLL_CREATED)).findAny();
        assertTrue(pollIncidentLog.isPresent());
        ExchangeLogStatusType logStatusType = incidentLogDto.getRelatedObjects().getPolls().get(pollIncidentLog.get().getRelatedObjectId().toString());
        assertTrue(logStatusType != null);

        assertEquals(TypeRefType.POLL, logStatusType.getTypeRef().getType());
        assertEquals(pollAdded.getPollId(), logStatusType.getTypeRef().getRefGuid());
        assertFalse(logStatusType.getHistory().isEmpty());

    }


    @Test
    public void linkIncidentToMovementTest() throws Exception {
        AssetDTO asset = AssetTestHelper.createAsset(AssetTestHelper.createBasicAsset());
        MobileTerminalDto mt = MobileTerminalTestHelper.createMobileTerminal();
        MobileTerminalTestHelper.assignMobileTerminal(asset, mt);

        Double latitude = 10d;
        Double longitude = 11d;
        LatLong position = new LatLong(latitude, longitude, new Date(System.currentTimeMillis() - 60000));
        ManualMovementDto manualMovement = ManualMovementRestHelper.mapToManualMovement(position, asset);

        Response response = ManualMovementRestHelper.sendTempMovement(manualMovement);
        assertEquals(200, response.getStatus());
        MovementHelper.pollMovementCreated();
        List<MovementDto> latestMovements = MovementHelper.getLatestMovements(Collections.singletonList(asset.getId().toString()));
        assertThat(latestMovements.size(), is(1));
        String movementId = latestMovements.get(0).getMovementGUID();

        IncidentTicketDto ticket = IncidentTestHelper.createTicket(asset.getId());
        ticket.setMovementId(movementId);
        IncidentDto incidentDto = IncidentTestHelper.createAssetNotSendingIncident(ticket, INCIDENT_CREATE);

        position = new LatLong(latitude, longitude, new Date());
        manualMovement = ManualMovementRestHelper.mapToManualMovement(position, asset);
        response = ManualMovementRestHelper.sendTempMovement(manualMovement);
        assertEquals(200, response.getStatus());

        MovementHelper.pollMovementCreated();
        latestMovements = MovementHelper.getLatestMovements(Collections.singletonList(asset.getId().toString()));
        assertThat(latestMovements.size(), is(1));
        String secondMovementId = latestMovements.get(0).getMovementGUID();

        ticket.setMovementId(secondMovementId);
        IncidentDto updated = IncidentTestHelper.createAssetNotSendingIncident(ticket, INCIDENT_UPDATE);

        response = getWebTarget()
                .path("web-gateway/rest/incidents/incidentLogForIncident")
                .path(incidentDto.getId().toString())
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getValidJwtToken())
                .get(Response.class);
        assertEquals(200, response.getStatus());

        String json = response.readEntity(String.class);

        ExtendedIncidentLogDto incidentLogDto = OBJECT_MAPPER.readValue(json, ExtendedIncidentLogDto.class);

        assertEquals(2, incidentLogDto.getIncidentLogs().size());
        assertTrue(incidentLogDto.getIncidentLogs().values().stream().allMatch(dto -> dto != null));
        assertTrue(incidentLogDto.getIncidentLogs().values().stream().allMatch(dto -> dto.getIncidentId() == incidentDto.getId().longValue()));

        Optional<IncidentLogDto> MovementIncidentLog = incidentLogDto.getIncidentLogs().values().stream().filter(dto -> dto.getEventType().equals(EventTypeEnum.MANUAL_POSITION)).findAny();
        assertTrue(MovementIncidentLog.isPresent());
        MicroMovement microMovement = incidentLogDto.getRelatedObjects().getPositions().get(MovementIncidentLog.get().getRelatedObjectId().toString());
        assertTrue(microMovement != null);

    }

}
