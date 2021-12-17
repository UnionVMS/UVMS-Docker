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

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selectors.byId;
import static com.codeborne.selenide.Selectors.byTagName;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;

public class MapInformationPanel {

    protected MapInformationPanel() {
        $(byId("realtime-right-column-menu")).$(byTagName("li"), 0).click();
    }

    public int getAssetsShowing() {
        return Integer.valueOf($(byText("Showing")).parent().$(byTagName("div"), 0).text());
    }

    public void assertFilteredAssetShowing(int expectedAssetsShowing) {
        $(byText("Showing")).parent().$(byTagName("div"), 0)
            .shouldHave(text(String.valueOf(expectedAssetsShowing)));
    }
}
