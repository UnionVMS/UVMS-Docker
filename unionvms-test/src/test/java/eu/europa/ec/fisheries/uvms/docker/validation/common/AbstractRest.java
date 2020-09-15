package eu.europa.ec.fisheries.uvms.docker.validation.common;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import eu.europa.ec.fisheries.uvms.docker.validation.AppError;
import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.ContextResolver;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public abstract class AbstractRest extends Assert {

    private static final Logger log = LoggerFactory.getLogger(AbstractRest.class.getSimpleName());

    protected static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .configure(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS, false)
            .configure(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS, false)
            .registerModule(new JavaTimeModule());

    private static final String BASE_URL = "http://localhost:28080/unionvms/";
    //protected static final String BASE_URL = "http://liaswf05t.havochvatten.se:28080/unionvms/";
    //protected static final String BASE_URL = "http://liaswf05p.havochvatten.se:28080/unionvms/";

    private static String validJwtToken;

    protected static String getBaseUrl() {
        return BASE_URL;
    }

    protected static WebTarget getWebTarget() {
        Client client = ClientBuilder.newBuilder().readTimeout(60, TimeUnit.SECONDS).newClient();
        /*JsonbConfig config = new JsonbConfig()
                .withAdapters(new JsonBInstantAdapter())
                .withPropertyNamingStrategy(PropertyNamingStrategy.UPPER_CAMEL_CASE)
                .setProperty(JsonbConfig.DATE_FORMAT, JsonbDateFormat.TIME_IN_MILLIS);*/
        client.register(new ContextResolver<ObjectMapper>() {
            @Override
            public ObjectMapper getContext(Class<?> type) {
                return OBJECT_MAPPER;
            }
        });
        //client.register(JsonBConfigurator.class);
        return client.target(getBaseUrl());
    }

    protected static String getValidJwtToken() {
        if (validJwtToken == null) {
            try {
                validJwtToken = acquireJwtToken("vms_admin_se", "password");
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

        assertTrue(response.getErrorDescription(), response.isAuthenticated());
        return response.getJWToken();
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

    public static void checkForAppErrorMessage(String json){
        if(json.contains("code:")){
            try {
                throw new RuntimeException(OBJECT_MAPPER.readValue(json, AppError.class).description);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }
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
