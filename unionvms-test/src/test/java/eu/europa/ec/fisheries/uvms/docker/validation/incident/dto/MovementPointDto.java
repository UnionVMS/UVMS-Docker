package eu.europa.ec.fisheries.uvms.docker.validation.incident.dto;

import java.io.Serializable;

public class MovementPointDto implements Serializable {
    private final static long serialVersionUID = 1L;

    protected double longitude;
    protected double latitude;
    protected double altitude;

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public Double getAltitude() {
        return altitude;
    }

    public void setAltitude(double altitude) {
        this.altitude = altitude;
    }
}
