/*

Developed with the contribution of the European Commission - Directorate General for Maritime Affairs and Fisheries
� European Union, 2017.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can
redistribute it and/or modify it under the terms of the GNU General Public License as published by the
Free Software Foundation, either version 3 of the License, or any later version. The IFDM Suite is distributed in
the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. You should have received a
copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

*/
package eu.europa.ec.fisheries.uvms.docker.validation.exchange;

import eu.europa.ec.fisheries.schema.exchange.v1.*;
import eu.europa.ec.fisheries.uvms.asset.client.model.AssetDTO;
import eu.europa.ec.fisheries.uvms.commons.rest.dto.ResponseDto;
import eu.europa.ec.fisheries.uvms.docker.validation.asset.AssetTestHelper;
import eu.europa.ec.fisheries.uvms.docker.validation.common.AbstractRest;
import eu.europa.ec.fisheries.uvms.docker.validation.mobileterminal.MobileTerminalTestHelper;
import eu.europa.ec.fisheries.uvms.docker.validation.mobileterminal.dto.CreatePollResultDto;
import org.apache.http.HttpResponse;
import org.apache.http.client.fluent.Request;
import org.junit.Test;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import java.text.SimpleDateFormat;
import java.util.*;

public class ExchangeLogRestIT extends AbstractRest {

	@Test
	public void getLogListByCriteriaTest() {
		ExchangeListQuery exchangeListQuery = new ExchangeListQuery();
		ExchangeListCriteria exchangeListCriteria = new ExchangeListCriteria();
		exchangeListCriteria.setIsDynamic(true);
		ExchangeListCriteriaPair exchangeListCriteriaPair = new ExchangeListCriteriaPair();
		exchangeListCriteriaPair.setKey(SearchField.STATUS);
		exchangeListCriteriaPair.setValue("SUCCESSFUL");
		exchangeListCriteria.getCriterias().add(exchangeListCriteriaPair);
		
		exchangeListQuery.setExchangeSearchCriteria(exchangeListCriteria);
		ExchangeListPagination exchangeListPagination = new ExchangeListPagination();
		exchangeListPagination.setPage(1);
		exchangeListPagination.setListSize(100);
		exchangeListQuery.setPagination(exchangeListPagination);

		ResponseDto listQueryResponse = getWebTarget()
				.path("exchange/rest/exchange/list")
				.request(MediaType.APPLICATION_JSON)
				.header(HttpHeaders.AUTHORIZATION, getValidJwtToken())
				.post(Entity.json(exchangeListQuery), ResponseDto.class);

		assertNotNull(listQueryResponse);
		HashMap logListMap = (HashMap) listQueryResponse.getData();
		Object obj = logListMap.get("logList");
		ArrayList logList = (ArrayList) obj;
		assertFalse(logList.isEmpty());
	}

	@Test
	public void getPollStatusQueryTest() {
		PollQuery pollQuery = new PollQuery();
		pollQuery.setStatus(ExchangeLogStatusTypeType.SUCCESSFUL);
		Date oldDate = new Date();
		oldDate.setTime( oldDate.getTime() - (long)10*1000*60*60*24 );
		pollQuery.setStatusFromDate(formatDateAsUTC(oldDate));
		pollQuery.setStatusToDate(formatDateAsUTC(new Date()));

		ResponseDto exchangeLogStatusTypeList = getWebTarget()
				.path("exchange/rest/exchange/poll/")
				.request(MediaType.APPLICATION_JSON)
				.header(HttpHeaders.AUTHORIZATION, getValidJwtToken())
				.post(Entity.json(pollQuery), ResponseDto.class);

		assertNotNull(exchangeLogStatusTypeList);
		List logListMap = (List) exchangeLogStatusTypeList.getData();
		assertNotNull(logListMap);
		assertFalse(logListMap.isEmpty());
	}

	@Test
	public void getPollStatusRefGuidTest() {
		AssetDTO testAsset = AssetTestHelper.createTestAsset();
        CreatePollResultDto createPollResultDto = MobileTerminalTestHelper.createPoll_Helper(testAsset);
        List<String> sentPolls = createPollResultDto.getSentPolls();
        String uid = sentPolls.get(0);

		ResponseDto exchangeLogStatusType = getWebTarget()
                .path("exchange/rest/exchange/poll/" + uid)
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getValidJwtToken())
                .get(ResponseDto.class);

		assertNotNull(exchangeLogStatusType);
		HashMap guidMap = (HashMap) exchangeLogStatusType.getData();
		String guid = (String) guidMap.get("guid");
		assertNotNull(guid);
	}

	@Test
	public void getExchangeLogByGuidTest() {
        AssetDTO testAsset = AssetTestHelper.createTestAsset();
        CreatePollResultDto createPollResultDto = MobileTerminalTestHelper.createPoll_Helper(testAsset);
        List<String> sentPolls = createPollResultDto.getSentPolls();
        String uid = sentPolls.get(0);

		ResponseDto exchangeLogStatusType = getWebTarget()
                .path("exchange/rest/exchange/poll/" + uid)
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getValidJwtToken())
                .get(ResponseDto.class);

		assertNotNull(exchangeLogStatusType);
		HashMap guidMap = (HashMap) exchangeLogStatusType.getData();
        String guid = (String) guidMap.get("guid");

		ResponseDto exchangeLogType = getWebTarget()
                .path("exchange/rest/exchange/" + guid)
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getValidJwtToken())
                .get(ResponseDto.class);

		assertNotNull(exchangeLogType);
		HashMap typeMap = (HashMap) exchangeLogType.getData();
        assertEquals(LogType.SEND_POLL.value(), typeMap.get("type"));
	}

	private String formatDateAsUTC(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");
		sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
		return sdf.format(date);
	}
}
