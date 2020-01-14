package eu.europa.ec.fisheries.uvms.docker.validation.incident.dto;

import java.io.Serializable;
import java.util.UUID;

public class IncidentDto implements Serializable {
    private final static long serialVersionUID = 1L;

    private long id;
    private UUID assetId;
    private UUID mobileTerminalId;
    private UUID ticketId;
    private String assetName;
    private String assetIrcs;
    private String status;
    private long createDate;
    private long updateDate;
    private MicroMovementDto lastKnownLocation;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public UUID getAssetId() {
        return assetId;
    }

    public void setAssetId(UUID assetId) {
        this.assetId = assetId;
    }

    public UUID getMobileTerminalId() {
        return mobileTerminalId;
    }

    public void setMobileTerminalId(UUID mobileTerminalId) {
        this.mobileTerminalId = mobileTerminalId;
    }

    public UUID getTicketId() {
        return ticketId;
    }

    public void setTicketId(UUID ticketId) {
        this.ticketId = ticketId;
    }

    public String getAssetName() {
        return assetName;
    }

    public void setAssetName(String assetName) {
        this.assetName = assetName;
    }

    public String getAssetIrcs() {
        return assetIrcs;
    }

    public void setAssetIrcs(String assetIrcs) {
        this.assetIrcs = assetIrcs;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public MicroMovementDto getLastKnownLocation() {
        return lastKnownLocation;
    }

    public void setLastKnownLocation(MicroMovementDto lastKnownLocation) {
        this.lastKnownLocation = lastKnownLocation;
    }

    public long getCreateDate() {
        return createDate;
    }

    public void setCreateDate(long createDate) {
        this.createDate = createDate;
    }

    public long getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(long updateDate) {
        this.updateDate = updateDate;
    }
}
