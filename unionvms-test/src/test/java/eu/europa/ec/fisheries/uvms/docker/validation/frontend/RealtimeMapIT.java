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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import java.util.Date;
import org.junit.Test;
import eu.europa.ec.fisheries.uvms.asset.client.model.AssetDTO;
import eu.europa.ec.fisheries.uvms.docker.validation.asset.AssetTestHelper;
import eu.europa.ec.fisheries.uvms.docker.validation.movement.LatLong;
import eu.europa.ec.fisheries.uvms.docker.validation.system.helper.FLUXHelper;

public class RealtimeMapIT {

    @Test
    public void clickOnAssetTest() throws Exception {
        RealtimeMapUI realtime = new RealtimeMapUI();
        AssetDTO asset = AssetTestHelper.createTestAsset();
        LatLong position = new LatLong(57d, 11d, new Date());
        FLUXHelper.sendPositionToFluxPlugin(asset, position);
        realtime.gotoCoordinates(position.latitude, position.longitude);
        realtime.clickOnCenter();
        AssetDetailsPanel assetDetails = realtime.getAssetDetailsPanel();
        assertThat(assetDetails.getIrcs(), is(asset.getIrcs()));
        assertThat(assetDetails.getMmsi(), is(asset.getMmsi()));
        assertThat(assetDetails.getExternalMarking(), is(asset.getExternalMarking()));
    }

}
