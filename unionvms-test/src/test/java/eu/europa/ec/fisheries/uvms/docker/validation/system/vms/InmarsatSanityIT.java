package eu.europa.ec.fisheries.uvms.docker.validation.system.vms;

import eu.europa.ec.fisheries.schema.exchange.movement.v1.*;
import eu.europa.ec.fisheries.schema.exchange.plugin.v1.SetReportRequest;
import eu.europa.ec.fisheries.schema.movementrules.customrule.v1.*;
import eu.europa.ec.fisheries.uvms.asset.client.model.AssetDTO;
import eu.europa.ec.fisheries.uvms.docker.validation.asset.AssetTestHelper;
import eu.europa.ec.fisheries.uvms.docker.validation.common.AbstractRest;
import eu.europa.ec.fisheries.uvms.docker.validation.common.MessageHelper;
import eu.europa.ec.fisheries.uvms.docker.validation.common.TopicListener;
import eu.europa.ec.fisheries.uvms.docker.validation.mobileterminal.MobileTerminalTestHelper;
import eu.europa.ec.fisheries.uvms.docker.validation.mobileterminal.dto.ChannelDto;
import eu.europa.ec.fisheries.uvms.docker.validation.mobileterminal.dto.MobileTerminalDto;
import eu.europa.ec.fisheries.uvms.docker.validation.movement.LatLong;
import eu.europa.ec.fisheries.uvms.docker.validation.movement.model.AlarmReport;
import eu.europa.ec.fisheries.uvms.docker.validation.system.helper.CustomRuleBuilder;
import eu.europa.ec.fisheries.uvms.docker.validation.system.helper.CustomRuleHelper;
import eu.europa.ec.fisheries.uvms.docker.validation.system.helper.InmarsatPluginMock;
import eu.europa.ec.fisheries.uvms.docker.validation.system.helper.SanityRuleHelper;
import eu.europa.ec.fisheries.uvms.docker.validation.system.helper.VMSSystemHelper;
import org.hamcrest.CoreMatchers;
import org.junit.*;

import javax.jms.JMSException;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Date;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertNotNull;

public class InmarsatSanityIT extends AbstractRest {

    private static final String SELECTOR = "ServiceName='eu.europa.ec.fisheries.uvms.plugins.flux.movement'";

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
    public void removeCustomRules() {
        CustomRuleHelper.removeCustomRulesByDefaultUser();
    }

    @Test
    public void inmarsatPosition_MEMBER_NUMBER_and_DNID() throws Exception {
        Instant timestamp = Instant.now();

        // create test data
        AssetDTO asset = AssetTestHelper.createTestAsset();
        MobileTerminalDto mobileTerminal = MobileTerminalTestHelper.createMobileTerminal();
        MobileTerminalTestHelper.assignMobileTerminal(asset, mobileTerminal);

        //
        String fluxEndpoint = "DNK";

        CustomRuleType flagStateRule = CustomRuleBuilder.getBuilder()
                .setName("Flag state => FLUX DNK")
                .rule(CriteriaType.ASSET, SubCriteriaType.FLAG_STATE,
                        ConditionType.EQ, asset.getFlagStateCode())
                .action(ActionType.SEND_REPORT, VMSSystemHelper.FLUX_NAME, fluxEndpoint)
                .action(ActionType.CREATE_TICKET, "Dummy ticket")
                .build();

        CustomRuleType createdCustomRule = CustomRuleHelper.createCustomRule(flagStateRule);
        assertNotNull(createdCustomRule);

        // create the position report only containing DNID and MEMBER_NUMBER  OBS NO ASSET REFERENCES AT ALL
        LatLong position = new LatLong(11d, 56d, new Date());
        try (TopicListener topicListener = new TopicListener(SELECTOR)) {
            InmarsatPluginMock.sendInmarsatPosition(mobileTerminal, position);

            CustomRuleHelper.pollTicketCreated();
            CustomRuleHelper.assertRuleTriggered(createdCustomRule, timestamp);
    
            SetReportRequest setReportRequest = topicListener.listenOnEventBusForSpecificMessage(SetReportRequest.class);
    
            assertThat(setReportRequest.getReport().getRecipient(), is(fluxEndpoint));
    
            MovementType movement = setReportRequest.getReport().getMovement();
            assertThat(movement.getAssetName(), is(asset.getName()));
            assertThat(movement.getIrcs(), is(asset.getIrcs()));
        }
    }
    
    @Test
    public void sendInmarsatVerifyAsset() throws Exception {
        String dnid = "12345";
        
        // create assets/mt
        AssetDTO asset1 = AssetTestHelper.createTestAsset();
        MobileTerminalDto mobileTerminal1 = MobileTerminalTestHelper.createBasicMobileTerminal();
        mobileTerminal1.getChannels().iterator().next().setDnid(dnid);
        mobileTerminal1 = MobileTerminalTestHelper.persistMobileTerminal(mobileTerminal1);
        MobileTerminalTestHelper.assignMobileTerminal(asset1, mobileTerminal1);
        
        AssetDTO asset2 = AssetTestHelper.createTestAsset();
        MobileTerminalDto mobileTerminal2 = MobileTerminalTestHelper.createBasicMobileTerminal();
        mobileTerminal2.getChannels().iterator().next().setDnid(dnid);
        mobileTerminal2 = MobileTerminalTestHelper.persistMobileTerminal(mobileTerminal2);
        MobileTerminalTestHelper.assignMobileTerminal(asset2, mobileTerminal2);
        
        AssetDTO asset3 = AssetTestHelper.createTestAsset();
        MobileTerminalDto mobileTerminal3 = MobileTerminalTestHelper.createBasicMobileTerminal();
        mobileTerminal3.getChannels().iterator().next().setDnid(dnid);
        mobileTerminal3 = MobileTerminalTestHelper.persistMobileTerminal(mobileTerminal3);
        MobileTerminalTestHelper.assignMobileTerminal(asset3, mobileTerminal3);

        // Send position and verify
        SetReportRequest setReportRequest = sendPositionAndReturnResponse(asset1, mobileTerminal1);
        MovementType movement1 = setReportRequest.getReport().getMovement();
        assertThat(movement1.getAssetName(), is(asset1.getName()));
        assertThat(movement1.getIrcs(), is(asset1.getIrcs()));
        
        SetReportRequest setReportRequest2 = sendPositionAndReturnResponse(asset2, mobileTerminal2);
        MovementType movement2 = setReportRequest2.getReport().getMovement();
        assertThat(movement2.getAssetName(), is(asset2.getName()));
        assertThat(movement2.getIrcs(), is(asset2.getIrcs()));
        
        SetReportRequest setReportRequest3 = sendPositionAndReturnResponse(asset3, mobileTerminal3);
        MovementType movement3 = setReportRequest3.getReport().getMovement();
        assertThat(movement3.getAssetName(), is(asset3.getName()));
        assertThat(movement3.getIrcs(), is(asset3.getIrcs()));
    }
    
    @Test
    public void sendInmarsatNonExistingAssetAndMobileTerminal() throws Exception {
        ZonedDateTime timestamp = ZonedDateTime.now(ZoneOffset.UTC);
        
        MobileTerminalDto mobileTerminal = MobileTerminalTestHelper.createBasicMobileTerminal();
        
        LatLong position = new LatLong(11d, 56d, new Date());
        InmarsatPluginMock.sendInmarsatPosition(mobileTerminal, position);
        
        SanityRuleHelper.pollAlarmReportCreated();
        
        AlarmReport alarmReport = SanityRuleHelper.getLatestOpenAlarmReportSince(timestamp);
        ChannelDto channel = mobileTerminal.getChannels().iterator().next();
        assertThat(alarmReport.getIncomingMovement().getMobileTerminalDNID(), CoreMatchers.is(channel.getDnid()));
        assertThat(alarmReport.getIncomingMovement().getMobileTerminalMemberNumber(), CoreMatchers.is(channel.getMemberNumber()));
    }

    private SetReportRequest sendPositionAndReturnResponse(AssetDTO asset1, MobileTerminalDto mobileTerminal1) throws Exception {
        Instant timestamp = Instant.now();
        String fluxEndpoint = "DNK";

        CustomRuleType flagStateRule = CustomRuleBuilder.getBuilder()
                .setName("Flag state => FLUX DNK")
                .rule(CriteriaType.ASSET, SubCriteriaType.ASSET_IRCS,
                        ConditionType.EQ, asset1.getIrcs())
                .action(ActionType.SEND_REPORT, VMSSystemHelper.FLUX_NAME, fluxEndpoint)
                .action(ActionType.CREATE_TICKET, "Dummy ticket")
                .build();

        CustomRuleType createdCustomRule = CustomRuleHelper.createCustomRule(flagStateRule);
        assertNotNull(createdCustomRule);

        // create the positionreport only containing DNID and MEMBER_NUMBER  OBS NO ASSET REFERENCES AT ALL
        LatLong position = new LatLong(11d, 56d, new Date());
        try (TopicListener topicListener = new TopicListener(SELECTOR)) {
            InmarsatPluginMock.sendInmarsatPosition(mobileTerminal1, position);

            CustomRuleHelper.pollTicketCreated();
            CustomRuleHelper.assertRuleTriggered(createdCustomRule, timestamp);

            SetReportRequest setReportRequest = topicListener.listenOnEventBusForSpecificMessage(SetReportRequest.class);

            assertThat(setReportRequest.getReport().getRecipient(), is(fluxEndpoint));
            return setReportRequest;
        }
    }
}
