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
import eu.europa.ec.fisheries.uvms.docker.validation.common.AbstractRest;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
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

        assertEquals(200, response.getStatus());
        assertNotNull(response.getEntity());
    }

    @Test
    public void getByModuleNameAllModulesTest() {
        Map<String, List<SettingType>> dataMap = getWebTarget()
                .path("config/rest/catalog")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getValidJwtToken())
                .get(new GenericType<Map<String, List<SettingType>>>() {});


        for (String module : dataMap.keySet()) {
            Response response = getWebTarget()
                    .path("config/rest/settings")
                    .queryParam("moduleName", module)
                    .request(MediaType.APPLICATION_JSON)
                    .header(HttpHeaders.AUTHORIZATION, getValidJwtToken())
                    .get(Response.class);

            assertEquals(200, response.getStatus());
            assertNotNull(response.getEntity());
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

        SettingType setting = getWebTarget()
                .path("config/rest/settings")
                .path(String.valueOf(settingType.getId()))
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getValidJwtToken())
                .get(SettingType.class);

        assertNotNull(setting);
    }

    @Test
    public void deleteTest() {
        SettingType settingType = createTestSettingType();
        assertNotNull(settingType);

        Response response = getWebTarget()
                .path("config/rest/settings")
                .path(String.valueOf(settingType.getId()))
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getValidJwtToken())
                .delete(Response.class);

        assertEquals(200, response.getStatus());
        assertNotNull(response.getEntity());
    }

    @Test
    public void updateTest() {
        SettingType settingType = createTestSettingType();
        assertNotNull(settingType);

        settingType.setDescription("Updated Desc" + UUID.randomUUID().toString());

        Response response = getWebTarget()
                .path("config/rest/settings")
                .path(String.valueOf(settingType.getId()))
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getValidJwtToken())
                .put(Entity.json(settingType), Response.class);

        assertEquals(200, response.getStatus());
        assertNotNull(response.getEntity());
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

        SettingType setting = getWebTarget()
                .path("config/rest/settings")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getValidJwtToken())
                .post(Entity.json(settingsCreateQuery), SettingType.class);

            return setting;
    }

    @Test
    public void catalogTest() {

        Map<String, List<SettingType>> catalog = getWebTarget()
                .path("config/rest/catalog")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getValidJwtToken())
                .get(new GenericType<Map<String, List<SettingType>>>() {});

        assertNotNull(catalog);
    }

    @Test
    public void getPingsTest() {
        Response response = getWebTarget()
                .path("config/rest/pings")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getValidJwtToken())
                .get(Response.class);

        assertEquals(200, response.getStatus());
        assertNotNull(response.getEntity());
    }

    @Test
    public void getGlobalSettingsTest() {
        Response response = getWebTarget()
                .path("config/rest/globals")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getValidJwtToken())
                .get(Response.class);

        assertEquals(200, response.getStatus());
        assertNotNull(response.getEntity());
    }
}
