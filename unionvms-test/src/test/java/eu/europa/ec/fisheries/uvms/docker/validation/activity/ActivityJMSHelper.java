package eu.europa.ec.fisheries.uvms.docker.validation.activity;

import java.time.Instant;
import java.util.UUID;
import eu.europa.ec.fisheries.schema.exchange.module.v1.ExchangeModuleMethod;
import eu.europa.ec.fisheries.schema.exchange.plugin.types.v1.PluginType;
import eu.europa.ec.fisheries.uvms.activity.model.mapper.ActivityModuleRequestMapper;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.MessageType;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.SyncAsyncRequestType;
import eu.europa.ec.fisheries.uvms.docker.validation.common.AbstractHelper;
import eu.europa.ec.fisheries.uvms.docker.validation.common.MessageHelper;
import eu.europa.ec.fisheries.uvms.docker.validation.common.TopicListener;
import eu.europa.ec.fisheries.uvms.exchange.model.mapper.ExchangeModuleRequestMapper;
import eu.europa.ec.fisheries.uvms.exchange.model.mapper.JAXBMarshaller;
import un.unece.uncefact.data.standard.fluxfareportmessage._3.FLUXFAReportMessage;

public class ActivityJMSHelper extends AbstractHelper {

    private static final String ACTIVITY_QUEUE = "UVMSActivityEvent";
    private static final String EXCHANGE_QUEUE = "UVMSExchangeEvent";

    private ActivityJMSHelper() {}

    public static void sendToActivity(FLUXFAReportMessage message) throws Exception {
        String activityRequest = ActivityModuleRequestMapper.mapToSetFLUXFAReportOrQueryMessageRequest(JAXBMarshaller.marshallJaxBObjectToString(message), PluginType.FLUX.name(), MessageType.FLUX_FA_REPORT_MESSAGE, SyncAsyncRequestType.ASYNC, UUID.randomUUID().toString());
        try (MessageHelper messageHelper = new MessageHelper();
                TopicListener topicListener = new TopicListener(TopicListener.EVENT_STREAM, "event = 'Activity'")) {
            messageHelper.sendMessage(ACTIVITY_QUEUE, activityRequest);
            topicListener.listenOnEventBus();
        }
    }

    public static void sendToExchange(FLUXFAReportMessage message) throws Exception {
        String exchangeRequest = ExchangeModuleRequestMapper.createFluxFAReportRequest(JAXBMarshaller.marshallJaxBObjectToString(message), "activity-plugin", "FLUXFAReportMessage", Instant.now(), message.getFLUXReportDocument().getIDS().get(0).getValue(), PluginType.OTHER, "SWE", null, null, null, null);
        try (MessageHelper messageHelper = new MessageHelper();
                TopicListener topicListener = new TopicListener(TopicListener.EVENT_STREAM, "event = 'Activity'")) {
            messageHelper.sendMessageWithFunction(EXCHANGE_QUEUE, exchangeRequest, ExchangeModuleMethod.SET_FLUX_FA_REPORT_MESSAGE.toString());
            topicListener.listenOnEventBus();
        }
    }
}
