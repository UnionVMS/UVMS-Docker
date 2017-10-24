package eu.europa.ec.fisheries.uvms.docker.validation.plugins.flux;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.ws.BindingProvider;

import org.junit.Assert;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.MobileTerminalType;
import eu.europa.ec.fisheries.uvms.docker.validation.asset.AssetTestHelper;
import eu.europa.ec.fisheries.uvms.docker.validation.mobileterminal.MobileTerminalTestHelper;
import eu.europa.ec.fisheries.wsdl.asset.types.Asset;
import un.unece.uncefact.data.standard.fluxvesselpositionmessage._4.FLUXVesselPositionMessage;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._18.FLUXReportDocumentType;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._18.VesselTransportMeansType;
import xeu.bridge_connector.v1.RequestType;
import xeu.bridge_connector.v1.ResponseType;
import xeu.bridge_connector.wsdl.v1.BridgeConnectorPortType;
import xeu.bridge_connector.wsdl.v1.MovementService;

/**
 * The Class FluxMessageReceiverBeanTest.
 */
public class FluxMessageReceiverBeanTest extends Assert {

	private String exampleXml = "<soapenv:Envelope\r\n" + 
			"    xmlns:soapenv=\\\"http://schemas.xmlsoap.org/soap/envelope/\\\"\r\n" + 
			"    xmlns:urn=\\\"urn:xeu:bridge-connector:v1\\\">\\n   \r\n" + 
			"    <soapenv:Header/>\\n   \r\n" + 
			"    <soapenv:Body>\\n      \r\n" + 
			"        <urn:Connector2BridgeRequest>\\n         \\t\\n\\t\r\n" + 
			"            <rsm:FLUXVesselPositionMessage\r\n" + 
			"                xmlns:rsm=\\\"urn:un:unece:uncefact:data:standard:FLUXVesselPositionMessage:4\\\"\r\n" + 
			"                xmlns=\\\"urn:xeu:connector-bridge:v1\\\"\r\n" + 
			"                xmlns:ram=\\\"urn:un:unece:uncefact:data:standard:ReusableAggregateBusinessInformationEntity:18\\\"\r\n" + 
			"                xmlns:s=\\\"http://schemas.xmlsoap.org/soap/envelope/\\\"\r\n" + 
			"                xmlns:udt=\\\"urn:un:unece:uncefact:data:standard:UnqualifiedDataType:18\\\"\r\n" + 
			"                xmlns:xsd=\\\"http://www.w3.org/2001/XMLSchema\\\"\r\n" + 
			"                xmlns:xsi=\\\"http://www.w3.org/2001/XMLSchema-instance\\\" xsi:schemaLocation=\\\"urn:un:unece:uncefact:data:standard:FLUXVesselPositionMessage:4 FLUXVesselPositionMessage_4p0.xsd\\\">\r\n" + 
			"                <rsm:FLUXReportDocument>\r\n" + 
			"                    <ram:ID>19641756</ram:ID>\r\n" + 
			"                    <ram:CreationDateTime>\r\n" + 
			"                        <udt:DateTime>2017-02-05T19:50:00Z</udt:DateTime>\r\n" + 
			"                    </ram:CreationDateTime>\r\n" + 
			"                    <ram:PurposeCode>9</ram:PurposeCode>\r\n" + 
			"                    <ram:OwnerFLUXParty>\r\n" + 
			"                        <ram:ID>EST</ram:ID>\r\n" + 
			"                    </ram:OwnerFLUXParty>\r\n" + 
			"                </rsm:FLUXReportDocument>\r\n" + 
			"                <rsm:VesselTransportMeans>\r\n" + 
			"                    <ram:ID schemeID=\\\"CFR\\\">EST010101021</ram:ID>\r\n" + 
			"                    <ram:ID schemeID=\\\"EXT_MARKING\\\">FIN-1114-U</ram:ID>\r\n" + 
			"                    <ram:ID schemeID=\\\"IRCS\\\">OJPN</ram:ID>\r\n" + 
			"                    <ram:RegistrationVesselCountry>\r\n" + 
			"                        <ram:ID>EST</ram:ID>\r\n" + 
			"                    </ram:RegistrationVesselCountry>\r\n" + 
			"                    <ram:SpecifiedVesselPositionEvent>\r\n" + 
			"                        <ram:ObtainedOccurrenceDateTime>\r\n" + 
			"                            <udt:DateTime>2017-02-05T17:38:00Z</udt:DateTime>\r\n" + 
			"                        </ram:ObtainedOccurrenceDateTime>\r\n" + 
			"                        <ram:TypeCode>POS</ram:TypeCode>\r\n" + 
			"                        <ram:SpeedValueMeasure>7.8</ram:SpeedValueMeasure>\r\n" + 
			"                        <ram:CourseValueMeasure>282</ram:CourseValueMeasure>\r\n" + 
			"                        <ram:SpecifiedVesselGeographicalCoordinate>\r\n" + 
			"                            <ram:LatitudeMeasure>59.6480000000</ram:LatitudeMeasure>\r\n" + 
			"                            <ram:LongitudeMeasure>21.5740000000</ram:LongitudeMeasure>\r\n" + 
			"                        </ram:SpecifiedVesselGeographicalCoordinate>\r\n" + 
			"                    </ram:SpecifiedVesselPositionEvent>\r\n" + 
			"                </rsm:VesselTransportMeans>\r\n" + 
			"            </rsm:FLUXVesselPositionMessage>\\n      \r\n" + 
			"        </urn:Connector2BridgeRequest>\\n   \r\n" + 
			"    </soapenv:Body>\\n\r\n" + 
			"</soapenv:Envelope>";
	
	/**
	 * Post request type empty request failure test.
	 */
	@Test
	public void postRequestTypeEmptyRequestFailureTest() {
		BridgeConnectorPortType bridgeConnectorPortType = createBridgeConnector();
		RequestType requestType = new RequestType();
		ResponseType responseType = bridgeConnectorPortType.post(requestType);
		assertNotNull(responseType);
		assertEquals("NOK", responseType.getStatus());
	}

	/**
	 * Post request type request success test.
	 *
	 * @throws Exception the exception
	 */
	@Test
	public void postRequestTypeRequestSuccessTest() throws Exception {
		Asset testAsset = AssetTestHelper.createTestAsset();
		MobileTerminalType mobileTerminalType = MobileTerminalTestHelper.createMobileTerminalType();
		MobileTerminalTestHelper.assignMobileTerminal(testAsset, mobileTerminalType);
		
		BridgeConnectorPortType bridgeConnectorPortType = createBridgeConnector();
		RequestType requestType = new RequestType();

		FLUXVesselPositionMessage fLUXVesselPositionMessage = new FLUXVesselPositionMessage();
		VesselTransportMeansType vesselTransportMeansType = new VesselTransportMeansType();
		
		fLUXVesselPositionMessage.setVesselTransportMeans(vesselTransportMeansType);
		FLUXReportDocumentType fluxReportDocumentType = new FLUXReportDocumentType();
		fLUXVesselPositionMessage.setFLUXReportDocument(fluxReportDocumentType);

		requestType.setAny(createAnyElement(fLUXVesselPositionMessage));

		requestType.setAD("ad");
		requestType.setAR(true);
		requestType.setDF("df");
		requestType.setON("on");
		requestType.setTO(1234);

		GregorianCalendar calendar = new GregorianCalendar();
		calendar.setTime(new Date());
		requestType.setTODT(DatatypeFactory.newInstance().newXMLGregorianCalendar(calendar));

		ResponseType responseType = bridgeConnectorPortType.post(requestType);
		assertNotNull(responseType);
		assertEquals("OK", responseType.getStatus());
	}

	/**
	 * Creates the bridge connector.
	 *
	 * @return the bridge connector port type
	 */
	private BridgeConnectorPortType createBridgeConnector() {
		BridgeConnectorPortType bridgeConnectorPortType = new MovementService().getBridgeConnectorPortType();

		BindingProvider bp = (BindingProvider) bridgeConnectorPortType;
		bp.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
				"http://localhost:28080/flux-service-3.0.1/MovementService/FluxMessageReceiverBean");
		return bridgeConnectorPortType;
	}

	/**
	 * Creates the any element.
	 *
	 * @param fLUXVesselPositionMessage the f LUX vessel position message
	 * @return the element
	 * @throws JAXBException the JAXB exception
	 * @throws ParserConfigurationException the parser configuration exception
	 */
	private Element createAnyElement(FLUXVesselPositionMessage fLUXVesselPositionMessage)
			throws JAXBException, ParserConfigurationException {
		JAXBContext jaxbContext = JAXBContext.newInstance(FLUXVesselPositionMessage.class);
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document document = db.newDocument();
		Marshaller marshaller = jaxbContext.createMarshaller();
		marshaller.marshal(fLUXVesselPositionMessage, document);

		Element documentElement = document.getDocumentElement();
		return documentElement;
	}

}
