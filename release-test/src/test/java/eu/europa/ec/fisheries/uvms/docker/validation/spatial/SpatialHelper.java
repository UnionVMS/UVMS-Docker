package eu.europa.ec.fisheries.uvms.docker.validation.spatial;

import java.io.StringReader;
import java.io.StringWriter;

import javax.jms.Message;
import javax.jms.TextMessage;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import eu.europa.ec.fisheries.uvms.docker.validation.common.AbstractHelper;
import eu.europa.ec.fisheries.uvms.docker.validation.common.MessageHelper;
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
	
}
