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
import org.apache.http.HttpResponse;
import org.apache.http.client.fluent.Request;
import org.junit.Test;
import eu.europa.ec.fisheries.uvms.docker.validation.common.AbstractRest;

/**
 * The Class SanityRulesRestIT.
 */

public class SanityRulesRestIT extends AbstractRest {

	/**
	 * Gets the sanity rules test.
	 *
	 * @return the sanity rules test
	 * @throws Exception the exception
	 */
	@Test
	public void getSanityRulesTest() throws Exception {
		final HttpResponse response = Request.Get(getBaseUrl() + "movement-rules/rest/sanityrules/listAll")
				.setHeader("Content-Type", "application/json").setHeader("Authorization", getValidJwtToken()).execute()
				.returnResponse();
		List dataList = checkSuccessResponseReturnType(response,List.class);
	}

}
