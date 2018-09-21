package eu.europa.ec.fisheries.uvms.docker.validation.spatial;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationModule;
import eu.europa.ec.fisheries.uvms.spatial.model.exception.SpatialModelMarshallException;
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
    private Double latitude = 57.715523;
    private Double longitude = 11.973965;

    private String BASE_URL = "";
    private static final ObjectMapper MAPPER = new ObjectMapper()
            .registerModule(new JaxbAnnotationModule())
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
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
                return mapper;
            }
        });
        webTarget = client.target(BASE_URL);
    }


    @Test
    public void getAreaByLocation() throws Exception {

        PointType point = new PointType();
        point.setLatitude(latitude);
        point.setLongitude(longitude);
        point.setCrs(crs);

        String jsonReq = MAPPER.writeValueAsString(point);
        // @formatter:off
        Response ret =  webTarget
                .path("getAreaByLocation")
                .request(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .post(Entity.json(jsonReq), Response.class);
        // @formatter:on

        Assert.assertEquals(200, ret.getStatus());

        String json = ret.readEntity(String.class);
        List<AreaExtendedIdentifierType> list = MAPPER.readValue(json, new TypeReference<List<AreaExtendedIdentifierType>>() {
        });

        List<String> control = new ArrayList<>();
        for (AreaExtendedIdentifierType aeit : list) {
            control.add(aeit.getName());
        }
        Assert.assertTrue(control.contains("Göteborg-Lundbyhamnen"));
    }


    @Test
    public void getClosestArea() throws Exception {

        PointType point = new PointType();
        point.setLatitude(latitude);
        point.setLongitude(longitude);
        point.setCrs(crs);
        ClosestAreaSpatialRQ request = createClosestAreaRequest(point, UnitType.METERS, Arrays.asList(AreaType.EEZ));
        String jsonReq = MAPPER.writeValueAsString(request);

        // @formatter:off
        Response ret =  webTarget
                .path("getClosestArea")
                .request(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .post(Entity.json(jsonReq), Response.class);
        // @formatter:on

        Assert.assertEquals(200, ret.getStatus());

        String json = ret.readEntity(String.class);
        List<Area> list = MAPPER.readValue(json, new TypeReference<List<Area>>() {
        });

        List<String> control = new ArrayList<>();
        for (Area aeit : list) {
            control.add(aeit.getName());
        }
        Assert.assertTrue(control.contains("Göteborg-Lundbyhamnen"));
    }

    @Test
    public void getClosestLocation() throws Exception {

        PointType point = new PointType();
        point.setLatitude(latitude);
        point.setLongitude(longitude);
        point.setCrs(crs);
        ClosestLocationSpatialRQ request = createClosestLocationRequest(point, UnitType.METERS, Arrays.asList(LocationType.PORT));
        String jsonReq = MAPPER.writeValueAsString(request);


        // @formatter:off
        Response ret =  webTarget
                .path("getClosestLocation")
                .request(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .post(Entity.json(jsonReq), Response.class);
        // @formatter:on

        Assert.assertEquals(200, ret.getStatus());

        String json = ret.readEntity(String.class);
        List<Location> list = MAPPER.readValue(json, new TypeReference<List<Location>>() {
        });

        List<String> control = new ArrayList<>();
        for (Location aeit : list) {
            control.add(aeit.getName());
        }
        Assert.assertTrue(control.contains("Kalvö"));
    }


    @Test
    public void getEnrichment() throws Exception {

        PointType point = new PointType();
        point.setCrs(4326); //this magical int is the World Geodetic System 1984, aka EPSG:4326. See: https://en.wikipedia.org/wiki/World_Geodetic_System or http://spatialreference.org/ref/epsg/wgs-84/
        point.setLatitude(latitude);
        point.setLongitude(longitude);

        List<LocationType> locationTypes = Arrays.asList(LocationType.PORT);
        List<AreaType> areaTypes = Arrays.asList(AreaType.COUNTRY);
        SpatialEnrichmentRQ request = createSpatialEnrichmentRequest(point, UnitType.NAUTICAL_MILES, locationTypes, areaTypes);
        String jsonReq = MAPPER.writeValueAsString(request);

        // @formatter:off
        Response ret =  webTarget
                .path("getEnrichment")
                .request(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .post(Entity.json(jsonReq), Response.class);
        // @formatter:on

        Assert.assertEquals(200, ret.getStatus());
        String json = ret.readEntity(String.class);
        SpatialEnrichmentRS rs = MAPPER.readValue(json, new TypeReference<SpatialEnrichmentRS>() {
        });

        List<Location> list = rs.getClosestLocations().getClosestLocations();

        List<String> control = new ArrayList<>();
        for (Location aeit : list) {
            control.add(aeit.getName());
        }
        Assert.assertTrue(control.contains("Kalvö"));
    }


    @Test
    public void getFilterArea() throws Exception {

        AreaIdentifierType areaType = new AreaIdentifierType();
        areaType.setAreaType(AreaType.EEZ);
        areaType.setId("1");
        FilterAreasSpatialRQ request = createFilterAreaSpatialRequest(Arrays.asList(areaType), Arrays.asList(areaType));
        String jsonReq = MAPPER.writeValueAsString(request);


        // @formatter:off
        Response ret =  webTarget
                .path("getFilterArea")
                .request(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .post(Entity.json(jsonReq), Response.class);
        // @formatter:on

        Assert.assertEquals(200, ret.getStatus());
        String json = ret.readEntity(String.class);
        FilterAreasSpatialRS rs = MAPPER.readValue(json, new TypeReference<FilterAreasSpatialRS>() {
        });

        Assert.assertTrue(json.contains("POLYGON"));
    }


    @Test
    public void ping() throws Exception {

        PingRQ request = new PingRQ();
        String jsonReq = MAPPER.writeValueAsString(request);
        // @formatter:off
        Response ret =  webTarget
                .path("ping")
                .request(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .post(Entity.json(jsonReq), Response.class);
                String json = ret.readEntity(String.class);
        PingRS rs = MAPPER.readValue(json, new TypeReference<PingRS>() {});
            String str = rs.getResponse();
        Assert.assertEquals("pong", str);
        // @formatter:on
    }









	/*


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


    public FilterAreasSpatialRQ createFilterAreaSpatialRequest(List<AreaIdentifierType> scopeAreaList, List<AreaIdentifierType> userAreaList) throws SpatialModelMarshallException {
        FilterAreasSpatialRQ request = new FilterAreasSpatialRQ();
        ScopeAreasType scopeAreas = new ScopeAreasType();
        UserAreasType userAreas = new UserAreasType();
        scopeAreas.getScopeAreas().addAll(scopeAreaList);
        userAreas.getUserAreas().addAll(userAreaList);
        request.setMethod(SpatialModuleMethod.GET_FILTER_AREA);
        request.setScopeAreas(scopeAreas);
        request.setUserAreas(userAreas);
        return request;
    }

    private SpatialEnrichmentRQ createSpatialEnrichmentRequest(PointType point, UnitType unit, List<LocationType> locationTypes, List<AreaType> areaTypes) throws SpatialModelMarshallException {

        SpatialEnrichmentRQ request = new SpatialEnrichmentRQ();
        request.setMethod(SpatialModuleMethod.GET_ENRICHMENT);
        request.setPoint(point);
        request.setUnit(unit);
        eu.europa.ec.fisheries.uvms.spatial.model.schemas.SpatialEnrichmentRQ.LocationTypes loc = new eu.europa.ec.fisheries.uvms.spatial.model.schemas.SpatialEnrichmentRQ.LocationTypes();
        if (locationTypes != null) {
            loc.getLocationTypes().addAll(locationTypes);
        }
        request.setLocationTypes(loc);
        eu.europa.ec.fisheries.uvms.spatial.model.schemas.SpatialEnrichmentRQ.AreaTypes area = new eu.europa.ec.fisheries.uvms.spatial.model.schemas.SpatialEnrichmentRQ.AreaTypes();
        if (areaTypes != null) {
            area.getAreaTypes().addAll(areaTypes);
        }
        request.setAreaTypes(area);
        return request;
    }


    private ClosestAreaSpatialRQ createClosestAreaRequest(PointType point, UnitType unit, List<AreaType> areaTypes) throws SpatialModelMarshallException {
        ClosestAreaSpatialRQ request = new ClosestAreaSpatialRQ();
        request.setMethod(SpatialModuleMethod.GET_CLOSEST_AREA);
        request.setPoint(point);
        request.setUnit(unit);
        ClosestAreaSpatialRQ.AreaTypes area = new ClosestAreaSpatialRQ.AreaTypes();
        if (areaTypes != null) {
            area.getAreaTypes().addAll(areaTypes);
        }
        request.setAreaTypes(area);
        return request;
    }

    public ClosestLocationSpatialRQ createClosestLocationRequest(PointType point, UnitType unit, List<LocationType> locationTypes) throws SpatialModelMarshallException {
        ClosestLocationSpatialRQ request = new ClosestLocationSpatialRQ();
        request.setMethod(SpatialModuleMethod.GET_CLOSEST_LOCATION);
        request.setPoint(point);
        request.setUnit(unit);
        ClosestLocationSpatialRQ.LocationTypes loc = new ClosestLocationSpatialRQ.LocationTypes();
        if (locationTypes != null) {
            loc.getLocationTypes().addAll(locationTypes);
        }
        request.setLocationTypes(loc);
        return request;
    }


}
