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
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;
import org.hamcrest.CoreMatchers;
import org.junit.Test;
import eu.europa.ec.fisheries.uvms.asset.client.model.AssetDTO;
import eu.europa.ec.fisheries.uvms.docker.validation.asset.AssetTestHelper;
import eu.europa.ec.fisheries.uvms.docker.validation.common.AbstractRest;
import eu.europa.ec.fisheries.uvms.docker.validation.mobileterminal.MobileTerminalTestHelper;
import eu.europa.ec.fisheries.uvms.docker.validation.mobileterminal.dto.MobileTerminalDto;
import eu.europa.ec.fisheries.uvms.docker.validation.movement.LatLong;
import eu.europa.ec.fisheries.uvms.docker.validation.movement.model.AlarmReport;
import eu.europa.ec.fisheries.uvms.docker.validation.system.helper.FLUXHelper;
import eu.europa.ec.fisheries.uvms.docker.validation.system.helper.InmarsatPluginMock;
import eu.europa.ec.fisheries.uvms.docker.validation.system.helper.NAFHelper;
import eu.europa.ec.fisheries.uvms.docker.validation.system.helper.SanityRuleHelper;
import un.unece.uncefact.data.standard.fluxvesselpositionmessage._4.FLUXVesselPositionMessage;
import xeu.bridge_connector.v1.RequestType;

public class SanityRulesIT extends AbstractRest {
    
    private String assetMustExistRuleName = "Asset not found";
    private String memberNoMissingRuleName = "Mem No. missing";
    private String dnidMissingRuleName = "DNID missing";

    /*
    <column name="sanityrule_description" value="An asset must exist"/>
    <column name="sanityrule_expression" value="assetGuid == null"/>
     */
    @Test
    public void assetMustExistTest() throws Exception {
        ZonedDateTime timestamp = ZonedDateTime.now(ZoneOffset.UTC);
        MobileTerminalDto mobileTerminal = MobileTerminalTestHelper.createBasicMobileTerminal();

        long openAlarmsBefore = SanityRuleHelper.countOpenAlarms();
        
        LatLong position = new LatLong(11d, 56d, new Date());
        InmarsatPluginMock.sendInmarsatPosition(mobileTerminal, position);

        SanityRuleHelper.pollAlarmReportCreated();
        long openAlarmsAfter = SanityRuleHelper.countOpenAlarms();
        assertThat(openAlarmsAfter, is(openAlarmsBefore + 1));

        AlarmReport latestAlarm = SanityRuleHelper.getLatestOpenAlarmReportSince(timestamp);
        assertTrue(latestAlarm.getAlarmItemList().stream().anyMatch(item -> item.getRuleName().equals(assetMustExistRuleName)));
    }

    /*
    <column name="sanityrule_description" value="Position time cannot be in the future"/>
    <column name="sanityrule_expression" value="positionTime != null &amp;&amp; positionTime.getTime() > new Date().getTime()"/>
     */
    @Test
    public void positionTimeCannotBeFutureTest() throws Exception {
        AssetDTO asset = AssetTestHelper.createTestAsset();

        long openAlarmsBefore = SanityRuleHelper.countOpenAlarms();
        
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR, 5);
        LatLong position = new LatLong(11d, 56d, calendar.getTime());
        NAFHelper.sendPositionToNAFPlugin(position, asset);

        SanityRuleHelper.pollAlarmReportCreated();
        long openAlarmsAfter = SanityRuleHelper.countOpenAlarms();
        assertThat(openAlarmsAfter, is(openAlarmsBefore + 1));
    }
    
    /*
    <column name="sanityrule_description" value="Latitude must exist"/>
    <column name="sanityrule_expression" value="latitude == null"/>
     */
    @Test
    public void latitudeMustExistsTest() throws Exception {
        AssetDTO asset = AssetTestHelper.createTestAsset();

        long openAlarmsBefore = SanityRuleHelper.countOpenAlarms();
        
        LatLong position = new LatLong(11d, 56d, new Date());
        FLUXVesselPositionMessage fluxMessage = FLUXHelper.createFluxMessage(asset, position);
        fluxMessage.getVesselTransportMeans().getSpecifiedVesselPositionEvents().get(0).getSpecifiedVesselGeographicalCoordinate().setLatitudeMeasure(null);
        RequestType report = FLUXHelper.createVesselReport(fluxMessage);
        FLUXHelper.sendVesselReportToFluxPlugin(report);

        SanityRuleHelper.pollAlarmReportCreated();
        long openAlarmsAfter = SanityRuleHelper.countOpenAlarms();
        assertThat(openAlarmsAfter, is(openAlarmsBefore + 1));
    }
    
    /*
    <column name="sanityrule_description" value="Longitude must exist"/>
    <column name="sanityrule_expression" value="longitude == null"/>
     */
    @Test
    public void longitudeMustExistsTest() throws Exception {
        AssetDTO asset = AssetTestHelper.createTestAsset();

        long openAlarmsBefore = SanityRuleHelper.countOpenAlarms();
        
        LatLong position = new LatLong(11d, 56d, new Date());
        FLUXVesselPositionMessage fluxMessage = FLUXHelper.createFluxMessage(asset, position);
        fluxMessage.getVesselTransportMeans().getSpecifiedVesselPositionEvents().get(0).getSpecifiedVesselGeographicalCoordinate().setLongitudeMeasure(null);
        RequestType report = FLUXHelper.createVesselReport(fluxMessage);
        FLUXHelper.sendVesselReportToFluxPlugin(report);

        SanityRuleHelper.pollAlarmReportCreated();
        long openAlarmsAfter = SanityRuleHelper.countOpenAlarms();
        assertThat(openAlarmsAfter, is(openAlarmsBefore + 1));
    }
    
    /*
    <column name="sanityrule_description" value="A mobile terminal must be connected to an asset"/>
    <column name="sanityrule_expression" value="mobileTerminalConnectId == null &amp;&amp; pluginType == &quot;SATELLITE_RECEIVER&quot;"/>
    */
    /*
    <column name="sanityrule_description" value="Member number must exist when INMARSAT_C"/>
    <column name="sanityrule_expression" value="mobileTerminalMemberNumber == null &amp;&amp; pluginType == &quot;SATELLITE_RECEIVER&quot; &amp;&amp; mobileTerminalType == &quot;INMARSAT_C&quot;"/>
    */
    @Test
    public void memberNumberMustExistWhenInmarsatTest() throws Exception {
        ZonedDateTime timestamp = ZonedDateTime.now(ZoneOffset.UTC);

        AssetDTO asset = AssetTestHelper.createTestAsset();
        MobileTerminalDto mobileTerminal = MobileTerminalTestHelper.createMobileTerminal();
        MobileTerminalTestHelper.assignMobileTerminal(asset, mobileTerminal);

        long openAlarmsBefore = SanityRuleHelper.countOpenAlarms();

        LatLong position = new LatLong(11d, 56d, new Date());
        mobileTerminal.getChannels().iterator().next().setMemberNumber(null);
        InmarsatPluginMock.sendInmarsatPosition(mobileTerminal, position);

        SanityRuleHelper.pollAlarmReportCreated();
        long openAlarmsAfter = SanityRuleHelper.countOpenAlarms();
        assertThat(openAlarmsAfter, is(openAlarmsBefore + 1));

        AlarmReport latestAlarm = SanityRuleHelper.getLatestOpenAlarmReportSince(timestamp);
        assertTrue(latestAlarm.getAlarmItemList().stream().anyMatch(item -> item.getRuleName().equals(memberNoMissingRuleName)));
    }
    /*
    <column name="sanityrule_description" value="DNID must exist when INMARSAT_C"/>
    <column name="sanityrule_expression" value="mobileTerminalDnid == null &amp;&amp; pluginType == &quot;SATELLITE_RECEIVER&quot; &amp;&amp; mobileTerminalType == &quot;INMARSAT_C&quot;"/>
     */
    @Test
    public void dnidMustExistWhenInmarsatTest() throws Exception {
        ZonedDateTime timestamp = ZonedDateTime.now(ZoneOffset.UTC);
        AssetDTO asset = AssetTestHelper.createTestAsset();
        MobileTerminalDto mobileTerminal = MobileTerminalTestHelper.createMobileTerminal();
        MobileTerminalTestHelper.assignMobileTerminal(asset, mobileTerminal);

        long openAlarmsBefore = SanityRuleHelper.countOpenAlarms();

        LatLong position = new LatLong(11d, 56d, new Date());
        mobileTerminal.getChannels().iterator().next().setDNID(null);
        InmarsatPluginMock.sendInmarsatPosition(mobileTerminal, position);

        SanityRuleHelper.pollAlarmReportCreated();
        long openAlarmsAfter = SanityRuleHelper.countOpenAlarms();
        assertThat(openAlarmsAfter, is(openAlarmsBefore + 1));

        AlarmReport latestAlarm = SanityRuleHelper.getLatestOpenAlarmReportSince(timestamp);
        assertTrue(latestAlarm.getAlarmItemList().stream().anyMatch(item -> item.getRuleName().equals(dnidMissingRuleName)));
    }
    /*
    <column name="sanityrule_description" value="Serial Number must exist when IRIDIUM"/>
    <column name="sanityrule_expression" value="mobileTerminalSerialNumber == null &amp;&amp; pluginType == &quot;SATELLITE_RECEIVER&quot; &amp;&amp; mobileTerminalType == &quot;IRIDIUM&quot;"/>
    */
    /*
    <column name="sanityrule_description" value="ComChannel Type must exist"/>
    <column name="sanityrule_expression" value="comChannelType == null"/>
     */
    /*
    <column name="sanityrule_description" value="Both CFR and IRCS must exist when FLUX or MANUAL"/>
    <column name="sanityrule_expression" value="cfr == null &amp;&amp; ircs == null &amp;&amp; (pluginType == &quot;FLUX&quot; || comChannelType == &quot;MANUAL&quot;)"/>
    */
    /*
    <column name="sanityrule_description" value="Plugin Type must exist"/>
    <column name="sanityrule_expression" value="pluginType == null"/>
     */
    /*
    <column name="sanityrule_description" value="Position time must exist"/>
    <column name="sanityrule_expression" value="positionTime == null"/>
    */
    @Test
    public void positionTimeMustExistTest() throws Exception {
        AssetDTO asset = AssetTestHelper.createTestAsset();

        long openAlarmsBefore = SanityRuleHelper.countOpenAlarms();
        
        LatLong position = new LatLong(11d, 56d, new Date());
        FLUXVesselPositionMessage fluxMessage = FLUXHelper.createFluxMessage(asset, position);
        fluxMessage.getVesselTransportMeans().getSpecifiedVesselPositionEvents().get(0).setObtainedOccurrenceDateTime(null);
        RequestType report = FLUXHelper.createVesselReport(fluxMessage);
        FLUXHelper.sendVesselReportToFluxPlugin(report);

        SanityRuleHelper.pollAlarmReportCreated();
        long openAlarmsAfter = SanityRuleHelper.countOpenAlarms();
        assertThat(openAlarmsAfter, is(openAlarmsBefore + 1));
    }
    
    @Test
    public void triggerTwoSanityRulesTest() throws Exception {
        ZonedDateTime timestamp = ZonedDateTime.now(ZoneOffset.UTC);
        
        AssetDTO asset = AssetTestHelper.createTestAsset();

        LatLong position = new LatLong(11d, 56d, new Date());
        FLUXVesselPositionMessage fluxMessage = FLUXHelper.createFluxMessage(asset, position);
        fluxMessage.getVesselTransportMeans().getSpecifiedVesselPositionEvents().get(0).getSpecifiedVesselGeographicalCoordinate().setLongitudeMeasure(null);
        fluxMessage.getVesselTransportMeans().getSpecifiedVesselPositionEvents().get(0).getSpecifiedVesselGeographicalCoordinate().setLatitudeMeasure(null);
        RequestType report = FLUXHelper.createVesselReport(fluxMessage);
        FLUXHelper.sendVesselReportToFluxPlugin(report);

        SanityRuleHelper.pollAlarmReportCreated();
        
        AlarmReport latestOpenAlarmReportSince = SanityRuleHelper.getLatestOpenAlarmReportSince(timestamp);
        
        assertThat(latestOpenAlarmReportSince, CoreMatchers.is(CoreMatchers.notNullValue()));
        assertThat(latestOpenAlarmReportSince.getAlarmItemList(), CoreMatchers.is(CoreMatchers.notNullValue()));
        assertThat(latestOpenAlarmReportSince.getAlarmItemList().size(), CoreMatchers.is(2));
    }
    
    @Test
    public void triggerThreeSanityRulesTest() throws Exception {
        ZonedDateTime timestamp = ZonedDateTime.now(ZoneOffset.UTC);
        
        AssetDTO asset = AssetTestHelper.createTestAsset();

        LatLong position = new LatLong(11d, 56d, Date.from(timestamp.plus(1, ChronoUnit.HOURS).toInstant()));
        FLUXVesselPositionMessage fluxMessage = FLUXHelper.createFluxMessage(asset, position);
        fluxMessage.getVesselTransportMeans().getSpecifiedVesselPositionEvents().get(0).getSpecifiedVesselGeographicalCoordinate().setLongitudeMeasure(null);
        fluxMessage.getVesselTransportMeans().getSpecifiedVesselPositionEvents().get(0).getSpecifiedVesselGeographicalCoordinate().setLatitudeMeasure(null);;
        RequestType report = FLUXHelper.createVesselReport(fluxMessage);
        FLUXHelper.sendVesselReportToFluxPlugin(report);

        SanityRuleHelper.pollAlarmReportCreated();
        
        AlarmReport latestOpenAlarmReportSince = SanityRuleHelper.getLatestOpenAlarmReportSince(timestamp);
        
        assertThat(latestOpenAlarmReportSince, CoreMatchers.is(CoreMatchers.notNullValue()));
        assertThat(latestOpenAlarmReportSince.getAlarmItemList(), CoreMatchers.is(CoreMatchers.notNullValue()));
        assertThat(latestOpenAlarmReportSince.getAlarmItemList().size(), CoreMatchers.is(3));
    }
}
