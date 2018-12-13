package eu.europa.ec.fisheries.uvms.docker.validation.mobileterminal.dto;

import java.io.Serializable;

public class MobileTerminalPluginDto implements Serializable {

    private String PluginServiceName;
    private String name;
    private String pluginSatelliteType;
    private Boolean pluginInactive;

    public String getPluginServiceName() {
        return PluginServiceName;
    }

    public void setPluginServiceName(String pluginServiceName) {
        PluginServiceName = pluginServiceName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPluginSatelliteType() {
        return pluginSatelliteType;
    }

    public void setPluginSatelliteType(String pluginSatelliteType) {
        this.pluginSatelliteType = pluginSatelliteType;
    }

    public Boolean getPluginInactive() {
        return pluginInactive;
    }

    public void setPluginInactive(Boolean pluginInactive) {
        this.pluginInactive = pluginInactive;
    }
}
