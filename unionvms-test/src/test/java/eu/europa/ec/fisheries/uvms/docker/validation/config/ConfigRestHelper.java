package eu.europa.ec.fisheries.uvms.docker.validation.config;

import eu.europa.ec.fisheries.schema.config.types.v1.SettingType;
import eu.europa.ec.fisheries.uvms.docker.validation.common.AbstractHelper;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

public class ConfigRestHelper extends AbstractHelper {

    public static void setLocalFlagStateToSwe(){
        List<SettingType> dtoList = getWebTarget()
                .path("config/rest/globals")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getValidJwtToken())
                .get(new GenericType<List<SettingType>>() {});

        SettingType flux_local_nation_code = dtoList.stream().filter(setting -> setting.getKey().equals("flux_local_nation_code")).findAny().get();
        flux_local_nation_code.setValue("SWE");

        Response response = getWebTarget()
                .path("config/rest/settings")
                .path(flux_local_nation_code.getId().toString())
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getValidJwtToken())
                .put(Entity.json(flux_local_nation_code), Response.class);
        assertEquals(200, response.getStatus());

    }
}
