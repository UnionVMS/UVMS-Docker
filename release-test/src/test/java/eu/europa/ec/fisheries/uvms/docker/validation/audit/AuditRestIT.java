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
package eu.europa.ec.fisheries.uvms.docker.validation.audit;

import java.math.BigInteger;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.fluent.Request;
import org.junit.Test;

import eu.europa.ec.fisheries.schema.audit.search.v1.AuditLogListQuery;
import eu.europa.ec.fisheries.schema.audit.search.v1.ListPagination;
import eu.europa.ec.fisheries.uvms.docker.validation.common.AbstractRestServiceTest;

/**
 * The Class AuditRestIT.
 */

public class AuditRestIT extends AbstractRestServiceTest {

	/**
	 * Gets the config search fields test.
	 *
	 * @return the config search fields test
	 * @throws Exception
	 *             the exception
	 */
	@Test
	public void getListByQueryTest() throws Exception {
		AuditLogListQuery auditLogListQuery = new AuditLogListQuery();
		ListPagination listPagination = new ListPagination();
		listPagination.setPage(BigInteger.valueOf(1));
		listPagination.setListSize(BigInteger.valueOf(25));
		auditLogListQuery.setPagination(listPagination);

		final HttpResponse response = Request.Post(BASE_URL + "audit/rest/audit/list")
				.setHeader("Content-Type", "application/json").setHeader("Authorization", getValidJwtToken())
				.bodyByteArray(writeValueAsString(auditLogListQuery).getBytes()).execute().returnResponse();

		Map<String, Object> dataMap = checkSuccessResponseReturnMap(response);
	}

}
