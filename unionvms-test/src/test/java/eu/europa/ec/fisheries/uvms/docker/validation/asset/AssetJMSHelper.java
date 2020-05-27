package eu.europa.ec.fisheries.uvms.docker.validation.asset;

import eu.europa.ec.fisheries.uvms.asset.client.model.AssetBO;
import eu.europa.ec.fisheries.uvms.asset.model.mapper.AssetModuleRequestMapper;
import eu.europa.ec.fisheries.uvms.asset.model.mapper.JAXBMarshaller;
import eu.europa.ec.fisheries.uvms.docker.validation.common.AbstractHelper;
import eu.europa.ec.fisheries.uvms.docker.validation.common.MessageHelper;
import eu.europa.ec.fisheries.wsdl.asset.module.*;
import eu.europa.ec.fisheries.wsdl.asset.types.*;

import javax.jms.JMSException;
import javax.jms.TextMessage;
import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

public class AssetJMSHelper extends AbstractHelper implements AutoCloseable {

    private static final String ASSET_QUEUE = "UVMSAssetEvent";

    private MessageHelper messageHelper;

    public AssetJMSHelper() throws JMSException {
        messageHelper = new MessageHelper();
    }

    @Override
    public void close() {
        messageHelper.close();
    }


    public Asset getAssetById(String value, AssetIdType type) throws Exception {
        String msg = AssetModuleRequestMapper.createGetAssetModuleRequest(value, type);
        TextMessage response = (TextMessage) messageHelper.getMessageResponse(ASSET_QUEUE, msg);
        GetAssetModuleResponse assetModuleResponse = JAXBMarshaller.unmarshallTextMessage(response, GetAssetModuleResponse.class);
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

    public void upsertAssetBO(AssetBO assetBo) throws Exception {
        messageHelper.sendMessageWithMethod(ASSET_QUEUE, OBJECT_MAPPER.writeValueAsString(assetBo), "UPSERT_ASSET");
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
}
