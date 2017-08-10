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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.TimeZone;

import org.apache.http.HttpResponse;
import org.apache.http.client.fluent.Request;
import org.junit.Test;

import eu.europa.ec.fisheries.schema.movement.asset.v1.VesselType;
import eu.europa.ec.fisheries.schema.movement.v1.MovementPoint;
import eu.europa.ec.fisheries.schema.movement.v1.TempMovementStateEnum;
import eu.europa.ec.fisheries.schema.movement.v1.TempMovementType;
import eu.europa.ec.fisheries.uvms.docker.validation.common.AbstractRestServiceTest;

/**
 * The Class MovementTempMovementRestIT.
 */
public class MovementTempMovementRestIT extends AbstractRestServiceTest {

	/**
	 * Creates the temp test.
	 *
	 * @throws Exception
	 *             the exception
	 */
	@Test
	public void createTempTest() throws Exception {
		final HttpResponse response = Request.Post(getBaseUrl() + "movement/rest/tempmovement")
				.setHeader("Content-Type", "application/json").setHeader("Authorization", getValidJwtToken())
				.bodyByteArray(writeValueAsString(createTempMovement()).getBytes()).execute().returnResponse();

		Map<String, Object> dataMap = checkSuccessResponseReturnMap(response);
	}

	/**
	 * Creates the temp movement.
	 *
	 * @return the temp movement type
	 */
	private static TempMovementType createTempMovement() {
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
