package eu.europa.ec.fisheries.uvms.docker.validation.exchange;

import com.fasterxml.jackson.databind.SerializationFeature;
import eu.europa.ec.fisheries.uvms.asset.client.model.AssetDTO;
import eu.europa.ec.fisheries.uvms.docker.validation.asset.AssetTestHelper;
import eu.europa.ec.fisheries.uvms.docker.validation.common.AbstractRest;
import eu.europa.ec.fisheries.uvms.docker.validation.common.MessageHelper;
import eu.europa.ec.fisheries.uvms.exchange.model.mapper.ExchangeModuleRequestMapper;

import org.junit.Test;

import javax.jms.Queue;
import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class ExchangeJmsIT extends AbstractRest {

    private Queue exchangeQueue;


    @Test
    public void assetInfoTestIT() throws Exception {
        AssetDTO asset = AssetTestHelper.createBasicAsset();
        asset.setName(null);
        AssetDTO createdAsset = AssetTestHelper.createAsset(asset);

        AssetTestHelper.generateARandomStringWithMaxLength(40);
        String newName = AssetTestHelper.generateARandomStringWithMaxLength(40);
        createdAsset.setName(newName);
        OBJECT_MAPPER.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        String assets = OBJECT_MAPPER.writeValueAsString(Arrays.asList(createdAsset));
        try (MessageHelper messageHelper = new MessageHelper()) {
            String msg = ExchangeModuleRequestMapper.createReceiveAssetInformation(assets, "Test");
            messageHelper.sendMessage("UVMSExchangeEvent", msg);
        }

        TimeUnit.SECONDS.sleep(5);

        AssetDTO fetchedAsset = AssetTestHelper.getAssetByGuid(createdAsset.getId());

        assertTrue(fetchedAsset != null);
        assertTrue(fetchedAsset.getName().equals(newName));
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
        String assets = OBJECT_MAPPER.writeValueAsString(Arrays.asList(createdAsset));
        try (MessageHelper messageHelper = new MessageHelper()) {
            String msg = ExchangeModuleRequestMapper.createReceiveAssetInformation(assets, "Test");
            messageHelper.sendMessage("UVMSExchangeEvent", msg);
        }

        TimeUnit.SECONDS.sleep(5);

        AssetDTO fetchedAsset = AssetTestHelper.getAssetByGuid(createdAsset.getId());

        assertTrue(fetchedAsset != null);
    }







}
