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

public class CustomRulesRestIT extends AbstractRest {

	@Test
	public void createCustomRuleTest() {
		ResponseDto<CustomRuleType> response = createAndPersistCustomRule();
		assertEquals(200, response.getCode());
		assertNotNull(response.getData().getGuid());
	}

	// Todo: Replace 511 status code with '400 Bad Request' or '422 Unprocessable Entity' in MovementRules
	@Test
	public void createCustomRuleTest_WillFailWithInvalidEntity() {
		CustomRuleType customRuleType = new CustomRuleType();
		ResponseDto response = getWebTarget()
				.path("movement-rules/rest/customrules")
				.request(MediaType.APPLICATION_JSON)
				.header(HttpHeaders.AUTHORIZATION, getValidJwtToken())
				.post(Entity.json(customRuleType), ResponseDto.class);
		assertEquals(511, response.getCode());
	}

	@Test
	public void getCustomRulesByUser() {
		createAndPersistCustomRule();
		ResponseDto<List<CustomRuleType>> response = getWebTarget()
				.path("movement-rules/rest/customrules/listAll")
				.path("vms_admin_com")
				.request(MediaType.APPLICATION_JSON)
				.header(HttpHeaders.AUTHORIZATION, getValidJwtToken())
				.get(new GenericType<ResponseDto<List<CustomRuleType>>>(){});

		List<CustomRuleType> customRuleList = response.getData();
		assertFalse(customRuleList.isEmpty());
	}

	@Test
	public void getCustomRulesByQueryTest() {
		ResponseDto<CustomRuleType> created = createAndPersistCustomRule();

		CustomRuleQuery CustomRuleQuery = new CustomRuleQuery();
		ListPagination lp = new ListPagination();
		lp.setListSize(10);
		lp.setPage(1);
		CustomRuleQuery.setPagination(lp);
		CustomRuleListCriteria crlc = new CustomRuleListCriteria();
		crlc.setKey(CustomRuleSearchKey.GUID);
		crlc.setValue(created.getData().getGuid());
		CustomRuleQuery.getCustomRuleSearchCriteria().add(crlc);
		CustomRuleQuery.setDynamic(true);

		ResponseDto<GetCustomRuleListByQueryResponse> response = getWebTarget()
				.path("movement-rules/rest/customrules/listByQuery")
				.request(MediaType.APPLICATION_JSON)
				.header(HttpHeaders.AUTHORIZATION, getValidJwtToken())
				.post(Entity.json(CustomRuleQuery), new GenericType<ResponseDto<GetCustomRuleListByQueryResponse>>(){});

		GetCustomRuleListByQueryResponse data = response.getData();
		List<CustomRuleType> customRules = data.getCustomRules();
		boolean found = customRules.stream().anyMatch(cr -> cr.getGuid().equals(created.getData().getGuid()));
		assertTrue(found);
	}

	@Test
	public void getCustomRuleByGuidTest() {
		ResponseDto<CustomRuleType> created = createAndPersistCustomRule();
		ResponseDto<CustomRuleType> response = getWebTarget()
				.path("movement-rules/rest/customrules")
				.path(created.getData().getGuid())
				.request(MediaType.APPLICATION_JSON)
				.header(HttpHeaders.AUTHORIZATION, getValidJwtToken())
				.get(new GenericType<ResponseDto<CustomRuleType>>(){});
		assertEquals(200, response.getCode());
		assertEquals(created.getData().getGuid(), response.getData().getGuid());
	}

	// Todo: MovementRules API should return e.g. 204 or 200 with empty data as opposed to 500 which
	// indicates a server error. This test will be updated after updating MovementRules API
	@Test
	public void getCustomRuleByGuidTest_WillFailWithInvalidUUID() {
		ResponseDto<CustomRuleType> response = getWebTarget()
				.path("movement-rules/rest/customrules")
				.path(UUID.randomUUID().toString())
				.request(MediaType.APPLICATION_JSON)
				.header(HttpHeaders.AUTHORIZATION, getValidJwtToken())
				.get(new GenericType<ResponseDto<CustomRuleType>>(){});
		assertEquals(500, response.getCode());
	}

	@Test
	public void deleteCustomRuleTest() {
		ResponseDto<CustomRuleType> created = createAndPersistCustomRule();
		ResponseDto<CustomRuleType> response = getWebTarget()
				.path("movement-rules/rest/customrules")
				.path(created.getData().getGuid())
				.request(MediaType.APPLICATION_JSON)
				.header(HttpHeaders.AUTHORIZATION, getValidJwtToken())
				.delete(new GenericType<ResponseDto<CustomRuleType>>(){});
		assertEquals(200, response.getCode());
	}

	// Todo: MovementRules API should return '204 Co Content' if there weren't a Server error
	// This test will be updated after updating MovementRules API
	@Test
	public void deleteCustomRuleTest_WillFailWithInvalidUUID() {
		ResponseDto<CustomRuleType> response = getWebTarget()
				.path("movement-rules/rest/customrules")
				.path(UUID.randomUUID().toString())
				.request(MediaType.APPLICATION_JSON)
				.header(HttpHeaders.AUTHORIZATION, getValidJwtToken())
				.delete(new GenericType<ResponseDto<CustomRuleType>>(){});
		assertEquals(500, response.getCode());
	}

	@Test
	public void updateCustomRuleTest() {
		ResponseDto<CustomRuleType> created = createAndPersistCustomRule();
		CustomRuleType customRuleType = created.getData();
		customRuleType.setName("NEW_TEST_NAME");
		ResponseDto<CustomRuleType> response = getWebTarget()
				.path("movement-rules/rest/customrules")
				.request(MediaType.APPLICATION_JSON)
				.header(HttpHeaders.AUTHORIZATION, getValidJwtToken())
				.put(Entity.json(customRuleType), new GenericType<ResponseDto<CustomRuleType>>(){});
		assertEquals(200, response.getCode());
		assertEquals("NEW_TEST_NAME", response.getData().getName());
	}

	// Todo: Replace 511 status code with '400 Bad Request' or '422 Unprocessable Entity' in MovementRules
	@Test
	public void updateCustomRuleTest_WillFailWithInvalidEntity() {
		CustomRuleType customRuleType = new CustomRuleType();
		ResponseDto<String> response = getWebTarget()
				.path("movement-rules/rest/customrules")
				.request(MediaType.APPLICATION_JSON)
				.header(HttpHeaders.AUTHORIZATION, getValidJwtToken())
				.put(Entity.json(customRuleType), new GenericType<ResponseDto<String>>(){});
		assertEquals("Custom rule data is not correct", response.getData());
		assertEquals(511, response.getCode());
	}

	// Todo: Replace 500 status code with '400 Bad Request' or '422 Unprocessable Entity' in MovementRules
	@Test
	public void updateSubscriptionTest() {
		UpdateSubscriptionType updateSubscriptionType = new UpdateSubscriptionType();
		ResponseDto<CustomRuleType> response = getWebTarget()
				.path("movement-rules/rest/customrules/subscription")
				.request(MediaType.APPLICATION_JSON)
				.header(HttpHeaders.AUTHORIZATION, getValidJwtToken())
				.post(Entity.json(updateSubscriptionType), new GenericType<ResponseDto<CustomRuleType>>(){});
		assertEquals(500, response.getCode());
	}

	private ResponseDto<CustomRuleType> createAndPersistCustomRule() {
		CustomRuleType customRuleType = CustomRulesTestHelper.getCompleteNewCustomRule();
		return getWebTarget()
				.path("movement-rules/rest/customrules")
				.request(MediaType.APPLICATION_JSON)
				.header(HttpHeaders.AUTHORIZATION, getValidJwtToken())
				.post(Entity.json(customRuleType), new GenericType<ResponseDto<CustomRuleType>>(){});
	}
}
