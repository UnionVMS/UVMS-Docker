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
import java.util.UUID;
import org.junit.Ignore;
import org.junit.Test;
import eu.europa.ec.fisheries.uvms.asset.client.model.AssetDTO;
import eu.europa.ec.fisheries.uvms.asset.client.model.AssetGroup;
import eu.europa.ec.fisheries.uvms.asset.client.model.AssetGroupField;
import eu.europa.ec.fisheries.uvms.asset.model.constants.AuditOperationEnum;
import eu.europa.ec.fisheries.uvms.commons.date.DateUtils;
import eu.europa.ec.fisheries.uvms.docker.validation.common.AbstractRest;

/**
 * The Class AssetGroupRestIT.
 */

public class AssetGroupRestIT extends AbstractRest {

	/**
	 * Gets the asset group list by user test.
	 *
	 * @return the asset group list by user test
	 * @throws Exception
	 *             the exception
	 */
	@Test
	public void getAssetGroupListByUserTest() throws Exception {
		// Create Group
	    AssetGroup assetGroup = AssetTestHelper.createBasicAssetGroup();
	    AssetGroup createdAssetGroup = AssetTestHelper.createAssetGroup(assetGroup);

		AssetDTO asset1 = AssetTestHelper.createTestAsset();
		AssetDTO asset2 = AssetTestHelper.createTestAsset();
		
		// Add assets to group
		AssetGroupField assetGroupField1 = new AssetGroupField();
		assetGroupField1.setField("GUID");
		assetGroupField1.setValue(asset1.getId().toString());
		assetGroupField1.setAssetGroup(createdAssetGroup);
		AssetGroupField createdAssetGroupField1 = AssetTestHelper.createAssetGroupField(createdAssetGroup.getId(), assetGroupField1);
	
		AssetGroupField assetGroupField2 = new AssetGroupField();
		assetGroupField2.setField("GUID");
		assetGroupField2.setValue(asset2.getId().toString());
		assetGroupField2.setAssetGroup(createdAssetGroup);
		AssetGroupField createdAssetGroupField2 = AssetTestHelper.createAssetGroupField(createdAssetGroup.getId(), assetGroupField2);
	
		List<AssetGroupField> fetchedAssetGroups = AssetTestHelper.getAssetGroupFieldByAssetGroup(createdAssetGroup.getId());
		assertTrue(fetchedAssetGroups.stream().anyMatch(field -> field.getId().equals(createdAssetGroupField1.getId())));
		assertTrue(fetchedAssetGroups.stream().anyMatch(field -> field.getId().equals(createdAssetGroupField2.getId())));
		
		List<AssetGroup> assetGroups = AssetTestHelper.getAssetGroupListByUser(createdAssetGroup.getOwner());

		assertTrue(assetGroups.stream().anyMatch(group -> group.getId().equals(createdAssetGroup.getId())));
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
		AssetGroup testAssetGroup = AssetTestHelper.createBasicAssetGroup();
		testAssetGroup = AssetTestHelper.createAssetGroup(testAssetGroup);
		
		AssetGroup assetGroupById = AssetTestHelper.getAssetGroupById(testAssetGroup.getId());
		assertEquals(testAssetGroup.getId(), assetGroupById.getId());
		assertEquals(testAssetGroup.getUpdateTime(), assetGroupById.getUpdateTime());
	}

	/**
	 * Creates the asset group test.
	 *
	 * @throws Exception the exception
	 */
	@Test
	public void createAssetGroupTest() throws Exception {
		AssetGroup testAssetGroup = AssetTestHelper.createBasicAssetGroup();
		AssetGroup createdAssetGroup = AssetTestHelper.createAssetGroup(testAssetGroup);

		assertEquals(testAssetGroup.getName(), createdAssetGroup.getName());
		assertEquals(testAssetGroup.getOwner(), createdAssetGroup.getOwner());
		assertEquals(testAssetGroup.getDynamic(), createdAssetGroup.getDynamic());
		assertEquals(testAssetGroup.getGlobal(), createdAssetGroup.getGlobal());
	}
	
	@Ignore // No audit logs sent by asset groups
	@Test
	public void createAssetGroupAuditLogCreatedTest() throws Exception {
		Date fromDate = DateUtils.getNowDateUTC();
		AssetGroup testAssetGroup = AssetTestHelper.createBasicAssetGroup();
		AssetGroup createdAssetGroup = AssetTestHelper.createAssetGroup(testAssetGroup);
		
		AssetTestHelper.assertAssetGroupAuditLogCreated(createdAssetGroup.getId(), AuditOperationEnum.CREATE, fromDate);
	}


	/**
	 * Update asset group test.
	 *
	 * @throws Exception the exception
	 */
	@Test
	public void updateAssetGroupTest() throws Exception {
		AssetGroup testAssetGroup = AssetTestHelper.createBasicAssetGroup();
		testAssetGroup = AssetTestHelper.createAssetGroup(testAssetGroup);

		testAssetGroup.setName("ChangedName" + UUID.randomUUID().toString());
		AssetGroup updatedAssetGroup = AssetTestHelper.updateAssetGroup(testAssetGroup);
		
		assertEquals(testAssetGroup.getName(), updatedAssetGroup.getName());
	}
	
	@Ignore
	@Test
	public void updateAssetGroupAuditLogCreatedTest() throws Exception {
		Date fromDate = DateUtils.getNowDateUTC();
		AssetGroup testAssetGroup = AssetTestHelper.createBasicAssetGroup();
		testAssetGroup = AssetTestHelper.createAssetGroup(testAssetGroup);
		testAssetGroup.setName("ChangedName" + UUID.randomUUID().toString());
		AssetTestHelper.updateAssetGroup(testAssetGroup);

		AssetTestHelper.assertAssetGroupAuditLogCreated(testAssetGroup.getId(), AuditOperationEnum.UPDATE, fromDate);
	}

	/**
	 * Delete asset group test.
	 *
	 * @throws Exception the exception
	 */
	@Test
	public void deleteAssetGroupTest() throws Exception {
		AssetGroup testAssetGroup = AssetTestHelper.createBasicAssetGroup();
		
		List<AssetGroup> initialAssetGroupList = AssetTestHelper.getAssetGroupListByUser(testAssetGroup.getOwner());
		int initialSize = initialAssetGroupList.size();
		
		testAssetGroup = AssetTestHelper.createAssetGroup(testAssetGroup);

		List<AssetGroup> firstAssetGroupList = AssetTestHelper.getAssetGroupListByUser(testAssetGroup.getOwner());
		assertEquals(initialSize + 1, firstAssetGroupList.size());
		
		// Delete the AssetGroup
		AssetTestHelper.deleteAssetGroup(testAssetGroup);
		
		List<AssetGroup> secondAssetGroupList = AssetTestHelper.getAssetGroupListByUser(testAssetGroup.getOwner());
		assertEquals(initialSize, secondAssetGroupList.size());
	}
	
	@Ignore
	@Test
	public void deleteAssetGroupAuditLogCreatedTest() throws Exception {
		Date fromDate = DateUtils.getNowDateUTC();
		AssetGroup testAssetGroup = AssetTestHelper.createBasicAssetGroup();
		testAssetGroup = AssetTestHelper.createAssetGroup(testAssetGroup);

		// Delete the AssetGroup
		AssetTestHelper.deleteAssetGroup(testAssetGroup);
		
		AssetTestHelper.assertAssetGroupAuditLogCreated(testAssetGroup.getId(), AuditOperationEnum.ARCHIVE, fromDate);
	}
}
