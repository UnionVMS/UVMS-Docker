package eu.europa.ec.fisheries.uvms.docker.validation.asset;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import eu.europa.ec.fisheries.wsdl.asset.group.AssetGroup;
import eu.europa.ec.fisheries.wsdl.asset.group.AssetGroupSearchField;
import eu.europa.ec.fisheries.wsdl.asset.types.Asset;
import eu.europa.ec.fisheries.wsdl.asset.types.AssetIdType;
import eu.europa.ec.fisheries.wsdl.asset.types.AssetListCriteriaPair;
import eu.europa.ec.fisheries.wsdl.asset.types.AssetListQuery;
import eu.europa.ec.fisheries.wsdl.asset.types.ConfigSearchField;

public class AssetJMSIT {

	@Test
	public void testGetAssetByGuid() throws Exception {
		Asset asset = AssetTestHelper.createTestAsset();
		Asset assetById = AssetJMSHelper.getAssetById(asset.getAssetId().getGuid(), AssetIdType.GUID);
		setDecimalScaleAndNullNotes(assetById);
		assertEquals(asset, assetById);
	}
	
	@Test
	public void testGetAssetByCFR() throws Exception {
		Asset asset = AssetTestHelper.createTestAsset();
		Asset assetById = AssetJMSHelper.getAssetById(asset.getCfr(), AssetIdType.CFR);
		setDecimalScaleAndNullNotes(assetById);
		assertEquals(asset, assetById);
	}
	
	@Test
	public void testGetAssetByIRCS() throws Exception {
		Asset asset = AssetTestHelper.createTestAsset();
		Asset assetById = AssetJMSHelper.getAssetById(asset.getIrcs(), AssetIdType.IRCS);
		setDecimalScaleAndNullNotes(assetById);
		assertEquals(asset, assetById);
	}
	
	@Test
	public void testGetAssetByMMSI() throws Exception {
		Asset asset = AssetTestHelper.createTestAsset();
		Asset assetById = AssetJMSHelper.getAssetById(asset.getMmsiNo(), AssetIdType.MMSI);
		setDecimalScaleAndNullNotes(assetById);
		assertEquals(asset, assetById);
	}
	
	@Test
	public void testGetAssetListByQuery() throws Exception {
		Asset asset = AssetTestHelper.createTestAsset();
		
		AssetListQuery assetListQuery = AssetTestHelper.getBasicAssetQuery();
		AssetListCriteriaPair assetListCriteriaPair = new AssetListCriteriaPair();
		assetListCriteriaPair.setKey(ConfigSearchField.FLAG_STATE);
		assetListCriteriaPair.setValue(asset.getCountryCode());
		assetListQuery.getAssetSearchCriteria().getCriterias().add(assetListCriteriaPair);
		
		List<Asset> assets = AssetJMSHelper.getAssetByAssetListQuery(assetListQuery);
		setDecimalScaleAndNullNotes(assets);
		assertTrue(assets.contains(asset));
	}
	
	@Test
	public void getAssetGroupListByUserTest() throws Exception {
		AssetGroup assetGroup = AssetTestHelper.createBasicAssetGroup();
		
		Asset asset1 = AssetTestHelper.createTestAsset();
		Asset asset2 = AssetTestHelper.createTestAsset();
		
		// Add assets to group
		AssetGroupSearchField assetGroupSearchField1 = new AssetGroupSearchField();
		assetGroupSearchField1.setKey(ConfigSearchField.GUID);
		assetGroupSearchField1.setValue(asset1.getAssetId().getGuid());
		assetGroup.getSearchFields().add(assetGroupSearchField1);
	
		AssetGroupSearchField assetGroupSearchField2 = new AssetGroupSearchField();
		assetGroupSearchField2.setKey(ConfigSearchField.GUID);
		assetGroupSearchField2.setValue(asset2.getAssetId().getGuid());
		assetGroup.getSearchFields().add(assetGroupSearchField2);
	
		// Create Group
		assetGroup = AssetTestHelper.createAssetGroup(assetGroup);

		List<AssetGroup> assetGroups = AssetJMSHelper.getAssetGroupByUser(assetGroup.getUser());
		
		assertTrue(assetGroups.contains(assetGroup));
	}
	
	@Test
	public void getAssetGroupByAssetGuidTest() throws Exception {
		AssetGroup assetGroup = AssetTestHelper.createBasicAssetGroup();
		
		Asset asset1 = AssetTestHelper.createTestAsset();
		
		// Add asset to group
		AssetGroupSearchField assetGroupSearchField1 = new AssetGroupSearchField();
		assetGroupSearchField1.setKey(ConfigSearchField.GUID);
		assetGroupSearchField1.setValue(asset1.getAssetId().getGuid());
		assetGroup.getSearchFields().add(assetGroupSearchField1);
	
		// Create Group
		assetGroup = AssetTestHelper.createAssetGroup(assetGroup);

		List<AssetGroup> assetGroups = AssetJMSHelper.getAssetGroupListByAssetGuid(asset1.getAssetId().getGuid());
		
		assertTrue(assetGroups.contains(assetGroup));
	}
	
	@Test
	public void getAssetListByAssetGroups() throws Exception {
		AssetGroup assetGroup = AssetTestHelper.createBasicAssetGroup();
		
		Asset asset1 = AssetTestHelper.createTestAsset();
		Asset asset2 = AssetTestHelper.createTestAsset();
		
		// Add assets to group
		AssetGroupSearchField assetGroupSearchField1 = new AssetGroupSearchField();
		assetGroupSearchField1.setKey(ConfigSearchField.GUID);
		assetGroupSearchField1.setValue(asset1.getAssetId().getGuid());
		assetGroup.getSearchFields().add(assetGroupSearchField1);
	
		AssetGroupSearchField assetGroupSearchField2 = new AssetGroupSearchField();
		assetGroupSearchField2.setKey(ConfigSearchField.GUID);
		assetGroupSearchField2.setValue(asset2.getAssetId().getGuid());
		assetGroup.getSearchFields().add(assetGroupSearchField2);
	
		// Create Group
		assetGroup = AssetTestHelper.createAssetGroup(assetGroup);

		List<AssetGroup> assetGroups = new ArrayList<AssetGroup>();
		assetGroups.add(assetGroup);
		List<Asset> assets = AssetJMSHelper.getAssetListByAssetGroups(assetGroups);
		
		setDecimalScaleAndNullNotes(assets);
		assertTrue(assets.contains(asset1));
		assertTrue(assets.contains(asset2));
	}

	@Test
	public void testPingAsset() throws Exception {
		String pingResponse = AssetJMSHelper.pingModule();
		assertNotNull(pingResponse);
	}
	
	
	/**
	 *  Adjust the values returned for easier comparison
	 */
	private void setDecimalScaleAndNullNotes(List<Asset> assets) {
		for (Asset asset : assets) {
			setDecimalScaleAndNullNotes(asset);
		}
	}
	
	private void setDecimalScaleAndNullNotes(Asset asset) {
		asset.setLengthOverAll(asset.getLengthOverAll().setScale(1));
		asset.setLengthBetweenPerpendiculars(asset.getLengthBetweenPerpendiculars().setScale(1));
		asset.setGrossTonnage(asset.getGrossTonnage().setScale(1));
		asset.setSafetyGrossTonnage(asset.getSafetyGrossTonnage().setScale(1));
		asset.setPowerMain(asset.getPowerMain().setScale(1));
		asset.setPowerAux(asset.getPowerAux().setScale(1));
		asset.getNotes();
	}
}
