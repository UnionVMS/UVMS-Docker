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

import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;
import java.util.UUID;
import com.codeborne.selenide.Condition;

public class AssetPage {

    protected AssetPage() {
    }

    protected AssetPage(UUID assetId) {
        open("/asset/" + assetId.toString());
    }

    public AssetNotesPage assetNotesPage() {
        return new AssetNotesPage();
    }

    public void assertFlagstate(String expectedFlagstate) {
        assertAssetAttribute("Flagstate", expectedFlagstate);
    }

    public void assertExternalMarking(String expectedExternalMarking) {
        assertAssetAttribute("External marking", expectedExternalMarking);
    }

    public void assertCfr(String expectedCfr) {
        assertAssetAttribute("CFR", expectedCfr);
    }

    public void assertIrcs(String expectedIrcs) {
        assertAssetAttribute("IRCS", expectedIrcs);
    }

    public void assertImo(String expectedImo) {
        assertAssetAttribute("IMO", expectedImo);
    }

    public void assertMmsi(String expectedMmsi) {
        assertAssetAttribute("MMSI", expectedMmsi);
    }

    private void assertAssetAttribute(String name, String expectedValue) {
        $(byText(name))
            .parent()
            .sibling(0)
            .shouldHave(Condition.text(expectedValue));
    }
}
