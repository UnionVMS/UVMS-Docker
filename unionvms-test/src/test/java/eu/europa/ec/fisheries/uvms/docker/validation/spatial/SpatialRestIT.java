package eu.europa.ec.fisheries.uvms.docker.validation.spatial;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import eu.europa.ec.fisheries.uvms.spatial.model.mapper.SpatialModuleRequestMapper;
import eu.europa.ec.fisheries.uvms.spatial.model.schemas.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ContextResolver;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SpatialRestIT {
    private Integer crs = 4326;
    private String BASE_URL = "";
    private static final ObjectMapper MAPPER = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true)
            .registerModule(new JavaTimeModule());
    private WebTarget webTarget;


    @Before
    public void before() {
        BASE_URL = "http://localhost:28080/unionvms/";
        BASE_URL += "spatial/spatialnonsecure/spatial/";


        Client client = ClientBuilder.newClient();

        client.register(new ContextResolver<ObjectMapper>() {

            @Override
            public ObjectMapper getContext(Class<?> type) {
                ObjectMapper mapper = new ObjectMapper();
                mapper.registerModule(new JavaTimeModule());
                mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                return mapper;
            }
        });
        webTarget = client.target(BASE_URL);
    }


    @Test
    public void getAreaByLocation() throws Exception {


        PointType point = new PointType();
        point.setLatitude(57.715523);
        point.setLongitude(11.973965);
        point.setCrs(crs);

		// @formatter:off
        Response ret =  webTarget
                .path("getAreaByLocation")
                .request(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .post(Entity.json(point), Response.class);
        // @formatter:on

        Assert.assertEquals(200, ret.getStatus());

        String json  = ret.readEntity(String.class);
        List<AreaExtendedIdentifierType> list = MAPPER.readValue(json, new TypeReference<List<AreaExtendedIdentifierType>>(){});

        List<String> control = new ArrayList<>();
        for(AreaExtendedIdentifierType aeit : list){
            control.add(aeit.getName());
        }
        Assert.assertTrue(control.contains("Göteborg-Lundbyhamnen"));
    }


    @Test
    public void getEnrichment() throws Exception {

        PointType point = new PointType();
        point.setCrs(4326); //this magical int is the World Geodetic System 1984, aka EPSG:4326. See: https://en.wikipedia.org/wiki/World_Geodetic_System or http://spatialreference.org/ref/epsg/wgs-84/
        point.setLatitude(57.715523);
        point.setLongitude(11.973965);

        List<LocationType> locationTypes = Arrays.asList(LocationType.PORT);
        List<AreaType> areaTypes = Arrays.asList(AreaType.COUNTRY);
        String spatialRequest = SpatialModuleRequestMapper.mapToCreateSpatialEnrichmentRequest(point, UnitType.NAUTICAL_MILES, locationTypes, areaTypes);

//		String spatialMessageId = producer.sendModuleMessage(spatialRequest, ModuleQueue.SPATIAL);
//		TextMessage spatialResponse = consumer.getMessage(spatialMessageId, TextMessage.class);
//		LOG.debug("Got response from Spatial " + spatialResponse.getText());
//		SpatialEnrichmentRS enrichment = SpatialModuleResponseMapper.mapToSpatialEnrichmentRSFromResponse(spatialResponse, spatialMessageId);


    }





	/*

	    public MovementType enrichMovementWithSpatialData(MovementBaseType movement) throws MovementServiceException {
        try {
            LOG.debug("Enrich movement with spatial data envoked in MovementSpatialServiceBean");
            PointType point = new PointType();
            point.setCrs(4326); //this magical int is the World Geodetic System 1984, aka EPSG:4326. See: https://en.wikipedia.org/wiki/World_Geodetic_System or http://spatialreference.org/ref/epsg/wgs-84/
            point.setLatitude(movement.getPosition().getLatitude());
            point.setLongitude(movement.getPosition().getLongitude());
            List<LocationType> locationTypes = Arrays.asList(LocationType.PORT);
            List<AreaType> areaTypes = Arrays.asList(AreaType.COUNTRY);
            String spatialRequest = SpatialModuleRequestMapper.mapToCreateSpatialEnrichmentRequest(point, UnitType.NAUTICAL_MILES, locationTypes, areaTypes);
            String spatialMessageId = producer.sendModuleMessage(spatialRequest, ModuleQueue.SPATIAL);
            TextMessage spatialResponse = consumer.getMessage(spatialMessageId, TextMessage.class);
            LOG.debug("Got response from Spatial " + spatialResponse.getText());
            SpatialEnrichmentRS enrichment = SpatialModuleResponseMapper.mapToSpatialEnrichmentRSFromResponse(spatialResponse, spatialMessageId);
            return MovementMapper.enrichAndMapToMovementType(movement, enrichment);
        } catch (JMSException | SpatialModelMapperException | MovementMessageException | MessageException ex) {
            throw new MovementServiceException("FAILED TO GET DATA FROM SPATIAL ", ex);
        }
    }

    @Override
    public List<MovementType> enrichMovementBatchWithSpatialData(List<MovementBaseType> movements) throws MovementServiceException {
        List<SpatialEnrichmentRQListElement> batchReqLements = new ArrayList<>();
        for (MovementBaseType movement : movements) {
            PointType point = new PointType();
            point.setCrs(4326);
            point.setLatitude(movement.getPosition().getLatitude());
            point.setLongitude(movement.getPosition().getLongitude());
            List<LocationType> locationTypes = Arrays.asList(LocationType.PORT);
            List<AreaType> areaTypes = Arrays.asList(AreaType.COUNTRY);
            SpatialEnrichmentRQListElement spatialEnrichmentRQListElement = SpatialModuleRequestMapper.mapToCreateSpatialEnrichmentRQElement(point, UnitType.NAUTICAL_MILES, locationTypes, areaTypes);
            batchReqLements.add(spatialEnrichmentRQListElement);
        }
        try {
            LOG.debug("Enrich movement Batch with spatial data envoked in MovementSpatialServiceBean");
            String spatialRequest = SpatialModuleRequestMapper.mapToCreateBatchSpatialEnrichmentRequest(batchReqLements);
            String spatialMessageId = producer.sendModuleMessage(spatialRequest, ModuleQueue.SPATIAL);
            TextMessage spatialJmsMessageRS = consumer.getMessage(spatialMessageId, TextMessage.class);
            LOG.debug("Got response from Spatial " + spatialJmsMessageRS.getText());
            BatchSpatialEnrichmentRS enrichment = SpatialModuleResponseMapper.mapToBatchSpatialEnrichmentRSFromResponse(spatialJmsMessageRS, spatialMessageId);
            return MovementMapper.enrichAndMapToMovementTypes(movements, enrichment);
        } catch (JMSException | SpatialModelMapperException | MovementMessageException | MessageException ex) {
            throw new MovementServiceException("FAILED TO GET DATA FROM SPATIAL ", ex);
        }
    }





	 */


    @Test
    public void ping() throws Exception {


        // @formatter:off
        String ret =  webTarget
                .path("ping")
                .request(MediaType.APPLICATION_JSON)
                .get(String.class);
        Assert.assertEquals("pong", ret);
        // @formatter:on



    }


    /*
    // @formatter:off
		final HttpResponse response = Request.Post(getBaseUrl() + "spatial/rest/config/" + id)
				.setHeader("Content-Type", "application/json")
				.setHeader("Authorization", token)
				.setHeader(AuthConstants.HTTP_HEADER_SCOPE_NAME, scopeName)
				.setHeader(AuthConstants.HTTP_HEADER_ROLE_NAME, roleName)
				.bodyByteArray(theDto.getBytes())
				.execute()
				.returnResponse();
		// @formatter:on

     */


    }
