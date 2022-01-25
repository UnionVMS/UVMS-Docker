package eu.europa.ec.fisheries.uvms.docker.validation.activity;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import eu.europa.ec.fisheries.uvms.asset.client.model.AssetDTO;
import un.unece.uncefact.data.standard.fluxfareportmessage._3.FLUXFAReportMessage;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.AAPProcess;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.DelimitedPeriod;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.FACatch;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.FAReportDocument;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.FLUXGeographicalCoordinate;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.FLUXLocation;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.FLUXParty;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.FLUXReportDocument;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.FishingActivity;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.FishingGear;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.FishingTrip;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.GearCharacteristic;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.SizeDistribution;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.VesselCountry;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.VesselTransportMeans;
import un.unece.uncefact.data.standard.unqualifieddatatype._20.CodeType;
import un.unece.uncefact.data.standard.unqualifieddatatype._20.DateTimeType;
import un.unece.uncefact.data.standard.unqualifieddatatype._20.IDType;
import un.unece.uncefact.data.standard.unqualifieddatatype._20.MeasureType;
import un.unece.uncefact.data.standard.unqualifieddatatype._20.NumericType;

public class ActivityMessageHelper {

    private ActivityMessageHelper() {}

    public static FLUXFAReportMessage getDeparture(AssetDTO asset, long tripId, Instant departureDate, String port) throws DatatypeConfigurationException {
        FLUXFAReportMessage faReportMessage = getFLUXFAReportMessage();
        FAReportDocument document = getFAReportDocument(asset, "DECLARATION", null);
        FishingActivity activity = new FishingActivity();
        activity.setTypeCode(getCodeType("FLUX_FA_TYPE", "DEPARTURE"));
        activity.setOccurrenceDateTime(getDateTime(departureDate));
        activity.setReasonCode(getCodeType("FA_REASON_DEPARTURE", "FIS"));
        FishingTrip trip = new FishingTrip();
        trip.getIDS().add(getIdType("EU_TRIP_ID", asset.getFlagStateCode() + "-TRP-" + tripId));
        activity.setSpecifiedFishingTrip(trip);
        FLUXLocation departurePort = new FLUXLocation();
        departurePort.setTypeCode(getCodeType("FLUX_LOCATION_TYPE", "LOCATION"));
        departurePort.setCountryID(getIdType("TERRITORY", asset.getFlagStateCode()));
        departurePort.setID(getIdType("LOCATION", port));
        activity.getRelatedFLUXLocations().add(departurePort);
        activity.getSpecifiedFishingGears().add(getFishingGear("ONBOARD"));
        document.getSpecifiedFishingActivities().add(activity);
        document.setSpecifiedVesselTransportMeans(getVessel(asset));
        faReportMessage.getFAReportDocuments().add(document);
        return faReportMessage;
    }

    public static FLUXFAReportMessage getArrival(AssetDTO asset, long tripId, Instant rtpDate, String port) throws DatatypeConfigurationException {
        FLUXFAReportMessage faReportMessage = getFLUXFAReportMessage();
        FAReportDocument document = getFAReportDocument(asset, "DECLARATION", null);
        FishingActivity activity = new FishingActivity();
        activity.setTypeCode(getCodeType("FLUX_FA_TYPE", "ARRIVAL"));
        activity.setOccurrenceDateTime(getDateTime(rtpDate));
        activity.setReasonCode(getCodeType("FA_REASON_ARRIVAL", "LAN"));
        FishingTrip trip = new FishingTrip();
        trip.getIDS().add(getIdType("EU_TRIP_ID", asset.getFlagStateCode() + "-TRP-" + tripId));
        activity.setSpecifiedFishingTrip(trip);
        FLUXLocation arrivalPort = new FLUXLocation();
        arrivalPort.setTypeCode(getCodeType("FLUX_LOCATION_TYPE", "LOCATION"));
        arrivalPort.setCountryID(getIdType("TERRITORY", asset.getFlagStateCode()));
        arrivalPort.setID(getIdType("LOCATION", port));
        activity.getRelatedFLUXLocations().add(arrivalPort);
        document.getSpecifiedFishingActivities().add(activity);
        document.setSpecifiedVesselTransportMeans(getVessel(asset));
        faReportMessage.getFAReportDocuments().add(document);
        return faReportMessage;
    }

    public static FLUXFAReportMessage getLanding(AssetDTO asset, long tripId, Instant landingDate, String port) throws DatatypeConfigurationException {
        FLUXFAReportMessage faReportMessage = getFLUXFAReportMessage();
        FAReportDocument document = getFAReportDocument(asset, "DECLARATION", null);
        FishingActivity activity = new FishingActivity();
        activity.setTypeCode(getCodeType("FLUX_FA_TYPE", "LANDING"));
        DelimitedPeriod landingPeriod = new DelimitedPeriod();
        landingPeriod.setStartDateTime(getDateTime(landingDate));
        landingPeriod.setEndDateTime(getDateTime(landingDate));
        activity.getSpecifiedDelimitedPeriods().add(landingPeriod);
        FishingTrip trip = new FishingTrip();
        trip.getIDS().add(getIdType("EU_TRIP_ID", asset.getFlagStateCode() + "-TRP-" + tripId));
        activity.setSpecifiedFishingTrip(trip);
        FLUXLocation landingPort = new FLUXLocation();
        landingPort.setTypeCode(getCodeType("FLUX_LOCATION_TYPE", "LOCATION"));
        landingPort.setCountryID(getIdType("TERRITORY", asset.getFlagStateCode()));
        landingPort.setID(getIdType("LOCATION", port));
        activity.getRelatedFLUXLocations().add(landingPort);
        FACatch faCatch = getFaCatch("UNLOADED", "ONBOARD");
        activity.getSpecifiedFACatches().add(faCatch);
        document.getSpecifiedFishingActivities().add(activity);
        document.setSpecifiedVesselTransportMeans(getVessel(asset));
        faReportMessage.getFAReportDocuments().add(document);
        return faReportMessage;
    }

    private static FLUXFAReportMessage getFLUXFAReportMessage() throws DatatypeConfigurationException {
        FLUXFAReportMessage fluxReport = new FLUXFAReportMessage();
        FLUXReportDocument fluxDocument = new FLUXReportDocument();
        fluxDocument.getIDS().add(getIdType("UUID", UUID.randomUUID().toString()));
        fluxDocument.setCreationDateTime(getDateTime(Instant.now()));
        fluxDocument.setPurposeCode(getCodeType("FLUX_GP_PURPOSE", "9"));
        FLUXParty ownerFluxParty = new FLUXParty();
        ownerFluxParty.getIDS().add(getIdType("FLUX_GP_PARTY", "SWE"));
        fluxDocument.setOwnerFLUXParty(ownerFluxParty);
        fluxReport.setFLUXReportDocument(fluxDocument);
        return fluxReport;
    }

    private static FAReportDocument getFAReportDocument(AssetDTO asset, String reportType, String referencedId) throws DatatypeConfigurationException {
        FAReportDocument document = new FAReportDocument();
        document.setTypeCode(getCodeType("FLUX_FA_REPORT_TYPE", reportType));
        document.setAcceptanceDateTime(getDateTime(Instant.now()));
        FLUXReportDocument relatedDocument = new FLUXReportDocument();
        relatedDocument.getIDS().add(getIdType("UUID", UUID.randomUUID().toString()));
        relatedDocument.setPurposeCode(getCodeType("FLUX_GP_PURPOSE", "9"));
        if (referencedId != null) {
            relatedDocument.setReferencedID(getIdType("UUID", referencedId));
            relatedDocument.setPurposeCode(getCodeType("FLUX_GP_PURPOSE", "5"));
        }
        relatedDocument.setCreationDateTime(getDateTime(Instant.now()));
        FLUXParty ownerParty = new FLUXParty();
        ownerParty.getIDS().add(getIdType("FLUX_GP_PARTY", asset.getFlagStateCode()));
        relatedDocument.setOwnerFLUXParty(ownerParty);
        document.setRelatedFLUXReportDocument(relatedDocument);
        return document;
    }

    private static VesselTransportMeans getVessel(AssetDTO asset) {
        VesselTransportMeans vesselMeans = new VesselTransportMeans();
        vesselMeans.getIDS().add(getIdType("CFR", asset.getCfr()));
        vesselMeans.getIDS().add(getIdType("IRCS", asset.getIrcs()));
        VesselCountry country = new VesselCountry();
        country.setID(getIdType("TERRITORY", asset.getFlagStateCode()));
        vesselMeans.setRegistrationVesselCountry(country);
        vesselMeans.setRoleCode(getCodeType("FA_VESSEL_ROLE", "CATCHING_VESSEL"));
        return vesselMeans;
    }

    private static FishingGear getFishingGear(String gearRole) {
        FishingGear fishingGear = new FishingGear();
        fishingGear.setTypeCode(getCodeType("GEAR_TYPE", "GND"));
        fishingGear.getRoleCodes().add(getCodeType("FA_GEAR_ROLE", gearRole));
        GearCharacteristic gearCharacteristics = new GearCharacteristic();
        gearCharacteristics.setTypeCode(getCodeType("FA_GEAR_CHARACTERISTIC", "ME"));
        gearCharacteristics.setValueMeasure(getMeasureType("20"));
        fishingGear.getApplicableGearCharacteristics().add(gearCharacteristics);
        GearCharacteristic gearCharacteristics2 = new GearCharacteristic();
        gearCharacteristics2.setTypeCode(getCodeType("FA_GEAR_CHARACTERISTIC", "NL"));
        gearCharacteristics2.setValueMeasure(getMeasureType("100"));
        fishingGear.getApplicableGearCharacteristics().add(gearCharacteristics2);
        return fishingGear;
    }

    private static FACatch getFaCatch(String catchType, String weightMeans) {
        FACatch faCatch = new FACatch();
        faCatch.setTypeCode(getCodeType("FA_CATCH_TYPE", catchType));
        faCatch.setSpeciesCode(getCodeType("FAO_SPECIES", "COD"));
        faCatch.setWeightMeasure(getMeasureType("100"));
        faCatch.setWeighingMeansCode(getCodeType("WEIGHT_MEANS", weightMeans));
        FLUXLocation locationPosition = new FLUXLocation();
        locationPosition.setTypeCode(getCodeType("FLUX_LOCATION_TYPE", "POSITION"));
        FLUXGeographicalCoordinate coordinates = new FLUXGeographicalCoordinate();
        coordinates.setLongitudeMeasure(getMeasureType("56"));
        coordinates.setLatitudeMeasure(getMeasureType("11"));
        locationPosition.setSpecifiedPhysicalFLUXGeographicalCoordinate(coordinates);
        faCatch.getSpecifiedFLUXLocations().add(locationPosition);
        FLUXLocation locationEez = new FLUXLocation();
        locationEez.setTypeCode(getCodeType("FLUX_LOCATION_TYPE", "AREA"));
        locationEez.setID(getIdType("TERRITORY", "SWE"));
        faCatch.getSpecifiedFLUXLocations().add(locationEez);
        FLUXLocation locationFao = new FLUXLocation();
        locationFao.setTypeCode(getCodeType("FLUX_LOCATION_TYPE", "AREA"));
        locationFao.setID(getIdType("FAO_AREA", "27.a.3.n"));
        faCatch.getSpecifiedFLUXLocations().add(locationFao);
        SizeDistribution size = new SizeDistribution();
        size.getClassCodes().add(getCodeType("FISH_SIZE_CLASS", "LSC"));
        faCatch.setSpecifiedSizeDistribution(size);
        AAPProcess aapProcess = new AAPProcess();
        aapProcess.getTypeCodes().add(getCodeType("FISH_PRESENTATION", "WHL"));
        aapProcess.getTypeCodes().add(getCodeType("FISH_PRESERVATION", "FRE"));
        aapProcess.setConversionFactorNumeric(getNumericType(BigDecimal.ONE));
        faCatch.getAppliedAAPProcesses().add(aapProcess);
        return faCatch;
    }

    private static CodeType getCodeType(String listId, String code) {
        CodeType codeType = new CodeType();
        codeType.setListID(listId);
        codeType.setValue(code);
        return codeType;
    }

    private static IDType getIdType(String schemeId, String value) {
        IDType idType = new IDType();
        idType.setSchemeID(schemeId);
        idType.setValue(value);
        return idType;
    }

    private static MeasureType getMeasureType(String value) {
        return getMeasureType(BigDecimal.valueOf(Double.parseDouble(value)));
    }

    private static MeasureType getMeasureType(BigDecimal value) {
        MeasureType measureType = new MeasureType();
        measureType.setValue(value);
        return measureType;
    }

    private static DateTimeType getDateTime(Instant date) throws DatatypeConfigurationException {
        DateTimeType dateTime = new DateTimeType();
        dateTime.setDateTime(instantToXmlGregorianCalendar(date));
        return dateTime;
    }

    private static XMLGregorianCalendar instantToXmlGregorianCalendar(Instant instant) throws DatatypeConfigurationException {
        return DatatypeFactory.newInstance().newXMLGregorianCalendar(instant.toString());
    }

    public static NumericType getNumericType(BigDecimal value) {
        NumericType numericType = new NumericType();
        numericType.setValue(value);
        return numericType;
    }
}
