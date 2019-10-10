package eu.europa.ec.fisheries.uvms.docker.validation.asset;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.sse.SseEventSource;

import eu.europa.ec.fisheries.schema.audit.search.v1.AuditLogListQuery;
import eu.europa.ec.fisheries.schema.audit.search.v1.ListCriteria;
import eu.europa.ec.fisheries.schema.audit.search.v1.SearchKey;
import eu.europa.ec.fisheries.schema.audit.v1.AuditLogType;
import eu.europa.ec.fisheries.uvms.asset.client.AssetClient;
import eu.europa.ec.fisheries.uvms.asset.client.model.AssetDTO;
import eu.europa.ec.fisheries.uvms.asset.client.model.AssetGroup;
import eu.europa.ec.fisheries.uvms.asset.client.model.AssetGroupField;
import eu.europa.ec.fisheries.uvms.asset.client.model.AssetListResponse;
import eu.europa.ec.fisheries.uvms.asset.client.model.AssetQuery;
import eu.europa.ec.fisheries.uvms.asset.client.model.ContactInfo;
import eu.europa.ec.fisheries.uvms.asset.client.model.Note;
import eu.europa.ec.fisheries.uvms.asset.model.constants.AuditObjectTypeEnum;
import eu.europa.ec.fisheries.uvms.asset.model.constants.AuditOperationEnum;
import eu.europa.ec.fisheries.uvms.commons.date.DateUtils;
import eu.europa.ec.fisheries.uvms.docker.validation.common.AbstractHelper;
import eu.europa.ec.fisheries.uvms.docker.validation.common.AuditHelper;
import eu.europa.ec.fisheries.uvms.docker.validation.movement.AuthorizationHeaderWebTarget;

public class AssetTestHelper extends AbstractHelper {

	public static AssetDTO createTestAsset() {
		AssetDTO asset = createBasicAsset();
		return createAsset(asset);
	}

	/* AssetResource */

	public static AssetDTO getAssetByGuid(UUID assetGuid) {
		return getWebTarget()
		        .path("asset/rest/asset")
                .path(assetGuid.toString())
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getValidJwtToken())
                .get(AssetDTO.class);
	}

	public static AssetDTO createAsset(AssetDTO asset) {
	    return getWebTarget()
                .path("asset/rest/asset")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getValidJwtToken())
                .post(Entity.json(asset), AssetDTO.class);
	}

	public static AssetDTO createAsset(AssetDTO asset, String user, String pwd) {
		return getWebTarget()
				.path("asset/rest/asset")
				.request(MediaType.APPLICATION_JSON)
				.header(HttpHeaders.AUTHORIZATION, getValidJwtToken(user, pwd))
				.post(Entity.json(asset), AssetDTO.class);
	}

	public static AssetDTO updateAsset(AssetDTO asset) {
		return getWebTarget()
                .path("asset/rest/asset")
                .queryParam("comment", "UpdatedAsset")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getValidJwtToken())
                .put(Entity.json(asset), AssetDTO.class);
	}

	public static AssetDTO archiveAsset(AssetDTO asset) {
		return getWebTarget()
                .path("asset/rest/asset/archive")
                .queryParam("comment", "Archive")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getValidJwtToken())
                .put(Entity.json(asset), AssetDTO.class);
	}
	
	public static AssetListResponse assetListQuery(AssetQuery query) {
		return getWebTarget()
                .path("asset/rest/asset/list")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getValidJwtToken())
                .post(Entity.json(query), AssetListResponse.class);
	}

	public static Integer assetListQueryCount(AssetQuery query) {
		return getWebTarget()
                .path("asset/rest/asset/listcount")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getValidJwtToken())
                .post(Entity.json(query), Integer.class);
	}

	public static List<AssetDTO> getAssetHistoryFromAssetGuid(UUID assetId) {
		return getWebTarget()
                .path("asset/rest/asset/history/asset/")
                .path(assetId.toString())
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getValidJwtToken())
                .get(new GenericType<List<AssetDTO>>() {});
	}
	
	public static AssetDTO getAssetHistoryFromHistoryGuid(UUID historyId) {
		return getWebTarget()
                .path("asset/rest/asset/history")
                .path(historyId.toString())
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getValidJwtToken())
                .get(AssetDTO.class);
	}

	public static AssetDTO getAssetFromAssetIdAndDate(String type, String value, OffsetDateTime date) {
		String dateStr = date.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
		return getWebTarget()
                .path("asset/rest/asset/history")
                .path(type)
                .path(value)
                .path(dateStr)
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getValidJwtToken())
                .get(AssetDTO.class);
	}
	
	public static ContactInfo createContactInfoForAsset(AssetDTO asset, ContactInfo contact) {
        return getWebTarget()
                .path("asset/rest/asset/")
                .path(asset.getId().toString())
                .path("contacts")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getValidJwtToken())
                .post(Entity.json(contact), ContactInfo.class);
	}
	
	public static Note createNoteForAsset(AssetDTO asset, Note note) {
        return getWebTarget()
                .path("asset/rest/asset/")
                .path(asset.getId().toString())
                .path("notes")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getValidJwtToken())
                .post(Entity.json(note), Note.class);
    }

	public static SseEventSource getSseStream() {
		WebTarget target = getWebTarget().path("asset/rest/sse/subscribe");
		AuthorizationHeaderWebTarget jwtTarget = new AuthorizationHeaderWebTarget(target, getValidJwtToken());
		return SseEventSource.
				target(jwtTarget).build();
	}

	// ************************************************
	//  AssetGroupResource
	// ************************************************
		
	public static AssetGroup createAssetGroup(AssetGroup assetGroup) {
		return getWebTarget()
                .path("asset/rest/group")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getValidJwtToken())
                .post(Entity.json(assetGroup), AssetGroup.class);
	}
	
	public static AssetGroup updateAssetGroup(AssetGroup assetGroup) {
	    return getWebTarget()
	            .path("asset/rest/group")
	            .request(MediaType.APPLICATION_JSON)
	            .header(HttpHeaders.AUTHORIZATION, getValidJwtToken())
	            .put(Entity.json(assetGroup), AssetGroup.class);
	}
	
	public static void deleteAssetGroup(AssetGroup assetGroup) {
	    getWebTarget()
	        .path("asset/rest/group")
	        .path(assetGroup.getId().toString())
	        .request(MediaType.APPLICATION_JSON)
	        .header(HttpHeaders.AUTHORIZATION, getValidJwtToken())
	        .delete();
	}
	
	public static AssetGroup getAssetGroupById(UUID assetGroupId) {
		return getWebTarget()
		        .path("asset/rest/group")
		        .path(assetGroupId.toString())
	            .request(MediaType.APPLICATION_JSON)
	            .header(HttpHeaders.AUTHORIZATION, getValidJwtToken())
	            .get(AssetGroup.class);
	}
	
	public static List<AssetGroup> getAssetGroupListByUser(String user) {
        return getWebTarget()
                .path("asset/rest/group/list")
                .queryParam("user", user)
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getValidJwtToken())
                .get(new GenericType<List<AssetGroup>>() {});
	}
	
	public static AssetGroupField createAssetGroupField(UUID assetGroupId, AssetGroupField assetGroupField) {
        return getWebTarget()
                .path("asset/rest/group")
                .path(assetGroupId.toString())
                .path("field")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getValidJwtToken())
                .post(Entity.json(assetGroupField), AssetGroupField.class);
	}
	
	public static List<AssetGroupField> getAssetGroupFieldByAssetGroup(UUID assetGroupId) {
        return getWebTarget()
                .path("asset/rest/group")
                .path(assetGroupId.toString())
                .path("fieldsForGroup")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getValidJwtToken())
                .get(new GenericType<List<AssetGroupField>>() {});
    }

	// ************************************************
	//  InternalResource
	// ************************************************

	public static AssetListResponse getAssetList(AssetQuery query) {
		return getWebTarget()
				.path("asset/rest/internal/query")
				.request(MediaType.APPLICATION_JSON)
				.header(HttpHeaders.AUTHORIZATION, getValidJwtToken())
				.post(Entity.json(query), AssetListResponse.class);
	}

	/*  Audit logs */
	
	public static void assertAssetAuditLogCreated(UUID guid, AuditOperationEnum auditOperation, Date fromDate) throws Exception {
		AuditLogListQuery auditLogListQuery = getAssetAuditLogListQuery(AuditObjectTypeEnum.ASSET);
		TimeUnit.MILLISECONDS.sleep(500);
		assertAuditLog(guid, auditOperation, auditLogListQuery, fromDate);
	}
	
	public static void assertAssetGroupAuditLogCreated(UUID guid, AuditOperationEnum auditOperation, Date fromDate) throws Exception {
		AuditLogListQuery auditLogListQuery = getAssetAuditLogListQuery(AuditObjectTypeEnum.ASSET_GROUP);
		TimeUnit.MILLISECONDS.sleep(500);
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
	
	private static void assertAuditLog(UUID guid, AuditOperationEnum auditOperation, AuditLogListQuery auditLogListQuery, Date fromDate) {
		ListCriteria typeListCriteria = new ListCriteria();
		typeListCriteria.setKey(SearchKey.OPERATION);
		typeListCriteria.setValue(auditOperation.getValue());
		auditLogListQuery.getAuditSearchCriteria().add(typeListCriteria);

		ListCriteria fromDateListCriteria = new ListCriteria();
		fromDateListCriteria.setKey(SearchKey.FROM_DATE);
		fromDateListCriteria.setValue(DateUtils.parseUTCDateToString(fromDate.toInstant()));
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

	/*  Misc  */
	
	public static Integer getAssetCountSweden() {
	    AssetQuery assetQuery = getBasicAssetQuery();
	    assetQuery.setFlagState(Arrays.asList("SWE"));
	    return assetListQueryCount(assetQuery);
	}
	
	public static AssetDTO createBasicAsset() {
        AssetDTO asset = new AssetDTO();

        asset.setActive(true);

        asset.setName("Ship" + generateARandomStringWithMaxLength(10));
        asset.setCfr("CFR" + generateARandomStringWithMaxLength(9));
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
