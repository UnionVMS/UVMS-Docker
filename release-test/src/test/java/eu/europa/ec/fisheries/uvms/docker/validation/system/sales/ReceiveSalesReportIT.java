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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.ws.BindingProvider;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;
import eu.europa.ec.fisheries.uvms.docker.validation.common.AbstractRestServiceTest;
import xeu.bridge_connector.v1.RequestType;
import xeu.bridge_connector.v1.VerbosityType;
import xeu.bridge_connector.wsdl.v1.BridgeConnectorPortType;
import xeu.bridge_connector.wsdl.v1.SalesService;

public class ReceiveSalesReportIT extends AbstractRestServiceTest {

    @Test
    public void pluginReceivesAMessageAndSendsItExchangeSuccessfully() throws Exception {
        //send message
        RequestType request = createRequest();
        BridgeConnectorPortType bridgeConnectorPortType = createBridgeConnector();
        bridgeConnectorPortType.post(request);

        //assert message content
//        assertEquals(1, receivedMessagesInExchange.getAll().size());
//        String actualMessage = receivedMessagesInExchange.getAll().get(0);
//        assertTrue(actualMessage.startsWith(EXPECTED_MESSAGE_BEFORE_CURRENT_DATE));
//        assertTrue(actualMessage.endsWith(EXPECTED_MESSAGE_AFTER_CURRENT_DATE));
    }

    private RequestType createRequest() {
        RequestType request = new RequestType();
        request.setON("12345678901234567890");
        request.setDF("urn:un:unece:uncefact:fisheries:FLUX:SALES:EU:2");
        request.setAny(marshalToDOM(SALES_REPORT));
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

    private static final String EXPECTED_MESSAGE_BEFORE_CURRENT_DATE = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
            "<ns2:ReceiveSalesReportRequest xmlns:ns2=\"urn:module.exchange.schema.fisheries.ec.europa.eu:v1\">\n" +
            "    <method>RECEIVE_SALES_REPORT</method>\n" +
            "    <username>FLUX</username>\n" +
            "    <pluginType>FLUX</pluginType>\n" +
            "    <senderOrReceiver>NLD</senderOrReceiver>\n" +
            "    <messageGuid>23b098af-e300-4369-bc7a-9b207305efdd</messageGuid>\n" +
            "    <date>";

    private static final String EXPECTED_MESSAGE_AFTER_CURRENT_DATE = "</date>\n" +
            "    <onValue>12345678901234567890</onValue>\n" +
            "    <report>&lt;?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?&gt;\n" +
            "&lt;ns4:Report xmlns=\"urn:un:unece:uncefact:data:standard:ReusableAggregateBusinessInformationEntity:20\" xmlns:ns2=\"urn:un:unece:uncefact:data:standard:UnqualifiedDataType:20\" xmlns:ns4=\"eu.europa.ec.fisheries.schema.sales\" xmlns:ns3=\"eu.europa.ec.fisheries.schema.sales.flux\"&gt;\n" +
            "    &lt;ns4:FLUXSalesReportMessage&gt;\n" +
            "        &lt;ns3:FLUXReportDocument&gt;\n" +
            "            &lt;ID schemeID=\"UUID\"&gt;23b098af-e300-4369-bc7a-9b207305efdd&lt;/ID&gt;\n" +
            "            &lt;CreationDateTime&gt;\n" +
            "                &lt;ns2:DateTime&gt;2018-04-19T10:08:08.391Z&lt;/ns2:DateTime&gt;\n" +
            "            &lt;/CreationDateTime&gt;\n" +
            "            &lt;PurposeCode listID=\"FLUX_GP_PURPOSE\"&gt;9&lt;/PurposeCode&gt;\n" +
            "            &lt;Purpose&gt;Sales note example&lt;/Purpose&gt;\n" +
            "            &lt;OwnerFLUXParty&gt;\n" +
            "                &lt;ID schemeID=\"FLUX_GP_PARTY\"&gt;FRA&lt;/ID&gt;\n" +
            "            &lt;/OwnerFLUXParty&gt;\n" +
            "        &lt;/ns3:FLUXReportDocument&gt;\n" +
            "        &lt;ns3:SalesReport&gt;\n" +
            "            &lt;ItemTypeCode listID=\"FLUX_SALES_TYPE\"&gt;SN&lt;/ItemTypeCode&gt;\n" +
            "            &lt;IncludedSalesDocument&gt;\n" +
            "                &lt;ID schemeID=\"EU_SALES_ID\"&gt;FRA-SN-23b098af-e300-4369-bc7a-9b207305efdd&lt;/ID&gt;\n" +
            "                &lt;CurrencyCode listID=\"TERRITORY_CURR\"&gt;EUR&lt;/CurrencyCode&gt;\n" +
            "                &lt;SpecifiedSalesBatch&gt;\n" +
            "                    &lt;SpecifiedAAPProduct&gt;\n" +
            "                        &lt;SpeciesCode listID=\"FAO_SPECIES\"&gt;PLE&lt;/SpeciesCode&gt;\n" +
            "                        &lt;WeightMeasure unitCode=\"KGM\"&gt;6&lt;/WeightMeasure&gt;\n" +
            "                        &lt;UsageCode listID=\"PROD_USAGE\"&gt;HCN&lt;/UsageCode&gt;\n" +
            "                        &lt;AppliedAAPProcess&gt;\n" +
            "                            &lt;TypeCode listID=\"FISH_FRESHNESS\"&gt;E&lt;/TypeCode&gt;\n" +
            "                            &lt;TypeCode listID=\"FISH_PRESERVATION\"&gt;FRE&lt;/TypeCode&gt;\n" +
            "                            &lt;TypeCode listID=\"FISH_PRESENTATION\"&gt;WHL&lt;/TypeCode&gt;\n" +
            "                        &lt;/AppliedAAPProcess&gt;\n" +
            "                        &lt;TotalSalesPrice&gt;\n" +
            "                            &lt;ChargeAmount&gt;1.31&lt;/ChargeAmount&gt;\n" +
            "                        &lt;/TotalSalesPrice&gt;\n" +
            "                        &lt;SpecifiedSizeDistribution&gt;\n" +
            "                            &lt;CategoryCode listID=\"FISH_SIZE_CATEGORY\"&gt;1&lt;/CategoryCode&gt;\n" +
            "                            &lt;ClassCode listID=\"FISH_SIZE_CLASS\"&gt;LSC&lt;/ClassCode&gt;\n" +
            "                        &lt;/SpecifiedSizeDistribution&gt;\n" +
            "                        &lt;OriginFLUXLocation&gt;\n" +
            "                            &lt;TypeCode listID=\"FLUX_LOCATION_TYPE\"&gt;AREA&lt;/TypeCode&gt;\n" +
            "                            &lt;ID schemeID=\"FAO_AREA\"&gt;27.3.d.24&lt;/ID&gt;\n" +
            "                        &lt;/OriginFLUXLocation&gt;\n" +
            "                    &lt;/SpecifiedAAPProduct&gt;\n" +
            "                    &lt;SpecifiedAAPProduct&gt;\n" +
            "                        &lt;SpeciesCode listID=\"FAO_SPECIES\"&gt;PLE&lt;/SpeciesCode&gt;\n" +
            "                        &lt;WeightMeasure unitCode=\"KGM\"&gt;36&lt;/WeightMeasure&gt;\n" +
            "                        &lt;UsageCode listID=\"PROD_USAGE\"&gt;HCN&lt;/UsageCode&gt;\n" +
            "                        &lt;AppliedAAPProcess&gt;\n" +
            "                            &lt;TypeCode listID=\"FISH_FRESHNESS\"&gt;B&lt;/TypeCode&gt;\n" +
            "                            &lt;TypeCode listID=\"FISH_PRESERVATION\"&gt;FRE&lt;/TypeCode&gt;\n" +
            "                            &lt;TypeCode listID=\"FISH_PRESENTATION\"&gt;WHL&lt;/TypeCode&gt;\n" +
            "                        &lt;/AppliedAAPProcess&gt;\n" +
            "                        &lt;TotalSalesPrice&gt;\n" +
            "                            &lt;ChargeAmount&gt;1.29&lt;/ChargeAmount&gt;\n" +
            "                        &lt;/TotalSalesPrice&gt;\n" +
            "                        &lt;SpecifiedSizeDistribution&gt;\n" +
            "                            &lt;CategoryCode listID=\"FISH_SIZE_CATEGORY\"&gt;2&lt;/CategoryCode&gt;\n" +
            "                            &lt;ClassCode listID=\"FISH_SIZE_CLASS\"&gt;LSC&lt;/ClassCode&gt;\n" +
            "                        &lt;/SpecifiedSizeDistribution&gt;\n" +
            "                        &lt;OriginFLUXLocation&gt;\n" +
            "                            &lt;TypeCode listID=\"FLUX_LOCATION_TYPE\"&gt;AREA&lt;/TypeCode&gt;\n" +
            "                            &lt;ID schemeID=\"FAO_AREA\"&gt;27.3.d.24&lt;/ID&gt;\n" +
            "                        &lt;/OriginFLUXLocation&gt;\n" +
            "                    &lt;/SpecifiedAAPProduct&gt;\n" +
            "                    &lt;SpecifiedAAPProduct&gt;\n" +
            "                        &lt;SpeciesCode listID=\"FAO_SPECIES\"&gt;DAB&lt;/SpeciesCode&gt;\n" +
            "                        &lt;WeightMeasure unitCode=\"KGM\"&gt;517&lt;/WeightMeasure&gt;\n" +
            "                        &lt;UsageCode listID=\"PROD_USAGE\"&gt;HCN&lt;/UsageCode&gt;\n" +
            "                        &lt;AppliedAAPProcess&gt;\n" +
            "                            &lt;TypeCode listID=\"FISH_FRESHNESS\"&gt;B&lt;/TypeCode&gt;\n" +
            "                            &lt;TypeCode listID=\"FISH_PRESERVATION\"&gt;FRE&lt;/TypeCode&gt;\n" +
            "                            &lt;TypeCode listID=\"FISH_PRESENTATION\"&gt;WHL&lt;/TypeCode&gt;\n" +
            "                        &lt;/AppliedAAPProcess&gt;\n" +
            "                        &lt;TotalSalesPrice&gt;\n" +
            "                            &lt;ChargeAmount&gt;1.12&lt;/ChargeAmount&gt;\n" +
            "                        &lt;/TotalSalesPrice&gt;\n" +
            "                        &lt;SpecifiedSizeDistribution&gt;\n" +
            "                            &lt;CategoryCode listID=\"FISH_SIZE_CATEGORY\"&gt;2&lt;/CategoryCode&gt;\n" +
            "                            &lt;ClassCode listID=\"FISH_SIZE_CLASS\"&gt;LSC&lt;/ClassCode&gt;\n" +
            "                        &lt;/SpecifiedSizeDistribution&gt;\n" +
            "                        &lt;OriginFLUXLocation&gt;\n" +
            "                            &lt;TypeCode listID=\"FLUX_LOCATION_TYPE\"&gt;AREA&lt;/TypeCode&gt;\n" +
            "                            &lt;ID schemeID=\"FAO_AREA\"&gt;27.3.d.24&lt;/ID&gt;\n" +
            "                        &lt;/OriginFLUXLocation&gt;\n" +
            "                    &lt;/SpecifiedAAPProduct&gt;\n" +
            "                    &lt;SpecifiedAAPProduct&gt;\n" +
            "                        &lt;SpeciesCode listID=\"FAO_SPECIES\"&gt;COD&lt;/SpeciesCode&gt;\n" +
            "                        &lt;WeightMeasure unitCode=\"KGM\"&gt;13&lt;/WeightMeasure&gt;\n" +
            "                        &lt;UsageCode listID=\"PROD_USAGE\"&gt;HCN&lt;/UsageCode&gt;\n" +
            "                        &lt;AppliedAAPProcess&gt;\n" +
            "                            &lt;TypeCode listID=\"FISH_FRESHNESS\"&gt;B&lt;/TypeCode&gt;\n" +
            "                            &lt;TypeCode listID=\"FISH_PRESERVATION\"&gt;FRE&lt;/TypeCode&gt;\n" +
            "                            &lt;TypeCode listID=\"FISH_PRESENTATION\"&gt;GUT&lt;/TypeCode&gt;\n" +
            "                        &lt;/AppliedAAPProcess&gt;\n" +
            "                        &lt;TotalSalesPrice&gt;\n" +
            "                            &lt;ChargeAmount&gt;2&lt;/ChargeAmount&gt;\n" +
            "                        &lt;/TotalSalesPrice&gt;\n" +
            "                        &lt;SpecifiedSizeDistribution&gt;\n" +
            "                            &lt;CategoryCode listID=\"FISH_SIZE_CATEGORY\"&gt;3&lt;/CategoryCode&gt;\n" +
            "                            &lt;ClassCode listID=\"FISH_SIZE_CLASS\"&gt;LSC&lt;/ClassCode&gt;\n" +
            "                        &lt;/SpecifiedSizeDistribution&gt;\n" +
            "                        &lt;OriginFLUXLocation&gt;\n" +
            "                            &lt;TypeCode listID=\"FLUX_LOCATION_TYPE\"&gt;AREA&lt;/TypeCode&gt;\n" +
            "                            &lt;ID schemeID=\"FAO_AREA\"&gt;27.3.d.24&lt;/ID&gt;\n" +
            "                        &lt;/OriginFLUXLocation&gt;\n" +
            "                    &lt;/SpecifiedAAPProduct&gt;\n" +
            "                    &lt;SpecifiedAAPProduct&gt;\n" +
            "                        &lt;SpeciesCode listID=\"FAO_SPECIES\"&gt;FLE&lt;/SpeciesCode&gt;\n" +
            "                        &lt;WeightMeasure unitCode=\"KGM\"&gt;102&lt;/WeightMeasure&gt;\n" +
            "                        &lt;UsageCode listID=\"PROD_USAGE\"&gt;HCN&lt;/UsageCode&gt;\n" +
            "                        &lt;AppliedAAPProcess&gt;\n" +
            "                            &lt;TypeCode listID=\"FISH_FRESHNESS\"&gt;B&lt;/TypeCode&gt;\n" +
            "                            &lt;TypeCode listID=\"FISH_PRESERVATION\"&gt;FRE&lt;/TypeCode&gt;\n" +
            "                            &lt;TypeCode listID=\"FISH_PRESENTATION\"&gt;WHL&lt;/TypeCode&gt;\n" +
            "                        &lt;/AppliedAAPProcess&gt;\n" +
            "                        &lt;TotalSalesPrice&gt;\n" +
            "                            &lt;ChargeAmount&gt;0.82&lt;/ChargeAmount&gt;\n" +
            "                        &lt;/TotalSalesPrice&gt;\n" +
            "                        &lt;SpecifiedSizeDistribution&gt;\n" +
            "                            &lt;CategoryCode listID=\"FISH_SIZE_CATEGORY\"&gt;2&lt;/CategoryCode&gt;\n" +
            "                            &lt;ClassCode listID=\"FISH_SIZE_CLASS\"&gt;LSC&lt;/ClassCode&gt;\n" +
            "                        &lt;/SpecifiedSizeDistribution&gt;\n" +
            "                        &lt;OriginFLUXLocation&gt;\n" +
            "                            &lt;TypeCode listID=\"FLUX_LOCATION_TYPE\"&gt;AREA&lt;/TypeCode&gt;\n" +
            "                            &lt;ID schemeID=\"FAO_AREA\"&gt;27.3.d.24&lt;/ID&gt;\n" +
            "                        &lt;/OriginFLUXLocation&gt;\n" +
            "                    &lt;/SpecifiedAAPProduct&gt;\n" +
            "                    &lt;SpecifiedAAPProduct&gt;\n" +
            "                        &lt;SpeciesCode listID=\"FAO_SPECIES\"&gt;LIN&lt;/SpeciesCode&gt;\n" +
            "                        &lt;WeightMeasure unitCode=\"KGM\"&gt;9&lt;/WeightMeasure&gt;\n" +
            "                        &lt;UsageCode listID=\"PROD_USAGE\"&gt;HCN&lt;/UsageCode&gt;\n" +
            "                        &lt;AppliedAAPProcess&gt;\n" +
            "                            &lt;TypeCode listID=\"FISH_FRESHNESS\"&gt;B&lt;/TypeCode&gt;\n" +
            "                            &lt;TypeCode listID=\"FISH_PRESERVATION\"&gt;FRE&lt;/TypeCode&gt;\n" +
            "                            &lt;TypeCode listID=\"FISH_PRESENTATION\"&gt;GUT&lt;/TypeCode&gt;\n" +
            "                        &lt;/AppliedAAPProcess&gt;\n" +
            "                        &lt;TotalSalesPrice&gt;\n" +
            "                            &lt;ChargeAmount&gt;3.55&lt;/ChargeAmount&gt;\n" +
            "                        &lt;/TotalSalesPrice&gt;\n" +
            "                        &lt;SpecifiedSizeDistribution&gt;\n" +
            "                            &lt;CategoryCode listID=\"FISH_SIZE_CATEGORY\"&gt;3&lt;/CategoryCode&gt;\n" +
            "                            &lt;ClassCode listID=\"FISH_SIZE_CLASS\"&gt;LSC&lt;/ClassCode&gt;\n" +
            "                        &lt;/SpecifiedSizeDistribution&gt;\n" +
            "                        &lt;OriginFLUXLocation&gt;\n" +
            "                            &lt;TypeCode listID=\"FLUX_LOCATION_TYPE\"&gt;AREA&lt;/TypeCode&gt;\n" +
            "                            &lt;ID schemeID=\"FAO_AREA\"&gt;27.7.a&lt;/ID&gt;\n" +
            "                        &lt;/OriginFLUXLocation&gt;\n" +
            "                    &lt;/SpecifiedAAPProduct&gt;\n" +
            "                &lt;/SpecifiedSalesBatch&gt;\n" +
            "                &lt;SpecifiedSalesEvent&gt;\n" +
            "                    &lt;OccurrenceDateTime&gt;\n" +
            "                        &lt;ns2:DateTime&gt;2018-04-19T05:09:08.391Z&lt;/ns2:DateTime&gt;\n" +
            "                    &lt;/OccurrenceDateTime&gt;\n" +
            "                &lt;/SpecifiedSalesEvent&gt;\n" +
            "                &lt;SpecifiedFishingActivity&gt;\n" +
            "                    &lt;TypeCode&gt;LAN&lt;/TypeCode&gt;\n" +
            "                    &lt;RelatedFLUXLocation&gt;\n" +
            "                        &lt;TypeCode listID=\"FLUX_LOCATION_TYPE\"&gt;LOCATION&lt;/TypeCode&gt;\n" +
            "                        &lt;CountryID schemeID=\"TERRITORY\"&gt;BEL&lt;/CountryID&gt;\n" +
            "                        &lt;ID schemeID=\"LOCATION\"&gt;BEOST&lt;/ID&gt;\n" +
            "                    &lt;/RelatedFLUXLocation&gt;\n" +
            "                    &lt;SpecifiedDelimitedPeriod&gt;\n" +
            "                        &lt;StartDateTime&gt;\n" +
            "                            &lt;ns2:DateTime&gt;2018-04-19T10:02:08.391Z&lt;/ns2:DateTime&gt;\n" +
            "                        &lt;/StartDateTime&gt;\n" +
            "                    &lt;/SpecifiedDelimitedPeriod&gt;\n" +
            "                    &lt;SpecifiedFishingTrip&gt;\n" +
            "                        &lt;ID schemeID=\"EU_TRIP_ID\"&gt;BEL-TRP-2018-1111-1111&lt;/ID&gt;\n" +
            "                    &lt;/SpecifiedFishingTrip&gt;\n" +
            "                    &lt;RelatedVesselTransportMeans&gt;\n" +
            "                        &lt;ID schemeID=\"CFR\"&gt;BEL123456789&lt;/ID&gt;\n" +
            "                        &lt;Name&gt;FAKE VESSEL&lt;/Name&gt;\n" +
            "                        &lt;RegistrationVesselCountry&gt;\n" +
            "                            &lt;ID schemeID=\"TERRITORY\"&gt;FRA&lt;/ID&gt;\n" +
            "                        &lt;/RegistrationVesselCountry&gt;\n" +
            "                        &lt;SpecifiedContactParty&gt;\n" +
            "                            &lt;RoleCode listID=\"FLUX_CONTACT_ROLE\"&gt;MASTER&lt;/RoleCode&gt;\n" +
            "                            &lt;SpecifiedContactPerson&gt;\n" +
            "                                &lt;GivenName&gt;Henrick&lt;/GivenName&gt;\n" +
            "                                &lt;MiddleName&gt;Jan&lt;/MiddleName&gt;\n" +
            "                                &lt;FamilyName&gt;JANSEN&lt;/FamilyName&gt;\n" +
            "                            &lt;/SpecifiedContactPerson&gt;\n" +
            "                        &lt;/SpecifiedContactParty&gt;\n" +
            "                    &lt;/RelatedVesselTransportMeans&gt;\n" +
            "                &lt;/SpecifiedFishingActivity&gt;\n" +
            "                &lt;SpecifiedFLUXLocation&gt;\n" +
            "                    &lt;TypeCode listID=\"FLUX_LOCATION_TYPE\"&gt;LOCATION&lt;/TypeCode&gt;\n" +
            "                    &lt;CountryID schemeID=\"TERRITORY\"&gt;BEL&lt;/CountryID&gt;\n" +
            "                    &lt;ID schemeID=\"LOCATION\"&gt;BEOST&lt;/ID&gt;\n" +
            "                &lt;/SpecifiedFLUXLocation&gt;\n" +
            "                &lt;SpecifiedSalesParty&gt;\n" +
            "                    &lt;ID schemeID=\"MS\"&gt;123456&lt;/ID&gt;\n" +
            "                    &lt;Name&gt;Mr SENDER&lt;/Name&gt;\n" +
            "                    &lt;RoleCode listID=\"FLUX_SALES_PARTY_ROLE\"&gt;SENDER&lt;/RoleCode&gt;\n" +
            "                &lt;/SpecifiedSalesParty&gt;\n" +
            "                &lt;SpecifiedSalesParty&gt;\n" +
            "                    &lt;ID schemeID=\"VAT\"&gt;1234567890&lt;/ID&gt;\n" +
            "                    &lt;Name&gt;Mr BUYER&lt;/Name&gt;\n" +
            "                    &lt;RoleCode listID=\"FLUX_SALES_PARTY_ROLE\"&gt;BUYER&lt;/RoleCode&gt;\n" +
            "                &lt;/SpecifiedSalesParty&gt;\n" +
            "                &lt;SpecifiedSalesParty&gt;\n" +
            "                    &lt;Name&gt;Mr PROVIDER&lt;/Name&gt;\n" +
            "                    &lt;RoleCode listID=\"FLUX_SALES_PARTY_ROLE\"&gt;PROVIDER&lt;/RoleCode&gt;\n" +
            "                &lt;/SpecifiedSalesParty&gt;\n" +
            "            &lt;/IncludedSalesDocument&gt;\n" +
            "        &lt;/ns3:SalesReport&gt;\n" +
            "    &lt;/ns4:FLUXSalesReportMessage&gt;\n" +
            "&lt;/ns4:Report&gt;\n" +
            "</report>\n" +
            "</ns2:ReceiveSalesReportRequest>\n";

    private static final String SALES_REPORT = "<FLUXSalesReportMessage xmlns:clm63155CommunicationChannelCode=\"urn:un:unece:uncefact:codelist:standard:UNECE:CommunicationMeansTypeCode:D16A\" xmlns:qdt=\"urn:un:unece:uncefact:data:Standard:QualifiedDataType:20\" xmlns:ram=\"urn:un:unece:uncefact:data:standard:ReusableAggregateBusinessInformationEntity:20\" xmlns:udt=\"urn:un:unece:uncefact:data:standard:UnqualifiedDataType:20\" xsi:schemaLocation=\"urn:un:unece:uncefact:data:standard:FLUXSalesReportMessage:3 FLUXSalesReportMessage_3p1.xsd\" xmlns=\"urn:un:unece:uncefact:data:standard:FLUXSalesReportMessage:3\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n" +
            "\t<FLUXReportDocument>\n" +
            "\t\t<ram:ID schemeID=\"UUID\">23b098af-e300-4369-bc7a-9b207305efdd</ram:ID>\n" +
            "\t\t<ram:CreationDateTime>\n" +
            "\t\t\t<udt:DateTime>2018-04-19T10:08:08.391Z</udt:DateTime>\n" +
            "\t\t</ram:CreationDateTime>\n" +
            "\t\t<ram:PurposeCode listID=\"FLUX_GP_PURPOSE\">9</ram:PurposeCode>\n" +
            "\t\t<ram:Purpose>Sales note example</ram:Purpose>\n" +
            "\t\t<ram:OwnerFLUXParty>\n" +
            "\t\t\t<ram:ID schemeID=\"FLUX_GP_PARTY\">FRA</ram:ID>\n" +
            "\t\t</ram:OwnerFLUXParty>\n" +
            "\t</FLUXReportDocument>\n" +
            "\t<SalesReport>\n" +
            "\t\t<ram:ItemTypeCode listID=\"FLUX_SALES_TYPE\">SN</ram:ItemTypeCode>\n" +
            "\t\t<ram:IncludedSalesDocument>\n" +
            "\t\t\t<ram:ID schemeID=\"EU_SALES_ID\">FRA-SN-23b098af-e300-4369-bc7a-9b207305efdd</ram:ID>\n" +
            "\t\t\t<ram:CurrencyCode listID=\"TERRITORY_CURR\">EUR</ram:CurrencyCode>\n" +
            "\t\t\t<ram:SpecifiedSalesBatch>\n" +
            "\t\t\t\t<ram:SpecifiedAAPProduct>\n" +
            "\t\t\t\t\t<ram:SpeciesCode listID=\"FAO_SPECIES\">PLE</ram:SpeciesCode>\n" +
            "\t\t\t\t\t<ram:WeightMeasure unitCode=\"KGM\">6</ram:WeightMeasure>\n" +
            "\t\t\t\t\t<ram:UsageCode listID=\"PROD_USAGE\">HCN</ram:UsageCode>\n" +
            "\t\t\t\t\t<ram:AppliedAAPProcess>\n" +
            "\t\t\t\t\t\t<ram:TypeCode listID=\"FISH_FRESHNESS\">E</ram:TypeCode>\n" +
            "\t\t\t\t\t\t<ram:TypeCode listID=\"FISH_PRESERVATION\">FRE</ram:TypeCode>\n" +
            "\t\t\t\t\t\t<ram:TypeCode listID=\"FISH_PRESENTATION\">WHL</ram:TypeCode>\n" +
            "\t\t\t\t\t</ram:AppliedAAPProcess>\n" +
            "\t\t\t\t\t<ram:TotalSalesPrice>\n" +
            "\t\t\t\t\t\t<ram:ChargeAmount>1.31</ram:ChargeAmount>\n" +
            "\t\t\t\t\t</ram:TotalSalesPrice>\n" +
            "\t\t\t\t\t<ram:SpecifiedSizeDistribution>\n" +
            "\t\t\t\t\t\t<ram:CategoryCode listID=\"FISH_SIZE_CATEGORY\">1</ram:CategoryCode>\n" +
            "\t\t\t\t\t\t<ram:ClassCode listID=\"FISH_SIZE_CLASS\">LSC</ram:ClassCode>\n" +
            "\t\t\t\t\t</ram:SpecifiedSizeDistribution>\n" +
            "\t\t\t\t\t<ram:OriginFLUXLocation>\n" +
            "\t\t\t\t\t\t<ram:TypeCode listID=\"FLUX_LOCATION_TYPE\">AREA</ram:TypeCode>\n" +
            "\t\t\t\t\t\t<ram:ID schemeID=\"FAO_AREA\">27.3.d.24</ram:ID>\n" +
            "\t\t\t\t\t</ram:OriginFLUXLocation>\n" +
            "\t\t\t\t</ram:SpecifiedAAPProduct>\n" +
            "\t\t\t\t<ram:SpecifiedAAPProduct>\n" +
            "\t\t\t\t\t<ram:SpeciesCode listID=\"FAO_SPECIES\">PLE</ram:SpeciesCode>\n" +
            "\t\t\t\t\t<ram:WeightMeasure unitCode=\"KGM\">36</ram:WeightMeasure>\n" +
            "\t\t\t\t\t<ram:UsageCode listID=\"PROD_USAGE\">HCN</ram:UsageCode>\n" +
            "\t\t\t\t\t<ram:AppliedAAPProcess>\n" +
            "\t\t\t\t\t\t<ram:TypeCode listID=\"FISH_FRESHNESS\">B</ram:TypeCode>\n" +
            "\t\t\t\t\t\t<ram:TypeCode listID=\"FISH_PRESERVATION\">FRE</ram:TypeCode>\n" +
            "\t\t\t\t\t\t<ram:TypeCode listID=\"FISH_PRESENTATION\">WHL</ram:TypeCode>\n" +
            "\t\t\t\t\t</ram:AppliedAAPProcess>\n" +
            "\t\t\t\t\t<ram:TotalSalesPrice>\n" +
            "\t\t\t\t\t\t<ram:ChargeAmount>1.29</ram:ChargeAmount>\n" +
            "\t\t\t\t\t</ram:TotalSalesPrice>\n" +
            "\t\t\t\t\t<ram:SpecifiedSizeDistribution>\n" +
            "\t\t\t\t\t\t<ram:CategoryCode listID=\"FISH_SIZE_CATEGORY\">2</ram:CategoryCode>\n" +
            "\t\t\t\t\t\t<ram:ClassCode listID=\"FISH_SIZE_CLASS\">LSC</ram:ClassCode>\n" +
            "\t\t\t\t\t</ram:SpecifiedSizeDistribution>\n" +
            "\t\t\t\t\t<ram:OriginFLUXLocation>\n" +
            "\t\t\t\t\t\t<ram:TypeCode listID=\"FLUX_LOCATION_TYPE\">AREA</ram:TypeCode>\n" +
            "\t\t\t\t\t\t<ram:ID schemeID=\"FAO_AREA\">27.3.d.24</ram:ID>\n" +
            "\t\t\t\t\t</ram:OriginFLUXLocation>\n" +
            "\t\t\t\t</ram:SpecifiedAAPProduct>\n" +
            "\t\t\t\t<ram:SpecifiedAAPProduct>\n" +
            "\t\t\t\t\t<ram:SpeciesCode listID=\"FAO_SPECIES\">DAB</ram:SpeciesCode>\n" +
            "\t\t\t\t\t<ram:WeightMeasure unitCode=\"KGM\">517</ram:WeightMeasure>\n" +
            "\t\t\t\t\t<ram:UsageCode listID=\"PROD_USAGE\">HCN</ram:UsageCode>\n" +
            "\t\t\t\t\t<ram:AppliedAAPProcess>\n" +
            "\t\t\t\t\t\t<ram:TypeCode listID=\"FISH_FRESHNESS\">B</ram:TypeCode>\n" +
            "\t\t\t\t\t\t<ram:TypeCode listID=\"FISH_PRESERVATION\">FRE</ram:TypeCode>\n" +
            "\t\t\t\t\t\t<ram:TypeCode listID=\"FISH_PRESENTATION\">WHL</ram:TypeCode>\n" +
            "\t\t\t\t\t</ram:AppliedAAPProcess>\n" +
            "\t\t\t\t\t<ram:TotalSalesPrice>\n" +
            "\t\t\t\t\t\t<ram:ChargeAmount>1.12</ram:ChargeAmount>\n" +
            "\t\t\t\t\t</ram:TotalSalesPrice>\n" +
            "\t\t\t\t\t<ram:SpecifiedSizeDistribution>\n" +
            "\t\t\t\t\t\t<ram:CategoryCode listID=\"FISH_SIZE_CATEGORY\">2</ram:CategoryCode>\n" +
            "\t\t\t\t\t\t<ram:ClassCode listID=\"FISH_SIZE_CLASS\">LSC</ram:ClassCode>\n" +
            "\t\t\t\t\t</ram:SpecifiedSizeDistribution>\n" +
            "\t\t\t\t\t<ram:OriginFLUXLocation>\n" +
            "\t\t\t\t\t\t<ram:TypeCode listID=\"FLUX_LOCATION_TYPE\">AREA</ram:TypeCode>\n" +
            "\t\t\t\t\t\t<ram:ID schemeID=\"FAO_AREA\">27.3.d.24</ram:ID>\n" +
            "\t\t\t\t\t</ram:OriginFLUXLocation>\n" +
            "\t\t\t\t</ram:SpecifiedAAPProduct>\n" +
            "\t\t\t\t<ram:SpecifiedAAPProduct>\n" +
            "\t\t\t\t\t<ram:SpeciesCode listID=\"FAO_SPECIES\">COD</ram:SpeciesCode>\n" +
            "\t\t\t\t\t<ram:WeightMeasure unitCode=\"KGM\">13</ram:WeightMeasure>\n" +
            "\t\t\t\t\t<ram:UsageCode listID=\"PROD_USAGE\">HCN</ram:UsageCode>\n" +
            "\t\t\t\t\t<ram:AppliedAAPProcess>\n" +
            "\t\t\t\t\t\t<ram:TypeCode listID=\"FISH_FRESHNESS\">B</ram:TypeCode>\n" +
            "\t\t\t\t\t\t<ram:TypeCode listID=\"FISH_PRESERVATION\">FRE</ram:TypeCode>\n" +
            "\t\t\t\t\t\t<ram:TypeCode listID=\"FISH_PRESENTATION\">GUT</ram:TypeCode>\n" +
            "\t\t\t\t\t</ram:AppliedAAPProcess>\n" +
            "\t\t\t\t\t<ram:TotalSalesPrice>\n" +
            "\t\t\t\t\t\t<ram:ChargeAmount>2</ram:ChargeAmount>\n" +
            "\t\t\t\t\t</ram:TotalSalesPrice>\n" +
            "\t\t\t\t\t<ram:SpecifiedSizeDistribution>\n" +
            "\t\t\t\t\t\t<ram:CategoryCode listID=\"FISH_SIZE_CATEGORY\">3</ram:CategoryCode>\n" +
            "\t\t\t\t\t\t<ram:ClassCode listID=\"FISH_SIZE_CLASS\">LSC</ram:ClassCode>\n" +
            "\t\t\t\t\t</ram:SpecifiedSizeDistribution>\n" +
            "\t\t\t\t\t<ram:OriginFLUXLocation>\n" +
            "\t\t\t\t\t\t<ram:TypeCode listID=\"FLUX_LOCATION_TYPE\">AREA</ram:TypeCode>\n" +
            "\t\t\t\t\t\t<ram:ID schemeID=\"FAO_AREA\">27.3.d.24</ram:ID>\n" +
            "\t\t\t\t\t</ram:OriginFLUXLocation>\n" +
            "\t\t\t\t</ram:SpecifiedAAPProduct>\n" +
            "\t\t\t\t<ram:SpecifiedAAPProduct>\n" +
            "\t\t\t\t\t<ram:SpeciesCode listID=\"FAO_SPECIES\">FLE</ram:SpeciesCode>\n" +
            "\t\t\t\t\t<ram:WeightMeasure unitCode=\"KGM\">102</ram:WeightMeasure>\n" +
            "\t\t\t\t\t<ram:UsageCode listID=\"PROD_USAGE\">HCN</ram:UsageCode>\n" +
            "\t\t\t\t\t<ram:AppliedAAPProcess>\n" +
            "\t\t\t\t\t\t<ram:TypeCode listID=\"FISH_FRESHNESS\">B</ram:TypeCode>\n" +
            "\t\t\t\t\t\t<ram:TypeCode listID=\"FISH_PRESERVATION\">FRE</ram:TypeCode>\n" +
            "\t\t\t\t\t\t<ram:TypeCode listID=\"FISH_PRESENTATION\">WHL</ram:TypeCode>\n" +
            "\t\t\t\t\t</ram:AppliedAAPProcess>\n" +
            "\t\t\t\t\t<ram:TotalSalesPrice>\n" +
            "\t\t\t\t\t\t<ram:ChargeAmount>0.82</ram:ChargeAmount>\n" +
            "\t\t\t\t\t</ram:TotalSalesPrice>\n" +
            "\t\t\t\t\t<ram:SpecifiedSizeDistribution>\n" +
            "\t\t\t\t\t\t<ram:CategoryCode listID=\"FISH_SIZE_CATEGORY\">2</ram:CategoryCode>\n" +
            "\t\t\t\t\t\t<ram:ClassCode listID=\"FISH_SIZE_CLASS\">LSC</ram:ClassCode>\n" +
            "\t\t\t\t\t</ram:SpecifiedSizeDistribution>\n" +
            "\t\t\t\t\t<ram:OriginFLUXLocation>\n" +
            "\t\t\t\t\t\t<ram:TypeCode listID=\"FLUX_LOCATION_TYPE\">AREA</ram:TypeCode>\n" +
            "\t\t\t\t\t\t<ram:ID schemeID=\"FAO_AREA\">27.3.d.24</ram:ID>\n" +
            "\t\t\t\t\t</ram:OriginFLUXLocation>\n" +
            "\t\t\t\t</ram:SpecifiedAAPProduct>\n" +
            "\t\t\t\t<ram:SpecifiedAAPProduct>\n" +
            "\t\t\t\t\t<ram:SpeciesCode listID=\"FAO_SPECIES\">LIN</ram:SpeciesCode>\n" +
            "\t\t\t\t\t<ram:WeightMeasure unitCode=\"KGM\">9</ram:WeightMeasure>\n" +
            "\t\t\t\t\t<ram:UsageCode listID=\"PROD_USAGE\">HCN</ram:UsageCode>\n" +
            "\t\t\t\t\t<ram:AppliedAAPProcess>\n" +
            "\t\t\t\t\t\t<ram:TypeCode listID=\"FISH_FRESHNESS\">B</ram:TypeCode>\n" +
            "\t\t\t\t\t\t<ram:TypeCode listID=\"FISH_PRESERVATION\">FRE</ram:TypeCode>\n" +
            "\t\t\t\t\t\t<ram:TypeCode listID=\"FISH_PRESENTATION\">GUT</ram:TypeCode>\n" +
            "\t\t\t\t\t</ram:AppliedAAPProcess>\n" +
            "\t\t\t\t\t<ram:TotalSalesPrice>\n" +
            "\t\t\t\t\t\t<ram:ChargeAmount>3.55</ram:ChargeAmount>\n" +
            "\t\t\t\t\t</ram:TotalSalesPrice>\n" +
            "\t\t\t\t\t<ram:SpecifiedSizeDistribution>\n" +
            "\t\t\t\t\t\t<ram:CategoryCode listID=\"FISH_SIZE_CATEGORY\">3</ram:CategoryCode>\n" +
            "\t\t\t\t\t\t<ram:ClassCode listID=\"FISH_SIZE_CLASS\">LSC</ram:ClassCode>\n" +
            "\t\t\t\t\t</ram:SpecifiedSizeDistribution>\n" +
            "\t\t\t\t\t<ram:OriginFLUXLocation>\n" +
            "\t\t\t\t\t\t<ram:TypeCode listID=\"FLUX_LOCATION_TYPE\">AREA</ram:TypeCode>\n" +
            "\t\t\t\t\t\t<ram:ID schemeID=\"FAO_AREA\">27.7.a</ram:ID>\n" +
            "\t\t\t\t\t</ram:OriginFLUXLocation>\n" +
            "\t\t\t\t</ram:SpecifiedAAPProduct>\n" +
            "\t\t\t</ram:SpecifiedSalesBatch>\n" +
            "\t\t\t<ram:SpecifiedSalesEvent>\n" +
            "\t\t\t\t<ram:OccurrenceDateTime>\n" +
            "\t\t\t\t\t<udt:DateTime>2018-04-19T05:09:08.391Z</udt:DateTime>\n" +
            "\t\t\t\t</ram:OccurrenceDateTime>\n" +
            "\t\t\t</ram:SpecifiedSalesEvent>\n" +
            "\t\t\t<ram:SpecifiedFishingActivity>\n" +
            "\t\t\t\t<ram:TypeCode>LAN</ram:TypeCode>\n" +
            "\t\t\t\t<ram:RelatedFLUXLocation>\n" +
            "\t\t\t\t\t<ram:TypeCode listID=\"FLUX_LOCATION_TYPE\">LOCATION</ram:TypeCode>\n" +
            "\t\t\t\t\t<ram:CountryID schemeID=\"TERRITORY\">BEL</ram:CountryID>\n" +
            "\t\t\t\t\t<ram:ID schemeID=\"LOCATION\">BEOST</ram:ID>\n" +
            "\t\t\t\t</ram:RelatedFLUXLocation>\n" +
            "\t\t\t\t<ram:SpecifiedDelimitedPeriod>\n" +
            "\t\t\t\t\t<ram:StartDateTime>\n" +
            "\t\t\t\t\t\t<udt:DateTime>2018-04-19T10:02:08.391Z</udt:DateTime>\n" +
            "\t\t\t\t\t</ram:StartDateTime>\n" +
            "\t\t\t\t</ram:SpecifiedDelimitedPeriod>\n" +
            "\t\t\t\t<ram:SpecifiedFishingTrip>\n" +
            "\t\t\t\t\t<ram:ID schemeID=\"EU_TRIP_ID\">BEL-TRP-2018-1111-1111</ram:ID>\n" +
            "\t\t\t\t</ram:SpecifiedFishingTrip>\n" +
            "\t\t\t\t<ram:RelatedVesselTransportMeans>\n" +
            "\t\t\t\t\t<ram:ID schemeID=\"CFR\">BEL123456789</ram:ID>\n" +
            "\t\t\t\t\t<ram:Name>FAKE VESSEL</ram:Name>\n" +
            "\t\t\t\t\t<ram:RegistrationVesselCountry>\n" +
            "\t\t\t\t\t\t<ram:ID schemeID=\"TERRITORY\">FRA</ram:ID>\n" +
            "\t\t\t\t\t</ram:RegistrationVesselCountry>\n" +
            "\t\t\t\t\t<ram:SpecifiedContactParty>\n" +
            "\t\t\t\t\t\t<ram:RoleCode listID=\"FLUX_CONTACT_ROLE\">MASTER</ram:RoleCode>\n" +
            "\t\t\t\t\t\t<ram:SpecifiedContactPerson>\n" +
            "\t\t\t\t\t\t\t<ram:GivenName>Henrick</ram:GivenName>\n" +
            "\t\t\t\t\t\t\t<ram:MiddleName>Jan</ram:MiddleName>\n" +
            "\t\t\t\t\t\t\t<ram:FamilyName>JANSEN</ram:FamilyName>\n" +
            "\t\t\t\t\t\t</ram:SpecifiedContactPerson>\n" +
            "\t\t\t\t\t</ram:SpecifiedContactParty>\n" +
            "\t\t\t\t</ram:RelatedVesselTransportMeans>\n" +
            "\t\t\t</ram:SpecifiedFishingActivity>\n" +
            "\t\t\t<ram:SpecifiedFLUXLocation>\n" +
            "\t\t\t\t<ram:TypeCode listID=\"FLUX_LOCATION_TYPE\">LOCATION</ram:TypeCode>\n" +
            "\t\t\t\t<ram:CountryID schemeID=\"TERRITORY\">BEL</ram:CountryID>\n" +
            "\t\t\t\t<ram:ID schemeID=\"LOCATION\">BEOST</ram:ID>\n" +
            "\t\t\t</ram:SpecifiedFLUXLocation>\n" +
            "\t\t\t<ram:SpecifiedSalesParty>\n" +
            "\t\t\t\t<ram:ID schemeID=\"MS\">123456</ram:ID>\n" +
            "\t\t\t\t<ram:Name>Mr SENDER</ram:Name>\n" +
            "\t\t\t\t<ram:RoleCode listID=\"FLUX_SALES_PARTY_ROLE\">SENDER</ram:RoleCode>\n" +
            "\t\t\t</ram:SpecifiedSalesParty>\n" +
            "\t\t\t<ram:SpecifiedSalesParty>\n" +
            "\t\t\t\t<ram:ID schemeID=\"VAT\">1234567890</ram:ID>\n" +
            "\t\t\t\t<ram:Name>Mr BUYER</ram:Name>\n" +
            "\t\t\t\t<ram:RoleCode listID=\"FLUX_SALES_PARTY_ROLE\">BUYER</ram:RoleCode>\n" +
            "\t\t\t</ram:SpecifiedSalesParty>\n" +
            "\t\t\t<ram:SpecifiedSalesParty>\n" +
            "\t\t\t\t<ram:Name>Mr PROVIDER</ram:Name>\n" +
            "\t\t\t\t<ram:RoleCode listID=\"FLUX_SALES_PARTY_ROLE\">PROVIDER</ram:RoleCode>\n" +
            "\t\t\t</ram:SpecifiedSalesParty>\n" +
            "\t\t</ram:IncludedSalesDocument>\n" +
            "\t</SalesReport>\n" +
            "</FLUXSalesReportMessage>\n";
}