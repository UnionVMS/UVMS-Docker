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
package eu.europa.ec.fisheries.uvms.docker.validation.movement;

import java.io.IOException;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.fluent.Request;
import com.fasterxml.jackson.core.JsonProcessingException;
import eu.europa.ec.fisheries.schema.movement.v1.TempMovementType;
import eu.europa.ec.fisheries.uvms.docker.validation.common.AbstractHelper;

public class TempMovementRestHelper extends AbstractHelper {

    public static TempMovementType createTempMovement(TempMovementType tempMovement) throws ClientProtocolException,
            JsonProcessingException, IOException {
        final HttpResponse response = Request.Post(getBaseUrl() + "movement/rest/tempmovement").setHeader(
                "Content-Type", "application/json").setHeader("Authorization", getValidJwtToken()).bodyByteArray(
                        writeValueAsString(tempMovement).getBytes()).execute().returnResponse();

        return checkSuccessResponseReturnObject(response, TempMovementType.class);
    }

    public static HttpResponse createTempMovementResponse(TempMovementType tempMovement) throws ClientProtocolException,
            JsonProcessingException, IOException {
        return Request.Post(getBaseUrl() + "movement/rest/tempmovement").setHeader("Content-Type", "application/json")
                .setHeader("Authorization", getValidJwtToken()).bodyByteArray(writeValueAsString(tempMovement)
                        .getBytes()).execute().returnResponse();
    }

    public static TempMovementType getTempMovement(String guid) throws ClientProtocolException, IOException {
        final HttpResponse response = Request.Get(getBaseUrl() + "movement/rest/tempmovement/" + guid).setHeader(
                "Content-Type", "application/json").setHeader("Authorization", getValidJwtToken()).execute()
                .returnResponse();

        return checkSuccessResponseReturnObject(response, TempMovementType.class);
    }

    public static HttpResponse getTempMovementResponse(String guid) throws ClientProtocolException, IOException {
        return Request.Get(getBaseUrl() + "movement/rest/tempmovement/" + guid).setHeader("Content-Type",
                "application/json").setHeader("Authorization", getValidJwtToken()).execute().returnResponse();
    }

    public static TempMovementType removeTempMovement(String guid) throws ClientProtocolException, IOException {
        final HttpResponse response = Request.Put(getBaseUrl() + "movement/rest/tempmovement/remove/" + guid).setHeader(
                "Content-Type", "application/json").setHeader("Authorization", getValidJwtToken()).execute()
                .returnResponse();

        return checkSuccessResponseReturnObject(response, TempMovementType.class);
    }

    public static HttpResponse removeTempMovementResponse(String guid) throws ClientProtocolException, IOException {
        return Request.Put(getBaseUrl() + "movement/rest/tempmovement/remove/" + guid).setHeader("Content-Type",
                "application/json").setHeader("Authorization", getValidJwtToken()).execute().returnResponse();
    }

    public static TempMovementType updateTempMovement(TempMovementType tempMovement) throws ClientProtocolException,
            JsonProcessingException, IOException {
        final HttpResponse response = Request.Put(getBaseUrl() + "movement/rest/tempmovement").setHeader("Content-Type",
                "application/json").setHeader("Authorization", getValidJwtToken()).bodyByteArray(writeValueAsString(
                        tempMovement).getBytes()).execute().returnResponse();

        return checkSuccessResponseReturnObject(response, TempMovementType.class);
    }

    public static HttpResponse updateTempMovementResponse(TempMovementType tempMovement) throws ClientProtocolException,
            JsonProcessingException, IOException {
        return Request.Put(getBaseUrl() + "movement/rest/tempmovement").setHeader("Content-Type", "application/json")
                .setHeader("Authorization", getValidJwtToken()).bodyByteArray(writeValueAsString(tempMovement)
                        .getBytes()).execute().returnResponse();
    }
}
