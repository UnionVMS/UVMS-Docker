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

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import eu.europa.ec.fisheries.uvms.docker.validation.common.AbstractHelper;

public class SanityRuleHelper extends AbstractHelper {
    
    public static long countOpenAlarms() {
        return getWebTarget()
                .path("movement/rest/alarms/countopen")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getValidJwtToken())
                .get(long.class);
    }
    
    public static void pollAlarmReportCreated() {
        getWebTarget()
            .path("movement/activity/alarm")
            .request(MediaType.APPLICATION_JSON)
            .get();
    }
}
