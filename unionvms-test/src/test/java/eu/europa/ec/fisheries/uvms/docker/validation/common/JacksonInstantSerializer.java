package eu.europa.ec.fisheries.uvms.docker.validation.common;

import java.io.IOException;
import java.time.Instant;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

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

        jgen.writeNumber(value.toEpochMilli());
    }
}
