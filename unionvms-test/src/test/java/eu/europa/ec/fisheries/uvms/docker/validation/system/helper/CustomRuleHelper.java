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

import eu.europa.ec.fisheries.schema.movementrules.customrule.v1.CustomRuleType;
import eu.europa.ec.fisheries.uvms.docker.validation.common.AbstractHelper;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class CustomRuleHelper extends AbstractHelper {

    public static CustomRuleType createCustomRule(CustomRuleType customRule) {
        return getWebTarget()
               .path("movement-rules/rest/customrules")
               .request(MediaType.APPLICATION_JSON)
               .header(HttpHeaders.AUTHORIZATION, getValidJwtToken())
               .post(Entity.json(customRule), CustomRuleType.class);
    }
    
    public static void removeCustomRule(String guid) {
        getWebTarget()
            .path("movement-rules/rest/customrules")
            .path(guid)
            .request(MediaType.APPLICATION_JSON)
            .header(HttpHeaders.AUTHORIZATION, getValidJwtToken())
            .delete();
    }

    public static void removeCustomRulesByDefaultUser() {
        List<CustomRuleType> customRules = getWebTarget()
                .path("movement-rules/rest/customrules/listAll")
                .path(CustomRuleBuilder.DEFAULT_USER)
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getValidJwtToken())
                .get(new GenericType<List<CustomRuleType>>() {});
        
        for (CustomRuleType customRuleType : customRules) {
            removeCustomRule(customRuleType.getGuid());
        }
    }
    
    public static void assertRuleTriggered(CustomRuleType rule, Instant dateFrom) {
        CustomRuleType fetchedCustomRule = getWebTarget()
                .path("movement-rules/rest/customrules")
                .path(rule.getGuid())
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getValidJwtToken())
                .get(CustomRuleType.class);
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss Z");
        Instant lastTriggered = Instant.ofEpochMilli(Long.valueOf(fetchedCustomRule.getLastTriggered()));
        assertNotNull(lastTriggered);

        assertTrue(lastTriggered.isAfter(dateFrom) || compareWithoutMillis(lastTriggered,dateFrom));
    }

    private static boolean compareWithoutMillis(Instant a, Instant b) {
        Instant i1 = a.truncatedTo(ChronoUnit.SECONDS);
        Instant i2 = b.truncatedTo(ChronoUnit.SECONDS);
        return i1.equals(i2);
    }
    
    public static void assertRuleNotTriggered(CustomRuleType rule) {
        CustomRuleType fetchedCustomRule = getWebTarget()
                .path("movement-rules/rest/customrules")
                .path(rule.getGuid())
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getValidJwtToken())
                .get(CustomRuleType.class);

        assertThat(fetchedCustomRule.getLastTriggered(), is(nullValue()));
    }

    public static void pollTicketCreated() {
        getWebTarget()
            .path("movement-rules/activity/ticket")
            .request(MediaType.APPLICATION_JSON)
            .get();
    }
}
