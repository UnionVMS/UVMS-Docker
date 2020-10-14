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

import eu.europa.ec.fisheries.schema.movement.v1.MovementTypeType;
import eu.europa.ec.fisheries.uvms.asset.client.model.AssetDTO;
import eu.europa.ec.fisheries.uvms.docker.validation.common.AbstractHelper;
import eu.europa.ec.fisheries.uvms.docker.validation.movement.LatLong;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import static org.junit.Assert.assertEquals;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NAFHelper extends AbstractHelper {

    public static void sendPositionToNAFPlugin(LatLong position, AssetDTO asset) throws IOException {
        String nafString = convertToNafString(position, asset, MovementTypeType.POS);
        sendReport(nafString);
    }

    public static void sendEntryReportToNAFPlugin(LatLong position, AssetDTO asset) throws IOException {
        String nafString = convertToNafString(position, asset, MovementTypeType.ENT);
        sendReport(nafString);
    }

    public static void sendExitReportToNAFPlugin(LatLong position, AssetDTO asset) throws IOException {
        String nafString = convertToNafString(position, asset, MovementTypeType.EXI);
        sendReport(nafString);
    }

    public static String readCodeValue(String code, String nafMessage) {
        Pattern pattern = Pattern.compile("//" + code + "/" + "([^" + "/" + "]+)" + "//");
        Matcher matcher = pattern.matcher(nafMessage);
        matcher.find();
        return matcher.group(1);
    }
    
    public static Double readCodeDoubleValue(String code, String nafMessage) {
        return Double.valueOf(readCodeValue(code, nafMessage));
    }

    public static boolean isCodePresent(String code, String nafMessage) {
        Pattern pattern = Pattern.compile("//" + code + "/" + "([^" + "/" + "]+)" + "//");
        Matcher matcher = pattern.matcher(nafMessage);
        return matcher.find();
    }

    private static String convertToNafString(LatLong position, AssetDTO asset, MovementTypeType typeOfMessage) throws UnsupportedEncodingException {
        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyyMMdd");
        dateFormatter.setTimeZone(TimeZone.getTimeZone(ZoneId.of("UTC")));
        SimpleDateFormat timeFormatter = new SimpleDateFormat("HHmm");
        timeFormatter.setTimeZone(TimeZone.getTimeZone(ZoneId.of("UTC")));

        DecimalFormat formatter = new DecimalFormat("#00.000");

        String str = "//SR" +
                "//FR/" + asset.getFlagStateCode() +
                "//AD/UVM" +
                "//TM/" + typeOfMessage.value() +
                "//RC/" + asset.getIrcs() +
                "//IR/" + asset.getCfr() +
                "//XR/" + asset.getExternalMarking();
        if (!MovementTypeType.EXI.equals(typeOfMessage)) {
        str +=  "//LT/" + formatter.format(position.latitude).replace(",", ".") +
                "//LG/" + formatter.format(position.longitude).replace(",", ".") +
                "//SP/" + (int) (position.speed * 10) +
                "//CO/" + (int) position.bearing;
        }
        str +=  "//DA/" + dateFormatter.format(position.positionTime) +
                "//TI/" + timeFormatter.format(position.positionTime) +
                "//NA/" + asset.getName() +
                "//FS/" + asset.getFlagStateCode() +
                "//ER//";
        return URLEncoder.encode(str, "UTF-8");
    }

    private static void sendReport(String report) {
        String requestPath = getNAFUrl() + "naf/rest/message/" + report;
        Response response = ClientBuilder.newClient()
                .target(requestPath)
                .request(MediaType.APPLICATION_JSON)
                .get();
        assertEquals(Status.OK.getStatusCode(), response.getStatus());
    }
    
    private static String getNAFUrl() {
        return getBaseUrl().replace("unionvms/", "");
    }
}