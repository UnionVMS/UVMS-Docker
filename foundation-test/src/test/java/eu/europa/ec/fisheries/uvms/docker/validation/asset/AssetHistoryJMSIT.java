package eu.europa.ec.fisheries.uvms.docker.validation.asset;

import eu.europa.ec.fisheries.wsdl.asset.types.Asset;
import eu.europa.ec.fisheries.wsdl.asset.types.AssetHistoryId;
import eu.europa.ec.fisheries.wsdl.asset.types.AssetIdType;
import eu.europa.ec.fisheries.wsdl.asset.types.FlagStateType;
import org.junit.Assert;
import org.junit.Test;

import java.util.Date;

public class AssetHistoryJMSIT {


    @Test
    public void getAssetFlagStateByAssetIdAndDate() throws Exception {

        try {

            Date eventDate = null;
            AssetHistoryId history = null;

            Date aNorDate = null;

            // Create asset versions
            Asset asset = AssetTestHelper.createTestAsset();
            String assetGuid = asset.getAssetId().getGuid();
            history = asset.getEventHistory();
            eventDate = history.getEventDate();
            Thread.sleep(1000);
            FlagStateType flagState = AssetJMSHelper.getFlagStateFromAssetGuidAndDate(assetGuid, eventDate);
            String returnedCode = flagState.getCode();
            Assert.assertEquals("SWE", returnedCode);


        } catch (RuntimeException e) {
            System.out.println(e.toString());
        }


    }
}