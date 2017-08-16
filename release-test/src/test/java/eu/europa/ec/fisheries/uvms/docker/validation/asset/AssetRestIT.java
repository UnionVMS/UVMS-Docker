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
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.fluent.Request;
import org.junit.Test;

import eu.europa.ec.fisheries.uvms.docker.validation.common.AbstractRestServiceTest;
import eu.europa.ec.fisheries.wsdl.asset.types.Asset;
import eu.europa.ec.fisheries.wsdl.asset.types.AssetListCriteria;
import eu.europa.ec.fisheries.wsdl.asset.types.AssetListCriteriaPair;
import eu.europa.ec.fisheries.wsdl.asset.types.AssetListPagination;
import eu.europa.ec.fisheries.wsdl.asset.types.AssetListQuery;
import eu.europa.ec.fisheries.wsdl.asset.types.ConfigSearchField;

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
		AssetListQuery assetListQuery = new AssetListQuery();
		AssetListPagination assetListPagination = new AssetListPagination();
		assetListPagination.setListSize(100);
		assetListPagination.setPage(1);
		assetListQuery.setPagination(assetListPagination);
		AssetListCriteria assetListCriteria = new AssetListCriteria();
		assetListCriteria.setIsDynamic(true);
		AssetListCriteriaPair assetListCriteriaPair = new AssetListCriteriaPair();
		assetListCriteriaPair.setKey(ConfigSearchField.FLAG_STATE);
		assetListCriteriaPair.setValue("SWE");
		assetListCriteria.getCriterias().add(assetListCriteriaPair);
		assetListQuery.setAssetSearchCriteria(assetListCriteria);
		final HttpResponse response = Request.Post(getBaseUrl() + "asset/rest/asset/list")
				.setHeader("Content-Type", "application/json").setHeader("Authorization", getValidJwtToken())
				.bodyByteArray(writeValueAsString(assetListQuery).getBytes()).execute().returnResponse();
		Map<String, Object> dataMap = checkSuccessResponseReturnMap(response);
	}

	@Test
	public void getAssetListItemCountTest() throws Exception {
		Asset asset = AssetTestHelper.createTestAsset();
		AssetListQuery assetListQuery = new AssetListQuery();
		AssetListPagination assetListPagination = new AssetListPagination();
		assetListPagination.setListSize(100);
		assetListPagination.setPage(1);
		assetListQuery.setPagination(assetListPagination);
		AssetListCriteria assetListCriteria = new AssetListCriteria();
		assetListCriteria.setIsDynamic(true);
		AssetListCriteriaPair assetListCriteriaPair = new AssetListCriteriaPair();
		assetListCriteriaPair.setKey(ConfigSearchField.FLAG_STATE);
		assetListCriteriaPair.setValue("SWE");
		assetListCriteria.getCriterias().add(assetListCriteriaPair);
		assetListQuery.setAssetSearchCriteria(assetListCriteria);
		final HttpResponse response = Request.Post(getBaseUrl() + "asset/rest/asset/listcount")
				.setHeader("Content-Type", "application/json").setHeader("Authorization", getValidJwtToken())
				.bodyByteArray(writeValueAsString(assetListQuery).getBytes()).execute().returnResponse();
		Integer dataValue = checkSuccessResponseReturnInt(response);
		assertTrue(dataValue > 0);
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
		final HttpResponse response = Request.Get(getBaseUrl() + "asset/rest/asset/" + asset.getAssetId().getGuid())
				.setHeader("Content-Type", "application/json").setHeader("Authorization", getValidJwtToken()).execute()
				.returnResponse();
		Map<String, Object> dataMap = checkSuccessResponseReturnMap(response);
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
		testAsset.setName(testAsset.getName() + "Changed");
		final HttpResponse response = Request.Put(getBaseUrl() + "asset/rest/asset?comment=ChangedName")
				.setHeader("Content-Type", "application/json").setHeader("Authorization", getValidJwtToken())
				.bodyByteArray(writeValueAsString(testAsset).getBytes()).execute().returnResponse();
		Map<String, Object> dataMap = checkSuccessResponseReturnMap(response);
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
