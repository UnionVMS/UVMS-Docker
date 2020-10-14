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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertNotNull;
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
import eu.europa.ec.fisheries.uvms.docker.validation.asset.AssetTestHelper;
import eu.europa.ec.fisheries.uvms.docker.validation.common.AbstractRest;

public class MovementMovementSearchRestIT extends AbstractRest {

	@Test
	public void createMovementSearchGroupTest() {
		MovementSearchGroup createMovementSearchGroup = createMovementSearchGroup();
		assertNotNull(createMovementSearchGroup);
	}

	private MovementSearchGroup createMovementSearchGroup() {
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

		return getWebTarget()
                .path("movement/rest/search/group")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getValidJwtToken())
                .post(Entity.json(movementSearchGroup), new GenericType<MovementSearchGroup>() {});
	}

	@Test
	public void getMovementSearchGroupTest() {
		MovementSearchGroup createMovementSearchGroup = createMovementSearchGroup();
		assertNotNull(createMovementSearchGroup);
		
		MovementSearchGroup fetchedSearchGroup = getWebTarget()
                .path("movement/rest/search/group/")
                .path(createMovementSearchGroup.getId().toString())
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getValidJwtToken())
                .get(new GenericType<MovementSearchGroup>() {});

        assertNotNull(fetchedSearchGroup);
        assertThat(fetchedSearchGroup.getId(), CoreMatchers.is(createMovementSearchGroup.getId()));
	}

	@Test
	public void updateMovementSearchGroupTest() {
		MovementSearchGroup movementSearchGroup = createMovementSearchGroup();
		assertNotNull(movementSearchGroup);

		movementSearchGroup.setName("ChangedName" + UUID.randomUUID().toString());
		
		MovementSearchGroup fetchedSearchGroup = getWebTarget()
                .path("movement/rest/search/group")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getValidJwtToken())
                .put(Entity.json(movementSearchGroup), new GenericType<MovementSearchGroup>() {});

		assertThat(fetchedSearchGroup.getName(), CoreMatchers.is(movementSearchGroup.getName()));
	}

	@Test
	public void getMovementSearchGroupsByUserTest() {
		List<MovementSearchGroup> searchGroups = getWebTarget()
                .path("movement/rest/search/groups")
                .queryParam("user", "vms_admin_com")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getValidJwtToken())
                .get(new GenericType<List<MovementSearchGroup>>() {});

		assertThat(searchGroups, CoreMatchers.is(CoreMatchers.notNullValue()));
	}

	@Test
	public void deleteMovementSearchGroupTest() {
		MovementSearchGroup createMovementSearchGroup = createMovementSearchGroup();
		assertNotNull(createMovementSearchGroup);

		MovementSearchGroup deletedGroup = getWebTarget()
                .path("movement/rest/search/group/")
                .path(createMovementSearchGroup.getId().toString())
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getValidJwtToken())
                .delete(new GenericType<MovementSearchGroup>() {});

		assertThat(deletedGroup, CoreMatchers.is(CoreMatchers.notNullValue()));
	}
}
