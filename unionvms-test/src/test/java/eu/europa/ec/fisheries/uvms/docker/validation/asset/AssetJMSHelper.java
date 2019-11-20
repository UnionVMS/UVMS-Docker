package eu.europa.ec.fisheries.uvms.docker.validation.asset;

import eu.europa.ec.fisheries.uvms.asset.model.mapper.AssetModuleRequestMapper;
import eu.europa.ec.fisheries.uvms.asset.model.mapper.JAXBMarshaller;
import eu.europa.ec.fisheries.uvms.docker.validation.common.MessageHelper;
import eu.europa.ec.fisheries.wsdl.asset.group.AssetGroup;
import eu.europa.ec.fisheries.wsdl.asset.group.ListAssetGroupResponse;
import eu.europa.ec.fisheries.wsdl.asset.module.AssetModuleMethod;
import eu.europa.ec.fisheries.wsdl.asset.module.FlagStateTypeResponse;
import eu.europa.ec.fisheries.wsdl.asset.module.GetAssetModuleRequest;
import eu.europa.ec.fisheries.wsdl.asset.module.GetAssetModuleResponse;
import eu.europa.ec.fisheries.wsdl.asset.module.PingResponse;
import eu.europa.ec.fisheries.wsdl.asset.types.Asset;
import eu.europa.ec.fisheries.wsdl.asset.types.AssetId;
import eu.europa.ec.fisheries.wsdl.asset.types.AssetIdType;
import eu.europa.ec.fisheries.wsdl.asset.types.AssetListCriteria;
import eu.europa.ec.fisheries.wsdl.asset.types.AssetListPagination;
import eu.europa.ec.fisheries.wsdl.asset.types.AssetListQuery;
import eu.europa.ec.fisheries.wsdl.asset.types.AssetProdOrgModel;
import eu.europa.ec.fisheries.wsdl.asset.types.CarrierSource;
import eu.europa.ec.fisheries.wsdl.asset.types.FlagStateType;
import eu.europa.ec.fisheries.wsdl.asset.types.ListAssetResponse;

import javax.jms.JMSException;
import javax.jms.TextMessage;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class AssetJMSHelper {

    private static final String ASSET_QUEUE = "UVMSAssetEvent";

    private MessageHelper messageHelper;

    public AssetJMSHelper() throws JMSException {
        messageHelper = new MessageHelper();
    }

    public void close() {
        messageHelper.close();
    }

    public void upsertAsset(Asset asset, String username) throws Exception {
        String upsertAssetModuleRequest = AssetModuleRequestMapper.createUpsertAssetModuleRequest(asset, username);
        messageHelper.sendMessage(ASSET_QUEUE, upsertAssetModuleRequest);
    }

    public Asset getAssetById(String value, AssetIdType type) throws Exception {
        String msg = AssetModuleRequestMapper.createGetAssetModuleRequest(value, type);
        TextMessage response = (TextMessage) messageHelper.getMessageResponse(ASSET_QUEUE, msg);
        GetAssetModuleResponse assetModuleResponse = JAXBMarshaller.unmarshallTextMessage(response, GetAssetModuleResponse.class);
        return assetModuleResponse.getAsset();
    }

    public List<Asset> getAssetByAssetListQuery(AssetListQuery assetListQuery) throws Exception {
        String msg = AssetModuleRequestMapper.createAssetListModuleRequest(assetListQuery);
        TextMessage response = (TextMessage) messageHelper.getMessageResponse(ASSET_QUEUE, msg);
        ListAssetResponse assetModuleResponse = JAXBMarshaller.unmarshallTextMessage(response, ListAssetResponse.class);
        return assetModuleResponse.getAsset();
    }

    public List<AssetGroup> getAssetGroupByUser(String username) throws Exception {
        String msg = AssetModuleRequestMapper.createAssetGroupListByUserModuleRequest(username);
        TextMessage response = (TextMessage) messageHelper.getMessageResponse(ASSET_QUEUE, msg);
        ListAssetGroupResponse assetModuleResponse = JAXBMarshaller.unmarshallTextMessage(response, ListAssetGroupResponse.class);
        return assetModuleResponse.getAssetGroup();
    }

    public List<AssetGroup> getAssetGroupListByAssetGuid(String assetGuid) throws Exception {
        String msg = AssetModuleRequestMapper.createAssetGroupListByAssetGuidRequest(assetGuid);
        TextMessage response = (TextMessage) messageHelper.getMessageResponse(ASSET_QUEUE, msg);
        ListAssetGroupResponse assetModuleResponse = JAXBMarshaller.unmarshallTextMessage(response, ListAssetGroupResponse.class);
        return assetModuleResponse.getAssetGroup();
    }

    public List<Asset> getAssetListByAssetGroups(List<AssetGroup> assetGroups) throws Exception {
        String msg = AssetModuleRequestMapper.createAssetListModuleRequest(assetGroups);
        TextMessage response = (TextMessage) messageHelper.getMessageResponse(ASSET_QUEUE, msg);
        ListAssetResponse assetModuleResponse = JAXBMarshaller.unmarshallTextMessage(response, ListAssetResponse.class);
        return assetModuleResponse.getAsset();
    }

    public String pingModule() throws Exception {
        GetAssetModuleRequest request = new GetAssetModuleRequest();
        request.setMethod(AssetModuleMethod.PING);
        String msg = JAXBMarshaller.marshallJaxBObjectToString(request);
        TextMessage response = (TextMessage) messageHelper.getMessageResponse(ASSET_QUEUE, msg);
        PingResponse assetModuleResponse = JAXBMarshaller.unmarshallTextMessage(response, PingResponse.class);
        return assetModuleResponse.getResponse();
    }

    public void sendStringToAssetWithFunction(String message, String function) throws Exception{
        messageHelper.sendMessageWithFunction(ASSET_QUEUE, message, function);
    }

    public FlagStateType getFlagStateFromAssetGuidAndDate(String guid, Date date) throws Exception {
        String msg = AssetModuleRequestMapper.createFlagStateRequest(guid, date);
        TextMessage response = (TextMessage) messageHelper.getMessageResponse(ASSET_QUEUE, msg);
        FlagStateTypeResponse flagStateTypeResponse = JAXBMarshaller.unmarshallTextMessage(response, FlagStateTypeResponse.class);
        return flagStateTypeResponse.getFlagStateType();
    }

    public Asset createDummyCFRAsset() {
        return createDummyAsset(AssetIdType.CFR);
    }

    public Asset createDummyAsset(AssetIdType assetIdType) {
        String ircs = "F" + generateARandomStringWithMaxLength(7);
        String cfr = UUID.randomUUID().toString().substring(0, 12);

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
            case CFR:
                assetId.setValue(cfr);
                break;
            case IRCS:
                assetId.setValue(ircs);
                break;
            default:
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
        asset.setCfr(cfr);
        asset.setIrcs(ircs);
        asset.setExternalMarking("EXT3");

        String imo = "0" + generateARandomStringWithMaxLength(6);
        asset.setImo(imo);
        String mmsi = generateARandomStringWithMaxLength(9);
        asset.setMmsiNo(mmsi);
        asset.setHasLicense(true);
        asset.setLicenseType("MOCK-license-DB");
        asset.setHomePort("TEST_GOT");
        asset.setLengthOverAll(new BigDecimal(15.0).setScale(1));
        asset.setLengthBetweenPerpendiculars(new BigDecimal(3.0).setScale(1));
        asset.setGrossTonnage(new BigDecimal(200.0).setScale(1));

        asset.setGrossTonnageUnit("OSLO");
        asset.setSafetyGrossTonnage(new BigDecimal(80.0).setScale(1));
        asset.setPowerMain(new BigDecimal(10.0).setScale(1));
        asset.setPowerAux(new BigDecimal(10.0).setScale(1));

        AssetProdOrgModel assetProdOrgModel = new AssetProdOrgModel();
        assetProdOrgModel.setName("NAME" + generateARandomStringWithMaxLength(10));
        assetProdOrgModel.setCity("CITY" + generateARandomStringWithMaxLength(10));
        assetProdOrgModel.setAddress("ADDRESS" + generateARandomStringWithMaxLength(10));
        assetProdOrgModel.setCode("CODE" + generateARandomStringWithMaxLength(10));
        assetProdOrgModel.setPhone("070" + generateARandomStringWithMaxLength(10));
        asset.setProducer(assetProdOrgModel);
        asset.getContact();
        asset.getNotes();

        return asset;
    }

    public eu.europa.ec.fisheries.uvms.asset.client.model.AssetDTO createBasicAsset() {
        eu.europa.ec.fisheries.uvms.asset.client.model.AssetDTO asset = new eu.europa.ec.fisheries.uvms.asset.client.model.AssetDTO();

        asset.setActive(true);

        asset.setName("Ship" + generateARandomStringWithMaxLength(10));
        asset.setCfr(UUID.randomUUID().toString().substring(0, 12));
        asset.setFlagStateCode("SWE");
        asset.setIrcsIndicator(true);
        asset.setIrcs("F" + generateARandomStringWithMaxLength(7));
        asset.setExternalMarking("EXT3");
        asset.setImo("0" + generateARandomStringWithMaxLength(6));
        asset.setMmsi(generateARandomStringWithMaxLength(9));

        asset.setSource("TEST");

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

    public AssetListQuery getBasicAssetQuery() {
        AssetListQuery assetListQuery = new AssetListQuery();
        AssetListPagination assetListPagination = new AssetListPagination();
        assetListPagination.setListSize(1000);
        assetListPagination.setPage(1);
        assetListQuery.setPagination(assetListPagination);
        AssetListCriteria assetListCriteria = new AssetListCriteria();
        assetListCriteria.setIsDynamic(true);
        assetListQuery.setAssetSearchCriteria(assetListCriteria);
        return assetListQuery;
    }

    public String generateARandomStringWithMaxLength(int len) {
        String ret = "";
        for (int i = 0; i < len; i++) {
            int val = new Random().nextInt(10);
            ret += String.valueOf(val);
        }
        return ret;
    }

    public AssetGroup createBasicAssetGroup() {
        AssetGroup assetGroup = new AssetGroup();
        assetGroup.setDynamic(false);
        assetGroup.setGlobal(false);
        assetGroup.setUser("vms_admin_com");
        assetGroup.setName("Name" + UUID.randomUUID().toString());
        return assetGroup;
    }
}
