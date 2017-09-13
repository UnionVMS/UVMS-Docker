package eu.europa.ec.fisheries.uvms.docker.validation.spatial;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ConfigResourceDto {

    @JsonProperty("timeStamp")
    private String timeStamp;

    @JsonProperty("timeStamp")
    public String getTimeStamp() {
        return timeStamp;
    }

    @JsonProperty("timeStamp")
    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }
}
