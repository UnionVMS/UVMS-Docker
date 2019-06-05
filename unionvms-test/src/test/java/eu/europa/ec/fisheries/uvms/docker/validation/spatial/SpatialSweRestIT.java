package eu.europa.ec.fisheries.uvms.docker.validation.spatial;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geomgraph.Position;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;
import eu.europa.ec.fisheries.schema.movement.v1.MovementPoint;
import eu.europa.ec.fisheries.schema.movement.v1.MovementType;
import eu.europa.ec.fisheries.schema.movement.v1.SegmentCategoryType;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import eu.europa.ec.fisheries.uvms.docker.validation.common.AbstractRest;
import eu.europa.ec.fisheries.uvms.spatial.model.exception.SpatialModelMarshallException;
import eu.europa.ec.fisheries.uvms.spatial.model.schemas.AllAreaTypesRequest;
import eu.europa.ec.fisheries.uvms.spatial.model.schemas.Area;
import eu.europa.ec.fisheries.uvms.spatial.model.schemas.AreaByCodeRequest;
import eu.europa.ec.fisheries.uvms.spatial.model.schemas.AreaByCodeResponse;
import eu.europa.ec.fisheries.uvms.spatial.model.schemas.AreaByLocationSpatialRQ;
import eu.europa.ec.fisheries.uvms.spatial.model.schemas.AreaExtendedIdentifierType;
import eu.europa.ec.fisheries.uvms.spatial.model.schemas.AreaIdentifierType;
import eu.europa.ec.fisheries.uvms.spatial.model.schemas.AreaSimpleType;
import eu.europa.ec.fisheries.uvms.spatial.model.schemas.AreaType;
import eu.europa.ec.fisheries.uvms.spatial.model.schemas.BatchSpatialEnrichmentRQ;
import eu.europa.ec.fisheries.uvms.spatial.model.schemas.BatchSpatialEnrichmentRS;
import eu.europa.ec.fisheries.uvms.spatial.model.schemas.ClosestAreaSpatialRQ;
import eu.europa.ec.fisheries.uvms.spatial.model.schemas.ClosestLocationSpatialRQ;
import eu.europa.ec.fisheries.uvms.spatial.model.schemas.CoordinatesFormat;
import eu.europa.ec.fisheries.uvms.spatial.model.schemas.FilterAreasSpatialRQ;
import eu.europa.ec.fisheries.uvms.spatial.model.schemas.FilterAreasSpatialRS;
import eu.europa.ec.fisheries.uvms.spatial.model.schemas.GeometryByPortCodeRequest;
import eu.europa.ec.fisheries.uvms.spatial.model.schemas.GeometryByPortCodeResponse;
import eu.europa.ec.fisheries.uvms.spatial.model.schemas.Location;
import eu.europa.ec.fisheries.uvms.spatial.model.schemas.LocationType;
import eu.europa.ec.fisheries.uvms.spatial.model.schemas.MapConfigurationType;
import eu.europa.ec.fisheries.uvms.spatial.model.schemas.PingRQ;
import eu.europa.ec.fisheries.uvms.spatial.model.schemas.PingRS;
import eu.europa.ec.fisheries.uvms.spatial.model.schemas.PointType;
import eu.europa.ec.fisheries.uvms.spatial.model.schemas.ScopeAreasType;
import eu.europa.ec.fisheries.uvms.spatial.model.schemas.SpatialDeleteMapConfigurationRQ;
import eu.europa.ec.fisheries.uvms.spatial.model.schemas.SpatialEnrichmentRQ;
import eu.europa.ec.fisheries.uvms.spatial.model.schemas.SpatialEnrichmentRQListElement;
import eu.europa.ec.fisheries.uvms.spatial.model.schemas.SpatialEnrichmentRS;
import eu.europa.ec.fisheries.uvms.spatial.model.schemas.SpatialEnrichmentRSListElement;
import eu.europa.ec.fisheries.uvms.spatial.model.schemas.SpatialGetMapConfigurationRQ;
import eu.europa.ec.fisheries.uvms.spatial.model.schemas.SpatialModuleMethod;
import eu.europa.ec.fisheries.uvms.spatial.model.schemas.SpatialSaveOrUpdateMapConfigurationRQ;
import eu.europa.ec.fisheries.uvms.spatial.model.schemas.UnitType;
import eu.europa.ec.fisheries.uvms.spatial.model.schemas.UserAreasType;

public class SpatialSweRestIT extends AbstractRest {
    int batchSize = 10;
    private Integer crs = 4326;
    private Double latitude = 57.715523;
    private Double longitude = 11.973965;

    @Test
    public void getAreaByLocation() throws Exception {

        PointType point = new PointType();
        point.setLatitude(latitude);
        point.setLongitude(longitude);
        point.setCrs(crs);

        AreaByLocationSpatialRQ areaByLocationSpatialRQ = new AreaByLocationSpatialRQ();
        areaByLocationSpatialRQ.setPoint(point);
        areaByLocationSpatialRQ.setMethod(SpatialModuleMethod.GET_AREA_BY_LOCATION);
        // @formatter:off
        Response ret = getWebTarget() 
                .path("spatialSwe/spatialnonsecure/json/getAreaByLocation")
                .request(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .post(Entity.json(areaByLocationSpatialRQ), Response.class);
        // @formatter:on

        Assert.assertEquals(200, ret.getStatus());
        List<AreaExtendedIdentifierType> list = ret.readEntity(new GenericType<List<AreaExtendedIdentifierType>>() {
        });
        List<String> control = new ArrayList<>();
        for (AreaExtendedIdentifierType aeit : list) {
            control.add(aeit.getName());
        }
        Assert.assertTrue(control.contains("Göteborg-Lundbyhamnen"));
    }

    @Test
    public void getAreaTypes() throws Exception {

        PointType point = new PointType();
        point.setLatitude(latitude);
        point.setLongitude(longitude);
        point.setCrs(crs);
        AllAreaTypesRequest request = createAllAreaTypesRequest();

        // @formatter:off
        Response ret =  getWebTarget() 
                .path("spatialSwe/spatialnonsecure/json/getAreaTypes")
                .request(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .post(Entity.json(request), Response.class);
        // @formatter:on

        Assert.assertEquals(200, ret.getStatus());

        List<String> list = ret.readEntity(new GenericType<List<String>>() {
        });

        List<String> control = new ArrayList<>();
        for (String str : list) {
            control.add(str);
        }
        Assert.assertTrue(control.contains("EEZ"));
    }

    @Test
    public void getClosestArea() throws Exception {

        PointType point = new PointType();
        point.setLatitude(latitude);
        point.setLongitude(longitude);
        point.setCrs(crs);
        ClosestAreaSpatialRQ request = createClosestAreaRequest(point, UnitType.METERS, Arrays.asList(AreaType.EEZ));

        // @formatter:off
        Response ret =  getWebTarget() 
                .path("spatialSwe/spatialnonsecure/json/getClosestArea")
                .request(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .post(Entity.json(request), Response.class);
        // @formatter:on

        Assert.assertEquals(200, ret.getStatus());
        List<Area> list = ret.readEntity(new GenericType<List<Area>>() {
        });

        List<String> control = new ArrayList<>();
        for (Area aeit : list) {
            control.add(aeit.getName());
        }
        Assert.assertTrue(control.contains("Swedish Exclusive Economic Zone"));
    }


    @Test
    @Ignore
    public void getClosestAreaWith4kPoints() throws Exception {

        double[] points = TestPoints.testpoints;
        StringBuilder sb = new StringBuilder();
        for(int i = 0 ; i < 8000 ; i = i + 2) {
            PointType point = new PointType();
            point.setLatitude(points[i]);
            point.setLongitude(points[i + 1]);
            point.setCrs(crs);
            ClosestAreaSpatialRQ request = createClosestAreaRequest(point, UnitType.METERS, Arrays.asList(AreaType.EEZ));

            // @formatter:off
            Response ret = getWebTarget()
                    .path("spatialSwe/spatialnonsecure/json/getClosestArea")
                    .request(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .post(Entity.json(request), Response.class);
            // @formatter:on

            Assert.assertEquals(200, ret.getStatus());
            List<Area> list = ret.readEntity(new GenericType<List<Area>>() {
            });

            sb.append("Latitude: " + point.getLatitude() + " Longitude: " + point.getLongitude());
            for (Area aeit : list) {
                sb.append(" " + aeit.getAreaType().value() + ": " + aeit.getCode());
            }
            sb.append("\r\n");

            if(i % 100 == 0){
                System.out.println("Number: " + i);
            }
        }
        System.out.println(sb.toString());
    }

    @Test
    public void getClosestLocation() throws Exception {

        PointType point = new PointType();
        point.setLatitude(latitude);
        point.setLongitude(longitude);
        point.setCrs(crs);
        ClosestLocationSpatialRQ request = createClosestLocationRequest(point, UnitType.METERS, Arrays.asList(LocationType.PORT));


        // @formatter:off
        Response ret =  getWebTarget() 
                .path("spatialSwe/spatialnonsecure/json/getClosestLocation")
                .request(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .post(Entity.json(request), Response.class);
        // @formatter:on

        Assert.assertEquals(200, ret.getStatus());

        List<Location> list = ret.readEntity(new GenericType<List<Location>>() {
        });


        List<String> control = new ArrayList<>();
        for (Location aeit : list) {
            control.add(aeit.getName());
        }
        Assert.assertTrue(control.contains("Göteborg-Ringökajen"));
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

        // @formatter:off
        Response ret = getWebTarget() 
                .path("spatialSwe/spatialnonsecure/json/getEnrichment")
                .request(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .post(Entity.json(request), Response.class);
        // @formatter:on

        Assert.assertEquals(200, ret.getStatus());
        SpatialEnrichmentRS rs = ret.readEntity(new GenericType<SpatialEnrichmentRS>() {
        });

        List<Location> list = rs.getClosestLocations().getClosestLocations();

        List<String> control = new ArrayList<>();
        for (Location aeit : list) {
            control.add(aeit.getName());
        }
        Assert.assertTrue(control.contains("Göteborg-Ringökajen"));
    }

    @Test
    @Ignore         //this functionality has been removed
    public void getFilterArea() throws Exception {

        AreaIdentifierType areaType = new AreaIdentifierType();
        areaType.setAreaType(AreaType.EEZ);
        areaType.setId("1");
        FilterAreasSpatialRQ request = createFilterAreaSpatialRequest(Arrays.asList(areaType), Arrays.asList(areaType));


        // @formatter:off
        Response ret = getWebTarget() 
                .path("spatialSwe/spatialnonsecure/json/getFilterArea")
                .request(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .post(Entity.json(request), Response.class);
        // @formatter:on

        Assert.assertEquals(200, ret.getStatus());
        FilterAreasSpatialRS rs = ret.readEntity(new GenericType<FilterAreasSpatialRS>() {
        });
        String geometry = rs.getGeometry();

        Assert.assertTrue(geometry.contains("POLYGON"));
    }

    @Test
    @Ignore         //this functionality has been removed
    public void getMapConfiguration() throws Exception {

        SpatialGetMapConfigurationRQ request = getSpatialGetMapConfigurationRQ();
        request.setReportId(1);

        // @formatter:off
        Response ret = getWebTarget() 
                .path("spatialSwe/spatialnonsecure/json/getMapConfiguration")
                .request(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .post(Entity.json(request), Response.class);
        // @formatter:on

        Assert.assertEquals(200, ret.getStatus());
        FilterAreasSpatialRS rs = ret.readEntity(new GenericType<FilterAreasSpatialRS>() {
        });
        // until we figure out something better
        Assert.assertTrue(rs != null);
    }

    private SpatialDeleteMapConfigurationRQ creatSpatialDeleteMapConfigurationRQ() {
        SpatialDeleteMapConfigurationRQ request = new SpatialDeleteMapConfigurationRQ();
        return request;
    }

    @Test
    public void ping() throws Exception {

        PingRQ request = new PingRQ();
        // @formatter:off
        Response ret = getWebTarget() 
                .path("spatialSwe/spatialnonsecure/json/ping")
                .request(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .post(Entity.json(request), Response.class);

        PingRS rs = ret.readEntity(new GenericType<PingRS>() {});

        String str = rs.getResponse();
        Assert.assertEquals("pong", str);
        // @formatter:on
    }

    @Test
    public void getAreaByCode() throws Exception {

        AreaByCodeRequest request = createAreaByCodeRequest();
        // @formatter:off
        Response ret = getWebTarget() 
                .path("spatialSwe/spatialnonsecure/json/getAreaByCode")
                .request(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .post(Entity.json(request), Response.class);
        // @formatter:on

        Assert.assertEquals(200, ret.getStatus());

        AreaByCodeResponse response = ret.readEntity(new GenericType<AreaByCodeResponse>() {
        });

        List<AreaSimpleType> resultList = response.getAreaSimples();
        Assert.assertNotNull(resultList);
        Assert.assertTrue(resultList.size() > 0);

        AreaSimpleType line = resultList.get(0);
        String wkt = line.getWkt();
        Assert.assertNotNull(wkt);
        Assert.assertTrue(wkt.contains("POLYGON"));
    }

    @Test
    public void getGeometryByPortCode() throws Exception {

        GeometryByPortCodeRequest request = createToGeometryByPortCodeRequest("AOLAD");
        // @formatter:off
        Response ret = getWebTarget() 
                .path("spatialSwe/spatialnonsecure/json/getGeometryByPortCode")
                .request(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .post(Entity.json(request), Response.class);

        Assert.assertEquals(200, ret.getStatus());
        GeometryByPortCodeResponse rs = ret.readEntity(new GenericType<GeometryByPortCodeResponse>() {});
        String geometry = rs.getPortGeometry();
        Assert.assertTrue(geometry != null);
        Assert.assertTrue(geometry.contains("MULTIPOINT"));
        // @formatter:on
    }

    @Test
    @Ignore     //this functionality has been removed
    public void getEnrichmentBatch() throws Exception {


        List<LocationType> locationTypes = Arrays.asList(LocationType.PORT);
        List<AreaType> areaTypes = Arrays.asList(AreaType.COUNTRY);

        PointType point = new PointType();
        Double localLatitude = latitude;
        Double localLongitude = longitude;
        point.setCrs(crs);
        point.setLatitude(localLatitude);
        point.setLongitude(localLongitude);

        List<SpatialEnrichmentRQListElement> listElements = new ArrayList<>();

        for (int i = 0; i < batchSize; i++) {
            listElements.add(createBatchLine(areaTypes, locationTypes, point));
            Double lat = point.getLatitude();
            lat = lat + 0.001;
            point.setLatitude(lat);
        }
        BatchSpatialEnrichmentRQ request = createBATCHSpatialEnrichmentRequest(listElements);


        // @formatter:off
        long then = System.currentTimeMillis();
        Response ret = getWebTarget() 
                .path("spatialSwe/spatialnonsecure/json/getEnrichmentBatch")
                .request(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .post(Entity.json(request), Response.class);
        long now = System.currentTimeMillis();
        // @formatter:on

        Assert.assertEquals(200, ret.getStatus());
        BatchSpatialEnrichmentRS rs = ret.readEntity(new GenericType<BatchSpatialEnrichmentRS>() {
        });


        List<SpatialEnrichmentRSListElement> responseLines = rs.getEnrichmentRespLists();

        Assert.assertTrue(responseLines.size() == batchSize);

        double elapsed = (double) (now - then);
        double elapsedSecs = (elapsed / 1000);

        //System.out.println(batchSize + " enrichments took " + elapsedSecs + " secs");
        //System.out.println(batchSize + " Average  " + elapsedSecs / batchSize);
    }

    @Test
    public void getEnrichmentMulti() throws Exception {

        PointType point = new PointType();
        point.setCrs(4326);
        point.setLatitude(latitude);
        point.setLongitude(longitude);

        List<LocationType> locationTypes = Arrays.asList(LocationType.PORT);
        List<AreaType> areaTypes = Arrays.asList(AreaType.COUNTRY);


        List<SpatialEnrichmentRQ> requests = new ArrayList<>();
        for (int i = 0; i < batchSize; i++) {

            Double lat = point.getLatitude();
            lat = lat + 0.001;
            point.setLatitude(lat);
            SpatialEnrichmentRQ request = createSpatialEnrichmentRequest(point, UnitType.NAUTICAL_MILES, locationTypes, areaTypes);
            requests.add(request);
        }

        // @formatter:off
         long then = System.currentTimeMillis();
        for(int i = 0 ; i < batchSize ; i++) {
             SpatialEnrichmentRQ request = requests.get(i);
          Response ret = getWebTarget() 
                .path("spatialSwe/spatialnonsecure/json/getEnrichment")
                .request(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .post(Entity.json(request), Response.class);
        // @formatter:on

            Assert.assertEquals(200, ret.getStatus());
            SpatialEnrichmentRS rs = ret.readEntity(new GenericType<SpatialEnrichmentRS>() {
            });
        }
        long now = System.currentTimeMillis();

        double elapsed = (double) (now - then);
        double elapsedSecs = (elapsed / 1000);

       // System.out.println(batchSize + " enrichments took " + elapsedSecs + " secs");
       // System.out.println(batchSize + " Average  " + elapsedSecs / batchSize);


    }

    @Test
    public void testSegmentCategoriFromHelsingborgToLidkoping(){
        List<MovementType> movementList = new ArrayList<>();
        String[] pointArray = HelsingborgToLidkoping.helsingborgToLidköping;
        Instant start = Instant.now().minusSeconds(60 * pointArray.length);

        for(int i = 0 ; i < pointArray.length - 1 ; i++) {

            movementList.clear();

            MovementType move = new MovementType();
            Point p = (Point)getGeometryFromWKTSrring(pointArray[i]);
            MovementPoint pos = new MovementPoint();
            pos.setLongitude(p.getX());
            pos.setLatitude(p.getY());
            move.setPosition(pos);
            move.setPositionTime(Date.from(start.plusSeconds(i * 60)));

            movementList.add(move);

            move = new MovementType();
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
            //System.out.println(pointArray[i] + "  " + pointArray[i+1] + " " + response.readEntity(new GenericType<SegmentCategoryType>() {}));

        }
    }


    /*******************************************************************************************************************
     * HELPERS                                                                                                         *
     *******************************************************************************************************************/

    /*

        List<LocationType> locationTypes = Arrays.asList(LocationType.PORT);
        List<AreaType> areaTypes = Arrays.asList(AreaType.COUNTRY);
        SpatialEnrichmentRQ request = createSpatialEnrichmentRequest(point, UnitType.NAUTICAL_MILES, locationTypes, areaTypes);


     */
    private BatchSpatialEnrichmentRQ createBATCHSpatialEnrichmentRequest(List<SpatialEnrichmentRQListElement> listElements) {

        BatchSpatialEnrichmentRQ request = new BatchSpatialEnrichmentRQ();
        request.setEnrichmentLists(listElements);
        return request;
    }

    private SpatialEnrichmentRQListElement createBatchLine(List<AreaType> areaTypes, List<LocationType> locationTypes, PointType point) {
        SpatialEnrichmentRQListElement line = new SpatialEnrichmentRQListElement();
        line.setUnit(UnitType.NAUTICAL_MILES);
        line.setAreaTypes(areaTypes);
        line.setLocationTypes(locationTypes);
        line.setPoint(point);
        return line;
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
