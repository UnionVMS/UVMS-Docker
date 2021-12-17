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

import java.util.Date;
import java.util.Random;
import org.junit.Test;
import eu.europa.ec.fisheries.uvms.asset.client.model.AssetDTO;
import eu.europa.ec.fisheries.uvms.docker.validation.asset.AssetTestHelper;
import eu.europa.ec.fisheries.uvms.docker.validation.common.TopicListener;
import eu.europa.ec.fisheries.uvms.docker.validation.frontend.pages.AssetDetailsPanel;
import eu.europa.ec.fisheries.uvms.docker.validation.frontend.pages.RealtimeMapPage;
import eu.europa.ec.fisheries.uvms.docker.validation.frontend.pages.UnionVMS;
import eu.europa.ec.fisheries.uvms.docker.validation.movement.LatLong;
import eu.europa.ec.fisheries.uvms.docker.validation.system.helper.FLUXHelper;

public class RealtimeMapTabIT {

    @Test
    public void clickOnAssetTest() throws Exception {
        UnionVMS uvms = UnionVMS.login();
        AssetDTO asset = AssetTestHelper.createTestAsset();
        int randomLatitude = new Random().nextInt(90);
        int randomLongitude = new Random().nextInt(180);
        LatLong position = new LatLong(randomLatitude, randomLongitude, new Date());
        try (TopicListener topicListener = new TopicListener(TopicListener.EVENT_STREAM, null)) {
            FLUXHelper.sendPositionToFluxPlugin(asset, position);
            topicListener.listenOnEventBus();
        }

        RealtimeMapPage realtime = uvms.realtimeMapPage();
        realtime.gotoCoordinates(position.latitude, position.longitude);
        realtime.clickOnCenter();
        AssetDetailsPanel assetDetailsPanel = realtime.assetDetailsPanel();
        assetDetailsPanel.assertIrcs(asset.getIrcs());
        assetDetailsPanel.assertMmsi(asset.getMmsi());
        assetDetailsPanel.assertExternalMarking(asset.getExternalMarking());
    }

}
