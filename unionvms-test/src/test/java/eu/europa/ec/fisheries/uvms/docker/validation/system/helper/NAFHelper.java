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

import eu.europa.ec.fisheries.uvms.docker.validation.common.AbstractHelper;
import eu.europa.ec.fisheries.uvms.docker.validation.movement.LatLong;
import eu.europa.ec.fisheries.wsdl.asset.types.Asset;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.fluent.Request;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

public class NAFHelper extends AbstractHelper {

    public static void sendPositionToNAFPlugin(LatLong position, Asset asset) throws ClientProtocolException, IOException {
        String nafString = convertToNafString(position, asset);
//        String requestPath = getBaseUrl() + "naf/rest/message/" + nafString;
        String requestPath = "http://localhost:28080/naf/rest/message/" + nafString;
        HttpResponse response = Request.Get(requestPath).execute().returnResponse();
        assertEquals(200, response.getStatusLine().getStatusCode());
    }
    
    private static String convertToNafString(LatLong position, Asset asset) throws UnsupportedEncodingException {
        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyyMMdd");
        SimpleDateFormat timeFormatter = new SimpleDateFormat("HHmm");
        
        DecimalFormat decimalformatter = new DecimalFormat("#00.000");
        
        StringBuilder str = new StringBuilder();
        str.append("//SR");
        str.append("//FR/" + asset.getCountryCode());
        str.append("//AD/UVM");
        str.append("//TM/POS");
        str.append("//RC/" + asset.getIrcs());
        str.append("//IR/" + asset.getCfr());
        str.append("//XT/" + asset.getExternalMarking());
        str.append("//LT/" + decimalformatter.format(position.latitude).replace(",", "."));
        str.append("//LG/" + decimalformatter.format(position.longitude).replace(",", "."));
        str.append("//SP/" + (int) position.speed);
        str.append("//CO/" + (int) position.bearing);
        str.append("//DA/" + dateFormatter.format(position.positionTime));
        str.append("//TI/" + timeFormatter.format(position.positionTime));
        str.append("//NA/" + asset.getName());
        str.append("//FS/" + asset.getCountryCode());
        str.append("//ER//");
        return URLEncoder.encode(str.toString(), "UTF-8");
    }
}
