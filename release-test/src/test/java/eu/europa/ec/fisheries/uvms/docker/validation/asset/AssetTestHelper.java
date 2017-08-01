package eu.europa.ec.fisheries.uvms.docker.validation.asset;

import java.math.BigDecimal;
import java.util.Random;
import java.util.UUID;

import eu.europa.ec.fisheries.wsdl.asset.types.Asset;
import eu.europa.ec.fisheries.wsdl.asset.types.AssetId;
import eu.europa.ec.fisheries.wsdl.asset.types.AssetIdType;
import eu.europa.ec.fisheries.wsdl.asset.types.AssetProdOrgModel;
import eu.europa.ec.fisheries.wsdl.asset.types.CarrierSource;

public class AssetTestHelper {

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
        //asset.setEventHistory();
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

    private static String generateARandomStringWithMaxLength(int len) {
        String ret = "";
        for (int i = 0; i < len; i++) {
            int val = new Random().nextInt(10);
            ret += String.valueOf(val);
        }
        return ret;
    }

}
