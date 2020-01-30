package eu.europa.ec.fisheries.uvms.docker.validation.movement.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.InstantSerializer;
import eu.europa.ec.fisheries.schema.movement.v1.MovementPoint;
import eu.europa.ec.fisheries.schema.movement.v1.MovementSourceType;
import eu.europa.ec.fisheries.uvms.movement.model.MovementInstantDeserializer;

import java.time.Instant;

public class MicroMovement {

    private MovementPoint location;

    private Double heading;

    private String guid;

    @JsonSerialize(using = InstantSerializer.class)
    @JsonDeserialize(using = MovementInstantDeserializer.class)
    private Instant timestamp;

    private Double speed;

    private MovementSourceType source;

    public MicroMovement() {}

    public MovementPoint getLocation() {
        return location;
    }

    public void setLocation(MovementPoint location) {
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

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
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
