package eu.europa.ec.fisheries.uvms.docker.validation.exchange;

import eu.europa.ec.fisheries.schema.exchange.plugin.types.v1.PluginType;
import eu.europa.ec.fisheries.uvms.asset.client.model.AssetDTO;
import eu.europa.ec.fisheries.uvms.docker.validation.asset.AssetTestHelper;
import eu.europa.ec.fisheries.uvms.docker.validation.common.AbstractRest;
import eu.europa.ec.fisheries.uvms.docker.validation.common.MessageHelper;
import eu.europa.ec.fisheries.uvms.exchange.model.mapper.ExchangeModuleRequestMapper;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
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
        String assets = writeValueAsString(Collections.singletonList(createdAsset));

        try (MessageHelper messageHelper = new MessageHelper()) {
            String msg = ExchangeModuleRequestMapper.createReceiveAssetInformation(assets, "Test", PluginType.OTHER, "Test Plugin");
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
        String assets = writeValueAsString(Collections.singletonList(createdAsset));

        try (MessageHelper messageHelper = new MessageHelper()) {
            String msg = ExchangeModuleRequestMapper.createReceiveAssetInformation(assets, "Test", PluginType.OTHER, "Test Plugin");
            messageHelper.sendMessage("UVMSExchangeEvent", msg);
        }

        TimeUnit.SECONDS.sleep(5);

        AssetDTO fetchedAsset = AssetTestHelper.getAssetByGuid(createdAsset.getId());

        assertNotNull(fetchedAsset);
    }
}
