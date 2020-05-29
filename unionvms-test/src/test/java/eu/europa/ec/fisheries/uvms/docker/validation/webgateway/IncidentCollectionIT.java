package eu.europa.ec.fisheries.uvms.docker.validation.webgateway;

import com.fasterxml.jackson.core.type.TypeReference;
import eu.europa.ec.fisheries.uvms.asset.client.model.AssetDTO;
import eu.europa.ec.fisheries.uvms.asset.client.model.Note;
import eu.europa.ec.fisheries.uvms.docker.validation.asset.AssetTestHelper;
import eu.europa.ec.fisheries.uvms.docker.validation.common.AbstractRest;
import eu.europa.ec.fisheries.uvms.docker.validation.incident.IncidentTestHelper;
import eu.europa.ec.fisheries.uvms.docker.validation.webgateway.dto.ExtendedIncidentLogDto;
import eu.europa.ec.fisheries.uvms.docker.validation.webgateway.dto.NoteAndIncidentDto;
import eu.europa.ec.fisheries.uvms.incident.model.dto.IncidentDto;
import eu.europa.ec.fisheries.uvms.incident.model.dto.IncidentTicketDto;
import eu.europa.ec.fisheries.uvms.incident.model.dto.enums.EventTypeEnum;
import org.junit.Test;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Optional;

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

        List<ExtendedIncidentLogDto> incidentLogList = OBJECT_MAPPER.readValue(json, new TypeReference<List<ExtendedIncidentLogDto>>(){});

        assertEquals(2, incidentLogList.size());
        assertFalse(incidentLogList.isEmpty());
        assertTrue(incidentLogList.stream().allMatch(dto -> dto.getIncidentLog() != null));
        assertTrue(incidentLogList.stream().allMatch(dto -> dto.getIncidentLog().getIncidentId() == incidentDto.getId().longValue()));
        Optional<ExtendedIncidentLogDto> noteIncidentLog = incidentLogList.stream().filter(dto -> dto.getIncidentLog().getEventType().equals(EventTypeEnum.NOTE_CREATED)).findAny();
        assertTrue(noteIncidentLog.isPresent());
        assertTrue(noteIncidentLog.get().getRelatedObject() != null);


    }
}
