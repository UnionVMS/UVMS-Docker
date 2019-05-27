/*

Developed with the contribution of the European Commission - Directorate General for Maritime Affairs and Fisheries
� European Union, 2017.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can
redistribute it and/or modify it under the terms of the GNU General Public License as published by the
Free Software Foundation, either version 3 of the License, or any later version. The IFDM Suite is distributed in
the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. You should have received a
copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

*/
package eu.europa.ec.fisheries.uvms.docker.validation.config;

import eu.europa.ec.fisheries.schema.config.types.v1.SettingType;
import eu.europa.ec.fisheries.schema.config.types.v1.SettingsCreateQuery;
import eu.europa.ec.fisheries.uvms.commons.rest.dto.ResponseDto;
import eu.europa.ec.fisheries.uvms.docker.validation.common.AbstractRest;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class SettingsRestIT extends AbstractRest {

    private static final Logger log = LoggerFactory.getLogger(SettingsRestIT.class.getSimpleName());

    @Test
    public void getByModuleNameTest() {
        Response response = getWebTarget()
                .path("config/rest/settings")
                .queryParam("moduleName","audit")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getValidJwtToken())
                .get();

        ResponseDto responseDto = response.readEntity(ResponseDto.class);
        assertNotNull(responseDto.getData());
    }

    @Test
    public void getByModuleNameAllModulesTest() {
        ResponseDto responseDto = getWebTarget()
                .path("config/rest/catalog")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getValidJwtToken())
                .get(ResponseDto.class);

        Map<String, List<SettingType>> dataMap = (Map) responseDto.getData();

        for (String module : dataMap.keySet()) {
            ResponseDto dto = getWebTarget()
                    .path("config/rest/settings")
                    .queryParam("moduleName", module)
                    .request(MediaType.APPLICATION_JSON)
                    .header(HttpHeaders.AUTHORIZATION, getValidJwtToken())
                    .get(ResponseDto.class);

            assertNotNull(dto.getData());
        }
    }

    @Test
    public void createTest() {
        SettingType settingType = createTestSettingType();
        assertNotNull(settingType.getId());
    }

    @Test
    public void getByIdTest() {
        SettingType settingType = createTestSettingType();
        assertNotNull(settingType);

        ResponseDto dto = getWebTarget()
                .path("config/rest/settings")
                .path(String.valueOf(settingType.getId()))
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getValidJwtToken())
                .get(ResponseDto.class);

        assertNotNull(dto.getData());
    }

    @Test
    public void deleteTest() {
        SettingType settingType = createTestSettingType();
        assertNotNull(settingType);

        ResponseDto dto = getWebTarget()
                .path("config/rest/settings")
                .path(String.valueOf(settingType.getId()))
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getValidJwtToken())
                .delete(ResponseDto.class);

        assertNotNull(dto.getData());
    }

    @Test
    public void updateTest() {
        SettingType settingType = createTestSettingType();
        assertNotNull(settingType);

        settingType.setDescription("Updated Desc" + UUID.randomUUID().toString());

        ResponseDto dto = getWebTarget()
                .path("config/rest/settings")
                .path(String.valueOf(settingType.getId()))
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getValidJwtToken())
                .put(Entity.json(settingType), ResponseDto.class);

        assertNotNull(dto.getData());
    }

    private SettingType createTestSettingType() {
        SettingType settingType = new SettingType();
        settingType.setDescription("SettingsRestIt" + UUID.randomUUID().toString());
        settingType.setGlobal(false);
        settingType.setKey("audit.key.SettingsRestIt." + UUID.randomUUID().toString());
        settingType.setValue(UUID.randomUUID().toString());

        SettingsCreateQuery settingsCreateQuery = new SettingsCreateQuery();
        settingsCreateQuery.setModuleName("audit");
        settingsCreateQuery.setSetting(settingType);

        ResponseDto dto = getWebTarget()
                .path("config/rest/settings")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getValidJwtToken())
                .post(Entity.json(settingsCreateQuery), ResponseDto.class);

        try {
            String valueAsString = writeValueAsString(dto.getData());
            return OBJECT_MAPPER.readValue(valueAsString, SettingType.class);
        } catch (IOException e) {
            log.error("Error occurred while retrieving Audit List", e);
            return null;
        }
    }

    @Test
    public void catalogTest() {

        ResponseDto dto = getWebTarget()
                .path("config/rest/catalog")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getValidJwtToken())
                .get(ResponseDto.class);

        assertNotNull(dto.getData());
    }

    @Test
    public void getPingsTest() {
        ResponseDto dto = getWebTarget()
                .path("config/rest/pings")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getValidJwtToken())
                .get(ResponseDto.class);

        assertNotNull(dto.getData());
    }

    @Test
    public void getGlobalSettingsTest() {
        ResponseDto dto = getWebTarget()
                .path("config/rest/globals")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getValidJwtToken())
                .get(ResponseDto.class);

        assertNotNull(dto.getData());
    }
}
