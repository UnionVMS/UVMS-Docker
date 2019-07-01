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
package eu.europa.ec.fisheries.uvms.docker.validation.movement;

import eu.europa.ec.fisheries.schema.movement.v1.TempMovementType;
import eu.europa.ec.fisheries.uvms.docker.validation.common.AbstractHelper;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public class TempMovementRestHelper extends AbstractHelper {

    static TempMovementType createTempMovement(TempMovementType tempMovement) {
        return getWebTarget()
                .path("movement/rest/tempmovement")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getValidJwtToken())
                .post(Entity.json(tempMovement), TempMovementType.class);
    }

    static Response createTempMovementResponse(TempMovementType tempMovement) {
        return getWebTarget()
                .path("movement/rest/tempmovement")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getValidJwtToken())
                .post(Entity.json(tempMovement));
    }

    static TempMovementType getTempMovement(String guid) {
        return getWebTarget()
                .path("movement/rest/tempmovement/")
                .path(guid)
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getValidJwtToken())
                .get(TempMovementType.class);
    }

    static Response getTempMovementResponse(String guid) {
        return getWebTarget()
                .path("movement/rest/tempmovement/" + guid)
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getValidJwtToken())
                .get();
    }

    static TempMovementType removeTempMovement(String guid) {
        return getWebTarget()
                .path("movement/rest/tempmovement/remove")
                .path(guid)
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getValidJwtToken())
                .put(null, TempMovementType.class);
    }

    static Response removeTempMovementResponse(String guid) {
        return getWebTarget()
                .path("movement/rest/tempmovement/remove/" + guid)
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getValidJwtToken())
                .put(null);
    }

    static TempMovementType updateTempMovement(TempMovementType tempMovement) {
        return getWebTarget()
                .path("movement/rest/tempmovement")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getValidJwtToken())
                .put(Entity.json(tempMovement), TempMovementType.class);
    }
    
    static TempMovementType sendTempMovement(String guid) {
        return getWebTarget()
                .path("movement/rest/tempmovement/send")
                .path(guid)
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getValidJwtToken())
                .put(Entity.json(guid), TempMovementType.class);
    }
}
