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

import java.util.*;

import org.apache.http.HttpResponse;
import org.apache.http.client.fluent.Request;
import org.junit.Test;

import eu.europa.ec.fisheries.uvms.asset.model.constants.AuditOperationEnum;
import eu.europa.ec.fisheries.uvms.commons.date.DateUtils;
import eu.europa.ec.fisheries.uvms.docker.validation.common.AbstractRestServiceTest;
import eu.europa.ec.fisheries.wsdl.asset.types.Asset;
import eu.europa.ec.fisheries.wsdl.asset.types.AssetIdType;
import eu.europa.ec.fisheries.wsdl.asset.types.AssetListCriteriaPair;
import eu.europa.ec.fisheries.wsdl.asset.types.AssetListQuery;
import eu.europa.ec.fisheries.wsdl.asset.types.ConfigSearchField;
import eu.europa.ec.fisheries.wsdl.asset.types.ListAssetResponse;

/**
 * The Class AssetRestIT.
 */
public class AssetRestIT extends AbstractRestServiceTest {

	/**
	 * Gets the asset list test.
	 *
	 * @return the asset list test
	 * @throws Exception
	 *             the exception
	 */
	@Test
	public void getAssetListTest() throws Exception {
		Asset asset = AssetTestHelper.createTestAsset();
		
		AssetListQuery assetListQuery = AssetTestHelper.getBasicAssetQuery();
		AssetListCriteriaPair assetListCriteriaPair = new AssetListCriteriaPair();
		assetListCriteriaPair.setKey(ConfigSearchField.FLAG_STATE);
		assetListCriteriaPair.setValue(asset.getCountryCode());
		assetListQuery.getAssetSearchCriteria().getCriterias().add(assetListCriteriaPair);
		
		ListAssetResponse assetListResponse = AssetTestHelper.assetListQuery(assetListQuery);
		List<Asset> assets = assetListResponse.getAsset();
		assertTrue(assets.contains(asset));
	}
	
	@Test
	public void getAssetListMultipleAssetsGuidsTest() throws Exception {
		Asset asset1 = AssetTestHelper.createTestAsset();
		Asset asset2 = AssetTestHelper.createTestAsset();
		
		AssetListQuery assetListQuery = AssetTestHelper.getBasicAssetQuery();
		AssetListCriteriaPair assetListCriteriaPair1 = new AssetListCriteriaPair();
		assetListCriteriaPair1.setKey(ConfigSearchField.GUID);
		assetListCriteriaPair1.setValue(asset1.getAssetId().getGuid());
		assetListQuery.getAssetSearchCriteria().getCriterias().add(assetListCriteriaPair1);
		AssetListCriteriaPair assetListCriteriaPair2 = new AssetListCriteriaPair();
		assetListCriteriaPair2.setKey(ConfigSearchField.GUID);
		assetListCriteriaPair2.setValue(asset2.getAssetId().getGuid());
		assetListQuery.getAssetSearchCriteria().getCriterias().add(assetListCriteriaPair2);
		
		ListAssetResponse assetListResponse = AssetTestHelper.assetListQuery(assetListQuery);
		List<Asset> assets = assetListResponse.getAsset();
		assertEquals(2, assets.size());
		assertTrue(assets.contains(asset1));
		assertTrue(assets.contains(asset2));
	}

	@Test
	public void getAssetListItemCountTest() throws Exception {
		AssetListQuery assetListQuery = AssetTestHelper.getBasicAssetQuery();
		AssetListCriteriaPair assetListCriteriaPair = new AssetListCriteriaPair();
		assetListCriteriaPair.setKey(ConfigSearchField.FLAG_STATE);
		assetListCriteriaPair.setValue("SWE");
		assetListQuery.getAssetSearchCriteria().getCriterias().add(assetListCriteriaPair);
		
		Integer countBefore = AssetTestHelper.assetListQueryCount(assetListQuery);
		
		// Add new asset
		Asset asset = AssetTestHelper.createDummyAsset(AssetIdType.GUID);
		asset.setCountryCode("SWE");
		AssetTestHelper.createAsset(asset);
		
		Integer countAfter = AssetTestHelper.assetListQueryCount(assetListQuery);
		assertEquals(Integer.valueOf(countBefore + 1), countAfter);
	}
	
	@Test
	public void getAssetListUpdatedIRCSNotFoundTest() throws Exception {
		Asset testAsset = AssetTestHelper.createTestAsset();
		
		AssetListQuery assetListQuery = AssetTestHelper.getBasicAssetQuery();
		AssetListCriteriaPair assetListCriteriaPair = new AssetListCriteriaPair();
		assetListCriteriaPair.setKey(ConfigSearchField.IRCS);
		assetListCriteriaPair.setValue(testAsset.getIrcs());
		assetListQuery.getAssetSearchCriteria().getCriterias().add(assetListCriteriaPair);
		
		ListAssetResponse assetList = AssetTestHelper.assetListQuery(assetListQuery);
		List<Asset> assets = assetList.getAsset();
		assertTrue(assets.contains(testAsset));
		
		testAsset.setIrcs(testAsset.getIrcs() + "NEW");
		AssetTestHelper.updateAsset(testAsset);
		
		// Search with same query, the asset should not be found
		assetList = AssetTestHelper.assetListQuery(assetListQuery);
		assets = assetList.getAsset();
		assertFalse(assets.contains(testAsset));
	}

	@Test
	public void getAssetListWithLikeSearchValue() throws Exception {
		Asset asset = AssetTestHelper.createDummyAsset(AssetIdType.GUID);
		asset.setHomePort("MyHomePort");
		asset = AssetTestHelper.createAsset(asset);
		
		AssetListQuery assetListQuery = AssetTestHelper.getBasicAssetQuery();
		AssetListCriteriaPair assetListCriteriaPair = new AssetListCriteriaPair();
		assetListCriteriaPair.setKey(ConfigSearchField.HOMEPORT);
		assetListCriteriaPair.setValue("My*");
		assetListQuery.getAssetSearchCriteria().getCriterias().add(assetListCriteriaPair);
		
		ListAssetResponse assetList = AssetTestHelper.assetListQuery(assetListQuery);
		List<Asset> assets = assetList.getAsset();
		assertTrue(assets.contains(asset));
	}
	
	/**
	 * Gets the note activity codes test.
	 *
	 * @return the note activity codes test
	 * @throws Exception
	 *             the exception
	 */
	@Test
	public void getNoteActivityCodesTest() throws Exception {
		final HttpResponse response = Request.Get(getBaseUrl() + "asset/rest/asset/activitycodes")
				.setHeader("Content-Type", "application/json").setHeader("Authorization", getValidJwtToken()).execute()
				.returnResponse();
		Map<String, Object> dataMap = checkSuccessResponseReturnMap(response);

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
		Asset asset = AssetTestHelper.createTestAsset();
		Asset assetByGuid = AssetTestHelper.getAssetByGuid(asset.getAssetId().getGuid());
		assertEquals(asset, assetByGuid);
	}

	/**
	 * Creates the asset test.
	 *
	 * @throws Exception
	 *             the exception
	 */
	@Test
	public void createAssetTest() throws Exception {
		AssetTestHelper.createTestAsset();
	}

	@Test
	public void createAssetAuditLogCreatedTest() throws Exception {
		Date fromDate = DateUtils.getNowDateUTC();
		Asset asset = AssetTestHelper.createTestAsset();
		AssetTestHelper.assertAssetAuditLogCreated(asset.getAssetId().getGuid(), AuditOperationEnum.CREATE, fromDate);
	}
	

	@Test
	public void updateAssetAuditLogCreatedTest() throws Exception {
		Date fromDate = DateUtils.getNowDateUTC();
		Asset testAsset = AssetTestHelper.createTestAsset();
		String newName = testAsset.getName() + "Changed";
		testAsset.setName(newName);
		AssetTestHelper.updateAsset(testAsset);
		
		AssetTestHelper.assertAssetAuditLogCreated(testAsset.getAssetId().getGuid(), AuditOperationEnum.UPDATE, fromDate);
	}

	/**
	 * Archive asset test.
	 *
	 * @throws Exception
	 *             the exception
	 */
	@Test
	public void archiveAssetTest() throws Exception {
		Asset testAsset = AssetTestHelper.createTestAsset();
		testAsset.setActive(false);
		AssetTestHelper.archiveAsset(testAsset);
	}

	@Test
	public void archiveAssetAuditLogCreatedTest() throws Exception {
		Date fromDate = DateUtils.getNowDateUTC();
		Asset testAsset = AssetTestHelper.createTestAsset();
		testAsset.setActive(false);
		AssetTestHelper.archiveAsset(testAsset);
		
		AssetTestHelper.assertAssetAuditLogCreated(testAsset.getAssetId().getGuid(), AuditOperationEnum.ARCHIVE, fromDate);
	}
	
	/**
	 * Asset list group by flag state test.
	 *
	 * @throws Exception
	 *             the exception
	 */
	@Test
	public void assetListGroupByFlagStateTest() throws Exception {
		Asset asset = AssetTestHelper.createTestAsset();
		ArrayList<String> assetIdList = new ArrayList<String>();
		assetIdList.add(asset.getAssetId().getGuid());

		final HttpResponse response = Request.Post(getBaseUrl() + "asset/rest/asset/listGroupByFlagState")
				.setHeader("Content-Type", "application/json").setHeader("Authorization", getValidJwtToken())
				.bodyByteArray(writeValueAsString(assetIdList).getBytes()).execute().returnResponse();
		Map<String, Object> dataMap = checkSuccessResponseReturnMap(response);
	}


	@Test
	public void getAssetListWithLikeSearchValue_ICCAT_AND_UVI_GFCM() throws Exception {
		Asset asset = AssetTestHelper.createDummyAsset(AssetIdType.GUID);

		String theValue = UUID.randomUUID().toString();
		asset.setIccat(theValue);
		asset.setUvi(theValue);
		asset.setGfcm(theValue);

		asset = AssetTestHelper.createAsset(asset);

		AssetListQuery assetListQuery = AssetTestHelper.getBasicAssetQuery();
		AssetListCriteriaPair assetListCriteriaPair_ICCAT = new AssetListCriteriaPair();
		AssetListCriteriaPair assetListCriteriaPair_UVI = new AssetListCriteriaPair();
		AssetListCriteriaPair assetListCriteriaPair_GFCM = new AssetListCriteriaPair();

		assetListCriteriaPair_ICCAT.setKey(ConfigSearchField.ICCAT);
		assetListCriteriaPair_ICCAT.setValue(theValue);
		assetListQuery.getAssetSearchCriteria().getCriterias().add(assetListCriteriaPair_ICCAT);

		assetListCriteriaPair_UVI.setKey(ConfigSearchField.UVI);
		assetListCriteriaPair_UVI.setValue(theValue);
		assetListQuery.getAssetSearchCriteria().getCriterias().add(assetListCriteriaPair_UVI);

		assetListCriteriaPair_GFCM.setKey(ConfigSearchField.GFCM);
		assetListCriteriaPair_GFCM.setValue(theValue);
		assetListQuery.getAssetSearchCriteria().getCriterias().add(assetListCriteriaPair_GFCM);

		ListAssetResponse assetList = AssetTestHelper.assetListQuery(assetListQuery);
		List<Asset> assets = assetList.getAsset();
		assertTrue(assets.contains(asset));
	}

	@Test
	public void getAssetListWithLikeSearchValue_CHANGE_KEY_AND_FAIL() throws Exception {
		Asset asset = AssetTestHelper.createTestAsset();
		String guid = asset.getAssetId().getGuid();

		String theValue = UUID.randomUUID().toString();
		String newName = asset.getName() + "Changed";
		asset.setName(newName);
		asset.setIccat(theValue);
		AssetTestHelper.updateAsset(asset);


		AssetListQuery assetListQuery = AssetTestHelper.getBasicAssetQuery();
		AssetListCriteriaPair assetListCriteriaPair_ICCAT = new AssetListCriteriaPair();
		assetListCriteriaPair_ICCAT.setKey(ConfigSearchField.ICCAT);
		assetListCriteriaPair_ICCAT.setValue(theValue);

		AssetListCriteriaPair assetListCriteriaPair_UUID = new AssetListCriteriaPair();
		assetListCriteriaPair_UUID.setKey(ConfigSearchField.GUID);
		assetListCriteriaPair_UUID.setValue(guid);


		assetListQuery.getAssetSearchCriteria().getCriterias().add(assetListCriteriaPair_ICCAT);
		assetListQuery.getAssetSearchCriteria().getCriterias().add(assetListCriteriaPair_UUID);



		ListAssetResponse listAssetResponse = AssetTestHelper.assetListQuery(assetListQuery);
		List<Asset> fetchedAsssets = listAssetResponse.getAsset();
		assertFalse(fetchedAsssets.contains(asset));
	}


	/**
	 * Update asset test.
	 *
	 * @throws Exception
	 *             the exception
	 */
	@Test
	public void updateAssetTest() throws Exception {
		Asset testAsset = AssetTestHelper.createTestAsset();
		String newName = testAsset.getName() + "Changed";
		testAsset.setName(newName);
		Asset updatedAsset = AssetTestHelper.updateAsset(testAsset);
		assertEquals(newName, updatedAsset.getName());
		assertEquals(testAsset.getAssetId().getGuid(), updatedAsset.getAssetId().getGuid());
		assertEquals(testAsset.getCfr(), updatedAsset.getCfr());
		assertNotEquals(testAsset.getEventHistory().getEventId(), updatedAsset.getEventHistory().getEventId());
	}

	@Test
	public void assetListQueryHistoryGuidTest() throws Exception {
		// Create asset versions
		Asset asset1 = AssetTestHelper.createTestAsset();

		Asset asset2 = AssetTestHelper.getAssetByGuid(asset1.getAssetId().getGuid());
		asset2.setName(asset2.getName() + "1");
		asset2 = AssetTestHelper.updateAsset(asset2);

		Asset asset3 = AssetTestHelper.getAssetByGuid(asset2.getAssetId().getGuid());
		asset3.setName(asset3.getName() + "2");
		asset3 = AssetTestHelper.updateAsset(asset3);

		AssetListQuery assetListQuery1 = AssetTestHelper.getBasicAssetQuery();
		AssetListCriteriaPair assetListCriteriaPair = new AssetListCriteriaPair();
		assetListCriteriaPair.setKey(ConfigSearchField.HIST_GUID);
		assetListCriteriaPair.setValue(asset1.getEventHistory().getEventId());
		assetListQuery1.getAssetSearchCriteria().getCriterias().add(assetListCriteriaPair);
		
		ListAssetResponse assetHistory1 = AssetTestHelper.assetListQuery(assetListQuery1);
		List<Asset> assets = assetHistory1.getAsset();
		assertEquals(1, assets.size());
		assertEquals(asset1, assets.get(0));
		
		
		AssetListQuery assetListQuery2 = AssetTestHelper.getBasicAssetQuery();
		AssetListCriteriaPair assetListCriteriaPair2 = new AssetListCriteriaPair();
		assetListCriteriaPair2.setKey(ConfigSearchField.HIST_GUID);
		assetListCriteriaPair2.setValue(asset2.getEventHistory().getEventId());
		assetListQuery2.getAssetSearchCriteria().getCriterias().add(assetListCriteriaPair2);
		
		ListAssetResponse assetHistory2 = AssetTestHelper.assetListQuery(assetListQuery2);
		List<Asset> assets2 = assetHistory2.getAsset();
		assertEquals(1, assets2.size());
		assertEquals(asset2, assets2.get(0));		
		
		
		AssetListQuery assetListQuery3 = AssetTestHelper.getBasicAssetQuery();
		AssetListCriteriaPair assetListCriteriaPair3 = new AssetListCriteriaPair();
		assetListCriteriaPair3.setKey(ConfigSearchField.HIST_GUID);
		assetListCriteriaPair3.setValue(asset3.getEventHistory().getEventId());
		assetListQuery3.getAssetSearchCriteria().getCriterias().add(assetListCriteriaPair3);
		
		ListAssetResponse assetHistory3 = AssetTestHelper.assetListQuery(assetListQuery3);
		List<Asset> assets3 = assetHistory3.getAsset();
		assertEquals(1, assets3.size());
		assertEquals(asset3, assets3.get(0));
	}
	
	@Test
	public void assetListQueryMultipleHistoryGuidTest() throws Exception {
		// Create asset versions
		Asset asset1 = AssetTestHelper.createTestAsset();

		Asset asset2 = AssetTestHelper.getAssetByGuid(asset1.getAssetId().getGuid());
		asset2.setName(asset2.getName() + "1");
		asset2 = AssetTestHelper.updateAsset(asset2);

		AssetListQuery assetListQuery = AssetTestHelper.getBasicAssetQuery();
		AssetListCriteriaPair assetListCriteriaPair = new AssetListCriteriaPair();
		assetListCriteriaPair.setKey(ConfigSearchField.HIST_GUID);
		assetListCriteriaPair.setValue(asset1.getEventHistory().getEventId());
		assetListQuery.getAssetSearchCriteria().getCriterias().add(assetListCriteriaPair);
		AssetListCriteriaPair assetListCriteriaPair2 = new AssetListCriteriaPair();
		assetListCriteriaPair2.setKey(ConfigSearchField.HIST_GUID);
		assetListCriteriaPair2.setValue(asset2.getEventHistory().getEventId());
		assetListQuery.getAssetSearchCriteria().getCriterias().add(assetListCriteriaPair2);
		
		ListAssetResponse assetHistory = AssetTestHelper.assetListQuery(assetListQuery);
		List<Asset> assets = assetHistory.getAsset();
		assertEquals(2, assets.size());
		assertTrue(assets.contains(asset1));		
		assertTrue(assets.contains(asset2));
	}
}
