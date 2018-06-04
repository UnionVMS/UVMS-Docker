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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import java.io.IOException;
import java.util.List;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.fluent.Request;
import eu.europa.ec.fisheries.schema.rules.customrule.v1.SanityRuleType;
import eu.europa.ec.fisheries.uvms.docker.validation.common.AbstractHelper;

public class SanityRuleHelper extends AbstractHelper {

    public static List<SanityRuleType> getAllSanityRules() throws ClientProtocolException, IOException {
        final HttpResponse response = Request.Get(getBaseUrl() + "movement-rules/rest/sanityrules/listAll")
                .setHeader("Content-Type", "application/json").setHeader("Authorization", getValidJwtToken())
                .execute().returnResponse();
        return checkSuccessResponseReturnList(response, SanityRuleType.class);
    }
    
    public static int countOpenAlarms() throws ClientProtocolException, IOException {
        final HttpResponse response = Request.Get(getBaseUrl() + "movement-rules/rest/alarms/countopen")
                .setHeader("Content-Type", "application/json").setHeader("Authorization", getValidJwtToken())
                .execute().returnResponse();
        return checkSuccessResponseReturnInt(response);
    }
    
    public static void pollAlarmReportCreated() throws ClientProtocolException, IOException {
        final HttpResponse response = Request.Get(getBaseUrl() + "movement-rules/activity/alarm")
                .setHeader("Content-Type", "application/json")
                .execute().returnResponse();
        
        assertThat(response, is(notNullValue()));
    }
}
