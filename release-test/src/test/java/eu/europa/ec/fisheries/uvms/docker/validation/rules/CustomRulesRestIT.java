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

import java.util.Map;

import org.apache.http.HttpResponse;
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
	@Test
	@Ignore
	public void createCustomRuleTest() throws Exception {
		CustomRuleType customRuleType = new CustomRuleType();
		final HttpResponse response = Request.Post(getBaseUrl() + "rules/rest/customrules/")
				.setHeader("Content-Type", "application/json").setHeader("Authorization", getValidJwtToken())
				.bodyByteArray(writeValueAsString(customRuleType).getBytes()).execute().returnResponse();
		Map<String, Object> dataMap = checkSuccessResponseReturnMap(response);
	}

	/**
	 * Gets the custom rules by user.
	 *
	 * @return the custom rules by user
	 * @throws Exception the exception
	 */
	@Test
	@Ignore
	public void getCustomRulesByUser() throws Exception {
		CustomRuleQuery CustomRuleQuery = new CustomRuleQuery();
		final HttpResponse response = Request.Get(getBaseUrl() + "rules/rest/customrules/listAll/{userName}")
				.setHeader("Content-Type", "application/json").setHeader("Authorization", getValidJwtToken()).execute()
				.returnResponse();
		Map<String, Object> dataMap = checkSuccessResponseReturnMap(response);
	}

	/**
	 * Gets the custom rules by query test.
	 *
	 * @return the custom rules by query test
	 * @throws Exception the exception
	 */
	@Test
	@Ignore
	public void getCustomRulesByQueryTest() throws Exception {
		CustomRuleQuery CustomRuleQuery = new CustomRuleQuery();
		final HttpResponse response = Request.Post(getBaseUrl() + "rules/rest/customrules/listByQuery")
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
	@Test
	@Ignore
	public void getCustomRuleByGuidTest() throws Exception {
		final HttpResponse response = Request.Get(getBaseUrl() + "rules/rest/customrules/{guid}")
				.setHeader("Content-Type", "application/json").setHeader("Authorization", getValidJwtToken()).execute()
				.returnResponse();
		Map<String, Object> dataMap = checkSuccessResponseReturnMap(response);
	}

	/**
	 * Delete custom rule test.
	 *
	 * @throws Exception the exception
	 */
	@Test
	@Ignore
	public void deleteCustomRuleTest() throws Exception {
		final HttpResponse response = Request.Delete(getBaseUrl() + "rules/rest/customrules/{guid}")
				.setHeader("Content-Type", "application/json").setHeader("Authorization", getValidJwtToken()).execute()
				.returnResponse();
		Map<String, Object> dataMap = checkSuccessResponseReturnMap(response);
	}

	/**
	 * Update subscription test.
	 *
	 * @throws Exception the exception
	 */
	@Test
	@Ignore
	public void updateSubscriptionTest() throws Exception {
		UpdateSubscriptionType updateSubscriptionType = new UpdateSubscriptionType();
		final HttpResponse response = Request.Post(getBaseUrl() + "rules/rest/customrules/subscription")
				.setHeader("Content-Type", "application/json").setHeader("Authorization", getValidJwtToken())
				.bodyByteArray(writeValueAsString(updateSubscriptionType).getBytes()).execute().returnResponse();
		Map<String, Object> dataMap = checkSuccessResponseReturnMap(response);
	}

	/**
	 * Update test.
	 *
	 * @throws Exception the exception
	 */
	@Test
	@Ignore
	public void updateTest() throws Exception {
		CustomRuleType customRuleType = new CustomRuleType();
		final HttpResponse response = Request.Put(getBaseUrl() + "rules/rest/customrules/")
				.setHeader("Content-Type", "application/json").setHeader("Authorization", getValidJwtToken())
				.bodyByteArray(writeValueAsString(customRuleType).getBytes()).execute().returnResponse();
		Map<String, Object> dataMap = checkSuccessResponseReturnMap(response);
	}

}
