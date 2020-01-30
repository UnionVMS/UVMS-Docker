package eu.europa.ec.fisheries.uvms.docker.validation.incident.dto;

import java.io.Serializable;

public class TicketDto implements Serializable {

    private enum TicketType {
        ASSET_NOT_SENDING,
        ASSET_SENDING_NORMAL
    }

    private TicketType type;
    private String ticketId;
    private String assetId;
    private String mobTermId;
    private String movementId;
    private String status;

    public TicketType getType() {
        return type;
    }

    public void setType(TicketType type) {
        this.type = type;
    }

    public String getTicketId() {
        return ticketId;
    }

    public void setTicketId(String ticketId) {
        this.ticketId = ticketId;
    }

    public String getAssetId() {
        return assetId;
    }

    public void setAssetId(String assetId) {
        this.assetId = assetId;
    }

    public String getMobTermId() {
        return mobTermId;
    }

    public void setMobTermId(String mobTermId) {
        this.mobTermId = mobTermId;
    }

    public String getMovementId() {
        return movementId;
    }

    public void setMovementId(String movementId) {
        this.movementId = movementId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "TicketDto{" +
                "ticketId='" + ticketId + '\'' +
                ", assetId='" + assetId + '\'' +
                ", mobTermId='" + mobTermId + '\'' +
                ", movementId='" + movementId + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
