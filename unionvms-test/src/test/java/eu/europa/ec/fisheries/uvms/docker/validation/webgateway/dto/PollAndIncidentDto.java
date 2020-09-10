package eu.europa.ec.fisheries.uvms.docker.validation.webgateway.dto;

import eu.europa.ec.fisheries.uvms.incident.model.dto.IncidentDto;

public class PollAndIncidentDto {
    String pollId;

    IncidentDto incident;

    public PollAndIncidentDto() {
    }

    public PollAndIncidentDto(String pollId, IncidentDto incident) {
        this.pollId = pollId;
        this.incident = incident;
    }

    public String getPollId() {
        return pollId;
    }

    public void setPollId(String pollId) {
        this.pollId = pollId;
    }

    public IncidentDto getIncident() {
        return incident;
    }

    public void setIncident(IncidentDto incident) {
        this.incident = incident;
    }
}
