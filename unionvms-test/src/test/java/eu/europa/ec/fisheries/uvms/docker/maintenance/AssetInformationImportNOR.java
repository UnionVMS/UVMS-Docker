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
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.Charset;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.junit.Ignore;
import org.junit.Test;
import eu.europa.ec.fisheries.uvms.asset.client.model.AssetDTO;
import eu.europa.ec.fisheries.uvms.docker.validation.asset.AssetTestHelper;
import eu.europa.ec.fisheries.uvms.docker.validation.common.AbstractRest;

public class AssetInformationImportNOR extends AbstractRest {
    private static final String EXTERNAL_MARKING = "Registreringsmerke";
    private static final String NAME = "Fartøynavn";
    private static final String IRCS = "Radio / Kjenningssignal";
    private static final String LOA = "Største lengde (m)";
    private static final String TONNAGE_LONDON = "Bruttotonnasje (1969)";
    private static final String TONNAGE_OSLO = "Bruttotonnasje (annen)";
    private static final String MAIN_POWER = "Motorstyrke (hk)";
    private static final String YEAR_OF_CONSTRUCTION = "Byggeår";

    private static final Double MINIMAL_LENGTH = 10d;
    
    private static final Double HORSEPOWER_TO_KW_RATIO = 0.745699872;
    
    @Ignore
    @Test
    public void importFromCsv() throws IOException {
        String csvFile = "/path/to/file.csv";
        
        Reader in = new FileReader(csvFile, Charset.forName("ISO-8859-1"));
        Iterable<CSVRecord> records = CSVFormat.DEFAULT
                                    .withDelimiter(';')
                                    .withFirstRecordAsHeader()
                                    .withQuote(null)
                                    .parse(in);
        
        for (CSVRecord csvRecord : records) {
            try {
                AssetDTO asset = populateAsset(csvRecord);
                    if (asset.getLengthOverAll() != null 
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
        asset.setFlagStateCode("NOR");
        asset.setExternalMarking(formatExternalMarking(record.get(EXTERNAL_MARKING)));
        String name = record.get(NAME);
        asset.setName(name.substring(0, Math.min(name.length(), 40)));
        asset.setIrcs(record.get(IRCS));
        asset.setVesselType("Fishing");
        String loa = record.get(LOA);
        asset.setLengthOverAll(loa.isEmpty() ? null : Double.valueOf(loa.replace(",", ".")));
        String london = record.get(TONNAGE_LONDON);
        String oslo = record.get(TONNAGE_OSLO);
        if (!london.isEmpty()) {
            asset.setGrossTonnage(Double.valueOf(london));
            asset.setGrossTonnageUnit("LONDON");
        } else if (!oslo.isEmpty()) {
            asset.setGrossTonnage(Double.valueOf(oslo));
            asset.setGrossTonnageUnit("OSLO");
        }
        String main_power = record.get(MAIN_POWER);
        asset.setPowerOfMainEngine(main_power.isEmpty() ? null : getPowerOfMainEngineInKw(main_power));
        String constructionYear = record.get(YEAR_OF_CONSTRUCTION);
        asset.setConstructionYear(constructionYear.isEmpty() ? null : constructionYear);
        asset.setSource("INTERNAL");
        return asset;
    }

    private String formatExternalMarking(String extMark) {
        String first = extMark.substring(0, 2).trim();
        String second = extMark.substring(2, 6);
        String third = extMark.substring(6, Math.min(extMark.length(), 8));
        return first + "-" + Integer.valueOf(second) + "-" + third;
    }

    private Double getPowerOfMainEngineInKw(String mainPower) {
        BigDecimal bd = new BigDecimal(Double.valueOf(mainPower) * HORSEPOWER_TO_KW_RATIO);
        bd = bd.setScale(2, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}
