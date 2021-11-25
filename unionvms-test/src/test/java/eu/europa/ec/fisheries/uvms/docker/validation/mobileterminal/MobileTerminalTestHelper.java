package eu.europa.ec.fisheries.uvms.docker.validation.mobileterminal;

import eu.europa.ec.fisheries.schema.mobileterminal.polltypes.v1.*;
import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.PluginCapability;
import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.PluginCapabilityType;
import eu.europa.ec.fisheries.uvms.asset.client.model.AssetDTO;
import eu.europa.ec.fisheries.uvms.docker.validation.common.AbstractHelper;
import eu.europa.ec.fisheries.uvms.docker.validation.mobileterminal.dto.*;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import java.time.Duration;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

public final class MobileTerminalTestHelper extends AbstractHelper {

	public static CreatePollResultDto createPollWithMT_Helper(AssetDTO testAsset, PollType pollType, MobileTerminalDto terminal) {
        terminal = assignMobileTerminal(testAsset, terminal);
        assertNotNull(terminal.getAssetId());

        String comChannelId = terminal.getChannels().iterator().next().getId().toString();

        PollRequestType pollRequestType = new PollRequestType();
        pollRequestType.setPollType(pollType);
        pollRequestType.setUserName("vms_admin_com");
        pollRequestType.setComment("Manual poll created by test");

        PollMobileTerminal pollMobileTerminal = new PollMobileTerminal();
        pollMobileTerminal.setComChannelId(comChannelId);
        pollMobileTerminal.setMobileTerminalId(terminal.getId().toString());

		List<PollAttribute> pollAttributes = pollRequestType.getAttributes();

		if(pollType.equals(PollType.PROGRAM_POLL)) {
			PollAttribute frequency = new PollAttribute();
			frequency.setKey(PollAttributeType.FREQUENCY);
			frequency.setValue("1000");
			pollAttributes.add(frequency);

			PollAttribute startDate = new PollAttribute();
			startDate.setKey(PollAttributeType.START_DATE);
			startDate.setValue(getDateAsString(2001, Calendar.JANUARY, 7, 1, 7, 23, 45));
			pollAttributes.add(startDate);

			PollAttribute endDate = new PollAttribute();
			endDate.setKey(PollAttributeType.END_DATE);
			endDate.setValue(getDateAsString(2027, Calendar.DECEMBER, 24, 11, 45, 7, 980));
			pollAttributes.add(endDate);
		}

        pollRequestType.getMobileTerminals().add(pollMobileTerminal);

        CreatePollResultDto response = getWebTarget()
                .path("asset/rest/poll")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getValidJwtToken())
                .post(Entity.json(pollRequestType), CreatePollResultDto.class);

        assertNotNull(response);
        return response;
	}

	public static CreatePollResultDto createConfigPollWithMT_Helper(AssetDTO testAsset, MobileTerminalDto terminal) {
		terminal = assignMobileTerminal(testAsset, terminal);
		assertNotNull(terminal.getAssetId());

		PluginCapability configurable = new PluginCapability();
		configurable.setName(PluginCapabilityType.CONFIGURABLE);
		configurable.setValue("TRUE");

		PluginCapability pollable = new PluginCapability();
		pollable.setName(PluginCapabilityType.POLLABLE);
		pollable.setValue("TRUE");

        PollRequestType pollRequest = new PollRequestType();

		PollMobileTerminal pmt = new PollMobileTerminal();
		pmt.setComChannelId(terminal.getChannels().iterator().next().getId().toString());
		pmt.setMobileTerminalId(terminal.getId().toString());
		pollRequest.getMobileTerminals().add(pmt);

		PollAttribute attrFrequency = new PollAttribute();
		attrFrequency.setKey(PollAttributeType.REPORT_FREQUENCY);
		attrFrequency.setValue("7200"); // 2:00

		PollAttribute attrGracePeriod = new PollAttribute();
		attrGracePeriod.setKey(PollAttributeType.GRACE_PERIOD);
		attrGracePeriod.setValue("48600"); // 13:30

		PollAttribute attrInPortGrace = new PollAttribute();
		attrInPortGrace.setKey(PollAttributeType.IN_PORT_GRACE);
		attrInPortGrace.setValue("48600");

		pollRequest.getAttributes().addAll(
				Arrays.asList(attrFrequency, attrGracePeriod, attrInPortGrace));

		pollRequest.setPollType(PollType.CONFIGURATION_POLL);
		pollRequest.setComment("Configuration poll created by test");
		pollRequest.setUserName("vms_admin_com");

		CreatePollResultDto createdPoll = getWebTarget()
				.path("asset/rest/poll")
				.request(MediaType.APPLICATION_JSON)
				.header(HttpHeaders.AUTHORIZATION, getValidJwtToken())
				.post(Entity.json(pollRequest), CreatePollResultDto.class);

		return createdPoll;
	}
	
	public static CreatePollResultDto createPoll_Helper(AssetDTO testAsset, PollType pollType) {
		MobileTerminalDto createdTerminal = createMobileTerminal();

		assertNotNull(createdTerminal);
		assertNull(createdTerminal.getAssetId());

		return createPollWithMT_Helper(testAsset, pollType, createdTerminal);
	}

	public static MobileTerminalDto createMobileTerminal() {
	    MobileTerminalDto terminal = createBasicMobileTerminal();
		return persistMobileTerminal(terminal);
	}

	public static MobileTerminalDto persistMobileTerminal(MobileTerminalDto terminal) {
		MobileTerminalDto createdTerminal = getWebTarget()
                .path("asset/rest/mobileterminal")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getValidJwtToken())
                .post(Entity.json(terminal), MobileTerminalDto.class);

        assertNotNull(createdTerminal);
        assertNotNull(createdTerminal.getId());
        return createdTerminal;
	}
	
	public static MobileTerminalDto getMobileTerminalById(UUID uuid) {
		return getWebTarget()
				.path("asset/rest/mobileterminal")
				.path(uuid.toString())
				.request(MediaType.APPLICATION_JSON)
				.header(HttpHeaders.AUTHORIZATION, getValidJwtToken())
				.get(MobileTerminalDto.class);
	}

	public static MobileTerminalDto updateMobileTerminal(MobileTerminalDto mobileTerminal) {
		return getWebTarget()
				.path("asset/rest/mobileterminal")
				.queryParam("comment", "MobileTerminal is Archived")
				.request(MediaType.APPLICATION_JSON)
				.header(HttpHeaders.AUTHORIZATION, getValidJwtToken())
				.put(Entity.json(mobileTerminal), MobileTerminalDto.class);
	}

	public static Response getMobileTerminalList(MTQuery queryRequest) {
		return getWebTarget()
				.path("asset/rest/mobileterminal/list")
				.request(MediaType.APPLICATION_JSON)
				.header(HttpHeaders.AUTHORIZATION, getValidJwtToken())
				.post(Entity.json(queryRequest));
	}

	public static MobileTerminalDto activateMobileTerminal(UUID uuid) {
		return getWebTarget()
				.path("asset/rest/mobileterminal")
				.path(uuid.toString())
				.path("status")
				.queryParam("comment", "Activate MobileTerminal")
				.request(MediaType.APPLICATION_JSON)
				.header(HttpHeaders.AUTHORIZATION, getValidJwtToken())
				.put(Entity.json("\"ACTIVE\""), MobileTerminalDto.class);
	}

	public static MobileTerminalDto removeMobileTerminal(UUID uuid) {
		return getWebTarget()
				.path("asset/rest/mobileterminal")
				.path(uuid.toString())
				.path("status")
				.queryParam("comment", "Remove MobileTerminal")
				.request(MediaType.APPLICATION_JSON)
				.header(HttpHeaders.AUTHORIZATION, getValidJwtToken())
				.put(Entity.json("\"ARCHIVE\""), MobileTerminalDto.class);
	}

	public static MobileTerminalDto inactivateMobileTerminal(UUID uuid) {
		return getWebTarget()
				.path("asset/rest/mobileterminal")
				.path(uuid.toString())
				.path("status")
				.queryParam("comment", "InActivate MobileTerminal")
				.request(MediaType.APPLICATION_JSON)
				.header(HttpHeaders.AUTHORIZATION, getValidJwtToken())
				.put(Entity.json("\"INACTIVE\""), MobileTerminalDto.class);
	}

	public static MobileTerminalDto assignMobileTerminal(AssetDTO asset, MobileTerminalDto mobileTerminal) {

		return getWebTarget()
				.path("asset/rest/mobileterminal")
				.path(mobileTerminal.getId().toString())
				.path("assign")
				.path(asset.getId().toString())
				.queryParam("comment", "comment")
				.request(MediaType.APPLICATION_JSON)
				.header(HttpHeaders.AUTHORIZATION, getValidJwtToken())
				.put(Entity.json(""), MobileTerminalDto.class);
	}

	public static MobileTerminalDto unAssignMobileTerminal(AssetDTO asset, MobileTerminalDto mobileTerminal) {

		return getWebTarget()
				.path("asset/rest/mobileterminal")
				.path(mobileTerminal.getId().toString())
				.path("unassign")
				.path(asset.getId().toString())
				.queryParam("comment", "MobileTerminal Unassigned")
				.request(MediaType.APPLICATION_JSON)
				.header(HttpHeaders.AUTHORIZATION, getValidJwtToken())
				.put(Entity.json(""), MobileTerminalDto.class);
	}

	public static List<MobileTerminalDto> getMobileTerminalHistoryList(UUID uuid) {
		return getWebTarget()
				.path("asset/rest/mobileterminal")
				.path(uuid.toString())
				.path("history")
				.request(MediaType.APPLICATION_JSON)
				.header(HttpHeaders.AUTHORIZATION, getValidJwtToken())
				.get(new GenericType<List<MobileTerminalDto>>(){});
	}

	public static MobileTerminalDto createBasicMobileTerminal() {
		MobileTerminalDto mobileTerminal = new MobileTerminalDto();
		mobileTerminal.setSource("INTERNAL");
		mobileTerminal.setMobileTerminalType("INMARSAT_C");
		mobileTerminal.setSerialNo(generateARandomStringWithMaxLength(10));
		mobileTerminal.setArchived(false);
		mobileTerminal.setActive(true);

		mobileTerminal.setSatelliteNumber("4" + generateARandomStringWithMaxLength(8));
		mobileTerminal.setAntenna("A");
		mobileTerminal.setTransceiverType("A");
		mobileTerminal.setSoftwareVersion("A");

		ChannelDto channel = new ChannelDto();
		channel.setName("VMS");
		channel.setFrequencyGracePeriod(Duration.ofSeconds(54000));
		channel.setMemberNumber("1" + generateARandomStringWithMaxLength(2));
		channel.setExpectedFrequency(Duration.ofSeconds(7200));
		channel.setExpectedFrequencyInPort(Duration.ofSeconds(10800));
		channel.setLesDescription("Thrane&Thrane");
		channel.setDnid("1" + generateARandomStringWithMaxLength(3));
		channel.setInstalledBy("Mike Great");
		channel.setArchived(false);
		channel.setActive(false);
		channel.setConfigChannel(true);
		channel.setDefaultChannel(true);
		channel.setPollChannel(true);
		channel.setMobileTerminal(mobileTerminal);

		mobileTerminal.getChannels().clear();
		mobileTerminal.getChannels().add(channel);

		mobileTerminal.setWestAtlanticOceanRegion(false); // 0
		mobileTerminal.setEastAtlanticOceanRegion(false); // 1
		mobileTerminal.setPacificOceanRegion(false); // 2
		mobileTerminal.setIndianOceanRegion(true); // 3

		MobileTerminalPluginDto plugin = new MobileTerminalPluginDto();
		plugin.setPluginServiceName("eu.europa.ec.fisheries.uvms.plugins.inmarsat");
		plugin.setName("Thrane&Thrane");
		plugin.setPluginSatelliteType("INMARSAT_C");
		plugin.setPluginInactive(false);
		mobileTerminal.setPlugin(plugin);

		return mobileTerminal;
	}
}
