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
package eu.europa.ec.fisheries.uvms.docker.validation.movement;

import eu.europa.ec.fisheries.uvms.asset.client.model.AssetDTO;
import eu.europa.ec.fisheries.uvms.docker.validation.asset.AssetTestHelper;
import eu.europa.ec.fisheries.uvms.docker.validation.common.AbstractRest;
import eu.europa.ec.fisheries.uvms.docker.validation.movement.model.ManualMovementDto;
import eu.europa.ec.fisheries.uvms.movement.model.dto.MovementDto;
import org.junit.Test;

import javax.ws.rs.core.Response;
import java.time.Instant;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;

public class ManualMovementRestIT extends AbstractRest {

    @Test
    public void sendManualMovementTest() {
        Double latitude = 10d;
        Double longitude = 11d;
        LatLong position = new LatLong(latitude, longitude, new Date());
        AssetDTO asset = AssetTestHelper.createTestAsset();
        ManualMovementDto manualMovement = ManualMovementRestHelper.mapToManualMovement(position, asset);

        Response response = ManualMovementRestHelper.sendManualMovement(manualMovement);
        assertEquals(200, response.getStatus());
        
        List<MovementDto> latestMovements = MovementHelper.getLatestMovements(Collections.singletonList(asset.getId().toString()));
        assertThat(latestMovements.size(), is(1));
        
        MovementDto createdMovement = latestMovements.get(0);
        assertThat(createdMovement.getLocation().getLatitude(), is(latitude));
        assertThat(createdMovement.getLocation().getLongitude(), is(longitude));
    }

    @Test
    public void sendManualMovementWithLessDataTest() {
        String input = "{\"movement\":{\"location\":{\"longitude\":0.5,\"latitude\":0.8},\"heading\":0.0,\"timestamp\":1575545948,\"speed\":0.0},\"asset\":{\"ircs\":\"OWIF\"}}";
        String epochMillis = "" + Instant.now().toEpochMilli();
        input = input.replace("1575545948", epochMillis);
        Response response = ManualMovementRestHelper.sendManualMovement(input);
        assertEquals(200, response.getStatus());

        List<MovementDto> latestMovements = MovementHelper.getLatestMovements(1);
        assertThat(latestMovements.size(), is(1));

        MovementDto createdMovement = latestMovements.get(0);
        assertEquals(Long.parseLong(epochMillis), createdMovement.getTimestamp().toEpochMilli());
        assertEquals(createdMovement.getLocation().getLatitude(), 0.8d, 0);
        assertEquals(createdMovement.getLocation().getLongitude(), 0.5d, 0);
    }



}
