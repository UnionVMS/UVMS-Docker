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
import static com.codeborne.selenide.Selectors.byAttribute;
import static com.codeborne.selenide.Selectors.byTagName;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.Selectors;
import eu.europa.ec.fisheries.uvms.asset.client.model.AssetDTO;

public class AssetSearchPage {

    private ElementsCollection searchResults = $(byTagName("tbody")).$$(byTagName("tr"));

    protected AssetSearchPage() {
        open("/asset");
    }

    public void searchAsset(String searchQuery) {
        $(byAttribute("name", "Search"))
            .setValue(searchQuery)
            .pressEnter();
    }

    public AssetPage clickOnResultRow(int row) {
        searchResults.get(0).click();
        return new AssetPage();
    }

    public void assertSearchResultSize(int expectedSize) {
        searchResults.shouldHave(size(expectedSize));
    }

    public void assertSearchResultAtPosition(int index, AssetDTO asset) {
        ElementsCollection searchResultRow = searchResults.get(index)
            .$$(Selectors.byTagName("td"));
        searchResultRow.get(0).shouldHave(text(asset.getExternalMarking()));
        searchResultRow.get(1).shouldHave(text(asset.getIrcs()));
        searchResultRow.get(2).shouldHave(text(asset.getName()));
        searchResultRow.get(3).shouldHave(text(asset.getCfr()));
        searchResultRow.get(4).shouldHave(text(asset.getFlagStateCode()));
        searchResultRow.get(5).shouldHave(text(asset.getMmsi()));
    }
}
