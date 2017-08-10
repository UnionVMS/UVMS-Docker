package eu.europa.ec.fisheries.uvms.docker.validation.asset;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.fluent.Request;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import eu.europa.ec.fisheries.uvms.docker.validation.common.AbstractHelper;
import eu.europa.ec.fisheries.wsdl.asset.types.Asset;
import eu.europa.ec.fisheries.wsdl.asset.types.AssetHistoryId;
import eu.europa.ec.fisheries.wsdl.asset.types.AssetId;
import eu.europa.ec.fisheries.wsdl.asset.types.AssetIdType;
import eu.europa.ec.fisheries.wsdl.asset.types.AssetProdOrgModel;
import eu.europa.ec.fisheries.wsdl.asset.types.CarrierSource;
import eu.europa.ec.fisheries.wsdl.asset.types.EventCode;

public class AssetTestHelper extends AbstractHelper {

	public static Asset createTestAsset() throws IOException, ClientProtocolException, JsonProcessingException,
			JsonParseException, JsonMappingException {

		Asset asset = helper_createAsset(AssetIdType.GUID);
		final HttpResponse response = Request.Post(getBaseUrl() + "asset/rest/asset")
				.setHeader("Content-Type", "application/json").setHeader("Authorization", getValidJwtToken())
				.bodyByteArray(writeValueAsString(asset).getBytes()).execute().returnResponse();
		Map<String, Object> dataMap = checkSuccessResponseReturnMap(response);

		Map<String, Object> assetMap = (Map<String, Object>) dataMap.get("assetId");
		assertNotNull(assetMap);
		String assetGuid = (String) assetMap.get("value");
		assertNotNull(assetGuid);

		Map<String, Object> eventHistoryMap = (Map<String, Object>) dataMap.get("eventHistory");
		assertNotNull(eventHistoryMap);
		String eventId = (String) eventHistoryMap.get("eventId");
		assertNotNull(eventId);
		String eventCode = (String) eventHistoryMap.get("eventCode");
		assertNotNull(eventCode);

		AssetHistoryId assetHistoryId = new AssetHistoryId();
		assetHistoryId.setEventId(eventId);
		assetHistoryId.setEventCode(EventCode.fromValue(eventCode));
		asset.setEventHistory(assetHistoryId);

		asset.setName(asset.getName() + "Changed");
		AssetId assetId = new AssetId();
		assetId.setGuid(assetGuid);
		assetId.setValue(assetGuid);
		assetId.setType(AssetIdType.GUID);
		asset.setAssetId(assetId);
		return asset;
	}

	public static Asset helper_createAsset(AssetIdType assetIdType, String ircs) {

		Asset asset = new Asset();
		AssetId assetId = new AssetId();
		assetId.setType(assetIdType);
		switch (assetIdType) {
		case GUID:
			assetId.setGuid(UUID.randomUUID().toString());
			break;
		case INTERNAL_ID:
			assetId.setValue("INTERNALID_" + UUID.randomUUID().toString());
			break;
		}

		asset.setActive(true);
		asset.setAssetId(assetId);

		asset.setSource(CarrierSource.INTERNAL);
		// asset.setEventHistory();
		asset.setName("Ship" + generateARandomStringWithMaxLength(10));
		asset.setCountryCode("SWE");
		asset.setGearType("DERMERSAL");
		asset.setHasIrcs("1");

		asset.setIrcs(ircs);
		asset.setExternalMarking("EXT3");

		String cfr = "CF" + UUID.randomUUID().toString();

		asset.setCfr("SWE0000" + ircs);

		String imo = "0" + generateARandomStringWithMaxLength(6);
		asset.setImo(imo);
		String mmsi = generateARandomStringWithMaxLength(9);
		asset.setMmsiNo(mmsi);
		asset.setHasLicense(true);
		asset.setLicenseType("MOCK-license-DB");
		asset.setHomePort("TEST_GOT");
		asset.setLengthOverAll(new BigDecimal(15l));
		asset.setLengthBetweenPerpendiculars(new BigDecimal(3l));
		asset.setGrossTonnage(new BigDecimal(200));

		asset.setGrossTonnageUnit("OSLO");
		asset.setSafetyGrossTonnage(new BigDecimal(80));
		asset.setPowerMain(new BigDecimal(10));
		asset.setPowerAux(new BigDecimal(10));

		AssetProdOrgModel assetProdOrgModel = new AssetProdOrgModel();
		assetProdOrgModel.setName("NAME" + generateARandomStringWithMaxLength(10));
		assetProdOrgModel.setCity("CITY" + generateARandomStringWithMaxLength(10));
		assetProdOrgModel.setAddress("ADDRESS" + generateARandomStringWithMaxLength(10));
		assetProdOrgModel.setCode("CODE" + generateARandomStringWithMaxLength(10));
		assetProdOrgModel.setPhone("070" + generateARandomStringWithMaxLength(10));
		asset.getContact();
		asset.getNotes();

		return asset;

	}

	public static Asset helper_createAsset(AssetIdType assetIdType) {
		String ircs = "F" + generateARandomStringWithMaxLength(4);
		return helper_createAsset(assetIdType, ircs);

	}

	public static String generateARandomStringWithMaxLength(int len) {
		String ret = "";
		for (int i = 0; i < len; i++) {
			int val = new Random().nextInt(10);
			ret += String.valueOf(val);
		}
		return ret;
	}

}
