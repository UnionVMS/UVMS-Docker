package eu.europa.ec.fisheries.uvms.docker.validation.spatial;

import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import eu.europa.ec.fisheries.uvms.docker.validation.common.AbstractRest;
import eu.europa.ec.fisheries.uvms.docker.validation.common.MessageHelper;
import eu.europa.ec.fisheries.uvms.docker.validation.movement.LatLong;
import eu.europa.ec.fisheries.uvms.docker.validation.movement.MovementHelper;
import eu.europa.ec.fisheries.uvms.spatial.model.schemas.AreaType;
import eu.europa.ec.fisheries.uvms.spatial.model.schemas.LocationType;
import eu.europa.ec.fisheries.uvms.spatial.model.schemas.PointType;
import eu.europa.ec.fisheries.uvms.spatial.model.schemas.SpatialEnrichmentRQ;
import eu.europa.ec.fisheries.uvms.spatial.model.schemas.SpatialEnrichmentRQ.AreaTypes;
import eu.europa.ec.fisheries.uvms.spatial.model.schemas.SpatialEnrichmentRQ.LocationTypes;
import eu.europa.ec.fisheries.uvms.spatial.model.schemas.SpatialEnrichmentRS;
import eu.europa.ec.fisheries.uvms.spatial.model.schemas.SpatialModuleMethod;
import eu.europa.ec.fisheries.uvms.spatial.model.schemas.UnitType;

import javax.jms.JMSException;

/**
 * The Class SpatialJmsIT.
 */
public class SpatialJmsIT extends AbstractRest {

	/** The spatial helper. */
	private static SpatialHelper spatialHelper;

	private static MovementHelper movementHelper;

	private static MessageHelper messageHelper;

	@BeforeClass
	public static void setup() throws JMSException {
		spatialHelper = new SpatialHelper();
		movementHelper = new MovementHelper();
		messageHelper = new MessageHelper();
	}

	@AfterClass
	public static void cleanup() {
		spatialHelper.close();
		movementHelper.close();
		messageHelper.close();
	}

	/**
	 * Creates the spatial enrichment request test.
	 *
	 * @throws Exception the exception
	 */
	@Test(timeout = 10000)
    //@Ignore         //all jms endpoints, wit hthe exceptions of config, has been removed
	public void createSpatialEnrichmentRequestTest() throws Exception {
		LatLong position = movementHelper.createRutt(1).get(0);

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
    //@Ignore         //all jms endpoints, wit hthe exceptions of config, has been removed
	public void createSpatialEnrichmentRequestForRuttTest() throws Exception {
		List<LatLong> position = movementHelper.createRutt(10);

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
    //@Ignore     //all jms endpoints, wit hthe exceptions of config, has been removed
	public void checkAllSpatialRequestProcessedOnQueue() throws Exception {
		assertFalse(messageHelper.checkQueueHasElements("UVMSSpatialEvent"));
	}

}
