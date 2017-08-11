package eu.europa.ec.fisheries.uvms.docker.validation.spatial;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.jms.Message;
import javax.jms.TextMessage;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import eu.europa.ec.fisheries.uvms.docker.validation.common.AbstractHelper;
import eu.europa.ec.fisheries.uvms.docker.validation.common.MessageHelper;
import eu.europa.ec.fisheries.uvms.docker.validation.movement.LatLong;
import eu.europa.ec.fisheries.uvms.spatial.model.schemas.SpatialEnrichmentRQ;
import eu.europa.ec.fisheries.uvms.spatial.model.schemas.SpatialEnrichmentRS;

/**
 * The Class SpatialHelper.
 */
public class SpatialHelper extends AbstractHelper {

	/** The Constant UVMS_SPATIAL_REQUEST_QUEUE. */
	private static final String UVMS_SPATIAL_REQUEST_QUEUE = "UVMSSpatialEvent";

	public String marshall(final SpatialEnrichmentRQ request) throws JAXBException {
		final StringWriter sw = new StringWriter();
		JAXBContext.newInstance(SpatialEnrichmentRQ.class).createMarshaller().marshal(request, sw);
		return sw.toString();
	}

	public SpatialEnrichmentRS unMarshall(final Message response) throws Exception {
		TextMessage textMessage = (TextMessage) response;
		JAXBContext jaxbContext = JAXBContext.newInstance(SpatialEnrichmentRS.class);
		return (SpatialEnrichmentRS) jaxbContext.createUnmarshaller()
				.unmarshal(new StringReader(textMessage.getText()));
	}

	public SpatialEnrichmentRS createSpatialEnrichment(SpatialEnrichmentRQ request) throws Exception {
		Message messageResponse = MessageHelper.getMessageResponse(UVMS_SPATIAL_REQUEST_QUEUE, marshall(request));
		return unMarshall(messageResponse);
	}
	
	public List<LatLong> createRutt(int numberPositions) {
		return createRutt(15 * 1000,numberPositions);
	}

	public List<LatLong> createRutt(int movementTimeDeltaInMillis, int numberPositions) {

		List<LatLong> rutt = new ArrayList<>();
		long ts = System.currentTimeMillis();
		rutt.add(new LatLong(50d, 50d, getDate(ts += movementTimeDeltaInMillis)));
		rutt.add(new LatLong(49d, 49d, getDate(ts += movementTimeDeltaInMillis)));
		rutt.add(new LatLong(48d, 48d, getDate(ts += movementTimeDeltaInMillis)));
		rutt.add(new LatLong(47d, 47d, getDate(ts += movementTimeDeltaInMillis)));
		rutt.add(new LatLong(46d, 46d, getDate(ts += movementTimeDeltaInMillis)));
		rutt.add(new LatLong(45d, 45d, getDate(ts += movementTimeDeltaInMillis)));
		
		return rutt.subList(0, numberPositions);

	}

	private Date getDate(Long millis) {
		return new Date(millis);
	}

}
