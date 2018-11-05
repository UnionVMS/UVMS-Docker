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

import java.io.IOException;
import java.util.List;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import org.apache.http.client.ClientProtocolException;
import eu.europa.ec.fisheries.schema.movementrules.customrule.v1.SanityRuleType;
import eu.europa.ec.fisheries.uvms.commons.rest.dto.ResponseDto;
import eu.europa.ec.fisheries.uvms.docker.validation.common.AbstractHelper;

public class SanityRuleHelper extends AbstractHelper {

    public static List<SanityRuleType> getAllSanityRules() {
        ResponseDto<List<SanityRuleType>> responseDto = getWebTarget()
                .path("movement-rules/rest/sanityrules/listAll")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getValidJwtToken())
                .get(new GenericType<ResponseDto<List<SanityRuleType>>>() {});
        return responseDto.getData();
    }
    
    public static int countOpenAlarms() {
        ResponseDto<Integer> responseDto = getWebTarget()
                .path("movement-rules/rest/alarms/countopen")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getValidJwtToken())
                .get(new GenericType<ResponseDto<Integer>>() {});
        return responseDto.getData();
    }
    
    public static void pollAlarmReportCreated() {
        getWebTarget()
            .path("movement-rules/activity/alarm")
            .request(MediaType.APPLICATION_JSON)
            .get();
    }
}
