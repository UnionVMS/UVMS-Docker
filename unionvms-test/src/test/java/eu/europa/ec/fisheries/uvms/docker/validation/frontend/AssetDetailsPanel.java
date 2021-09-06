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
package eu.europa.ec.fisheries.uvms.docker.validation.frontend;

import static com.codeborne.selenide.Selenide.$;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.codeborne.selenide.SelenideElement;

public class AssetDetailsPanel {

    private Map<String, String> information = new HashMap<>();
    
    private Map<String, String> extractVesselInformation() {
        SelenideElement assetInformation = $("div.asset-information");
        List<SelenideElement> legends = assetInformation.$$("legend.ng-star-inserted");
        for (SelenideElement legend : legends) {
            information.put(legend.text(), legend.sibling(0).text());
        }
        return information;
    }
    
    public String getIrcs() {
        Map<String, String> information = extractVesselInformation();
        return information.get("Ircs:"); 
    }

    public String getMmsi() {
        Map<String, String> information = extractVesselInformation();
        return information.get("Mmsi:"); 
    }

    public String getFlagstate() {
        Map<String, String> information = extractVesselInformation();
        return information.get("Flagstate:"); 
    }

    public String getExternalMarking() {
        Map<String, String> information = extractVesselInformation();
        return information.get("External marking:"); 
    }

    public String getLength() {
        Map<String, String> information = extractVesselInformation();
        return information.get("Length:"); 
    }
}
