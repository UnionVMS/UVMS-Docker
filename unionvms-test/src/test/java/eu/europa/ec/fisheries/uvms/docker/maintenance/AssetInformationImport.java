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
package eu.europa.ec.fisheries.uvms.docker.maintenance;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.junit.Ignore;
import org.junit.Test;
import eu.europa.ec.fisheries.uvms.asset.client.model.AssetDTO;
import eu.europa.ec.fisheries.uvms.docker.validation.asset.AssetTestHelper;
import eu.europa.ec.fisheries.uvms.docker.validation.common.AbstractRest;

public class AssetInformationImport extends AbstractRest {
    private static final String FLAGSTATE = "Country of Registration"; 
    private static final String CFR = "CFR";
    private static final String UVI = "UVI";
    private static final String EVENT_CODE = "Event";
    private static final String EVENT_START_DATE = "Event Start Date";
    private static final String EVENT_END_DATE = "Event End Date";
    private static final String REGISTRATION_NUMBER = "Registration Number";
    private static final String EXTERNAL_MARKING = "External marking";
    private static final String NAME = "Name of vessel";
    private static final String PLACE_OF_REGISTRATION = "Place of registration";
    private static final String IRCS = "IRCS";
    private static final String IRCS_INDICATOR = "IRCS indicator";
    private static final String LICENCE_INDICATOR = "Licence indicator";
    private static final String VMS_INDICATOR = "VMS indicator";
    private static final String ERS_INDICATOR = "ERS indicator";
    private static final String AIS_INDICATOR = "AIS indicator";
    private static final String MMSI = "MMSI";
    private static final String VESSEL_TYPE = "Vessel Type";
    private static final String MAIN_FISHING_GEAR = "Main fishing gear";
    private static final String SUB_FISHING_GEAR = "Subsidiary fishing gear 1";
    private static final String SUB_FISHING_GEAR2 = "Subsidiary fishing gear 2";
    private static final String SUB_FISHING_GEAR3 = "Subsidiary fishing gear 3";
    private static final String SUB_FISHING_GEAR4 = "Subsidiary fishing gear 4";
    private static final String SUB_FISHING_GEAR5 = "Subsidiary fishing gear 5";
    private static final String LOA = "LOA";
    private static final String LBP = "LBP";
    private static final String TONNAGE = "Tonnage GT";
    private static final String OTHER_TONNAGE = "Other tonnage";
    private static final String GTS = "GTs";
    private static final String MAIN_POWER = "Power of main engine";
    private static final String AUX_POWER = "Power of auxiliary engine";
    private static final String HULL_MATERIAL = "Hull material";
    private static final String DATE_OF_ENTRY = "Date of entry into service";
    private static final String SEGMENT = "Segment";
    private static final String COUNTRY_OF_IMP_EXP = "Country of importation/exportation";
    private static final String TYPE_OF_EXPORT = "Type of export";
    private static final String PUBLIC_AID = "Public aid";
    private static final String YEAR_OF_CONSTRUCTION = "Year of construction";

    private static final List<String> IMPORT_COUNTRIES = Arrays.asList("BEL", "DEU", "DNK", "EST", "FIN", "FRA", "GBR", "IRL", "LTU", "LVA", "NLD", "POL", "ESP", "PRT");
    private static final Double MINIMAL_LENGTH = 10d;
    
    @Ignore
    @Test
    public void importCsvFromFleet() throws IOException {
        String csvFile = "/path/to/file.csv";
        
        Reader in = new FileReader(csvFile);
        Iterable<CSVRecord> records = CSVFormat.Builder.create()
                                    .setDelimiter(';')
//                                    .withFirstRecordAsHeader()
                                    .setQuote(null)
                                    .build()
                                    .parse(in);
        
        for (CSVRecord csvRecord : records) {
            try {
                AssetDTO asset = populateAsset(csvRecord);
                    if (IMPORT_COUNTRIES.contains(asset.getFlagStateCode()) 
                            && asset.getLengthOverAll() != null 
                            && asset.getLengthOverAll() > MINIMAL_LENGTH) {
                        AssetTestHelper.createAsset(asset);
                    }
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
        }
    }
    
    private AssetDTO populateAsset(CSVRecord record) {
        AssetDTO asset = new AssetDTO();
        asset.setFlagStateCode(record.get(FLAGSTATE));
        asset.setCfr(record.get(CFR));
        asset.setImo(record.get(UVI));
        asset.setEventCode(record.get(EVENT_CODE));
        asset.setRegistrationNumber(record.get(REGISTRATION_NUMBER));
        asset.setExternalMarking(record.get(EXTERNAL_MARKING));
        String name = record.get(NAME);
        asset.setName(name.substring(0, Math.min(name.length(), 40)));
        asset.setPortOfRegistration(record.get(PLACE_OF_REGISTRATION));
        asset.setIrcs(record.get(IRCS));
        asset.setIrcsIndicator("Y".equals(record.get(IRCS_INDICATOR)));
        asset.setHasLicence("Y".equals(record.get(LICENCE_INDICATOR)));
        asset.setHasVms("Y".equals(record.get(VMS_INDICATOR)));
        asset.setErsIndicator("Y".equals(record.get(ERS_INDICATOR)));
        asset.setAisIndicator("Y".equals(record.get(AIS_INDICATOR)));
        asset.setMmsi(record.get(MMSI));
        asset.setVesselType("Fishing");
        asset.setMainFishingGearCode(record.get(MAIN_FISHING_GEAR));
        asset.setSubFishingGearCode(record.get(SUB_FISHING_GEAR));
        asset.setLengthOverAll(Double.valueOf(record.get(LOA)));
        asset.setLengthBetweenPerpendiculars(Double.valueOf(record.get(LBP)));
        asset.setGrossTonnage(Double.valueOf(record.get(TONNAGE)));
        asset.setGrossTonnageUnit("LONDON");
        asset.setOtherTonnage(Double.valueOf(record.get(OTHER_TONNAGE)));
        asset.setPowerOfMainEngine(Double.valueOf(record.get(MAIN_POWER)));
        asset.setPowerOfAuxEngine(Double.valueOf(record.get(AUX_POWER)));
        asset.setHullMaterial(record.get(HULL_MATERIAL));
        asset.setSegment(record.get(SEGMENT));
        asset.setCountryOfImportOrExport(record.get(COUNTRY_OF_IMP_EXP).isEmpty() ? null : record.get(COUNTRY_OF_IMP_EXP));
        asset.setTypeOfExport(record.get(TYPE_OF_EXPORT));
        asset.setPublicAid(record.get(PUBLIC_AID));
        String constructionYear = record.get(YEAR_OF_CONSTRUCTION);
        asset.setConstructionYear(constructionYear.isEmpty() ? null : constructionYear.split("-")[0]);
        asset.setSource("INTERNAL");
        return asset;
    }

}
