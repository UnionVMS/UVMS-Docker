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

import eu.europa.ec.fisheries.schema.config.types.v1.SettingType;
import eu.europa.ec.fisheries.schema.mobileterminal.polltypes.v1.PollType;
import eu.europa.ec.fisheries.uvms.asset.client.model.AssetDTO;
import eu.europa.ec.fisheries.uvms.docker.validation.asset.AssetTestHelper;
import eu.europa.ec.fisheries.uvms.docker.validation.common.AbstractRest;
import eu.europa.ec.fisheries.uvms.docker.validation.mobileterminal.MobileTerminalTestHelper;
import eu.europa.ec.fisheries.uvms.docker.validation.mobileterminal.dto.ChannelDto;
import eu.europa.ec.fisheries.uvms.docker.validation.mobileterminal.dto.MobileTerminalDto;
import eu.europa.ec.fisheries.uvms.docker.validation.system.helper.LESMock;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;

public class InmarsatSystemIT extends AbstractRest {

    private static final int PORT = 29006;
    
    @BeforeClass
    public static void initSettings() throws SocketException, InterruptedException {
        String urlKey = "eu.europa.ec.fisheries.uvms.plugins.inmarsat.URL";
        String portKey = "eu.europa.ec.fisheries.uvms.plugins.inmarsat.PORT";
        
        List<SettingType> response = getWebTarget()
                .path("config/rest/settings")
                .queryParam("moduleName", "exchange")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getValidJwtToken())
                .get(new GenericType<List<SettingType>>() {});

        SettingType urlSetting = null;
        SettingType portSetting = null;
        for (SettingType settingType : response) {
            if (settingType.getKey().equals(urlKey)) {
                urlSetting = settingType;
            } else if (settingType.getKey().equals(portKey)) {
                portSetting = settingType;
            }
        }
        assertThat(urlSetting, is(notNullValue()));
        assertThat(portSetting, is(notNullValue()));

        urlSetting.setValue(getDockerHostIp());
        portSetting.setValue(String.valueOf(PORT));
        
        getWebTarget()
            .path("config/rest/settings")
            .path(urlSetting.getId().toString())
            .request(MediaType.APPLICATION_JSON)
            .header(HttpHeaders.AUTHORIZATION, getValidJwtToken())
            .put(Entity.json(urlSetting));
        
        getWebTarget()
            .path("config/rest/settings")
            .path(portSetting.getId().toString())
            .request(MediaType.APPLICATION_JSON)
            .header(HttpHeaders.AUTHORIZATION, getValidJwtToken())
            .put(Entity.json(portSetting));
        
        TimeUnit.SECONDS.sleep(1);
    }
    
    @Test
    @Ignore("Will run this test after we have released a new version of inmarsat")
    public void createManualPollTest() throws Exception {
        try (LESMock les = new LESMock(PORT)) {
            AssetDTO dto = AssetTestHelper.createBasicAsset();
            AssetDTO asset = AssetTestHelper.createAsset(dto);
            MobileTerminalDto mt = MobileTerminalTestHelper.createMobileTerminal();
            MobileTerminalTestHelper.createPollWithMT_Helper(asset, PollType.MANUAL_POLL, mt);
            
            String message = les.getMessage(10);
            String satelliteNumber = mt.getSatelliteNumber();

            Set<ChannelDto> channels = mt.getChannels();
            ChannelDto[] arr = channels.toArray(new ChannelDto[0]);
            String memberNumber = arr[0].getMemberNumber();
            String DNID = arr[0].getDNID();

            assertTrue(message.startsWith("POLL "));

            message = message.substring(5);
            message = message.replace(" ", "");

            String[] split = message.split(",");

            assertEquals("0", split[0].trim());     // Ocean Region 0 = West Atlantic Ocean Region
            assertEquals("I", split[1].trim());     // Poll Type I = Individual poll
            assertEquals(DNID, split[2].trim());             // DNID
            assertEquals("D", split[3].trim());     // Response type
            assertEquals("1", split[4].trim());     // Sub-address
            assertEquals(satelliteNumber, split[5].trim());  // satellite number
            assertEquals("0", split[6].trim());     // command type  0 = unreserved as required in response
            assertEquals(memberNumber, split[7].trim());     // member number
            assertEquals("0", split[8].trim());     // start frame   N/A here
            assertEquals("", split[9].trim());      // reports per 24 hours A/A here
            assertEquals("0", split[10].trim());    // ack  0
        }
    }
    
    // Find docker host machine ip. Replace this with 'host.docker.internal' when supported on Linux.
    private static String getDockerHostIp() throws SocketException {
        Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
        while (interfaces.hasMoreElements()) {
            NetworkInterface e = interfaces.nextElement();
            Enumeration<InetAddress> inetAddresses = e.getInetAddresses();
            while (inetAddresses.hasMoreElements()) {
                InetAddress inetAddress = inetAddresses.nextElement();
                if (inetAddress.getHostAddress().startsWith("172")) {
                    return inetAddress.getHostAddress();
                }
            }
        }
        return "host.docker.internal";
    }
}
