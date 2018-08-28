package eu.europa.ec.fisheries.uvms.docker.validation.asset;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import eu.europa.ec.fisheries.uvms.asset.client.model.Asset;
import eu.europa.ec.fisheries.uvms.asset.client.model.AssetGroup;
import eu.europa.ec.fisheries.uvms.asset.client.model.AssetGroupField;
import eu.europa.ec.fisheries.wsdl.asset.types.AssetIdType;
import eu.europa.ec.fisheries.wsdl.asset.types.AssetListCriteriaPair;
import eu.europa.ec.fisheries.wsdl.asset.types.AssetListQuery;
import eu.europa.ec.fisheries.wsdl.asset.types.ConfigSearchField;

public class AssetJMSIT {

	@Test
	public void testGetAssetByGuid() throws Exception {
		Asset asset = AssetTestHelper.createTestAsset();
		eu.europa.ec.fisheries.wsdl.asset.types.Asset assetById = AssetJMSHelper.getAssetById(asset.getId().toString(), AssetIdType.GUID);
		
		assertEquals(asset.getId().toString(), assetById.getAssetId().getGuid());
		assertEquals(asset.getHistoryId().toString(), assetById.getEventHistory().getEventId());
	}

	@Test
	public void testGetAssetByCFR() throws Exception {
		Asset asset = AssetTestHelper.createBasicAsset();
		asset = AssetTestHelper.createAsset(asset);
		eu.europa.ec.fisheries.wsdl.asset.types.Asset assetById = AssetJMSHelper.getAssetById(asset.getCfr(), AssetIdType.CFR);

		assertEquals(asset.getId().toString(), assetById.getAssetId().getGuid());
        assertEquals(asset.getHistoryId().toString(), assetById.getEventHistory().getEventId());
        assertEquals(asset.getCfr(), assetById.getCfr());
	}
	
	@Test
	public void testGetAssetByIRCS() throws Exception {
		Asset asset = AssetTestHelper.createTestAsset();
		eu.europa.ec.fisheries.wsdl.asset.types.Asset assetById = AssetJMSHelper.getAssetById(asset.getIrcs(), AssetIdType.IRCS);
		
		assertEquals(asset.getId().toString(), assetById.getAssetId().getGuid());
        assertEquals(asset.getHistoryId().toString(), assetById.getEventHistory().getEventId());
        assertEquals(asset.getIrcs(), assetById.getIrcs());
	}
	
	@Test
	public void testGetAssetByMMSI() throws Exception {
		Asset asset = AssetTestHelper.createTestAsset();
		eu.europa.ec.fisheries.wsdl.asset.types.Asset assetById = AssetJMSHelper.getAssetById(asset.getMmsi(), AssetIdType.MMSI);
		
		assertEquals(asset.getId().toString(), assetById.getAssetId().getGuid());
        assertEquals(asset.getHistoryId().toString(), assetById.getEventHistory().getEventId());
        assertEquals(asset.getMmsi(), assetById.getMmsiNo());
	}
	
	@Test
	public void testGetAssetListByQuery() throws Exception {
		Asset asset = AssetTestHelper.createTestAsset();
		
		AssetListQuery assetListQuery = AssetJMSHelper.getBasicAssetQuery();
		AssetListCriteriaPair assetListCriteriaPair = new AssetListCriteriaPair();
		assetListCriteriaPair.setKey(ConfigSearchField.FLAG_STATE);
		assetListCriteriaPair.setValue(asset.getFlagStateCode());
		assetListQuery.getAssetSearchCriteria().getCriterias().add(assetListCriteriaPair);
		
		List<eu.europa.ec.fisheries.wsdl.asset.types.Asset> assets = AssetJMSHelper.getAssetByAssetListQuery(assetListQuery);

		assertTrue(assets.stream().anyMatch(a -> a.getAssetId().getGuid().equals(asset.getId().toString())));
	}
	
	@Test
	public void getAssetGroupListByUserTest() throws Exception {
	    // Create Group
		AssetGroup assetGroup = AssetTestHelper.createBasicAssetGroup();
		AssetGroup createdAssetGroup = AssetTestHelper.createAssetGroup(assetGroup);
		
		Asset asset1 = AssetTestHelper.createTestAsset();
		Asset asset2 = AssetTestHelper.createTestAsset();
		
		// Add assets to group
		AssetGroupField assetGroupField1 = new AssetGroupField();
		assetGroupField1.setField(ConfigSearchField.GUID.toString());
		assetGroupField1.setValue(asset1.getId().toString());
		assetGroupField1.setAssetGroup(createdAssetGroup.getId());
		AssetTestHelper.createAssetGroupField(createdAssetGroup.getId(), assetGroupField1);
	
		AssetGroupField assetGroupField2 = new AssetGroupField();
		assetGroupField2.setField(ConfigSearchField.GUID.toString());
		assetGroupField2.setValue(asset2.getId().toString());
		assetGroupField2.setAssetGroup(createdAssetGroup.getId());
		AssetTestHelper.createAssetGroupField(createdAssetGroup.getId(), assetGroupField2);
	

		List<eu.europa.ec.fisheries.wsdl.asset.group.AssetGroup> assetGroups = AssetJMSHelper.getAssetGroupByUser(createdAssetGroup.getOwner());
		
		assertTrue(assetGroups.stream().anyMatch(group -> group.getGuid().equals(createdAssetGroup.getId().toString()) && 
		        group.getSearchFields().stream().anyMatch(field -> field.getValue().equals(asset1.getId().toString())) &&
		        group.getSearchFields().stream().anyMatch(field -> field.getValue().equals(asset2.getId().toString()))));
	}
	
	@Test
	public void getAssetGroupByAssetGuidTest() throws Exception {
	    // Create Group
		AssetGroup assetGroup = AssetTestHelper.createBasicAssetGroup();
		AssetGroup createdAssetGroup = AssetTestHelper.createAssetGroup(assetGroup);
		
		Asset asset1 = AssetTestHelper.createTestAsset();
		
		// Add asset to group
		AssetGroupField assetGroupField1 = new AssetGroupField();
		assetGroupField1.setField(ConfigSearchField.GUID.toString());
		assetGroupField1.setValue(asset1.getId().toString());
		assetGroupField1.setAssetGroup(assetGroup.getId());
		AssetTestHelper.createAssetGroupField(createdAssetGroup.getId(), assetGroupField1);
	

		List<eu.europa.ec.fisheries.wsdl.asset.group.AssetGroup> assetGroups = AssetJMSHelper.getAssetGroupListByAssetGuid(asset1.getId().toString());
		
		assertTrue(assetGroups.stream().anyMatch(group -> group.getGuid().equals(createdAssetGroup.getId().toString()) && 
                group.getSearchFields().stream().anyMatch(field -> field.getValue().equals(asset1.getId().toString()))));
	}
	
	@Test
	public void getAssetListByAssetGroups() throws Exception {
	    // Create Group
		AssetGroup assetGroup = AssetTestHelper.createBasicAssetGroup();
		AssetGroup createdAssetGroup = AssetTestHelper.createAssetGroup(assetGroup);
		
		Asset asset1 = AssetTestHelper.createTestAsset();
		Asset asset2 = AssetTestHelper.createTestAsset();
		
		// Add assets to group
		AssetGroupField assetGroupField1 = new AssetGroupField();
        assetGroupField1.setField(ConfigSearchField.GUID.toString());
        assetGroupField1.setValue(asset1.getId().toString());
        assetGroupField1.setAssetGroup(createdAssetGroup.getId());
        AssetTestHelper.createAssetGroupField(createdAssetGroup.getId(), assetGroupField1);
    
        AssetGroupField assetGroupField2 = new AssetGroupField();
        assetGroupField2.setField(ConfigSearchField.GUID.toString());
        assetGroupField2.setValue(asset2.getId().toString());
        assetGroupField2.setAssetGroup(createdAssetGroup.getId());
        AssetTestHelper.createAssetGroupField(createdAssetGroup.getId(), assetGroupField2);
	
        List<eu.europa.ec.fisheries.wsdl.asset.group.AssetGroup> assetGroups = AssetJMSHelper.getAssetGroupListByAssetGuid(asset1.getId().toString());
        assertEquals(1, assetGroups.size());
        
		List<eu.europa.ec.fisheries.wsdl.asset.group.AssetGroup> assetGroupsSearch = new ArrayList<>();
		assetGroupsSearch.add(assetGroups.get(0));
		List<eu.europa.ec.fisheries.wsdl.asset.types.Asset> assets = AssetJMSHelper.getAssetListByAssetGroups(assetGroupsSearch);
		
		assertTrue(assets.stream().anyMatch(asset -> asset.getAssetId().getGuid().equals(asset1.getId().toString())));
		assertTrue(assets.stream().anyMatch(asset -> asset.getAssetId().getGuid().equals(asset2.getId().toString())));
	}

	@Test
	public void testPingAsset() throws Exception {
		String pingResponse = AssetJMSHelper.pingModule();
		assertNotNull(pingResponse);
	}
}
