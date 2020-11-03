package eu.europa.ec.fisheries.uvms.docker.validation.webgateway.dto;

public class PollIdDto {

    String pollId;

    public PollIdDto() {
    }

    public PollIdDto(String pollId) {
        this.pollId = pollId;
    }

    public String getPollId() {
        return pollId;
    }

    public void setPollId(String pollId) {
        this.pollId = pollId;
    }
}
