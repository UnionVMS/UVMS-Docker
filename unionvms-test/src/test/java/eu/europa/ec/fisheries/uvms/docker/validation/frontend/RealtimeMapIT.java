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
package eu.europa.ec.fisheries.uvms.docker.validation.frontend;

import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Random;
import java.util.UUID;
import org.junit.Test;
import eu.europa.ec.fisheries.uvms.asset.client.model.AssetDTO;
import eu.europa.ec.fisheries.uvms.docker.validation.asset.AssetTestHelper;
import eu.europa.ec.fisheries.uvms.docker.validation.common.TopicListener;
import eu.europa.ec.fisheries.uvms.docker.validation.frontend.pages.AssetDetailsPanel;
import eu.europa.ec.fisheries.uvms.docker.validation.frontend.pages.IncidentPanel;
import eu.europa.ec.fisheries.uvms.docker.validation.frontend.pages.MapFilterPanel;
import eu.europa.ec.fisheries.uvms.docker.validation.frontend.pages.RealtimeMapPage;
import eu.europa.ec.fisheries.uvms.docker.validation.frontend.pages.UnionVMS;
import eu.europa.ec.fisheries.uvms.docker.validation.frontend.pages.WorkflowsPanel;
import eu.europa.ec.fisheries.uvms.docker.validation.incident.IncidentTestHelper;
import eu.europa.ec.fisheries.uvms.docker.validation.mobileterminal.MobileTerminalTestHelper;
import eu.europa.ec.fisheries.uvms.docker.validation.mobileterminal.dto.MobileTerminalDto;
import eu.europa.ec.fisheries.uvms.docker.validation.movement.LatLong;
import eu.europa.ec.fisheries.uvms.docker.validation.movement.MovementHelper;
import eu.europa.ec.fisheries.uvms.docker.validation.movement.model.IncomingMovement;
import eu.europa.ec.fisheries.uvms.docker.validation.system.helper.FLUXHelper;
import eu.europa.ec.fisheries.uvms.docker.validation.system.helper.VMSSystemHelper;
import eu.europa.ec.fisheries.uvms.incident.model.dto.IncidentDto;
import eu.europa.ec.fisheries.uvms.incident.model.dto.IncidentTicketDto;
import eu.europa.ec.fisheries.uvms.movement.model.dto.MovementDto;

public class RealtimeMapIT {

    @Test
    public void clickOnAssetTest() throws Exception {
        UnionVMS uvms = UnionVMS.login();
        AssetDTO asset = AssetTestHelper.createTestAsset();
        int randomLatitude = new Random().nextInt(60);
        int randomLongitude = new Random().nextInt(100);
        LatLong position = new LatLong(randomLatitude, randomLongitude, new Date());
        try (TopicListener topicListener = new TopicListener(TopicListener.EVENT_STREAM, "event = 'Movement'")) {
            FLUXHelper.sendPositionToFluxPlugin(asset, position);
            topicListener.listenOnEventBus();
        }

        RealtimeMapPage realtime = uvms.realtimeMapPage();
        realtime.gotoCoordinates(position.latitude, position.longitude);
        realtime.clickOnCenter();
        AssetDetailsPanel assetDetailsPanel = realtime.assetDetailsPanel();
        assetDetailsPanel.assertIrcs(asset.getIrcs());
        assetDetailsPanel.assertMmsi(asset.getMmsi());
        assetDetailsPanel.assertExternalMarking(asset.getExternalMarking());
    }

    @Test
    public void assetPollHistoryTest() throws Exception {
        UnionVMS uvms = UnionVMS.login();
        AssetDTO asset = AssetTestHelper.createTestAsset();
        MobileTerminalDto mobileTerminal = MobileTerminalTestHelper.createMobileTerminal();
        MobileTerminalTestHelper.assignMobileTerminal(asset, mobileTerminal);

        int randomLatitude = new Random().nextInt(60);
        int randomLongitude = new Random().nextInt(100);
        LatLong position = new LatLong(randomLatitude, randomLongitude, new Date());
        try (TopicListener topicListener = new TopicListener(TopicListener.EVENT_STREAM, "event = 'Movement'")) {
            FLUXHelper.sendPositionToFluxPlugin(asset, position);
            topicListener.listenOnEventBus();
        }

        RealtimeMapPage realtime = uvms.realtimeMapPage();
        realtime.gotoCoordinates(position.latitude, position.longitude);
        realtime.clickOnCenter();
        AssetDetailsPanel assetDetailsPanel = realtime.assetDetailsPanel();
        assetDetailsPanel.assertLast24hPollHistoryContainsItems(0);
        try (TopicListener topicListener = new TopicListener(VMSSystemHelper.INMARSAT_SELECTOR)) {
            assetDetailsPanel.sendManuallPoll("Test comment");
            topicListener.listenOnEventBus();
        }
        assetDetailsPanel.assertLast24hPollHistoryContainsItems(1);
    }

    @Test
    public void filterTest() throws IOException, Exception {
        UnionVMS uvms = UnionVMS.login();
        AssetDTO asset = AssetTestHelper.createTestAsset();
        int randomLatitude = new Random().nextInt(90);
        int randomLongitude = new Random().nextInt(180);
        LatLong position = new LatLong(randomLatitude, randomLongitude, new Date());
        try (TopicListener topicListener = new TopicListener(TopicListener.EVENT_STREAM, "event = 'Movement'")) {
            FLUXHelper.sendPositionToFluxPlugin(asset, position);
            topicListener.listenOnEventBus();
        }

        RealtimeMapPage realtimeMapPage = uvms.realtimeMapPage();
        realtimeMapPage.mapFilterPanel().setFilter("/i " + asset.getIrcs());
        realtimeMapPage.mapInformationPanel().assertFilteredAssetShowing(1);

        realtimeMapPage.mapFilterPanel().setFilter("/i " + UUID.randomUUID().toString());
        realtimeMapPage.mapInformationPanel().assertFilteredAssetShowing(0);
    }

    @Test
    public void createFilterTest() {
        UnionVMS uvms = UnionVMS.login();
        RealtimeMapPage realtimeMapPage = uvms.realtimeMapPage();
        MapFilterPanel mapFilterPanel = realtimeMapPage.mapFilterPanel();
        String filterName = "Filter " + UUID.randomUUID();
        mapFilterPanel.setFilter("/f " + UUID.randomUUID());
        mapFilterPanel.createFilter(filterName);
        mapFilterPanel.assertSavedFilterExists(filterName);
    }

    @Test
    public void deleteFilterTest() {
        UnionVMS uvms = UnionVMS.login();
        RealtimeMapPage realtimeMapPage = uvms.realtimeMapPage();
        MapFilterPanel mapFilterPanel = realtimeMapPage.mapFilterPanel();
        String filterName = "Filter " + UUID.randomUUID();
        mapFilterPanel.setFilter("/f " + UUID.randomUUID());
        mapFilterPanel.createFilter(filterName);

        mapFilterPanel.deleteFilter(filterName);
        mapFilterPanel.assertSavedFilterNotExists(filterName);
    }

    @Test
    public void createIncidentTest() throws Exception {
        AssetDTO asset = AssetTestHelper.createTestAsset();
        LatLong position = new LatLong(new Random().nextInt(60), new Random().nextInt(100), new Date());
        MovementDto movement;
        try (MovementHelper movementHelper = new MovementHelper()) {
            IncomingMovement incomingMovement = movementHelper.createIncomingMovement(asset, position);
            movement = movementHelper.createMovement(incomingMovement);
        }
        IncidentTicketDto ticket = IncidentTestHelper.createTicket(asset.getId());
        ticket.setMovementId(movement.getId().toString());
        IncidentDto incident = IncidentTestHelper.createAssetNotSendingIncident(ticket, IncidentTestHelper.INCIDENT_CREATE_EVENT);

        UnionVMS uvms = UnionVMS.login();
        RealtimeMapPage realtimeMapPage = uvms.realtimeMapPage();
        WorkflowsPanel workflowsPanel = realtimeMapPage.workflowsPanel();
        workflowsPanel.showAssetNotSending();

        workflowsPanel.assertIncidentIrcsByAsset(asset.getName(), asset.getIrcs());
        workflowsPanel.assertIncidentPositionTimestampByAsset(asset.getName(), movement.getTimestamp());

        IncidentPanel incidentPanel = workflowsPanel.selectIncidentByAsset(asset.getName());
        incidentPanel.assertIncidentName(asset.getIrcs(), asset.getName());
        incidentPanel.assertIncidentId(incident.getId());
    }

    @Test
    public void moveIncidentToParkedTest() throws Exception {
        AssetDTO asset = AssetTestHelper.createTestAsset();
        LatLong position = new LatLong(new Random().nextInt(60), new Random().nextInt(100), new Date());
        MovementDto movement;
        try (MovementHelper movementHelper = new MovementHelper()) {
            IncomingMovement incomingMovement = movementHelper.createIncomingMovement(asset, position);
            movement = movementHelper.createMovement(incomingMovement);
        }
        IncidentTicketDto ticket = IncidentTestHelper.createTicket(asset.getId());
        ticket.setMovementId(movement.getId().toString());
        IncidentTestHelper.createAssetNotSendingIncident(ticket, IncidentTestHelper.INCIDENT_CREATE_EVENT);

        UnionVMS uvms = UnionVMS.login();
        RealtimeMapPage realtimeMapPage = uvms.realtimeMapPage();
        WorkflowsPanel workflowsPanel = realtimeMapPage.workflowsPanel();
        workflowsPanel.showAssetNotSending();
        IncidentPanel incidentPanel = workflowsPanel.selectIncidentByAsset(asset.getName());
        incidentPanel.moveIncidentToParked("Comment " + UUID.randomUUID());

        workflowsPanel.showParked();
        workflowsPanel.assertIncidentExists(asset.getName());
    }

    @Test
    public void setExpiryDateOnParkedIncidentTest() throws Exception {
        AssetDTO asset = AssetTestHelper.createTestAsset();
        LatLong position = new LatLong(new Random().nextInt(60), new Random().nextInt(100), new Date());
        MovementDto movement;
        try (MovementHelper movementHelper = new MovementHelper()) {
            IncomingMovement incomingMovement = movementHelper.createIncomingMovement(asset, position);
            movement = movementHelper.createMovement(incomingMovement);
        }
        IncidentTicketDto ticket = IncidentTestHelper.createTicket(asset.getId());
        ticket.setMovementId(movement.getId().toString());
        IncidentTestHelper.createAssetNotSendingIncident(ticket, IncidentTestHelper.INCIDENT_CREATE_EVENT);

        UnionVMS uvms = UnionVMS.login();
        RealtimeMapPage realtimeMapPage = uvms.realtimeMapPage();
        WorkflowsPanel workflowsPanel = realtimeMapPage.workflowsPanel();
        workflowsPanel.showAssetNotSending();
        IncidentPanel incidentPanel = workflowsPanel.selectIncidentByAsset(asset.getName());
        incidentPanel.moveIncidentToParked("Comment " + UUID.randomUUID());

        workflowsPanel.showParked();
        incidentPanel = workflowsPanel.selectIncidentByAsset(asset.getName());

        incidentPanel.setExpiryDate(Instant.now().plus(1, ChronoUnit.HOURS), "Comment " + UUID.randomUUID());
        workflowsPanel.assertProgressCircleExists(asset.getName());
        workflowsPanel.assertProgressCircleValue(asset.getName(), "-59 min");
    }
}
