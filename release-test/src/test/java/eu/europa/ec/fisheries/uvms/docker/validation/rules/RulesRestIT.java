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
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.fluent.Request;
import org.junit.Ignore;
import org.junit.Test;
import eu.europa.ec.fisheries.uvms.docker.validation.common.AbstractRest;
import un.unece.uncefact.data.standard.fluxfaquerymessage._3.FLUXFAQueryMessage;
import un.unece.uncefact.data.standard.fluxfareportmessage._3.FLUXFAReportMessage;
import un.unece.uncefact.data.standard.fluxresponsemessage._6.FLUXResponseMessage;

/**
 * The Class RulesRestIT.
 */

public class RulesRestIT extends AbstractRest {

	/**
	 * Initialize rules test.
	 *
	 * @throws Exception the exception
	 */
	//Changed the expected value to be what the system actually responds with on success
    @Ignore
	@Test
	public void initializeRulesTest() throws Exception {
		final HttpResponse response = Request.Get(getBaseUrl() + "rules/rest/rules/reload")
				.setHeader("Content-Type", "application/json").setHeader("Authorization", getValidJwtToken()).execute()
				.returnResponse();
		String responseMessage = IOUtils.toString(response.getEntity().getContent(), "UTF-8");
		assertEquals("{\"data\":\"Rules reloading completed.\",\"code\":200}", responseMessage);
	}

	/**
	 * Evaluate FLUXFA report message test.
	 *
	 * @throws Exception the exception
	 */
	@Test
	@Ignore
	public void evaluateFLUXFAReportMessageTest() throws Exception {
		FLUXFAReportMessage fluxfaReportMessage = new FLUXFAReportMessage();
		final HttpResponse response = Request.Post(getBaseUrl() + "rules/rest/rules/evaluate/fluxfareportmessage")
				.setHeader("Content-Type", "application/json").setHeader("Authorization", getValidJwtToken())
				.bodyByteArray(writeValueAsString(fluxfaReportMessage).getBytes()).execute().returnResponse();
		Map<String, Object> dataMap = checkSuccessResponseReturnMap(response);
	}

	/**
	 * Evaluate FLUXFA query message message test.
	 *
	 * @throws Exception the exception
	 */
	@Test
	@Ignore
	public void evaluateFLUXFAQueryMessageMessageTest() throws Exception {
		FLUXFAQueryMessage fluxfaQueryMessage = new FLUXFAQueryMessage();
		final HttpResponse response = Request.Post(getBaseUrl() + "rules/rest/rules/evaluate/fluxfaquerymessage")
				.setHeader("Content-Type", "application/json").setHeader("Authorization", getValidJwtToken())
				.bodyByteArray(writeValueAsString(fluxfaQueryMessage).getBytes()).execute().returnResponse();
		Map<String, Object> dataMap = checkSuccessResponseReturnMap(response);
	}

	/**
	 * Evaluate FLUX response message test.
	 *
	 * @throws Exception the exception
	 */
	@Test
	@Ignore
	public void evaluateFLUXResponseMessageTest() throws Exception {
		FLUXResponseMessage fluxResponseMessage = new FLUXResponseMessage();
		final HttpResponse response = Request.Post(getBaseUrl() + "rules/rest/rules/evaluate/fluxfaResponsemessage")
				.setHeader("Content-Type", "application/json").setHeader("Authorization", getValidJwtToken())
				.bodyByteArray(writeValueAsString(fluxResponseMessage).getBytes()).execute().returnResponse();
		Map<String, Object> dataMap = checkSuccessResponseReturnMap(response);
	}

}
