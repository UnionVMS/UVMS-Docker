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
package eu.europa.ec.fisheries.uvms.docker.validation.system.sales;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.UUID;
import javax.jms.TextMessage;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.ws.BindingProvider;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;
import eu.europa.ec.fisheries.schema.exchange.plugin.v1.SendSalesResponseRequest;
import eu.europa.ec.fisheries.uvms.docker.validation.common.AbstractRestServiceTest;
import eu.europa.ec.fisheries.uvms.docker.validation.common.MessageHelper;
import eu.europa.ec.fisheries.uvms.exchange.model.mapper.JAXBMarshaller;
import xeu.bridge_connector.v1.RequestType;
import xeu.bridge_connector.v1.VerbosityType;
import xeu.bridge_connector.wsdl.v1.BridgeConnectorPortType;
import xeu.bridge_connector.wsdl.v1.SalesService;

public class ReceiveSalesReportIT extends AbstractRestServiceTest {
    
    private static final String SELECTOR = "ServiceName='eu.europa.ec.fisheries.uvms.plugins.flux.sales'";
    private static final long TIMEOUT = 10000;

    @Test
    public void sendSalesReportTest() throws Exception {
        //send message
        RequestType request = createRequest();
        BridgeConnectorPortType bridgeConnectorPortType = createBridgeConnector();
        bridgeConnectorPortType.post(request);

        // listen for response
        TextMessage message = (TextMessage) MessageHelper.listenOnEventBus(SELECTOR, TIMEOUT);
        assertThat(message, is(notNullValue()));
        
//        System.out.println(message.getText());
        
        SendSalesResponseRequest salesResponse = JAXBMarshaller.unmarshallTextMessage(message, SendSalesResponseRequest.class);
        assertThat(salesResponse.getRecipient(), is("NLD"));
    }

    private RequestType createRequest() {
        RequestType request = new RequestType();
        request.setON("12345678901234567890");
        request.setDF("urn:un:unece:uncefact:fisheries:FLUX:SALES:EU:2");
        request.setAny(marshalToDOM(getBasicSalesReport()));
        request.setAD("BEL");
        request.setAR(false);
        request.setTO(200);
//        request.setTODT(new DateTime(2018, 1, 12, 13, 14));
        request.setVB(VerbosityType.ERROR);
        request.getOtherAttributes().put(new QName("FR"), "NLD");
        return request;
    }
    
    private static BridgeConnectorPortType createBridgeConnector() {
        BridgeConnectorPortType bridgeConnectorPortType = new SalesService().getBridgeConnectorPortType();

        BindingProvider bp = (BindingProvider) bridgeConnectorPortType;
        bp.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
                getBaseUrl() + "flux-sales-plugin/SalesService/FluxMessageReceiverBean");
        return bridgeConnectorPortType;
    }

    private Element marshalToDOM(String message) {
        try {
            InputStream xmlAsInputStream = new ByteArrayInputStream(message.getBytes("UTF-8"));

            javax.xml.parsers.DocumentBuilderFactory b = javax.xml.parsers.DocumentBuilderFactory.newInstance();
            b.setNamespaceAware(true);
            DocumentBuilder db = b.newDocumentBuilder();

            Document document = db.parse(xmlAsInputStream);
            return document.getDocumentElement();
        } catch (ParserConfigurationException | SAXException | IOException e) {
            throw new RuntimeException("Could not marshall message into an Element", e);
        }
    }

    private static final String getBasicSalesReport() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"); 
        Date timestamp = new Date(System.currentTimeMillis() - 24 * 60 * 60 * 1000);
        return
            "<FLUXSalesReportMessage xmlns:clm63155CommunicationChannelCode=\"urn:un:unece:uncefact:codelist:standard:UNECE:CommunicationMeansTypeCode:D16A\" xmlns:qdt=\"urn:un:unece:uncefact:data:Standard:QualifiedDataType:20\" xmlns:ram=\"urn:un:unece:uncefact:data:standard:ReusableAggregateBusinessInformationEntity:20\" xmlns:udt=\"urn:un:unece:uncefact:data:standard:UnqualifiedDataType:20\" xsi:schemaLocation=\"urn:un:unece:uncefact:data:standard:FLUXSalesReportMessage:3 FLUXSalesReportMessage_3p1.xsd\" xmlns=\"urn:un:unece:uncefact:data:standard:FLUXSalesReportMessage:3\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n" +
            "   <FLUXReportDocument>\n" +
            "       <ram:ID schemeID=\"UUID\">" + UUID.randomUUID().toString() + "</ram:ID>\n" +
            "       <ram:CreationDateTime>\n" +
            "           <udt:DateTime>" + format.format(timestamp) + "</udt:DateTime>\n" +
            "       </ram:CreationDateTime>\n" +
            "       <ram:PurposeCode listID=\"FLUX_GP_PURPOSE\">9</ram:PurposeCode>\n" +
            "       <ram:Purpose>Sales note example</ram:Purpose>\n" +
            "       <ram:OwnerFLUXParty>\n" +
            "           <ram:ID schemeID=\"FLUX_GP_PARTY\">NLD</ram:ID>\n" +
            "       </ram:OwnerFLUXParty>\n" +
            "   </FLUXReportDocument>\n" +
            "   <SalesReport>\n" + 
            "        <ram:ItemTypeCode listID=\"FLUX_SALES_TYPE\">SN</ram:ItemTypeCode>\n" + 
            "        <ram:IncludedSalesDocument>\n" + 
            "            <ram:ID schemeID=\"EU_SALES_ID\">NLD-SN-2018-" + getRandomIntegers(6) + "</ram:ID>\n" + 
            "            <ram:CurrencyCode listID=\"TERRITORY_CURR\">EUR</ram:CurrencyCode>\n" + 
            "            <ram:SpecifiedSalesBatch>\n" + 
            "                <ram:SpecifiedAAPProduct>\n" + 
            "                    <ram:SpeciesCode listID=\"FAO_SPECIES\">AAK</ram:SpeciesCode>\n" + 
            "                    <ram:WeightMeasure unitCode=\"KGM\">6616</ram:WeightMeasure>\n" + 
            "                    <ram:UsageCode listID=\"PROD_USAGE\">WST</ram:UsageCode>\n" + 
            "                    <ram:AppliedAAPProcess>\n" + 
            "                        <ram:TypeCode listID=\"FISH_FRESHNESS\">SO</ram:TypeCode>\n" + 
            "                        <ram:TypeCode listID=\"FISH_PRESERVATION\">FRO</ram:TypeCode>\n" + 
            "                        <ram:TypeCode listID=\"FISH_PRESENTATION\">SKI</ram:TypeCode>\n" + 
            "                    </ram:AppliedAAPProcess>\n" + 
            "                    <ram:TotalSalesPrice>\n" + 
            "                        <ram:ChargeAmount>3387.76</ram:ChargeAmount>\n" + 
            "                    </ram:TotalSalesPrice>\n" + 
            "                    <ram:SpecifiedSizeDistribution>\n" + 
            "                        <ram:CategoryCode listID=\"FISH_SIZE_CATEGORY\">2</ram:CategoryCode>\n" + 
            "                        <ram:ClassCode listID=\"FISH_SIZE_CLASS\">LSC</ram:ClassCode>\n" + 
            "                    </ram:SpecifiedSizeDistribution>\n" + 
            "                    <ram:OriginFLUXLocation>\n" + 
            "                        <ram:TypeCode listID=\"FLUX_LOCATION_TYPE\">AREA</ram:TypeCode>\n" + 
            "                        <ram:ID schemeID=\"FAO_AREA\">27.3.d.24</ram:ID>\n" + 
            "                    </ram:OriginFLUXLocation>\n" + 
            "                </ram:SpecifiedAAPProduct>\n" + 
            "                <ram:SpecifiedAAPProduct>\n" + 
            "                    <ram:SpeciesCode listID=\"FAO_SPECIES\">ABS</ram:SpeciesCode>\n" + 
            "                    <ram:WeightMeasure unitCode=\"KGM\">8168</ram:WeightMeasure>\n" + 
            "                    <ram:UsageCode listID=\"PROD_USAGE\">WST</ram:UsageCode>\n" + 
            "                    <ram:AppliedAAPProcess>\n" + 
            "                        <ram:TypeCode listID=\"FISH_FRESHNESS\">A</ram:TypeCode>\n" + 
            "                        <ram:TypeCode listID=\"FISH_PRESERVATION\">SMO</ram:TypeCode>\n" + 
            "                        <ram:TypeCode listID=\"FISH_PRESENTATION\">DWT</ram:TypeCode>\n" + 
            "                    </ram:AppliedAAPProcess>\n" + 
            "                    <ram:TotalSalesPrice>\n" + 
            "                        <ram:ChargeAmount>6726.38</ram:ChargeAmount>\n" + 
            "                    </ram:TotalSalesPrice>\n" + 
            "                    <ram:SpecifiedSizeDistribution>\n" + 
            "                        <ram:CategoryCode listID=\"FISH_SIZE_CATEGORY\">4b</ram:CategoryCode>\n" + 
            "                        <ram:ClassCode listID=\"FISH_SIZE_CLASS\">LSC</ram:ClassCode>\n" + 
            "                    </ram:SpecifiedSizeDistribution>\n" + 
            "                    <ram:OriginFLUXLocation>\n" + 
            "                        <ram:TypeCode listID=\"FLUX_LOCATION_TYPE\">AREA</ram:TypeCode>\n" + 
            "                        <ram:ID schemeID=\"FAO_AREA\">27.3.d.24</ram:ID>\n" + 
            "                    </ram:OriginFLUXLocation>\n" + 
            "                </ram:SpecifiedAAPProduct>\n" + 
            "                <ram:SpecifiedAAPProduct>\n" + 
            "                    <ram:SpeciesCode listID=\"FAO_SPECIES\">ACE</ram:SpeciesCode>\n" + 
            "                    <ram:WeightMeasure unitCode=\"KGM\">3028</ram:WeightMeasure>\n" + 
            "                    <ram:UsageCode listID=\"PROD_USAGE\">IND</ram:UsageCode>\n" + 
            "                    <ram:AppliedAAPProcess>\n" + 
            "                        <ram:TypeCode listID=\"FISH_FRESHNESS\">V</ram:TypeCode>\n" + 
            "                        <ram:TypeCode listID=\"FISH_PRESERVATION\">DRI</ram:TypeCode>\n" + 
            "                        <ram:TypeCode listID=\"FISH_PRESENTATION\">TNG</ram:TypeCode>\n" + 
            "                    </ram:AppliedAAPProcess>\n" + 
            "                    <ram:TotalSalesPrice>\n" + 
            "                        <ram:ChargeAmount>8525.89</ram:ChargeAmount>\n" + 
            "                    </ram:TotalSalesPrice>\n" + 
            "                    <ram:SpecifiedSizeDistribution>\n" + 
            "                        <ram:CategoryCode listID=\"FISH_SIZE_CATEGORY\">6</ram:CategoryCode>\n" + 
            "                        <ram:ClassCode listID=\"FISH_SIZE_CLASS\">LSC</ram:ClassCode>\n" + 
            "                    </ram:SpecifiedSizeDistribution>\n" + 
            "                    <ram:OriginFLUXLocation>\n" + 
            "                        <ram:TypeCode listID=\"FLUX_LOCATION_TYPE\">AREA</ram:TypeCode>\n" + 
            "                        <ram:ID schemeID=\"FAO_AREA\">27.3.d.24</ram:ID>\n" + 
            "                    </ram:OriginFLUXLocation>\n" + 
            "                </ram:SpecifiedAAPProduct>\n" + 
            "                <ram:SpecifiedAAPProduct>\n" + 
            "                    <ram:SpeciesCode listID=\"FAO_SPECIES\">ABB</ram:SpeciesCode>\n" + 
            "                    <ram:WeightMeasure unitCode=\"KGM\">2067</ram:WeightMeasure>\n" + 
            "                    <ram:UsageCode listID=\"PROD_USAGE\">HCN-INDIRECT</ram:UsageCode>\n" + 
            "                    <ram:AppliedAAPProcess>\n" + 
            "                        <ram:TypeCode listID=\"FISH_FRESHNESS\">E</ram:TypeCode>\n" + 
            "                        <ram:TypeCode listID=\"FISH_PRESERVATION\">FRE</ram:TypeCode>\n" + 
            "                        <ram:TypeCode listID=\"FISH_PRESENTATION\">GUH</ram:TypeCode>\n" + 
            "                    </ram:AppliedAAPProcess>\n" + 
            "                    <ram:TotalSalesPrice>\n" + 
            "                        <ram:ChargeAmount>6158.17</ram:ChargeAmount>\n" + 
            "                    </ram:TotalSalesPrice>\n" + 
            "                    <ram:SpecifiedSizeDistribution>\n" + 
            "                        <ram:CategoryCode listID=\"FISH_SIZE_CATEGORY\">N/A</ram:CategoryCode>\n" + 
            "                        <ram:ClassCode listID=\"FISH_SIZE_CLASS\">BMS</ram:ClassCode>\n" + 
            "                    </ram:SpecifiedSizeDistribution>\n" + 
            "                    <ram:OriginFLUXLocation>\n" + 
            "                        <ram:TypeCode listID=\"FLUX_LOCATION_TYPE\">AREA</ram:TypeCode>\n" + 
            "                        <ram:ID schemeID=\"FAO_AREA\">27.3.d.24</ram:ID>\n" + 
            "                    </ram:OriginFLUXLocation>\n" + 
            "                </ram:SpecifiedAAPProduct>\n" + 
            "            </ram:SpecifiedSalesBatch>\n" + 
            "            <ram:SpecifiedSalesEvent>\n" + 
            "                <ram:OccurrenceDateTime>\n" + 
            "                    <udt:DateTime>" + format.format(timestamp) + "</udt:DateTime>\n" + 
            "                </ram:OccurrenceDateTime>\n" + 
            "            </ram:SpecifiedSalesEvent>\n" + 
            "            <ram:SpecifiedFishingActivity>\n" + 
            "                <ram:TypeCode>LAN</ram:TypeCode>\n" + 
            "                <ram:RelatedFLUXLocation>\n" + 
            "                    <ram:TypeCode listID=\"FLUX_LOCATION_TYPE\">LOCATION</ram:TypeCode>\n" + 
            "                    <ram:CountryID schemeID=\"TERRITORY\">NLD</ram:CountryID>\n" + 
            "                    <ram:ID schemeID=\"LOCATION\">NLAMS</ram:ID>\n" + 
            "                </ram:RelatedFLUXLocation>\n" + 
            "                <ram:SpecifiedDelimitedPeriod>\n" + 
            "                    <ram:StartDateTime>\n" + 
            "                        <udt:DateTime>" + format.format(timestamp) + "</udt:DateTime>\n" + 
            "                    </ram:StartDateTime>\n" + 
            "                </ram:SpecifiedDelimitedPeriod>\n" + 
            "                <ram:SpecifiedFishingTrip>\n" + 
            "                    <ram:ID schemeID=\"EU_TRIP_ID\">NLD-TRP-2018-29861837</ram:ID>\n" + 
            "                </ram:SpecifiedFishingTrip>\n" + 
            "                <ram:RelatedVesselTransportMeans>\n" + 
            "                    <ram:ID schemeID=\"CFR\">NLD531861791</ram:ID>\n" + 
            "                    <ram:Name>Awesome</ram:Name>\n" + 
            "                    <ram:RegistrationVesselCountry>\n" + 
            "                        <ram:ID schemeID=\"TERRITORY\">NLD</ram:ID>\n" + 
            "                    </ram:RegistrationVesselCountry>\n" + 
            "                    <ram:SpecifiedContactParty>\n" + 
            "                        <ram:RoleCode listID=\"FLUX_CONTACT_ROLE\">MASTER</ram:RoleCode>\n" + 
            "                        <ram:SpecifiedContactPerson>\n" + 
            "                            <ram:GivenName>Mathias</ram:GivenName>\n" + 
            "                            <ram:MiddleName>Mathias</ram:MiddleName>\n" + 
            "                            <ram:FamilyName>Van Impe</ram:FamilyName>\n" + 
            "                        </ram:SpecifiedContactPerson>\n" + 
            "                    </ram:SpecifiedContactParty>\n" + 
            "                </ram:RelatedVesselTransportMeans>\n" + 
            "            </ram:SpecifiedFishingActivity>\n" + 
            "            <ram:SpecifiedFLUXLocation>\n" + 
            "                <ram:TypeCode listID=\"FLUX_LOCATION_TYPE\">LOCATION</ram:TypeCode>\n" + 
            "                <ram:CountryID schemeID=\"TERRITORY\">NLD</ram:CountryID>\n" + 
            "                <ram:ID schemeID=\"LOCATION\">NLAMS</ram:ID>\n" + 
            "            </ram:SpecifiedFLUXLocation>\n" + 
            "            <ram:SpecifiedSalesParty>\n" + 
            "                <ram:ID schemeID=\"MS\">4521845272</ram:ID>\n" + 
            "                <ram:Name>Bogaerts</ram:Name>\n" + 
            "                <ram:RoleCode listID=\"FLUX_SALES_PARTY_ROLE\">SENDER</ram:RoleCode>\n" + 
            "            </ram:SpecifiedSalesParty>\n" + 
            "            <ram:SpecifiedSalesParty>\n" + 
            "                <ram:ID schemeID=\"VAT\">6208439451</ram:ID>\n" + 
            "                <ram:Name>Schockaert</ram:Name>\n" + 
            "                <ram:RoleCode listID=\"FLUX_SALES_PARTY_ROLE\">BUYER</ram:RoleCode>\n" + 
            "            </ram:SpecifiedSalesParty>\n" + 
            "            <ram:SpecifiedSalesParty>\n" + 
            "                <ram:Name>Blaaaaaaaa</ram:Name>\n" + 
            "                <ram:RoleCode listID=\"FLUX_SALES_PARTY_ROLE\">PROVIDER</ram:RoleCode>\n" + 
            "            </ram:SpecifiedSalesParty>\n" + 
            "        </ram:IncludedSalesDocument>\n" + 
            "    </SalesReport>\n" +
            "</FLUXSalesReportMessage>\n";
    }
    
    private static String getRandomIntegers(int length) {
        return new Random()
                .ints(0,9)
                .mapToObj(i -> String.valueOf(i))
                .limit(length)
                .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append)
                .toString();
    }
}