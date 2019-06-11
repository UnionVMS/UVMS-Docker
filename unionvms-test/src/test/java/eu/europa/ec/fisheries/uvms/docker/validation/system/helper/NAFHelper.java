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
import org.apache.http.HttpResponse;
import org.apache.http.client.fluent.Request;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.TimeZone;

public class NAFHelper extends AbstractHelper {

    public static void sendPositionToNAFPlugin(LatLong position, AssetDTO asset) throws IOException {
        String nafString = convertToNafString(position, asset);
        String requestPath = "http://localhost:28080/naf/rest/message/" + nafString;
        HttpResponse response = Request.Get(requestPath).execute().returnResponse();
        assertEquals(200, response.getStatusLine().getStatusCode());
    }
    
    private static String convertToNafString(LatLong position, AssetDTO asset) throws UnsupportedEncodingException {
        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyyMMdd");
        dateFormatter.setTimeZone(TimeZone.getTimeZone(ZoneId.of("UTC")));
        SimpleDateFormat timeFormatter = new SimpleDateFormat("HHmm");
        timeFormatter.setTimeZone(TimeZone.getTimeZone(ZoneId.of("UTC")));

        DecimalFormat formatter = new DecimalFormat("#00.000");

        String str = "//SR" +
                "//FR/" + asset.getFlagStateCode() +
                "//AD/UVM" +
                "//TM/POS" +
                "//RC/" + asset.getIrcs() +
                "//IR/" + asset.getCfr() +
                "//XT/" + asset.getExternalMarking() +
                "//LT/" + formatter.format(position.latitude).replace(",", ".") +
                "//LG/" + formatter.format(position.longitude).replace(",", ".") +
                "//SP/" + (int) position.speed +
                "//CO/" + (int) position.bearing +
                "//DA/" + dateFormatter.format(position.positionTime) +
                "//TI/" + timeFormatter.format(position.positionTime) +
                "//NA/" + asset.getName() +
                "//FS/" + asset.getFlagStateCode() +
                "//ER//";
        return URLEncoder.encode(str, "UTF-8");
    }
}
