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

import java.util.List;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import org.hamcrest.CoreMatchers;
import org.junit.Test;
import eu.europa.ec.fisheries.schema.exchange.movement.v1.MovementActivityTypeType;
import eu.europa.ec.fisheries.schema.movement.search.v1.SearchKeyType;
import eu.europa.ec.fisheries.schema.movement.v1.MovementSourceType;
import eu.europa.ec.fisheries.schema.movement.v1.MovementTypeType;
import eu.europa.ec.fisheries.schema.movement.v1.SegmentCategoryType;
import eu.europa.ec.fisheries.uvms.commons.rest.dto.ResponseDto;
import eu.europa.ec.fisheries.uvms.docker.validation.common.AbstractRest;

/**
 * The Class MovementConfigRestIT.
 */

public class MovementConfigRestIT extends AbstractRest {

	/**
	 * Gets the movement types test.
	 *
	 * @return the movement types test
	 * @throws Exception
	 *             the exception
	 */
	@Test
	public void getMovementTypesTest() throws Exception {
		ResponseDto<List<MovementTypeType>> response = getWebTarget()
		        .path("movement/rest/config/movementTypes")
		        .request(MediaType.APPLICATION_JSON)
		        .header(HttpHeaders.AUTHORIZATION, getValidJwtToken())
		        .get(new GenericType<ResponseDto<List<MovementTypeType>>>() {});
		assertThat(response.getData(), CoreMatchers.is(CoreMatchers.notNullValue()));
	}

	/**
	 * Gets the segmet types test.
	 *
	 * @return the segmet types test
	 * @throws Exception
	 *             the exception
	 */
	@Test
	public void getSegmentTypesTest() throws Exception {
		ResponseDto<List<SegmentCategoryType>> response = getWebTarget()
                .path("movement/rest/config/segmentCategoryTypes")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getValidJwtToken())
                .get(new GenericType<ResponseDto<List<SegmentCategoryType>>>() {});
        assertThat(response.getData(), CoreMatchers.is(CoreMatchers.notNullValue()));
	}

	/**
	 * Gets the movement search keys test.
	 *
	 * @return the movement search keys test
	 * @throws Exception
	 *             the exception
	 */
	@Test
	public void getMovementSearchKeysTest() throws Exception {
		ResponseDto<List<SearchKeyType>> response = getWebTarget()
                .path("movement/rest/config/searchKeys")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getValidJwtToken())
                .get(new GenericType<ResponseDto<List<SearchKeyType>>>() {});
        assertThat(response.getData(), CoreMatchers.is(CoreMatchers.notNullValue()));
	}

	/**
	 * Gets the movement source types test.
	 *
	 * @return the movement source types test
	 * @throws Exception
	 *             the exception
	 */
	@Test
	public void getMovementSourceTypesTest() throws Exception {
		ResponseDto<List<MovementSourceType>> response = getWebTarget()
                .path("movement/rest/config/movementSourceTypes")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getValidJwtToken())
                .get(new GenericType<ResponseDto<List<MovementSourceType>>>() {});
        assertThat(response.getData(), CoreMatchers.is(CoreMatchers.notNullValue()));
	}

	/**
	 * Gets the activity types test.
	 *
	 * @return the activity types test
	 * @throws Exception
	 *             the exception
	 */
	@Test
	public void getActivityTypesTest() throws Exception {
		ResponseDto<List<MovementActivityTypeType>> response = getWebTarget()
                .path("movement/rest/config/activityTypes")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getValidJwtToken())
                .get(new GenericType<ResponseDto<List<MovementActivityTypeType>>>() {});
        assertThat(response.getData(), CoreMatchers.is(CoreMatchers.notNullValue()));
	}

	/**
	 * Gets the configuration test.
	 *
	 * @return the configuration test
	 * @throws Exception
	 *             the exception
	 */
	@Test
	public void getConfigurationTest() throws Exception {
		Response response = getWebTarget()
                .path("movement/rest/config/")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getValidJwtToken())
                .get();
        assertThat(response, CoreMatchers.is(CoreMatchers.notNullValue()));
        assertThat(response.getStatus(), CoreMatchers.is(Status.OK.getStatusCode()));
	}

}
