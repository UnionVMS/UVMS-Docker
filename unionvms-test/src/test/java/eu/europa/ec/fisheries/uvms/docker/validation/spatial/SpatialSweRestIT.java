package eu.europa.ec.fisheries.uvms.docker.validation.spatial;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationModule;
import eu.europa.ec.fisheries.uvms.docker.validation.spatial.dto.AreaExtendedIdentifierType;
import eu.europa.ec.fisheries.uvms.docker.validation.spatial.dto.InputToSegmentCategoryType;
import eu.europa.ec.fisheries.uvms.spatial.model.schemas.*;
import org.junit.Before;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;
import eu.europa.ec.fisheries.schema.movement.v1.MovementPoint;
import eu.europa.ec.fisheries.schema.movement.v1.MovementType;
import eu.europa.ec.fisheries.uvms.docker.validation.common.AbstractRest;
import eu.europa.ec.fisheries.uvms.spatial.model.exception.SpatialModelMarshallException;
import org.junit.Ignore;
import org.junit.Test;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class SpatialSweRestIT extends AbstractRest {
    private Integer crs = 4326;
    private Double latitude = 57.715523;
    private Double longitude = 11.973965;

    @Before
    public void init(){
        OBJECT_MAPPER.registerModule(new JaxbAnnotationModule());
    }

    @Test
    public void getAreaByLocation() {
        PointType point = new PointType();
        point.setLatitude(latitude);
        point.setLongitude(longitude);
        point.setCrs(crs);

        AreaByLocationSpatialRQ areaByLocationSpatialRQ = new AreaByLocationSpatialRQ();
        areaByLocationSpatialRQ.setPoint(point);
        areaByLocationSpatialRQ.setMethod(SpatialModuleMethod.GET_AREA_BY_LOCATION);

        Response ret = getWebTarget() 
                .path("spatialSwe/spatialnonsecure/json/getAreaByLocation")
                .request(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .post(Entity.json(areaByLocationSpatialRQ), Response.class);

        assertEquals(200, ret.getStatus());

        List<AreaExtendedIdentifierType> list = ret.readEntity(new GenericType<List<AreaExtendedIdentifierType>>(){});
        List<String> control = list.stream().map(AreaExtendedIdentifierType::getName).collect(Collectors.toList());
        assertTrue(control.contains("Göteborg-Lundbyhamnen"));
    }

    @Test
    public void getAreaTypes() {
        PointType point = new PointType();
        point.setLatitude(latitude);
        point.setLongitude(longitude);
        point.setCrs(crs);
        AllAreaTypesRequest request = createAllAreaTypesRequest();

        Response ret =  getWebTarget() 
                .path("spatialSwe/spatialnonsecure/json/getAreaTypes")
                .request(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .post(Entity.json(request), Response.class);

        assertEquals(200, ret.getStatus());

        List<String> list = ret.readEntity(new GenericType<List<String>>() {});
        assertTrue(list.contains("EEZ"));
    }

    @Test
    public void getClosestArea() throws Exception {
        PointType point = new PointType();
        point.setLatitude(latitude);
        point.setLongitude(longitude);
        point.setCrs(crs);
        ClosestAreaSpatialRQ request = createClosestAreaRequest(point,
                UnitType.METERS, Collections.singletonList(AreaType.EEZ));

        Response ret =  getWebTarget() 
                .path("spatialSwe/spatialnonsecure/json/getClosestArea")
                .request(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .post(Entity.json(request), Response.class);

        assertEquals(200, ret.getStatus());
        String json = ret.readEntity(String.class);
        assertTrue(json.contains("Swedish Exclusive Economic Zone"));
    }

    @Test
    @Ignore
    public void getClosestAreaWith4kPoints() throws Exception {
        double[] points = TestPoints.testpoints;
        StringBuilder builder = new StringBuilder();
        for(int i = 0 ; i < 8000 ; i = i + 2) {
            PointType point = new PointType();
            point.setLatitude(points[i]);
            point.setLongitude(points[i + 1]);
            point.setCrs(crs);
            ClosestAreaSpatialRQ request = createClosestAreaRequest(point,
                    UnitType.METERS, Collections.singletonList(AreaType.EEZ));

            Response ret = getWebTarget()
                    .path("spatialSwe/spatialnonsecure/json/getClosestArea")
                    .request(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .post(Entity.json(request), Response.class);

            assertEquals(200, ret.getStatus());
            List<Area> list = ret.readEntity(new GenericType<List<Area>>() {});

            builder.append("Latitude: " + point.getLatitude() + " Longitude: " + point.getLongitude());
            for (Area aeit : list) {
                builder.append(" " + aeit.getAreaType().value() + ": " + aeit.getCode());
            }
            builder.append("\r\n");

            if(i % 100 == 0){
                System.out.println("Number: " + i);
            }
        }
        System.out.println(builder.toString());
    }

    @Test
    public void getClosestLocation() throws Exception {
        PointType point = new PointType();
        point.setLatitude(latitude);
        point.setLongitude(longitude);
        point.setCrs(crs);
        ClosestLocationSpatialRQ request = createClosestLocationRequest(point,
                UnitType.METERS, Collections.singletonList(LocationType.PORT));

        Response ret =  getWebTarget() 
                .path("spatialSwe/spatialnonsecure/json/getClosestLocation")
                .request(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .post(Entity.json(request), Response.class);

        assertEquals(200, ret.getStatus());

        String json = ret.readEntity(String.class);
        assertTrue(json.contains("Göteborg-Ringökajen"));
    }

    @Test
    public void getEnrichment() throws Exception {
        PointType point = new PointType();
        // This magical int (4326) is the World Geodetic System 1984, aka EPSG:4326.
        // See: https://en.wikipedia.org/wiki/World_Geodetic_System or http://spatialreference.org/ref/epsg/wgs-84/
        point.setCrs(4326);
        point.setLatitude(latitude);
        point.setLongitude(longitude);

        List<LocationType> locationTypes = Collections.singletonList(LocationType.PORT);
        List<AreaType> areaTypes = Collections.singletonList(AreaType.COUNTRY);
        SpatialEnrichmentRQ request = createSpatialEnrichmentRequest(point, UnitType.NAUTICAL_MILES, locationTypes, areaTypes);


        Response ret = getWebTarget() 
                .path("spatialSwe/spatialnonsecure/json/getEnrichment")
                .request(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .post(Entity.json(request), Response.class);

        assertEquals(200, ret.getStatus());
        SpatialEnrichmentRS enrichmentRS = ret.readEntity(new GenericType<SpatialEnrichmentRS>() {});

        List<Location> list = enrichmentRS.getClosestLocations().getClosestLocations();
        List<String> control = list.stream().map(Location::getName).collect(Collectors.toList());
        assertTrue(control.contains("Göteborg-Ringökajen"));
    }

    @Test
    public void ping() {
        PingRQ request = new PingRQ();

        Response response = getWebTarget()
                .path("spatialSwe/spatialnonsecure/json/ping")
                .request(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .post(Entity.json(request), Response.class);

        PingRS pingRS = response.readEntity(new GenericType<PingRS>() {});
        assertEquals("pong", pingRS.getResponse());
    }

    @Test
    public void getAreaByCode() {
        AreaByCodeRequest request = createAreaByCodeRequest();

        Response ret = getWebTarget() 
                .path("spatialSwe/spatialnonsecure/json/getAreaByCode")
                .request(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .post(Entity.json(request), Response.class);

        assertEquals(200, ret.getStatus());

        AreaByCodeResponse response = ret.readEntity(new GenericType<AreaByCodeResponse>() {});

        List<AreaSimpleType> resultList = response.getAreaSimples();
        assertNotNull(resultList);
        assertTrue(resultList.size() > 0);

        AreaSimpleType line = resultList.get(0);
        String wkt = line.getWkt();
        assertNotNull(wkt);
        assertTrue(wkt.contains("POLYGON"));
    }

    @Test
    public void getGeometryByPortCode() throws Exception {
        GeometryByPortCodeRequest request = createToGeometryByPortCodeRequest("AOLAD");

        Response ret = getWebTarget() 
                .path("spatialSwe/spatialnonsecure/json/getGeometryByPortCode")
                .request(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .post(Entity.json(request), Response.class);

        assertEquals(200, ret.getStatus());
        GeometryByPortCodeResponse rs = ret.readEntity(new GenericType<GeometryByPortCodeResponse>() {});
        String geometry = rs.getPortGeometry();
        assertNotNull(geometry);
        assertTrue(geometry.contains("MULTIPOINT"));
    }

    @Test
    public void getEnrichmentMulti() throws Exception {
        PointType point = new PointType();
        point.setCrs(4326);
        point.setLatitude(latitude);
        point.setLongitude(longitude);

        List<LocationType> locationTypes = Collections.singletonList(LocationType.PORT);
        List<AreaType> areaTypes = Collections.singletonList(AreaType.COUNTRY);

        List<SpatialEnrichmentRQ> requests = new ArrayList<>();
        int batchSize = 10;
        for (int i = 0; i < batchSize; i++) {
            Double lat = point.getLatitude();
            lat = lat + 0.001;
            point.setLatitude(lat);
            SpatialEnrichmentRQ request = createSpatialEnrichmentRequest(point, UnitType.NAUTICAL_MILES, locationTypes, areaTypes);
            requests.add(request);
        }

        for(int i = 0; i < batchSize; i++) {
            SpatialEnrichmentRQ request = requests.get(i);
            Response ret = getWebTarget()
                .path("spatialSwe/spatialnonsecure/json/getEnrichment")
                .request(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .post(Entity.json(request), Response.class);

            assertEquals(200, ret.getStatus());
        }
    }

    @Test
    public void testSegmentCategoriFromHelsingborgToLidkoping() throws JsonProcessingException {
        List<InputToSegmentCategoryType> movementList = new ArrayList<>();
        String[] pointArray = HelsingborgToLidkoping.helsingborgToLidköping;
        Instant start = Instant.now().minusSeconds(60 * pointArray.length);
        OBJECT_MAPPER.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, true);

        for(int i = 0 ; i < pointArray.length - 1 ; i++) {
            movementList.clear();

            InputToSegmentCategoryType move = new InputToSegmentCategoryType();
            Point p = (Point)getGeometryFromWKTSrring(pointArray[i]);
            MovementPoint pos = new MovementPoint();
            pos.setLongitude(p.getX());
            pos.setLatitude(p.getY());
            move.setPosition(pos);
            move.setPositionTime(Date.from(start.plusSeconds(i * 60)));

            movementList.add(move);

            move = new InputToSegmentCategoryType();
            p = (Point)getGeometryFromWKTSrring(pointArray[i + 1]);
            pos = new MovementPoint();
            pos.setLongitude(p.getX());
            pos.setLatitude(p.getY());
            move.setPosition(pos);
            move.setPositionTime(Date.from(start.plusSeconds((i + 1) * 60)));

            movementList.add(move);

            Response response =  getWebTarget()
                    .path("spatialSwe/spatialnonsecure/json/getSegmentCategoryType")
                    .request(MediaType.APPLICATION_JSON)
                    .post(Entity.json(movementList), Response.class);

            assertEquals(200, response.getStatus());
        }
    }

    @Test
    public void getAllNonUserAreasTest() throws Exception {
        Response response =  getWebTarget()
                .path("spatialSwe/rest/area/allNonUserAreas")
                .request(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getValidJwtToken())
                .get(Response.class);

        //System.out.println(response.readEntity(String.class));
        assertEquals(200, response.getStatus());
    }

    @Test
    public void getAllLayersTest() throws Exception {
        Response response =  getWebTarget()
                .path("spatialSwe/rest/area/layers")
                .request(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getValidJwtToken())
                .get(Response.class);

        //System.out.println(response.readEntity(String.class));
        assertEquals(200, response.getStatus());
    }

    @Test
    public void getAAreaTypesAreasTest() throws Exception {
        Response response =  getWebTarget()
                .path("spatialSwe/rest/area/getAreaLayer/EEZ")
                .request(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getValidJwtToken())
                .get(Response.class);

        //System.out.println(response.readEntity(String.class));
        assertEquals(200, response.getStatus());
    }

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

    private AllAreaTypesRequest createAllAreaTypesRequest() {
        AllAreaTypesRequest allAreaTypesRequest = new AllAreaTypesRequest();
        return allAreaTypesRequest;
    }


    private GeometryByPortCodeRequest createToGeometryByPortCodeRequest(String portCode) {
        GeometryByPortCodeRequest request = new GeometryByPortCodeRequest();
        request.setPortCode(portCode);
        request.setMethod(SpatialModuleMethod.GET_GEOMETRY_BY_PORT_CODE);
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

    private static Geometry getGeometryFromWKTSrring(String wkt) {
        try {
            WKTReader reader = new WKTReader();
            Geometry geom = reader.read(wkt);
            geom.setSRID(4326);
            return geom;
        }catch (ParseException e){
            throw new IllegalArgumentException("Inputstring " + wkt + " causes a parse exception.", e);
        }
    }
}
