package eu.europa.ec.fisheries.uvms.docker.validation.asset.assetfilter.test.dto;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbConfig;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;


@Provider
public class JsonBConf implements ContextResolver<Jsonb>{

	    protected JsonbConfig config;

	    public JsonBConf() {
	        config = new JsonbConfig()
	        		.withNullValues(true);
	    }

	    @Override
	    public Jsonb getContext(Class<?> type) {
	        return JsonbBuilder.newBuilder()
	                .withConfig(config)
	                .build();
	    }
}
