package eu.europa.ec.fisheries.uvms.docker.validation.mobileterminal;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.fluent.Request;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.ComChannelAttribute;
import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.ComChannelCapability;
import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.ComChannelType;
import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.MobileTerminalAttribute;
import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.MobileTerminalId;
import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.MobileTerminalSource;
import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.MobileTerminalType;
import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.Plugin;
import eu.europa.ec.fisheries.uvms.docker.validation.asset.AssetTestHelper;
import eu.europa.ec.fisheries.uvms.docker.validation.common.AbstractRestServiceTest;

public class AbstractMobileTerminalTest extends AbstractRestServiceTest {

	protected MobileTerminalType createMobileTerminalType() throws IOException, ClientProtocolException,
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
		addChannelAttribute(comChannelType, "LES_DESCRIPTION", "twostage");
		addChannelAttribute(comChannelType, "DNID", "1" + AssetTestHelper.generateARandomStringWithMaxLength(3));
		addChannelAttribute(comChannelType, "INSTALLED_BY", "Mike Great");

		addChannelCapability(comChannelType, "POLLABLE", true);
		addChannelCapability(comChannelType, "CONFIGURABLE", true);
		addChannelCapability(comChannelType, "DEFAULT_REPORTING", true);

		Plugin plugin = new Plugin();
		plugin.setServiceName("eu.europa.ec.fisheries.uvms.plugins.inmarsat");
		plugin.setLabelName("twostage");
		plugin.setSatelliteType("INMARSAT_C");
		plugin.setInactive(false);

		mobileTerminalRequest.setPlugin(plugin);

		final HttpResponse response = Request.Post(BASE_URL + "mobileterminal/rest/mobileterminal")
				.setHeader("Content-Type", "application/json").setHeader("Authorization", getValidJwtToken())
				.bodyByteArray(writeValueAsString(mobileTerminalRequest).getBytes()).execute().returnResponse();

		Map<String, Object> dataMap = checkSuccessResponseReturnMap(response);

		Map<String, Object> assetMap = (Map<String, Object>) dataMap.get("mobileTerminalId");
		assertNotNull(assetMap);
		String mobileTerminalGuid = (String) assetMap.get("guid");
		assertNotNull(mobileTerminalGuid);
		
		ArrayList channelsList =   (ArrayList)dataMap.get("channels");
		assertNotNull(channelsList);
		Map<String, Object> channelMap = (Map<String, Object>) channelsList.get(0);
		assertNotNull(channelMap);
		String channelGuid = (String)channelMap.get("guid");
		comChannelType.setGuid(channelGuid);


		mobileTerminalRequest.setId((Integer) dataMap.get("id"));

		MobileTerminalId mobileTerminalId = new MobileTerminalId();
		mobileTerminalId.setGuid(mobileTerminalGuid);
		mobileTerminalRequest.setMobileTerminalId(mobileTerminalId);
		return mobileTerminalRequest;
	}
	
	
	private void addChannelCapability(ComChannelType comChannelType, String type, boolean value) {
		ComChannelCapability channelCapability = new ComChannelCapability();

		channelCapability.setType(type);
		channelCapability.setValue(value);
		comChannelType.getCapabilities().add(channelCapability);
	}

	private void addChannelAttribute(ComChannelType comChannelType, String type, String value) {
		ComChannelAttribute channelAttribute = new ComChannelAttribute();
		channelAttribute.setType(type);
		channelAttribute.setValue(value);
		comChannelType.getAttributes().add(channelAttribute);
	}

	private void addAttribute(List<MobileTerminalAttribute> attributes, String type, String value) {
		MobileTerminalAttribute serialNumberMobileTerminalAttribute = new MobileTerminalAttribute();
		serialNumberMobileTerminalAttribute.setType(type);
		serialNumberMobileTerminalAttribute.setValue(value);
		attributes.add(serialNumberMobileTerminalAttribute);
	}
	
	protected String getDate(int year4,int month ,int day, int hour,int minute ,int sec, int millis){
		
		Calendar myCalendar = Calendar.getInstance();
		myCalendar.set(Calendar.YEAR, year4);
		myCalendar.set(Calendar.MONTH, month);
		myCalendar.set(Calendar.DAY_OF_MONTH, day);
		
		myCalendar.set(Calendar.HOUR, hour);
		myCalendar.set(Calendar.MINUTE, minute);
		myCalendar.set(Calendar.SECOND, sec);
		myCalendar.set(Calendar.MILLISECOND, millis);
		Date date = myCalendar.getTime();
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");
		return sdf.format(date);
	}


}
