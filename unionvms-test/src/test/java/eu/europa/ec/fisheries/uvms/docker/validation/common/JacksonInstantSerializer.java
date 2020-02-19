package eu.europa.ec.fisheries.uvms.docker.validation.common;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import eu.europa.ec.fisheries.uvms.commons.date.DateUtils;

import java.io.IOException;
import java.time.Instant;

public class JacksonInstantSerializer extends StdSerializer<Instant> {

    public JacksonInstantSerializer() {
        this(null);
    }

    public JacksonInstantSerializer(Class<Instant> t) {
        super(t);
    }

    @Override
    public void serialize(
            Instant value, JsonGenerator jgen, SerializerProvider provider)
            throws IOException {

        jgen.writeString(DateUtils.dateToEpochMilliseconds(value));
    }
}
