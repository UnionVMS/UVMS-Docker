/*
﻿Developed with the contribution of the European Commission - Directorate General for Maritime Affairs and Fisheries
© European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can
redistribute it and/or modify it under the terms of the GNU General Public License as published by the
Free Software Foundation, either version 3 of the License, or any later version. The IFDM Suite is distributed in
the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. You should have received a
copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.
 */
package eu.europa.ec.fisheries.uvms.docker.validation.system.helper;

import eu.europa.ec.fisheries.uvms.asset.client.model.AssetDTO;
import eu.europa.ec.fisheries.uvms.docker.validation.common.AbstractHelper;
import eu.europa.ec.fisheries.uvms.docker.validation.movement.LatLong;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import un.unece.uncefact.data.standard.fluxvesselpositionmessage._4.FLUXVesselPositionMessage;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._18.*;
import un.unece.uncefact.data.standard.unqualifieddatatype._18.*;
import xeu.bridge_connector.v1.RequestType;
import xeu.bridge_connector.v1.ResponseType;
import xeu.bridge_connector.wsdl.v1.BridgeConnectorPortType;
import xeu.bridge_connector.wsdl.v1.MovementService;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.ws.BindingProvider;
import java.math.BigDecimal;
import java.util.GregorianCalendar;

public class FLUXHelper extends AbstractHelper {

    public static void sendPositionToFluxPlugin(AssetDTO asset, LatLong position) throws Exception {
        RequestType vesselReport = createVesselReport(asset, position);
        sendVesselReportToFluxPlugin(vesselReport);
    }
    
    public static void sendVesselReportToFluxPlugin(RequestType requestType) {
        BridgeConnectorPortType bridgeConnectorPortType = createBridgeConnector();
        ResponseType responseType = bridgeConnectorPortType.post(requestType);
        assertNotNull(responseType);
        assertEquals("OK", responseType.getStatus());
    }

    private static RequestType createVesselReport(AssetDTO testAsset, LatLong latLong) throws DatatypeConfigurationException, JAXBException, ParserConfigurationException {
        FLUXVesselPositionMessage fluxVesselPositionMessage = createFluxMessage(testAsset, latLong);
        return createVesselReport(fluxVesselPositionMessage);
    }
    
    public static RequestType createVesselReport(FLUXVesselPositionMessage fluxVesselPositionMessage) throws DatatypeConfigurationException, JAXBException, ParserConfigurationException {
        GregorianCalendar calendar = new GregorianCalendar();
        
        RequestType requestType = new RequestType();
        requestType.setAny(createAnyElement(fluxVesselPositionMessage));
        requestType.setAD("SWE");
        requestType.setAR(true);
        requestType.setDF("df");
        requestType.setON("on");
        requestType.setTO(1234);
        requestType.getOtherAttributes().put(new QName("USER"), "FLUX");
        requestType.getOtherAttributes().put(new QName("FR"), "FLUX Test");
        requestType.setTODT(DatatypeFactory.newInstance().newXMLGregorianCalendar(calendar));
        
        return requestType;
    }
    
    public static FLUXVesselPositionMessage createFluxMessage(AssetDTO testAsset, LatLong latLong) throws DatatypeConfigurationException {
        FLUXVesselPositionMessage fluxVesselPositionMessage = new FLUXVesselPositionMessage();
        VesselTransportMeansType vesselTransportMeansType = new VesselTransportMeansType();
        
        IDType cfrId = new IDType();
        cfrId.setSchemeID("CFR");
        cfrId.setValue(testAsset.getCfr());
        vesselTransportMeansType.getIDS().add(cfrId);

        IDType ircsId = new IDType();
        ircsId.setSchemeID("IRCS");
        ircsId.setValue(testAsset.getIrcs());
        vesselTransportMeansType.getIDS().add(ircsId);

        IDType externalMarking = new IDType();
        externalMarking.setSchemeID("EXT_MARK");
        externalMarking.setValue(testAsset.getExternalMarking());
        vesselTransportMeansType.getIDS().add(externalMarking);

        VesselCountryType vesselCountry = new VesselCountryType();
        IDType countryId = new IDType();
        countryId.setValue(testAsset.getFlagStateCode());
        vesselCountry.setID(countryId);
        vesselTransportMeansType.setRegistrationVesselCountry(vesselCountry);

        VesselPositionEventType vesselPositionEventType = new VesselPositionEventType();
        
        MeasureType measureType = new MeasureType();
        measureType.setValue(new BigDecimal(latLong.bearing));
        vesselPositionEventType.setCourseValueMeasure(measureType);
        
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTime(latLong.positionTime);
        
        DateTimeType posDateTime = new DateTimeType();
        posDateTime.setDateTime(DatatypeFactory.newInstance().newXMLGregorianCalendar(calendar));
        vesselPositionEventType.setObtainedOccurrenceDateTime(posDateTime);
        
        VesselGeographicalCoordinateType cordinates = new VesselGeographicalCoordinateType();
        MeasureType longitude = new MeasureType();
        longitude.setValue(new BigDecimal(latLong.longitude));
        cordinates.setLongitudeMeasure(longitude);
        MeasureType latitude = new MeasureType();
        latitude.setValue(new BigDecimal(latLong.latitude));
        cordinates.setLatitudeMeasure(latitude);
        vesselPositionEventType.setSpecifiedVesselGeographicalCoordinate(cordinates);
        
        MeasureType speedValue = new MeasureType();
        speedValue.setValue(new BigDecimal(latLong.speed));
        vesselPositionEventType.setSpeedValueMeasure(speedValue);
        
        CodeType typeCodeValue = new CodeType();
        typeCodeValue.setValue("POS");
        vesselPositionEventType.setTypeCode(typeCodeValue);
        
        vesselTransportMeansType.getSpecifiedVesselPositionEvents().add(vesselPositionEventType);
        
        fluxVesselPositionMessage.setVesselTransportMeans(vesselTransportMeansType);
        
        FLUXReportDocumentType fluxReportDocumentType = new FLUXReportDocumentType();
        DateTimeType dateTimeType = new DateTimeType();
        dateTimeType.setDateTime(DatatypeFactory.newInstance().newXMLGregorianCalendar(calendar));
        fluxReportDocumentType.setCreationDateTime(dateTimeType);
        FLUXPartyType fLUXPartyType = new FLUXPartyType();
        fLUXPartyType.getIDS().add(countryId);
        fluxReportDocumentType.setOwnerFLUXParty(fLUXPartyType);
        TextType textType = new TextType();
        fluxReportDocumentType.setPurpose(textType);        
        CodeType purposeCode = new CodeType();
        purposeCode.setValue("9");
        fluxReportDocumentType.setPurposeCode(purposeCode);     
        IDType idType = new IDType();
        fluxReportDocumentType.setReferencedID(idType);
        fluxReportDocumentType.getIDS().add(idType);
        CodeType typeCode = new CodeType();
        fluxReportDocumentType.setTypeCode(typeCode);       
        fluxVesselPositionMessage.setFLUXReportDocument(fluxReportDocumentType);
        return fluxVesselPositionMessage;
    }
    
    private static Element createAnyElement(FLUXVesselPositionMessage fLUXVesselPositionMessage)
            throws JAXBException, ParserConfigurationException {
        JAXBContext jaxbContext = JAXBContext.newInstance(FLUXVesselPositionMessage.class);
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document document = db.newDocument();
        Marshaller marshaller = jaxbContext.createMarshaller();
        marshaller.marshal(fLUXVesselPositionMessage, document);
        return document.getDocumentElement();
    }
    
    private static BridgeConnectorPortType createBridgeConnector() {
        BridgeConnectorPortType bridgeConnectorPortType = new MovementService().getBridgeConnectorPortType();

        BindingProvider bp = (BindingProvider) bridgeConnectorPortType;
        bp.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
                getBaseUrl() + "movement-service/MovementPositionService/FluxMovementPositionReceiverBean");
        return bridgeConnectorPortType;
    }
}
