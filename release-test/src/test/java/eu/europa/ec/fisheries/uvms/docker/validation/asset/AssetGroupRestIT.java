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
package eu.europa.ec.fisheries.uvms.docker.validation.asset;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.http.HttpResponse;
import org.apache.http.client.fluent.Request;
import org.junit.Test;

import eu.europa.ec.fisheries.uvms.docker.validation.common.AbstractRestServiceTest;
import eu.europa.ec.fisheries.wsdl.asset.group.AssetGroup;
import eu.europa.ec.fisheries.wsdl.asset.group.AssetGroupSearchField;
import eu.europa.ec.fisheries.wsdl.asset.types.Asset;
import eu.europa.ec.fisheries.wsdl.asset.types.ConfigSearchField;

/**
 * The Class AssetGroupRestIT.
 */

public class AssetGroupRestIT extends AbstractRestServiceTest {

	/**
	 * Gets the asset group list by user test.
	 *
	 * @return the asset group list by user test
	 * @throws Exception
	 *             the exception
	 */
	@Test
	public void getAssetGroupListByUserTest() throws Exception {
		final HttpResponse response = Request.Get(BASE_URL + "asset/rest/group/list?user=vms_admin_com")
				.setHeader("Content-Type", "application/json").setHeader("Authorization", getValidJwtToken()).execute()
				.returnResponse();
		List dataList = checkSuccessResponseReturnType(response, List.class);
	}

	/**
	 * Gets the asset by id test.
	 *
	 * @return the asset by id test
	 * @throws Exception
	 *             the exception
	 */
	@Test
	public void getAssetByIdTest() throws Exception {
		AssetGroup testAssetGroup = createTestAssetGroup();
		assertNotNull(testAssetGroup);

		final HttpResponse response = Request.Get(BASE_URL + "asset/rest/group/" + testAssetGroup.getGuid())
				.setHeader("Content-Type", "application/json").setHeader("Authorization", getValidJwtToken()).execute()
				.returnResponse();
		Map<String, Object> dataMap = checkSuccessResponseReturnMap(response);
	}

	/**
	 * Creates the asset group test.
	 *
	 * @throws Exception the exception
	 */
	@Test
	public void createAssetGroupTest() throws Exception {
		AssetGroup testAssetGroup = createTestAssetGroup();
		assertNotNull(testAssetGroup);
	}

	/**
	 * Creates the test asset group.
	 *
	 * @return the asset group
	 * @throws Exception the exception
	 */
	private AssetGroup createTestAssetGroup() throws Exception {
		Asset testAsset = createTestAsset();
		AssetGroup assetGroup = new AssetGroup();
		assetGroup.setDynamic(false);
		assetGroup.setGlobal(false);
		assetGroup.setUser("vms_admin_com");
		assetGroup.setName("Name" + UUID.randomUUID().toString());
		assetGroup.setGuid(UUID.randomUUID().toString());

		AssetGroupSearchField assetGroupSearchField = new AssetGroupSearchField();
		assetGroupSearchField.setKey(ConfigSearchField.GUID);
		assetGroupSearchField.setValue(testAsset.getAssetId().getGuid());
		assetGroup.getSearchFields().add(assetGroupSearchField);

		final HttpResponse response = Request.Post(BASE_URL + "asset/rest/group")
				.setHeader("Content-Type", "application/json").setHeader("Authorization", getValidJwtToken())
				.bodyByteArray(writeValueAsString(assetGroup).getBytes()).execute().returnResponse();
		Map<String, Object> dataMap = checkSuccessResponseReturnMap(response);
		String guid = (String) dataMap.get("guid");
		assetGroup.setGuid(guid);
		return assetGroup;
	}

	/**
	 * Update asset group test.
	 *
	 * @throws Exception the exception
	 */
	@Test
	public void updateAssetGroupTest() throws Exception {
		AssetGroup testAssetGroup = createTestAssetGroup();
		assertNotNull(testAssetGroup);
		testAssetGroup.setName("ChangedName" + UUID.randomUUID().toString());

		
		final HttpResponse response = Request.Put(BASE_URL + "asset/rest/group")
				.setHeader("Content-Type", "application/json").setHeader("Authorization", getValidJwtToken())
				.bodyByteArray(writeValueAsString(testAssetGroup).getBytes()).execute().returnResponse();
		Map<String, Object> dataMap = checkSuccessResponseReturnMap(response);
	}

	/**
	 * Delete asset group test.
	 *
	 * @throws Exception the exception
	 */
	@Test
	public void deleteAssetGroupTest() throws Exception {
		AssetGroup testAssetGroup = createTestAssetGroup();
		assertNotNull(testAssetGroup);

		final HttpResponse response = Request.Delete(BASE_URL + "asset/rest/group/" + testAssetGroup.getGuid())
				.setHeader("Content-Type", "application/json").setHeader("Authorization", getValidJwtToken()).execute()
				.returnResponse();
		Map<String, Object> dataMap = checkSuccessResponseReturnMap(response);
	}

}
