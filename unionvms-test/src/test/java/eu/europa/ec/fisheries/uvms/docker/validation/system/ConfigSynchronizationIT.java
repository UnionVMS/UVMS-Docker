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
package eu.europa.ec.fisheries.uvms.docker.validation.system;

import eu.europa.ec.fisheries.schema.config.types.v1.SettingType;
import eu.europa.ec.fisheries.schema.exchange.plugin.v1.SetConfigRequest;
import eu.europa.ec.fisheries.uvms.docker.validation.common.AbstractRest;
import eu.europa.ec.fisheries.uvms.docker.validation.common.TopicListener;
import eu.europa.ec.fisheries.uvms.docker.validation.system.helper.VMSSystemHelper;
import org.junit.Test;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.Map;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertTrue;

public class ConfigSynchronizationIT extends AbstractRest {

    private static final String EXPECTED_GLOBAL_CONFIG = "flux_local_nation_code";
    
    @Test
    public void checkAssetConfigTest() {
        Map<String, String> response = getWebTarget()
            .path("asset/rest/config/parameters")
            .request(MediaType.APPLICATION_JSON)
            .header(HttpHeaders.AUTHORIZATION, getValidJwtToken())
            .get(new GenericType<Map<String, String>>() {});
        
        assertTrue(response.keySet().contains(EXPECTED_GLOBAL_CONFIG));
    }
    
    @Test
    public void nafPluginConfigUpdateTest() throws Exception {
        String expectedKey = "eu.europa.ec.fisheries.uvms.plugins.naf.connectTimeout";
        
        List<SettingType> response = getWebTarget()
                .path("config/rest/settings")
                .queryParam("moduleName", "exchange")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getValidJwtToken())
                .get(new GenericType<List<SettingType>>() {});

        SettingType expectedSetting = null;
        for (SettingType settingType : response) {
            if (settingType.getKey().equals(expectedKey)) {
                expectedSetting = settingType;
            }
        }
        assertThat(expectedSetting, is(notNullValue()));

        String newValue = generateARandomStringWithMaxLength(6);
        expectedSetting.setValue(newValue);
        
        try (TopicListener listener = new TopicListener(VMSSystemHelper.NAF_SELECTOR)) {
            getWebTarget()
                    .path("config/rest/settings")
                    .path(expectedSetting.getId().toString())
                    .request(MediaType.APPLICATION_JSON)
                    .header(HttpHeaders.AUTHORIZATION, getValidJwtToken())
                    .put(Entity.json(expectedSetting));

            SetConfigRequest configRequest = listener.listenOnEventBusForSpecificMessage(SetConfigRequest.class);
            assertThat(configRequest, is(notNullValue()));
            assertThat(configRequest.getConfigurations().getSetting().size(), is(6));
            configRequest.getConfigurations().getSetting().removeIf(v -> !v.getKey().equals(expectedKey));
            assertThat(configRequest.getConfigurations().getSetting().get(0).getKey(), is(expectedKey));
            assertThat(configRequest.getConfigurations().getSetting().get(0).getValue(), is(newValue));
        }
    }
}
