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
package eu.europa.ec.fisheries.uvms.docker.validation.system.vms;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import org.junit.Test;
import eu.europa.ec.fisheries.schema.movement.v1.MovementSourceType;
import eu.europa.ec.fisheries.uvms.asset.client.model.AssetDTO;
import eu.europa.ec.fisheries.uvms.docker.validation.asset.AssetTestHelper;
import eu.europa.ec.fisheries.uvms.docker.validation.mobileterminal.MobileTerminalTestHelper;
import eu.europa.ec.fisheries.uvms.docker.validation.mobileterminal.dto.MobileTerminalDto;
import eu.europa.ec.fisheries.uvms.docker.validation.mobileterminal.dto.MobileTerminalPluginDto;
import eu.europa.ec.fisheries.uvms.docker.validation.movement.LatLong;
import eu.europa.ec.fisheries.uvms.docker.validation.movement.MovementHelper;
import eu.europa.ec.fisheries.uvms.docker.validation.system.helper.SiriusOnePluginMock;
import eu.europa.ec.fisheries.uvms.mobileterminal.model.constants.MobileTerminalTypeEnum;
import eu.europa.ec.fisheries.uvms.movement.model.dto.MovementDto;

public class SiriusOneSystemIT {

    @Test
    public void siriousOnePositionTest() throws Exception {
        AssetDTO asset = AssetTestHelper.createTestAsset();
        
        MobileTerminalDto terminal = MobileTerminalTestHelper.createBasicMobileTerminal();
        terminal.setMobileTerminalType(MobileTerminalTypeEnum.IRIDIUM.toString());

        MobileTerminalPluginDto plugin = new MobileTerminalPluginDto();
        plugin.setPluginServiceName("eu.europa.ec.fisheries.uvms.plugins.iridium.siriusone");
        terminal.setPlugin(plugin);
        
        MobileTerminalDto mobileTerminal = MobileTerminalTestHelper.persistMobileTerminal(terminal);
        MobileTerminalTestHelper.assignMobileTerminal(asset, mobileTerminal);
        
        LatLong position = new LatLong(11d, 56d, new Date());
        SiriusOnePluginMock.sendSiriusOnePosition(mobileTerminal, position);
        
        MovementHelper.pollMovementCreated();
        
        List<MovementDto> latestMovements = MovementHelper.getLatestMovements(Arrays.asList(asset.getId().toString()));
        
        assertThat(latestMovements.size(), is(1));
        MovementDto movement = latestMovements.get(0);
        assertThat(movement.getSource(), is(MovementSourceType.IRIDIUM));
        assertThat(movement.getLocation().getLatitude(), is(position.latitude));
        assertThat(movement.getLocation().getLongitude(), is(position.longitude));
        assertThat(movement.getTimestamp(), is(position.positionTime.toInstant()));
    }
}
