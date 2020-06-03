package eu.europa.ec.fisheries.uvms.docker.validation.webgateway.dto;

import eu.europa.ec.fisheries.schema.exchange.v1.ExchangeLogStatusType;
import eu.europa.ec.fisheries.uvms.asset.client.model.Note;
import eu.europa.ec.fisheries.uvms.docker.validation.movement.model.MicroMovement;
import eu.europa.ec.fisheries.uvms.incident.model.dto.IncidentLogDto;

import java.util.HashMap;
import java.util.Map;

public class ExtendedIncidentLogDto {

    private Map<Long, IncidentLogDto> incidentLogs = new HashMap<>();

    private Map<String, Note> notes = new HashMap<>();

    private Map<String, ExchangeLogStatusType> polls = new HashMap<>();

    private Map<String, MicroMovement> manualPositions = new HashMap<>();

    public Map<Long, IncidentLogDto> getIncidentLogs() {
        return incidentLogs;
    }

    public void setIncidentLogs(Map<Long, IncidentLogDto> incidentLogs) {
        this.incidentLogs = incidentLogs;
    }

    public Map<String, Note> getNotes() {
        return notes;
    }

    public void setNotes(Map<String, Note> notes) {
        this.notes = notes;
    }

    public Map<String, ExchangeLogStatusType> getPolls() {
        return polls;
    }

    public void setPolls(Map<String, ExchangeLogStatusType> polls) {
        this.polls = polls;
    }

    public Map<String, MicroMovement> getManualPositions() {
        return manualPositions;
    }

    public void setManualPositions(Map<String, MicroMovement> manualPositions) {
        this.manualPositions = manualPositions;
    }
}
