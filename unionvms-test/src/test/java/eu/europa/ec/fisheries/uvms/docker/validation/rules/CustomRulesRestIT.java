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
import java.util.UUID;

import com.fasterxml.jackson.databind.node.ObjectNode;
import eu.europa.ec.fisheries.schema.movementrules.module.v1.GetCustomRuleListByQueryResponse;
import eu.europa.ec.fisheries.uvms.commons.rest.dto.ResponseDto;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.fluent.Request;
import org.junit.Test;
import eu.europa.ec.fisheries.schema.movementrules.customrule.v1.CustomRuleType;
import eu.europa.ec.fisheries.schema.movementrules.customrule.v1.UpdateSubscriptionType;
import eu.europa.ec.fisheries.schema.movementrules.search.v1.CustomRuleListCriteria;
import eu.europa.ec.fisheries.schema.movementrules.search.v1.CustomRuleQuery;
import eu.europa.ec.fisheries.schema.movementrules.search.v1.CustomRuleSearchKey;
import eu.europa.ec.fisheries.schema.movementrules.search.v1.ListPagination;
import eu.europa.ec.fisheries.uvms.docker.validation.common.AbstractRest;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

public class CustomRulesRestIT extends AbstractRest {

	@Test
	public void createCustomRuleTest() {
		CustomRuleType response = createAndPersistCustomRule();
		assertNotNull(response.getGuid());
	}

	@Test
	public void createCustomRuleTest_WillFailWithInvalidEntity() {
		CustomRuleType customRuleType = new CustomRuleType();
		Response response = getWebTarget()
				.path("movement-rules/rest/customrules")
				.request(MediaType.APPLICATION_JSON)
				.header(HttpHeaders.AUTHORIZATION, getValidJwtToken())
				.post(Entity.json(customRuleType), Response.class);
		assertEquals(Status.BAD_REQUEST.getStatusCode(), response.getStatus());
	}

	@Test
	public void getCustomRulesByUser() {
		createAndPersistCustomRule();
		List<CustomRuleType> response = getWebTarget()
				.path("movement-rules/rest/customrules/listAll")
				.path("vms_admin_se")
				.request(MediaType.APPLICATION_JSON)
				.header(HttpHeaders.AUTHORIZATION, getValidJwtToken())
				.get(new GenericType<List<CustomRuleType>>(){});

		assertFalse(response.isEmpty());
	}

	@Test
	public void getCustomRulesByQueryTest() {
		CustomRuleType created = createAndPersistCustomRule();

		CustomRuleQuery CustomRuleQuery = new CustomRuleQuery();
		ListPagination lp = new ListPagination();
		lp.setListSize(10);
		lp.setPage(1);
		CustomRuleQuery.setPagination(lp);
		CustomRuleListCriteria crlc = new CustomRuleListCriteria();
		crlc.setKey(CustomRuleSearchKey.GUID);
		crlc.setValue(created.getGuid());
		CustomRuleQuery.getCustomRuleSearchCriteria().add(crlc);
		CustomRuleQuery.setDynamic(true);

		GetCustomRuleListByQueryResponse response = getWebTarget()
				.path("movement-rules/rest/customrules/listByQuery")
				.request(MediaType.APPLICATION_JSON)
				.header(HttpHeaders.AUTHORIZATION, getValidJwtToken())
				.post(Entity.json(CustomRuleQuery), GetCustomRuleListByQueryResponse.class);

		List<CustomRuleType> customRules = response.getCustomRules();
		boolean found = customRules.stream().anyMatch(cr -> cr.getGuid().equals(created.getGuid()));
		assertTrue(found);
	}

	@Test
	public void getCustomRuleByGuidTest() {
		CustomRuleType created = createAndPersistCustomRule();
		CustomRuleType fetched = getWebTarget()
				.path("movement-rules/rest/customrules")
				.path(created.getGuid())
				.request(MediaType.APPLICATION_JSON)
				.header(HttpHeaders.AUTHORIZATION, getValidJwtToken())
				.get(CustomRuleType.class);
		assertEquals(created.getGuid(), fetched.getGuid());
	}

	@Test
	public void getCustomRuleByGuidTest_WillFailWithInvalidUUID() {
		Response response = getWebTarget()
				.path("movement-rules/rest/customrules")
				.path(UUID.randomUUID().toString())
				.request(MediaType.APPLICATION_JSON)
				.header(HttpHeaders.AUTHORIZATION, getValidJwtToken())
				.get();
		assertEquals(Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());

		CustomRuleType customRuleType = response.readEntity(CustomRuleType.class);
		assertNull(customRuleType.getGuid());
	}

	@Test
	public void deleteCustomRuleTest() {
		CustomRuleType created = createAndPersistCustomRule();
		Response response = getWebTarget()
				.path("movement-rules/rest/customrules")
				.path(created.getGuid())
				.request(MediaType.APPLICATION_JSON)
				.header(HttpHeaders.AUTHORIZATION, getValidJwtToken())
				.delete();
		assertEquals(Status.OK.getStatusCode(), response.getStatus());

		CustomRuleType customRuleType = response.readEntity(CustomRuleType.class);
		assertFalse(customRuleType.isActive());
	}

	@Test
	public void deleteCustomRuleTest_WillFailWithInvalidUUID() {
		Response response = getWebTarget()
				.path("movement-rules/rest/customrules")
				.path(UUID.randomUUID().toString())
				.request(MediaType.APPLICATION_JSON)
				.header(HttpHeaders.AUTHORIZATION, getValidJwtToken())
				.delete();
		assertEquals(Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
	}

	@Test
	public void updateCustomRuleTest() {
		CustomRuleType created = createAndPersistCustomRule();
		created.setName("NEW_TEST_NAME");
		CustomRuleType updated = getWebTarget()
				.path("movement-rules/rest/customrules")
				.request(MediaType.APPLICATION_JSON)
				.header(HttpHeaders.AUTHORIZATION, getValidJwtToken())
				.put(Entity.json(created), CustomRuleType.class);

		assertEquals("NEW_TEST_NAME", updated.getName());
	}

	@Test
	public void updateCustomRuleTest_WillFailWithInvalidEntity() {
		CustomRuleType customRuleType = new CustomRuleType();
		Response response = getWebTarget()
				.path("movement-rules/rest/customrules")
				.request(MediaType.APPLICATION_JSON)
				.header(HttpHeaders.AUTHORIZATION, getValidJwtToken())
				.put(Entity.json(customRuleType));
		assertEquals(Status.BAD_REQUEST.getStatusCode(), response.getStatus());
	}

	@Test
	public void updateSubscriptionTest() {
		UpdateSubscriptionType updateSubscriptionType = new UpdateSubscriptionType();
		Response response = getWebTarget()
				.path("movement-rules/rest/customrules/subscription")
				.request(MediaType.APPLICATION_JSON)
				.header(HttpHeaders.AUTHORIZATION, getValidJwtToken())
				.post(Entity.json(updateSubscriptionType));
		assertEquals(Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
	}

	private CustomRuleType createAndPersistCustomRule() {
		CustomRuleType customRuleType = CustomRulesTestHelper.getCompleteNewCustomRule();
		return getWebTarget()
				.path("movement-rules/rest/customrules")
				.request(MediaType.APPLICATION_JSON)
				.header(HttpHeaders.AUTHORIZATION, getValidJwtToken())
				.post(Entity.json(customRuleType), CustomRuleType.class);
	}
}
