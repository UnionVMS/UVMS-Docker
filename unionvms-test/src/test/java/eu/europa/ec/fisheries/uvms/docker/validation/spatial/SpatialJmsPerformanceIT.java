package eu.europa.ec.fisheries.uvms.docker.validation.spatial;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import eu.europa.ec.fisheries.uvms.docker.validation.common.MessageHelper;
import org.databene.contiperf.PerfTest;
import org.databene.contiperf.Required;
import org.databene.contiperf.junit.ContiPerfRule;
import org.junit.*;
import eu.europa.ec.fisheries.uvms.docker.validation.common.AbstractRest;
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
 * The Class SpatialJmsPerformanceIT.
 */
public class SpatialJmsPerformanceIT extends AbstractRest {

	/** The i. */
	@Rule
	public ContiPerfRule i = new ContiPerfRule();

	/** The spatial helper. */

	private static MovementHelper movementHelper;
	private static SpatialHelper spatialHelper;

	@BeforeClass
	public static void setup() throws JMSException {
		movementHelper = new MovementHelper();
		spatialHelper = new SpatialHelper();
	}

	@AfterClass
	public static void cleanup() {
		movementHelper.close();
		spatialHelper.close();
	}


	/** The create rutt. */
	private List<LatLong> createRutt = movementHelper.createRutt(30);
		
	/**
	 * Creates the spatial enrichment request performance test.
	 *
	 * @throws Exception the exception
	 */
	@Test
	@PerfTest(threads = 2, duration = 10000)
	@Required(max = 6900, average = 2500, percentile95 = 2500, throughput = 1)
	@Ignore				//all jms endpoints, wit hthe exceptions of config, has been removed
	public void createSpatialEnrichmentRequestPerformanceTest() throws Exception {
		LatLong position = createRutt.get(ThreadLocalRandom.current().nextInt(0, 30));

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
	
}
