package eu.europa.ec.fisheries.uvms.docker.validation.common;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import eu.europa.ec.fisheries.uvms.commons.date.DateUtils;

import java.io.IOException;
import java.time.Instant;

public class JacksonInstantDeserializer extends JsonDeserializer<Instant> {

    @Override
    public Instant deserialize(JsonParser p, DeserializationContext ctx) throws IOException {
        String str = p.getText();
        return DateUtils.stringToDate(str);
    }
}
