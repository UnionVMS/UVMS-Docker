/*
﻿Developed with the contribution of the European Commission - Directorate General for Maritime Affairs and Fisheries
© European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can
redistribute it and/or modify it under the terms of the GNU General Public License as published by the
Free Software Foundation, either version 3 of the License, or any later version. The IFDM Suite is distributed in
the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. You should have received a
copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.
 */
package eu.europa.ec.fisheries.uvms.docker.validation.frontend.pages;

import static com.codeborne.selenide.Selenide.*;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import eu.europa.ec.fisheries.uvms.docker.validation.mobileterminal.dto.ChannelDto;
import eu.europa.ec.fisheries.uvms.docker.validation.mobileterminal.dto.MobileTerminalDto;
import static com.codeborne.selenide.Selectors.*;

public class AssetMobileTerminalEditPage {

    protected AssetMobileTerminalEditPage() {}

    public void populateFieldsFromMobileTerminal(MobileTerminalDto mobileTerminal) {
        setTransceiverType(mobileTerminal.getTransceiverType());
        setSoftwareVersion(mobileTerminal.getSoftwareVersion());
        setAntenna(mobileTerminal.getAntenna());
        setSatelliteNumber(mobileTerminal.getSatelliteNumber());
        ChannelDto channel = mobileTerminal.getChannels().iterator().next();
        setChannelName(channel.getName());
        setLesDescription(channel.getLesDescription());
        setMemberNumber(channel.getMemberNumber());
        setDnid(channel.getDnid());
        setPollChannel(channel.isPollChannel());
        setConfigChannel(channel.isConfigChannel());
        setDefaultChannel(channel.isDefaultChannel());
        if (channel.getStartDate() != null) {
            setStarted(channel.getStartDate());
        }
        if (channel.getEndDate() != null) {
            setStopped(channel.getEndDate());
        }
    }

    public void setTransceiverType(String transceiverType) {
        setFormValue("transceiverType", transceiverType);
    }

    public void setSoftwareVersion(String softwareVersion) {
        setFormValue("softwareVersion", softwareVersion);
    }

    public void setAntenna(String antenna) {
        setFormValue("antenna", antenna);
    }

    public void setSatelliteNumber(String satelliteNumber) {
        setFormValue("satelliteNumber", satelliteNumber);
    }

    public void setInstalledBy(String installedBy) {
        setFormValue("installedBy", installedBy);
    }

    public void setInstalledOn(Instant installedDate) {
        String installedDateString = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm").format(installedDate.atZone(ZoneId.of("UTC")));
        $(by("label", "Installed on")).$(byTagName("input")).setValue(installedDateString);
    }

    public void setUninstalledOn(Instant uninstalledDate) {
        String uninstalledDateString = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm").format(uninstalledDate.atZone(ZoneId.of("UTC")));
        $(by("label", "Uninstalled on")).$(byTagName("input")).setValue(uninstalledDateString);
    }

    public void setChannelName(String channelName) {
        setFormValue("name", channelName);
    }

    public void setLesDescription(String lesDescription) {
        setFormValue("lesDescription", lesDescription);
    }

    public void setDnid(String dnid) {
        setFormValue("dnid", dnid);
    }

    public void setMemberNumber(String memberNumber) {
        $(by("formcontrolname", "memberNumber")).setValue(memberNumber);
    }

    public void setPollChannel(boolean pollChannel) {
        if (pollChannel) {
            $(byText("Poll")).parent().click();
        }
    }

    public void setConfigChannel(boolean configChannel) {
        if (configChannel) {
            $(byText("Config")).parent().click();
        }
    }

    public void setDefaultChannel(boolean defaultChannel) {
        if (defaultChannel) {
            $(byText("Default")).parent().click();
        }
    }

    public void setStarted(Instant started) {
        String startedDateString = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm").format(started.atZone(ZoneId.of("UTC")));
        $(by("label", "Started")).$(byTagName("input")).setValue(startedDateString);
    }

    public void setStopped(Instant stopped) {
        String stoppedDateString = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm").format(stopped.atZone(ZoneId.of("UTC")));
        $(by("label", "Stopped")).$(byTagName("input")).setValue(stoppedDateString);
    }

    public void saveChanges(String comment) {
        $(byText("Save changes")).click();
        $(byTagName("textarea")).setValue(comment);
        $(byText("Save mobile terminal")).click();
    }

    private void setFormValue(String formcontrolname, String value) {
        $(by("formcontrolname", formcontrolname)).setValue(value);
    }
}
