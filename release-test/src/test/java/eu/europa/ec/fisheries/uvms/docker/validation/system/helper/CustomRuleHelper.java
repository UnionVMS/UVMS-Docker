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
import static org.hamcrest.CoreMatchers.nullValue;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.fluent.Request;
import com.fasterxml.jackson.core.JsonProcessingException;
import eu.europa.ec.fisheries.schema.movementrules.customrule.v1.CustomRuleType;
import eu.europa.ec.fisheries.uvms.docker.validation.common.AbstractHelper;

public class CustomRuleHelper extends AbstractHelper {

    public static CustomRuleType createCustomRule(CustomRuleType customRule) throws ClientProtocolException, JsonProcessingException, IOException {
        final HttpResponse response = Request.Post(getBaseUrl() + "movement-rules/rest/customrules")
                .setHeader("Content-Type", "application/json").setHeader("Authorization", getValidJwtToken())
                .bodyByteArray(writeValueAsString(customRule).getBytes()).execute().returnResponse();

        return checkSuccessResponseReturnObject(response, CustomRuleType.class);
    }
    
    public static void removeCustomRule(String guid) throws Exception {
        HttpResponse response = Request.Delete(getBaseUrl() + "movement-rules/rest/customrules/" + guid)
                .setHeader("Content-Type", "application/json").setHeader("Authorization", getValidJwtToken())
                .execute().returnResponse();
        
        checkSuccessResponseReturnDataMap(response);
    }

    public static void assertRuleTriggered(CustomRuleType rule, Date dateFrom) throws Exception {
        HttpResponse response = Request.Get(getBaseUrl() + "movement-rules/rest/customrules/" + rule.getGuid())
                .setHeader("Content-Type", "application/json").setHeader("Authorization", getValidJwtToken()).execute()
                .returnResponse();

        CustomRuleType fetchedCustomRule = checkSuccessResponseReturnObject(response, CustomRuleType.class);
        
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");
        formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date lastTriggered = formatter.parse(fetchedCustomRule.getLastTriggered());
        assertNotNull(lastTriggered);
        assertTrue(lastTriggered.getTime() >= dateFrom.getTime());
    }
    
    public static void assertRuleNotTriggered(CustomRuleType rule) throws Exception {
        HttpResponse response = Request.Get(getBaseUrl() + "movement-rules/rest/customrules/" + rule.getGuid())
                .setHeader("Content-Type", "application/json").setHeader("Authorization", getValidJwtToken()).execute()
                .returnResponse();

        CustomRuleType fetchedCustomRule = checkSuccessResponseReturnObject(response, CustomRuleType.class);
        
        assertThat(fetchedCustomRule.getLastTriggered(), is(nullValue()));
    }
    
    public static void pollTicketCreated() throws ClientProtocolException, IOException {
        final HttpResponse response = Request.Get(getBaseUrl() + "rules/activity/ticket")
                .setHeader("Content-Type", "application/json")
                .execute().returnResponse();
        
        assertThat(response.getStatusLine().getStatusCode(), is(200));
    }
}
