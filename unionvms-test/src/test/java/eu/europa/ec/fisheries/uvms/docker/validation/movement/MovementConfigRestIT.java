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

import eu.europa.ec.fisheries.schema.exchange.movement.v1.MovementActivityTypeType;
import eu.europa.ec.fisheries.schema.movement.search.v1.SearchKeyType;
import eu.europa.ec.fisheries.schema.movement.v1.MovementSourceType;
import eu.europa.ec.fisheries.schema.movement.v1.MovementTypeType;
import eu.europa.ec.fisheries.schema.movement.v1.SegmentCategoryType;
import eu.europa.ec.fisheries.uvms.docker.validation.common.AbstractRest;
import org.junit.Test;

import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import java.util.List;

public class MovementConfigRestIT extends AbstractRest {

	@Test
	public void getMovementTypesTest() {
		List<MovementTypeType> response = getWebTarget()
		        .path("movement/rest/config/movementTypes")
		        .request(MediaType.APPLICATION_JSON)
		        .header(HttpHeaders.AUTHORIZATION, getValidJwtToken())
		        .get(new GenericType<List<MovementTypeType>>() {});
		assertNotNull(response);
	}

	@Test
	public void getSegmentTypesTest() {
		List<SegmentCategoryType> response = getWebTarget()
                .path("movement/rest/config/segmentCategoryTypes")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getValidJwtToken())
                .get(new GenericType<List<SegmentCategoryType>>() {});
		assertNotNull(response);
	}

	@Test
	public void getMovementSearchKeysTest() {
		List<SearchKeyType> response = getWebTarget()
                .path("movement/rest/config/searchKeys")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getValidJwtToken())
                .get(new GenericType<List<SearchKeyType>>() {});
		assertNotNull(response);
	}

	@Test
	public void getMovementSourceTypesTest() {
		List<MovementSourceType> response = getWebTarget()
                .path("movement/rest/config/movementSourceTypes")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getValidJwtToken())
                .get(new GenericType<List<MovementSourceType>>() {});
		assertNotNull(response);
	}

	@Test
	public void getActivityTypesTest() {
		List<MovementActivityTypeType> response = getWebTarget()
                .path("movement/rest/config/activityTypes")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getValidJwtToken())
                .get(new GenericType<List<MovementActivityTypeType>>() {});
		assertNotNull(response);
	}

	@Test
	public void getConfigurationTest() {
		Response response = getWebTarget()
                .path("movement/rest/config/")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getValidJwtToken())
                .get();
		assertNotNull(response);
        assertEquals(Status.OK.getStatusCode(), response.getStatus());
	}

    @Test
    public void getAlarmStatusesTest() {
		List response = getWebTarget()
				.path("movement/rest/config/alarmstatus")
				.request(MediaType.APPLICATION_JSON)
				.header(HttpHeaders.AUTHORIZATION, getValidJwtToken())
				.get(List.class);

		assertEquals(3, response.size());
    }
}
