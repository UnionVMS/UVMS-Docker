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
import java.io.IOException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import org.junit.After;
import org.junit.Ignore;
import org.junit.Test;
import eu.europa.ec.fisheries.schema.exchange.movement.v1.MovementTypeType;
import eu.europa.ec.fisheries.schema.movementrules.customrule.v1.ActionType;
import eu.europa.ec.fisheries.schema.movementrules.customrule.v1.ConditionType;
import eu.europa.ec.fisheries.schema.movementrules.customrule.v1.CriteriaType;
import eu.europa.ec.fisheries.schema.movementrules.customrule.v1.CustomRuleType;
import eu.europa.ec.fisheries.schema.movementrules.customrule.v1.SubCriteriaType;
import eu.europa.ec.fisheries.uvms.asset.client.model.AssetDTO;
import eu.europa.ec.fisheries.uvms.docker.validation.asset.AssetTestHelper;
import eu.europa.ec.fisheries.uvms.docker.validation.common.AbstractRest;
import eu.europa.ec.fisheries.uvms.docker.validation.movement.LatLong;
import eu.europa.ec.fisheries.uvms.docker.validation.system.helper.CustomRuleBuilder;
import eu.europa.ec.fisheries.uvms.docker.validation.system.helper.CustomRuleHelper;
import eu.europa.ec.fisheries.uvms.docker.validation.system.helper.NAFHelper;
import eu.europa.ec.fisheries.uvms.docker.validation.system.helper.NafEndpoint;
import eu.europa.ec.fisheries.uvms.docker.validation.user.UserHelper;
import eu.europa.ec.fisheries.uvms.docker.validation.user.dto.Channel;
import eu.europa.ec.fisheries.uvms.docker.validation.user.dto.EndPoint;
import eu.europa.ec.fisheries.uvms.docker.validation.user.dto.Organisation;

public class NAFSystemIT extends AbstractRest {

    public static int ENDPOINT_PORT = 29001;
    
    @After
    public void removeCustomRules() {
        CustomRuleHelper.removeCustomRulesByDefaultUser();
    }
    
    @Test
    @Ignore("Ignoring this test until docker on jenkins supports 'host.docker.internal'")
    public void sendPositionToNorwayAndVerifyMandatoryFields() throws IOException, Exception {
        Organisation organisation = createOrganisation();
        
        CustomRuleType flagStateRule = CustomRuleBuilder.getBuilder()
                .setName("Area NOR => Send to NOR")
                .rule(CriteriaType.AREA, SubCriteriaType.AREA_CODE, 
                        ConditionType.EQ, "NOR")
                .action(ActionType.SEND_TO_NAF, organisation.getName())
                .build();
        
        CustomRuleType createdCustomRule = CustomRuleHelper.createCustomRule(flagStateRule);
        assertNotNull(createdCustomRule);
        
        AssetDTO asset = AssetTestHelper.createTestAsset();
        LatLong position = new LatLong(58.973, 5.781, new Date());
        position.speed = 5;
        
        String message;
        try (NafEndpoint nafEndpoint = new NafEndpoint(ENDPOINT_PORT)) {
            NAFHelper.sendPositionToNAFPlugin(position, asset);
            message = nafEndpoint.getMessage(10000);
        }
        
        assertThat(NAFHelper.readCodeValue("AD", message), is(organisation.getNation()));
//        assertThat(NAFHelper.readCodeValue("FR", message), is("SWE");
        assertThat(NAFHelper.readCodeValue("TM", message), is(MovementTypeType.POS.toString()));
        assertThat(NAFHelper.readCodeValue("RC", message), is(asset.getIrcs()));
        assertThat(NAFHelper.readCodeValue("LT", message), is(String.valueOf(position.latitude)));
        assertThat(NAFHelper.readCodeValue("LG", message), is(String.valueOf(position.longitude)));
        assertThat(NAFHelper.readCodeValue("SP", message), is(String.valueOf((int)position.speed * 10)));
        assertThat(NAFHelper.readCodeValue("CO", message), is(String.valueOf((int)position.bearing)));
        ZonedDateTime positionTime = ZonedDateTime.ofInstant(position.positionTime.toInstant(), ZoneId.of("UTC"));
        assertThat(NAFHelper.readCodeValue("DA", message), is(positionTime.format(DateTimeFormatter.ofPattern("yyyyMMdd"))));
        assertThat(NAFHelper.readCodeValue("TI", message), is(positionTime.format(DateTimeFormatter.ofPattern("HHmm"))));
    }

    private Organisation createOrganisation() {
        Organisation organisation = UserHelper.getBasicOrganisation();
        organisation.setNation(generateARandomStringWithMaxLength(9));
        UserHelper.createOrganisation(organisation);
        EndPoint endpoint = new EndPoint();
        endpoint.setName("NAF");
        endpoint.setURI("http://host.docker.internal:"+ENDPOINT_PORT+"/naf/message/#MESSAGE#");
        endpoint.setStatus("E");
        endpoint.setOrganisationName(organisation.getName());
        EndPoint createdEndpoint = UserHelper.createEndpoint(endpoint);
        Channel channel = new Channel();
        channel.setDataflow("NAF");
        channel.setService("NAF");
        channel.setPriority(1);
        channel.setEndpointId(createdEndpoint.getEndpointId());
        UserHelper.createChannel(channel);
        return organisation;
    }
}
