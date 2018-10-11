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
package eu.europa.ec.fisheries.uvms.docker.validation.mobileterminal;

import java.util.Map;

import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.*;
import org.apache.http.HttpResponse;
import org.apache.http.client.fluent.Request;
import org.junit.Test;

import eu.europa.ec.fisheries.uvms.asset.client.model.AssetDTO;
import eu.europa.ec.fisheries.uvms.docker.validation.asset.AssetTestHelper;

/**
 * The Class MobileTerminalRestIT.
 */

public class MobileTerminalRestIT extends AbstractMobileTerminalTest {

	/**
	 * Creates the mobile terminal test.
	 *
	 * @throws Exception
	 *             the exception
	 */
	@Test
	public void createMobileTerminalTest() throws Exception {
		MobileTerminalTestHelper.createMobileTerminalType();
	}

	/**
	 * Gets the mobile terminal by id test.
	 *
	 * @return the mobile terminal by id test
	 * @throws Exception
	 *             the exception
	 */
	@Test
	public void getMobileTerminalByIdTest() throws Exception {
		MobileTerminalType createdMobileTerminalType = MobileTerminalTestHelper.createMobileTerminalType();
		
		final HttpResponse response = Request.Get(getBaseUrl() + "asset/rest/mobileterminal/" + createdMobileTerminalType.getMobileTerminalId().getGuid())
				.setHeader("Content-Type", "application/json").setHeader("Authorization", getValidJwtToken()).execute()
				.returnResponse();
		Map<String, Object> dataMap = checkSuccessResponseReturnMap(response);
	}

	/**
	 * Update mobile terminal test.
	 *
	 * @throws Exception
	 *             the exception
	 */
	@Test
	public void updateMobileTerminalTest() throws Exception {
		MobileTerminalType createdMobileTerminalType = MobileTerminalTestHelper.createMobileTerminalType();
		
		createdMobileTerminalType.setArchived(true);
		
		final HttpResponse response = Request.Put(getBaseUrl() + "asset/rest/mobileterminal?comment=comment")
				.setHeader("Content-Type", "application/json").setHeader("Authorization", getValidJwtToken())
				.bodyByteArray(writeValueAsString(createdMobileTerminalType).getBytes()).execute().returnResponse();

		Map<String, Object> dataMap = checkSuccessResponseReturnMap(response);
	}

	/**
	 * Gets the mobile terminal list test.
	 *
	 * @return the mobile terminal list test
	 * @throws Exception
	 *             the exception
	 */
	@Test
	public void getMobileTerminalListTest() throws Exception {
		MobileTerminalListQuery queryRequest = new MobileTerminalListQuery();
		ListPagination pagination = new ListPagination();
		pagination.setListSize(100);
		pagination.setPage(1);
		queryRequest.setPagination(pagination);
		MobileTerminalSearchCriteria criteria = new MobileTerminalSearchCriteria();

		ListCriteria cr = new ListCriteria();
		cr.setKey(SearchKey.TRANSPONDER_TYPE);
		cr.setValue("dummy");

		criteria.getCriterias().add(cr);
		criteria.setIsDynamic(true);
		queryRequest.setMobileTerminalSearchCriteria(criteria);
		
		final HttpResponse response = Request.Post(getBaseUrl() + "asset/rest/mobileterminal/list")
				.setHeader("Content-Type", "application/json").setHeader("Authorization", getValidJwtToken())
				.bodyByteArray(writeValueAsString(queryRequest).getBytes()).execute().returnResponse();

		Map<String, Object> dataMap = checkSuccessResponseReturnMap(response);
	}

	/**
	 * Assign mobile terminal test.
	 *
	 * @throws Exception
	 *             the exception
	 */
	@Test
	public void assignMobileTerminalTest() throws Exception {
		AssetDTO testAsset = AssetTestHelper.createTestAsset();
		MobileTerminalType createdMobileTerminalType = MobileTerminalTestHelper.createMobileTerminalType();

		Map<String, Object> dataMap = MobileTerminalTestHelper.assignMobileTerminal(testAsset, createdMobileTerminalType);
	}

	/**
	 * Un assign mobile terminal test.
	 *
	 * @throws Exception
	 *             the exception
	 */
	@Test
	public void unAssignMobileTerminalTest() throws Exception {
		AssetDTO testAsset = AssetTestHelper.createTestAsset();
		MobileTerminalType createdMobileTerminalType = MobileTerminalTestHelper.createMobileTerminalType();

		MobileTerminalAssignQuery mobileTerminalAssignQuery = new MobileTerminalAssignQuery();
		mobileTerminalAssignQuery.setMobileTerminalId(createdMobileTerminalType.getMobileTerminalId());
		mobileTerminalAssignQuery.setConnectId(testAsset.getId().toString());
		{
			// Assign first
			final HttpResponse response = Request
					.Post(getBaseUrl() + "asset/rest/mobileterminal/assign?comment=comment")
					.setHeader("Content-Type", "application/json").setHeader("Authorization", getValidJwtToken())
					.bodyByteArray(writeValueAsString(mobileTerminalAssignQuery).getBytes()).execute()
					.returnResponse();
	
			Map<String, Object> dataMap = checkSuccessResponseReturnMap(response);
		}
		final HttpResponse response = Request
				.Post(getBaseUrl() + "asset/rest/mobileterminal/unassign?comment=comment")
				.setHeader("Content-Type", "application/json").setHeader("Authorization", getValidJwtToken())
				.bodyByteArray(writeValueAsString(mobileTerminalAssignQuery).getBytes()).execute()
				.returnResponse();

		Map<String, Object> dataMap = checkSuccessResponseReturnMap(response);
	}

	/**
	 * Sets the status active test.
	 *
	 * @throws Exception
	 *             the exception
	 */
	@Test
	public void setStatusActiveTest() throws Exception {
		MobileTerminalType createdMobileTerminalType = MobileTerminalTestHelper.createMobileTerminalType();

		final HttpResponse response = Request
				.Put(getBaseUrl() + "asset/rest/mobileterminal/status/activate?comment=comment")
				.setHeader("Content-Type", "application/json").setHeader("Authorization", getValidJwtToken())
				.bodyByteArray(writeValueAsString(createdMobileTerminalType.getMobileTerminalId()).getBytes()).execute().returnResponse();

		Map<String, Object> dataMap = checkSuccessResponseReturnMap(response);
	}

	/**
	 * Sets the status inactive test.
	 *
	 * @throws Exception
	 *             the exception
	 */
	@Test
	public void setStatusInactiveTest() throws Exception {
		MobileTerminalType createdMobileTerminalType = MobileTerminalTestHelper.createMobileTerminalType();

		String endpoint = getBaseUrl() + "asset/rest/mobileterminal/";

		final HttpResponse response = Request
				.Put(endpoint + "status/inactivate?comment=comment")
				.setHeader("Content-Type", "application/json").setHeader("Authorization", getValidJwtToken())
				.bodyByteArray(writeValueAsString(createdMobileTerminalType.getMobileTerminalId()).getBytes()).execute().returnResponse();

		Map<String, Object> dataMap = checkSuccessResponseReturnMap(response);
	}

	/**
	 * Sets the status removed test.
	 *
	 * @throws Exception
	 *             the exception
	 */
	@Test
	public void setStatusRemovedTest() throws Exception {
		MobileTerminalType createdMobileTerminalType = MobileTerminalTestHelper.createMobileTerminalType();

		final HttpResponse response = Request
				.Put(getBaseUrl() + "asset/rest/mobileterminal/status/remove?comment=comment")
				.setHeader("Content-Type", "application/json").setHeader("Authorization", getValidJwtToken())
				.bodyByteArray(writeValueAsString(createdMobileTerminalType.getMobileTerminalId()).getBytes()).execute().returnResponse();

		Map<String, Object> dataMap = checkSuccessResponseReturnMap(response);
	}

	/**
	 * Gets the mobile terminal history list by mobile terminal id test.
	 *
	 * @return the mobile terminal history list by mobile terminal id test
	 * @throws Exception
	 *             the exception
	 */
	@Test
	public void getMobileTerminalHistoryListByMobileTerminalIdTest() throws Exception {
		MobileTerminalType createdMobileTerminalType = MobileTerminalTestHelper.createMobileTerminalType();

		final HttpResponse response = Request.Get(getBaseUrl() + "asset/rest/mobileterminal/history/" + createdMobileTerminalType.getMobileTerminalId().getGuid())
				.setHeader("Content-Type", "application/json").setHeader("Authorization", getValidJwtToken()).execute()
				.returnResponse();
		Map<String, Object> dataMap = checkSuccessResponseReturnMap(response);
	}

}
