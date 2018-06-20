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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import eu.europa.ec.fisheries.uvms.docker.validation.common.AbstractRestServiceTest;
import eu.europa.ec.fisheries.wsdl.asset.types.Asset;
import eu.europa.ec.fisheries.wsdl.asset.types.AssetHistoryId;
import eu.europa.ec.fisheries.wsdl.asset.types.AssetIdType;
import eu.europa.ec.fisheries.wsdl.asset.types.FlagStateType;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

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
	public void getAssetHistoryListByAssetIdNumberOfHistoriesTest() throws Exception {
		Asset asset = AssetTestHelper.createTestAsset();
		List<Asset> assetHistories = AssetTestHelper.getAssetHistoryFromAssetGuid(asset.getAssetId().getGuid());
		assertTrue(assetHistories.size() == 1);
		
		asset.setName(asset.getName() + "Updated");
		asset = AssetTestHelper.updateAsset(asset);
		assetHistories = AssetTestHelper.getAssetHistoryFromAssetGuid(asset.getAssetId().getGuid());
		assertTrue(assetHistories.size() == 2);
		
		asset.setName(asset.getName() + "Updated2");
		asset = AssetTestHelper.updateAsset(asset);
		assetHistories = AssetTestHelper.getAssetHistoryFromAssetGuid(asset.getAssetId().getGuid());
		assertTrue(assetHistories.size() == 3);
	}
	
	@Test
	public void getAssetHistoryListByAssetIdHistoriesIsRetainedTest() throws Exception {
		// Create asset versions
		Asset asset1 = AssetTestHelper.createTestAsset();
		
		Asset asset2 = AssetTestHelper.getAssetByGuid(asset1.getAssetId().getGuid());
		asset2.setName(asset2.getName() + "1");
		asset2 = AssetTestHelper.updateAsset(asset2);
		
		Asset asset3 = AssetTestHelper.getAssetByGuid(asset2.getAssetId().getGuid());
		asset3.setName(asset3.getName() + "2");
		asset3 = AssetTestHelper.updateAsset(asset3);
		
		List<Asset> assetHistories = AssetTestHelper.getAssetHistoryFromAssetGuid(asset3.getAssetId().getGuid());
		assertTrue(assetHistories.size() == 3);
		
		assertTrue(assetHistories.contains(asset1));
		assertTrue(assetHistories.contains(asset2));
		assertTrue(assetHistories.contains(asset3));
	}

	/**
	 * Gets the asset history by asset hist guid test.
	 *
	 * @return the asset history by asset hist guid test
	 * @throws Exception
	 *             the exception
	 */
	@Test
    //@Ignore //FIXME timeout on jenkins
	public void getAssetHistoryByAssetHistGuidTest() throws Exception {
		Asset asset = AssetTestHelper.createTestAsset();		
		Asset assetFromHistory = AssetTestHelper.getAssetHistoryFromHistoryGuid(asset.getEventHistory().getEventId());
		assertEquals(asset, assetFromHistory);
	}
	
	@Test
	public void getAssetHistoryByAssetHistGuidHistortyIsRetained() throws Exception {
		// Create asset versions
		Asset asset1 = AssetTestHelper.createTestAsset();

		Asset asset2 = AssetTestHelper.getAssetByGuid(asset1.getAssetId().getGuid());
		asset2.setName(asset2.getName() + "1");
		asset2 = AssetTestHelper.updateAsset(asset2);

		Asset asset3 = AssetTestHelper.getAssetByGuid(asset2.getAssetId().getGuid());
		asset3.setName(asset3.getName() + "2");
		asset3 = AssetTestHelper.updateAsset(asset3);
		
		Asset assetHistory1 = AssetTestHelper.getAssetHistoryFromHistoryGuid(asset1.getEventHistory().getEventId());
		assertEquals(asset1, assetHistory1);
		
		Asset assetHistory2 = AssetTestHelper.getAssetHistoryFromHistoryGuid(asset2.getEventHistory().getEventId());
		assertEquals(asset2, assetHistory2);
		
		Asset assetHistory3 = AssetTestHelper.getAssetHistoryFromHistoryGuid(asset3.getEventHistory().getEventId());
		assertEquals(asset3, assetHistory3);
	}


	@Test
	public void getAssetFlagStateByAssetIdAndDate() throws Exception {

		try {

			Date eventDate = null;
			AssetHistoryId history = null;

			Date aNorDate = null;

			// Create asset versions
			Asset asset = AssetTestHelper.createTestAsset();
			String assetGuid = asset.getAssetId().getGuid();
			history = asset.getEventHistory();
			eventDate = history.getEventDate();
			Thread.sleep(1000);
			FlagStateType flagState = AssetTestHelper.getFlagStateFromAssetGuidAndDate(assetGuid, eventDate);
			String returnedCode = flagState.getCode();
			Assert.assertEquals("SWE", returnedCode);

			asset.setCountryCode("DNK");
			AssetTestHelper.updateAsset(asset);
			asset = AssetTestHelper.getAssetByGuid(assetGuid);
			history = asset.getEventHistory();
			eventDate = history.getEventDate();
			flagState = AssetTestHelper.getFlagStateFromAssetGuidAndDate(assetGuid, eventDate);
			returnedCode = flagState.getCode();
			Assert.assertEquals("DNK", returnedCode);


			asset.setCountryCode("NOR");
			asset = AssetTestHelper.updateAsset(asset);
			history = asset.getEventHistory();
			eventDate = history.getEventDate();
			aNorDate = history.getEventDate();
			Thread.sleep(1000);
			flagState = AssetTestHelper.getFlagStateFromAssetGuidAndDate(assetGuid, eventDate);
			returnedCode = flagState.getCode();
			Assert.assertEquals("NOR", returnedCode);

			asset.setCountryCode("DNK");
			asset = AssetTestHelper.updateAsset(asset);
			history = asset.getEventHistory();
			eventDate = history.getEventDate();
			Thread.sleep(1000);
			flagState = AssetTestHelper.getFlagStateFromAssetGuidAndDate(assetGuid, eventDate);
			returnedCode = flagState.getCode();
			Assert.assertEquals("DNK", returnedCode);

			asset.setCountryCode("SWE");
			asset = AssetTestHelper.updateAsset(asset);
			history = asset.getEventHistory();
			eventDate = history.getEventDate();
			Thread.sleep(1000);
			flagState = AssetTestHelper.getFlagStateFromAssetGuidAndDate(assetGuid, eventDate);
			returnedCode = flagState.getCode();
			Assert.assertEquals("SWE", returnedCode);

			asset.setCountryCode("DNK");
			asset = AssetTestHelper.updateAsset(asset);
			history = asset.getEventHistory();
			eventDate = history.getEventDate();
			Thread.sleep(1000);
			flagState = AssetTestHelper.getFlagStateFromAssetGuidAndDate(assetGuid, eventDate);
			returnedCode = flagState.getCode();
			Assert.assertEquals("DNK", returnedCode);

			// here we test a saved norwegian date
			flagState = AssetTestHelper.getFlagStateFromAssetGuidAndDate(assetGuid, aNorDate);
			returnedCode = flagState.getCode();
			Assert.assertEquals("NOR", returnedCode);

		} catch(RuntimeException e){
			System.out.println(e.toString());
		}

	}


	@Test
	public void getAssetFromAssetIdAndDate() throws Exception {

		try {

			Date eventDate = null;
			AssetHistoryId history = null;


			// Create asset
			Asset asset = AssetTestHelper.createCfrTestAsset();
			history = asset.getEventHistory();
			eventDate = history.getEventDate();
			Thread.sleep(1000);
			AssetIdType assetIdType = AssetIdType.CFR;
			String value = asset.getCfr();
			String type = assetIdType.value();
			Asset fetchedAsset  = AssetTestHelper.getAssetFromAssetIdAndDate(type, value, eventDate);


			Assert.assertEquals(asset.getCfr(), fetchedAsset.getCfr());

		} catch(RuntimeException e){
			System.out.println(e.toString());
		}

	}






	private String code(Map<String, Object> map){

		LinkedHashMap data = (LinkedHashMap) map.get("data");
		return (String) data.get("code");


	}





}
