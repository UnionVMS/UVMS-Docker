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

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.http.HttpResponse;
import org.apache.http.client.fluent.Request;
import org.junit.Test;

import eu.europa.ec.fisheries.uvms.asset.model.constants.AuditOperationEnum;
import eu.europa.ec.fisheries.uvms.commons.date.DateUtils;
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
		AssetGroup assetGroup = createBasicAssetGroup();
		
		Asset asset1 = AssetTestHelper.createTestAsset();
		Asset asset2 = AssetTestHelper.createTestAsset();
		
		// Add assets to group
		AssetGroupSearchField assetGroupSearchField1 = new AssetGroupSearchField();
		assetGroupSearchField1.setKey(ConfigSearchField.GUID);
		assetGroupSearchField1.setValue(asset1.getAssetId().getGuid());
		assetGroup.getSearchFields().add(assetGroupSearchField1);
	
		AssetGroupSearchField assetGroupSearchField2 = new AssetGroupSearchField();
		assetGroupSearchField2.setKey(ConfigSearchField.GUID);
		assetGroupSearchField2.setValue(asset2.getAssetId().getGuid());
		assetGroup.getSearchFields().add(assetGroupSearchField2);
	
		// Create Group
		assetGroup = AssetTestHelper.createAssetGroup(assetGroup);

		assertTrue(assetGroup.getSearchFields().contains(assetGroupSearchField1));
		assertTrue(assetGroup.getSearchFields().contains(assetGroupSearchField2));
		
		List<AssetGroup> assetGroups = AssetTestHelper.getAssetGroupListByUser(assetGroup.getUser());

		assertTrue(assetGroups.contains(assetGroup));
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
		AssetGroup testAssetGroup = createBasicAssetGroup();
		testAssetGroup = AssetTestHelper.createAssetGroup(testAssetGroup);
		
		AssetGroup assetGroupById = AssetTestHelper.getAssetGroupById(testAssetGroup.getGuid());
		assertEquals(testAssetGroup, assetGroupById);
	}

	/**
	 * Creates the asset group test.
	 *
	 * @throws Exception the exception
	 */
	@Test
	public void createAssetGroupTest() throws Exception {
		AssetGroup testAssetGroup = createBasicAssetGroup();
		AssetGroup createdAssetGroup = AssetTestHelper.createAssetGroup(testAssetGroup);

		assertEquals(testAssetGroup.getName(), createdAssetGroup.getName());
		assertEquals(testAssetGroup.getUser(), createdAssetGroup.getUser());
		assertEquals(testAssetGroup.isDynamic(), createdAssetGroup.isDynamic());
		assertEquals(testAssetGroup.isGlobal(), createdAssetGroup.isGlobal());
	}
	
	@Test
	public void createAssetGroupAuditLogCreatedTest() throws Exception {
		Date fromDate = DateUtils.getNowDateUTC();
		AssetGroup testAssetGroup = createBasicAssetGroup();
		AssetGroup createdAssetGroup = AssetTestHelper.createAssetGroup(testAssetGroup);
		
		AssetTestHelper.assertAssetGroupAuditLogCreated(createdAssetGroup.getGuid(), AuditOperationEnum.CREATE, fromDate);
	}


	/**
	 * Update asset group test.
	 *
	 * @throws Exception the exception
	 */
	@Test
	public void updateAssetGroupTest() throws Exception {
		AssetGroup testAssetGroup = createBasicAssetGroup();
		testAssetGroup = AssetTestHelper.createAssetGroup(testAssetGroup);

		testAssetGroup.setName("ChangedName" + UUID.randomUUID().toString());
		AssetGroup updatedAssetGroup = AssetTestHelper.updateAssetGroup(testAssetGroup);
		
		assertEquals(testAssetGroup.getName(), updatedAssetGroup.getName());
	}
	
	@Test
	public void updateAssetGroupAuditLogCreatedTest() throws Exception {
		Date fromDate = DateUtils.getNowDateUTC();
		AssetGroup testAssetGroup = createBasicAssetGroup();
		testAssetGroup = AssetTestHelper.createAssetGroup(testAssetGroup);
		testAssetGroup.setName("ChangedName" + UUID.randomUUID().toString());
		AssetTestHelper.updateAssetGroup(testAssetGroup);

		AssetTestHelper.assertAssetGroupAuditLogCreated(testAssetGroup.getGuid(), AuditOperationEnum.UPDATE, fromDate);
	}

	/**
	 * Delete asset group test.
	 *
	 * @throws Exception the exception
	 */
	@Test
	public void deleteAssetGroupTest() throws Exception {
		AssetGroup testAssetGroup = createBasicAssetGroup();
		
		List<AssetGroup> initialAssetGroupList = AssetTestHelper.getAssetGroupListByUser(testAssetGroup.getUser());
		int initialSize = initialAssetGroupList.size();
		
		testAssetGroup = AssetTestHelper.createAssetGroup(testAssetGroup);

		List<AssetGroup> firstAssetGroupList = AssetTestHelper.getAssetGroupListByUser(testAssetGroup.getUser());
		assertEquals(initialSize + 1, firstAssetGroupList.size());
		
		// Delete the AssetGroup
		AssetTestHelper.deleteAssetGroup(testAssetGroup);
		
		List<AssetGroup> secondAssetGroupList = AssetTestHelper.getAssetGroupListByUser(testAssetGroup.getUser());
		assertEquals(initialSize, secondAssetGroupList.size());
	}
	
	@Test
	public void deleteAssetGroupAuditLogCreatedTest() throws Exception {
		Date fromDate = DateUtils.getNowDateUTC();
		AssetGroup testAssetGroup = createBasicAssetGroup();
		testAssetGroup = AssetTestHelper.createAssetGroup(testAssetGroup);

		// Delete the AssetGroup
		AssetTestHelper.deleteAssetGroup(testAssetGroup);
		
		AssetTestHelper.assertAssetGroupAuditLogCreated(testAssetGroup.getGuid(), AuditOperationEnum.ARCHIVE, fromDate);
	}

	private AssetGroup createBasicAssetGroup() {
		AssetGroup assetGroup = new AssetGroup();
		assetGroup.setDynamic(false);
		assetGroup.setGlobal(false);
		assetGroup.setUser("vms_admin_com");
		assetGroup.setName("Name" + UUID.randomUUID().toString());
		return assetGroup;
	}
}
