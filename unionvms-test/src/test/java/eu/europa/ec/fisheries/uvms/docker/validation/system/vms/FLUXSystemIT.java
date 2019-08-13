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
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.datatype.DatatypeConfigurationException;
import org.joda.time.Instant;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.w3c.dom.Element;
import eu.europa.ec.fisheries.schema.config.types.v1.SettingType;
import eu.europa.ec.fisheries.schema.movement.v1.MovementTypeType;
import eu.europa.ec.fisheries.schema.movementrules.customrule.v1.ActionType;
import eu.europa.ec.fisheries.schema.movementrules.customrule.v1.ConditionType;
import eu.europa.ec.fisheries.schema.movementrules.customrule.v1.CriteriaType;
import eu.europa.ec.fisheries.schema.movementrules.customrule.v1.CustomRuleType;
import eu.europa.ec.fisheries.schema.movementrules.customrule.v1.SubCriteriaType;
import eu.europa.ec.fisheries.uvms.asset.client.model.AssetDTO;
import eu.europa.ec.fisheries.uvms.docker.validation.asset.AssetTestHelper;
import eu.europa.ec.fisheries.uvms.docker.validation.common.AbstractRest;
import eu.europa.ec.fisheries.uvms.docker.validation.movement.LatLong;
import eu.europa.ec.fisheries.uvms.docker.validation.movement.MovementDto;
import eu.europa.ec.fisheries.uvms.docker.validation.movement.MovementHelper;
import eu.europa.ec.fisheries.uvms.docker.validation.system.helper.CustomRuleBuilder;
import eu.europa.ec.fisheries.uvms.docker.validation.system.helper.CustomRuleHelper;
import eu.europa.ec.fisheries.uvms.docker.validation.system.helper.FLUXEndpoint;
import eu.europa.ec.fisheries.uvms.docker.validation.system.helper.FLUXHelper;
import eu.europa.ec.fisheries.uvms.docker.validation.user.UserHelper;
import eu.europa.ec.fisheries.uvms.docker.validation.user.dto.Channel;
import eu.europa.ec.fisheries.uvms.docker.validation.user.dto.EndPoint;
import eu.europa.ec.fisheries.uvms.docker.validation.user.dto.Organisation;
import un.unece.uncefact.data.standard.fluxvesselpositionmessage._4.FLUXVesselPositionMessage;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._18.VesselGeographicalCoordinateType;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._18.VesselPositionEventType;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._18.VesselTransportMeansType;
import un.unece.uncefact.data.standard.unqualifieddatatype._18.IDType;
import xeu.bridge_connector.v1.RequestType;
import xeu.connector_bridge.v1.PostMsgType;

public class FLUXSystemIT extends AbstractRest {

    public static String DEFAULT_DATAFLOW = "FLUX_DATAFLOW";
    
    @BeforeClass
    public static void initFLUXSettings() throws InterruptedException, SocketException {
        String expectedKey = "eu.europa.ec.fisheries.uvms.plugins.flux.movement.FLUX_ENDPOINT";
        
        List<SettingType> response = getWebTarget()
                .path("config/rest/settings")
                .queryParam("moduleName", "exchange")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getValidJwtToken())
                .get(new GenericType<List<SettingType>>() {});

        SettingType expectedSetting = null;
        for (SettingType settingType : response) {
            if (settingType.getKey().equals(expectedKey)) {
                expectedSetting = settingType;
            }
        }
        assertThat(expectedSetting, is(notNullValue()));

        String newValue = "http://" + getDockerHostIp() + ":" + FLUXEndpoint.ENDPOINT_PORT + "/";
        expectedSetting.setValue(newValue);
        
        getWebTarget()
            .path("config/rest/settings")
            .path(expectedSetting.getId().toString())
            .request(MediaType.APPLICATION_JSON)
            .header(HttpHeaders.AUTHORIZATION, getValidJwtToken())
            .put(Entity.json(expectedSetting));
        
        TimeUnit.SECONDS.sleep(1);
    }
    
    @After
    public void removeCustomRules() {
        CustomRuleHelper.removeCustomRulesByDefaultUser();
    }
    
    @Test
    public void sendPositionToFLUXAndVerifyAttributes() throws Exception {
        AssetDTO asset = AssetTestHelper.createTestAsset();
        
        String destination = "NOR";
        CustomRuleType flagStateRule = CustomRuleBuilder.getBuilder()
                .setName("Area NOR => Send to NOR")
                .rule(CriteriaType.AREA, SubCriteriaType.AREA_CODE, 
                        ConditionType.EQ, destination)
                .action(ActionType.SEND_TO_FLUX, destination)
                .build();
        
        CustomRuleType createdCustomRule = CustomRuleHelper.createCustomRule(flagStateRule);
        assertNotNull(createdCustomRule);
        
        LatLong position = new LatLong(58.973, 5.781, Instant.now().toDate());
        position.speed = 5;
        
        PostMsgType message;
        try (FLUXEndpoint fluxEndpoint = new FLUXEndpoint()) {
            FLUXHelper.sendPositionToFluxPlugin(asset, position);
            message = fluxEndpoint.getMessage(10000);
        }
        
        assertThat(message.getAD(), is(destination));
        assertThat(message.getDF(), is(DEFAULT_DATAFLOW));
        assertThat(message.getID(), is(notNullValue()));
    }
    
    @Test
    public void sendPositionToFLUXWithCustomDataflow() throws Exception {
        String customDataflow = "urn:un:unece:uncefact:data:standard:FLUXVesselPositionMessage:4:Custom";
        Organisation organisation = createOrganisationWithCustomDF(customDataflow);
        AssetDTO asset = AssetTestHelper.createTestAsset();
        
        CustomRuleType flagStateRule = CustomRuleBuilder.getBuilder()
                .setName("Area NOR => Send to NOR")
                .rule(CriteriaType.AREA, SubCriteriaType.AREA_CODE, 
                        ConditionType.EQ, "NOR")
                .action(ActionType.SEND_TO_FLUX, organisation.getName())
                .build();
        
        CustomRuleType createdCustomRule = CustomRuleHelper.createCustomRule(flagStateRule);
        assertNotNull(createdCustomRule);
        
        LatLong position = new LatLong(58.973, 5.781, Instant.now().toDate());
        position.speed = 5;
        
        PostMsgType message;
        try (FLUXEndpoint fluxEndpoint = new FLUXEndpoint()) {
            FLUXHelper.sendPositionToFluxPlugin(asset, position);
            message = fluxEndpoint.getMessage(10000);
        }
        
        assertThat(message.getDF(), is(customDataflow));
    }
    
    @Test
    public void sendPositionToFLUXAndVerifyFLUXVesselReportDocument() throws Exception {
        AssetDTO asset = AssetTestHelper.createTestAsset();
        
        CustomRuleType flagStateRule = CustomRuleBuilder.getBuilder()
                .setName("Area NOR => Send to NOR")
                .rule(CriteriaType.AREA, SubCriteriaType.AREA_CODE, 
                        ConditionType.EQ, "NOR")
                .action(ActionType.SEND_TO_FLUX, "SWE")
                .build();
        
        CustomRuleType createdCustomRule = CustomRuleHelper.createCustomRule(flagStateRule);
        assertNotNull(createdCustomRule);
        
        LatLong position = new LatLong(58.973, 5.781, Instant.now().toDate());
        position.speed = 5;
        position.bearing = 123;
        
        PostMsgType message;
        try (FLUXEndpoint fluxEndpoint = new FLUXEndpoint()) {
            FLUXHelper.sendPositionToFluxPlugin(asset, position);
            message = fluxEndpoint.getMessage(10000);
        }
        FLUXVesselPositionMessage positionMessage = extractVesselPositionMessage(message.getAny());
        
        VesselTransportMeansType vesselTransportMeans = positionMessage.getVesselTransportMeans();
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
    
    @Test
    public void verifyAssetIdentifiersWithUnknownAsset() throws Exception {
        // Don't save to database
        AssetDTO asset = AssetTestHelper.createBasicAsset();
        
        CustomRuleType flagStateRule = CustomRuleBuilder.getBuilder()
                .setName("Area NOR => Send to NOR")
                .rule(CriteriaType.AREA, SubCriteriaType.AREA_CODE, 
                        ConditionType.EQ, "NOR")
                .action(ActionType.SEND_TO_FLUX, "NOR")
                .build();
        
        CustomRuleType createdCustomRule = CustomRuleHelper.createCustomRule(flagStateRule);
        assertNotNull(createdCustomRule);
        
        LatLong position = new LatLong(58.973, 5.781, Instant.now().toDate());
        position.speed = 5;
        
        PostMsgType message;
        try (FLUXEndpoint fluxEndpoint = new FLUXEndpoint()) {
            FLUXHelper.sendPositionToFluxPlugin(asset, position);
            message = fluxEndpoint.getMessage(10000);
        }
        FLUXVesselPositionMessage positionMessage = extractVesselPositionMessage(message.getAny());
        VesselTransportMeansType vesselTransportMeans = positionMessage.getVesselTransportMeans();
        
        assertThat(vesselTransportMeans.getRegistrationVesselCountry().getID().getValue(), is(asset.getFlagStateCode()));
        
        Map<String, String> assetIds = vesselTransportMeans.getIDS().stream().collect(Collectors.toMap(IDType::getSchemeID, IDType::getValue));
        assertThat(assetIds.get("CFR"), is(asset.getCfr()));
        assertThat(assetIds.get("IRCS"), is(asset.getIrcs()));
        assertThat(assetIds.get("EXT_MARK"), is(asset.getExternalMarking()));
    }
    
    @Test
    public void verifyEntryPositionType() throws Exception {
        Organisation organisation = UserHelper.getBasicOrganisation();
        organisation.setNation("NOR");
        UserHelper.createOrganisation(organisation);
        AssetDTO asset = AssetTestHelper.createTestAsset();
        
        CustomRuleType flagStateRule = CustomRuleBuilder.getBuilder()
                .setName("Area NOR => Send to NOR")
                .rule(CriteriaType.AREA, SubCriteriaType.AREA_CODE, 
                        ConditionType.EQ, "NOR")
                .action(ActionType.SEND_TO_FLUX, organisation.getName())
                .build();
        
        CustomRuleType createdCustomRule = CustomRuleHelper.createCustomRule(flagStateRule);
        assertNotNull(createdCustomRule);
        
        LatLong swePosition = new LatLong(57.716673, 11.973996, Instant.now().minus(10 * 60 * 1000).toDate());
        swePosition.speed = 5;
        LatLong norPosition = new LatLong(58.973, 5.781, Instant.now().toDate());
        norPosition.speed = 5;
        
        PostMsgType message;
        try (FLUXEndpoint fluxEndpoint = new FLUXEndpoint()) {
            FLUXHelper.sendPositionToFluxPlugin(asset, swePosition);
            MovementHelper.pollMovementCreated();
            FLUXHelper.sendPositionToFluxPlugin(asset, norPosition);
            message = fluxEndpoint.getMessage(10000);
        }
        FLUXVesselPositionMessage positionMessage = extractVesselPositionMessage(message.getAny());
        VesselTransportMeansType vesselTransportMeans = positionMessage.getVesselTransportMeans();
        assertThat(vesselTransportMeans.getSpecifiedVesselPositionEvents().size(), is(1));
        VesselPositionEventType positionEvent = vesselTransportMeans.getSpecifiedVesselPositionEvents().get(0);
        assertThat(positionEvent.getTypeCode().getValue(), is("ENTRY"));
    }
    
    @Test
    public void verifyExitPositionType() throws Exception {
        Organisation organisation = UserHelper.getBasicOrganisation();
        organisation.setNation("NOR");
        UserHelper.createOrganisation(organisation);
        AssetDTO asset = AssetTestHelper.createTestAsset();
        
        CustomRuleType flagStateRule = CustomRuleBuilder.getBuilder()
                .setName("Area NOR => Send to NOR")
                .rule(CriteriaType.AREA, SubCriteriaType.AREA_CODE_EXT, 
                        ConditionType.EQ, "NOR")
                .action(ActionType.SEND_TO_FLUX, organisation.getName())
                .build();
        
        CustomRuleType createdCustomRule = CustomRuleHelper.createCustomRule(flagStateRule);
        assertNotNull(createdCustomRule);
        
        LatLong norPosition = new LatLong(58.973, 5.781, Instant.now().minus(10 * 60 * 1000).toDate());
        norPosition.speed = 5;
        LatLong swePosition = new LatLong(57.716673, 11.973996, Instant.now().toDate());
        swePosition.speed = 5;
        
        PostMsgType message;
        try (FLUXEndpoint fluxEndpoint = new FLUXEndpoint()) {
            FLUXHelper.sendPositionToFluxPlugin(asset, norPosition);
            MovementHelper.pollMovementCreated();
            FLUXHelper.sendPositionToFluxPlugin(asset, swePosition);
            message = fluxEndpoint.getMessage(10000);
        }
        FLUXVesselPositionMessage positionMessage = extractVesselPositionMessage(message.getAny());
        VesselTransportMeansType vesselTransportMeans = positionMessage.getVesselTransportMeans();
        assertThat(vesselTransportMeans.getSpecifiedVesselPositionEvents().size(), is(1));
        VesselPositionEventType positionEvent = vesselTransportMeans.getSpecifiedVesselPositionEvents().get(0);
        assertThat(positionEvent.getTypeCode().getValue(), is("EXIT"));
    }
    
    @Test
    public void incomingEntryReportTest() throws Exception {
        AssetDTO asset = AssetTestHelper.createTestAsset();
        LatLong position = new LatLong(58.973, 5.781, Instant.now().toDate());
        
        FLUXVesselPositionMessage fluxMessage = FLUXHelper.createFluxMessage(asset, position);
        fluxMessage.getVesselTransportMeans().getSpecifiedVesselPositionEvents().get(0).getTypeCode().setValue("ENTRY");
        RequestType request = FLUXHelper.createVesselReport(fluxMessage);
        FLUXHelper.sendVesselReportToFluxPlugin(request);
        MovementHelper.pollMovementCreated();
        List<MovementDto> latestMovements = MovementHelper.getLatestMovements(Arrays.asList(asset.getId().toString()));
        assertThat(latestMovements.size(), is(1));
        MovementDto latest = latestMovements.get(0);
        assertThat(latest.getMovementType(), is(MovementTypeType.ENT));
    }
    
    @Test
    public void incomingExitReportTest() throws Exception {
        AssetDTO asset = AssetTestHelper.createTestAsset();
        LatLong position = new LatLong(58.973, 5.781, Instant.now().toDate());
        
        FLUXVesselPositionMessage fluxMessage = FLUXHelper.createFluxMessage(asset, position);
        fluxMessage.getVesselTransportMeans().getSpecifiedVesselPositionEvents().get(0).getTypeCode().setValue("EXIT");
        RequestType request = FLUXHelper.createVesselReport(fluxMessage);
        FLUXHelper.sendVesselReportToFluxPlugin(request);
        MovementHelper.pollMovementCreated();
        List<MovementDto> latestMovements = MovementHelper.getLatestMovements(Arrays.asList(asset.getId().toString()));
        assertThat(latestMovements.size(), is(1));
        MovementDto latest = latestMovements.get(0);
        assertThat(latest.getMovementType(), is(MovementTypeType.EXI));
    }
 
    private Organisation createOrganisationWithCustomDF(String dataflow) throws SocketException {
        Organisation organisation = UserHelper.getBasicOrganisation();
        organisation.setNation("NOR");
        UserHelper.createOrganisation(organisation);
        EndPoint endpoint = new EndPoint();
        endpoint.setName("FLUX");
        endpoint.setURI("SWE");
        endpoint.setStatus("E");
        endpoint.setOrganisationName(organisation.getName());
        EndPoint createdEndpoint = UserHelper.createEndpoint(endpoint);
        Channel channel = new Channel();
        channel.setDataflow(dataflow);
        channel.setService("FLUX");
        channel.setPriority(1);
        channel.setEndpointId(createdEndpoint.getEndpointId());
        UserHelper.createChannel(channel);
        return organisation;
    }
    
    private FLUXVesselPositionMessage extractVesselPositionMessage(Element any) throws JAXBException {
        JAXBContext jc = JAXBContext.newInstance(FLUXVesselPositionMessage.class);
        Unmarshaller unmarshaller = jc.createUnmarshaller();
        return (FLUXVesselPositionMessage) unmarshaller.unmarshal(any);
    }
    
    // Find docker host machine ip. Replace this with 'host.docker.internal' when supported on Linux.
    private static String getDockerHostIp() throws SocketException {
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
