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

import java.math.BigInteger;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.http.HttpResponse;
import org.apache.http.client.fluent.Request;
import org.junit.Test;

import eu.europa.ec.fisheries.schema.movement.search.v1.GroupListCriteria;
import eu.europa.ec.fisheries.schema.movement.search.v1.MovementSearchGroup;
import eu.europa.ec.fisheries.schema.movement.search.v1.SearchKeyType;
import eu.europa.ec.fisheries.uvms.docker.validation.asset.AssetTestHelper;
import eu.europa.ec.fisheries.uvms.docker.validation.common.AbstractRestServiceTest;
import eu.europa.ec.fisheries.wsdl.asset.types.Asset;

/**
 * The Class MovementPerformanceIT.
 */

public class MovementMovementSearchRestIT extends AbstractRestServiceTest {

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
		Asset testAsset = AssetTestHelper.createTestAsset();

		MovementSearchGroup movementSearchGroup = new MovementSearchGroup();
		movementSearchGroup.setDynamic(false);
		movementSearchGroup.setUser("vms_admin_com");
		movementSearchGroup.setName("Name" + UUID.randomUUID().toString());
		GroupListCriteria groupListCriteria = new GroupListCriteria();
		groupListCriteria.setType(SearchKeyType.ASSET);
		groupListCriteria.setKey("GUID");
		groupListCriteria.setValue(testAsset.getAssetId().getGuid());
		movementSearchGroup.getSearchFields().add(groupListCriteria);
		
		final HttpResponse response = Request.Post(getBaseUrl() + "movement/rest/search/group")
				.setHeader("Content-Type", "application/json").setHeader("Authorization", getValidJwtToken())
				.bodyByteArray(writeValueAsString(movementSearchGroup).getBytes()).execute().returnResponse();

		Map<String, Object> dataMap = checkSuccessResponseReturnMap(response);
		movementSearchGroup.setId(BigInteger.valueOf(Long.valueOf(""+ dataMap.get("id"))));
		return movementSearchGroup;
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

		final HttpResponse response = Request.Get(getBaseUrl() + "movement/rest/search/group/" + createMovementSearchGroup.getId())
				.setHeader("Content-Type", "application/json").setHeader("Authorization", getValidJwtToken()).execute()
				.returnResponse();

		Map<String, Object> dataMap = checkSuccessResponseReturnMap(response);
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
		
		final HttpResponse response = Request.Put(getBaseUrl() + "movement/rest/search/group")
				.setHeader("Content-Type", "application/json").setHeader("Authorization", getValidJwtToken())
				.bodyByteArray(writeValueAsString(movementSearchGroup).getBytes()).execute().returnResponse();

		Map<String, Object> dataMap = checkSuccessResponseReturnMap(response);
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
		final HttpResponse response = Request.Get(getBaseUrl() + "movement/rest/search/groups/?user=" + URLEncoder.encode("vms_admin_com", "UTF-8"))
				.setHeader("Content-Type", "application/json").setHeader("Authorization", getValidJwtToken()).execute()
				.returnResponse();

		List dataList = checkSuccessResponseReturnType(response,List.class);
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

		final HttpResponse response = Request.Delete(getBaseUrl() + "movement/rest/search/group/" + createMovementSearchGroup.getId())
				.setHeader("Content-Type", "application/json").setHeader("Authorization", getValidJwtToken()).execute()
				.returnResponse();

		Map<String, Object> dataMap = checkSuccessResponseReturnMap(response);
	}

}
