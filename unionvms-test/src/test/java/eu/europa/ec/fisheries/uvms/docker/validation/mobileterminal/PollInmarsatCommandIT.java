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
package eu.europa.ec.fisheries.uvms.docker.validation.mobileterminal;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Test;
import eu.europa.ec.fisheries.schema.exchange.common.v1.CommandTypeType;
import eu.europa.ec.fisheries.schema.exchange.common.v1.KeyValueType;
import eu.europa.ec.fisheries.schema.exchange.plugin.v1.SetCommandRequest;
import eu.europa.ec.fisheries.uvms.asset.client.model.AssetDTO;
import eu.europa.ec.fisheries.uvms.docker.validation.asset.AssetTestHelper;
import eu.europa.ec.fisheries.uvms.docker.validation.common.AbstractRest;
import eu.europa.ec.fisheries.uvms.docker.validation.mobileterminal.dto.ChannelDto;
import eu.europa.ec.fisheries.uvms.docker.validation.mobileterminal.dto.MobileTerminalDto;
import eu.europa.ec.fisheries.uvms.docker.validation.system.helper.PollHelper;

public class PollInmarsatCommandIT extends AbstractRest {
    
    @Test
    public void sendPollAndVerifySetCommandValues() throws Exception {
        AssetDTO testAsset = AssetTestHelper.createTestAsset();
        MobileTerminalDto mobileTerminal = MobileTerminalTestHelper.createMobileTerminal();
        
        SetCommandRequest command = PollHelper.createPollAndReturnSetCommandRequest(testAsset, mobileTerminal);
        
        assertThat(command, is(notNullValue()));
        assertThat(command.getCommand().getCommand(), is(CommandTypeType.POLL));
        
        List<KeyValueType> pollReceiverValues = command.getCommand().getPoll().getPollReceiver();
        Map<String, String> receiverValuesMap = new HashMap<>();
        for (KeyValueType keyValueType : pollReceiverValues) {
            receiverValuesMap.put(keyValueType.getKey(), keyValueType.getValue());
        }
        assertThat(receiverValuesMap.get("SATELLITE_NUMBER"), is(mobileTerminal.getSatelliteNumber()));
        
        ChannelDto channel = mobileTerminal.getChannels().iterator().next();
        assertThat(receiverValuesMap.get("DNID"), is(channel.getDNID()));
        assertThat(receiverValuesMap.get("MEMBER_NUMBER"), is(channel.getMemberNumber()));
    }

}
