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

import static org.hamcrest.CoreMatchers.is;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.UUID;
import org.junit.Test;
import eu.europa.ec.fisheries.schema.movement.asset.v1.VesselType;
import eu.europa.ec.fisheries.schema.movement.v1.MovementPoint;
import eu.europa.ec.fisheries.schema.movement.v1.TempMovementStateEnum;
import eu.europa.ec.fisheries.schema.movement.v1.TempMovementType;
import eu.europa.ec.fisheries.uvms.asset.client.model.AssetDTO;
import eu.europa.ec.fisheries.uvms.commons.rest.dto.ResponseDto;
import eu.europa.ec.fisheries.uvms.docker.validation.asset.AssetTestHelper;
import eu.europa.ec.fisheries.uvms.docker.validation.common.AbstractRest;

/**
 * The Class TempMovementRestIT.
 */
public class TempMovementRestIT extends AbstractRest {

    /**
     * Creates the temp test.
     *
     * @throws Exception the exception
     */
    @Test
    public void createTempMovementTest() throws Exception {
        TempMovementType tempMovement = getTempMovement();
        TempMovementType createdTempMovement = TempMovementRestHelper.createTempMovement(tempMovement);

        assertTrue(createdTempMovement.getGuid() != null);
        assertEquals(tempMovement.getAsset(), createdTempMovement.getAsset());
        assertEquals(tempMovement.getCourse(), createdTempMovement.getCourse());
        assertEquals(tempMovement.getPosition().getLatitude(), createdTempMovement.getPosition().getLatitude());
        assertEquals(tempMovement.getPosition().getLongitude(), createdTempMovement.getPosition().getLongitude());
        assertEquals(tempMovement.getSpeed(), createdTempMovement.getSpeed());
        assertEquals(tempMovement.getState(), createdTempMovement.getState());
        assertEquals(tempMovement.getTime(), createdTempMovement.getTime());
    }

    @Test
    public void createTempMovementNullInputShouldFail() throws Exception {
        ResponseDto<?> response = TempMovementRestHelper.createTempMovementResponse(new TempMovementType());
        checkErrorResponse(response);
    }

    @Test
    public void createTempMovementNoPositionShouldFail() throws Exception {
        TempMovementType tempMovement = getTempMovement();
        tempMovement.getPosition().setLatitude(null);
        tempMovement.getPosition().setLongitude(null);
        ResponseDto<?> response = TempMovementRestHelper.createTempMovementResponse(tempMovement);
        checkErrorResponse(response);
    }

    @Test
    public void createTempMovementNoValidLatitudeShouldFail() throws Exception {
        TempMovementType tempMovement = getTempMovement();
        tempMovement.getPosition().setLatitude(100d);
        ResponseDto<?> response = TempMovementRestHelper.createTempMovementResponse(tempMovement);
        checkErrorResponse(response);
    }

    @Test
    public void getTempMovementTest() throws Exception {
        TempMovementType tempMovement = getTempMovement();
        TempMovementType createdTempMovement = TempMovementRestHelper.createTempMovement(tempMovement);

        TempMovementType fetchedTempMovement = TempMovementRestHelper.getTempMovement(createdTempMovement.getGuid());
        assertEquals(createdTempMovement, fetchedTempMovement);
    }

    @Test
    public void getTempMovementNullGuidShouldFail() throws Exception {
        ResponseDto<?> response = TempMovementRestHelper.getTempMovementResponse(null);
        checkErrorResponse(response);
    }

    @Test
    public void getTempMovementNonExistingGuidShouldFail() throws Exception {
        ResponseDto<?> response = TempMovementRestHelper.getTempMovementResponse(UUID.randomUUID().toString());
        checkErrorResponse(response);
    }

    @Test
    public void removeTempMovementTest() throws Exception {
        TempMovementType tempMovement = getTempMovement();
        TempMovementType createdTempMovement = TempMovementRestHelper.createTempMovement(tempMovement);
        assertEquals(TempMovementStateEnum.SENT, createdTempMovement.getState());

        TempMovementType removedTempMovement = TempMovementRestHelper.removeTempMovement(createdTempMovement.getGuid());
        assertEquals(TempMovementStateEnum.DELETED, removedTempMovement.getState());
    }

    @Test
    public void removeTempMovementNullGuidShouldFail() throws Exception {
        ResponseDto<?> response = TempMovementRestHelper.removeTempMovementResponse(null);
        checkErrorResponse(response);
    }

    @Test
    public void updateTempMovementTest() throws Exception {
        TempMovementType tempMovement = getTempMovement();
        TempMovementType createdTempMovement = TempMovementRestHelper.createTempMovement(tempMovement);

        Double newCourse = 123d;
        createdTempMovement.setCourse(newCourse);

        TempMovementType updatedTempMovement = TempMovementRestHelper.updateTempMovement(createdTempMovement);

        assertEquals(createdTempMovement.getAsset(), updatedTempMovement.getAsset());
        assertEquals(newCourse, updatedTempMovement.getCourse());
        assertEquals(createdTempMovement.getPosition().getLatitude(), updatedTempMovement.getPosition().getLatitude());
        assertEquals(createdTempMovement.getPosition().getLongitude(), updatedTempMovement.getPosition()
                .getLongitude());
        assertEquals(createdTempMovement.getSpeed(), updatedTempMovement.getSpeed());
        assertEquals(createdTempMovement.getState(), updatedTempMovement.getState());
        assertEquals(createdTempMovement.getTime(), updatedTempMovement.getTime());
    }
    
    @Test
    public void sendTempMovementTest() throws Exception {
        Double latitude = 10d;
        Double longitude = 11d;
        AssetDTO asset = AssetTestHelper.createTestAsset();
        TempMovementType tempMovement = getTempMovement();
        tempMovement.getAsset().setCfr(asset.getCfr());
        tempMovement.getAsset().setIrcs(asset.getIrcs());
        tempMovement.getAsset().setExtMarking(asset.getExternalMarking());
        tempMovement.getAsset().setFlagState(asset.getFlagStateCode());
        tempMovement.getAsset().setName(asset.getName());
        tempMovement.getPosition().setLatitude(latitude);
        tempMovement.getPosition().setLongitude(longitude);
        TempMovementType createdTempMovement = TempMovementRestHelper.createTempMovement(tempMovement);
        
        TempMovementRestHelper.sendTempMovement(createdTempMovement.getGuid());
        
        MovementHelper.pollMovementCreated();
        
        List<MovementDto> latestMovements = MovementHelper.getLatestMovements(Arrays.asList(asset.getId().toString()));
        assertThat(latestMovements.size(), is(1));
        
        MovementDto createdMovement = latestMovements.get(0);
        assertThat(createdMovement.getLatitude(), is(latitude));
        assertThat(createdMovement.getLongitude(), is(longitude));
    }

    /**
     * Creates the temp movement.
     *
     * @return the temp movement type
     */
    private static TempMovementType getTempMovement() {
        final VesselType vesselType = new VesselType();
        vesselType.setCfr("T");
        vesselType.setExtMarking("T");
        vesselType.setFlagState("T");
        vesselType.setIrcs("T");
        vesselType.setName("T");

        final MovementPoint movementPoint = new MovementPoint();
        movementPoint.setAltitude(0.0);
        movementPoint.setLatitude(0.0);
        movementPoint.setLongitude(0.0);

        final Date d = Calendar.getInstance().getTime();
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));

        final TempMovementType tempMovementType = new TempMovementType();
        tempMovementType.setAsset(vesselType);
        tempMovementType.setCourse(0.0);
        tempMovementType.setPosition(movementPoint);
        tempMovementType.setSpeed(0.0);
        tempMovementType.setState(TempMovementStateEnum.SENT);
        tempMovementType.setTime(sdf.format(d));
        return tempMovementType;
    }
}
