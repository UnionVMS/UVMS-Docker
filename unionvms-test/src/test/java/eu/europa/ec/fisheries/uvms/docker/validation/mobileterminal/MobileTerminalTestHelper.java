package eu.europa.ec.fisheries.uvms.docker.validation.mobileterminal;

import eu.europa.ec.fisheries.schema.mobileterminal.polltypes.v1.*;
import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.MobileTerminalListQuery;
import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.PluginCapability;
import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.PluginCapabilityType;
import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.PluginService;
import eu.europa.ec.fisheries.uvms.asset.client.model.AssetDTO;
import eu.europa.ec.fisheries.uvms.docker.validation.common.AbstractHelper;
import eu.europa.ec.fisheries.uvms.docker.validation.mobileterminal.dto.ChannelDto;
import eu.europa.ec.fisheries.uvms.docker.validation.mobileterminal.dto.CreatePollResultDto;
import eu.europa.ec.fisheries.uvms.docker.validation.mobileterminal.dto.MobileTerminalDto;
import eu.europa.ec.fisheries.uvms.docker.validation.mobileterminal.dto.MobileTerminalPluginDto;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

public final class MobileTerminalTestHelper extends AbstractHelper {

	public static CreatePollResultDto createPollWithMT_Helper(AssetDTO testAsset, PollType pollType, MobileTerminalDto terminal) {
        terminal = assignMobileTerminal(testAsset, terminal);
        assertNotNull(terminal.getAsset());

        String comChannelId = terminal.getChannels().iterator().next().getId().toString();

        PollRequestType pollRequestType = new PollRequestType();
        pollRequestType.setPollType(pollType);
        pollRequestType.setUserName("vms_admin_com");
        pollRequestType.setComment("Manual poll created by test");

        PollMobileTerminal pollMobileTerminal = new PollMobileTerminal();
        pollMobileTerminal.setComChannelId(comChannelId);
        pollMobileTerminal.setConnectId(testAsset.getId().toString());
        pollMobileTerminal.setMobileTerminalId(terminal.getId().toString());

        List<PollAttribute> pollAttributes = pollRequestType.getAttributes();

        PollAttribute frequency = new PollAttribute();
        PollAttribute startDate = new PollAttribute();
        PollAttribute endDate = new PollAttribute();

        pollAttributes.add(frequency);
        frequency.setKey(PollAttributeType.FREQUENCY);
        frequency.setValue("1000");

        pollAttributes.add(startDate);
        startDate.setKey(PollAttributeType.START_DATE);
        startDate.setValue(getDateAsString(2001, Calendar.JANUARY, 7, 1, 7, 23, 45));

        pollAttributes.add(endDate);
        endDate.setKey(PollAttributeType.END_DATE);
        endDate.setValue(getDateAsString(2027, Calendar.DECEMBER, 24, 11, 45, 7, 980));

        pollRequestType.getMobileTerminals().add(pollMobileTerminal);

        CreatePollResultDto response = getWebTarget()
                .path("asset/rest/poll")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getValidJwtToken())
                .post(Entity.json(pollRequestType), CreatePollResultDto.class);

        assertNotNull(response);
        return response;
	}

	static CreatePollResultDto createConfigPollWithMT_Helper(AssetDTO testAsset, MobileTerminalDto terminal) {
		terminal = assignMobileTerminal(testAsset, terminal);
		assertNotNull(terminal.getAsset());

		PluginCapability configurable = new PluginCapability();
		configurable.setName(PluginCapabilityType.CONFIGURABLE);
		configurable.setValue("TRUE");

		PluginCapability pollable = new PluginCapability();
		pollable.setName(PluginCapabilityType.POLLABLE);
		pollable.setValue("TRUE");

        PollRequestType pollRequest = new PollRequestType();

		PollMobileTerminal pmt = new PollMobileTerminal();
		pmt.setComChannelId(terminal.getChannels().iterator().next().getId().toString());
		pmt.setConnectId(terminal.getAsset().getId().toString());
		pmt.setMobileTerminalId(terminal.getId().toString());
		pollRequest.getMobileTerminals().add(pmt);

		PollAttribute attrFrequency = new PollAttribute();
		attrFrequency.setKey(PollAttributeType.REPORT_FREQUENCY);
		attrFrequency.setValue("11000");

		PollAttribute attrGracePeriod = new PollAttribute();
		attrGracePeriod.setKey(PollAttributeType.GRACE_PERIOD);
		attrGracePeriod.setValue("11020");

		PollAttribute attrInPortGrace = new PollAttribute();
		attrInPortGrace.setKey(PollAttributeType.IN_PORT_GRACE);
		attrInPortGrace.setValue("11040");

		pollRequest.getAttributes().addAll(Arrays.asList(attrFrequency, attrGracePeriod, attrInPortGrace));

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
		assertNull(createdTerminal.getAsset());

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
        return createdTerminal;
	}
	
	static MobileTerminalDto getMobileTerminalById(UUID uuid) {
		return getWebTarget()
				.path("asset/rest/mobileterminal")
				.path(uuid.toString())
				.request(MediaType.APPLICATION_JSON)
				.header(HttpHeaders.AUTHORIZATION, getValidJwtToken())
				.get(MobileTerminalDto.class);
	}

	static MobileTerminalDto updateMobileTerminal(MobileTerminalDto mobileTerminal) {
		return getWebTarget()
				.path("asset/rest/mobileterminal")
				.queryParam("comment", "MobileTerminal is Archived")
				.request(MediaType.APPLICATION_JSON)
				.header(HttpHeaders.AUTHORIZATION, getValidJwtToken())
				.put(Entity.json(mobileTerminal), MobileTerminalDto.class);
	}

	static Response getMobileTerminalList(MobileTerminalListQuery queryRequest) {
		return getWebTarget()
				.path("asset/rest/mobileterminal/list")
				.request(MediaType.APPLICATION_JSON)
				.header(HttpHeaders.AUTHORIZATION, getValidJwtToken())
				.post(Entity.json(queryRequest));
	}

	static MobileTerminalDto activateMobileTerminal(UUID uuid) {
		return getWebTarget()
				.path("asset/rest/mobileterminal/status/activate")
				.queryParam("comment", "Activate MobileTerminal")
				.request(MediaType.APPLICATION_JSON)
				.header(HttpHeaders.AUTHORIZATION, getValidJwtToken())
				.put(Entity.json(uuid), MobileTerminalDto.class);
	}

	static MobileTerminalDto removeMobileTerminal(UUID uuid) {
		return getWebTarget()
				.path("asset/rest/mobileterminal/status/remove")
				.queryParam("comment", "Remove MobileTerminal")
				.request(MediaType.APPLICATION_JSON)
				.header(HttpHeaders.AUTHORIZATION, getValidJwtToken())
				.put(Entity.json(uuid), MobileTerminalDto.class);
	}

	static MobileTerminalDto inactivateMobileTerminal(UUID uuid) {
		return getWebTarget()
				.path("asset/rest/mobileterminal/status/inactivate")
				.queryParam("comment", "InActivate MobileTerminal")
				.request(MediaType.APPLICATION_JSON)
				.header(HttpHeaders.AUTHORIZATION, getValidJwtToken())
				.put(Entity.json(uuid), MobileTerminalDto.class);
	}

	public static MobileTerminalDto assignMobileTerminal(AssetDTO asset, MobileTerminalDto mobileTerminal) {

		return getWebTarget()
				.path("asset/rest/mobileterminal/assign")
				.queryParam("comment", "comment")
				.queryParam("connectId", asset.getId())
				.request(MediaType.APPLICATION_JSON)
				.header(HttpHeaders.AUTHORIZATION, getValidJwtToken())
				.put(Entity.json(mobileTerminal.getId()), MobileTerminalDto.class);
	}

	static MobileTerminalDto unAssignMobileTerminal(AssetDTO asset, MobileTerminalDto mobileTerminal) {

		return getWebTarget()
				.path("asset/rest/mobileterminal/unassign")
				.queryParam("comment", "MobileTerminal Unassigned")
				.queryParam("connectId", asset.getId())
				.request(MediaType.APPLICATION_JSON)
				.header(HttpHeaders.AUTHORIZATION, getValidJwtToken())
				.put(Entity.json(mobileTerminal.getId()), MobileTerminalDto.class);
	}

	static List<MobileTerminalDto> getMobileTerminalHistoryList(UUID uuid) {
		return getWebTarget()
				.path("asset/rest/mobileterminal/history")
				.path(uuid.toString())
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
		mobileTerminal.setInactivated(false);

		mobileTerminal.setSatelliteNumber("S" + generateARandomStringWithMaxLength(4));
		mobileTerminal.setAntenna("A");
		mobileTerminal.setTransceiverType("A");
		mobileTerminal.setSoftwareVersion("A");

		ChannelDto channel = new ChannelDto();
		channel.setName("VMS");
		channel.setFrequencyGracePeriod("54000");
		channel.setMemberNumber(generateARandomStringWithMaxLength(3));
		channel.setExpectedFrequency("7200");
		channel.setExpectedFrequencyInPort("10800");
		channel.setLesDescription("Thrane&Thrane");
		channel.setDNID("1" + generateARandomStringWithMaxLength(3));
		channel.setInstalledBy("Mike Great");
		channel.setArchived(false);
		channel.setActive(false);
		channel.setConfigChannel(true);
		channel.setDefaultChannel(true);
		channel.setPollChannel(true);
		channel.setMobileTerminal(mobileTerminal);

		mobileTerminal.setConfigChannel(channel);
		mobileTerminal.setDefaultChannel(channel);
		mobileTerminal.setPollChannel(channel);

		mobileTerminal.getChannels().clear();
		mobileTerminal.getChannels().add(channel);

		MobileTerminalPluginDto plugin = new MobileTerminalPluginDto();
		plugin.setPluginServiceName("eu.europa.ec.fisheries.uvms.plugins.inmarsat");
		plugin.setName("Thrane&Thrane");
		plugin.setPluginSatelliteType("INMARSAT_C");
		plugin.setPluginInactive(false);
		mobileTerminal.setPlugin(plugin);

		return mobileTerminal;
	}
}
