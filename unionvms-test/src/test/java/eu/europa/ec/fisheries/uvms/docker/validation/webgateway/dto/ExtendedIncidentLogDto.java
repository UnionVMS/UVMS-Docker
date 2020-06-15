package eu.europa.ec.fisheries.uvms.docker.validation.webgateway.dto;

import eu.europa.ec.fisheries.uvms.incident.model.dto.IncidentLogDto;

import java.util.HashMap;
import java.util.Map;

public class ExtendedIncidentLogDto {

    private Map<Long, IncidentLogDto> incidentLogs;

    private RelatedObjectDto relatedObjects = new RelatedObjectDto();

    public ExtendedIncidentLogDto() {
        incidentLogs = new HashMap<>();
    }

    public ExtendedIncidentLogDto(int amountOfLogs) {
        incidentLogs = new HashMap<>(amountOfLogs);
    }

    public Map<Long, IncidentLogDto> getIncidentLogs() {
        return incidentLogs;
    }

    public void setIncidentLogs(Map<Long, IncidentLogDto> incidentLogs) {
        this.incidentLogs = incidentLogs;
    }

    public RelatedObjectDto getRelatedObjects() {
        return relatedObjects;
    }

    public void setRelatedObjects(RelatedObjectDto relatedObjects) {
        this.relatedObjects = relatedObjects;
    }
}
