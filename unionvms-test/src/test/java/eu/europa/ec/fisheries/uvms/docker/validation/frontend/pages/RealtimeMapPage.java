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

import static com.codeborne.selenide.Selectors.by;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.actions;
import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.Selenide.sleep;

public class RealtimeMapPage {

    public static final int DEFAULT_ANIMATION_DURATION = 1000;

    protected RealtimeMapPage() {
        open("/map/realtime");
    }

    public RealtimeMapPage gotoCoordinates(Double latitude, Double longitude) {
        $(by("data-placeholder", "Search"))
            .setValue(String.format("/c %s %s", latitude, longitude))
            .pressEnter();
        sleep(DEFAULT_ANIMATION_DURATION * 2);
        return this;
    }

    public RealtimeMapPage clickOnCenter() {
        actions().moveToElement($("#realtime-map canvas"), 0, 0)
                 .click()
                 .perform();
        return this;
    }

    public MapFilterPanel mapFilterPanel() {
        return new MapFilterPanel();
    }

    public WorkflowsPanel workflowsPanel() {
        return new WorkflowsPanel();
    }

    public MapInformationPanel mapInformationPanel() {
        return new MapInformationPanel();
    }

    public AssetDetailsPanel assetDetailsPanel() {
        return new AssetDetailsPanel();
    }
}
