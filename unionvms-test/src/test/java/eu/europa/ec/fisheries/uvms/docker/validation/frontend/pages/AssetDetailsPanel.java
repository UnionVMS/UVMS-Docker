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

import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selectors.by;
import static com.codeborne.selenide.Selectors.byClassName;
import static com.codeborne.selenide.Selectors.byId;
import static com.codeborne.selenide.Selectors.byTagName;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import com.codeborne.selenide.SelenideElement;

public class AssetDetailsPanel {

    private SelenideElement assetInformation = $(byClassName("asset-information"));

    protected AssetDetailsPanel() {
        initPanel();
    }

    private void initPanel() {
        $(byId("realtime-right-column-menu")).$(byTagName("li"), 1).click();
    }

    public void assertIrcs(String expectedIrcs) {
        assetInformation.$(byText("Ircs:"))
            .sibling(0)
            .shouldHave(text(expectedIrcs));
    }

    public void assertMmsi(String expectedMmsi) {
        assetInformation.$(byText("Mmsi:"))
            .sibling(0)
            .shouldHave(text(expectedMmsi));
    }

    public void assertFlagstate(String expectedFlagstate) {
        assetInformation.$(byText("Flagstate:"))
            .sibling(0)
            .shouldHave(text(expectedFlagstate));
    }

    public void assertExternalMarking(String expectedExternalMarking) {
        assetInformation.$(byText("External marking:"))
            .sibling(0)
            .shouldHave(text(expectedExternalMarking));
    }

    public void assertLength(String expectedLength) {
        assetInformation.$(byText("Length:"))
            .sibling(0)
            .shouldHave(text(expectedLength));
    }

    public void sendManuallPoll(String comment) {
        initPanel();
        $(byText("Manage polling")).click();
        $(by("formcontrolname", "comment")).setValue(comment);
        $(byId("manual-poll-form--save")).click();
    }

    public void assertLast24hPollHistoryContainsItems(int expectedSize) {
        initPanel();
        $(byText("Manage polling")).click();
        $(byText("Last 24 hours history")).click();
        $$(byTagName("map-asset-poll-manual"))
            .shouldHave(size(expectedSize));
    }
}
