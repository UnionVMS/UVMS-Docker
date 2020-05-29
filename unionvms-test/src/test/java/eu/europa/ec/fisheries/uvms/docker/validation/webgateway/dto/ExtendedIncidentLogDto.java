package eu.europa.ec.fisheries.uvms.docker.validation.webgateway.dto;

import eu.europa.ec.fisheries.uvms.incident.model.dto.IncidentLogDto;

public class ExtendedIncidentLogDto {

    private IncidentLogDto incidentLog;

    private Object relatedObject;

    public ExtendedIncidentLogDto() {
    }

    public ExtendedIncidentLogDto(IncidentLogDto incidentLog, Object relatedObject) {
        this.incidentLog = incidentLog;
        this.relatedObject = relatedObject;
    }

    public IncidentLogDto getIncidentLog() {
        return incidentLog;
    }

    public void setIncidentLog(IncidentLogDto incidentLog) {
        this.incidentLog = incidentLog;
    }

    public Object getRelatedObject() {
        return relatedObject;
    }

    public void setRelatedObject(Object relatedObject) {
        this.relatedObject = relatedObject;
    }
}
