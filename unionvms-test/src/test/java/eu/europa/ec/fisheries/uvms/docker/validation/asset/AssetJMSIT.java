package eu.europa.ec.fisheries.uvms.docker.validation.asset;

import eu.europa.ec.fisheries.uvms.asset.client.model.AssetDTO;
import eu.europa.ec.fisheries.uvms.asset.client.model.AssetGroup;
import eu.europa.ec.fisheries.uvms.asset.client.model.AssetGroupField;
import eu.europa.ec.fisheries.wsdl.asset.types.AssetIdType;
import eu.europa.ec.fisheries.wsdl.asset.types.AssetListCriteriaPair;
import eu.europa.ec.fisheries.wsdl.asset.types.AssetListQuery;
import eu.europa.ec.fisheries.wsdl.asset.types.ConfigSearchField;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.jms.JMSException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class AssetJMSIT {

	private static AssetJMSHelper assetJMSHelper;

	@BeforeClass
	public static void setup() throws JMSException {
		assetJMSHelper = new AssetJMSHelper();
	}

	@AfterClass
	public static void cleanup() {
		assetJMSHelper.close();
	}

	@Test
	public void testGetAssetByGuid() throws Exception {
		AssetDTO asset = AssetTestHelper.createTestAsset();
		eu.europa.ec.fisheries.wsdl.asset.types.Asset assetById = assetJMSHelper.getAssetById(asset.getId().toString(), AssetIdType.GUID);

		assertEquals(asset.getId().toString(), assetById.getAssetId().getGuid());
		assertEquals(asset.getHistoryId().toString(), assetById.getEventHistory().getEventId());
	}

	@Test
	public void testGetAssetByCFR() throws Exception {
		AssetDTO asset = AssetTestHelper.createBasicAsset();
		asset = AssetTestHelper.createAsset(asset);
		eu.europa.ec.fisheries.wsdl.asset.types.Asset assetById = assetJMSHelper.getAssetById(asset.getCfr(), AssetIdType.CFR);

		assertEquals(asset.getId().toString(), assetById.getAssetId().getGuid());
		assertEquals(asset.getHistoryId().toString(), assetById.getEventHistory().getEventId());
		assertEquals(asset.getCfr(), assetById.getCfr());
	}

	@Test
	public void testGetAssetByIRCS() throws Exception {
		AssetDTO asset = AssetTestHelper.createTestAsset();
		eu.europa.ec.fisheries.wsdl.asset.types.Asset assetById = assetJMSHelper.getAssetById(asset.getIrcs(), AssetIdType.IRCS);

		assertEquals(asset.getId().toString(), assetById.getAssetId().getGuid());
		assertEquals(asset.getHistoryId().toString(), assetById.getEventHistory().getEventId());
		assertEquals(asset.getIrcs(), assetById.getIrcs());
	}

	@Test
	public void testGetAssetByMMSI() throws Exception {
		AssetDTO asset = AssetTestHelper.createTestAsset();
		eu.europa.ec.fisheries.wsdl.asset.types.Asset assetById = assetJMSHelper.getAssetById(asset.getMmsi(), AssetIdType.MMSI);

		assertEquals(asset.getId().toString(), assetById.getAssetId().getGuid());
		assertEquals(asset.getHistoryId().toString(), assetById.getEventHistory().getEventId());
		assertEquals(asset.getMmsi(), assetById.getMmsiNo());
	}

	@Test
	public void testGetAssetListByQuery() throws Exception {
		AssetDTO asset = AssetTestHelper.createTestAsset();

		AssetListQuery assetListQuery = assetJMSHelper.getBasicAssetQuery();
		AssetListCriteriaPair assetListCriteriaPair = new AssetListCriteriaPair();
		assetListCriteriaPair.setKey(ConfigSearchField.FLAG_STATE);
		assetListCriteriaPair.setValue(asset.getFlagStateCode());
		assetListQuery.getAssetSearchCriteria().getCriterias().add(assetListCriteriaPair);

		List<eu.europa.ec.fisheries.wsdl.asset.types.Asset> assets = assetJMSHelper.getAssetByAssetListQuery(assetListQuery);

		assertTrue(assets.stream().anyMatch(a -> a.getAssetId().getGuid().equals(asset.getId().toString())));
	}

	@Test
	public void getAssetGroupListByUserTest() throws Exception {
		AssetGroup assetGroup = AssetTestHelper.createBasicAssetGroup();
		AssetGroup createdAssetGroup = AssetTestHelper.createAssetGroup(assetGroup);

		AssetDTO asset1 = AssetTestHelper.createTestAsset();
		AssetDTO asset2 = AssetTestHelper.createTestAsset();

		AssetGroupField assetGroupField1 = new AssetGroupField();
		assetGroupField1.setField(ConfigSearchField.GUID.toString());
		assetGroupField1.setValue(asset1.getId().toString());
		assetGroupField1.setAssetGroup(createdAssetGroup);
		AssetTestHelper.createAssetGroupField(createdAssetGroup.getId(), assetGroupField1);

		AssetGroupField assetGroupField2 = new AssetGroupField();
		assetGroupField2.setField(ConfigSearchField.GUID.toString());
		assetGroupField2.setValue(asset2.getId().toString());
		assetGroupField2.setAssetGroup(createdAssetGroup);
		AssetTestHelper.createAssetGroupField(createdAssetGroup.getId(), assetGroupField2);


		List<eu.europa.ec.fisheries.wsdl.asset.group.AssetGroup> assetGroups = assetJMSHelper.getAssetGroupByUser(createdAssetGroup.getOwner());

		assertTrue(assetGroups.stream().anyMatch(group -> group.getGuid().equals(createdAssetGroup.getId().toString()) &&
				group.getSearchFields().stream().anyMatch(field -> field.getValue().equals(asset1.getId().toString())) &&
				group.getSearchFields().stream().anyMatch(field -> field.getValue().equals(asset2.getId().toString()))));
	}

	@Test
	public void getAssetGroupByAssetGuidTest() throws Exception {
		AssetGroup assetGroup = AssetTestHelper.createBasicAssetGroup();
		AssetGroup createdAssetGroup = AssetTestHelper.createAssetGroup(assetGroup);

		AssetDTO asset1 = AssetTestHelper.createTestAsset();

		AssetGroupField assetGroupField1 = new AssetGroupField();
		assetGroupField1.setField(ConfigSearchField.GUID.toString());
		assetGroupField1.setValue(asset1.getId().toString());
		assetGroupField1.setAssetGroup(assetGroup);
		AssetTestHelper.createAssetGroupField(createdAssetGroup.getId(), assetGroupField1);

		List<eu.europa.ec.fisheries.wsdl.asset.group.AssetGroup> assetGroups = assetJMSHelper.getAssetGroupListByAssetGuid(asset1.getId().toString());

		assertTrue(assetGroups.stream().anyMatch(group -> group.getGuid().equals(createdAssetGroup.getId().toString()) &&
				group.getSearchFields().stream().anyMatch(field -> field.getValue().equals(asset1.getId().toString()))));
	}

	@Test
	public void getAssetListByAssetGroups() throws Exception {
		AssetGroup assetGroup = AssetTestHelper.createBasicAssetGroup();
		AssetGroup createdAssetGroup = AssetTestHelper.createAssetGroup(assetGroup);

		AssetDTO asset1 = AssetTestHelper.createTestAsset();
		AssetDTO asset2 = AssetTestHelper.createTestAsset();

		AssetGroupField assetGroupField1 = new AssetGroupField();
		assetGroupField1.setField(ConfigSearchField.GUID.toString());
		assetGroupField1.setValue(asset1.getId().toString());
		assetGroupField1.setAssetGroup(createdAssetGroup);
		AssetTestHelper.createAssetGroupField(createdAssetGroup.getId(), assetGroupField1);

		AssetGroupField assetGroupField2 = new AssetGroupField();
		assetGroupField2.setField(ConfigSearchField.GUID.toString());
		assetGroupField2.setValue(asset2.getId().toString());
		assetGroupField2.setAssetGroup(createdAssetGroup);
		AssetTestHelper.createAssetGroupField(createdAssetGroup.getId(), assetGroupField2);

		List<eu.europa.ec.fisheries.wsdl.asset.group.AssetGroup> assetGroups = assetJMSHelper.getAssetGroupListByAssetGuid(asset1.getId().toString());
		assertEquals(1, assetGroups.size());

		List<eu.europa.ec.fisheries.wsdl.asset.group.AssetGroup> assetGroupsSearch = new ArrayList<>();
		assetGroupsSearch.add(assetGroups.get(0));
		List<eu.europa.ec.fisheries.wsdl.asset.types.Asset> assets = assetJMSHelper.getAssetListByAssetGroups(assetGroupsSearch);

		assertTrue(assets.stream().anyMatch(asset -> asset.getAssetId().getGuid().equals(asset1.getId().toString())));
		assertTrue(assets.stream().anyMatch(asset -> asset.getAssetId().getGuid().equals(asset2.getId().toString())));
	}

	@Test
	public void testPingAsset() throws Exception {
		String pingResponse = assetJMSHelper.pingModule();
		assertNotNull(pingResponse);
	}
}
