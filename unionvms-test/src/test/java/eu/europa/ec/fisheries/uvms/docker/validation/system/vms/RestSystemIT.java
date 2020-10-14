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

import eu.europa.ec.fisheries.schema.movementrules.customrule.v1.*;
import eu.europa.ec.fisheries.uvms.asset.client.model.AssetDTO;
import eu.europa.ec.fisheries.uvms.docker.validation.asset.AssetTestHelper;
import eu.europa.ec.fisheries.uvms.docker.validation.common.AbstractRest;
import eu.europa.ec.fisheries.uvms.docker.validation.movement.LatLong;
import eu.europa.ec.fisheries.uvms.docker.validation.system.helper.*;
import eu.europa.ec.fisheries.uvms.docker.validation.user.UserHelper;
import eu.europa.ec.fisheries.uvms.docker.validation.user.dto.Channel;
import eu.europa.ec.fisheries.uvms.docker.validation.user.dto.EndPoint;
import eu.europa.ec.fisheries.uvms.docker.validation.user.dto.Organisation;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.After;
import org.junit.Test;
import un.unece.uncefact.data.standard.fluxvesselpositionmessage._4.FLUXVesselPositionMessage;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._18.VesselGeographicalCoordinateType;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._18.VesselPositionEventType;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._18.VesselTransportMeansType;
import un.unece.uncefact.data.standard.unqualifieddatatype._18.IDType;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.time.Instant;
import java.util.Date;
import java.util.Enumeration;
import java.util.Map;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertNotNull;

public class RestSystemIT extends AbstractRest {

    @After
    public void removeCustomRules() {
        CustomRuleHelper.removeCustomRulesByDefaultUser();
    }
    
    @Test
    public void sendPosition() throws IOException, Exception {
        Organisation organisation = createOrganisation();

        AssetDTO asset = AssetTestHelper.createTestAsset();
        
        CustomRuleType flagStateRule = CustomRuleBuilder.getBuilder()
                .rule(CriteriaType.AREA, SubCriteriaType.AREA_CODE, 
                        ConditionType.EQ, "NOR")
                .action(ActionType.SEND_REPORT, VMSSystemHelper.REST_NAME, organisation.getName())
                .build();
        
        CustomRuleType createdCustomRule = CustomRuleHelper.createCustomRule(flagStateRule);
        assertNotNull(createdCustomRule);
        
        LatLong position = new LatLong(58.973, 5.781, Date.from(Instant.now()));
        position.speed = 5;
        position.bearing = 123;
        
        FLUXVesselPositionMessage positionMessage;
        try (RESTEndpoint restEndpoint = new RESTEndpoint(RESTEndpoint.ENDPOINT_PORT)) {
            FLUXHelper.sendPositionToFluxPlugin(asset, position);
            positionMessage = restEndpoint.getMessage(10000);
        }
        
        VesselTransportMeansType vesselTransportMeans = positionMessage.getVesselTransportMeans();
        assertThat(vesselTransportMeans, is(CoreMatchers.notNullValue()));
        assertThat(vesselTransportMeans.getRegistrationVesselCountry().getID().getValue(), is(asset.getFlagStateCode()));

        Map<String, String> assetIds = vesselTransportMeans.getIDS().stream().collect(Collectors.toMap(IDType::getSchemeID, IDType::getValue));
        assertThat(assetIds.get("CFR"), is(asset.getCfr()));
        assertThat(assetIds.get("IRCS"), is(asset.getIrcs()));
        assertThat(assetIds.get("EXT_MARK"), is(asset.getExternalMarking()));

        assertThat(vesselTransportMeans.getSpecifiedVesselPositionEvents().size(), is(1));
        VesselPositionEventType positionEvent = vesselTransportMeans.getSpecifiedVesselPositionEvents().get(0);
        assertThat(positionEvent.getObtainedOccurrenceDateTime().getDateTime().toGregorianCalendar().getTime(), is(position.positionTime));
        assertThat(positionEvent.getTypeCode().getValue(), is("POS"));
        assertThat(positionEvent.getSpeedValueMeasure().getValue().doubleValue(), is(position.speed));
        assertThat(positionEvent.getCourseValueMeasure().getValue().doubleValue(), is(position.bearing));
        VesselGeographicalCoordinateType vesselCoordinates = positionEvent.getSpecifiedVesselGeographicalCoordinate();
        assertThat(vesselCoordinates.getLatitudeMeasure().getValue().doubleValue(), is(position.latitude));
        assertThat(vesselCoordinates.getLongitudeMeasure().getValue().doubleValue(), is(position.longitude));

    }

    private Organisation createOrganisation() throws SocketException {
        Organisation organisation = UserHelper.getBasicOrganisation();
        organisation.setNation("SWE");
        UserHelper.createOrganisation(organisation);
        EndPoint endpoint = new EndPoint();
        endpoint.setName("REST");
        endpoint.setURI("http://" + getDockerHostIp() + ":"+RESTEndpoint.ENDPOINT_PORT+"/");
        endpoint.setStatus("E");
        endpoint.setOrganisationName(organisation.getName());
        EndPoint createdEndpoint = UserHelper.createEndpoint(endpoint);
        Channel channel = new Channel();
        channel.setDataflow("REST");
        channel.setService("REST");
        channel.setPriority(1);
        channel.setEndpointId(createdEndpoint.getEndpointId());
        UserHelper.createChannel(channel);
        return organisation;
    }

    // Find docker host machine ip. Replace this with 'host.docker.internal' when supported on Linux.
    private String getDockerHostIp() throws SocketException {
        Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
        while (interfaces.hasMoreElements()) {
            NetworkInterface e = interfaces.nextElement();
            Enumeration<InetAddress> inetAddresses = e.getInetAddresses();
            while (inetAddresses.hasMoreElements()) {
                InetAddress inetAddress = inetAddresses.nextElement();
                if (inetAddress.getHostAddress().startsWith("172")) {
                    return inetAddress.getHostAddress();
                }
            }
        }
        return "host.docker.internal";
    }
}
