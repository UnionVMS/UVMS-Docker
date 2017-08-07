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

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.http.HttpResponse;
import org.apache.http.client.fluent.Request;
import org.junit.Ignore;
import org.junit.Test;

import eu.europa.ec.fisheries.uvms.docker.validation.common.AbstractRestServiceTest;
import eu.europa.ec.fisheries.uvms.rest.security.RequiresFeature;
import eu.europa.ec.fisheries.uvms.rest.security.UnionVMSFeature;
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
		List dataList = checkSuccessResponseReturnType(response,List.class);
	}

	/**
	 * Gets the asset by id test.
	 *
	 * @return the asset by id test
	 * @throws Exception
	 *             the exception
	 */
	@Test
	@Ignore
	public void getAssetByIdTest() throws Exception {
		final HttpResponse response = Request.Get(BASE_URL + "asset/rest/group/{id}")
				.setHeader("Content-Type", "application/json").setHeader("Authorization", getValidJwtToken()).execute()
				.returnResponse();
		Map<String, Object> dataMap = checkSuccessResponseReturnMap(response);
	}


	@Test
	@Ignore
	public void createAssetGroupTest() throws Exception {
		Asset testAsset = createTestAsset();
		
		AssetGroup assetGroup = new AssetGroup();
		assetGroup.setDynamic(true);
		assetGroup.setGlobal(false);
		assetGroup.setName("Name" + new Date().getTime());
		assetGroup.setGuid(UUID.randomUUID().toString());
		
		{
			AssetGroupSearchField assetGroupSearchField = new AssetGroupSearchField();
			assetGroupSearchField.setKey(ConfigSearchField.GUID);
			assetGroupSearchField.setValue(testAsset.getAssetId().getGuid());
			assetGroup.getSearchFields().add(assetGroupSearchField);
		}
		
		final HttpResponse response = Request.Post(BASE_URL + "asset/rest/group")
				.setHeader("Content-Type", "application/json").setHeader("Authorization", getValidJwtToken())
				.bodyByteArray(writeValueAsString(assetGroup).getBytes()).execute().returnResponse();
		Map<String, Object> dataMap = checkSuccessResponseReturnMap(response);
	}

	@Test
	@Ignore
	public void updateAssetGroupTest() throws Exception {
		AssetGroup assetGroup = new AssetGroup();
		final HttpResponse response = Request.Put(BASE_URL + "asset/rest/group")
				.setHeader("Content-Type", "application/json").setHeader("Authorization", getValidJwtToken())
				.bodyByteArray(writeValueAsString(assetGroup).getBytes()).execute().returnResponse();
		Map<String, Object> dataMap = checkSuccessResponseReturnMap(response);
	}

	@Test
	@Ignore
	public void deleteAssetGroupTest() throws Exception {
		AssetGroup assetGroup = new AssetGroup();
		final HttpResponse response = Request.Delete(BASE_URL + "asset/rest/group/{id}")
				.setHeader("Content-Type", "application/json").setHeader("Authorization", getValidJwtToken()).execute().returnResponse();
		Map<String, Object> dataMap = checkSuccessResponseReturnMap(response);
	}


	
}
