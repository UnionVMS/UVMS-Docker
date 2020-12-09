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
package eu.europa.ec.fisheries.uvms.docker.validation.exchange;

import static org.hamcrest.MatcherAssert.assertThat;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import org.hamcrest.CoreMatchers;
import eu.europa.ec.fisheries.schema.exchange.v1.ExchangeListCriteria;
import eu.europa.ec.fisheries.schema.exchange.v1.ExchangeListCriteriaPair;
import eu.europa.ec.fisheries.schema.exchange.v1.ExchangeListPagination;
import eu.europa.ec.fisheries.schema.exchange.v1.ExchangeListQuery;
import eu.europa.ec.fisheries.schema.exchange.v1.SearchField;
import eu.europa.ec.fisheries.uvms.docker.validation.common.AbstractHelper;
import eu.europa.ec.fisheries.uvms.docker.validation.exchange.dto.ExchangeLogDto;
import eu.europa.ec.fisheries.uvms.docker.validation.exchange.dto.ListQueryResponse;

public class ExchangeHelper extends AbstractHelper {

    private ExchangeHelper() {}

    public static ExchangeLogDto getIncomingExchangeLogByTypeGUID(String guid) {
        return getExchangeLogByTypeGUID("INCOMING", guid);
    }

    public static ExchangeLogDto getOutgoingExchangeLogByTypeGUID(String guid) {
        return getExchangeLogByTypeGUID("OUTGOING", guid);
    }

    private static ExchangeLogDto getExchangeLogByTypeGUID(String direction, String guid) {
        ExchangeListQuery exchangeListQuery = new ExchangeListQuery();
        ExchangeListCriteria exchangeListCriteria = new ExchangeListCriteria();
        exchangeListCriteria.setIsDynamic(true);
        ExchangeListCriteriaPair exchangeListCriteriaPair = new ExchangeListCriteriaPair();
        exchangeListCriteriaPair.setKey(SearchField.MESSAGE_DIRECTION);
        exchangeListCriteriaPair.setValue(direction);
        exchangeListCriteria.getCriterias().add(exchangeListCriteriaPair);
        ExchangeListCriteriaPair exchangeListCriteriaPair2 = new ExchangeListCriteriaPair();
        exchangeListCriteriaPair2.setKey(SearchField.TYPE_GUID);
        exchangeListCriteriaPair2.setValue(guid);
        exchangeListCriteria.getCriterias().add(exchangeListCriteriaPair2);
        
        exchangeListQuery.setExchangeSearchCriteria(exchangeListCriteria);
        ExchangeListPagination exchangeListPagination = new ExchangeListPagination();
        exchangeListPagination.setPage(1);
        exchangeListPagination.setListSize(100);
        exchangeListQuery.setPagination(exchangeListPagination);

        ListQueryResponse listQueryResponse = getWebTarget()
                .path("exchange/rest/exchange/list")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getValidJwtToken())
                .post(Entity.json(exchangeListQuery), ListQueryResponse.class);

        assertThat(listQueryResponse, CoreMatchers.is(CoreMatchers.notNullValue()));
        assertThat(listQueryResponse.getLogList().size(), CoreMatchers.is(1));
        return listQueryResponse.getLogList().get(0);
    }

    public static String getExchangeLogMessage(String guid) {
        return getWebTarget()
                .path("exchange/rest/exchange/message")
                .path(guid)
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getValidJwtToken())
                .get(String.class);
    }
}
