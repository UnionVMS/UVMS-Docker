package eu.europa.ec.fisheries.uvms.docker.validation.incident.dto;

public class MicroMovementDto {

    private MovementPointDto location;

    private Double heading;

    private String guid;

    private long timestamp;

    private Double speed;

    private MovementSourceType source;

    public MovementPointDto getLocation() {
        return location;
    }

    public void setLocation(MovementPointDto location) {
        this.location = location;
    }

    public Double getHeading() {
        return heading;
    }

    public void setHeading(Double heading) {
        this.heading = heading;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public Double getSpeed() {
        return speed;
    }

    public void setSpeed(Double speed) {
        this.speed = speed;
    }

    public MovementSourceType getSource() {
        return source;
    }

    public void setSource(MovementSourceType source) {
        this.source = source;
    }
}
