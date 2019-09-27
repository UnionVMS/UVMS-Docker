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
import static org.hamcrest.CoreMatchers.notNullValue;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.TimeUnit;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import eu.europa.ec.fisheries.schema.config.types.v1.SettingType;
import eu.europa.ec.fisheries.schema.mobileterminal.polltypes.v1.PollType;
import eu.europa.ec.fisheries.uvms.asset.client.model.AssetDTO;
import eu.europa.ec.fisheries.uvms.docker.validation.asset.AssetTestHelper;
import eu.europa.ec.fisheries.uvms.docker.validation.common.AbstractRest;
import eu.europa.ec.fisheries.uvms.docker.validation.mobileterminal.MobileTerminalTestHelper;
import eu.europa.ec.fisheries.uvms.docker.validation.mobileterminal.dto.MobileTerminalDto;
import eu.europa.ec.fisheries.uvms.docker.validation.system.helper.LESMock;

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
    @Ignore("Ignoring until inmarsatPollHandler can update settings")
    public void createManualPollTest() throws IOException, Exception {
        try (LESMock les = new LESMock(PORT)) {
            AssetDTO dto = AssetTestHelper.createBasicAsset();
            AssetDTO asset = AssetTestHelper.createAsset(dto);
            MobileTerminalDto mt = MobileTerminalTestHelper.createMobileTerminal();
            MobileTerminalTestHelper.createPollWithMT_Helper(asset, PollType.MANUAL_POLL, mt);
            
            String message = les.getMessage(10);
            assertTrue(message.startsWith("POLL"));
//            System.out.println(message);
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
