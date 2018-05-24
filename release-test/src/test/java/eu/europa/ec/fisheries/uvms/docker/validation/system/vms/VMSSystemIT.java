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
import java.util.Date;
import javax.jms.TextMessage;
import org.junit.Test;
import eu.europa.ec.fisheries.schema.exchange.movement.v1.MovementType;
import eu.europa.ec.fisheries.schema.exchange.plugin.v1.SetReportRequest;
import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.MobileTerminalType;
import eu.europa.ec.fisheries.schema.rules.customrule.v1.ActionType;
import eu.europa.ec.fisheries.schema.rules.customrule.v1.ConditionType;
import eu.europa.ec.fisheries.schema.rules.customrule.v1.CriteriaType;
import eu.europa.ec.fisheries.schema.rules.customrule.v1.CustomRuleType;
import eu.europa.ec.fisheries.schema.rules.customrule.v1.SubCriteriaType;
import eu.europa.ec.fisheries.uvms.docker.validation.asset.AssetTestHelper;
import eu.europa.ec.fisheries.uvms.docker.validation.common.AbstractRestServiceTest;
import eu.europa.ec.fisheries.uvms.docker.validation.common.MessageHelper;
import eu.europa.ec.fisheries.uvms.docker.validation.mobileterminal.MobileTerminalTestHelper;
import eu.europa.ec.fisheries.uvms.docker.validation.movement.LatLong;
import eu.europa.ec.fisheries.uvms.docker.validation.system.helper.CustomRuleBuilder;
import eu.europa.ec.fisheries.uvms.docker.validation.system.helper.CustomRuleHelper;
import eu.europa.ec.fisheries.uvms.docker.validation.system.helper.FLUXHelper;
import eu.europa.ec.fisheries.uvms.exchange.model.mapper.JAXBMarshaller;
import eu.europa.ec.fisheries.wsdl.asset.types.Asset;

public class VMSSystemIT extends AbstractRestServiceTest {

    private static final String SELECTOR = "ServiceName='eu.europa.ec.fisheries.uvms.plugins.flux'";
    private static final long TIMEOUT = 10000;

    @Test
    public void sendFSToFLUXDNKTest() throws Exception {

        Date timestamp = new Date();
        
        Asset asset = AssetTestHelper.createTestAsset();
        MobileTerminalType mobileTerminalType = MobileTerminalTestHelper.createMobileTerminalType();
        MobileTerminalTestHelper.assignMobileTerminal(asset, mobileTerminalType);
        
        String fluxEndpoint = "DNK";

        CustomRuleType flagStateRule = CustomRuleBuilder.getBuilder()
                .setName("Flag state => FLUX DNK")
                .rule(CriteriaType.ASSET, SubCriteriaType.FLAG_STATE, 
                        ConditionType.EQ, asset.getCountryCode())
                .action(ActionType.SEND_TO_FLUX, fluxEndpoint)
                .build();
        
        CustomRuleType createdCustomRule = CustomRuleHelper.createCustomRule(flagStateRule);
        assertNotNull(createdCustomRule);
        
        LatLong position = new LatLong(11d, 56d, new Date());
        
        FLUXHelper.sendPositionToFluxPlugin(asset, position);
        
        TextMessage message = (TextMessage) MessageHelper.listenOnEventBus(SELECTOR, TIMEOUT);
        assertThat(message, is(notNullValue()));
        
        CustomRuleHelper.assertRuleTriggered(createdCustomRule, timestamp);
        
        SetReportRequest setReportRequest = JAXBMarshaller.unmarshallTextMessage(message, SetReportRequest.class);
        
        assertThat(setReportRequest.getReport().getRecipient(), is(fluxEndpoint));
        
        MovementType movement = setReportRequest.getReport().getMovement();
        assertThat(movement.getAssetName(), is(asset.getName()));
        assertThat(movement.getIrcs(), is(asset.getIrcs()));
        assertThat(movement.getPosition().getLatitude(), is(position.latitude));
        assertThat(movement.getPosition().getLongitude(), is(position.longitude));
        
        CustomRuleHelper.removeCustomRule(createdCustomRule.getGuid());
    }
    
    @Test
    public void sendFSAndDNKAreaToFLUXDNKTest() throws Exception {

        Date timestamp = new Date();
        
        Asset asset = AssetTestHelper.createTestAsset();
        MobileTerminalType mobileTerminalType = MobileTerminalTestHelper.createMobileTerminalType();
        MobileTerminalTestHelper.assignMobileTerminal(asset, mobileTerminalType);
        
        String areaCode = "DNK";
        String fluxEndpoint = "DNK";
        
        CustomRuleType flagStateAndAreaRule = CustomRuleBuilder.getBuilder()
                .setName("Flag state && Area => FLUX DNK")
                .rule(CriteriaType.ASSET, SubCriteriaType.FLAG_STATE, 
                        ConditionType.EQ, asset.getCountryCode())
                .and(CriteriaType.AREA, SubCriteriaType.AREA_CODE, 
                        ConditionType.EQ, areaCode)
                .action(ActionType.SEND_TO_FLUX, fluxEndpoint)
                .build();
        CustomRuleType createdCustomRule = CustomRuleHelper.createCustomRule(flagStateAndAreaRule);
        assertNotNull(createdCustomRule);
        
        LatLong position = new LatLong(56d, 10.5, new Date());
        
        FLUXHelper.sendPositionToFluxPlugin(asset, position);
        
        TextMessage message = (TextMessage) MessageHelper.listenOnEventBus(SELECTOR, TIMEOUT);
        assertThat(message, is(notNullValue()));
        
        CustomRuleHelper.assertRuleTriggered(createdCustomRule, timestamp);
        
        SetReportRequest setReportRequest = JAXBMarshaller.unmarshallTextMessage(message, SetReportRequest.class);
        
        assertThat(setReportRequest.getReport().getRecipient(), is(fluxEndpoint));
        
        MovementType movement = setReportRequest.getReport().getMovement();
        assertThat(movement.getAssetName(), is(asset.getName()));
        assertThat(movement.getIrcs(), is(asset.getIrcs()));
        assertThat(movement.getPosition().getLatitude(), is(position.latitude));
        assertThat(movement.getPosition().getLongitude(), is(position.longitude));
        
        CustomRuleHelper.removeCustomRule(createdCustomRule.getGuid());
    }
}