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

import eu.europa.ec.fisheries.uvms.asset.client.model.AssetDTO;
import eu.europa.ec.fisheries.uvms.docker.validation.common.AbstractRest;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.List;

public class AssetHistoryRestIT extends AbstractRest {

	@Test
	public void getAssetHistoryListByAssetIdNumberOfHistoriesTest() {
		AssetDTO asset = AssetTestHelper.createTestAsset();
		List<AssetDTO> assetHistories = AssetTestHelper.getAssetHistoryFromAssetGuid(asset.getId());
		assertEquals(1, assetHistories.size());
		
		asset.setName(asset.getName() + "Updated");
		asset = AssetTestHelper.updateAsset(asset);
		assetHistories = AssetTestHelper.getAssetHistoryFromAssetGuid(asset.getId());
		assertEquals(2, assetHistories.size());
		
		asset.setName(asset.getName() + "Updated2");
		asset = AssetTestHelper.updateAsset(asset);
		assetHistories = AssetTestHelper.getAssetHistoryFromAssetGuid(asset.getId());
		assertEquals(3, assetHistories.size());
	}
	
	@Test
	public void getAssetHistoryListByAssetIdHistoriesIsRetainedTest() {
		AssetDTO asset1 = AssetTestHelper.createTestAsset();

		AssetDTO asset2 = AssetTestHelper.getAssetByGuid(asset1.getId());
		asset2.setName(asset2.getName() + "1");
		AssetDTO createdAsset2 = AssetTestHelper.updateAsset(asset2);

		AssetDTO asset3 = AssetTestHelper.getAssetByGuid(asset2.getId());
		asset3.setName(asset3.getName() + "2");
		AssetDTO createdAsset3 = AssetTestHelper.updateAsset(asset3);
		
		List<AssetDTO> assetHistories = AssetTestHelper.getAssetHistoryFromAssetGuid(createdAsset3.getId());
		assertEquals(3, assetHistories.size());
		
		assertTrue(assetHistories.stream().anyMatch(a -> a.getId().equals(asset1.getId()) && a.getName().equals(asset1.getName())));
		assertTrue(assetHistories.stream().anyMatch(a -> a.getId().equals(createdAsset2.getId()) && a.getName().equals(createdAsset2.getName())));
		assertTrue(assetHistories.stream().anyMatch(a -> a.getId().equals(createdAsset3.getId()) && a.getName().equals(createdAsset3.getName())));
	}

	@Test
	public void getAssetHistoryByAssetHistGuidTest() {
		AssetDTO asset = AssetTestHelper.createTestAsset();
		AssetDTO assetFromHistory = AssetTestHelper.getAssetHistoryFromHistoryGuid(asset.getHistoryId());
		assertEquals(asset.getId(), assetFromHistory.getId());
	}
	
	@Test
	public void getAssetHistoryByAssetHistGuidHistoryIsRetained() {
		AssetDTO asset1 = AssetTestHelper.createTestAsset();

		AssetDTO asset2 = AssetTestHelper.getAssetByGuid(asset1.getId());
		asset2.setName(asset2.getName() + "1");
		AssetDTO createdAsset2 = AssetTestHelper.updateAsset(asset2);

		AssetDTO asset3 = AssetTestHelper.getAssetByGuid(asset2.getId());
		asset3.setName(asset3.getName() + "2");
		AssetDTO createdAsset3 = AssetTestHelper.updateAsset(asset3);

		AssetDTO assetHistory1 = AssetTestHelper.getAssetHistoryFromHistoryGuid(asset1.getHistoryId());
		assertEquals(asset1.getId(), assetHistory1.getId());
		assertEquals(asset1.getName(), assetHistory1.getName());

		AssetDTO assetHistory2 = AssetTestHelper.getAssetHistoryFromHistoryGuid(createdAsset2.getHistoryId());
		assertEquals(asset2.getId(), assetHistory2.getId());
		assertEquals(asset2.getName(), assetHistory2.getName());

		AssetDTO assetHistory3 = AssetTestHelper.getAssetHistoryFromHistoryGuid(createdAsset3.getHistoryId());
		assertEquals(asset3.getId(), assetHistory3.getId());
        assertEquals(asset3.getName(), assetHistory3.getName());
	}

    @Test
    public void getAssetFromAssetIdAndDate() {
        // Create asset
		AssetDTO asset = AssetTestHelper.createTestAsset();
        String value = asset.getCfr();
        String type = "cfr";
		AssetDTO fetchedAsset = AssetTestHelper.getAssetFromAssetIdAndDate(type, value, OffsetDateTime.now(ZoneId.of("UTC")));

        assertEquals(asset.getId(), fetchedAsset.getId());
        assertEquals(asset.getCfr(), fetchedAsset.getCfr());
    }
}
