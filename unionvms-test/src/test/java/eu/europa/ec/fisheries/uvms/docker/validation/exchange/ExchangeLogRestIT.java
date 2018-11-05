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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import org.apache.http.HttpResponse;
import org.apache.http.client.fluent.Request;
import org.junit.Test;
import eu.europa.ec.fisheries.schema.exchange.v1.ExchangeListCriteria;
import eu.europa.ec.fisheries.schema.exchange.v1.ExchangeListCriteriaPair;
import eu.europa.ec.fisheries.schema.exchange.v1.ExchangeListPagination;
import eu.europa.ec.fisheries.schema.exchange.v1.ExchangeListQuery;
import eu.europa.ec.fisheries.schema.exchange.v1.ExchangeLogStatusTypeType;
import eu.europa.ec.fisheries.schema.exchange.v1.SearchField;
import eu.europa.ec.fisheries.uvms.asset.client.model.AssetDTO;
import eu.europa.ec.fisheries.uvms.docker.validation.asset.AssetTestHelper;
import eu.europa.ec.fisheries.uvms.docker.validation.common.AbstractRest;
import eu.europa.ec.fisheries.uvms.docker.validation.mobileterminal.MobileTerminalTestHelper;

/**
 * The Class ExchangeLogRestIT.
 */
public class ExchangeLogRestIT extends AbstractRest {

	/**
	 * Gets the log list by criteria test.
	 *
	 * @return the log list by criteria test
	 * @throws Exception the exception
	 */
	@Test
	public void getLogListByCriteriaTest() throws Exception {
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
		
		final HttpResponse response = Request.Post(getBaseUrl() + "exchange/rest/exchange/list")
				.bodyByteArray(writeValueAsString(exchangeListQuery).getBytes())
				.setHeader("Content-Type", "application/json").setHeader("Authorization", getValidJwtToken()).execute()
				.returnResponse();
		Map<String, Object> dataMap = checkSuccessResponseReturnMap(response);
		List<List> logList = (List) dataMap.get("logList");
		assertFalse(logList.isEmpty());
	}

	/**
	 * Gets the poll status query test.
	 *
	 * @return the poll status query test
	 * @throws Exception the exception
	 */
	@Test
	public void getPollStatusQueryTest() throws Exception {
		PollQuery pollQuery = new PollQuery();
		pollQuery.setStatus(ExchangeLogStatusTypeType.SUCCESSFUL);
		Date oldDate = new Date();
		oldDate.setTime( oldDate.getTime() - (long)10*1000*60*60*24 );
		pollQuery.setStatusFromDate(formatDateAsUTC(oldDate));
		pollQuery.setStatusToDate(formatDateAsUTC(new Date()));
			
		final HttpResponse response = Request.Post(getBaseUrl() + "exchange/rest/exchange/poll")
				.bodyByteArray(writeValueAsString(pollQuery).getBytes())
				.setHeader("Content-Type", "application/json").setHeader("Authorization", getValidJwtToken()).execute()
				.returnResponse();
		List dataList = checkSuccessResponseReturnType(response,List.class);

	}
	
	private String formatDateAsUTC(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");
		sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
		return sdf.format(date);
	}
	
	/**
	 * Gets the poll status ref guid test.
	 *
	 * @return the poll status ref guid test
	 * @throws Exception the exception
	 */
	@Test
	public void getPollStatusRefGuidTest() throws Exception {
		AssetDTO testAsset = AssetTestHelper.createTestAsset();
		Map<String, Object> programPollDataMap = MobileTerminalTestHelper.createPoll_Helper(testAsset);
		ArrayList sendPolls = (ArrayList) programPollDataMap.get("sentPolls");
		String uid = (String) sendPolls.get(0);

		final HttpResponse response = Request.Get(getBaseUrl() + "exchange/rest/exchange/poll/" + uid)
				.setHeader("Content-Type", "application/json").setHeader("Authorization", getValidJwtToken()).execute()
				.returnResponse();

		Map<String, Object> dataMap = checkSuccessResponseReturnMap(response);
		assertNotNull(uid,dataMap.get("guid"));

	}

	/**
	 * Gets the exchange log by guid test.
	 *
	 * @return the exchange log by guid test
	 * @throws Exception the exception
	 */
	@Test
	public void getExchangeLogByGuidTest() throws Exception {
		String guid=null;
		{
			AssetDTO testAsset = AssetTestHelper.createTestAsset();
			Map<String, Object> programPollDataMap = MobileTerminalTestHelper.createPoll_Helper(testAsset);
			ArrayList sendPolls = (ArrayList) programPollDataMap.get("sentPolls");
			String uid = (String) sendPolls.get(0);

			final HttpResponse response = Request.Get(getBaseUrl() + "exchange/rest/exchange/poll/" + uid)
					.setHeader("Content-Type", "application/json").setHeader("Authorization", getValidJwtToken()).execute()
					.returnResponse();

			Map<String, Object> dataMap = checkSuccessResponseReturnMap(response);
			guid = (String) dataMap.get("guid");

		}
		
		final HttpResponse response = Request.Get(getBaseUrl() + "exchange/rest/exchange/" + guid)
				.setHeader("Content-Type", "application/json").setHeader("Authorization", getValidJwtToken()).execute()
				.returnResponse();

		Map<String, Object> dataMap = checkSuccessResponseReturnMap(response);
		assertEquals("SEND_POLL",dataMap.get("type"));

	}

	
}
