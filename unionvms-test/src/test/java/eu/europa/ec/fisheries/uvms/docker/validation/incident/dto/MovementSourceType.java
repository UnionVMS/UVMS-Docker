package eu.europa.ec.fisheries.uvms.docker.validation.incident.dto;

public enum MovementSourceType {
    INMARSAT_C,
    AIS,
    IRIDIUM,
    MANUAL,
    OTHER,
    NAF;

    MovementSourceType() {
    }

    public String value() {
        return this.name();
    }

    public static MovementSourceType fromValue(String v) {
        return valueOf(v);
    }
}