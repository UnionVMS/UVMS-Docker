/*
﻿Developed with the contribution of the European Commission - Directorate General for Maritime Affairs and Fisheries
© European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can
redistribute it and/or modify it under the terms of the GNU General Public License as published by the
Free Software Foundation, either version 3 of the License, or any later version. The IFDM Suite is distributed in
the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. You should have received a
copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.
 */
package eu.europa.ec.fisheries.uvms.docker.validation.system.helper;

import eu.europa.ec.fisheries.schema.exchange.module.v1.GetServiceListResponse;
import eu.europa.ec.fisheries.schema.exchange.plugin.types.v1.PluginType;
import eu.europa.ec.fisheries.schema.exchange.plugin.v1.SetCommandRequest;
import eu.europa.ec.fisheries.schema.exchange.plugin.v1.SetReportRequest;
import eu.europa.ec.fisheries.schema.exchange.service.v1.CapabilityListType;
import eu.europa.ec.fisheries.schema.exchange.service.v1.ServiceResponseType;
import eu.europa.ec.fisheries.schema.exchange.service.v1.ServiceType;
import eu.europa.ec.fisheries.schema.exchange.service.v1.SettingListType;
import eu.europa.ec.fisheries.schema.movementrules.customrule.v1.*;
import eu.europa.ec.fisheries.uvms.asset.client.model.AssetDTO;
import eu.europa.ec.fisheries.uvms.commons.message.api.MessageConstants;
import eu.europa.ec.fisheries.uvms.docker.validation.asset.AssetTestHelper;
import eu.europa.ec.fisheries.uvms.docker.validation.common.MessageHelper;
import eu.europa.ec.fisheries.uvms.docker.validation.common.TopicListener;
import eu.europa.ec.fisheries.uvms.docker.validation.movement.LatLong;
import eu.europa.ec.fisheries.uvms.exchange.model.constant.ExchangeModelConstants;
import eu.europa.ec.fisheries.uvms.exchange.model.mapper.ExchangeModuleRequestMapper;
import eu.europa.ec.fisheries.uvms.exchange.model.mapper.JAXBMarshaller;

import javax.jms.Message;
import javax.jms.TextMessage;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertNotNull;

public class VMSSystemHelper {

    private static final String SERVICE_NAME = "eu.europa.ec.fisheries.uvms.docker.validation.system.rules.EMAIL";
    private static final long TIMEOUT = 10000;

    public static final String FLUX_SELECTOR = "ServiceName='eu.europa.ec.fisheries.uvms.plugins.flux.movement'";
    public static final String NAF_SELECTOR = "ServiceName='eu.europa.ec.fisheries.uvms.plugins.naf'";
    public static final String INMARSAT_SELECTOR = "ServiceName='eu.europa.ec.fisheries.uvms.plugins.inmarsat'";
    public static String emailSelector = "ServiceName='" + SERVICE_NAME + "'";
    public static String emailPluginName = "TEST EMAIL";
    
    public static SetReportRequest triggerBasicRuleAndSendToFlux(String fluxEndpoint) throws Exception {
        return triggerBasicRuleWithAction(ActionType.SEND_TO_FLUX, fluxEndpoint, SetReportRequest.class, FLUX_SELECTOR);
    }

    public static SetReportRequest triggerBasicRuleAndSendToNAF(String nation) throws Exception {
        return triggerBasicRuleWithAction(ActionType.SEND_TO_NAF, nation, SetReportRequest.class, NAF_SELECTOR);
    }

    public static SetCommandRequest triggerBasicRuleAndSendEmail(String email) throws Exception {
        return triggerBasicRuleWithAction(ActionType.EMAIL, email, SetCommandRequest.class, emailSelector);
    }
    
    private static <T> T triggerBasicRuleWithAction(ActionType actionType, String actionValue, Class<T> expectedType, String selector) throws Exception {
        try {
            OffsetDateTime timestamp = OffsetDateTime.now(ZoneOffset.UTC);
            AssetDTO asset = AssetTestHelper.createTestAsset();
    
            CustomRuleType flagStateRule = CustomRuleBuilder.getBuilder()
                    .setName("Flag state => FLUX DNK")
                    .rule(CriteriaType.ASSET, SubCriteriaType.FLAG_STATE, 
                            ConditionType.EQ, asset.getFlagStateCode())
                    .action(actionType, actionValue)
                    .build();
            
            CustomRuleType createdCustomRule = CustomRuleHelper.createCustomRule(flagStateRule);
            assertNotNull(createdCustomRule);
    
            LatLong position = new LatLong(11d, 56d, new Date());

            T reportRequest;
            try (TopicListener topicListener = new TopicListener(selector)) {
                FLUXHelper.sendPositionToFluxPlugin(asset, position);
                CustomRuleHelper.pollTicketCreated();
                reportRequest = topicListener.listenOnEventBusForSpecificMessage(expectedType);
            }
            CustomRuleHelper.assertRuleTriggered(createdCustomRule, timestamp);
            return reportRequest;
        } finally {
            CustomRuleHelper.removeCustomRulesByDefaultUser();
        }
    }
    
    public static void registerEmailPluginIfNotExisting() throws Exception {
        try (MessageHelper messageHelper = new MessageHelper()) {
            String exchangeRequest = ExchangeModuleRequestMapper.createGetServiceListRequest(Collections.singletonList(PluginType.EMAIL));
            Message exchangeResponse = messageHelper.getMessageResponse(MessageConstants.QUEUE_EXCHANGE_EVENT_NAME, exchangeRequest);
            GetServiceListResponse emailServices = JAXBMarshaller.unmarshallTextMessage((TextMessage) exchangeResponse, GetServiceListResponse.class);
            List<ServiceResponseType> service = emailServices.getService();
            if (!service.isEmpty()) {
                emailSelector = "ServiceName='" + service.get(0).getServiceClassName() + "'";
            } else {
                ServiceType serviceType = new ServiceType();
                serviceType.setName(emailPluginName);
                serviceType.setPluginType(PluginType.EMAIL);
                serviceType.setServiceClassName(SERVICE_NAME);
                serviceType.setServiceResponseMessageName(SERVICE_NAME);
                String registerRequest = ExchangeModuleRequestMapper.createRegisterServiceRequest(serviceType, new CapabilityListType(), new SettingListType());
                messageHelper.sendToEventBus(registerRequest, ExchangeModelConstants.EXCHANGE_REGISTER_SERVICE);
                
                // Clear topic
                messageHelper.listenOnEventBus(emailSelector, TIMEOUT);
                messageHelper.listenOnEventBus(emailSelector, TIMEOUT);
            }
        }
    }
}
