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
import static com.codeborne.selenide.Selectors.by;
import static com.codeborne.selenide.Selectors.byClassName;
import static com.codeborne.selenide.Selectors.byId;
import static com.codeborne.selenide.Selectors.byTagName;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selectors.byTitle;
import static com.codeborne.selenide.Selenide.$;

public class MapFilterPanel {

    protected MapFilterPanel() {
        $(byId("realtime-left-column-menu")).$(byTagName("li"), 0).click();
    }

    public void setFilter(String filterString) {
        $(by("data-placeholder", "Filter"))
            .setValue(filterString);
    }

    public void createFilter(String filterName) {
        $(byText("Create new filter")).click();
        $(by("data-placeholder", "Name")).setValue(filterName);
        $(byText("Save filter")).click();
    }

    public void deleteFilter(String filterName) {
        $(byClassName("saved-filters"))
            .$(byTitle(filterName))
            .hover()
            .sibling(0)
            .click();
        $(byText("Delete")).click();
    }

    public void assertSavedFilterExists(String expectedFilterName) {
        $(byClassName("saved-filters"))
            .$(byTitle(expectedFilterName))
            .should(exist);
    }

    public void assertSavedFilterNotExists(String expectedFilterName) {
        $(byClassName("saved-filters"))
            .$(byTitle(expectedFilterName))
            .shouldNot(exist);
    }
}
