package eu.europa.ec.fisheries.uvms.docker.validation.exchange;

import com.fasterxml.jackson.databind.SerializationFeature;
import eu.europa.ec.fisheries.uvms.asset.client.model.AssetDTO;
import eu.europa.ec.fisheries.uvms.docker.validation.asset.AssetTestHelper;
import eu.europa.ec.fisheries.uvms.docker.validation.common.AbstractRest;
import eu.europa.ec.fisheries.uvms.docker.validation.common.MessageHelper;
import eu.europa.ec.fisheries.uvms.exchange.model.mapper.ExchangeModuleRequestMapper;
import org.junit.Test;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

public class ExchangeJmsIT extends AbstractRest {

    @Test
    public void assetInfoTestIT() throws Exception {
        AssetDTO asset = AssetTestHelper.createBasicAsset();
        asset.setName(null);
        AssetDTO createdAsset = AssetTestHelper.createAsset(asset);

        AssetTestHelper.generateARandomStringWithMaxLength(40);
        String newName = AssetTestHelper.generateARandomStringWithMaxLength(40);
        createdAsset.setName(newName);
        OBJECT_MAPPER.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        String assets = OBJECT_MAPPER.writeValueAsString(Collections.singletonList(createdAsset));

        try (MessageHelper messageHelper = new MessageHelper()) {
            String msg = ExchangeModuleRequestMapper.createReceiveAssetInformation(assets, "Test");
            messageHelper.sendMessage("UVMSExchangeEvent", msg);
        }

        TimeUnit.SECONDS.sleep(5);

        AssetDTO fetchedAsset = AssetTestHelper.getAssetByGuid(createdAsset.getId());

        assertNotNull(fetchedAsset);
        assertEquals(newName, fetchedAsset.getName());
    }


    @Test
    public void assetInfoTest_NULLMMsi_IT() throws Exception {
        AssetDTO asset = AssetTestHelper.createBasicAsset();
        asset.setName(null);
        AssetDTO createdAsset = AssetTestHelper.createAsset(asset);

        AssetTestHelper.generateARandomStringWithMaxLength(40);
        String newName = AssetTestHelper.generateARandomStringWithMaxLength(40);
        createdAsset.setName(newName);
        createdAsset.setMmsi("");
        OBJECT_MAPPER.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        String assets = OBJECT_MAPPER.writeValueAsString(Collections.singletonList(createdAsset));

        try (MessageHelper messageHelper = new MessageHelper()) {
            String msg = ExchangeModuleRequestMapper.createReceiveAssetInformation(assets, "Test");
            messageHelper.sendMessage("UVMSExchangeEvent", msg);
        }

        TimeUnit.SECONDS.sleep(5);

        AssetDTO fetchedAsset = AssetTestHelper.getAssetByGuid(createdAsset.getId());

        assertNotNull(fetchedAsset);
    }
}
