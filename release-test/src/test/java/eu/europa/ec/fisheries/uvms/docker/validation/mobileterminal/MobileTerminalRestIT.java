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

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.fluent.Request;
import org.databene.contiperf.PerfTest;
import org.databene.contiperf.Required;
import org.databene.contiperf.junit.ContiPerfRule;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.MobileTerminalAssignQuery;
import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.MobileTerminalId;
import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.MobileTerminalListQuery;
import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.MobileTerminalType;
import eu.europa.ec.fisheries.uvms.docker.validation.common.AbstractRestServiceTest;

/**
 * The Class MobileTerminalRestIT.
 */
@PerfTest(threads = 4, duration = 3000, warmUp = 1000)
@Required(max = 5000, average = 3000, percentile95 = 3500, throughput = 2)
public class MobileTerminalRestIT extends AbstractRestServiceTest {

	/** The i. */
	@Rule
	public ContiPerfRule contiPerfRule = new ContiPerfRule();

	/**
	 * Creates the mobile terminal test.
	 *
	 * @throws Exception the exception
	 */
	@Test
	public void createMobileTerminalTest() throws Exception {
		final HttpResponse response = Request.Post(BASE_URL + "mobileterminal/rest/mobileterminal")
				.setHeader("Content-Type", "application/json").setHeader("Authorization", getValidJwtToken())
				.bodyByteArray(writeValueAsString(new MobileTerminalType()).getBytes()).execute().returnResponse();

		assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
		final Map<String, Object> data = getJsonMap(response);
		assertFalse(data.isEmpty());
		assertNotNull(data.get("data"));		
	}

	/**
	 * Gets the mobile terminal by id test.
	 *
	 * @return the mobile terminal by id test
	 * @throws Exception the exception
	 */
	@Test
	@Ignore
	public void getMobileTerminalByIdTest() throws Exception {
		final HttpResponse response = Request.Get(BASE_URL + "mobileterminal/rest/mobileterminal/{id}")
				.setHeader("Content-Type", "application/json").setHeader("Authorization", getValidJwtToken()).execute()
				.returnResponse();
		assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
		final Map<String, Object> data = getJsonMap(response);
		assertFalse(data.isEmpty());
		assertNotNull(data.get("data"));		
	}

	/**
	 * Update mobile terminal test.
	 *
	 * @throws Exception the exception
	 */
	@Test
	public void updateMobileTerminalTest() throws Exception {
		final HttpResponse response = Request.Put(BASE_URL + "mobileterminal/rest/mobileterminal?comment=comment")
				.setHeader("Content-Type", "application/json").setHeader("Authorization", getValidJwtToken())
				.bodyByteArray(writeValueAsString(new MobileTerminalType()).getBytes()).execute().returnResponse();

		assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
		final Map<String, Object> data = getJsonMap(response);
		assertFalse(data.isEmpty());
		assertNotNull(data.get("data"));		
	}

	/**
	 * Gets the mobile terminal list test.
	 *
	 * @return the mobile terminal list test
	 * @throws Exception the exception
	 */
	@Test
	public void getMobileTerminalListTest() throws Exception {
		final HttpResponse response = Request.Post(BASE_URL + "mobileterminal/rest/mobileterminal/list")
				.setHeader("Content-Type", "application/json").setHeader("Authorization", getValidJwtToken())
				.bodyByteArray(writeValueAsString(new MobileTerminalListQuery()).getBytes()).execute().returnResponse();

		assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
		final Map<String, Object> data = getJsonMap(response);
		assertFalse(data.isEmpty());
		assertNotNull(data.get("data"));		
	}

	/**
	 * Assign mobile terminal test.
	 *
	 * @throws Exception the exception
	 */
	@Test
	public void assignMobileTerminalTest() throws Exception {
		final HttpResponse response = Request
				.Post(BASE_URL + "mobileterminal/rest/mobileterminal/assign?comment=comment")
				.setHeader("Content-Type", "application/json").setHeader("Authorization", getValidJwtToken())
				.bodyByteArray(writeValueAsString(new MobileTerminalAssignQuery()).getBytes()).execute()
				.returnResponse();

		assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
		final Map<String, Object> data = getJsonMap(response);
		assertFalse(data.isEmpty());
		assertNotNull(data.get("data"));		
	}

	/**
	 * Un assign mobile terminal test.
	 *
	 * @throws Exception the exception
	 */
	@Test
	public void unAssignMobileTerminalTest() throws Exception {
		final HttpResponse response = Request
				.Post(BASE_URL + "mobileterminal/rest/mobileterminal/unassign?comment=comment")
				.setHeader("Content-Type", "application/json").setHeader("Authorization", getValidJwtToken())
				.bodyByteArray(writeValueAsString(new MobileTerminalAssignQuery()).getBytes()).execute()
				.returnResponse();

		assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
		final Map<String, Object> data = getJsonMap(response);
		assertFalse(data.isEmpty());
		assertNotNull(data.get("data"));		
	}

	/**
	 * Sets the status active test.
	 *
	 * @throws Exception the exception
	 */
	@Test
	public void setStatusActiveTest() throws Exception {
		final HttpResponse response = Request
				.Put(BASE_URL + "mobileterminal/rest/mobileterminal/status/activate?comment=comment")
				.setHeader("Content-Type", "application/json").setHeader("Authorization", getValidJwtToken())
				.bodyByteArray(writeValueAsString(new MobileTerminalId()).getBytes()).execute().returnResponse();

		assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
		final Map<String, Object> data = getJsonMap(response);
		assertFalse(data.isEmpty());
		assertNotNull(data.get("data"));		
	}

	/**
	 * Sets the status inactive test.
	 *
	 * @throws Exception the exception
	 */
	@Test
	public void setStatusInactiveTest() throws Exception {
		final HttpResponse response = Request
				.Put(BASE_URL + "mobileterminal/rest/mobileterminal/status/inactivate?comment=comment")
				.setHeader("Content-Type", "application/json").setHeader("Authorization", getValidJwtToken())
				.bodyByteArray(writeValueAsString(new MobileTerminalId()).getBytes()).execute().returnResponse();

		assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
		final Map<String, Object> data = getJsonMap(response);
		assertFalse(data.isEmpty());
		assertNotNull(data.get("data"));		
	}

	/**
	 * Sets the status removed test.
	 *
	 * @throws Exception the exception
	 */
	@Test
	public void setStatusRemovedTest() throws Exception {
		final HttpResponse response = Request
				.Put(BASE_URL + "mobileterminal/rest/mobileterminal/status/remove?comment=comment")
				.setHeader("Content-Type", "application/json").setHeader("Authorization", getValidJwtToken())
				.bodyByteArray(writeValueAsString(new MobileTerminalId()).getBytes()).execute().returnResponse();

		assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
		final Map<String, Object> data = getJsonMap(response);
		assertFalse(data.isEmpty());
		assertNotNull(data.get("data"));		
	}

	/**
	 * Gets the mobile terminal history list by mobile terminal id test.
	 *
	 * @return the mobile terminal history list by mobile terminal id test
	 * @throws Exception the exception
	 */
	@Test
	@Ignore
	public void getMobileTerminalHistoryListByMobileTerminalIdTest() throws Exception {
		final HttpResponse response = Request.Get(BASE_URL + "mobileterminal/rest/mobileterminal/history/{id}")
				.setHeader("Content-Type", "application/json").setHeader("Authorization", getValidJwtToken()).execute()
				.returnResponse();
		assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
		final Map<String, Object> data = getJsonMap(response);
		assertFalse(data.isEmpty());
		assertNotNull(data.get("data"));		
	}

}
