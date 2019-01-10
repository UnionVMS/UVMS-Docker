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

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import org.hamcrest.CoreMatchers;
import eu.europa.ec.fisheries.schema.movementrules.search.v1.AlarmListCriteria;
import eu.europa.ec.fisheries.schema.movementrules.search.v1.AlarmQuery;
import eu.europa.ec.fisheries.schema.movementrules.search.v1.AlarmSearchKey;
import eu.europa.ec.fisheries.schema.movementrules.search.v1.ListPagination;
import eu.europa.ec.fisheries.uvms.docker.validation.common.AbstractHelper;
import eu.europa.ec.fisheries.uvms.docker.validation.movement.model.AlarmListResponseDto;
import eu.europa.ec.fisheries.uvms.docker.validation.movement.model.AlarmReport;

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
    
    public static AlarmReport getLatestOpenAlarmReportSince(ZonedDateTime timestamp) {
        AlarmQuery query = new AlarmQuery();
        ListPagination pagination = new ListPagination();
        pagination.setListSize(100);
        pagination.setPage(1);
        query.setPagination(pagination);
        query.setDynamic(true);
        AlarmListCriteria criteria = new AlarmListCriteria();
        criteria.setKey(AlarmSearchKey.FROM_DATE);
        criteria.setValue(timestamp.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss Z")));
        query.getAlarmSearchCriteria().add(criteria);
        AlarmListCriteria criteria2 = new AlarmListCriteria();
        criteria2.setKey(AlarmSearchKey.STATUS);
        criteria2.setValue("OPEN");
        query.getAlarmSearchCriteria().add(criteria2);

        AlarmListResponseDto alarmResponse = getWebTarget()
                .path("movement/rest/alarms/list")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getValidJwtToken())
                .post(Entity.json(query), AlarmListResponseDto.class);
        

        assertThat(alarmResponse, CoreMatchers.is(CoreMatchers.notNullValue()));
        List<AlarmReport> alarms = alarmResponse.getAlarmList();
        alarms.sort((a1, a2) -> a2.getCreatedDate().compareTo(a1.getCreatedDate()));
        return alarms.get(0);
    }
}
