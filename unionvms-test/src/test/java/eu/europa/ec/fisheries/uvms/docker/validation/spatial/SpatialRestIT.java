package eu.europa.ec.fisheries.uvms.docker.validation.spatial;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationModule;
import eu.europa.ec.fisheries.uvms.spatial.model.exception.SpatialModelMarshallException;
import eu.europa.ec.fisheries.uvms.spatial.model.schemas.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
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
    private WebTarget webTarget;

    @Before
    public void before() {
        BASE_URL = "http://localhost:28080/unionvms/";
        BASE_URL += "spatial/spatialnonsecure/json/";

        Client client = ClientBuilder.newClient();
        client.register(new ContextResolver<ObjectMapper>() {
            @Override
            public ObjectMapper getContext(Class<?> type) {
                ObjectMapper mapper = new ObjectMapper();
                mapper.registerModule(new JavaTimeModule());
                mapper.registerModule(new JaxbAnnotationModule());
                mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                return mapper;
            }
        });
        webTarget = client.target(BASE_URL);
    }



    @Test
    @Ignore
    public void getAreaByLocation() throws Exception {

        PointType point = new PointType();
        point.setLatitude(latitude);
        point.setLongitude(longitude);
        point.setCrs(crs);

        AreaByLocationSpatialRQ areaByLocationSpatialRQ = new AreaByLocationSpatialRQ();
        areaByLocationSpatialRQ.setPoint(point);
        areaByLocationSpatialRQ.setMethod(SpatialModuleMethod.GET_AREA_BY_LOCATION);
        // @formatter:off
        Response ret =  webTarget
                .path("getAreaByLocation")
                .request(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .post(Entity.json(areaByLocationSpatialRQ), Response.class);
        // @formatter:on

        Assert.assertEquals(200, ret.getStatus());
        List<AreaExtendedIdentifierType> list = ret.readEntity(new GenericType<List<AreaExtendedIdentifierType>>() {});
        List<String> control = new ArrayList<>();
        for (AreaExtendedIdentifierType aeit : list) {
            control.add(aeit.getName());
        }
        Assert.assertTrue(control.contains("Göteborg-Lundbyhamnen"));
    }

    @Test
    @Ignore
    public void getAreaTypes() throws Exception {

        PointType point = new PointType();
        point.setLatitude(latitude);
        point.setLongitude(longitude);
        point.setCrs(crs);
        AllAreaTypesRequest request = createAllAreaTypesRequest();

        // @formatter:off
        Response ret =  webTarget
                .path("getAreaTypes")
                .request(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .post(Entity.json(request), Response.class);
        // @formatter:on

        Assert.assertEquals(200, ret.getStatus());

        List<String> list = ret.readEntity(new GenericType<List<String>>() {});

        List<String> control = new ArrayList<>();
        for (String str : list) {
            control.add(str);
        }
        Assert.assertTrue(control.contains("EEZ"));
    }


    @Test
    @Ignore
    public void getClosestArea() throws Exception {

        PointType point = new PointType();
        point.setLatitude(latitude);
        point.setLongitude(longitude);
        point.setCrs(crs);
        ClosestAreaSpatialRQ request = createClosestAreaRequest(point, UnitType.METERS, Arrays.asList(AreaType.EEZ));

        // @formatter:off
        Response ret =  webTarget
                .path("getClosestArea")
                .request(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .post(Entity.json(request), Response.class);
        // @formatter:on

        Assert.assertEquals(200, ret.getStatus());
        List<Area> list = ret.readEntity(new GenericType<List<Area>>() {});

        List<String> control = new ArrayList<>();
        for (Area aeit : list) {
            control.add(aeit.getName());
        }
        Assert.assertTrue(control.contains("Göteborg-Lundbyhamnen"));
    }

    @Test
    @Ignore
    public void getClosestLocation() throws Exception {

        PointType point = new PointType();
        point.setLatitude(latitude);
        point.setLongitude(longitude);
        point.setCrs(crs);
        ClosestLocationSpatialRQ request = createClosestLocationRequest(point, UnitType.METERS, Arrays.asList(LocationType.PORT));


        // @formatter:off
        Response ret =  webTarget
                .path("getClosestLocation")
                .request(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .post(Entity.json(request), Response.class);
        // @formatter:on

        Assert.assertEquals(200, ret.getStatus());

        List<Location> list = ret.readEntity(new GenericType<List<Location>>() {});


        List<String> control = new ArrayList<>();
        for (Location aeit : list) {
            control.add(aeit.getName());
        }
        Assert.assertTrue(control.contains("Kalvö"));
    }


    @Test
    @Ignore
    public void getEnrichment() throws Exception {

        PointType point = new PointType();
        point.setCrs(4326); //this magical int is the World Geodetic System 1984, aka EPSG:4326. See: https://en.wikipedia.org/wiki/World_Geodetic_System or http://spatialreference.org/ref/epsg/wgs-84/
        point.setLatitude(latitude);
        point.setLongitude(longitude);

        List<LocationType> locationTypes = Arrays.asList(LocationType.PORT);
        List<AreaType> areaTypes = Arrays.asList(AreaType.COUNTRY);
        SpatialEnrichmentRQ request = createSpatialEnrichmentRequest(point, UnitType.NAUTICAL_MILES, locationTypes, areaTypes);

        // @formatter:off
        Response ret =  webTarget
                .path("getEnrichment")
                .request(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .post(Entity.json(request), Response.class);
        // @formatter:on

        Assert.assertEquals(200, ret.getStatus());
        SpatialEnrichmentRS rs = ret.readEntity(new GenericType<SpatialEnrichmentRS>() {});

        List<Location> list = rs.getClosestLocations().getClosestLocations();

        List<String> control = new ArrayList<>();
        for (Location aeit : list) {
            control.add(aeit.getName());
        }
        Assert.assertTrue(control.contains("Kalvö"));
    }

    @Test
    @Ignore
    public void getFilterArea() throws Exception {

        AreaIdentifierType areaType = new AreaIdentifierType();
        areaType.setAreaType(AreaType.EEZ);
        areaType.setId("1");
        FilterAreasSpatialRQ request = createFilterAreaSpatialRequest(Arrays.asList(areaType), Arrays.asList(areaType));


        // @formatter:off
        Response ret =  webTarget
                .path("getFilterArea")
                .request(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .post(Entity.json(request), Response.class);
        // @formatter:on

        Assert.assertEquals(200, ret.getStatus());
        FilterAreasSpatialRS rs = ret.readEntity(new GenericType<FilterAreasSpatialRS>() {});
        String geometry = rs.getGeometry();
        System.out.println();

        Assert.assertTrue(geometry.contains("POLYGON"));
    }

    @Test
    @Ignore
    public void getMapConfiguration() throws Exception {

        SpatialGetMapConfigurationRQ request = getSpatialGetMapConfigurationRQ();
        request.setReportId(1);

        // @formatter:off
        Response ret =  webTarget
                .path("getMapConfiguration")
                .request(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .post(Entity.json(request), Response.class);
        // @formatter:on

        Assert.assertEquals(200, ret.getStatus());
        FilterAreasSpatialRS rs = ret.readEntity(new GenericType<FilterAreasSpatialRS>() {});
        // until we figure out something better
        Assert.assertTrue(rs != null);
    }


    private SpatialDeleteMapConfigurationRQ creatSpatialDeleteMapConfigurationRQ() {
        SpatialDeleteMapConfigurationRQ request = new SpatialDeleteMapConfigurationRQ();
        return request;
    }


    @Test
    @Ignore
    public void ping() throws Exception {

        PingRQ request = new PingRQ();
        // @formatter:off
        Response ret =  webTarget
                .path("ping")
                .request(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .post(Entity.json(request), Response.class);

        PingRS rs = ret.readEntity(new GenericType<PingRS>() {});

        String str = rs.getResponse();
        Assert.assertEquals("pong", str);
        // @formatter:on
    }

    @Test
    @Ignore
    public void getAreaByCode() throws Exception {

        AreaByCodeRequest request = createAreaByCodeRequest();
        // @formatter:off
        Response ret =  webTarget
                .path("getAreaByCode")
                .request(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .post(Entity.json(request), Response.class);
        // @formatter:on

        Assert.assertEquals(200, ret.getStatus());

        AreaByCodeResponse response = ret.readEntity(new GenericType<AreaByCodeResponse>() {});

        List<AreaSimpleType> resultList = response.getAreaSimples();
        Assert.assertNotNull(resultList);
        Assert.assertTrue(resultList.size() > 0);

        AreaSimpleType line = resultList.get(0);
        String wkt = line.getWkt();
        Assert.assertNotNull(wkt);
        Assert.assertTrue(wkt.contains("POLYGON"));
    }


    @Test
    @Ignore
    public void getGeometryByPortCode() throws Exception {

        GeometryByPortCodeRequest request = createToGeometryByPortCodeRequest("AOLAD");
        // @formatter:off
        Response ret =  webTarget
                .path("getGeometryByPortCode")
                .request(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .post(Entity.json(request), Response.class);
        GeometryByPortCodeResponse rs = ret.readEntity(new GenericType<GeometryByPortCodeResponse>() {});
        Assert.assertEquals(200, ret.getStatus());
        String geometry = rs.getPortGeometry();
        Assert.assertTrue(geometry != null);
        Assert.assertTrue(geometry.contains("MULTIPOINT"));
        // @formatter:on
    }

    @Test
    @Ignore
    public void getEnrichmentBatch() throws Exception {

    }



    /*******************************************************************************************************************
     * HELPERS                                                                                                         *
     *******************************************************************************************************************/

    private AreaByCodeRequest createAreaByCodeRequest() {

        AreaByCodeRequest request = new AreaByCodeRequest();
        List<AreaSimpleType> list = new ArrayList<>();
        AreaSimpleType ast = new AreaSimpleType();

        ast.setAreaCode("DOM");
        ast.setAreaType("eez");
        //ast.setWkt("");

        list.add(ast);
        request.setAreaSimples(list);

        return request;
    }


    private SpatialSaveOrUpdateMapConfigurationRQ createSpatialSaveOrUpdateMapConfigurationRQ() {
        SpatialSaveOrUpdateMapConfigurationRQ request = new SpatialSaveOrUpdateMapConfigurationRQ();
        MapConfigurationType config = new MapConfigurationType();

        config.setCoordinatesFormat(CoordinatesFormat.DD);
        config.setDisplayProjectionId(1L);
        config.setReportId(1L);
        config.setMapProjectionId(42L);


        request.setMapConfiguration(config);
        return request;
    }


    private SpatialGetMapConfigurationRQ getSpatialGetMapConfigurationRQ() {
        SpatialGetMapConfigurationRQ request = new SpatialGetMapConfigurationRQ();
        return request;
    }


    private AllAreaTypesRequest createAllAreaTypesRequest() {
        AllAreaTypesRequest allAreaTypesRequest = new AllAreaTypesRequest();
        return allAreaTypesRequest;
    }


    private GeometryByPortCodeRequest createToGeometryByPortCodeRequest(String portCode) throws SpatialModelMarshallException {
        GeometryByPortCodeRequest request = new GeometryByPortCodeRequest();
        request.setPortCode(portCode);
        request.setMethod(SpatialModuleMethod.GET_GEOMETRY_BY_PORT_CODE);
        return request;
    }

    private FilterAreasSpatialRQ createFilterAreaSpatialRequest(List<AreaIdentifierType> scopeAreaList, List<AreaIdentifierType> userAreaList) throws SpatialModelMarshallException {
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

    private ClosestLocationSpatialRQ createClosestLocationRequest(PointType point, UnitType unit, List<LocationType> locationTypes) throws SpatialModelMarshallException {
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
