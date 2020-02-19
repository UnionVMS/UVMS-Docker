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
import eu.europa.ec.fisheries.schema.exchange.plugin.v1.SetConfigRequest;
import eu.europa.ec.fisheries.schema.mobileterminal.polltypes.v1.PollType;
import eu.europa.ec.fisheries.uvms.asset.client.model.AssetDTO;
import eu.europa.ec.fisheries.uvms.docker.validation.asset.AssetTestHelper;
import eu.europa.ec.fisheries.uvms.docker.validation.common.AbstractRest;
import eu.europa.ec.fisheries.uvms.docker.validation.common.TopicListener;
import eu.europa.ec.fisheries.uvms.docker.validation.mobileterminal.MobileTerminalTestHelper;
import eu.europa.ec.fisheries.uvms.docker.validation.mobileterminal.dto.ChannelDto;
import eu.europa.ec.fisheries.uvms.docker.validation.mobileterminal.dto.MobileTerminalDto;
import eu.europa.ec.fisheries.uvms.docker.validation.system.helper.LESMock;
import eu.europa.ec.fisheries.uvms.docker.validation.system.helper.VMSSystemHelper;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
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
    public static void initSettings() throws IOException, Exception {
        String urlKey = "eu.europa.ec.fisheries.uvms.plugins.inmarsat.URL";
        String portKey = "eu.europa.ec.fisheries.uvms.plugins.inmarsat.PORT";

        List<SettingType> response = getWebTarget()
                .path("config/rest/settings")
                .queryParam("moduleName", "exchange")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getValidJwtToken())
                .get(new GenericType<List<SettingType>>() {
                });

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

        String ip = getDockerHostIp();
        urlSetting.setValue(ip);
        portSetting.setValue(String.valueOf(PORT));

        try (TopicListener listener = new TopicListener(VMSSystemHelper.INMARSAT_SELECTOR)) {
            getWebTarget()
                    .path("config/rest/settings")
                    .path(urlSetting.getId().toString())
                    .request(MediaType.APPLICATION_JSON)
                    .header(HttpHeaders.AUTHORIZATION, getValidJwtToken())
                    .put(Entity.json(urlSetting));
    
            SetConfigRequest configMessage1 = listener.listenOnEventBusForSpecificMessage(SetConfigRequest.class);
            assertTrue(configMessage1.getConfigurations().getSetting().stream().anyMatch(c -> c.getValue().equals(ip)));

            getWebTarget()
                    .path("config/rest/settings")
                    .path(portSetting.getId().toString())
                    .request(MediaType.APPLICATION_JSON)
                    .header(HttpHeaders.AUTHORIZATION, getValidJwtToken())
                    .put(Entity.json(portSetting));

            SetConfigRequest configMessage2 = listener.listenOnEventBusForSpecificMessage(SetConfigRequest.class);
            assertTrue(configMessage2.getConfigurations().getSetting().stream().anyMatch(c -> c.getValue().equals(String.valueOf(PORT))));
            assertTrue(configMessage2.getConfigurations().getSetting().stream().anyMatch(c -> c.getValue().equals(ip)));
        }
        TimeUnit.SECONDS.sleep(2);
    }

    @Test
    public void createManualPollTest() throws Exception {
        try (LESMock les = new LESMock(PORT)) {
            AssetDTO dto = AssetTestHelper.createBasicAsset();
            AssetDTO asset = AssetTestHelper.createAsset(dto);
            MobileTerminalDto mt = MobileTerminalTestHelper.createMobileTerminal();
            MobileTerminalTestHelper.createPollWithMT_Helper(asset, PollType.MANUAL_POLL, mt);

            List<String> message = les.getMessage(10);
            String satelliteNumber = mt.getSatelliteNumber();

            Set<ChannelDto> channels = mt.getChannels();
            ChannelDto[] arr = channels.toArray(new ChannelDto[0]);
            String memberNumber = arr[0].getMemberNumber();
            String DNID = arr[0].getDNID();

            assertEquals(1, message.size());
            String command = message.get(0);
            assertTrue(command.startsWith("poll "));

            String[] split = splitCommand(command);

            assertEquals("3", split[0].trim());
            assertEquals("I", split[1].trim());
            assertEquals(DNID, split[2].trim());
            assertEquals("D", split[3].trim());
            assertEquals("1", split[4].trim());
            assertEquals(satelliteNumber, split[5].trim());
            assertEquals("0", split[6].trim());
            assertEquals(memberNumber, split[7].trim());
        }
    }

    @Test
    public void createConfigPollTest() throws Exception {
        try (LESMock les = new LESMock(PORT)) {
            AssetDTO dto = AssetTestHelper.createBasicAsset();
            AssetDTO asset = AssetTestHelper.createAsset(dto);
            MobileTerminalDto mt = MobileTerminalTestHelper.createMobileTerminal();
            MobileTerminalTestHelper.createConfigPollWithMT_Helper(asset, mt);

            List<String> messageList = les.getMessage(30);

            Set<ChannelDto> channels = mt.getChannels();
            ChannelDto[] arr = channels.toArray(new ChannelDto[0]);
            String DNID = arr[0].getDNID();
            String satelliteNumber = mt.getSatelliteNumber();
            String memberNumber = arr[0].getMemberNumber();

            assertEquals(3, messageList.size());

            // validation for common fields.
            for (String command : messageList) {
                assertTrue(command.startsWith("poll "));
                String[] split = splitCommand(command);

                assertEquals("3", split[0].trim());
                assertEquals("I", split[1].trim());
                assertEquals(DNID, split[2].trim());
                assertEquals("1", split[4].trim());
                assertEquals(satelliteNumber, split[5].trim());
                assertEquals(memberNumber, split[7].trim());
            }

            String stop = messageList.get(0);
            validateCommand(stop, "stop");

            String config = messageList.get(1);
            validateCommand(config, "config");

            String start = messageList.get(2);
            validateCommand(start, "start");
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

    private void validateCommand(String command, String type) {
        String[] split = splitCommand(command);

        // validate different parts of different polls
        if (type.equalsIgnoreCase("stop")) {
            assertEquals("N", split[3].trim());
            assertEquals("6", split[6].trim());
        } else if (type.equalsIgnoreCase("config")) {
            assertEquals("N", split[3].trim());
            assertEquals("4", split[6].trim());
            assertEquals("5625", split[8].trim());
            assertEquals("12", split[9].trim());
        } else if (type.equalsIgnoreCase("start")) {
            assertEquals("N", split[3].trim());
            assertEquals("5", split[6].trim());
        }
    }

    private String[] splitCommand(String command) {
        command = command.substring(5);
        command = command.replace(" ", "");
        return command.split(",");
    }
}
