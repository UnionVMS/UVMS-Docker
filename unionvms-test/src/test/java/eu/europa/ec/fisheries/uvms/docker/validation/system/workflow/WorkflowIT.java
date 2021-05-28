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
package eu.europa.ec.fisheries.uvms.docker.validation.system.workflow;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import org.junit.BeforeClass;
import org.junit.Test;
import eu.europa.ec.fisheries.uvms.asset.client.model.AssetDTO;
import eu.europa.ec.fisheries.uvms.docker.validation.asset.AssetTestHelper;
import eu.europa.ec.fisheries.uvms.docker.validation.common.AbstractRest;
import eu.europa.ec.fisheries.uvms.docker.validation.common.TopicListener;
import eu.europa.ec.fisheries.uvms.docker.validation.config.ConfigRestHelper;
import eu.europa.ec.fisheries.uvms.docker.validation.incident.IncidentTestHelper;
import eu.europa.ec.fisheries.uvms.docker.validation.mobileterminal.MobileTerminalTestHelper;
import eu.europa.ec.fisheries.uvms.docker.validation.mobileterminal.dto.MobileTerminalDto;
import eu.europa.ec.fisheries.uvms.docker.validation.rules.MovementRulesTestHelper;
import eu.europa.ec.fisheries.uvms.docker.validation.rules.dto.PreviousReportDto;
import eu.europa.ec.fisheries.uvms.docker.validation.system.helper.VMSSystemHelper;
import eu.europa.ec.fisheries.uvms.incident.model.dto.IncidentDto;
import eu.europa.ec.fisheries.uvms.incident.model.dto.IncidentTicketDto;
import eu.europa.ec.fisheries.uvms.incident.model.dto.OpenAndRecentlyResolvedIncidentsDto;
import eu.europa.ec.fisheries.uvms.incident.model.dto.enums.StatusEnum;

public class WorkflowIT extends AbstractRest {

    @BeforeClass
    public static void beforeClass() throws Exception {
        ConfigRestHelper.setLocalFlagStateToSwe();
    }

    @Test
    public void removePreviousReportAndOpenIncidentForInactiveAssetTest() throws IOException, Exception {
        AssetDTO asset = AssetTestHelper.createAsset(AssetTestHelper.createBasicAsset());
        MobileTerminalDto mt = MobileTerminalTestHelper.createMobileTerminal();
        MobileTerminalTestHelper.assignMobileTerminal(asset, mt);

        // Create previous report
        VMSSystemHelper.triggerBasicRuleWithSatellitePosition(mt);
        List<PreviousReportDto> previousReports = MovementRulesTestHelper.getPreviousReports();
        List<PreviousReportDto> reportsByAsset = previousReports.stream()
                .filter(r -> r.getAssetGuid().equals(asset.getId().toString()))
                .collect(Collectors.toList());
        assertThat(reportsByAsset.size(), is(1));
        
        // Create asset not sending incident
        IncidentTicketDto ticket = IncidentTestHelper.createTicket(asset.getId());
        ticket.setMovementId(reportsByAsset.get(0).getMovementGuid().toString());
        IncidentDto dto = IncidentTestHelper.createAssetNotSendingIncident(ticket, IncidentTestHelper.INCIDENT_CREATE_EVENT);
        assertThat(dto, is(notNullValue()));

        try (TopicListener topicListener = new TopicListener(TopicListener.EVENT_STREAM, null)) {
            asset.setActive(false);
            AssetTestHelper.updateAsset(asset);
            topicListener.listenOnEventBus();
            TimeUnit.SECONDS.sleep(1);
        }

        previousReports = MovementRulesTestHelper.getPreviousReports();
        assertThat(previousReports.stream()
                .filter(r -> r.getAssetGuid().equals(asset.getId().toString()))
                .collect(Collectors.toList()).size(), is(0));

        OpenAndRecentlyResolvedIncidentsDto incidents = IncidentTestHelper.getAllOpenAndRecentlyResolvedIncidents();
        assertThat(incidents.getRecentlyResolved().get(dto.getId()).getStatus(), is(StatusEnum.RESOLVED));
    }
}
