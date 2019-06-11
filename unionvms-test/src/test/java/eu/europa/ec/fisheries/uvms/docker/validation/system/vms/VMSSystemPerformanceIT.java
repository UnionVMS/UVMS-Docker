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

import eu.europa.ec.fisheries.schema.exchange.plugin.v1.SetReportRequest;
import eu.europa.ec.fisheries.schema.movementrules.customrule.v1.*;
import eu.europa.ec.fisheries.uvms.asset.client.model.AssetDTO;
import eu.europa.ec.fisheries.uvms.docker.validation.asset.AssetTestHelper;
import eu.europa.ec.fisheries.uvms.docker.validation.common.AbstractRest;
import eu.europa.ec.fisheries.uvms.docker.validation.common.TopicListener;
import eu.europa.ec.fisheries.uvms.docker.validation.mobileterminal.MobileTerminalTestHelper;
import eu.europa.ec.fisheries.uvms.docker.validation.mobileterminal.dto.MobileTerminalDto;
import eu.europa.ec.fisheries.uvms.docker.validation.movement.LatLong;
import eu.europa.ec.fisheries.uvms.docker.validation.movement.MovementHelper;
import eu.europa.ec.fisheries.uvms.docker.validation.system.helper.CustomRuleBuilder;
import eu.europa.ec.fisheries.uvms.docker.validation.system.helper.CustomRuleHelper;
import eu.europa.ec.fisheries.uvms.docker.validation.system.helper.FLUXHelper;
import eu.europa.ec.fisheries.uvms.exchange.model.mapper.JAXBMarshaller;
import org.databene.contiperf.PerfTest;
import org.databene.contiperf.Required;
import org.databene.contiperf.junit.ContiPerfRule;
import org.junit.After;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

import javax.jms.TextMessage;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;

public class VMSSystemPerformanceIT extends AbstractRest {

    private static final String SELECTOR = "ServiceName='eu.europa.ec.fisheries.uvms.plugins.flux.movement'";

    private static final int NUMBER_OF_POSITIONS = 10;
    
    @Rule
    public ContiPerfRule contiPerfRule = new ContiPerfRule();
    
    @After
    public void removeCustomRules() {
        CustomRuleHelper.removeCustomRulesByDefaultUser();
    }
    
    @Test
    @PerfTest(threads = 1)
    @Required(max = 45000)
    public void createPositionAndTriggerRulePerformanceTest() throws Exception {
        AssetDTO asset = AssetTestHelper.createTestAsset();
        MobileTerminalDto mobileTerminal = MobileTerminalTestHelper.createMobileTerminal();
        MobileTerminalTestHelper.assignMobileTerminal(asset, mobileTerminal);

        String fluxEndpoint = "DNK";
        
        CustomRuleType flagStateRule = CustomRuleBuilder.getBuilder()
                .setName("Flag state => FLUX DNK")
                .rule(CriteriaType.ASSET, SubCriteriaType.FLAG_STATE, 
                        ConditionType.EQ, asset.getFlagStateCode())
                .action(ActionType.SEND_TO_FLUX, fluxEndpoint)
                .build();

        CustomRuleType createdCustomRule = CustomRuleHelper.createCustomRule(flagStateRule);
        assertNotNull(createdCustomRule);

        List<LatLong> postitions = new MovementHelper().createRuttGeneric(57d, 11d, 58d, 1d, NUMBER_OF_POSITIONS);

        try (TopicListener topicListener = new TopicListener(SELECTOR)) {
            for (LatLong position : postitions) {
                FLUXHelper.sendPositionToFluxPlugin(asset, position);
            }
            int receivedReports = 0;
            while (receivedReports < postitions.size()) {
                TextMessage message = (TextMessage) topicListener.listenOnEventBus();
                assertThat(message, is(notNullValue()));

                SetReportRequest setReportRequest = JAXBMarshaller.unmarshallTextMessage(message, SetReportRequest.class);

                assertThat(setReportRequest.getReport().getRecipient(), is(fluxEndpoint));
                assertThat(setReportRequest.getReport().getMovement().getAssetName(), is(asset.getName()));
                assertThat(setReportRequest.getReport().getMovement().getIrcs(), is(asset.getIrcs()));
                receivedReports++;
            }
        }      
        CustomRuleHelper.removeCustomRule(createdCustomRule.getGuid());
    }
    
    @Ignore
    @Test
    @PerfTest(threads = 1)
    public void createPositionAndTriggerRulePerformanceTestTenShips() throws Exception {
        // Create rule
        String flagStateCode = "SWE";
        String fluxEndpoint = "DNK";
        
        CustomRuleType flagStateRule = CustomRuleBuilder.getBuilder()
                .setName("Flag state => FLUX DNK")
                .rule(CriteriaType.ASSET, SubCriteriaType.FLAG_STATE, 
                        ConditionType.EQ, flagStateCode)
                .action(ActionType.SEND_TO_FLUX, fluxEndpoint)
                .build();

        CustomRuleType createdCustomRule = CustomRuleHelper.createCustomRule(flagStateRule);
        assertNotNull(createdCustomRule);

        try (TopicListener topicListener = new TopicListener(SELECTOR)) {
            int numberOfShips = 10;
            for (int i = 0; i < numberOfShips; i++) {
                AssetDTO asset = AssetTestHelper.createBasicAsset();
                asset.setFlagStateCode(flagStateCode);
                AssetDTO createdAsset = AssetTestHelper.createAsset(asset);

                List<LatLong> postitions = new MovementHelper().createRuttGeneric(57d, 11d, 58d, 1d, NUMBER_OF_POSITIONS);

                for (LatLong position : postitions) {
                    FLUXHelper.sendPositionToFluxPlugin(createdAsset, position);
                }
            }
            int receivedReports = 0;
            while (receivedReports < numberOfShips * NUMBER_OF_POSITIONS) {
                TextMessage message = (TextMessage) topicListener.listenOnEventBus();
                assertThat(message, is(notNullValue()));

                SetReportRequest setReportRequest = JAXBMarshaller.unmarshallTextMessage(message, SetReportRequest.class);

                assertThat(setReportRequest.getReport().getRecipient(), is(fluxEndpoint));
                receivedReports++;
            }
        }
        CustomRuleHelper.removeCustomRule(createdCustomRule.getGuid());
    }
}
