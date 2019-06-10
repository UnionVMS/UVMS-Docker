package eu.europa.ec.fisheries.uvms.docker.validation.spatial;

import eu.europa.ec.fisheries.uvms.docker.validation.common.AbstractHelper;
import eu.europa.ec.fisheries.uvms.docker.validation.common.MessageHelper;
import eu.europa.ec.fisheries.uvms.spatial.model.schemas.SpatialEnrichmentRQ;
import eu.europa.ec.fisheries.uvms.spatial.model.schemas.SpatialEnrichmentRS;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import java.io.StringReader;
import java.io.StringWriter;

public class SpatialHelper extends AbstractHelper {

	private static final String UVMS_SPATIAL_REQUEST_QUEUE = "UVMSSpatialEvent";

	private final MessageHelper messageHelper;

	public SpatialHelper() throws JMSException {
		messageHelper = new MessageHelper();
	}

	public void close() {
		messageHelper.close();
	}

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
		Message messageResponse = messageHelper.getMessageResponse(UVMS_SPATIAL_REQUEST_QUEUE, marshall(request));
		return unMarshall(messageResponse);
	}

}
