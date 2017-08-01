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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.fluent.Request;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import eu.europa.ec.fisheries.uvms.docker.validation.common.AbstractRestServiceTest;
import eu.europa.ec.fisheries.wsdl.asset.types.Asset;
import eu.europa.ec.fisheries.wsdl.asset.types.AssetId;
import eu.europa.ec.fisheries.wsdl.asset.types.AssetIdType;
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
		final HttpResponse response = Request.Post(BASE_URL + "asset/rest/asset/list")
				.setHeader("Content-Type", "application/json").setHeader("Authorization", getValidJwtToken())
				.bodyByteArray(writeValueAsString(assetListQuery).getBytes()).execute().returnResponse();
		assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
		final Map<String, Object> data = getJsonMap(response);
		assertFalse(data.isEmpty());
		assertNotNull(data.get("data"));
		assertEquals(200, data.get("code"));
	}

	@Test
	public void getAssetListItemCountTest() throws Exception {
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
		final HttpResponse response = Request.Post(BASE_URL + "asset/rest/asset/listcount")
				.setHeader("Content-Type", "application/json").setHeader("Authorization", getValidJwtToken())
				.bodyByteArray(writeValueAsString(assetListQuery).getBytes()).execute().returnResponse();
		assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
		final Map<String, Object> data = getJsonMap(response);
		assertFalse(data.isEmpty());
		assertNotNull(data.get("data"));
		assertEquals(200, data.get("code"));
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
		final HttpResponse response = Request.Get(BASE_URL + "asset/rest/asset/activitycodes")
				.setHeader("Content-Type", "application/json").setHeader("Authorization", getValidJwtToken()).execute()
				.returnResponse();
		assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
		final Map<String, Object> data = getJsonMap(response);
		assertFalse(data.isEmpty());
		assertNotNull(data.get("data"));
		assertEquals(200, data.get("code"));

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
		Asset asset = createTestAsset();		
		final HttpResponse response = Request.Get(BASE_URL + "asset/rest/asset/" + asset.getAssetId().getGuid())
				.setHeader("Content-Type", "application/json").setHeader("Authorization", getValidJwtToken()).execute()
				.returnResponse();
		assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
		final Map<String, Object> data = getJsonMap(response);
		assertFalse(data.isEmpty());
		assertNotNull(data.get("data"));
		assertEquals(200, data.get("code"));
	}

	/**
	 * Creates the asset test.
	 *
	 * @throws Exception
	 *             the exception
	 */
	@Test
	public void createAssetTest() throws Exception {
		createTestAsset();
	}

	private Asset createTestAsset() throws IOException, ClientProtocolException, JsonProcessingException,
			JsonParseException, JsonMappingException {

		Asset asset = AssetTestHelper.helper_createAsset(AssetIdType.GUID);
		final HttpResponse response = Request.Post(BASE_URL + "asset/rest/asset")
				.setHeader("Content-Type", "application/json").setHeader("Authorization", getValidJwtToken())
				.bodyByteArray(writeValueAsString(asset).getBytes()).execute().returnResponse();
		assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
		final Map<String, Object> data = getJsonMap(response);
		assertFalse(data.isEmpty());
		assertNotNull(data.get("data"));
		assertEquals(200, data.get("code"));

		Map<String, Object> dataMap = (Map<String, Object>) data.get("data");
		assertNotNull(dataMap);

		Map<String, Object> assetMap = (Map<String, Object>) dataMap.get("assetId");
		assertNotNull(assetMap);
		String assetGuid = (String) assetMap.get("value");
		assertNotNull(assetGuid);

		asset.setName(asset.getName() + "Changed");
		AssetId assetId = new AssetId();
		assetId.setGuid(assetGuid);
		assetId.setValue(assetGuid);
		assetId.setType(AssetIdType.GUID);
		asset.setAssetId(assetId);
		return asset;
	}

	/**
	 * Update asset test.
	 *
	 * @throws Exception
	 *             the exception
	 */
	@Test
	public void updateAssetTest() throws Exception {
		final HttpResponse response = Request.Put(BASE_URL + "asset/rest/asset?comment=ChangedName")
				.setHeader("Content-Type", "application/json").setHeader("Authorization", getValidJwtToken())
				.bodyByteArray(writeValueAsString(createTestAsset()).getBytes()).execute().returnResponse();
		assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
		final Map<String, Object> data = getJsonMap(response);
		assertFalse(data.isEmpty());
		assertEquals(200, data.get("code"));
	}

	/**
	 * Archive asset test.
	 *
	 * @throws Exception
	 *             the exception
	 */
	@Test
	public void archiveAssetTest() throws Exception {

		final HttpResponse response = Request.Put(BASE_URL + "asset/rest/asset/archive?comment=Archive")
				.setHeader("Content-Type", "application/json").setHeader("Authorization", getValidJwtToken())
				.bodyByteArray(writeValueAsString(createTestAsset()).getBytes()).execute().returnResponse();
		assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
		final Map<String, Object> data = getJsonMap(response);
		assertFalse(data.isEmpty());

		assertEquals(200, data.get("code"));
	}

	/**
	 * Asset list group by flag state test.
	 *
	 * @throws Exception
	 *             the exception
	 */
	@Test
	public void assetListGroupByFlagStateTest() throws Exception {
		Asset asset = createTestAsset();
		ArrayList<String> assetIdList = new ArrayList<String>();
		assetIdList.add(asset.getAssetId().getGuid());

		final HttpResponse response = Request.Post(BASE_URL + "asset/rest/asset/listGroupByFlagState")
				.setHeader("Content-Type", "application/json").setHeader("Authorization", getValidJwtToken())
				.bodyByteArray(writeValueAsString(assetIdList).getBytes()).execute().returnResponse();
		assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
		final Map<String, Object> data = getJsonMap(response);
		assertFalse(data.isEmpty());
		assertNotNull(data.get("data"));
		assertEquals(200, data.get("code"));
	}

}
