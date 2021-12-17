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

import java.util.UUID;
import org.junit.Test;
import eu.europa.ec.fisheries.uvms.asset.client.model.AssetDTO;
import eu.europa.ec.fisheries.uvms.docker.validation.asset.AssetTestHelper;
import eu.europa.ec.fisheries.uvms.docker.validation.frontend.pages.AssetNotesPage;
import eu.europa.ec.fisheries.uvms.docker.validation.frontend.pages.AssetPage;
import eu.europa.ec.fisheries.uvms.docker.validation.frontend.pages.AssetSearchPage;
import eu.europa.ec.fisheries.uvms.docker.validation.frontend.pages.UnionVMS;

public class AssetTabIT {

    @Test
    public void searchAssetTest() throws InterruptedException {
        UnionVMS uvms = UnionVMS.login();
        AssetDTO asset = AssetTestHelper.createTestAsset();

        AssetSearchPage assetSearchPage = uvms.assetSearchPage();
        assetSearchPage.assertSearchResultSize(0);

        assetSearchPage.searchAsset(asset.getCfr());
        assetSearchPage.assertSearchResultSize(1);
        assetSearchPage.assertSearchResultAtPosition(0, asset);
    }

    @Test
    public void createNoteTest() throws InterruptedException {
        UnionVMS uvms = UnionVMS.login();

        AssetDTO asset = AssetTestHelper.createTestAsset();

        AssetPage assetPage = uvms.assetPage(asset.getId());
        AssetNotesPage assetNotesPage = assetPage.assetNotesPage();

        String note = "Test note: " + UUID.randomUUID().toString();
        assetNotesPage.assertNumberOfNotes(0);
        assetNotesPage.createNote(note);

        assetNotesPage.assertNumberOfNotes(1);
        assetNotesPage.assertNoteAtPosition(0, note);
    }

}
