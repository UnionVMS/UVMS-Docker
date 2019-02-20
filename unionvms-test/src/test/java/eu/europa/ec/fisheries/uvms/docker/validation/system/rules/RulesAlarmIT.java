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
package eu.europa.ec.fisheries.uvms.docker.validation.system.rules;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;

import eu.europa.ec.fisheries.schema.exchange.common.v1.CommandTypeType;
import eu.europa.ec.fisheries.schema.exchange.common.v1.KeyValueType;
import eu.europa.ec.fisheries.schema.mobileterminal.polltypes.v1.PollType;
import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.ComChannelType;
import eu.europa.ec.fisheries.schema.movement.v1.MovementComChannelType;
import eu.europa.ec.fisheries.schema.movement.v1.MovementSourceType;
import eu.europa.ec.fisheries.uvms.docker.validation.common.TopicListener;
import eu.europa.ec.fisheries.uvms.docker.validation.mobileterminal.MobileTerminalTestHelper;
import eu.europa.ec.fisheries.uvms.docker.validation.mobileterminal.dto.ChannelDto;
import eu.europa.ec.fisheries.uvms.docker.validation.mobileterminal.dto.MobileTerminalDto;
import eu.europa.ec.fisheries.uvms.docker.validation.movement.MovementDto;
import eu.europa.ec.fisheries.uvms.docker.validation.movement.model.IncomingMovement;
import eu.europa.ec.fisheries.uvms.docker.validation.spatial.SpatialHelper;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import eu.europa.ec.fisheries.schema.exchange.module.v1.GetServiceListResponse;
import eu.europa.ec.fisheries.schema.exchange.movement.v1.MovementType;
import eu.europa.ec.fisheries.schema.exchange.plugin.types.v1.PluginType;
import eu.europa.ec.fisheries.schema.exchange.plugin.v1.SetCommandRequest;
import eu.europa.ec.fisheries.schema.exchange.plugin.v1.SetReportRequest;
import eu.europa.ec.fisheries.schema.exchange.service.v1.CapabilityListType;
import eu.europa.ec.fisheries.schema.exchange.service.v1.ServiceResponseType;
import eu.europa.ec.fisheries.schema.exchange.service.v1.ServiceType;
import eu.europa.ec.fisheries.schema.exchange.service.v1.SettingListType;
import eu.europa.ec.fisheries.schema.movementrules.customrule.v1.ActionType;
import eu.europa.ec.fisheries.schema.movementrules.customrule.v1.ConditionType;
import eu.europa.ec.fisheries.schema.movementrules.customrule.v1.CriteriaType;
import eu.europa.ec.fisheries.schema.movementrules.customrule.v1.CustomRuleType;
import eu.europa.ec.fisheries.schema.movementrules.customrule.v1.SubCriteriaType;
import eu.europa.ec.fisheries.uvms.asset.client.model.AssetDTO;
import eu.europa.ec.fisheries.uvms.commons.message.api.MessageConstants;
import eu.europa.ec.fisheries.uvms.docker.validation.asset.AssetTestHelper;
import eu.europa.ec.fisheries.uvms.docker.validation.common.AbstractRest;
import eu.europa.ec.fisheries.uvms.docker.validation.common.MessageHelper;
import eu.europa.ec.fisheries.uvms.docker.validation.movement.LatLong;
import eu.europa.ec.fisheries.uvms.docker.validation.movement.MovementHelper;
import eu.europa.ec.fisheries.uvms.docker.validation.system.helper.CustomRuleBuilder;
import eu.europa.ec.fisheries.uvms.docker.validation.system.helper.CustomRuleHelper;
import eu.europa.ec.fisheries.uvms.docker.validation.system.helper.FLUXHelper;
import eu.europa.ec.fisheries.uvms.exchange.model.constant.ExchangeModelConstants;
import eu.europa.ec.fisheries.uvms.exchange.model.mapper.ExchangeModuleRequestMapper;
import eu.europa.ec.fisheries.uvms.exchange.model.mapper.JAXBMarshaller;

public class RulesAlarmIT extends AbstractRest {

    private static final long TIMEOUT = 10000;
    private static final String SELECTOR_FLUX = "ServiceName='eu.europa.ec.fisheries.uvms.plugins.flux.movement'";
    private static final String SERVICE_NAME = "eu.europa.ec.fisheries.uvms.docker.validation.system.rules.EMAIL";
    private static String emailSelector = "ServiceName='" + SERVICE_NAME + "'";

    private static MessageHelper messageHelper;

    @AfterClass
    public static void cleanup() {
        messageHelper.close();
    }
    
    
    @BeforeClass
    public static void registerEmailPluginIfNotExisting() throws Exception {
        messageHelper = new MessageHelper();
        String exchangeRequest = ExchangeModuleRequestMapper.createGetServiceListRequest(Arrays.asList(PluginType.EMAIL));
        Message exchangeResponse = messageHelper.getMessageResponse(MessageConstants.QUEUE_EXCHANGE_EVENT_NAME, exchangeRequest);
        GetServiceListResponse emailServices = JAXBMarshaller.unmarshallTextMessage((TextMessage) exchangeResponse, GetServiceListResponse.class);
        List<ServiceResponseType> service = emailServices.getService();
        if (!service.isEmpty()) {
            emailSelector = "ServiceName='" + service.get(0).getServiceClassName() + "'";
        } else {
            ServiceType serviceType = new ServiceType();
            serviceType.setName("TEST EMAIL");
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
    
    @After
    public void removeCustomRules() throws Exception {
        CustomRuleHelper.removeCustomRulesByDefaultUser();
    }
    
    @Test
    public void sendEmailIfReportedSpeedIsGreaterThan10knotsTest() throws Exception {
        LocalDateTime timestamp = LocalDateTime.now(ZoneOffset.UTC);

        AssetDTO asset = AssetTestHelper.createTestAsset();
        
        String email = System.currentTimeMillis() + "@mail.com";
        CustomRuleType speedRule = CustomRuleBuilder.getBuilder()
                .setName("Speed > 10 knots => Send email")
                .rule(CriteriaType.POSITION, SubCriteriaType.REPORTED_SPEED, 
                        ConditionType.GT, "10")
                .action(ActionType.EMAIL, email)
                .build();
        
        CustomRuleType createdSpeedRule = CustomRuleHelper.createCustomRule(speedRule);
        assertNotNull(createdSpeedRule);
        
        LatLong position = new LatLong(11d, 56d, new Date());
        position.speed = 10.5;
        FLUXHelper.sendPositionToFluxPlugin(asset, position);
        
        TextMessage message = (TextMessage) messageHelper.listenOnEventBus(emailSelector, TIMEOUT);
        assertThat(message, is(notNullValue()));
        
        SetCommandRequest setCommandRequest = JAXBMarshaller.unmarshallTextMessage(message, SetCommandRequest.class);
        assertThat(setCommandRequest.getCommand().getEmail().getTo(), is(email));
        assertThat(setCommandRequest.getCommand().getFwdRule(), is(createdSpeedRule.getName()));
        
        CustomRuleHelper.assertRuleTriggered(createdSpeedRule, timestamp);
    }
    
    @Test
    public void sendEmailIfReportedSpeedIslessThan10knotsTest() throws Exception {
        LocalDateTime timestamp = LocalDateTime.now(ZoneOffset.UTC);

        AssetDTO asset = AssetTestHelper.createTestAsset();
        
        String email = System.currentTimeMillis() + "@mail.com";
        CustomRuleType speedRule = CustomRuleBuilder.getBuilder()
                .setName("Speed < 10 knots => Send email")
                .rule(CriteriaType.POSITION, SubCriteriaType.REPORTED_SPEED, 
                        ConditionType.LT, "10")
                .action(ActionType.EMAIL, email)
                .build();
        
        CustomRuleType createdSpeedRule = CustomRuleHelper.createCustomRule(speedRule);
        assertNotNull(createdSpeedRule);
        
        LatLong position = new LatLong(11d, 56d, new Date());
        position.speed = 9.5;
        FLUXHelper.sendPositionToFluxPlugin(asset, position);
        
        TextMessage message = (TextMessage) messageHelper.listenOnEventBus(emailSelector, TIMEOUT);
        assertThat(message, is(notNullValue()));
        
        SetCommandRequest setCommandRequest = JAXBMarshaller.unmarshallTextMessage(message, SetCommandRequest.class);
        assertThat(setCommandRequest.getCommand().getEmail().getTo(), is(email));
        assertThat(setCommandRequest.getCommand().getFwdRule(), is(createdSpeedRule.getName()));
        
        CustomRuleHelper.assertRuleTriggered(createdSpeedRule, timestamp);
    }

    @Test
    public void doNotTriggerRuleIfReportedSpeedIsLessThan10knotsTest() throws Exception {
        LocalDateTime timestamp = LocalDateTime.now(ZoneOffset.UTC);

        AssetDTO asset = AssetTestHelper.createTestAsset();
        
        CustomRuleType speedRule = CustomRuleBuilder.getBuilder()
                .setName("Speed > 10 knots => Send email")
                .rule(CriteriaType.POSITION, SubCriteriaType.REPORTED_SPEED, 
                        ConditionType.GT, "10")
                .action(ActionType.EMAIL, "test@mail.com")
                .build();
        
        CustomRuleType createdSpeedRule = CustomRuleHelper.createCustomRule(speedRule);
        assertNotNull(createdSpeedRule);
        
        String email = System.currentTimeMillis() + "@mail.com";
        CustomRuleType fsRule = CustomRuleBuilder.getBuilder()
                .setName("Flag state => Send email")
                .rule(CriteriaType.ASSET, SubCriteriaType.FLAG_STATE, 
                        ConditionType.EQ, asset.getFlagStateCode())
                .action(ActionType.EMAIL, email)
                .build();
        
        CustomRuleType createdFsRule = CustomRuleHelper.createCustomRule(fsRule);
        assertNotNull(createdFsRule);
        
        LatLong position = new LatLong(11d, 56d, new Date());
        position.speed = 9.5;
        FLUXHelper.sendPositionToFluxPlugin(asset, position);
        
        TextMessage message = (TextMessage) messageHelper.listenOnEventBus(emailSelector, TIMEOUT);
        assertThat(message, is(notNullValue()));
        
        SetCommandRequest setCommandRequest = JAXBMarshaller.unmarshallTextMessage(message, SetCommandRequest.class);
        assertThat(setCommandRequest.getCommand().getEmail().getTo(), is(email));
        assertThat(setCommandRequest.getCommand().getFwdRule(), is(createdFsRule.getName()));
        
        CustomRuleHelper.assertRuleNotTriggered(createdSpeedRule);
        CustomRuleHelper.assertRuleTriggered(createdFsRule, timestamp);
    }
    
    @Test
    public void doNotTriggerRuleIfReportedSpeedIsGreaterThan10knotsTest() throws Exception {
        LocalDateTime timestamp = LocalDateTime.now(ZoneOffset.UTC);

        AssetDTO asset = AssetTestHelper.createTestAsset();
        
        CustomRuleType speedRule = CustomRuleBuilder.getBuilder()
                .setName("Speed < 10 knots => Send email")
                .rule(CriteriaType.POSITION, SubCriteriaType.REPORTED_SPEED, 
                        ConditionType.LT, "10")
                .action(ActionType.EMAIL, "test@mail.com")
                .build();
        
        CustomRuleType createdSpeedRule = CustomRuleHelper.createCustomRule(speedRule);
        assertNotNull(createdSpeedRule);
        
        String email = System.currentTimeMillis() + "@mail.com";
        CustomRuleType fsRule = CustomRuleBuilder.getBuilder()
                .setName("Flag state => Send email")
                .rule(CriteriaType.ASSET, SubCriteriaType.FLAG_STATE, 
                        ConditionType.EQ, asset.getFlagStateCode())
                .action(ActionType.EMAIL, email)
                .build();
        
        CustomRuleType createdFsRule = CustomRuleHelper.createCustomRule(fsRule);
        assertNotNull(createdFsRule);
        
        LatLong position = new LatLong(11d, 56d, new Date());
        position.speed = 10.5;
        FLUXHelper.sendPositionToFluxPlugin(asset, position);
        
        TextMessage message = (TextMessage) messageHelper.listenOnEventBus(emailSelector, TIMEOUT);
        assertThat(message, is(notNullValue()));
        
        SetCommandRequest setCommandRequest = JAXBMarshaller.unmarshallTextMessage(message, SetCommandRequest.class);
        assertThat(setCommandRequest.getCommand().getEmail().getTo(), is(email));
        assertThat(setCommandRequest.getCommand().getFwdRule(), is(createdFsRule.getName()));
        
        CustomRuleHelper.assertRuleNotTriggered(createdSpeedRule);
        CustomRuleHelper.assertRuleTriggered(createdFsRule, timestamp);
    }
    
    @Test
    public void sendEmailIfReportedSpeedIsGreaterThanOrEqual10knotsTest() throws Exception {
        LocalDateTime timestamp = LocalDateTime.now(ZoneOffset.UTC);

        AssetDTO asset = AssetTestHelper.createTestAsset();
        
        String email = System.currentTimeMillis() + "@mail.com";
        CustomRuleType speedRule = CustomRuleBuilder.getBuilder()
                .setName("Speed >= 10 knots => Send email")
                .rule(CriteriaType.POSITION, SubCriteriaType.REPORTED_SPEED, 
                        ConditionType.GE, "10")
                .action(ActionType.EMAIL, email)
                .build();
        
        CustomRuleType createdSpeedRule = CustomRuleHelper.createCustomRule(speedRule);
        assertNotNull(createdSpeedRule);
        
        LatLong position = new LatLong(11d, 56d, new Date());
        position.speed = 10;
        FLUXHelper.sendPositionToFluxPlugin(asset, position);
        
        TextMessage message = (TextMessage) messageHelper.listenOnEventBus(emailSelector, TIMEOUT);
        assertThat(message, is(notNullValue()));
        
        SetCommandRequest setCommandRequest = JAXBMarshaller.unmarshallTextMessage(message, SetCommandRequest.class);
        assertThat(setCommandRequest.getCommand().getEmail().getTo(), is(email));
        assertThat(setCommandRequest.getCommand().getFwdRule(), is(createdSpeedRule.getName()));
        
        CustomRuleHelper.assertRuleTriggered(createdSpeedRule, timestamp);
    }
    
    @Test
    public void sendEmailIfReportedSpeedIsLessThanOrEqual10knotsTest() throws Exception {
        LocalDateTime timestamp = LocalDateTime.now(ZoneOffset.UTC);

        AssetDTO asset = AssetTestHelper.createTestAsset();
        
        String email = System.currentTimeMillis() + "@mail.com";
        CustomRuleType speedRule = CustomRuleBuilder.getBuilder()
                .setName("Speed <= 10 knots => Send email")
                .rule(CriteriaType.POSITION, SubCriteriaType.REPORTED_SPEED, 
                        ConditionType.LE, "10")
                .action(ActionType.EMAIL, email)
                .build();
        
        CustomRuleType createdSpeedRule = CustomRuleHelper.createCustomRule(speedRule);
        assertNotNull(createdSpeedRule);
        
        LatLong position = new LatLong(11d, 56d, new Date());
        position.speed = 10;
        FLUXHelper.sendPositionToFluxPlugin(asset, position);
        
        TextMessage message = (TextMessage) messageHelper.listenOnEventBus(emailSelector, TIMEOUT);
        assertThat(message, is(notNullValue()));
        
        SetCommandRequest setCommandRequest = JAXBMarshaller.unmarshallTextMessage(message, SetCommandRequest.class);
        assertThat(setCommandRequest.getCommand().getEmail().getTo(), is(email));
        assertThat(setCommandRequest.getCommand().getFwdRule(), is(createdSpeedRule.getName()));
        
        CustomRuleHelper.assertRuleTriggered(createdSpeedRule, timestamp);
    }
    
    @Test
    public void sendEmailIfReportedSpeedIsGreaterThan10knotsAndAreaIsDNKTest() throws Exception {
        LocalDateTime timestamp = LocalDateTime.now(ZoneOffset.UTC);

        AssetDTO asset = AssetTestHelper.createTestAsset();
        
        String email = System.currentTimeMillis() + "@mail.com";
        CustomRuleType speedAndAreaRule = CustomRuleBuilder.getBuilder()
                .setName("Sp > 10 && area = DNK => Send email")
                .rule(CriteriaType.POSITION, SubCriteriaType.REPORTED_SPEED, 
                        ConditionType.GT, "10")
                .and(CriteriaType.AREA, SubCriteriaType.AREA_CODE, 
                        ConditionType.EQ, "DNK")
                .action(ActionType.EMAIL, email)
                .build();
        
        CustomRuleType createdSpeedAndAreaRule = CustomRuleHelper.createCustomRule(speedAndAreaRule);
        assertNotNull(createdSpeedAndAreaRule);
        
        LatLong position = new LatLong(56d, 10.5, new Date());
        position.speed = 10.5;
        FLUXHelper.sendPositionToFluxPlugin(asset, position);
        
        TextMessage message = (TextMessage) messageHelper.listenOnEventBus(emailSelector, TIMEOUT);
        assertThat(message, is(notNullValue()));
        
        SetCommandRequest setCommandRequest = JAXBMarshaller.unmarshallTextMessage(message, SetCommandRequest.class);
        assertThat(setCommandRequest.getCommand().getEmail().getTo(), is(email));
        assertThat(setCommandRequest.getCommand().getFwdRule(), is(createdSpeedAndAreaRule.getName()));
        
        CustomRuleHelper.assertRuleTriggered(createdSpeedAndAreaRule, timestamp);
    }
    
    @Test
    public void sendEmailIfReportedSpeedIsLessThan10knotsAndAreaIsDNKTest() throws Exception {
        LocalDateTime timestamp = LocalDateTime.now(ZoneOffset.UTC);

        AssetDTO asset = AssetTestHelper.createTestAsset();
        
        String email = System.currentTimeMillis() + "@mail.com";
        CustomRuleType speedAndAreaRule = CustomRuleBuilder.getBuilder()
                .setName("Sp < 10 && area = DNK => Send email")
                .rule(CriteriaType.POSITION, SubCriteriaType.REPORTED_SPEED, 
                        ConditionType.LT, "10")
                .and(CriteriaType.AREA, SubCriteriaType.AREA_CODE, 
                        ConditionType.EQ, "DNK")
                .action(ActionType.EMAIL, email)
                .build();
        
        CustomRuleType createdSpeedAndAreaRule = CustomRuleHelper.createCustomRule(speedAndAreaRule);
        assertNotNull(createdSpeedAndAreaRule);
        
        LatLong position = new LatLong(56d, 10.5, new Date());
        position.speed = 9.5;
        FLUXHelper.sendPositionToFluxPlugin(asset, position);
        
        TextMessage message = (TextMessage) messageHelper.listenOnEventBus(emailSelector, TIMEOUT);
        assertThat(message, is(notNullValue()));
        
        SetCommandRequest setCommandRequest = JAXBMarshaller.unmarshallTextMessage(message, SetCommandRequest.class);
        assertThat(setCommandRequest.getCommand().getEmail().getTo(), is(email));
        assertThat(setCommandRequest.getCommand().getFwdRule(), is(createdSpeedAndAreaRule.getName()));
        
        CustomRuleHelper.assertRuleTriggered(createdSpeedAndAreaRule, timestamp);
    }
    
    @Test
    public void sendEmailIfAreaCodeIsDEUTest() throws Exception {
        LocalDateTime timestamp = LocalDateTime.now(ZoneOffset.UTC);

        AssetDTO asset = AssetTestHelper.createTestAsset();
        
        String email = System.currentTimeMillis() + "@mail.com";
        CustomRuleType areaRule = CustomRuleBuilder.getBuilder()
                .setName("Area = DEU => Send email")
                .rule(CriteriaType.AREA, SubCriteriaType.AREA_CODE, 
                        ConditionType.EQ, "DEU")
                .action(ActionType.EMAIL, email)
                .build();
        
        CustomRuleType createdAreaRule = CustomRuleHelper.createCustomRule(areaRule);
        assertNotNull(createdAreaRule);
        
        LatLong position = new LatLong(54.528352, 12.877972, new Date());
        FLUXHelper.sendPositionToFluxPlugin(asset, position);
        
        TextMessage message = (TextMessage) messageHelper.listenOnEventBus(emailSelector, TIMEOUT);
        assertThat(message, is(notNullValue()));
        
        SetCommandRequest setCommandRequest = JAXBMarshaller.unmarshallTextMessage(message, SetCommandRequest.class);
        assertThat(setCommandRequest.getCommand().getEmail().getTo(), is(email));
        assertThat(setCommandRequest.getCommand().getFwdRule(), is(createdAreaRule.getName()));
        
        CustomRuleHelper.assertRuleTriggered(createdAreaRule, timestamp);
    }
    
    @Test
    public void sendEmailIfAssetIRCSMatchesTest() throws Exception {
        LocalDateTime timestamp = LocalDateTime.now(ZoneOffset.UTC);

        AssetDTO asset = AssetTestHelper.createTestAsset();
        
        String email = System.currentTimeMillis() + "@mail.com";
        CustomRuleType ircsRule = CustomRuleBuilder.getBuilder()
                .setName("IRCS => Send email")
                .rule(CriteriaType.ASSET, SubCriteriaType.ASSET_IRCS, 
                        ConditionType.EQ, asset.getIrcs())
                .action(ActionType.EMAIL, email)
                .build();
        
        CustomRuleType createdIrcsRule = CustomRuleHelper.createCustomRule(ircsRule);
        assertNotNull(createdIrcsRule);
        
        LatLong position = new LatLong(1d, 1d, new Date());
        FLUXHelper.sendPositionToFluxPlugin(asset, position);
        
        TextMessage message = (TextMessage) messageHelper.listenOnEventBus(emailSelector, TIMEOUT);
        assertThat(message, is(notNullValue()));
        
        SetCommandRequest setCommandRequest = JAXBMarshaller.unmarshallTextMessage(message, SetCommandRequest.class);
        assertThat(setCommandRequest.getCommand().getEmail().getTo(), is(email));
        assertThat(setCommandRequest.getCommand().getFwdRule(), is(createdIrcsRule.getName()));
        
        CustomRuleHelper.assertRuleTriggered(createdIrcsRule, timestamp);
    }
    
    @Test
    public void sendEmailIfIrcsDisjunctionMatchesTest() throws Exception {
        LocalDateTime timestamp = LocalDateTime.now(ZoneOffset.UTC);

        AssetDTO asset1 = AssetTestHelper.createTestAsset();
        AssetDTO asset2 = AssetTestHelper.createTestAsset();
        
        String email = System.currentTimeMillis() + "@mail.com";
        CustomRuleType customRule = CustomRuleBuilder.getBuilder()
                .rule(CriteriaType.ASSET, SubCriteriaType.ASSET_IRCS, 
                        ConditionType.EQ, asset1.getIrcs())
                .or(CriteriaType.ASSET, SubCriteriaType.ASSET_IRCS, 
                        ConditionType.EQ, asset2.getIrcs())
                .action(ActionType.EMAIL, email)
                .build();
        
        CustomRuleType createdCustomRule = CustomRuleHelper.createCustomRule(customRule);
        assertNotNull(createdCustomRule);
        
        LatLong position = new LatLong(1d, 1d, new Date());
        FLUXHelper.sendPositionToFluxPlugin(asset1, position);
        
        TextMessage message = (TextMessage) messageHelper.listenOnEventBus(emailSelector, TIMEOUT);
        assertThat(message, is(notNullValue()));
        
        SetCommandRequest setCommandRequest = JAXBMarshaller.unmarshallTextMessage(message, SetCommandRequest.class);
        assertThat(setCommandRequest.getCommand().getEmail().getTo(), is(email));
        assertThat(setCommandRequest.getCommand().getFwdRule(), is(createdCustomRule.getName()));
        
        CustomRuleHelper.assertRuleTriggered(createdCustomRule, timestamp);
        
        timestamp = LocalDateTime.now(ZoneOffset.UTC);
        
        LatLong position2 = new LatLong(2d, 2d, new Date());
        FLUXHelper.sendPositionToFluxPlugin(asset2, position2);
        
        TextMessage message2 = (TextMessage) messageHelper.listenOnEventBus(emailSelector, TIMEOUT);
        assertThat(message2, is(notNullValue()));
        
        SetCommandRequest setCommandRequest2 = JAXBMarshaller.unmarshallTextMessage(message2, SetCommandRequest.class);
        assertThat(setCommandRequest2.getCommand().getEmail().getTo(), is(email));
        assertThat(setCommandRequest2.getCommand().getFwdRule(), is(createdCustomRule.getName()));
        
        CustomRuleHelper.assertRuleTriggered(createdCustomRule, timestamp);
    }
    
    @Test
    public void sendEmailIfIrcsCfrConjunctionMatchesTest() throws Exception {
        LocalDateTime timestamp = LocalDateTime.now(ZoneOffset.UTC);

        AssetDTO asset = AssetTestHelper.createTestAsset();
        
        String email = System.currentTimeMillis() + "@mail.com";
        CustomRuleType customRule = CustomRuleBuilder.getBuilder()
                .rule(CriteriaType.ASSET, SubCriteriaType.ASSET_IRCS, 
                        ConditionType.EQ, asset.getIrcs())
                .and(CriteriaType.ASSET, SubCriteriaType.ASSET_CFR, 
                        ConditionType.EQ, asset.getCfr())
                .action(ActionType.EMAIL, email)
                .build();
        
        CustomRuleType createdCustomRule = CustomRuleHelper.createCustomRule(customRule);
        assertNotNull(createdCustomRule);
        
        LatLong position = new LatLong(1d, 1d, new Date());
        FLUXHelper.sendPositionToFluxPlugin(asset, position);
        
        TextMessage message = (TextMessage) messageHelper.listenOnEventBus(emailSelector, TIMEOUT);
        assertThat(message, is(notNullValue()));
        
        SetCommandRequest setCommandRequest = JAXBMarshaller.unmarshallTextMessage(message, SetCommandRequest.class);
        assertThat(setCommandRequest.getCommand().getEmail().getTo(), is(email));
        assertThat(setCommandRequest.getCommand().getFwdRule(), is(createdCustomRule.getName()));
        
        CustomRuleHelper.assertRuleTriggered(createdCustomRule, timestamp);
    }
    
    @Test
    public void doNotTriggerRuleIfIrcsCfrConjunctionNotMatchesTest() throws Exception {
        LocalDateTime timestamp = LocalDateTime.now(ZoneOffset.UTC);

        AssetDTO asset = AssetTestHelper.createTestAsset();
        
        CustomRuleType customRule = CustomRuleBuilder.getBuilder()
                .rule(CriteriaType.ASSET, SubCriteriaType.ASSET_IRCS, 
                        ConditionType.EQ, asset.getIrcs())
                .and(CriteriaType.ASSET, SubCriteriaType.ASSET_CFR, 
                        ConditionType.EQ, "MOCKCFR")
                .action(ActionType.EMAIL, "test@mail.com")
                .build();
        
        CustomRuleType createdCustomRule = CustomRuleHelper.createCustomRule(customRule);
        assertNotNull(createdCustomRule);
        
        String email = System.currentTimeMillis() + "@mail.com";
        CustomRuleType fsRule = CustomRuleBuilder.getBuilder()
                .rule(CriteriaType.ASSET, SubCriteriaType.FLAG_STATE, 
                        ConditionType.EQ, asset.getFlagStateCode())
                .action(ActionType.EMAIL, email)
                .build();
        
        CustomRuleType createdFsRule = CustomRuleHelper.createCustomRule(fsRule);
        assertNotNull(createdFsRule);
        
        LatLong position = new LatLong(1d, 1d, new Date());
        FLUXHelper.sendPositionToFluxPlugin(asset, position);
        
        TextMessage message = (TextMessage) messageHelper.listenOnEventBus(emailSelector, TIMEOUT);
        assertThat(message, is(notNullValue()));
        
        SetCommandRequest setCommandRequest = JAXBMarshaller.unmarshallTextMessage(message, SetCommandRequest.class);
        assertThat(setCommandRequest.getCommand().getEmail().getTo(), is(email));
        assertThat(setCommandRequest.getCommand().getFwdRule(), is(createdFsRule.getName()));
        
        CustomRuleHelper.assertRuleNotTriggered(createdCustomRule);
        CustomRuleHelper.assertRuleTriggered(createdFsRule, timestamp);
    }
    
    @Test
    public void sendEmailIfAssetCFRMatchesTest() throws Exception {
        LocalDateTime timestamp = LocalDateTime.now(ZoneOffset.UTC);

        AssetDTO asset = AssetTestHelper.createTestAsset();
        
        String email = System.currentTimeMillis() + "@mail.com";
        CustomRuleType cfrRule = CustomRuleBuilder.getBuilder()
                .setName("CFR => Send email")
                .rule(CriteriaType.ASSET, SubCriteriaType.ASSET_CFR, 
                        ConditionType.EQ, asset.getCfr())
                .action(ActionType.EMAIL, email)
                .build();
        
        CustomRuleType createdCfrRule = CustomRuleHelper.createCustomRule(cfrRule);
        assertNotNull(createdCfrRule);
        
        LatLong position = new LatLong(1d, 1d, new Date());
        FLUXHelper.sendPositionToFluxPlugin(asset, position);
        
        TextMessage message = (TextMessage) messageHelper.listenOnEventBus(emailSelector, TIMEOUT);
        assertThat(message, is(notNullValue()));
        
        SetCommandRequest setCommandRequest = JAXBMarshaller.unmarshallTextMessage(message, SetCommandRequest.class);
        assertThat(setCommandRequest.getCommand().getEmail().getTo(), is(email));
        assertThat(setCommandRequest.getCommand().getFwdRule(), is(createdCfrRule.getName()));
        
        CustomRuleHelper.assertRuleTriggered(createdCfrRule, timestamp);
    }
    
    @Test
    public void sendEmailIfAssetNameMatchesTest() throws Exception {
        LocalDateTime timestamp = LocalDateTime.now(ZoneOffset.UTC);

        AssetDTO asset = AssetTestHelper.createTestAsset();
        
        String email = System.currentTimeMillis() + "@mail.com";
        CustomRuleType assetNameRule = CustomRuleBuilder.getBuilder()
                .setName("Asset name => Send email")
                .rule(CriteriaType.ASSET, SubCriteriaType.ASSET_NAME, 
                        ConditionType.EQ, asset.getName())
                .action(ActionType.EMAIL, email)
                .build();
        
        CustomRuleType createdAssetNameRule = CustomRuleHelper.createCustomRule(assetNameRule);
        assertNotNull(createdAssetNameRule);
        
        LatLong position = new LatLong(1d, 1d, new Date());
        FLUXHelper.sendPositionToFluxPlugin(asset, position);
        
        TextMessage message = (TextMessage) messageHelper.listenOnEventBus(emailSelector, TIMEOUT);
        assertThat(message, is(notNullValue()));
        
        SetCommandRequest setCommandRequest = JAXBMarshaller.unmarshallTextMessage(message, SetCommandRequest.class);
        assertThat(setCommandRequest.getCommand().getEmail().getTo(), is(email));
        assertThat(setCommandRequest.getCommand().getFwdRule(), is(createdAssetNameRule.getName()));
        
        CustomRuleHelper.assertRuleTriggered(createdAssetNameRule, timestamp);
    }
    
    @Test
    public void sendEmailIfLatitudeIsGreaterThan10Test() throws Exception {
        LocalDateTime timestamp = LocalDateTime.now(ZoneOffset.UTC);

        AssetDTO asset = AssetTestHelper.createTestAsset();
        
        String email = System.currentTimeMillis() + "@mail.com";
        CustomRuleType customRule = CustomRuleBuilder.getBuilder()
                .rule(CriteriaType.POSITION, SubCriteriaType.LATITUDE, 
                        ConditionType.GT, "10")
                .action(ActionType.EMAIL, email)
                .build();
        
        CustomRuleType createdCustomRule = CustomRuleHelper.createCustomRule(customRule);
        assertNotNull(createdCustomRule);
        
        LatLong position = new LatLong(11d, 1d, new Date());
        FLUXHelper.sendPositionToFluxPlugin(asset, position);
        
        TextMessage message = (TextMessage) messageHelper.listenOnEventBus(emailSelector, TIMEOUT);
        assertThat(message, is(notNullValue()));
        
        SetCommandRequest setCommandRequest = JAXBMarshaller.unmarshallTextMessage(message, SetCommandRequest.class);
        assertThat(setCommandRequest.getCommand().getEmail().getTo(), is(email));
        assertThat(setCommandRequest.getCommand().getFwdRule(), is(createdCustomRule.getName()));
        
        CustomRuleHelper.assertRuleTriggered(createdCustomRule, timestamp);
    }
    
    @Test
    public void sendEmailIfLongitudeIsGreaterThan10Test() throws Exception {
        LocalDateTime timestamp = LocalDateTime.now(ZoneOffset.UTC);

        AssetDTO asset = AssetTestHelper.createTestAsset();
        
        String email = System.currentTimeMillis() + "@mail.com";
        CustomRuleType customRule = CustomRuleBuilder.getBuilder()
                .rule(CriteriaType.POSITION, SubCriteriaType.LONGITUDE, 
                        ConditionType.GT, "10")
                .action(ActionType.EMAIL, email)
                .build();
        
        CustomRuleType createdCustomRule = CustomRuleHelper.createCustomRule(customRule);
        assertNotNull(createdCustomRule);
        
        LatLong position = new LatLong(1d, 11d, new Date());
        FLUXHelper.sendPositionToFluxPlugin(asset, position);
        
        TextMessage message = (TextMessage) messageHelper.listenOnEventBus(emailSelector, TIMEOUT);
        assertThat(message, is(notNullValue()));
        
        SetCommandRequest setCommandRequest = JAXBMarshaller.unmarshallTextMessage(message, SetCommandRequest.class);
        assertThat(setCommandRequest.getCommand().getEmail().getTo(), is(email));
        assertThat(setCommandRequest.getCommand().getFwdRule(), is(createdCustomRule.getName()));
        
        CustomRuleHelper.assertRuleTriggered(createdCustomRule, timestamp);
    }
    
    @Test
    public void sendEmailIfPositionReportTimeIsGreaterOrEqualTest() throws Exception {
        ZonedDateTime timestamp = ZonedDateTime.now(ZoneOffset.UTC);

        AssetDTO asset = AssetTestHelper.createTestAsset();
        
        String email = System.currentTimeMillis() + "@mail.com";
        CustomRuleType customRule = CustomRuleBuilder.getBuilder()
                .rule(CriteriaType.POSITION, SubCriteriaType.POSITION_REPORT_TIME, 
                        ConditionType.GE, timestamp.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss Z")))
                .action(ActionType.EMAIL, email)
                .build();
        
        CustomRuleType createdCustomRule = CustomRuleHelper.createCustomRule(customRule);
        assertNotNull(createdCustomRule);
        
        LatLong position = new LatLong(1d, 1d, new Date());
        FLUXHelper.sendPositionToFluxPlugin(asset, position);
        
        TextMessage message = (TextMessage) messageHelper.listenOnEventBus(emailSelector, TIMEOUT);
        assertThat(message, is(notNullValue()));
        
        SetCommandRequest setCommandRequest = JAXBMarshaller.unmarshallTextMessage(message, SetCommandRequest.class);
        assertThat(setCommandRequest.getCommand().getEmail().getTo(), is(email));
        assertThat(setCommandRequest.getCommand().getFwdRule(), is(createdCustomRule.getName()));
        
        CustomRuleHelper.assertRuleTriggered(createdCustomRule, timestamp.toLocalDateTime());
    }
    
    @Test
    public void triggerAreaEntryRule() throws Exception {
        ZonedDateTime timestamp = ZonedDateTime.now(ZoneOffset.UTC);

        AssetDTO asset = AssetTestHelper.createTestAsset();
        
        LatLong positionSwe = new LatLong(57.670176, 11.799626, new Date());
        FLUXHelper.sendPositionToFluxPlugin(asset, positionSwe);
        MovementHelper.pollMovementCreated();
        
        String fluxEndpoint = "DNK";
        CustomRuleType areaEntryRule = CustomRuleBuilder.getBuilder()
                .setName("Area entry DNK")
                .rule(CriteriaType.AREA, SubCriteriaType.AREA_CODE_ENT, 
                        ConditionType.EQ, "DNK")
                .action(ActionType.SEND_TO_FLUX, fluxEndpoint)
                .build();
        
        CustomRuleType createdAreaRule = CustomRuleHelper.createCustomRule(areaEntryRule);
        assertNotNull(createdAreaRule);
        
        LatLong positionDnk = new LatLong(56d, 10.5, new Date());
        FLUXHelper.sendPositionToFluxPlugin(asset, positionDnk);
        
        TextMessage message = (TextMessage) messageHelper.listenOnEventBus(SELECTOR_FLUX, TIMEOUT);
        assertThat(message, is(notNullValue()));
        
        SetReportRequest setReportRequest = JAXBMarshaller.unmarshallTextMessage(message, SetReportRequest.class);
        assertThat(setReportRequest.getReport().getRecipient(), is(fluxEndpoint));
        
        MovementType movement = setReportRequest.getReport().getMovement();
        assertThat(movement.getAssetName(), is(asset.getName()));
        assertThat(movement.getIrcs(), is(asset.getIrcs()));
        
        CustomRuleHelper.assertRuleTriggered(createdAreaRule, timestamp.toLocalDateTime());
    }
    
    @Test
    public void doNotTriggerAreaEntryRule() throws Exception {
        ZonedDateTime timestamp = ZonedDateTime.now(ZoneOffset.UTC);

        AssetDTO asset = AssetTestHelper.createTestAsset();
        
        LatLong positionSwe = new LatLong(57.670176, 11.799626, new Date());
        FLUXHelper.sendPositionToFluxPlugin(asset, positionSwe);
        MovementHelper.pollMovementCreated();
        
        String fluxEndpoint = "DNK";
        CustomRuleType areaEntryRule = CustomRuleBuilder.getBuilder()
                .setName("Area entry SWE")
                .rule(CriteriaType.AREA, SubCriteriaType.AREA_CODE_ENT, 
                        ConditionType.EQ, "SWE")
                .action(ActionType.SEND_TO_FLUX, fluxEndpoint)
                .build();
        
        CustomRuleType createdAreaRule = CustomRuleHelper.createCustomRule(areaEntryRule);
        assertNotNull(createdAreaRule);
        
        String email = System.currentTimeMillis() + "@mail.com";
        CustomRuleType fsRule = CustomRuleBuilder.getBuilder()
                .setName("Flag state => Send email")
                .rule(CriteriaType.ASSET, SubCriteriaType.FLAG_STATE, 
                        ConditionType.EQ, asset.getFlagStateCode())
                .action(ActionType.EMAIL, email)
                .build();
        
        CustomRuleType createdFsRule = CustomRuleHelper.createCustomRule(fsRule);
        assertNotNull(createdFsRule);
        
        LatLong positionSwe2 = new LatLong(57.670176, 11.799626, new Date());
        FLUXHelper.sendPositionToFluxPlugin(asset, positionSwe2);
        
        TextMessage message = (TextMessage) messageHelper.listenOnEventBus(emailSelector, TIMEOUT);
        assertThat(message, is(notNullValue()));
        
        SetCommandRequest setCommandRequest = JAXBMarshaller.unmarshallTextMessage(message, SetCommandRequest.class);
        assertThat(setCommandRequest.getCommand().getEmail().getTo(), is(email));
        assertThat(setCommandRequest.getCommand().getFwdRule(), is(createdFsRule.getName()));
        
        CustomRuleHelper.assertRuleNotTriggered(createdAreaRule);
        CustomRuleHelper.assertRuleTriggered(createdFsRule, timestamp.toLocalDateTime());
    }
    
    @Test
    public void triggerAreaExitRule() throws Exception {
        ZonedDateTime timestamp = ZonedDateTime.now(ZoneOffset.UTC);

        AssetDTO asset = AssetTestHelper.createTestAsset();
        
        LatLong positionSwe = new LatLong(57.670176, 11.799626, new Date());
        FLUXHelper.sendPositionToFluxPlugin(asset, positionSwe);
        MovementHelper.pollMovementCreated();
        
        String fluxEndpoint = "DNK";
        CustomRuleType areaEntryRule = CustomRuleBuilder.getBuilder()
                .setName("Area exit SWE")
                .rule(CriteriaType.AREA, SubCriteriaType.AREA_CODE_EXT, 
                        ConditionType.EQ, "SWE")
                .action(ActionType.SEND_TO_FLUX, fluxEndpoint)
                .build();
        
        CustomRuleType createdAreaRule = CustomRuleHelper.createCustomRule(areaEntryRule);
        assertNotNull(createdAreaRule);
        
        LatLong positionDnk = new LatLong(56d, 10.5, new Date());
        FLUXHelper.sendPositionToFluxPlugin(asset, positionDnk);
        
        TextMessage message = (TextMessage) messageHelper.listenOnEventBus(SELECTOR_FLUX, TIMEOUT);
        assertThat(message, is(notNullValue()));
        
        SetReportRequest setReportRequest = JAXBMarshaller.unmarshallTextMessage(message, SetReportRequest.class);
        assertThat(setReportRequest.getReport().getRecipient(), is(fluxEndpoint));
        
        MovementType movement = setReportRequest.getReport().getMovement();
        assertThat(movement.getAssetName(), is(asset.getName()));
        assertThat(movement.getIrcs(), is(asset.getIrcs()));
        
        CustomRuleHelper.assertRuleTriggered(createdAreaRule, timestamp.toLocalDateTime());
    }

    private static final String INMARSAT_SELECTOR = "ServiceName='eu.europa.ec.fisheries.uvms.plugins.inmarsat'";

    @Test
    public void createPollIfReportedSpeedIsGreaterThan10knotsTest() throws Exception {
        LocalDateTime timestamp = LocalDateTime.now(ZoneOffset.UTC);

        AssetDTO asset = AssetTestHelper.createTestAsset();
        MobileTerminalDto mobileTerminal = MobileTerminalTestHelper.createMobileTerminal();
        MobileTerminalTestHelper.assignMobileTerminal(asset, mobileTerminal);


        CustomRuleType speedRule = CustomRuleBuilder.getBuilder()
                .setName("Speed > 10 knots => Send poll")
                .rule(CriteriaType.POSITION, SubCriteriaType.REPORTED_SPEED,
                        ConditionType.GT, "10")
                .action(ActionType.MANUAL_POLL, "Not needed")
                .build();



        CustomRuleType createdSpeedRule = CustomRuleHelper.createCustomRule(speedRule);
        assertNotNull(createdSpeedRule);

        TextMessage message = null;
        try (TopicListener topicListener = new TopicListener(INMARSAT_SELECTOR)) {

            LatLong position = new LatLong(11d, 56d, new Date());
            position.speed = 10.5;

            ChannelDto channel = null;
            Iterator it = mobileTerminal.getChannels().iterator();                  //srsly why is this a bloody set?????? And why have we not set the different channel variables???????
            while(it.hasNext()){
                channel = (ChannelDto)it.next();
            }
            MovementHelper movementHelper = new MovementHelper();
            IncomingMovement incomingMovement = movementHelper.createIncomingMovement(asset, position);
            incomingMovement.setMovementSourceType(MovementSourceType.INMARSAT_C.value());
            incomingMovement.setPluginType(PluginType.SATELLITE_RECEIVER.value());
            incomingMovement.setMobileTerminalMemberNumber(channel.getMemberNumber());
            incomingMovement.setMobileTerminalDNID(channel.getDNID());
            incomingMovement.setComChannelType(MovementComChannelType.MOBILE_TERMINAL.value());

            movementHelper.createMovement(incomingMovement);
            message = (TextMessage) topicListener.listenOnEventBus();
        }

        assertThat(message, is(notNullValue()));
        SetCommandRequest response = JAXBMarshaller.unmarshallString(message.getText(), SetCommandRequest.class);


        assertThat(response.getCommand().getCommand(), is(CommandTypeType.POLL));

        List<KeyValueType> pollReceiverValues = response.getCommand().getPoll().getPollReceiver();
        Map<String, String> receiverValuesMap = new HashMap<>();
        for (KeyValueType keyValueType : pollReceiverValues) {
            receiverValuesMap.put(keyValueType.getKey(), keyValueType.getValue());
        }
        assertThat(receiverValuesMap.get("SATELLITE_NUMBER"), is(mobileTerminal.getSatelliteNumber()));

        ChannelDto channel = mobileTerminal.getChannels().iterator().next();
        assertThat(receiverValuesMap.get("DNID"), is(channel.getDNID()));
        assertThat(receiverValuesMap.get("MEMBER_NUMBER"), is(channel.getMemberNumber()));

        CustomRuleHelper.assertRuleTriggered(createdSpeedRule, timestamp);
    }

}
