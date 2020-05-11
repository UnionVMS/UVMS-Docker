package eu.europa.ec.fisheries.uvms.docker.validation.asset;

import eu.europa.ec.fisheries.uvms.asset.client.model.AssetDTO;
import eu.europa.ec.fisheries.wsdl.asset.types.AssetIdType;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.jms.JMSException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

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
	public void testPingAsset() throws Exception {
		String pingResponse = assetJMSHelper.pingModule();
		assertNotNull(pingResponse);
	}
}
