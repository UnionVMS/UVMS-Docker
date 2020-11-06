package eu.europa.ec.fisheries.uvms.docker.validation.webgateway.dto;

import eu.europa.ec.fisheries.schema.exchange.v1.ExchangeLogStatusType;
import eu.europa.ec.fisheries.uvms.asset.client.model.mt.MobileTerminal;
import eu.europa.ec.fisheries.uvms.docker.validation.asset.assetfilter.test.dto.SanePollDto;

public class PollInfoDto {

    SanePollDto pollInfo;

    ExchangeLogStatusType pollStatus;

    MobileTerminal mobileTerminalSnapshot;

    public PollInfoDto() {
    }

    public PollInfoDto(SanePollDto pollInfo, ExchangeLogStatusType pollStatus) {
        this.pollInfo = pollInfo;
        this.pollStatus = pollStatus;
    }

    public SanePollDto getPollInfo() {
        return pollInfo;
    }

    public void setPollInfo(SanePollDto pollInfo) {
        this.pollInfo = pollInfo;
    }

    public ExchangeLogStatusType getPollStatus() {
        return pollStatus;
    }

    public void setPollStatus(ExchangeLogStatusType pollStatus) {
        this.pollStatus = pollStatus;
    }

    public MobileTerminal getMobileTerminalSnapshot() {
        return mobileTerminalSnapshot;
    }

    public void setMobileTerminalSnapshot(MobileTerminal mobileTerminalSnapshot) {
        this.mobileTerminalSnapshot = mobileTerminalSnapshot;
    }
}
