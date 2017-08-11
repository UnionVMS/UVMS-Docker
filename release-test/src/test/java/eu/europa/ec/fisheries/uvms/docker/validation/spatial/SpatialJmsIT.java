package eu.europa.ec.fisheries.uvms.docker.validation.spatial;

import java.util.List;

import org.junit.Test;

import eu.europa.ec.fisheries.uvms.docker.validation.common.AbstractRestServiceTest;
import eu.europa.ec.fisheries.uvms.docker.validation.common.MessageHelper;
import eu.europa.ec.fisheries.uvms.docker.validation.movement.LatLong;
import eu.europa.ec.fisheries.uvms.spatial.model.schemas.AreaType;
import eu.europa.ec.fisheries.uvms.spatial.model.schemas.LocationType;
import eu.europa.ec.fisheries.uvms.spatial.model.schemas.PointType;
import eu.europa.ec.fisheries.uvms.spatial.model.schemas.SpatialEnrichmentRQ;
import eu.europa.ec.fisheries.uvms.spatial.model.schemas.SpatialEnrichmentRQ.AreaTypes;
import eu.europa.ec.fisheries.uvms.spatial.model.schemas.SpatialEnrichmentRQ.LocationTypes;
import eu.europa.ec.fisheries.uvms.spatial.model.schemas.SpatialEnrichmentRS;
import eu.europa.ec.fisheries.uvms.spatial.model.schemas.SpatialModuleMethod;
import eu.europa.ec.fisheries.uvms.spatial.model.schemas.UnitType;

/**
 * The Class SpatialJmsIT.
 */
public class SpatialJmsIT extends AbstractRestServiceTest {

	/** The spatial helper. */
	private SpatialHelper spatialHelper = new SpatialHelper();

	/**
	 * Creates the spatial enrichment request test.
	 *
	 * @throws Exception the exception
	 */
	@Test(timeout = 10000)
	public void createSpatialEnrichmentRequestTest() throws Exception {
		LatLong position = spatialHelper.createRutt(1).get(0);

		SpatialEnrichmentRQ spatialEnrichmentRQ = new SpatialEnrichmentRQ();
		AreaTypes areaTypes = new AreaTypes();
		areaTypes.getAreaTypes().add(AreaType.COUNTRY);
		areaTypes.getAreaTypes().add(AreaType.PORT);
		areaTypes.getAreaTypes().add(AreaType.FMZ);
		

		spatialEnrichmentRQ.setAreaTypes(areaTypes);
		LocationTypes locationTypes = new LocationTypes();
		locationTypes.getLocationTypes().add(LocationType.PORT);

		spatialEnrichmentRQ.setLocationTypes(locationTypes);
		spatialEnrichmentRQ.setMethod(SpatialModuleMethod.GET_ENRICHMENT);
		spatialEnrichmentRQ.setUnit(UnitType.NAUTICAL_MILES);
		PointType pointType = new PointType();
		spatialEnrichmentRQ.setPoint(pointType);
		pointType.setLatitude(position.latitude);
		pointType.setLongitude(position.longitude);
		pointType.setCrs(4326);

		SpatialEnrichmentRS spatialEnrichmentRS = spatialHelper.createSpatialEnrichment(spatialEnrichmentRQ);
		assertNotNull(spatialEnrichmentRS);
	}
	


	@Test(timeout = 40000)
	public void createSpatialEnrichmentRequestForRuttTest() throws Exception {
		List<LatLong> position = spatialHelper.createRutt(4);

		for (LatLong latLong : position) {
			SpatialEnrichmentRQ spatialEnrichmentRQ = new SpatialEnrichmentRQ();
			AreaTypes areaTypes = new AreaTypes();
			areaTypes.getAreaTypes().add(AreaType.COUNTRY);
			areaTypes.getAreaTypes().add(AreaType.RFMO);
			areaTypes.getAreaTypes().add(AreaType.EEZ);

			spatialEnrichmentRQ.setAreaTypes(areaTypes);
			LocationTypes locationTypes = new LocationTypes();
			locationTypes.getLocationTypes().add(LocationType.PORT);

			spatialEnrichmentRQ.setLocationTypes(locationTypes);
			spatialEnrichmentRQ.setMethod(SpatialModuleMethod.GET_ENRICHMENT);
			spatialEnrichmentRQ.setUnit(UnitType.NAUTICAL_MILES);
			PointType pointType = new PointType();
			spatialEnrichmentRQ.setPoint(pointType);
			pointType.setLatitude(latLong.latitude);
			pointType.setLongitude(latLong.longitude);
			pointType.setCrs(4326);

			SpatialEnrichmentRS spatialEnrichmentRS = spatialHelper.createSpatialEnrichment(spatialEnrichmentRQ);
			assertNotNull(spatialEnrichmentRS);
			
		}
	}

	/**
	 * Check all spatial request processed on queue.
	 *
	 * @throws Exception the exception
	 */
	@Test
	public void checkAllSpatialRequestProcessedOnQueue() throws Exception {
		assertFalse(MessageHelper.checkQueueHasElements("UVMSSpatialEvent"));
	}

}
