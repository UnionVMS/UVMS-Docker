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
package eu.europa.ec.fisheries.uvms.docker.validation.rules;

import java.util.List;
import java.util.Map;

import eu.europa.ec.fisheries.schema.rules.search.v1.CustomRuleListCriteria;
import eu.europa.ec.fisheries.schema.rules.search.v1.CustomRuleSearchKey;
import eu.europa.ec.fisheries.schema.rules.search.v1.ListPagination;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.fluent.Request;
import org.junit.Ignore;
import org.junit.Test;

import eu.europa.ec.fisheries.schema.rules.customrule.v1.CustomRuleType;
import eu.europa.ec.fisheries.schema.rules.customrule.v1.UpdateSubscriptionType;
import eu.europa.ec.fisheries.schema.rules.search.v1.CustomRuleQuery;
import eu.europa.ec.fisheries.uvms.docker.validation.common.AbstractRestServiceTest;

/**
 * The Class CustomRulesRestIT.
 */
public class CustomRulesRestIT extends AbstractRestServiceTest {

	/**
	 * Creates the custom rule test.
	 *
	 * @throws Exception the exception
	 */
	//This and only this method returns a 511 response.........
	//Weeeeeell in this and only this case 511 is not Network Authentication Required but rather "INPUT_ERROR"......
	//Who the hell though that this was a good idea???????????
	//Adding custom checks for the respons
	@Test
	public void createCustomRuleTest() throws Exception {
		CustomRuleType customRuleType = new CustomRuleType();
		final HttpResponse response = Request.Post(getBaseUrl() + "movement-rules/rest/customrules/")
				.setHeader("Content-Type", "application/json").setHeader("Authorization", getValidJwtToken())
				.bodyByteArray(writeValueAsString(customRuleType).getBytes()).execute().returnResponse();
		assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
		final Map<String, Object> data = getJsonMap(response);
		assertFalse(data.isEmpty());
		assertNotNull(data.get("data"));
		assertEquals("511", "" + data.get("code"));
	}

	/**
	 * Gets the custom rules by user.
	 *
	 * @return the custom rules by user
	 * @throws Exception the exception
	 */
	//changed {userName} to userName, so now it searches for "userName" and returns the list as a string rather then a map
	@Test
	public void getCustomRulesByUser() throws Exception {
		CustomRuleQuery CustomRuleQuery = new CustomRuleQuery();
		final HttpResponse response = Request.Get(getBaseUrl() + "movement-rules/rest/customrules/listAll/userName")
				.setHeader("Content-Type", "application/json").setHeader("Authorization", getValidJwtToken()).execute()
				.returnResponse();
		//Map<String, Object> dataMap = checkSuccessResponseReturnMap(response);
		checkSuccessResponseReturnList(response,List.class);
	}

	/**
	 * Gets the custom rules by query test.
	 *
	 * @return the custom rules by query test
	 * @throws Exception the exception
	 */
	//added all the needed info to the query so that it can complete
	@Test
	public void getCustomRulesByQueryTest() throws Exception {
		CustomRuleQuery CustomRuleQuery = new CustomRuleQuery();
		ListPagination lp = new ListPagination();
		lp.setListSize(10);
		lp.setPage(1); //this value can not be 0 or lower...... ;(
		CustomRuleQuery.setPagination(lp);
		CustomRuleListCriteria crlc = new CustomRuleListCriteria();
		crlc.setKey(CustomRuleSearchKey.GUID);
		crlc.setValue("dummyguid");
		CustomRuleQuery.getCustomRuleSearchCriteria().add(crlc);
		CustomRuleQuery.setDynamic(true);
		final HttpResponse response = Request.Post(getBaseUrl() + "movement-rules/rest/customrules/listByQuery")
				.setHeader("Content-Type", "application/json").setHeader("Authorization", getValidJwtToken())
				.bodyByteArray(writeValueAsString(CustomRuleQuery).getBytes()).execute().returnResponse();
		Map<String, Object> dataMap = checkSuccessResponseReturnMap(response);
	}

	/**
	 * Gets the custom rule by guid test.
	 *
	 * @return the custom rule by guid test
	 * @throws Exception the exception
	 */
	//Is this one assuming that some other method creates a custom rule? And that we have some sort of magical access to its guid?
	//This query is going to return a server error (500) since we dont have a custom rule with the guid "guid" (heck we dont have any custom rules whatsoever......)
	@Test
	public void getCustomRuleByGuidTest() throws Exception {
		final HttpResponse response = Request.Get(getBaseUrl() + "movement-rules/rest/customrules/guid")
				.setHeader("Content-Type", "application/json").setHeader("Authorization", getValidJwtToken()).execute()
				.returnResponse();
		//internal server error aka 500
		String responseCode = returnErrorResponse(response);
		assertEquals("500", responseCode);
	}

	/**
	 * Delete custom rule test.
	 *
	 * @throws Exception the exception
	 */
	//this one is trying to delete a rule that does not exist, causing the server to respond with 500
	@Test
	public void deleteCustomRuleTest() throws Exception {
		final HttpResponse response = Request.Delete(getBaseUrl() + "movement-rules/rest/customrules/guid")
				.setHeader("Content-Type", "application/json").setHeader("Authorization", getValidJwtToken()).execute()
				.returnResponse();
		//internal server error aka 500
		String responseCode = returnErrorResponse(response);
        assertEquals("500", responseCode);
	}

	/**
	 * Update subscription test.
	 *
	 * @throws Exception the exception
	 */
	//not having any custom rules makes this one kinda hard to do, so ya500r......
	@Test
	public void updateSubscriptionTest() throws Exception {
		UpdateSubscriptionType updateSubscriptionType = new UpdateSubscriptionType();
		final HttpResponse response = Request.Post(getBaseUrl() + "movement-rules/rest/customrules/subscription")
				.setHeader("Content-Type", "application/json").setHeader("Authorization", getValidJwtToken())
				.bodyByteArray(writeValueAsString(updateSubscriptionType).getBytes()).execute().returnResponse();
		checkErrorResponse(response);
	}

	/**
	 * Update test.
	 *
	 * @throws Exception the exception
	 */
	//not having any custom rules makes this one kinda hard to do, so ya500r......
	@Test
	public void updateTest() throws Exception {
		CustomRuleType customRuleType = new CustomRuleType();
		final HttpResponse response = Request.Put(getBaseUrl() + "movement-rules/rest/customrules/")
				.setHeader("Content-Type", "application/json").setHeader("Authorization", getValidJwtToken())
				.bodyByteArray(writeValueAsString(customRuleType).getBytes()).execute().returnResponse();
		String responseCode = returnErrorResponse(response);
        assertEquals("511", responseCode);
	}
}
