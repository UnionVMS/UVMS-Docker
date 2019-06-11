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
import eu.europa.ec.fisheries.uvms.commons.rest.dto.ResponseDto;
import eu.europa.ec.fisheries.uvms.docker.validation.common.AbstractHelper;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;

public class CustomRuleHelper extends AbstractHelper {

    public static CustomRuleType createCustomRule(CustomRuleType customRule) {
         ResponseDto<CustomRuleType> responseDto = getWebTarget()
                .path("movement-rules/rest/customrules")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getValidJwtToken())
                .post(Entity.json(customRule), new GenericType<ResponseDto<CustomRuleType>>() {});
         return responseDto.getData();
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
        ResponseDto<List<CustomRuleType>> responseDto = getWebTarget()
                .path("movement-rules/rest/customrules/listAll")
                .path(CustomRuleBuilder.DEFAULT_USER)
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getValidJwtToken())
                .get(new GenericType<ResponseDto<List<CustomRuleType>>>() {});
        List<CustomRuleType> customRules = responseDto.getData();
        
        for (CustomRuleType customRuleType : customRules) {
            removeCustomRule(customRuleType.getGuid());
        }
    }
    
    public static void assertRuleTriggered(CustomRuleType rule, OffsetDateTime dateFrom) {
        ResponseDto<CustomRuleType> responseDto = getWebTarget()
                .path("movement-rules/rest/customrules")
                .path(rule.getGuid())
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getValidJwtToken())
                .get(new GenericType<ResponseDto<CustomRuleType>>() {});
        CustomRuleType fetchedCustomRule = responseDto.getData();
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss Z");
        OffsetDateTime lastTriggered = OffsetDateTime.parse(fetchedCustomRule.getLastTriggered(), formatter);
        assertNotNull(lastTriggered);

        assertTrue(lastTriggered.isAfter(dateFrom) || compareWithoutMillis(lastTriggered,dateFrom));
    }

    private static boolean compareWithoutMillis(OffsetDateTime a, OffsetDateTime b) {
        return a.getYear() == b.getYear() && a.getMonth().equals(b.getMonth()) && a.getDayOfMonth() == b.getDayOfMonth() &&
                a.getHour() == b.getHour() && a.getMinute() == b.getMinute() && a.getSecond() == b.getSecond();
    }
    
    public static void assertRuleNotTriggered(CustomRuleType rule) {
        ResponseDto<CustomRuleType> responseDto = getWebTarget()
                .path("movement-rules/rest/customrules")
                .path(rule.getGuid())
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getValidJwtToken())
                .get(new GenericType<ResponseDto<CustomRuleType>>() {});
        CustomRuleType fetchedCustomRule = responseDto.getData();
        assertThat(fetchedCustomRule.getLastTriggered(), is(nullValue()));
    }

    public static void pollTicketCreated() {
        getWebTarget()
            .path("movement-rules/activity/ticket")
            .request(MediaType.APPLICATION_JSON)
            .get();
    }
}
