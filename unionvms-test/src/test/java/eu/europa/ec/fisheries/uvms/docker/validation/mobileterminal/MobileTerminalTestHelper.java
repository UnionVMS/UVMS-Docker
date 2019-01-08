package eu.europa.ec.fisheries.uvms.docker.validation.mobileterminal;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.fluent.Request;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import eu.europa.ec.fisheries.schema.mobileterminal.polltypes.v1.PollAttribute;
import eu.europa.ec.fisheries.schema.mobileterminal.polltypes.v1.PollAttributeType;
import eu.europa.ec.fisheries.schema.mobileterminal.polltypes.v1.PollMobileTerminal;
import eu.europa.ec.fisheries.schema.mobileterminal.polltypes.v1.PollRequestType;
import eu.europa.ec.fisheries.schema.mobileterminal.polltypes.v1.PollType;
import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.ComChannelAttribute;
import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.ComChannelCapability;
import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.ComChannelType;
import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.MobileTerminalAssignQuery;
import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.MobileTerminalAttribute;
import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.MobileTerminalSource;
import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.MobileTerminalType;
import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.Plugin;
import eu.europa.ec.fisheries.uvms.asset.client.model.AssetDTO;
import eu.europa.ec.fisheries.uvms.docker.validation.asset.AssetTestHelper;
import eu.europa.ec.fisheries.uvms.docker.validation.common.AbstractHelper;

public final class MobileTerminalTestHelper extends AbstractHelper {

	public static Map<String, Object> createPoll_Helper(AssetDTO testAsset) throws Exception {
		MobileTerminalType createdMobileTerminalType = createMobileTerminalType();

		{
			MobileTerminalAssignQuery mobileTerminalAssignQuery = new MobileTerminalAssignQuery();
			mobileTerminalAssignQuery.setMobileTerminalId(createdMobileTerminalType.getMobileTerminalId());
			mobileTerminalAssignQuery.setConnectId(testAsset.getId().toString());
			// Assign first
			final HttpResponse response = Request
					.Post(getBaseUrl() + "asset/rest/mobileterminal/assign?comment=comment")
					.setHeader("Content-Type", "application/json").setHeader("Authorization", getValidJwtToken())
					.bodyByteArray(writeValueAsString(mobileTerminalAssignQuery).getBytes()).execute().returnResponse();

			Map<String, Object> dataMap = checkSuccessResponseReturnMap(response);
		}

		String comChannelId = createdMobileTerminalType.getChannels().get(0).getGuid();

		PollRequestType pollRequestType = new PollRequestType();
		pollRequestType.setPollType(PollType.PROGRAM_POLL);
		pollRequestType.setUserName("vms_admin_com");
		pollRequestType.setComment("Manual poll created by test");

		PollMobileTerminal pollMobileTerminal = new PollMobileTerminal();
		pollMobileTerminal.setComChannelId(comChannelId);
		pollMobileTerminal.setConnectId(testAsset.getId().toString());
		pollMobileTerminal.setMobileTerminalId(createdMobileTerminalType.getMobileTerminalId().getGuid());

		List<MobileTerminalAttribute> mobileTerminalAttributes = createdMobileTerminalType.getAttributes();
		List<PollAttribute> pollAttributes = pollRequestType.getAttributes();

		for (MobileTerminalAttribute mobileTerminalAttribute : mobileTerminalAttributes) {
			String type = mobileTerminalAttribute.getType();
			String value = mobileTerminalAttribute.getValue();
			PollAttribute pollAttribute = new PollAttribute();
			try {
				PollAttributeType pollAttributeType = PollAttributeType.valueOf(type);
				pollAttribute.setKey(pollAttributeType);
				pollAttribute.setValue(value);
				pollAttributes.add(pollAttribute);
			} catch (RuntimeException rte) {
				// ignore
			}
		}

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

		final HttpResponse response = Request.Post(getBaseUrl() + "asset/rest/poll")
				.setHeader("Content-Type", "application/json").setHeader("Authorization", getValidJwtToken())
				.bodyByteArray(writeValueAsString(pollRequestType).getBytes()).execute().returnResponse();

		Map<String, Object> dataMap = checkSuccessResponseReturnMap(response);

		return dataMap;
	}

	public static MobileTerminalType createMobileTerminalType() throws IOException, ClientProtocolException,
			JsonProcessingException, JsonParseException, JsonMappingException {
		MobileTerminalType mobileTerminalRequest = new MobileTerminalType();
		mobileTerminalRequest.setSource(MobileTerminalSource.INTERNAL);
		mobileTerminalRequest.setType("INMARSAT_C");

		List<MobileTerminalAttribute> attributes = mobileTerminalRequest.getAttributes();
		addAttribute(attributes, "SERIAL_NUMBER", AssetTestHelper.generateARandomStringWithMaxLength(10));
		addAttribute(attributes, "SATELLITE_NUMBER", "S" + AssetTestHelper.generateARandomStringWithMaxLength(4));
		addAttribute(attributes, "ANTENNA", "A");
		addAttribute(attributes, "TRANSCEIVER_TYPE", "A");
		addAttribute(attributes, "SOFTWARE_VERSION", "A");

		List<ComChannelType> channels = mobileTerminalRequest.getChannels();
		ComChannelType comChannelType = new ComChannelType();
		channels.add(comChannelType);
		comChannelType.setGuid(UUID.randomUUID().toString());
		comChannelType.setName("VMS");

		addChannelAttribute(comChannelType, "FREQUENCY_GRACE_PERIOD", "54000");
		addChannelAttribute(comChannelType, "MEMBER_NUMBER", "100");
		addChannelAttribute(comChannelType, "FREQUENCY_EXPECTED", "7200");
		addChannelAttribute(comChannelType, "FREQUENCY_IN_PORT", "10800");
		addChannelAttribute(comChannelType, "LES_DESCRIPTION", "Thrane&Thrane");
		addChannelAttribute(comChannelType, "DNID", "1" + AssetTestHelper.generateARandomStringWithMaxLength(3));
		addChannelAttribute(comChannelType, "INSTALLED_BY", "Mike Great");

		addChannelCapability(comChannelType, "POLLABLE", true);
		addChannelCapability(comChannelType, "CONFIGURABLE", true);
		addChannelCapability(comChannelType, "DEFAULT_REPORTING", true);

		Plugin plugin = new Plugin();
		plugin.setServiceName("eu.europa.ec.fisheries.uvms.plugins.inmarsat");
		plugin.setLabelName("Thrane&Thrane");
		plugin.setSatelliteType("INMARSAT_C");
		plugin.setInactive(false);

		mobileTerminalRequest.setPlugin(plugin);

		String ep = getBaseUrl() + "asset/rest/mobileterminal/";
		String jwt = getValidJwtToken();
		final HttpResponse response = Request.Post(getBaseUrl() + "asset/rest/mobileterminal")
				.setHeader("Content-Type", "application/json").setHeader("Authorization", getValidJwtToken())
				.bodyByteArray(writeValueAsString(mobileTerminalRequest).getBytes()).execute().returnResponse();

		return checkSuccessResponseReturnObject(response, MobileTerminalType.class);
	}

	private static void addChannelCapability(ComChannelType comChannelType, String type, boolean value) {
		ComChannelCapability channelCapability = new ComChannelCapability();

		channelCapability.setType(type);
		channelCapability.setValue(value);
		comChannelType.getCapabilities().add(channelCapability);
	}

	private static void addChannelAttribute(ComChannelType comChannelType, String type, String value) {
		ComChannelAttribute channelAttribute = new ComChannelAttribute();
		channelAttribute.setType(type);
		channelAttribute.setValue(value);
		comChannelType.getAttributes().add(channelAttribute);
	}

	private static void addAttribute(List<MobileTerminalAttribute> attributes, String type, String value) {
		MobileTerminalAttribute serialNumberMobileTerminalAttribute = new MobileTerminalAttribute();
		serialNumberMobileTerminalAttribute.setType(type);
		serialNumberMobileTerminalAttribute.setValue(value);
		attributes.add(serialNumberMobileTerminalAttribute);
	}

	public static Map<String, Object> assignMobileTerminal(AssetDTO testAsset, MobileTerminalType createdMobileTerminalType)
			throws Exception {

		MobileTerminalAssignQuery mobileTerminalAssignQuery = new MobileTerminalAssignQuery();
		mobileTerminalAssignQuery.setMobileTerminalId(createdMobileTerminalType.getMobileTerminalId());
		mobileTerminalAssignQuery.setConnectId(testAsset.getId().toString());
		createdMobileTerminalType.setConnectId(testAsset.getId().toString());
		
		final HttpResponse response = Request
				.Post(getBaseUrl() + "asset/rest/mobileterminal/assign?comment=comment")
				.setHeader("Content-Type", "application/json").setHeader("Authorization", getValidJwtToken())
				.bodyByteArray(writeValueAsString(mobileTerminalAssignQuery).getBytes()).execute().returnResponse();

		Map<String, Object> dataMap = checkSuccessResponseReturnMap(response);

		return dataMap;
	}

}
