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
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import javax.jms.Message;
import javax.jms.TextMessage;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import eu.europa.ec.fisheries.schema.exchange.module.v1.GetServiceListResponse;
import eu.europa.ec.fisheries.schema.exchange.plugin.types.v1.PluginType;
import eu.europa.ec.fisheries.schema.exchange.plugin.v1.SetCommandRequest;
import eu.europa.ec.fisheries.schema.exchange.service.v1.CapabilityListType;
import eu.europa.ec.fisheries.schema.exchange.service.v1.ServiceResponseType;
import eu.europa.ec.fisheries.schema.exchange.service.v1.ServiceType;
import eu.europa.ec.fisheries.schema.exchange.service.v1.SettingListType;
import eu.europa.ec.fisheries.schema.rules.customrule.v1.ActionType;
import eu.europa.ec.fisheries.schema.rules.customrule.v1.ConditionType;
import eu.europa.ec.fisheries.schema.rules.customrule.v1.CriteriaType;
import eu.europa.ec.fisheries.schema.rules.customrule.v1.CustomRuleType;
import eu.europa.ec.fisheries.schema.rules.customrule.v1.SubCriteriaType;
import eu.europa.ec.fisheries.uvms.commons.message.api.MessageConstants;
import eu.europa.ec.fisheries.uvms.docker.validation.asset.AssetTestHelper;
import eu.europa.ec.fisheries.uvms.docker.validation.common.AbstractRestServiceTest;
import eu.europa.ec.fisheries.uvms.docker.validation.common.MessageHelper;
import eu.europa.ec.fisheries.uvms.docker.validation.movement.LatLong;
import eu.europa.ec.fisheries.uvms.docker.validation.system.helper.CustomRuleBuilder;
import eu.europa.ec.fisheries.uvms.docker.validation.system.helper.CustomRuleHelper;
import eu.europa.ec.fisheries.uvms.docker.validation.system.helper.FLUXHelper;
import eu.europa.ec.fisheries.uvms.exchange.model.constant.ExchangeModelConstants;
import eu.europa.ec.fisheries.uvms.exchange.model.mapper.ExchangeModuleRequestMapper;
import eu.europa.ec.fisheries.uvms.exchange.model.mapper.JAXBMarshaller;
import eu.europa.ec.fisheries.wsdl.asset.types.Asset;

public class RulesAlarmIT extends AbstractRestServiceTest {

    private static final long TIMEOUT = 10000;
    private static final String SERVICE_NAME = "eu.europa.ec.fisheries.uvms.docker.validation.system.rules.EMAIL";
    private static String emailSelector = "ServiceName='" + SERVICE_NAME + "'";
    
    @BeforeClass
    public static void registerEmailPluginIfNotExisting() throws Exception {
        String exchangeRequest = ExchangeModuleRequestMapper.createGetServiceListRequest(Arrays.asList(PluginType.EMAIL));
        Message exchangeResponse = MessageHelper.getMessageResponse(MessageConstants.QUEUE_EXCHANGE_EVENT_NAME, exchangeRequest);
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
            MessageHelper.sendToEventBus(registerRequest, ExchangeModelConstants.EXCHANGE_REGISTER_SERVICE);
            
            // Clear topic
            MessageHelper.listenOnEventBus(emailSelector, TIMEOUT);
            MessageHelper.listenOnEventBus(emailSelector, TIMEOUT);
        }
    }
    
    @Test
    public void sendEmailIfReportedSpeedIsGreaterThan10knotsTest() throws Exception {
        Date timestamp = new Date();
        
        Asset asset = AssetTestHelper.createTestAsset();
        
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
        
        TextMessage message = (TextMessage) MessageHelper.listenOnEventBus(emailSelector, TIMEOUT);
        assertThat(message, is(notNullValue()));
        
        SetCommandRequest setCommandRequest = JAXBMarshaller.unmarshallTextMessage(message, SetCommandRequest.class);
        assertThat(setCommandRequest.getCommand().getEmail().getTo(), is(email));
        assertThat(setCommandRequest.getCommand().getFwdRule(), is(createdSpeedRule.getName()));
        
        CustomRuleHelper.assertRuleTriggered(createdSpeedRule, timestamp);
        
        CustomRuleHelper.removeCustomRule(createdSpeedRule.getGuid());
    }
    
    @Test
    public void sendEmailIfReportedSpeedIslessThan10knotsTest() throws Exception {
        Date timestamp = new Date();
        
        Asset asset = AssetTestHelper.createTestAsset();
        
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
        
        TextMessage message = (TextMessage) MessageHelper.listenOnEventBus(emailSelector, TIMEOUT);
        assertThat(message, is(notNullValue()));
        
        SetCommandRequest setCommandRequest = JAXBMarshaller.unmarshallTextMessage(message, SetCommandRequest.class);
        assertThat(setCommandRequest.getCommand().getEmail().getTo(), is(email));
        assertThat(setCommandRequest.getCommand().getFwdRule(), is(createdSpeedRule.getName()));
        
        CustomRuleHelper.assertRuleTriggered(createdSpeedRule, timestamp);
        
        CustomRuleHelper.removeCustomRule(createdSpeedRule.getGuid());
    }

    @Test
    public void doNotTriggerRuleIfReportedSpeedIsLessThan10knotsTest() throws Exception {
        Date timestamp = new Date();
        
        Asset asset = AssetTestHelper.createTestAsset();
        
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
                        ConditionType.EQ, asset.getCountryCode())
                .action(ActionType.EMAIL, email)
                .build();
        
        CustomRuleType createdFsRule = CustomRuleHelper.createCustomRule(fsRule);
        assertNotNull(createdFsRule);
        
        LatLong position = new LatLong(11d, 56d, new Date());
        position.speed = 9.5;
        FLUXHelper.sendPositionToFluxPlugin(asset, position);
        
        TextMessage message = (TextMessage) MessageHelper.listenOnEventBus(emailSelector, TIMEOUT);
        assertThat(message, is(notNullValue()));
        
        SetCommandRequest setCommandRequest = JAXBMarshaller.unmarshallTextMessage(message, SetCommandRequest.class);
        assertThat(setCommandRequest.getCommand().getEmail().getTo(), is(email));
        assertThat(setCommandRequest.getCommand().getFwdRule(), is(createdFsRule.getName()));
        
        CustomRuleHelper.assertRuleNotTriggered(createdSpeedRule);
        CustomRuleHelper.assertRuleTriggered(createdFsRule, timestamp);
        
        CustomRuleHelper.removeCustomRule(createdSpeedRule.getGuid());
        CustomRuleHelper.removeCustomRule(createdFsRule.getGuid());
    }
    
    @Test
    public void doNotTriggerRuleIfReportedSpeedIsGreaterThan10knotsTest() throws Exception {
        Date timestamp = new Date();
        
        Asset asset = AssetTestHelper.createTestAsset();
        
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
                        ConditionType.EQ, asset.getCountryCode())
                .action(ActionType.EMAIL, email)
                .build();
        
        CustomRuleType createdFsRule = CustomRuleHelper.createCustomRule(fsRule);
        assertNotNull(createdFsRule);
        
        LatLong position = new LatLong(11d, 56d, new Date());
        position.speed = 10.5;
        FLUXHelper.sendPositionToFluxPlugin(asset, position);
        
        TextMessage message = (TextMessage) MessageHelper.listenOnEventBus(emailSelector, TIMEOUT);
        assertThat(message, is(notNullValue()));
        
        SetCommandRequest setCommandRequest = JAXBMarshaller.unmarshallTextMessage(message, SetCommandRequest.class);
        assertThat(setCommandRequest.getCommand().getEmail().getTo(), is(email));
        assertThat(setCommandRequest.getCommand().getFwdRule(), is(createdFsRule.getName()));
        
        CustomRuleHelper.assertRuleNotTriggered(createdSpeedRule);
        CustomRuleHelper.assertRuleTriggered(createdFsRule, timestamp);
        
        CustomRuleHelper.removeCustomRule(createdSpeedRule.getGuid());
        CustomRuleHelper.removeCustomRule(createdFsRule.getGuid());
    }
    
    @Test
    public void sendEmailIfReportedSpeedIsGreaterThanOrEqual10knotsTest() throws Exception {
        Date timestamp = new Date();
        
        Asset asset = AssetTestHelper.createTestAsset();
        
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
        
        TextMessage message = (TextMessage) MessageHelper.listenOnEventBus(emailSelector, TIMEOUT);
        assertThat(message, is(notNullValue()));
        
        SetCommandRequest setCommandRequest = JAXBMarshaller.unmarshallTextMessage(message, SetCommandRequest.class);
        assertThat(setCommandRequest.getCommand().getEmail().getTo(), is(email));
        assertThat(setCommandRequest.getCommand().getFwdRule(), is(createdSpeedRule.getName()));
        
        CustomRuleHelper.assertRuleTriggered(createdSpeedRule, timestamp);
        
        CustomRuleHelper.removeCustomRule(createdSpeedRule.getGuid());
    }
    
    @Test
    public void sendEmailIfReportedSpeedIsLessThanOrEqual10knotsTest() throws Exception {
        Date timestamp = new Date();
        
        Asset asset = AssetTestHelper.createTestAsset();
        
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
        
        TextMessage message = (TextMessage) MessageHelper.listenOnEventBus(emailSelector, TIMEOUT);
        assertThat(message, is(notNullValue()));
        
        SetCommandRequest setCommandRequest = JAXBMarshaller.unmarshallTextMessage(message, SetCommandRequest.class);
        assertThat(setCommandRequest.getCommand().getEmail().getTo(), is(email));
        assertThat(setCommandRequest.getCommand().getFwdRule(), is(createdSpeedRule.getName()));
        
        CustomRuleHelper.assertRuleTriggered(createdSpeedRule, timestamp);
        
        CustomRuleHelper.removeCustomRule(createdSpeedRule.getGuid());
    }
    
    @Test
    public void sendEmailIfReportedSpeedIsGreaterThan10knotsAndAreaIsDNKTest() throws Exception {
        Date timestamp = new Date();
        
        Asset asset = AssetTestHelper.createTestAsset();
        
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
        
        TextMessage message = (TextMessage) MessageHelper.listenOnEventBus(emailSelector, TIMEOUT);
        assertThat(message, is(notNullValue()));
        
        SetCommandRequest setCommandRequest = JAXBMarshaller.unmarshallTextMessage(message, SetCommandRequest.class);
        assertThat(setCommandRequest.getCommand().getEmail().getTo(), is(email));
        assertThat(setCommandRequest.getCommand().getFwdRule(), is(createdSpeedAndAreaRule.getName()));
        
        CustomRuleHelper.assertRuleTriggered(createdSpeedAndAreaRule, timestamp);
        
        CustomRuleHelper.removeCustomRule(createdSpeedAndAreaRule.getGuid());
    }
    
    @Test
    public void sendEmailIfReportedSpeedIsLessThan10knotsAndAreaIsDNKTest() throws Exception {
        Date timestamp = new Date();
        
        Asset asset = AssetTestHelper.createTestAsset();
        
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
        
        TextMessage message = (TextMessage) MessageHelper.listenOnEventBus(emailSelector, TIMEOUT);
        assertThat(message, is(notNullValue()));
        
        SetCommandRequest setCommandRequest = JAXBMarshaller.unmarshallTextMessage(message, SetCommandRequest.class);
        assertThat(setCommandRequest.getCommand().getEmail().getTo(), is(email));
        assertThat(setCommandRequest.getCommand().getFwdRule(), is(createdSpeedAndAreaRule.getName()));
        
        CustomRuleHelper.assertRuleTriggered(createdSpeedAndAreaRule, timestamp);
        
        CustomRuleHelper.removeCustomRule(createdSpeedAndAreaRule.getGuid());
    }
    
    @Test
    public void sendEmailIfAreaCodeIsDEUTest() throws Exception {
        Date timestamp = new Date();
        
        Asset asset = AssetTestHelper.createTestAsset();
        
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
        
        TextMessage message = (TextMessage) MessageHelper.listenOnEventBus(emailSelector, TIMEOUT);
        assertThat(message, is(notNullValue()));
        
        SetCommandRequest setCommandRequest = JAXBMarshaller.unmarshallTextMessage(message, SetCommandRequest.class);
        assertThat(setCommandRequest.getCommand().getEmail().getTo(), is(email));
        assertThat(setCommandRequest.getCommand().getFwdRule(), is(createdAreaRule.getName()));
        
        CustomRuleHelper.assertRuleTriggered(createdAreaRule, timestamp);
        
        CustomRuleHelper.removeCustomRule(createdAreaRule.getGuid());
    }
    
    @Test
    public void sendEmailIfAssetIRCSMatchesTest() throws Exception {
        Date timestamp = new Date();
        
        Asset asset = AssetTestHelper.createTestAsset();
        
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
        
        TextMessage message = (TextMessage) MessageHelper.listenOnEventBus(emailSelector, TIMEOUT);
        assertThat(message, is(notNullValue()));
        
        SetCommandRequest setCommandRequest = JAXBMarshaller.unmarshallTextMessage(message, SetCommandRequest.class);
        assertThat(setCommandRequest.getCommand().getEmail().getTo(), is(email));
        assertThat(setCommandRequest.getCommand().getFwdRule(), is(createdIrcsRule.getName()));
        
        CustomRuleHelper.assertRuleTriggered(createdIrcsRule, timestamp);
        
        CustomRuleHelper.removeCustomRule(createdIrcsRule.getGuid());
    }
    
    @Test
    public void sendEmailIfIrcsDisjunctionMatchesTest() throws Exception {
        Date timestamp = new Date();

        Asset asset1 = AssetTestHelper.createTestAsset();
        Asset asset2 = AssetTestHelper.createTestAsset();
        
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
        
        TextMessage message = (TextMessage) MessageHelper.listenOnEventBus(emailSelector, TIMEOUT);
        assertThat(message, is(notNullValue()));
        
        SetCommandRequest setCommandRequest = JAXBMarshaller.unmarshallTextMessage(message, SetCommandRequest.class);
        assertThat(setCommandRequest.getCommand().getEmail().getTo(), is(email));
        assertThat(setCommandRequest.getCommand().getFwdRule(), is(createdCustomRule.getName()));
        
        CustomRuleHelper.assertRuleTriggered(createdCustomRule, timestamp);
        
        timestamp = new Date();
        
        LatLong position2 = new LatLong(2d, 2d, new Date());
        FLUXHelper.sendPositionToFluxPlugin(asset2, position2);
        
        TextMessage message2 = (TextMessage) MessageHelper.listenOnEventBus(emailSelector, TIMEOUT);
        assertThat(message2, is(notNullValue()));
        
        SetCommandRequest setCommandRequest2 = JAXBMarshaller.unmarshallTextMessage(message2, SetCommandRequest.class);
        assertThat(setCommandRequest2.getCommand().getEmail().getTo(), is(email));
        assertThat(setCommandRequest2.getCommand().getFwdRule(), is(createdCustomRule.getName()));
        
        CustomRuleHelper.assertRuleTriggered(createdCustomRule, timestamp);
        
        CustomRuleHelper.removeCustomRule(createdCustomRule.getGuid());
    }
    
    @Test
    public void sendEmailIfIrcsCfrConjunctionMatchesTest() throws Exception {
        Date timestamp = new Date();

        Asset asset = AssetTestHelper.createTestAsset();
        
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
        
        TextMessage message = (TextMessage) MessageHelper.listenOnEventBus(emailSelector, TIMEOUT);
        assertThat(message, is(notNullValue()));
        
        SetCommandRequest setCommandRequest = JAXBMarshaller.unmarshallTextMessage(message, SetCommandRequest.class);
        assertThat(setCommandRequest.getCommand().getEmail().getTo(), is(email));
        assertThat(setCommandRequest.getCommand().getFwdRule(), is(createdCustomRule.getName()));
        
        CustomRuleHelper.assertRuleTriggered(createdCustomRule, timestamp);
        
        CustomRuleHelper.removeCustomRule(createdCustomRule.getGuid());
    }
    
    @Test
    public void doNotTriggerRuleIfIrcsCfrConjunctionNotMatchesTest() throws Exception {
        Date timestamp = new Date();
        
        Asset asset = AssetTestHelper.createTestAsset();
        
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
                        ConditionType.EQ, asset.getCountryCode())
                .action(ActionType.EMAIL, email)
                .build();
        
        CustomRuleType createdFsRule = CustomRuleHelper.createCustomRule(fsRule);
        assertNotNull(createdFsRule);
        
        LatLong position = new LatLong(1d, 1d, new Date());
        FLUXHelper.sendPositionToFluxPlugin(asset, position);
        
        TextMessage message = (TextMessage) MessageHelper.listenOnEventBus(emailSelector, TIMEOUT);
        assertThat(message, is(notNullValue()));
        
        SetCommandRequest setCommandRequest = JAXBMarshaller.unmarshallTextMessage(message, SetCommandRequest.class);
        assertThat(setCommandRequest.getCommand().getEmail().getTo(), is(email));
        assertThat(setCommandRequest.getCommand().getFwdRule(), is(createdFsRule.getName()));
        
        CustomRuleHelper.assertRuleNotTriggered(createdCustomRule);
        CustomRuleHelper.assertRuleTriggered(createdFsRule, timestamp);
        
        CustomRuleHelper.removeCustomRule(createdCustomRule.getGuid());
        CustomRuleHelper.removeCustomRule(createdFsRule.getGuid());
    }
    
    @Test
    public void sendEmailIfAssetCFRMatchesTest() throws Exception {
        Date timestamp = new Date();
        
        Asset asset = AssetTestHelper.createTestAsset();
        
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
        
        TextMessage message = (TextMessage) MessageHelper.listenOnEventBus(emailSelector, TIMEOUT);
        assertThat(message, is(notNullValue()));
        
        SetCommandRequest setCommandRequest = JAXBMarshaller.unmarshallTextMessage(message, SetCommandRequest.class);
        assertThat(setCommandRequest.getCommand().getEmail().getTo(), is(email));
        assertThat(setCommandRequest.getCommand().getFwdRule(), is(createdCfrRule.getName()));
        
        CustomRuleHelper.assertRuleTriggered(createdCfrRule, timestamp);
        
        CustomRuleHelper.removeCustomRule(createdCfrRule.getGuid());
    }
    
    @Test
    public void sendEmailIfAssetNameMatchesTest() throws Exception {
        Date timestamp = new Date();
        
        Asset asset = AssetTestHelper.createTestAsset();
        
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
        
        TextMessage message = (TextMessage) MessageHelper.listenOnEventBus(emailSelector, TIMEOUT);
        assertThat(message, is(notNullValue()));
        
        SetCommandRequest setCommandRequest = JAXBMarshaller.unmarshallTextMessage(message, SetCommandRequest.class);
        assertThat(setCommandRequest.getCommand().getEmail().getTo(), is(email));
        assertThat(setCommandRequest.getCommand().getFwdRule(), is(createdAssetNameRule.getName()));
        
        CustomRuleHelper.assertRuleTriggered(createdAssetNameRule, timestamp);
        
        CustomRuleHelper.removeCustomRule(createdAssetNameRule.getGuid());
    }
    
    @Test
    public void sendEmailIfLatitudeIsGreaterThan10Test() throws Exception {
        Date timestamp = new Date();

        Asset asset = AssetTestHelper.createTestAsset();
        
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
        
        TextMessage message = (TextMessage) MessageHelper.listenOnEventBus(emailSelector, TIMEOUT);
        assertThat(message, is(notNullValue()));
        
        SetCommandRequest setCommandRequest = JAXBMarshaller.unmarshallTextMessage(message, SetCommandRequest.class);
        assertThat(setCommandRequest.getCommand().getEmail().getTo(), is(email));
        assertThat(setCommandRequest.getCommand().getFwdRule(), is(createdCustomRule.getName()));
        
        CustomRuleHelper.assertRuleTriggered(createdCustomRule, timestamp);
        
        CustomRuleHelper.removeCustomRule(createdCustomRule.getGuid());
    }
    
    @Test
    public void sendEmailIfLongitudeIsGreaterThan10Test() throws Exception {
        Date timestamp = new Date();

        Asset asset = AssetTestHelper.createTestAsset();
        
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
        
        TextMessage message = (TextMessage) MessageHelper.listenOnEventBus(emailSelector, TIMEOUT);
        assertThat(message, is(notNullValue()));
        
        SetCommandRequest setCommandRequest = JAXBMarshaller.unmarshallTextMessage(message, SetCommandRequest.class);
        assertThat(setCommandRequest.getCommand().getEmail().getTo(), is(email));
        assertThat(setCommandRequest.getCommand().getFwdRule(), is(createdCustomRule.getName()));
        
        CustomRuleHelper.assertRuleTriggered(createdCustomRule, timestamp);
        
        CustomRuleHelper.removeCustomRule(createdCustomRule.getGuid());
    }
    
    @Test
    public void sendEmailIfPositionReportTimeIsGreaterOrEqualTest() throws Exception {
        Date timestamp = new Date();

        Asset asset = AssetTestHelper.createTestAsset();
        
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");
        formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
        
        String email = System.currentTimeMillis() + "@mail.com";
        CustomRuleType customRule = CustomRuleBuilder.getBuilder()
                .rule(CriteriaType.POSITION, SubCriteriaType.POSITION_REPORT_TIME, 
                        ConditionType.GE, formatter.format(timestamp))
                .action(ActionType.EMAIL, email)
                .build();
        
        CustomRuleType createdCustomRule = CustomRuleHelper.createCustomRule(customRule);
        assertNotNull(createdCustomRule);
        
        LatLong position = new LatLong(1d, 1d, new Date());
        FLUXHelper.sendPositionToFluxPlugin(asset, position);
        
        TextMessage message = (TextMessage) MessageHelper.listenOnEventBus(emailSelector, TIMEOUT);
        assertThat(message, is(notNullValue()));
        
        SetCommandRequest setCommandRequest = JAXBMarshaller.unmarshallTextMessage(message, SetCommandRequest.class);
        assertThat(setCommandRequest.getCommand().getEmail().getTo(), is(email));
        assertThat(setCommandRequest.getCommand().getFwdRule(), is(createdCustomRule.getName()));
        
        CustomRuleHelper.assertRuleTriggered(createdCustomRule, timestamp);
        
        CustomRuleHelper.removeCustomRule(createdCustomRule.getGuid());
    }
}
