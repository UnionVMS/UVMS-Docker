package eu.europa.ec.fisheries.uvms.docker.validation.common;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.MapType;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import eu.europa.ec.fisheries.uvms.commons.rest.dto.ResponseDto;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.util.EntityUtils;
import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.ContextResolver;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

public abstract class AbstractRest extends Assert {

    private static final Logger log = LoggerFactory.getLogger(AbstractRest.class.getSimpleName());

    protected static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .registerModule(new JavaTimeModule());

    private static final String BASE_URL = "http://localhost:28080/unionvms/";
    //protected static final String BASE_URL = "http://livm73u.havochvatten.se:28080/unionvms/";

    private static String validJwtToken;

    protected static String getBaseUrl() {
        return BASE_URL;
    }

    protected static WebTarget getWebTarget() {
        Client client = ClientBuilder.newClient();
        client.register(new ContextResolver<ObjectMapper>() {
            @Override
            public ObjectMapper getContext(Class<?> type) {
                return OBJECT_MAPPER;
            }
        });
        return client.target(getBaseUrl());
    }

    private static String aquireJwtTokenForTest() {
        return acquireJwtToken("vms_admin_se", "password");
    }

    protected static String getValidJwtToken() {
        if (validJwtToken == null) {
            try {
                validJwtToken = aquireJwtTokenForTest();
            } catch (Exception e) {
                Assert.fail("Not possible to get jwt token");
            }
        }
        return validJwtToken;
    }

    protected static String getValidJwtToken(String uid, String pwd) {
        try {
            return acquireJwtToken(uid, pwd);
        } catch (Exception e) {
            Assert.fail("Not possible to get jwt token");
            return null;
        }
    }

    private static String acquireJwtToken(final String username, final String password) {
        AuthenticationResponse response = getWebTarget()
                .path("usm-administration/rest/authenticate")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(new AuthenticationRequest(username, password)), AuthenticationResponse.class);

        assertTrue(response.isAuthenticated());
        return response.getJwtoken();
    }

    protected static Map<String, Object> checkSuccessResponseReturnMap(final HttpResponse response) throws IOException {
        final Map<String, Object> data = checkSuccessResponseReturnDataMap(response);

        Map<String, Object> dataMap = (Map<String, Object>) data.get("data");
        assertNotNull(dataMap);
        return dataMap;
    }

    private static Map<String, Object> checkSuccessResponseReturnDataMap(final HttpResponse response)
            throws IOException {
        assertEquals(EntityUtils.toString(response.getEntity()), HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
        final Map<String, Object> data = getJsonMap(response);
        assertFalse(data.isEmpty());
        assertNotNull(data.get("data"));
        assertEquals("200", "" + data.get("code"));
        return data;
    }

    protected static Integer checkSuccessResponseReturnInt(final HttpResponse response)
            throws IOException {
        final Map<String, Object> data = checkSuccessResponseReturnDataMap(response);

        Integer dataValue = (Integer) data.get("data");
        assertNotNull(dataValue);
        return dataValue;
    }

    protected static <T> T checkSuccessResponseReturnType(final HttpResponse response, Class<T> classCast)
            throws IOException {
        final Map<String, Object> data = checkSuccessResponseReturnDataMap(response);
        T dataValue = (T) data.get("data");
        assertNotNull(dataValue);
        return dataValue;
    }

    protected static <T> T checkSuccessResponseReturnObject(final HttpResponse response, Class<T> classCast) throws IOException {
        final Map<String, Object> data = checkSuccessResponseReturnDataMap(response);
        Map<String, Object> dataMap = (Map<String, Object>) data.get("data");
        T dataValue = OBJECT_MAPPER.readValue(writeValueAsString(dataMap), classCast);
        assertNotNull(dataValue);
        return dataValue;
    }

    protected static <T> T checkSuccessResponseAndReturnType(final HttpResponse response, Class<T> classCast) throws IOException {
        assertEquals("Response Body: " + EntityUtils.toString(response.getEntity()),
                HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
        HttpEntity entity = response.getEntity();
        T dataValue = OBJECT_MAPPER.readValue(EntityUtils.toString(entity), classCast);
        assertNotNull(dataValue);
        return dataValue;
    }

    protected static <T> List<T> checkSuccessResponseReturnList(final HttpResponse response, Class<T> classCast) throws IOException {
        final Map<String, Object> data = checkSuccessResponseReturnDataMap(response);
        String valueAsString = writeValueAsString(data.get("data"));

        List<T> dataValue = OBJECT_MAPPER.readValue(valueAsString, OBJECT_MAPPER.getTypeFactory().constructCollectionType(List.class, classCast));
        assertNotNull(dataValue);
        return dataValue;
    }

    protected static <T> List<T> checkSuccessResponseAndReturnList(final HttpResponse response, Class<T> classCast)
            throws IOException {
        assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
        HttpEntity entity = response.getEntity();
        List<T> returnList = OBJECT_MAPPER.readValue(EntityUtils.toString(entity),
                OBJECT_MAPPER.getTypeFactory().constructCollectionType(List.class, classCast));
        assertNotNull(returnList);
        return returnList;
    }

    protected static void checkErrorResponse(final HttpResponse response) throws IOException {
        assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
        final Map<String, Object> data = getJsonMap(response);
        assertFalse(data.isEmpty());
        assertEquals("500", "" + data.get("code"));
    }

    protected static void checkErrorResponse(ResponseDto<?> response) {
        assertEquals("500", String.valueOf(response.getCode()));
    }

    protected static String returnErrorResponse(final HttpResponse response) throws IOException {
        assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
        final Map<String, Object> data = getJsonMap(response);
        assertFalse(data.isEmpty());
        return "" + data.get("code");
    }

    protected static String writeValueAsString(final Object value) throws JsonProcessingException {
        String ret = "";
        try {
            ret = OBJECT_MAPPER.writeValueAsString(value);
        } catch (RuntimeException e) {
            log.error("Serializing error: " + e);
        }
        return ret;
    }

    protected static String getDateAsString(int year4, int month, int day, int hour, int minute, int sec, int millis) {
        Date date = getDate(year4, month, day, hour, minute, sec, millis);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");
        return sdf.format(date);
    }

    private static Date getDate(int year4, int month, int day, int hour, int minute, int sec, int millis) {
        Calendar myCalendar = Calendar.getInstance();
        myCalendar.set(Calendar.YEAR, year4);
        myCalendar.set(Calendar.MONTH, month);
        myCalendar.set(Calendar.DAY_OF_MONTH, day);

        myCalendar.set(Calendar.HOUR, hour);
        myCalendar.set(Calendar.MINUTE, minute);
        myCalendar.set(Calendar.SECOND, sec);
        myCalendar.set(Calendar.MILLISECOND, millis);
        return myCalendar.getTime();
    }

    protected static Map<String, Object> getJsonMap(final HttpResponse response)
            throws IOException {
        final MapType type = OBJECT_MAPPER.getTypeFactory().constructMapType(Map.class, String.class, Object.class);
        final Map<String, Object> data = OBJECT_MAPPER.readValue(response.getEntity().getContent(), type);
        return data;
    }

    protected static String generateARandomStringWithMaxLength(int len) {
        StringBuilder ret = new StringBuilder();
        for (int i = 0; i < len; i++) {
            int val = new Random().nextInt(10);
            ret.append(String.valueOf(val));
        }
        return ret.toString();
    }
}
