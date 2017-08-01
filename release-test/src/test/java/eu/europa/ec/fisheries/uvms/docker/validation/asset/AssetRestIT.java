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
import org.apache.http.HttpStatus;
import org.apache.http.client.fluent.Request;
import org.databene.contiperf.PerfTest;
import org.databene.contiperf.Required;
import org.databene.contiperf.junit.ContiPerfRule;
import org.junit.Ignore;
import org.junit.Rule;
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
@PerfTest(threads = 4, duration = 3000, warmUp = 1000)
@Required(max = 5000, average = 3000, percentile95 = 3500, throughput = 2)
public class AssetRestIT extends AbstractRestServiceTest {

	/** The i. */
	@Rule
	public ContiPerfRule contiPerfRule = new ContiPerfRule();

	/**
	 * Gets the asset list test.
	 *
	 * @return the asset list test
	 * @throws Exception the exception
	 */
	@Test
	public void getAssetListTest() throws Exception {
		AssetListQuery assetListQuery = new AssetListQuery();
		AssetListPagination assetListPagination = new AssetListPagination();
		assetListPagination.setListSize(100);
		assetListPagination.setPage(0);
		assetListQuery.setPagination(assetListPagination);
		AssetListCriteria assetListCriteria = new AssetListCriteria();
		AssetListCriteriaPair assetListCriteriaPair = new AssetListCriteriaPair();
		assetListCriteriaPair.setKey(ConfigSearchField.ASSET_TYPE);
		assetListCriteria.getCriterias().add(assetListCriteriaPair);
		assetListQuery.setAssetSearchCriteria(assetListCriteria);
		final HttpResponse response = Request.Post(BASE_URL + "asset/rest/asset/list")
				.setHeader("Content-Type", "application/json").setHeader("Authorization", getValidJwtToken())
				.bodyByteArray(writeValueAsString(assetListQuery).getBytes()).execute().returnResponse();
		assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
		final Map<String, Object> data = getJsonMap(response);
		assertFalse(data.isEmpty());
		assertNotNull(data.get("data"));
	}

	/**
	 * Gets the note activity codes test.
	 *
	 * @return the note activity codes test
	 * @throws Exception the exception
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
	}

	/**
	 * Gets the asset by id test.
	 *
	 * @return the asset by id test
	 * @throws Exception the exception
	 */
	@Test
	@Ignore
	public void getAssetByIdTest() throws Exception {
		final HttpResponse response = Request.Get(BASE_URL + "asset/rest/asset/{id}")
				.setHeader("Content-Type", "application/json").setHeader("Authorization", getValidJwtToken()).execute()
				.returnResponse();
		assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
		final Map<String, Object> data = getJsonMap(response);
		assertFalse(data.isEmpty());
		assertNotNull(data.get("data"));
	}

	/**
	 * Creates the asset test.
	 *
	 * @throws Exception the exception
	 */
	@Test
	@Ignore
	public void createAssetTest() throws Exception {
		final HttpResponse response = Request.Post(BASE_URL + "asset/rest/asset")
				.setHeader("Content-Type", "application/json").setHeader("Authorization", getValidJwtToken())
				.bodyByteArray(writeValueAsString(new Asset()).getBytes()).execute().returnResponse();
		assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
		final Map<String, Object> data = getJsonMap(response);
		assertFalse(data.isEmpty());
		assertNotNull(data.get("data"));
	}

	/**
	 * Update asset test.
	 *
	 * @throws Exception the exception
	 */
	@Test
	@Ignore
	public void updateAssetTest() throws Exception {
		final HttpResponse response = Request.Put(BASE_URL + "asset/rest/asset?comment=comment")
				.setHeader("Content-Type", "application/json").setHeader("Authorization", getValidJwtToken())
				.bodyByteArray(writeValueAsString(new Asset()).getBytes()).execute().returnResponse();
		assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
		final Map<String, Object> data = getJsonMap(response);
		assertFalse(data.isEmpty());
	}

	/**
	 * Archive asset test.
	 *
	 * @throws Exception the exception
	 */
	@Test
	@Ignore
	public void archiveAssetTest() throws Exception {
		final HttpResponse response = Request.Put(BASE_URL + "asset/rest/asset/archive?comment=comment")
				.setHeader("Content-Type", "application/json").setHeader("Authorization", getValidJwtToken())
				.bodyByteArray(writeValueAsString(new Asset()).getBytes()).execute().returnResponse();
		assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
		final Map<String, Object> data = getJsonMap(response);
		assertFalse(data.isEmpty());
	}

	/**
	 * Asset list group by flag state test.
	 *
	 * @throws Exception the exception
	 */
	@Test
	public void assetListGroupByFlagStateTest() throws Exception {
		final HttpResponse response = Request.Post(BASE_URL + "asset/rest/asset/listGroupByFlagState")
				.setHeader("Content-Type", "application/json").setHeader("Authorization", getValidJwtToken())
				.bodyByteArray(writeValueAsString(new ArrayList<String>()).getBytes()).execute().returnResponse();
		assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
		final Map<String, Object> data = getJsonMap(response);
		assertFalse(data.isEmpty());
		assertNotNull(data.get("data"));
	}

}
