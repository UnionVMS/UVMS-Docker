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

import eu.europa.ec.fisheries.schema.movement.asset.v1.VesselType;
import eu.europa.ec.fisheries.schema.movement.v1.MovementPoint;
import eu.europa.ec.fisheries.schema.movement.v1.MovementSourceType;
import eu.europa.ec.fisheries.uvms.asset.client.model.AssetDTO;
import eu.europa.ec.fisheries.uvms.docker.validation.common.AbstractHelper;
import eu.europa.ec.fisheries.uvms.docker.validation.movement.model.ManualMovementDto;
import eu.europa.ec.fisheries.uvms.docker.validation.movement.model.MicroMovement;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public class ManualMovementRestHelper extends AbstractHelper {

    public static Response sendManualMovement(ManualMovementDto movementDto) {
        return getWebTarget()
                .path("movement/rest/manualMovement")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getValidJwtToken())
                .post(Entity.json(movementDto), Response.class);
    }

    public static Response sendManualMovement(String movementDto) {
        return getWebTarget()
                .path("movement/rest/manualMovement")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getValidJwtToken())
                .post(Entity.json(movementDto), Response.class);
    }

    public static ManualMovementDto mapToManualMovement(LatLong position, AssetDTO asset) {
        ManualMovementDto movement = new ManualMovementDto();
        VesselType vessel = new VesselType();
        vessel.setCfr(asset.getCfr());
        vessel.setExtMarking(asset.getExternalMarking());
        vessel.setFlagState(asset.getFlagStateCode());
        vessel.setIrcs(asset.getIrcs());
        vessel.setName(asset.getName());
        movement.setAsset(vessel);

        MicroMovement micro = new MicroMovement();
        MovementPoint location = new MovementPoint();
        location.setLatitude(position.latitude);
        location.setLongitude(position.longitude);
        micro.setLocation(location);
        micro.setTimestamp(position.positionTime.toInstant());
        micro.setHeading(position.bearing);
        micro.setSpeed(position.speed);
        micro.setSource(MovementSourceType.MANUAL);
        movement.setMovement(micro);

        return movement;
    }
}
