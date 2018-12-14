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
import java.util.UUID;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import org.hamcrest.CoreMatchers;
import org.junit.Test;
import eu.europa.ec.fisheries.schema.movement.search.v1.GroupListCriteria;
import eu.europa.ec.fisheries.schema.movement.search.v1.MovementSearchGroup;
import eu.europa.ec.fisheries.schema.movement.search.v1.SearchKeyType;
import eu.europa.ec.fisheries.uvms.asset.client.model.AssetDTO;
import eu.europa.ec.fisheries.uvms.commons.rest.dto.ResponseDto;
import eu.europa.ec.fisheries.uvms.docker.validation.asset.AssetTestHelper;
import eu.europa.ec.fisheries.uvms.docker.validation.common.AbstractRest;

/**
 * The Class MovementPerformanceIT.
 */

public class MovementMovementSearchRestIT extends AbstractRest {

	/**
	 * Creates the movement search group test.
	 *
	 * @throws Exception
	 *             the exception
	 */
	@Test
	public void createMovementSearchGroupTest() throws Exception {
		MovementSearchGroup createMovementSearchGroup = createMovementSearchGroup();
		assertNotNull(createMovementSearchGroup);
	}

	/**
	 * Creates the movement search group.
	 *
	 * @return the movement search group
	 * @throws Exception the exception
	 */
	private MovementSearchGroup createMovementSearchGroup() throws Exception {
		AssetDTO testAsset = AssetTestHelper.createTestAsset();

		MovementSearchGroup movementSearchGroup = new MovementSearchGroup();
		movementSearchGroup.setDynamic(false);
		movementSearchGroup.setUser("vms_admin_com");
		movementSearchGroup.setName("Name" + UUID.randomUUID().toString());
		GroupListCriteria groupListCriteria = new GroupListCriteria();
		groupListCriteria.setType(SearchKeyType.ASSET);
		groupListCriteria.setKey("GUID");
		groupListCriteria.setValue(testAsset.getId().toString());
		movementSearchGroup.getSearchFields().add(groupListCriteria);
		
		ResponseDto<MovementSearchGroup> response = getWebTarget()
                .path("movement/rest/search/group")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getValidJwtToken())
                .post(Entity.json(movementSearchGroup), new GenericType<ResponseDto<MovementSearchGroup>>() {});
        return response.getData();
		
	}

	/**
	 * Gets the movement search group test.
	 *
	 * @return the movement search group test
	 * @throws Exception
	 *             the exception
	 */
	@Test
	public void getMovementSearchGroupTest() throws Exception {
		MovementSearchGroup createMovementSearchGroup = createMovementSearchGroup();
		assertNotNull(createMovementSearchGroup);
		
		ResponseDto<MovementSearchGroup> response = getWebTarget()
                .path("movement/rest/search/group/")
                .path(createMovementSearchGroup.getId().toString())
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getValidJwtToken())
                .get(new GenericType<ResponseDto<MovementSearchGroup>>() {});
        
		MovementSearchGroup fetchedSearchGroup = response.getData();
        assertNotNull(fetchedSearchGroup);
        assertThat(fetchedSearchGroup.getId(), CoreMatchers.is(createMovementSearchGroup.getId()));
	}

	/**
	 * Update movement seach group test.
	 *
	 * @throws Exception
	 *             the exception
	 */
	@Test
	public void updateMovementSeachGroupTest() throws Exception {
		MovementSearchGroup movementSearchGroup = createMovementSearchGroup();
		assertNotNull(movementSearchGroup);

		movementSearchGroup.setName("ChangedName" + UUID.randomUUID().toString());
		
		ResponseDto<MovementSearchGroup> response = getWebTarget()
                .path("movement/rest/search/group")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getValidJwtToken())
                .put(Entity.json(movementSearchGroup), new GenericType<ResponseDto<MovementSearchGroup>>() {});
		
		MovementSearchGroup fetchedSearchGroup = response.getData();
		assertThat(fetchedSearchGroup.getName(), CoreMatchers.is(movementSearchGroup.getName()));
		
	}

	/**
	 * Gets the movement search groups by user test.
	 *
	 * @return the movement search groups by user test
	 * @throws Exception
	 *             the exception
	 */
	@Test
	public void getMovementSearchGroupsByUserTest() throws Exception {
		ResponseDto<List<MovementSearchGroup>> response = getWebTarget()
                .path("movement/rest/search/groups")
                .queryParam("user", "vms_admin_com")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getValidJwtToken())
                .get(new GenericType<ResponseDto<List<MovementSearchGroup>>>() {});
		List<MovementSearchGroup> searchGroups = response.getData();
		assertThat(searchGroups, CoreMatchers.is(CoreMatchers.notNullValue()));
	}

	/**
	 * Delete movement search group test.
	 *
	 * @throws Exception
	 *             the exception
	 */
	@Test
	public void deleteMovementSearchGroupTest() throws Exception {
		MovementSearchGroup createMovementSearchGroup = createMovementSearchGroup();
		assertNotNull(createMovementSearchGroup);

		ResponseDto<MovementSearchGroup> response = getWebTarget()
                .path("movement/rest/search/group/")
                .path(createMovementSearchGroup.getId().toString())
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getValidJwtToken())
                .delete(new GenericType<ResponseDto<MovementSearchGroup>>() {});
		
		MovementSearchGroup deletedGroup = response.getData();
		assertThat(deletedGroup, CoreMatchers.is(CoreMatchers.notNullValue()));
	}

}
