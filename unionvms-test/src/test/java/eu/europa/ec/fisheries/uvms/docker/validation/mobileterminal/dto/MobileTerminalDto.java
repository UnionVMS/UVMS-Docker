package eu.europa.ec.fisheries.uvms.docker.validation.mobileterminal.dto;

import eu.europa.ec.fisheries.uvms.asset.client.model.AssetDTO;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class MobileTerminalDto implements Serializable {

    private UUID id;
    private Boolean archived = false;
    private Boolean inactivated = false;
    private String source;
    private String mobileTerminalType;
    private String updateuser;
    private String serialNo;
    private MobileTerminalPluginDto plugin;
    private ChannelDto defaultChannel;
    private ChannelDto configChannel;
    private ChannelDto pollChannel;
    private AssetDTO asset;
    private Set<ChannelDto> channels;
    private String satelliteNumber;
    private String antenna;
    private String transceiverType;
    private String softwareVersion;

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
    public Boolean getInactivated() {
        return inactivated;
    }
    public void setInactivated(Boolean inactivated) {
        this.inactivated = inactivated;
    }
    public String getSource() {
        return source;
    }
    public void setSource(String source) {
        this.source = source;
    }
    public String getMobileTerminalType() {
        return mobileTerminalType;
    }
    public void setMobileTerminalType(String mobileTerminalType) {
        this.mobileTerminalType = mobileTerminalType;
    }
    public String getUpdateuser() {
        return updateuser;
    }
    public void setUpdateuser(String updateuser) {
        this.updateuser = updateuser;
    }
    public String getSerialNo() {
        return serialNo;
    }
    public void setSerialNo(String serialNo) {
        this.serialNo = serialNo;
    }
    public MobileTerminalPluginDto getPlugin() {
        return plugin;
    }
    public void setPlugin(MobileTerminalPluginDto plugin) {
        this.plugin = plugin;
    }
    public ChannelDto getDefaultChannel() {
        return defaultChannel;
    }
    public void setDefaultChannel(ChannelDto defaultChannel) {
        this.defaultChannel = defaultChannel;
    }
    public ChannelDto getConfigChannel() {
        return configChannel;
    }
    public void setConfigChannel(ChannelDto configChannel) {
        this.configChannel = configChannel;
    }
    public ChannelDto getPollChannel() {
        return pollChannel;
    }
    public void setPollChannel(ChannelDto pollChannel) {
        this.pollChannel = pollChannel;
    }
    public AssetDTO getAsset() {
        return asset;
    }
    public void setAsset(AssetDTO asset) {
        this.asset = asset;
    }
    public Set<ChannelDto> getChannels() {
        if(channels == null)
            channels = new HashSet<>();
        return channels;
    }
    public void setChannels(Set<ChannelDto> channels) {
        this.channels = channels;
    }
    public String getSatelliteNumber() {
        return satelliteNumber;
    }
    public void setSatelliteNumber(String satelliteNumber) {
        this.satelliteNumber = satelliteNumber;
    }
    public String getAntenna() {
        return antenna;
    }
    public void setAntenna(String antenna) {
        this.antenna = antenna;
    }
    public String getTransceiverType() {
        return transceiverType;
    }
    public void setTransceiverType(String transceiverType) {
        this.transceiverType = transceiverType;
    }
    public String getSoftwareVersion() {
        return softwareVersion;
    }
    public void setSoftwareVersion(String softwareVersion) {
        this.softwareVersion = softwareVersion;
    }
}
