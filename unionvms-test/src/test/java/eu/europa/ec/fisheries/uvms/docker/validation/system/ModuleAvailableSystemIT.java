/*

Developed with the contribution of the European Commission - Directorate General for Maritime Affairs and Fisheries
© European Union, 2017.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can
redistribute it and/or modify it under the terms of the GNU General Public License as published by the
Free Software Foundation, either version 3 of the License, or any later version. The IFDM Suite is distributed in
the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. You should have received a
copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

*/
package eu.europa.ec.fisheries.uvms.docker.validation.system;

import eu.europa.ec.fisheries.uvms.docker.validation.common.AbstractRest;
import org.junit.Ignore;
import org.junit.Test;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public class ModuleAvailableSystemIT extends AbstractRest {

	@Test
	public void checkUnionVmsWebAccessTest() {
		assertEquals(Response.Status.OK.getStatusCode(), getResponseCodeForEndpoint(""));
	}

	@Test
	public void checkUserAccessTest() {
		assertEquals(Response.Status.OK.getStatusCode(), getResponseCodeForEndpoint("usm-administration/"));
		assertEquals(Response.Status.OK.getStatusCode(), getResponseCodeForEndpoint("user/monitoring"));
		assertEquals(Response.Status.FORBIDDEN.getStatusCode(), getResponseCodeForEndpoint("usm-administration/rest"));
	}

	@Test
	public void checkConfigAccessTest() {
		assertEquals(Response.Status.OK.getStatusCode(), getResponseCodeForEndpoint("config/"));
		assertEquals(Response.Status.OK.getStatusCode(), getResponseCodeForEndpoint("config/monitoring"));
		assertEquals(Response.Status.FORBIDDEN.getStatusCode(), getResponseCodeForEndpoint("config/rest"));
	}

	@Test
	public void checkExchangeAccessTest() {
		assertEquals(Response.Status.FORBIDDEN.getStatusCode(), getResponseCodeForEndpoint("exchange/"));
		assertEquals(Response.Status.OK.getStatusCode(), getResponseCodeForEndpoint("exchange/monitoring"));
		assertEquals(Response.Status.FORBIDDEN.getStatusCode(), getResponseCodeForEndpoint("exchange/rest"));
	}

	@Test
	public void checkSpatialAccessTest() {
		assertEquals(Response.Status.FORBIDDEN.getStatusCode(), getResponseCodeForEndpoint("spatial/"));
		assertEquals(Response.Status.OK.getStatusCode(), getResponseCodeForEndpoint("spatial/monitoring"));
		assertEquals(Response.Status.FORBIDDEN.getStatusCode(), getResponseCodeForEndpoint("spatial/rest"));
	}

	@Test
	public void checkMovementAccessTest() {
		assertEquals(Response.Status.FORBIDDEN.getStatusCode(), getResponseCodeForEndpoint("movement/"));
		assertEquals(Response.Status.OK.getStatusCode(), getResponseCodeForEndpoint("movement/monitoring"));
		assertEquals(Response.Status.FORBIDDEN.getStatusCode(), getResponseCodeForEndpoint("movement/rest/config"));
		assertEquals(Response.Status.FORBIDDEN.getStatusCode(), getResponseCodeForEndpoint("movement/rest/alarms"));
		assertEquals(Response.Status.FORBIDDEN.getStatusCode(), getResponseCodeForEndpoint("movement/rest/movement"));
		assertEquals(Response.Status.FORBIDDEN.getStatusCode(), getResponseCodeForEndpoint("movement/rest/search"));
		assertEquals(Response.Status.FORBIDDEN.getStatusCode(), getResponseCodeForEndpoint("movement/rest/manualMovement"));
		assertEquals(Response.Status.FORBIDDEN.getStatusCode(), getResponseCodeForEndpoint("movement/rest/sse"));
		assertEquals(Response.Status.FORBIDDEN.getStatusCode(), getResponseCodeForEndpoint("movement/rest/segment"));
		assertEquals(Response.Status.FORBIDDEN.getStatusCode(), getResponseCodeForEndpoint("movement/rest/track"));
		assertEquals(Response.Status.OK.getStatusCode(), getResponseCodeForEndpoint("movement/monitoring"));
	}

	@Test
	public void checkAuditAccessTest() {
		assertEquals(Response.Status.OK.getStatusCode(), getResponseCodeForEndpoint("audit/"));
		assertEquals(Response.Status.OK.getStatusCode(), getResponseCodeForEndpoint("audit/monitoring"));
		assertEquals(Response.Status.FORBIDDEN.getStatusCode(), getResponseCodeForEndpoint("audit/rest"));
	}

	@Test
	public void checkAssetAccessTest() {
		assertEquals(Response.Status.OK.getStatusCode(), getResponseCodeForEndpoint("asset/"));
		assertEquals(Response.Status.OK.getStatusCode(), getResponseCodeForEndpoint("asset/monitoring"));
		assertEquals(Response.Status.FORBIDDEN.getStatusCode(), getResponseCodeForEndpoint("asset/rest/config"));
		assertEquals(Response.Status.FORBIDDEN.getStatusCode(), getResponseCodeForEndpoint("asset/rest/asset"));
		assertEquals(Response.Status.FORBIDDEN.getStatusCode(), getResponseCodeForEndpoint("asset/rest/group"));
		assertEquals(Response.Status.FORBIDDEN.getStatusCode(), getResponseCodeForEndpoint("asset/rest/mobileterminal"));
	}

	@Test
	public void checkRulesAccessTest() {
		assertEquals(Response.Status.FORBIDDEN.getStatusCode(), getResponseCodeForEndpoint("movement-rules/"));
		assertEquals(Response.Status.OK.getStatusCode(), getResponseCodeForEndpoint("movement-rules/monitoring"));
		assertEquals(Response.Status.FORBIDDEN.getStatusCode(), getResponseCodeForEndpoint("movement-rules/rest/customrules"));
		assertEquals(Response.Status.FORBIDDEN.getStatusCode(), getResponseCodeForEndpoint("movement-rules/rest/tickets"));
	}

	@Test
	public void checkReportingAccessTest() {
		assertEquals(Response.Status.FORBIDDEN.getStatusCode(), getResponseCodeForEndpoint("reporting/"));
		assertEquals(Response.Status.OK.getStatusCode(), getResponseCodeForEndpoint("reporting/monitoring"));
	}

	@Test
	@Ignore
	public void checkMapfishPrintAccessTest() {
		Client client = ClientBuilder.newClient();
		WebTarget target = client.target("http://localhost:28080/mapfish-print/");
		Response response = target
				.request(MediaType.APPLICATION_JSON)
				.get();

		assertEquals(Response.Status.FORBIDDEN.getStatusCode(), response.getStatus());
	}

	@Test
	public void checkGeoServerAccessTest() {
		Client client = ClientBuilder.newClient();
		WebTarget target = client.target("http://localhost:28080/geoserver/");
		Response response = target
				.request(MediaType.APPLICATION_JSON)
				.get();

		assertEquals(Response.Status.FOUND.getStatusCode(), response.getStatus());
	}

	private int getResponseCodeForEndpoint(String relativePath) {
		Response response = getWebTarget()
				.path(relativePath)
				.request(MediaType.APPLICATION_JSON)
				.get();
		return response.getStatus();
	}
}
