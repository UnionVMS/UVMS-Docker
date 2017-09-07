package eu.europa.ec.fisheries.uvms.docker.validation.common;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.fluent.Request;
import org.junit.Assert;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.MapType;

public abstract class AbstractRest extends Assert {

	private static final String DOCKER_RELEASE_TEST_BASE_URL_PROPERTY = "docker.release.test.base.url";

	/** The Constant OBJECT_MAPPER. */
	private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

	/** The Constant BASE_URL. */
	protected static final String BASE_URL = "http://localhost:28080/";

	/** The valid jwt token. */
	private static String validJwtToken;

	protected static final String getBaseUrl() {
		String property = System.getProperty(DOCKER_RELEASE_TEST_BASE_URL_PROPERTY);
		if (property != null) {
			return property;
		} else {
			return BASE_URL;
		}		
	}	
	

	/**
	 * Aquire jwt token for test.
	 *
	 * @throws Exception
	 *             the exception
	 */
	public static String aquireJwtTokenForTest() throws Exception {
		return aquireJwtToken("vms_admin_com","password");
	}

	/**
	 * Gets the valid jwt token.
	 *
	 * @return the valid jwt token
	 */
	protected static final String getValidJwtToken() {
		if (validJwtToken == null) {
			try {
				validJwtToken = aquireJwtTokenForTest();
			} catch (Exception e) {
				Assert.fail("Not possible to get jwt token");
			}
		}
		
		return validJwtToken;
	}
	
	protected static final String getValidJwtToken(String uid, String pwd) {
		try {
			return aquireJwtToken(uid, pwd);
		} catch (Exception e) {
			Assert.fail("Not possible to get jwt token");
			return null;
		}
	}

	public static final String aquireJwtToken(final String username, final String password) throws Exception {
		final HttpResponse response = Request.Post(getBaseUrl() + "usm-administration/rest/authenticate")
				.setHeader("Content-Type", "application/json")
				.bodyByteArray(("{\"userName\":\"" + username + "\",\"password\":\"" + password + "\"}").getBytes())
				.execute().returnResponse();

		assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
		final Map<String, Object> data = getJsonMap(response);
		assertEquals(true, data.get("authenticated"));
		return (String) data.get("jwtoken");
	}


	protected static final Map<String, Object> checkSuccessResponseReturnMap(final HttpResponse response)
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
	 * Write value as string.
	 *
	 * @param value
	 *            the value
	 * @return the string
	 * @throws JsonProcessingException
	 *             the json processing exception
	 */
	protected final static String writeValueAsString(final Object value) throws JsonProcessingException {
		return OBJECT_MAPPER.writeValueAsString(value);
	}

	protected static final String getDateAsString(int year4, int month, int day, int hour, int minute, int sec,
			int millis) {
			
				Date date = getDate(year4, month, day, hour, minute, sec, millis);
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");
				return sdf.format(date);
			}

	protected static final Date getDate(int year4, int month, int day, int hour, int minute, int sec,
			int millis) {
			
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
	protected static final Map<String, Object> getJsonMap(final HttpResponse response)
			throws IOException, JsonParseException, JsonMappingException, ClientProtocolException {
				final ObjectMapper mapper = new ObjectMapper();
				final MapType type = mapper.getTypeFactory().constructMapType(Map.class, String.class, Object.class);
				final Map<String, Object> data = mapper.readValue(response.getEntity().getContent(), type);
				return data;
			}

}
