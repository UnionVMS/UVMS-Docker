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

import org.apache.http.HttpResponse;
import org.apache.http.client.fluent.Request;
import org.junit.Test;

import eu.europa.ec.fisheries.uvms.docker.validation.common.AbstractRestServiceTest;
import eu.europa.ec.fisheries.wsdl.asset.types.Asset;

/**
 * The Class AssetHistoryRestIT.
 */

public class AssetHistoryRestIT extends AbstractRestServiceTest {

	/**
	 * Gets the asset history list by asset id test.
	 *
	 * @return the asset history list by asset id test
	 * @throws Exception
	 *             the exception
	 */
	@Test
	public void getAssetHistoryListByAssetIdTest() throws Exception {
		Asset asset = AssetTestHelper.createTestAsset();
		final HttpResponse response = Request.Get(getBaseUrl() + "asset/rest/history/asset?assetId=" + asset.getAssetId().getGuid() + "&maxNbr=100")
				.setHeader("Content-Type", "application/json").setHeader("Authorization", getValidJwtToken()).execute()
				.returnResponse();
		List dataList = checkSuccessResponseReturnType(response,List.class);
	}

	/**
	 * Gets the asset history by asset hist guid test.
	 *
	 * @return the asset history by asset hist guid test
	 * @throws Exception
	 *             the exception
	 */
	@Test
	public void getAssetHistoryByAssetHistGuidTest() throws Exception {
		Asset asset = AssetTestHelper.createTestAsset();		
		final HttpResponse response = Request.Get(getBaseUrl() + "asset/rest/history/" + asset.getEventHistory().getEventId())
				.setHeader("Content-Type", "application/json").setHeader("Authorization", getValidJwtToken()).execute()
				.returnResponse();
		Map<String, Object> dataMap = checkSuccessResponseReturnMap(response);
	}

}
