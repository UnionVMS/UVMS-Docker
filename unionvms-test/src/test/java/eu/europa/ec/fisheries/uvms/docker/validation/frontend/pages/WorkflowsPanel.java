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

import static com.codeborne.selenide.Condition.exist;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selectors.byClassName;
import static com.codeborne.selenide.Selectors.byId;
import static com.codeborne.selenide.Selectors.byTagName;
import static com.codeborne.selenide.Selectors.byTitle;
import static com.codeborne.selenide.Selectors.withText;
import static com.codeborne.selenide.Selenide.$;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import org.openqa.selenium.By.ByClassName;

public class WorkflowsPanel {

    protected WorkflowsPanel() {
        $(byId("realtime-left-column-menu")).$(byTagName("li"), 1).click();
    }

    public void showAssetNotSending() {
        $(withText("Asset not sending")).click();
    }

    public void showParked() {
        $(withText("Asset not sending")).click();
    }

    public IncidentPanel selectIncidentByAsset(String assetName) {
        $(byTitle(assetName)).click();
        return new IncidentPanel();
    }

    public void assertIncidentExists(String assetName) {
        $(byTitle(assetName))
            .should(exist);
    }

    public void assertIncidentIrcsByAsset(String assetName, String expectedIrcs) {
        $(byTitle(assetName))
            .$(byClassName("ircs"))
            .shouldHave(text(expectedIrcs));
    }

    public void assertIncidentPositionTimestampByAsset(String assetName, Instant expectedTimestamp) {
        String expectedDate = DateTimeFormatter.ofPattern("yyyy-MM-dd").format(expectedTimestamp.atZone(ZoneId.of("UTC")));
        String expectedTime = DateTimeFormatter.ofPattern("HH:mm").format(expectedTimestamp.atZone(ZoneId.of("UTC")));
        String expectedDateTime = expectedDate + " • " + expectedTime;
        $(byTitle(assetName))
            .$(byClassName("time"))
            .shouldHave(text(expectedDateTime));
    }
}
