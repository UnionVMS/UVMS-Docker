package eu.europa.ec.fisheries.uvms.docker.validation.webgateway.dto;

import eu.europa.ec.fisheries.uvms.incident.model.dto.IncidentLogDto;

import java.util.Map;

public class ExtendedIncidentLogDto {

    private IncidentLogDto incidentLog;

    private Map<String, String> relatedObject;

    public ExtendedIncidentLogDto() {
    }

    public ExtendedIncidentLogDto(IncidentLogDto incidentLog, Map<String, String> relatedObject) {
        this.incidentLog = incidentLog;
        this.relatedObject = relatedObject;
    }

    public IncidentLogDto getIncidentLog() {
        return incidentLog;
    }

    public void setIncidentLog(IncidentLogDto incidentLog) {
        this.incidentLog = incidentLog;
    }

    public Map<String, String> getRelatedObject() {
        return relatedObject;
    }

    public void setRelatedObject(Map<String, String> relatedObject) {
        this.relatedObject = relatedObject;
    }
}
