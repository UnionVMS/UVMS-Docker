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
package eu.europa.ec.fisheries.uvms.docker.validation.spatial;

import eu.europa.ec.fisheries.uvms.docker.validation.common.AbstractRest;
import org.hamcrest.CoreMatchers;
import org.junit.Test;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

public class SpatialImageRestIT extends AbstractRest {
    
    @Test
    public void renderImagesTest() {
        String response = getWebTarget()
                .path("spatial/rest/image")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getValidJwtToken())
                .header("roleName", "AdminAllUVMS")
                .post(Entity.json(getPayload()), String.class);
        
        assertThat(response, CoreMatchers.is(CoreMatchers.notNullValue()));
    }

    private String getPayload() {
        return "{\"positions\":{\"title\":\"F.S\",\"cluster\":{\"text\":\"Clustered positions\",\"bgcolor\":\"#FFFFFF\",\"bordercolor\":\"#F7580D\"},\"classes\":[{\"text\":\"SWE\",\"color\":\"#815FB3\"}]}}";
    }
}
