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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.fluent.Request;
import org.junit.Test;

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

	/**
	 * Archive asset test.
	 *
	 * @throws Exception
	 *             the exception
	 */
	@Test
	public void archiveAssetTest() throws Exception {

		final HttpResponse response = Request.Put(getBaseUrl() + "asset/rest/asset/archive?comment=Archive")
				.setHeader("Content-Type", "application/json").setHeader("Authorization", getValidJwtToken())
				.bodyByteArray(writeValueAsString(AssetTestHelper.createTestAsset()).getBytes()).execute().returnResponse();
		Map<String, Object> dataMap = checkSuccessResponseReturnMap(response);
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
}
