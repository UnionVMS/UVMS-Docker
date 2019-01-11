package eu.europa.ec.fisheries.uvms.docker.validation.system.vms;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import javax.jms.JMSException;

import eu.europa.ec.fisheries.uvms.docker.validation.movement.MovementHelper;
import org.junit.*;
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
import eu.europa.ec.fisheries.uvms.docker.validation.mobileterminal.MobileTerminalTestHelper;
import eu.europa.ec.fisheries.uvms.docker.validation.mobileterminal.dto.ChannelDto;
import eu.europa.ec.fisheries.uvms.docker.validation.mobileterminal.dto.MobileTerminalDto;
import eu.europa.ec.fisheries.uvms.docker.validation.system.helper.CustomRuleBuilder;
import eu.europa.ec.fisheries.uvms.docker.validation.system.helper.CustomRuleHelper;
import eu.europa.ec.fisheries.uvms.exchange.model.mapper.ExchangeModuleRequestMapper;
import eu.europa.ec.fisheries.uvms.exchange.model.mapper.JAXBMarshaller;

import javax.jms.TextMessage;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Set;
import java.util.TimeZone;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;

public class InmarsatSanityIT extends AbstractRest {

    private static final String SELECTOR = "ServiceName='eu.europa.ec.fisheries.uvms.plugins.flux.movement'";
    private static final long TIMEOUT = 10000;

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

        LocalDateTime timestamp = LocalDateTime.now(ZoneOffset.UTC);

        // create testdata
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

        // extract DNID and MEMBERNUMBER from testdata created above
        Set<ChannelDto> channels = mobileTerminal.getChannels();
        Assert.assertEquals(1, channels.size());
        ChannelDto channel = channels.iterator().next();

        String dnid = channel.getDNID();
        String memberNumber = channel.getMemberNumber();

        // create the positionreport only containing DNID and MEMBER_NUMBER  OBS NO ASSET REFERENCES AT ALL
        SetReportMovementType reportType = createReportType(dnid, memberNumber);
        String text =
                ExchangeModuleRequestMapper.createSetMovementReportRequest(
                        reportType,
                        "TWOSTAGE",
                        null,
                        DateUtils.nowUTC().toDate(),
                        null,
                        PluginType.SATELLITE_RECEIVER,
                        "TWOSTAGE",
                        null);

        messageHelper.sendMessage("UVMSExchangeEvent", text);
        // WAITFOR AND CHECK RESULTS
        TextMessage message = (TextMessage) messageHelper.listenOnEventBus(SELECTOR, TIMEOUT);
        assertThat(message, is(notNullValue()));

        CustomRuleHelper.assertRuleTriggered(createdCustomRule, timestamp);

        SetReportRequest setReportRequest = JAXBMarshaller.unmarshallTextMessage(message, SetReportRequest.class);

        assertThat(setReportRequest.getReport().getRecipient(), is(fluxEndpoint));

        MovementType movement = setReportRequest.getReport().getMovement();
        assertThat(movement.getAssetName(), is(asset.getName()));
        assertThat(movement.getIrcs(), is(asset.getIrcs()));
    }

    private SetReportMovementType createReportType(String theDnid, String theMemberNumber) {

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
        return reportType;
    }
}
