package eu.europa.ec.fisheries.uvms.docker.validation.system.vms;

import eu.europa.ec.fisheries.schema.exchange.movement.mobileterminal.v1.IdList;
import eu.europa.ec.fisheries.schema.exchange.movement.mobileterminal.v1.IdType;
import eu.europa.ec.fisheries.schema.exchange.movement.mobileterminal.v1.MobileTerminalId;
import eu.europa.ec.fisheries.schema.exchange.movement.v1.*;
import eu.europa.ec.fisheries.schema.exchange.plugin.types.v1.PluginType;
import eu.europa.ec.fisheries.schema.exchange.plugin.v1.SetReportRequest;
import eu.europa.ec.fisheries.schema.movementrules.customrule.v1.*;
import eu.europa.ec.fisheries.uvms.asset.client.model.AssetDTO;
import eu.europa.ec.fisheries.uvms.commons.date.DateUtils;
import eu.europa.ec.fisheries.uvms.docker.validation.asset.AssetTestHelper;
import eu.europa.ec.fisheries.uvms.docker.validation.common.AbstractRest;
import eu.europa.ec.fisheries.uvms.docker.validation.common.MessageHelper;
import eu.europa.ec.fisheries.uvms.docker.validation.common.TopicListener;
import eu.europa.ec.fisheries.uvms.docker.validation.mobileterminal.MobileTerminalTestHelper;
import eu.europa.ec.fisheries.uvms.docker.validation.mobileterminal.dto.ChannelDto;
import eu.europa.ec.fisheries.uvms.docker.validation.mobileterminal.dto.MobileTerminalDto;
import eu.europa.ec.fisheries.uvms.docker.validation.movement.model.AlarmReport;
import eu.europa.ec.fisheries.uvms.docker.validation.system.helper.CustomRuleBuilder;
import eu.europa.ec.fisheries.uvms.docker.validation.system.helper.CustomRuleHelper;
import eu.europa.ec.fisheries.uvms.docker.validation.system.helper.SanityRuleHelper;
import eu.europa.ec.fisheries.uvms.exchange.model.mapper.ExchangeModuleRequestMapper;
import org.hamcrest.CoreMatchers;
import org.junit.*;

import javax.jms.JMSException;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Set;
import java.util.TimeZone;

import static org.hamcrest.CoreMatchers.is;

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
        OffsetDateTime timestamp = OffsetDateTime.now(ZoneOffset.UTC);

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
                .action(ActionType.SEND_TO_FLUX, fluxEndpoint)
                .build();

        CustomRuleType createdCustomRule = CustomRuleHelper.createCustomRule(flagStateRule);
        assertNotNull(createdCustomRule);

        // create the position report only containing DNID and MEMBER_NUMBER  OBS NO ASSET REFERENCES AT ALL
        String request = createReportRequest(mobileTerminal);
        try (TopicListener topicListener = new TopicListener(SELECTOR)) {
            messageHelper.sendMessage("UVMSExchangeEvent", request);

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
        mobileTerminal1.getChannels().iterator().next().setDNID(dnid);
        mobileTerminal1 = MobileTerminalTestHelper.persistMobileTerminal(mobileTerminal1);
        MobileTerminalTestHelper.assignMobileTerminal(asset1, mobileTerminal1);
        
        AssetDTO asset2 = AssetTestHelper.createTestAsset();
        MobileTerminalDto mobileTerminal2 = MobileTerminalTestHelper.createBasicMobileTerminal();
        mobileTerminal2.getChannels().iterator().next().setDNID(dnid);
        mobileTerminal2 = MobileTerminalTestHelper.persistMobileTerminal(mobileTerminal2);
        MobileTerminalTestHelper.assignMobileTerminal(asset2, mobileTerminal2);
        
        AssetDTO asset3 = AssetTestHelper.createTestAsset();
        MobileTerminalDto mobileTerminal3 = MobileTerminalTestHelper.createBasicMobileTerminal();
        mobileTerminal3.getChannels().iterator().next().setDNID(dnid);
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
        
        String request = createReportRequest(mobileTerminal);
        messageHelper.sendMessage("UVMSExchangeEvent", request);
        
        SanityRuleHelper.pollAlarmReportCreated();
        
        AlarmReport alarmReport = SanityRuleHelper.getLatestOpenAlarmReportSince(timestamp);
        ChannelDto channel = mobileTerminal.getChannels().iterator().next();
        assertThat(alarmReport.getIncomingMovement().getMobileTerminalDNID(), CoreMatchers.is(channel.getDNID()));
        assertThat(alarmReport.getIncomingMovement().getMobileTerminalMemberNumber(), CoreMatchers.is(channel.getMemberNumber()));
    }

    private SetReportRequest sendPositionAndReturnResponse(AssetDTO asset1, MobileTerminalDto mobileTerminal1) throws Exception {
        OffsetDateTime timestamp = OffsetDateTime.now(ZoneOffset.UTC);
        String fluxEndpoint = "DNK";

        CustomRuleType flagStateRule = CustomRuleBuilder.getBuilder()
                .setName("Flag state => FLUX DNK")
                .rule(CriteriaType.ASSET, SubCriteriaType.ASSET_IRCS,
                        ConditionType.EQ, asset1.getIrcs())
                .action(ActionType.SEND_TO_FLUX, fluxEndpoint)
                .build();

        CustomRuleType createdCustomRule = CustomRuleHelper.createCustomRule(flagStateRule);
        assertNotNull(createdCustomRule);

        // create the positionreport only containing DNID and MEMBER_NUMBER  OBS NO ASSET REFERENCES AT ALL
        String request = createReportRequest(mobileTerminal1);
        try (TopicListener topicListener = new TopicListener(SELECTOR)) {
            messageHelper.sendMessage("UVMSExchangeEvent", request);

            CustomRuleHelper.pollTicketCreated();
            CustomRuleHelper.assertRuleTriggered(createdCustomRule, timestamp);

            SetReportRequest setReportRequest = topicListener.listenOnEventBusForSpecificMessage(SetReportRequest.class);

            assertThat(setReportRequest.getReport().getRecipient(), is(fluxEndpoint));
            return setReportRequest;
        }
    }

    private String createReportRequest(MobileTerminalDto mobileTerminal) throws IllegalArgumentException {
        Set<ChannelDto> channels = mobileTerminal.getChannels();
        Assert.assertEquals(1, channels.size());
        ChannelDto channel = channels.iterator().next();

        String theDnid = channel.getDNID();
        String theMemberNumber = channel.getMemberNumber();
        
        SetReportMovementType reportType = new SetReportMovementType();
        MobileTerminalId mobileTerminalId = new MobileTerminalId();

        if(theDnid != null) {
            IdList dnid = new IdList();
            dnid.setType(IdType.DNID);
            dnid.setValue(theDnid);
            mobileTerminalId.getMobileTerminalIdList().add(dnid);
        }

        if(theMemberNumber != null) {
            IdList memberNumber = new IdList();
            memberNumber.setType(IdType.MEMBER_NUMBER);
            memberNumber.setValue(theMemberNumber);
            mobileTerminalId.getMobileTerminalIdList().add(memberNumber);
        }

        MovementBaseType movement = new MovementBaseType();
        movement.setMobileTerminalId(mobileTerminalId);
        movement.setComChannelType(MovementComChannelType.MOBILE_TERMINAL);
        movement.setMobileTerminalId(mobileTerminalId);
        movement.setMovementType(MovementTypeType.POS);

        MovementPoint mp = new MovementPoint();
        mp.setAltitude(0.0);
        mp.setLatitude(1d);
        mp.setLongitude(1d);
        movement.setPosition(mp);

        movement.setPositionTime(new Date());
        movement.setReportedCourse(42d);
        movement.setReportedSpeed(43d);
        movement.setSource(MovementSourceType.INMARSAT_C);
        movement.setStatus("11");

        reportType.setMovement(movement);
        GregorianCalendar gcal =
                (GregorianCalendar) GregorianCalendar.getInstance(TimeZone.getTimeZone("UTC"));
        reportType.setTimestamp(gcal.getTime());
        reportType.setPluginName("eu.europa.ec.fisheries.uvms.plugins.inmarsat");
        reportType.setPluginType(PluginType.SATELLITE_RECEIVER);

        reportType.setMovement(movement);
        
        return ExchangeModuleRequestMapper.createSetMovementReportRequest(
                reportType,
                "TWOSTAGE",
                null,
                Instant.now(),
                PluginType.SATELLITE_RECEIVER,
                "TWOSTAGE",
                null);
    }
}
