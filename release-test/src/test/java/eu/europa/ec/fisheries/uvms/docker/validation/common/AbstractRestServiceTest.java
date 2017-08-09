/*

Developed with the contribution of the European Commission - Directorate General for Maritime Affairs and Fisheries
� European Union, 2017.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can
redistribute it and/or modify it under the terms of the GNU General Public License as published by the
Free Software Foundation, either version 3 of the License, or any later version. The IFDM Suite is distributed in
the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. You should have received a
copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

*/
package eu.europa.ec.fisheries.uvms.docker.validation.common;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.fluent.Request;
import org.junit.Assert;
import org.junit.BeforeClass;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.MapType;

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
import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.MobileTerminalId;
import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.MobileTerminalSource;
import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.MobileTerminalType;
import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.Plugin;
import eu.europa.ec.fisheries.uvms.docker.validation.asset.AssetTestHelper;
import eu.europa.ec.fisheries.wsdl.asset.types.Asset;
import eu.europa.ec.fisheries.wsdl.asset.types.AssetHistoryId;
import eu.europa.ec.fisheries.wsdl.asset.types.AssetId;
import eu.europa.ec.fisheries.wsdl.asset.types.AssetIdType;
import eu.europa.ec.fisheries.wsdl.asset.types.EventCode;

/**
 * The Class AbstractRestServiceTest.
 */
public abstract class AbstractRestServiceTest extends Assert {

	/** The Constant BASE_URL. */
	protected static final String BASE_URL = "http://localhost:28080/";

	/** The valid jwt token. */
	private static String validJwtToken;

	/** The Constant OBJECT_MAPPER. */
	private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

	/**
	 * Aquire jwt token for test.
	 *
	 * @throws Exception
	 *             the exception
	 */
	@BeforeClass
	public static void aquireJwtTokenForTest() throws Exception {
		final HttpResponse response = Request.Post(BASE_URL + "usm-administration/rest/authenticate")
				.setHeader("Content-Type", "application/json")
				.bodyByteArray("{\"userName\":\"vms_admin_com\",\"password\":\"password\"}".getBytes()).execute()
				.returnResponse();

		assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
		final Map<String, Object> data = getJsonMap(response);
		assertEquals(true, data.get("authenticated"));
		validJwtToken = (String) data.get("jwtoken");
	}

	protected final Map<String, Object> checkSuccessResponseReturnMap(final HttpResponse response)
			throws IOException, JsonParseException, JsonMappingException, ClientProtocolException {
		final Map<String, Object> data = checkSuccessResponseReturnDataMap(response);

		Map<String, Object> dataMap = (Map<String, Object>) data.get("data");
		assertNotNull(dataMap);
		return dataMap;
	}

	private static Map<String, Object> checkSuccessResponseReturnDataMap(final HttpResponse response)
			throws IOException, JsonParseException, JsonMappingException, ClientProtocolException {
		assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
		final Map<String, Object> data = getJsonMap(response);
		assertFalse(data.isEmpty());
		assertNotNull(data.get("data"));
		assertEquals("200", "" + data.get("code"));
		return data;
	}

	protected final Integer checkSuccessResponseReturnInt(final HttpResponse response)
			throws IOException, JsonParseException, JsonMappingException, ClientProtocolException {
		final Map<String, Object> data = checkSuccessResponseReturnDataMap(response);

		Integer dataValue = (Integer) data.get("data");
		assertNotNull(dataValue);
		return dataValue;
	}

	protected final <T> T checkSuccessResponseReturnType(final HttpResponse response, Class<T> classCast)
			throws IOException, JsonParseException, JsonMappingException, ClientProtocolException {
		final Map<String, Object> data = checkSuccessResponseReturnDataMap(response);
		T dataValue = (T) data.get("data");
		assertNotNull(dataValue);
		return dataValue;
	}

	/**
	 * Gets the valid jwt token.
	 *
	 * @return the valid jwt token
	 */
	protected final String getValidJwtToken() {
		return validJwtToken;
	}

	public final String aquireJwtToken(final String username, final String password) throws Exception {
		final HttpResponse response = Request.Post(BASE_URL + "usm-administration/rest/authenticate")
				.setHeader("Content-Type", "application/json")
				.bodyByteArray(("{\"userName\":\"" + username + "\",\"password\":\"" + password + "\"}").getBytes())
				.execute().returnResponse();

		assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
		final Map<String, Object> data = getJsonMap(response);
		assertEquals(true, data.get("authenticated"));
		return (String) data.get("jwtoken");
	}

	/**
	 * Write value as string.
	 *
	 * @param value
	 *            the value
	 * @return the string
	 * @throws JsonProcessingException
	 *             the json processing exception
	 */
	protected final String writeValueAsString(final Object value) throws JsonProcessingException {
		return OBJECT_MAPPER.writeValueAsString(value);
	}

	protected Asset createTestAsset() throws IOException, ClientProtocolException, JsonProcessingException,
			JsonParseException, JsonMappingException {

		Asset asset = AssetTestHelper.helper_createAsset(AssetIdType.GUID);
		final HttpResponse response = Request.Post(BASE_URL + "asset/rest/asset")
				.setHeader("Content-Type", "application/json").setHeader("Authorization", getValidJwtToken())
				.bodyByteArray(writeValueAsString(asset).getBytes()).execute().returnResponse();
		Map<String, Object> dataMap = checkSuccessResponseReturnMap(response);

		Map<String, Object> assetMap = (Map<String, Object>) dataMap.get("assetId");
		assertNotNull(assetMap);
		String assetGuid = (String) assetMap.get("value");
		assertNotNull(assetGuid);

		Map<String, Object> eventHistoryMap = (Map<String, Object>) dataMap.get("eventHistory");
		assertNotNull(eventHistoryMap);
		String eventId = (String) eventHistoryMap.get("eventId");
		assertNotNull(eventId);
		String eventCode = (String) eventHistoryMap.get("eventCode");
		assertNotNull(eventCode);

		AssetHistoryId assetHistoryId = new AssetHistoryId();
		assetHistoryId.setEventId(eventId);
		assetHistoryId.setEventCode(EventCode.fromValue(eventCode));
		asset.setEventHistory(assetHistoryId);

		asset.setName(asset.getName() + "Changed");
		AssetId assetId = new AssetId();
		assetId.setGuid(assetGuid);
		assetId.setValue(assetGuid);
		assetId.setType(AssetIdType.GUID);
		asset.setAssetId(assetId);
		return asset;
	}

	/**
	 * Gets the json map.
	 *
	 * @param response
	 *            the response
	 * @return the json map
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws JsonParseException
	 *             the json parse exception
	 * @throws JsonMappingException
	 *             the json mapping exception
	 * @throws ClientProtocolException
	 *             the client protocol exception
	 */
	protected final static Map<String, Object> getJsonMap(final HttpResponse response)
			throws IOException, JsonParseException, JsonMappingException, ClientProtocolException {
		final ObjectMapper mapper = new ObjectMapper();
		final MapType type = mapper.getTypeFactory().constructMapType(Map.class, String.class, Object.class);
		final Map<String, Object> data = mapper.readValue(response.getEntity().getContent(), type);
		return data;
	}

	protected String getDateAsString(int year4, int month, int day, int hour, int minute, int sec, int millis) {

		Date date = getDate(year4, month, day, hour, minute, sec, millis);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");
		return sdf.format(date);
	}

	protected Date getDate(int year4, int month, int day, int hour, int minute, int sec, int millis) {

		Calendar myCalendar = Calendar.getInstance();
		myCalendar.set(Calendar.YEAR, year4);
		myCalendar.set(Calendar.MONTH, month);
		myCalendar.set(Calendar.DAY_OF_MONTH, day);

		myCalendar.set(Calendar.HOUR, hour);
		myCalendar.set(Calendar.MINUTE, minute);
		myCalendar.set(Calendar.SECOND, sec);
		myCalendar.set(Calendar.MILLISECOND, millis);
		Date date = myCalendar.getTime();
		return date;

	}

	protected Map<String, Object> createPoll_Helper() throws Exception {
		Asset testAsset = createTestAsset();
		MobileTerminalType createdMobileTerminalType = createMobileTerminalType();
	
		{
			MobileTerminalAssignQuery mobileTerminalAssignQuery = new MobileTerminalAssignQuery();
			mobileTerminalAssignQuery.setMobileTerminalId(createdMobileTerminalType.getMobileTerminalId());
			mobileTerminalAssignQuery.setConnectId(testAsset.getAssetId().getGuid());
			// Assign first
			final HttpResponse response = Request
					.Post(BASE_URL + "mobileterminal/rest/mobileterminal/assign?comment=comment")
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
		pollMobileTerminal.setConnectId(testAsset.getAssetId().getGuid());
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
		endDate.setValue(getDateAsString(2017, Calendar.DECEMBER, 24, 11, 45, 7, 980));
	
		pollRequestType.getMobileTerminals().add(pollMobileTerminal);
	
		final HttpResponse response = Request.Post(BASE_URL + "mobileterminal/rest/poll")
				.setHeader("Content-Type", "application/json").setHeader("Authorization", getValidJwtToken())
				.bodyByteArray(writeValueAsString(pollRequestType).getBytes()).execute().returnResponse();
	
		Map<String, Object> dataMap = checkSuccessResponseReturnMap(response);
	
		return dataMap;
	}

	public MobileTerminalType createMobileTerminalType() throws IOException, ClientProtocolException, JsonProcessingException,
			JsonParseException, JsonMappingException {
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

}
