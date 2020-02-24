package eu.europa.ec.fisheries.uvms.docker.validation.common;

import java.io.IOException;
import java.time.Instant;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

public class JacksonInstantDeserializer extends JsonDeserializer<Instant> {

    @Override
    public Instant deserialize(JsonParser p, DeserializationContext ctx) throws IOException {
        return Instant.ofEpochMilli(p.getLongValue());
    }
}
