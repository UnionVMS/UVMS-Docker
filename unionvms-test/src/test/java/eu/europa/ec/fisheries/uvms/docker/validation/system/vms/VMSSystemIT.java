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
package eu.europa.ec.fisheries.uvms.docker.validation.system.vms;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Calendar;
import java.util.Date;
import javax.jms.JMSException;
import javax.jms.TextMessage;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import eu.europa.ec.fisheries.schema.exchange.movement.v1.MovementType;
import eu.europa.ec.fisheries.schema.exchange.plugin.v1.SetReportRequest;
import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.MobileTerminalType;
import eu.europa.ec.fisheries.schema.movementrules.customrule.v1.ActionType;
import eu.europa.ec.fisheries.schema.movementrules.customrule.v1.ConditionType;
import eu.europa.ec.fisheries.schema.movementrules.customrule.v1.CriteriaType;
import eu.europa.ec.fisheries.schema.movementrules.customrule.v1.CustomRuleType;
import eu.europa.ec.fisheries.schema.movementrules.customrule.v1.SubCriteriaType;
import eu.europa.ec.fisheries.uvms.asset.client.model.AssetDTO;
import eu.europa.ec.fisheries.uvms.docker.validation.asset.AssetTestHelper;
import eu.europa.ec.fisheries.uvms.docker.validation.common.AbstractRest;
import eu.europa.ec.fisheries.uvms.docker.validation.common.MessageHelper;
import eu.europa.ec.fisheries.uvms.docker.validation.mobileterminal.MobileTerminalTestHelper;
import eu.europa.ec.fisheries.uvms.docker.validation.movement.LatLong;
import eu.europa.ec.fisheries.uvms.docker.validation.system.helper.CustomRuleBuilder;
import eu.europa.ec.fisheries.uvms.docker.validation.system.helper.CustomRuleHelper;
import eu.europa.ec.fisheries.uvms.docker.validation.system.helper.FLUXHelper;
import eu.europa.ec.fisheries.uvms.exchange.model.mapper.JAXBMarshaller;

public class VMSSystemIT extends AbstractRest {

    private static final String SELECTOR = "ServiceName='eu.europa.ec.fisheries.uvms.plugins.flux.movement'";
    private static final long TIMEOUT = 10000;

    private static MessageHelper messageHelper;

    @BeforeClass
    public static void setup() throws JMSException {
        messageHelper = new MessageHelper();
    }

    @AfterClass
    public static void cleanup() {
        messageHelper.close();
    }

    @After
    public void removeCustomRules() throws Exception {
        CustomRuleHelper.removeCustomRulesByDefaultUser();
    }
    
    @Test
    public void sendFlagStateToFLUXDNKTest() throws Exception {
        LocalDateTime timestamp = LocalDateTime.now(ZoneOffset.UTC);


        AssetDTO asset = AssetTestHelper.createTestAsset();
        MobileTerminalType mobileTerminalType = MobileTerminalTestHelper.createMobileTerminalType();
        MobileTerminalTestHelper.assignMobileTerminal(asset, mobileTerminalType);
        
        String fluxEndpoint = "DNK";

        CustomRuleType flagStateRule = CustomRuleBuilder.getBuilder()
                .setName("Flag state => FLUX DNK")
                .rule(CriteriaType.ASSET, SubCriteriaType.FLAG_STATE, 
                        ConditionType.EQ, asset.getFlagStateCode())
                .action(ActionType.SEND_TO_FLUX, fluxEndpoint)
                .build();
        
        CustomRuleType createdCustomRule = CustomRuleHelper.createCustomRule(flagStateRule);
        assertNotNull(createdCustomRule);
        
        LatLong position = new LatLong(11d, 56d, new Date());
        
        FLUXHelper.sendPositionToFluxPlugin(asset, position);
        
        TextMessage message = (TextMessage) messageHelper.listenOnEventBus(SELECTOR, TIMEOUT);
        assertThat(message, is(notNullValue()));
        
        CustomRuleHelper.assertRuleTriggered(createdCustomRule, timestamp);
        
        SetReportRequest setReportRequest = JAXBMarshaller.unmarshallTextMessage(message, SetReportRequest.class);
        
        assertThat(setReportRequest.getReport().getRecipient(), is(fluxEndpoint));
        
        MovementType movement = setReportRequest.getReport().getMovement();
        assertThat(movement.getAssetName(), is(asset.getName()));
        assertThat(movement.getIrcs(), is(asset.getIrcs()));
        assertThat(movement.getPosition().getLatitude(), is(position.latitude));
        assertThat(movement.getPosition().getLongitude(), is(position.longitude));
    }
    
    @Test
    public void sendFlagStateAndAreaDNKToFLUXDNKTest() throws Exception {
        LocalDateTime timestamp = LocalDateTime.now(ZoneOffset.UTC);

        AssetDTO asset = AssetTestHelper.createTestAsset();
        MobileTerminalType mobileTerminalType = MobileTerminalTestHelper.createMobileTerminalType();
        MobileTerminalTestHelper.assignMobileTerminal(asset, mobileTerminalType);
        
        String areaCode = "DNK";
        String fluxEndpoint = "DNK";
        
        CustomRuleType flagStateAndAreaRule = CustomRuleBuilder.getBuilder()
                .setName("Flag state && Area => FLUX DNK")
                .rule(CriteriaType.ASSET, SubCriteriaType.FLAG_STATE, 
                        ConditionType.EQ, asset.getFlagStateCode())
                .and(CriteriaType.AREA, SubCriteriaType.AREA_CODE, 
                        ConditionType.EQ, areaCode)
                .action(ActionType.SEND_TO_FLUX, fluxEndpoint)
                .build();
        CustomRuleType createdCustomRule = CustomRuleHelper.createCustomRule(flagStateAndAreaRule);
        assertNotNull(createdCustomRule);
        
        LatLong position = new LatLong(56d, 10.5, new Date());
        
        FLUXHelper.sendPositionToFluxPlugin(asset, position);
        
        TextMessage message = (TextMessage) messageHelper.listenOnEventBus(SELECTOR, TIMEOUT);
        assertThat(message, is(notNullValue()));
        
        CustomRuleHelper.assertRuleTriggered(createdCustomRule, timestamp);
        
        SetReportRequest setReportRequest = JAXBMarshaller.unmarshallTextMessage(message, SetReportRequest.class);
        
        assertThat(setReportRequest.getReport().getRecipient(), is(fluxEndpoint));
        
        MovementType movement = setReportRequest.getReport().getMovement();
        assertThat(movement.getAssetName(), is(asset.getName()));
        assertThat(movement.getIrcs(), is(asset.getIrcs()));
        assertThat(movement.getPosition().getLatitude(), is(position.latitude));
        assertThat(movement.getPosition().getLongitude(), is(position.longitude));
    }
    
    @Test
    public void sendFlagStateToFLUXDNKWithRuleIntervalTest() throws Exception {
        LocalDateTime timestamp = LocalDateTime.now(ZoneOffset.UTC);

        AssetDTO asset = AssetTestHelper.createTestAsset();
        MobileTerminalType mobileTerminalType = MobileTerminalTestHelper.createMobileTerminalType();
        MobileTerminalTestHelper.assignMobileTerminal(asset, mobileTerminalType);
        
        String fluxEndpoint = "DNK";

        Calendar calendarStart = Calendar.getInstance();
        calendarStart.add(Calendar.HOUR, -1);
        Date ruleIntervalStart = calendarStart.getTime();
        
        Calendar calendarEnd = Calendar.getInstance();
        calendarEnd.add(Calendar.HOUR, 1);
        Date ruleIntervalEnd = calendarEnd.getTime();
        
        CustomRuleType flagStateRuleWithInterval = CustomRuleBuilder.getBuilder()
                .setName("Flag state => FLUX DNK")
                .rule(CriteriaType.ASSET, SubCriteriaType.FLAG_STATE, 
                        ConditionType.EQ, asset.getFlagStateCode())
                .interval(ruleIntervalStart, ruleIntervalEnd)
                .action(ActionType.SEND_TO_FLUX, fluxEndpoint)
                .build();
        
        CustomRuleType createdCustomRule = CustomRuleHelper.createCustomRule(flagStateRuleWithInterval);
        assertNotNull(createdCustomRule);
        
        LatLong position = new LatLong(11d, 56d, new Date());
        
        FLUXHelper.sendPositionToFluxPlugin(asset, position);
        
        TextMessage message = (TextMessage) messageHelper.listenOnEventBus(SELECTOR, TIMEOUT);
        assertThat(message, is(notNullValue()));
        
        CustomRuleHelper.assertRuleTriggered(createdCustomRule, timestamp);
        
        SetReportRequest setReportRequest = JAXBMarshaller.unmarshallTextMessage(message, SetReportRequest.class);
        
        assertThat(setReportRequest.getReport().getRecipient(), is(fluxEndpoint));
        
        MovementType movement = setReportRequest.getReport().getMovement();
        assertThat(movement.getAssetName(), is(asset.getName()));
        assertThat(movement.getIrcs(), is(asset.getIrcs()));
        assertThat(movement.getPosition().getLatitude(), is(position.latitude));
        assertThat(movement.getPosition().getLongitude(), is(position.longitude));
    }
    
    @Test
    public void sendFlagStateToFLUXDNKWithPastValidRuleIntervalTest() throws Exception {
        LocalDateTime timestamp = LocalDateTime.now(ZoneOffset.UTC);

        AssetDTO asset = AssetTestHelper.createTestAsset();
        MobileTerminalType mobileTerminalType = MobileTerminalTestHelper.createMobileTerminalType();
        MobileTerminalTestHelper.assignMobileTerminal(asset, mobileTerminalType);
        
        String fluxEndpoint = "DNK";

        Calendar calendarStart = Calendar.getInstance();
        calendarStart.add(Calendar.HOUR, -2);
        Date ruleIntervalStart = calendarStart.getTime();
        
        Calendar calendarEnd = Calendar.getInstance();
        calendarEnd.add(Calendar.HOUR, -1);
        Date ruleIntervalEnd = calendarEnd.getTime();
        
        CustomRuleType flagStateRuleWithInterval = CustomRuleBuilder.getBuilder()
                .setName("Flag state => FLUX DNK")
                .rule(CriteriaType.ASSET, SubCriteriaType.FLAG_STATE, 
                        ConditionType.EQ, asset.getFlagStateCode())
                .interval(ruleIntervalStart, ruleIntervalEnd)
                .action(ActionType.SEND_TO_FLUX, fluxEndpoint)
                .build();
        
        CustomRuleType createdCustomRuleWithInterval = CustomRuleHelper.createCustomRule(flagStateRuleWithInterval);
        assertNotNull(createdCustomRuleWithInterval);
        
        CustomRuleType flagStateRuleWithoutInterval = CustomRuleBuilder.getBuilder()
                .setName("Flag state => FLUX DNK")
                .rule(CriteriaType.ASSET, SubCriteriaType.FLAG_STATE, 
                        ConditionType.EQ, asset.getFlagStateCode())
                .action(ActionType.SEND_TO_FLUX, fluxEndpoint)
                .build();
        
        CustomRuleType createdCustomRuleWithoutInterval = CustomRuleHelper.createCustomRule(flagStateRuleWithoutInterval);
        assertNotNull(createdCustomRuleWithoutInterval);
        
        LatLong position = new LatLong(11d, 56d, new Date());
        
        FLUXHelper.sendPositionToFluxPlugin(asset, position);
        
        TextMessage message = (TextMessage) messageHelper.listenOnEventBus(SELECTOR, TIMEOUT);
        assertThat(message, is(notNullValue()));
        
        CustomRuleHelper.assertRuleNotTriggered(createdCustomRuleWithInterval);
        CustomRuleHelper.assertRuleTriggered(createdCustomRuleWithoutInterval, timestamp);
    }
    
    @Test
    public void sendFlagStateToFLUXDNKWithFutureValidRuleIntervalTest() throws Exception {
        LocalDateTime timestamp = LocalDateTime.now(ZoneOffset.UTC);

        AssetDTO asset = AssetTestHelper.createTestAsset();
        MobileTerminalType mobileTerminalType = MobileTerminalTestHelper.createMobileTerminalType();
        MobileTerminalTestHelper.assignMobileTerminal(asset, mobileTerminalType);
        
        String fluxEndpoint = "DNK";

        Calendar calendarStart = Calendar.getInstance();
        calendarStart.add(Calendar.HOUR, 1);
        Date ruleIntervalStart = calendarStart.getTime();
        
        Calendar calendarEnd = Calendar.getInstance();
        calendarEnd.add(Calendar.HOUR, 2);
        Date ruleIntervalEnd = calendarEnd.getTime();
        
        CustomRuleType flagStateRuleWithInterval = CustomRuleBuilder.getBuilder()
                .setName("Flag state => FLUX DNK")
                .rule(CriteriaType.ASSET, SubCriteriaType.FLAG_STATE, 
                        ConditionType.EQ, asset.getFlagStateCode())
                .interval(ruleIntervalStart, ruleIntervalEnd)
                .action(ActionType.SEND_TO_FLUX, fluxEndpoint)
                .build();
        
        CustomRuleType createdCustomRuleWithInterval = CustomRuleHelper.createCustomRule(flagStateRuleWithInterval);
        assertNotNull(createdCustomRuleWithInterval);
        
        CustomRuleType flagStateRuleWithoutInterval = CustomRuleBuilder.getBuilder()
                .setName("Flag state => FLUX DNK")
                .rule(CriteriaType.ASSET, SubCriteriaType.FLAG_STATE, 
                        ConditionType.EQ, asset.getFlagStateCode())
                .action(ActionType.SEND_TO_FLUX, fluxEndpoint)
                .build();
        
        CustomRuleType createdCustomRuleWithoutInterval = CustomRuleHelper.createCustomRule(flagStateRuleWithoutInterval);
        assertNotNull(createdCustomRuleWithoutInterval);
        
        LatLong position = new LatLong(11d, 56d, new Date());
        
        FLUXHelper.sendPositionToFluxPlugin(asset, position);
        
        TextMessage message = (TextMessage) messageHelper.listenOnEventBus(SELECTOR, TIMEOUT);
        assertThat(message, is(notNullValue()));
        
        CustomRuleHelper.assertRuleNotTriggered(createdCustomRuleWithInterval);
        CustomRuleHelper.assertRuleTriggered(createdCustomRuleWithoutInterval, timestamp);
    }
}