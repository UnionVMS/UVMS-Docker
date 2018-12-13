package eu.europa.ec.fisheries.uvms.docker.validation.mobileterminal.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;
import java.time.Duration;
import java.util.UUID;

public class ChannelDto implements Serializable {

    private UUID id;
    private Boolean archived = false;
    @JsonIgnore
    private MobileTerminalDto mobileTerminal;
    private String name;
    private boolean active;
    private boolean defaultChannel;
    private boolean configChannel;
    private boolean pollChannel;
    private String DNID;
    private String expectedFrequency;
    private String expectedFrequencyInPort;
    private String frequencyGracePeriod;
    private String memberNumber;
    private String lesDescription;
    private String installedBy;

    public UUID getId() {
        return id;
    }
    public void setId(UUID id) {
        this.id = id;
    }
    public Boolean getArchived() {
        return archived;
    }
    public void setArchived(Boolean archived) {
        this.archived = archived;
    }
    public MobileTerminalDto getMobileTerminal() {
        return mobileTerminal;
    }
    public void setMobileTerminal(MobileTerminalDto mobileTerminal) {
        this.mobileTerminal = mobileTerminal;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public boolean isActive() {
        return active;
    }
    public void setActive(boolean active) {
        this.active = active;
    }
    public boolean isDefaultChannel() {
        return defaultChannel;
    }
    public void setDefaultChannel(boolean defaultChannel) {
        this.defaultChannel = defaultChannel;
    }
    public boolean isConfigChannel() {
        return configChannel;
    }
    public void setConfigChannel(boolean configChannel) {
        this.configChannel = configChannel;
    }
    public boolean isPollChannel() {
        return pollChannel;
    }
    public void setPollChannel(boolean pollChannel) {
        this.pollChannel = pollChannel;
    }
    public String getDNID() {
        return DNID;
    }
    public void setDNID(String DNID) {
        this.DNID = DNID;
    }
    public String getExpectedFrequency() {
        return expectedFrequency;
    }
    public void setExpectedFrequency(String expectedFrequency) {
        this.expectedFrequency = expectedFrequency;
    }
    public String getExpectedFrequencyInPort() {
        return expectedFrequencyInPort;
    }
    public void setExpectedFrequencyInPort(String expectedFrequencyInPort) {
        this.expectedFrequencyInPort = expectedFrequencyInPort;
    }
    public String getFrequencyGracePeriod() {
        return frequencyGracePeriod;
    }
    public void setFrequencyGracePeriod(String frequencyGracePeriod) {
        this.frequencyGracePeriod = frequencyGracePeriod;
    }
    public String getMemberNumber() {
        return memberNumber;
    }
    public void setMemberNumber(String memberNumber) {
        this.memberNumber = memberNumber;
    }
    public String getLesDescription() {
        return lesDescription;
    }
    public void setLesDescription(String lesDescription) {
        this.lesDescription = lesDescription;
    }
    public String getInstalledBy() {
        return installedBy;
    }
    public void setInstalledBy(String installedBy) {
        this.installedBy = installedBy;
    }
}
