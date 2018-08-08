package eu.europa.ec.fisheries.uvms.docker.validation.asset;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.fluent.Request;
import org.apache.http.util.EntityUtils;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import eu.europa.ec.fisheries.schema.audit.search.v1.AuditLogListQuery;
import eu.europa.ec.fisheries.schema.audit.search.v1.ListCriteria;
import eu.europa.ec.fisheries.schema.audit.search.v1.SearchKey;
import eu.europa.ec.fisheries.schema.audit.v1.AuditLogType;
import eu.europa.ec.fisheries.uvms.asset.client.model.Asset;
import eu.europa.ec.fisheries.uvms.asset.client.model.AssetGroup;
import eu.europa.ec.fisheries.uvms.asset.client.model.AssetGroupField;
import eu.europa.ec.fisheries.uvms.asset.client.model.AssetListResponse;
import eu.europa.ec.fisheries.uvms.asset.client.model.AssetQuery;
import eu.europa.ec.fisheries.uvms.asset.model.constants.AuditObjectTypeEnum;
import eu.europa.ec.fisheries.uvms.asset.model.constants.AuditOperationEnum;
import eu.europa.ec.fisheries.uvms.commons.date.DateUtils;
import eu.europa.ec.fisheries.uvms.docker.validation.common.AbstractHelper;
import eu.europa.ec.fisheries.uvms.docker.validation.common.AuditHelper;

public class AssetTestHelper extends AbstractHelper {

	// ************************************************
	//  AssetResource
	// ************************************************

	public static Asset createTestAsset() throws IOException, ClientProtocolException, JsonProcessingException,
			JsonParseException, JsonMappingException {
		Asset asset = createBasicAsset();
		final HttpResponse response = Request.Post(getBaseUrl() + "asset/rest/asset")
				.setHeader("Content-Type", "application/json").setHeader("Authorization", getValidJwtToken())
				.bodyByteArray(writeValueAsString(asset).getBytes()).execute().returnResponse();

		return checkSuccessResponseAndReturnType(response, Asset.class);
	}

	public static Asset getAssetByGuid(UUID assetGuid) throws ClientProtocolException, IOException {
		final HttpResponse response = Request.Get(getBaseUrl() + "asset/rest/asset/" + assetGuid)
				.setHeader("Content-Type", "application/json").setHeader("Authorization", getValidJwtToken()).execute()
				.returnResponse();
		return checkSuccessResponseAndReturnType(response, Asset.class);
	}

	public static Asset createAsset(Asset asset) throws ClientProtocolException, JsonProcessingException, IOException {
		final HttpResponse response = Request.Post(getBaseUrl() + "asset/rest/asset")
				.setHeader("Content-Type", "application/json").setHeader("Authorization", getValidJwtToken())
				.bodyByteArray(writeValueAsString(asset).getBytes()).execute().returnResponse();
		return checkSuccessResponseAndReturnType(response, Asset.class);
	}

	public static Asset updateAsset(Asset asset) throws ClientProtocolException, JsonProcessingException, IOException {
		final HttpResponse response = Request.Put(getBaseUrl() + "asset/rest/asset?comment=UpdatedAsset")
				.setHeader("Content-Type", "application/json").setHeader("Authorization", getValidJwtToken())
				.bodyByteArray(writeValueAsString(asset).getBytes()).execute().returnResponse();
		return checkSuccessResponseAndReturnType(response, Asset.class);
	}

	public static Asset archiveAsset(Asset asset) throws ClientProtocolException, JsonProcessingException, IOException {
		final HttpResponse response = Request.Put(getBaseUrl() + "asset/rest/asset/archive?comment=Archive")
				.setHeader("Content-Type", "application/json").setHeader("Authorization", getValidJwtToken())
				.bodyByteArray(writeValueAsString(asset).getBytes()).execute().returnResponse();
		return checkSuccessResponseAndReturnType(response, Asset.class);
	}
	
	public static AssetListResponse assetListQuery(AssetQuery query) throws ClientProtocolException, JsonProcessingException, IOException {
		final HttpResponse response = Request.Post(getBaseUrl() + "asset/rest/asset/list")
				.setHeader("Content-Type", "application/json").setHeader("Authorization", getValidJwtToken())
				.bodyByteArray(writeValueAsString(query).getBytes()).execute().returnResponse();
		return checkSuccessResponseAndReturnType(response, AssetListResponse.class);
	}

	public static Integer assetListQueryCount(AssetQuery query) throws ClientProtocolException, JsonProcessingException, IOException {
		final HttpResponse response = Request.Post(getBaseUrl() + "asset/rest/asset/listcount")
				.setHeader("Content-Type", "application/json").setHeader("Authorization", getValidJwtToken())
				.bodyByteArray(writeValueAsString(query).getBytes()).execute().returnResponse();
		return checkSuccessResponseAndReturnType(response, Integer.class);
	}

	// ************************************************
	//  AssetHistoryResource
	// ************************************************
		
	public static List<Asset> getAssetHistoryFromAssetGuid(UUID assetId) throws ClientProtocolException, IOException {
		final HttpResponse response = Request.Get(getBaseUrl() + "asset/rest/asset/history/asset/" + assetId)
				.setHeader("Content-Type", "application/json").setHeader("Authorization", getValidJwtToken()).execute()
				.returnResponse();
		return checkSuccessResponseAndReturnList(response, Asset.class);
	}
	
	public static Asset getAssetHistoryFromHistoryGuid(UUID historyId) throws ClientProtocolException, IOException {
		final HttpResponse response = Request.Get(getBaseUrl() + "asset/rest/asset/history/" + historyId)
				.setHeader("Content-Type", "application/json").setHeader("Authorization", getValidJwtToken()).execute()
				.returnResponse();
		return checkSuccessResponseAndReturnType(response, Asset.class);
	}

	public static Asset getAssetFromAssetIdAndDate(String type, String value, LocalDateTime date) throws ClientProtocolException, IOException {
		String dateStr = date.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
		final HttpResponse response = Request.Get(getBaseUrl() + "asset/rest/asset/history/" + type + "/" + value + "/" + dateStr)
				.setHeader("Content-Type", "application/json").setHeader("Authorization", getValidJwtToken()).execute()
				.returnResponse();
		return checkSuccessResponseAndReturnType(response, Asset.class);
	}

		// ************************************************
	//  AssetGroupResource
	// ************************************************
		
	public static AssetGroup createAssetGroup(AssetGroup assetGroup) throws ClientProtocolException, JsonProcessingException, IOException {
		final HttpResponse response = Request.Post(getBaseUrl() + "asset/rest/group")
				.setHeader("Content-Type", "application/json").setHeader("Authorization", getValidJwtToken())
				.bodyByteArray(writeValueAsString(assetGroup).getBytes()).execute().returnResponse();
		return checkSuccessResponseAndReturnType(response, AssetGroup.class);
	}
	
	public static AssetGroup updateAssetGroup(AssetGroup assetGroup) throws ClientProtocolException, JsonProcessingException, IOException {
		final HttpResponse response = Request.Put(getBaseUrl() + "asset/rest/group")
				.setHeader("Content-Type", "application/json").setHeader("Authorization", getValidJwtToken())
				.bodyByteArray(writeValueAsString(assetGroup).getBytes()).execute().returnResponse();
		return checkSuccessResponseAndReturnType(response, AssetGroup.class);
	}
	
	public static void deleteAssetGroup(AssetGroup assetGroup) throws ClientProtocolException, IOException {
		final HttpResponse response = Request.Delete(getBaseUrl() + "asset/rest/group/" + assetGroup.getId())
				.setHeader("Content-Type", "application/json").setHeader("Authorization", getValidJwtToken()).execute()
				.returnResponse();
		assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
	}
	
	public static AssetGroup getAssetGroupById(UUID assetGroupId) throws ClientProtocolException, IOException {
		final HttpResponse response = Request.Get(getBaseUrl() + "asset/rest/group/" + assetGroupId)
				.setHeader("Content-Type", "application/json").setHeader("Authorization", getValidJwtToken()).execute()
				.returnResponse();
		return checkSuccessResponseAndReturnType(response, AssetGroup.class);
	}
	
	public static List<AssetGroup> getAssetGroupListByUser(String user) throws ClientProtocolException, IOException {
		final HttpResponse response = Request.Get(getBaseUrl() + "asset/rest/group/list?user=" + user)
				.setHeader("Content-Type", "application/json").setHeader("Authorization", getValidJwtToken()).execute()
				.returnResponse();
		return checkSuccessResponseAndReturnList(response, AssetGroup.class);
	}
	
	public static AssetGroupField createAssetGroupField(UUID assetGroupId, AssetGroupField assetGroupField) throws ClientProtocolException, JsonProcessingException, IOException {
	    final HttpResponse response = Request.Post(getBaseUrl() + "asset/rest/group/" + assetGroupId + "/field")
                .setHeader("Content-Type", "application/json").setHeader("Authorization", getValidJwtToken())
                .bodyByteArray(writeValueAsString(assetGroupField).getBytes()).execute().returnResponse();
        return checkSuccessResponseAndReturnType(response, AssetGroupField.class);
	}
	
	public static List<AssetGroupField> getAssetGroupFieldByAssetGroup(UUID assetGroupId) throws ClientProtocolException, IOException {
        final HttpResponse response = Request.Get(getBaseUrl() + "asset/rest/group/" + assetGroupId + "/fieldsForGroup")
                .setHeader("Content-Type", "application/json").setHeader("Authorization", getValidJwtToken()).execute()
                .returnResponse();
        return checkSuccessResponseAndReturnList(response, AssetGroupField.class);
    }
	
	// ************************************************
	//  Audit logs
	// ************************************************
	
	public static void assertAssetAuditLogCreated(UUID guid, AuditOperationEnum auditOperation, Date fromDate) throws Exception {
		AuditLogListQuery auditLogListQuery = getAssetAuditLogListQuery(AuditObjectTypeEnum.ASSET);
		assertAuditLog(guid, auditOperation, auditLogListQuery, fromDate);
	}
	
	public static void assertAssetGroupAuditLogCreated(UUID guid, AuditOperationEnum auditOperation, Date fromDate) throws Exception {
		AuditLogListQuery auditLogListQuery = getAssetAuditLogListQuery(AuditObjectTypeEnum.ASSET_GROUP);
		assertAuditLog(guid, auditOperation, auditLogListQuery, fromDate);
	}
	
	private static AuditLogListQuery getAssetAuditLogListQuery(AuditObjectTypeEnum auditObjectType) {
		AuditLogListQuery auditLogListQuery = AuditHelper.getBasicAuditLogListQuery();
		ListCriteria typeListCriteria = new ListCriteria();
		typeListCriteria.setKey(SearchKey.TYPE);
		typeListCriteria.setValue(auditObjectType.getValue());
		auditLogListQuery.getAuditSearchCriteria().add(typeListCriteria);
		return auditLogListQuery;
	}
	
	private static void assertAuditLog(UUID guid, AuditOperationEnum auditOperation, AuditLogListQuery auditLogListQuery, Date fromDate) throws Exception {
		ListCriteria typeListCriteria = new ListCriteria();
		typeListCriteria.setKey(SearchKey.OPERATION);
		typeListCriteria.setValue(auditOperation.getValue());
		auditLogListQuery.getAuditSearchCriteria().add(typeListCriteria);

		ListCriteria fromDateListCriteria = new ListCriteria();
		fromDateListCriteria.setKey(SearchKey.FROM_DATE);
		fromDateListCriteria.setValue(DateUtils.parseUTCDateToString(fromDate));
		auditLogListQuery.getAuditSearchCriteria().add(fromDateListCriteria);
		
		List<AuditLogType> auditLogs = AuditHelper.getAuditLogs(auditLogListQuery);
		boolean found = false;
		for (AuditLogType auditLogType : auditLogs) {
			if (UUID.fromString(auditLogType.getAffectedObject()).equals(guid)) {
				found = true;
			}
		}
		assertTrue(found);
	}

	// ************************************************
	//  Misc
	// ************************************************
	
	public static Integer getAssetCountSweden() throws ClientProtocolException, JsonProcessingException, IOException {
	    AssetQuery assetQuery = getBasicAssetQuery();
	    assetQuery.setFlagState(Arrays.asList("SWE"));
		final HttpResponse response = Request.Post(getBaseUrl() + "asset/rest/asset/listcount")
				.setHeader("Content-Type", "application/json").setHeader("Authorization", getValidJwtToken())
				.bodyByteArray(writeValueAsString(assetQuery).getBytes()).execute().returnResponse();
		return checkSuccessResponseReturnInt(response);
	}
	
	public static eu.europa.ec.fisheries.uvms.asset.client.model.Asset createBasicAsset() {
        eu.europa.ec.fisheries.uvms.asset.client.model.Asset asset = new eu.europa.ec.fisheries.uvms.asset.client.model.Asset();

        asset.setActive(true);

        asset.setName("Ship" + generateARandomStringWithMaxLength(10));
        asset.setCfr(UUID.randomUUID().toString().substring(0, 12));
        asset.setFlagStateCode("SWE");
        asset.setIrcsIndicator(true);
        asset.setIrcs("F" + generateARandomStringWithMaxLength(7));
        asset.setExternalMarking("EXT3");
        asset.setImo("0" + generateARandomStringWithMaxLength(6));
        asset.setMmsi(generateARandomStringWithMaxLength(9));

        asset.setSource("INTERNAL");

        asset.setMainFishingGearCode("DERMERSAL");
        asset.setHasLicence(true);
        asset.setLicenceType("MOCK-license-DB");
        asset.setPortOfRegistration("TEST_GOT");
        asset.setLengthOverAll(15.0);
        asset.setLengthBetweenPerpendiculars(3.0);
        asset.setGrossTonnage(200.0);

        asset.setGrossTonnageUnit("OSLO");
        asset.setSafteyGrossTonnage(80.0);
        asset.setPowerOfMainEngine(10.0);
        asset.setPowerOfAuxEngine(10.0);

        return asset;
    }
	
	public static AssetQuery getBasicAssetQuery() {
	    AssetQuery assetListQuery = new AssetQuery();
		return assetListQuery;
	}

	public static String generateARandomStringWithMaxLength(int len) {
		String ret = "";
		for (int i = 0; i < len; i++) {
			int val = new Random().nextInt(10);
			ret += String.valueOf(val);
		}
		return ret;
	}
	
	public static AssetGroup createBasicAssetGroup() {
		AssetGroup assetGroup = new AssetGroup();
		assetGroup.setDynamic(false);
		assetGroup.setGlobal(false);
		assetGroup.setOwner("vms_admin_se");
		assetGroup.setName("Name" + UUID.randomUUID().toString());
		return assetGroup;
	}
}